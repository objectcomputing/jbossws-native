/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ws.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxrpc.ParameterWrapping;
import org.jboss.ws.core.soap.SOAPContentElement;
import org.jboss.ws.core.utils.HolderUtils;
import org.jboss.ws.core.utils.MimeUtils;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.ws.metadata.umdm.WrappedParameter;
import org.jboss.wsf.common.JavaUtils;
import org.w3c.dom.Element;

/** A web service invocation.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 16-Oct-2004
 */
public class EndpointInvocation
{
   // provide logging
   private static final Logger log = Logger.getLogger(EndpointInvocation.class);

   // The operation meta data for this invocation
   private OperationMetaData opMetaData;
   // Map the named endpoint parameters
   private Map<QName, Object> reqPayload = new LinkedHashMap<QName, Object>();
   // Map the named endpoint parameters
   private Map<QName, Object> resPayload = new LinkedHashMap<QName, Object>();
   // The return value
   private Object returnValue;
   // Map of output parameters, key being the parameter index in the method signature
   private Map<Integer, Object> outParameters = new HashMap<Integer, Object>();

   public EndpointInvocation(OperationMetaData opMetaData)
   {
      this.opMetaData = opMetaData;
   }

   public OperationMetaData getOperationMetaData()
   {
      return opMetaData;
   }

   public Method getJavaMethod()
   {
      return opMetaData.getJavaMethod();
   }

   public Map<Integer, Object> getOutParameters()
   {
      return outParameters;
   }

   public List<QName> getRequestParamNames()
   {
      List<QName> xmlNames = new ArrayList<QName>();
      xmlNames.addAll(reqPayload.keySet());
      return xmlNames;
   }

   public void setRequestParamValue(QName xmlName, Object value)
   {
      if (log.isDebugEnabled())
         log.debug("setRequestParamValue: [name=" + xmlName + ",value=" + getTypeName(value) + "]");
      reqPayload.put(xmlName, value);
   }

   public Object getRequestParamValue(QName xmlName)
   {
      if (log.isDebugEnabled())
         log.debug("getRequestParamValue: " + xmlName);
      Object paramValue = reqPayload.get(xmlName);
      ParameterMetaData paramMetaData = opMetaData.getParameter(xmlName);
      try
      {
         paramValue = transformPayloadValue(paramMetaData, paramValue);
      }
      catch (SOAPException ex)
      {
         throw new WSException(ex);
      }
      return paramValue;
   }

   /** Returns the payload that can be passed on to the endpoint implementation
    */
   public Object[] getRequestPayload()
   {
      log.debug("getRequestPayload");
      List<QName> xmlNames = getRequestParamNames();

      Object[] payload = new Object[opMetaData.getJavaMethod().getParameterTypes().length];
      for (int i = 0; i < xmlNames.size(); i++)
      {
         QName xmlName = xmlNames.get(i);
         Object paramValue = getRequestParamValue(xmlName);

         ParameterMetaData paramMetaData = opMetaData.getParameter(xmlName);
         syncEndpointInputParam(paramMetaData, paramValue, payload);
      }

      return payload;
   }

   public List<QName> getResponseParamNames()
   {
      List<QName> xmlNames = new ArrayList<QName>();
      xmlNames.addAll(resPayload.keySet());
      return xmlNames;
   }

   public void setResponseParamValue(QName xmlName, Object value)
   {
      if (log.isDebugEnabled())
         log.debug("setResponseParamValue: [name=" + xmlName + ",value=" + getTypeName(value) + "]");
      resPayload.put(xmlName, value);
   }

   public Object getResponseParamValue(QName xmlName) throws SOAPException
   {
      if (log.isDebugEnabled())
         log.debug("getResponseParamValue: " + xmlName);
      Object paramValue = resPayload.get(xmlName);
      ParameterMetaData paramMetaData = opMetaData.getParameter(xmlName);
      paramValue = transformPayloadValue(paramMetaData, paramValue);
      if (paramValue != null)
      {
         Class valueType = paramValue.getClass();
         if (HolderUtils.isHolderType(valueType))
         {
            paramValue = HolderUtils.getHolderValue(paramValue);
         }
      }
      return paramValue;
   }

   public void setReturnValue(Object value)
   {
      ParameterMetaData retMetaData = opMetaData.getReturnParameter();
      if (value != null && retMetaData == null)
         throw new WSException("Operation does not have a return value: " + opMetaData.getQName());

      if (log.isDebugEnabled())
         log.debug("setReturnValue: " + getTypeName(value));
      this.returnValue = value;
   }

   public Object getReturnValue()
   {
      if (log.isDebugEnabled())
         log.debug("getReturnValue");
      Object paramValue = returnValue;
      ParameterMetaData paramMetaData = opMetaData.getReturnParameter();
      if (paramMetaData != null)
      {
         try
         {
            paramValue = transformPayloadValue(paramMetaData, paramValue);
         }
         catch (SOAPException ex)
         {
            throw new WSException(ex);
         }
      }
      return paramValue;
   }

   private Object transformPayloadValue(ParameterMetaData paramMetaData, final Object paramValue) throws SOAPException
   {
      QName xmlName = paramMetaData.getXmlName();
      QName xmlType = paramMetaData.getXmlType();
      Class<?> javaType = paramMetaData.getJavaType();
      String javaName = paramMetaData.getJavaTypeName();

      if (xmlType == null)
         throw new IllegalStateException("Cannot obtain xml type for: [xmlName=" + xmlName + ",javaName=" + javaName + "]");

      Object retValue = paramValue;

      // Handle attachment part
      if (paramValue instanceof AttachmentPart)
      {
         AttachmentPart part = (AttachmentPart)paramValue;

         Set mimeTypes = paramMetaData.getMimeTypes();
         if (DataHandler.class.isAssignableFrom(javaType) && !javaType.equals(Object.class))
         {
            DataHandler handler = part.getDataHandler();
            String mimeType = MimeUtils.getBaseMimeType(handler.getContentType());

            // JAX-WS 2.0, 2.6.3.1 MIME Content
            // Conformance (MIME type mismatch): On receipt of a message where the MIME type of a part does not
            // match that described in the WSDL an implementation SHOULD throw a WebServiceException.
            if (mimeTypes != null && !MimeUtils.isMemberOf(mimeType, mimeTypes))
               log.warn("Mime type " + mimeType + " not allowed for parameter " + xmlName + " allowed types are " + mimeTypes);

            retValue = part.getDataHandler();
         }
         else
         {
            retValue = part.getContent();
            String mimeType = MimeUtils.getBaseMimeType(part.getContentType());

            if (mimeTypes != null && !MimeUtils.isMemberOf(mimeType, mimeTypes))
               throw new SOAPException("Mime type " + mimeType + " not allowed for parameter " + xmlName + " allowed types are " + mimeTypes);

            if (retValue != null)
            {
               Class valueType = retValue.getClass();
               if (JavaUtils.isAssignableFrom(javaType, valueType) == false)
                  throw new SOAPException("javaType [" + javaType.getName() + "] is not assignable from attachment content: " + valueType.getName());
            }
         }
      }
      else if (paramValue instanceof SOAPContentElement)
      {
         // If this is bound to a SOAPElement, or Element, then we can return
         // the content element as is.
         // Note, that it is possible for a Java type to be bound to an any
         // type, so checking the xml type is not sufficient.
         if (!Element.class.isAssignableFrom(javaType))
         {
            SOAPContentElement soapElement = (SOAPContentElement)paramValue;
            retValue = soapElement.getObjectValue();
         }
      }

      if (log.isDebugEnabled())
         log.debug("transformPayloadValue: " + getTypeName(paramValue) + " -> " + getTypeName(retValue));
      return retValue;
   }

   /** Synchronize the operation IN, INOUT paramters with the call input parameters.
    *  Essetially it unwrapps holders and converts primitives to wrapper types.
    */
   public void initInputParams(Object[] inputParams)
   {
      for (ParameterMetaData paramMetaData : opMetaData.getParameters())
      {
         int index = paramMetaData.getIndex();

         // doc/lit wrapped return headers are OUT, so skip
         if (index < 0)
            continue;

         QName xmlName = paramMetaData.getXmlName();
         Class javaType = paramMetaData.getJavaType();

         Object value;
         if (opMetaData.isDocumentWrapped() && !paramMetaData.isInHeader() && !paramMetaData.isSwA())
         {
            value = ParameterWrapping.wrapRequestParameters(paramMetaData, inputParams);
         }
         else
         {
            value = inputParams[index];
            if (value != null)
            {
               Class inputType = value.getClass();

               if (HolderUtils.isHolderType(inputType))
               {
                  // At runtime we lose the generic info for JAX-WS types,
                  // So we use the actual instance type
                  value = HolderUtils.getHolderValue(value);
                  inputType = (value == null) ? null : value.getClass();
               }

               // Verify that the java type matches a registered xmlType
               // Attachments are skipped because they don't use type mapping
               if (value != null && !paramMetaData.isSwA() && !paramMetaData.isXOP())
               {
                  if (JavaUtils.isAssignableFrom(javaType, inputType) == false)
                     throw new WSException("Parameter '" + javaType + "' not assignable from: " + inputType);
               }
            }
         }

         setRequestParamValue(xmlName, value);
      }
   }

   /**
    * Synchronize the operation paramters with the endpoint method parameters
    */
   private void syncEndpointInputParam(ParameterMetaData paramMetaData, final Object paramValue, Object[] payload)
   {
      Object retValue = paramValue;
      Method method = opMetaData.getJavaMethod();
      Class[] targetParameterTypes = method.getParameterTypes();

      if (opMetaData.isDocumentWrapped() && !paramMetaData.isInHeader() && !paramMetaData.isSwA() && !paramMetaData.isMessageType())
      {
         outParameters = ParameterWrapping.unwrapRequestParameters(paramMetaData, paramValue, payload);
         syncOutWrappedParameters(targetParameterTypes, payload);
      }
      else
      {
         // Replace INOUT and OUT parameters by their respective holder values
         int index = paramMetaData.getIndex();
         Class targetParameterType = targetParameterTypes[index];

         if (paramMetaData.getMode() == ParameterMode.INOUT || paramMetaData.getMode() == ParameterMode.OUT)
         {
            retValue = HolderUtils.createHolderInstance(paramValue, targetParameterType);

            QName xmlName = paramMetaData.getXmlName();
            setResponseParamValue(xmlName, retValue);
         }

         if (retValue != null)
         {
            Class valueType = retValue.getClass();
            if (JavaUtils.isAssignableFrom(targetParameterType, valueType) == false)
               throw new WSException("Parameter " + targetParameterType.getName() + " is not assignable from: " + getTypeName(retValue));

            if (valueType.isArray())
               retValue = JavaUtils.syncArray(retValue, targetParameterType);
         }

         if (log.isDebugEnabled())
            log.debug("syncEndpointInputParam: " + getTypeName(paramValue) + " -> " + getTypeName(retValue) + "(" + index + ")");
         payload[index] = retValue;
      }
   }

   private void syncOutWrappedParameters(Class[] targetParameterTypes, Object[] payload)
   {
      ParameterMetaData returnMetaData = opMetaData.getReturnParameter();
      if (returnMetaData != null)
      {
         for (WrappedParameter param : returnMetaData.getWrappedParameters())
         {
            try
            {
               // only OUT parameters need to be initialized
               if (param.getIndex() >= 0 && !outParameters.containsKey(param.getIndex()))
               {
                  Object holder = targetParameterTypes[param.getIndex()].newInstance();
                  payload[param.getIndex()] = holder;
                  outParameters.put(param.getIndex(), holder);
               }
            }
            catch (Exception e)
            {
               throw new WSException("Could not add output param: " + param.getName(), e);

            }
         }
      }
   }

   private String getTypeName(Object value)
   {
      String valueType = (value != null ? value.getClass().getName() : null);
      return valueType;
   }
}
