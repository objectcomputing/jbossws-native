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
package org.jboss.ws.metadata.umdm;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.jboss.logging.Logger;
import org.jboss.util.NotImplementedException;
import org.jboss.ws.WSException;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.core.soap.Use;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.wsf.common.JavaUtils;
import org.w3c.dom.Element;

/**
 * An Operation component describes an operation that a given interface supports.
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @since 12-May-2004
 */
public class OperationMetaData extends ExtensibleMetaData implements InitalizableMetaData
{
   // provide logging
   private final Logger log = Logger.getLogger(OperationMetaData.class);

   // The parent interface
   private EndpointMetaData epMetaData;

   private QName qname;
   private QName responseName;
   private String javaName;
   private Method javaMethod;
   private boolean oneWay;
   private String soapAction;
   private ParameterStyle parameterStyle;
   private List<ParameterMetaData> parameters = new ArrayList<ParameterMetaData>();
   private List<FaultMetaData> faults = new ArrayList<FaultMetaData>();
   private ParameterMetaData returnParam;
   private String documentation;

   public OperationMetaData(EndpointMetaData epMetaData, QName qname, String javaName)
   {
      log.trace("new OperationMetaData: [xmlName=" + qname + ",javaName=" + javaName + "]");
      initOperationMetaData(epMetaData, qname, javaName);
   }

   private void initOperationMetaData(EndpointMetaData epMetaData, QName qname, String javaName)
   {
      this.epMetaData = epMetaData;
      this.qname = qname;
      this.javaName = javaName;

      if (qname == null)
         throw new IllegalArgumentException("Invalid null qname argument");
      if (javaName == null)
         throw new IllegalArgumentException("Invalid null javaName argument, for: " + qname);

      String nsURI = qname.getNamespaceURI();
      String localPart = qname.getLocalPart();
      this.responseName = new QName(nsURI, localPart + "Response");
   }

   public EndpointMetaData getEndpointMetaData()
   {
      return epMetaData;
   }

   public QName getQName()
   {
      return qname;
   }

   public QName getResponseName()
   {
      return responseName;
   }

   public String getSOAPAction()
   {
      return soapAction;
   }

   public void setSOAPAction(String soapAction)
   {
      this.soapAction = soapAction;
   }

   public Style getStyle()
   {
      return epMetaData.getStyle();
   }

   public Use getUse()
   {
      return epMetaData.getEncodingStyle();
   }

   public ParameterStyle getParameterStyle()
   {
      return (parameterStyle != null) ? parameterStyle : epMetaData.getParameterStyle();
   }

   public void setParameterStyle(ParameterStyle parameterStyle)
   {
      this.parameterStyle = parameterStyle;
   }

   public boolean isRPCLiteral()
   {
      return getStyle() == Style.RPC && getUse() == Use.LITERAL;
   }

   public boolean isRPCEncoded()
   {
      return getStyle() == Style.RPC && getUse() == Use.ENCODED;
   }

   public boolean isDocumentBare()
   {
      return getStyle() == Style.DOCUMENT && getParameterStyle() == ParameterStyle.BARE;
   }

   public boolean isDocumentWrapped()
   {
      return getStyle() == Style.DOCUMENT && getParameterStyle() == ParameterStyle.WRAPPED;
   }

   public String getJavaName()
   {
      return javaName;
   }

   public Method getJavaMethod()
   {
      Method tmpMethod = javaMethod;
      Class seiClass = epMetaData.getServiceEndpointInterface();
      if (tmpMethod == null && seiClass != null)
      {
         for (Method method : seiClass.getMethods())
         {
            if (isJavaMethod(method))
            {
               tmpMethod = method;

               UnifiedMetaData wsMetaData = epMetaData.getServiceMetaData().getUnifiedMetaData();
               if (wsMetaData.isEagerInitialized())
               {
                  if (UnifiedMetaData.isFinalRelease() == false)
                     log.warn("Loading java method after eager initialization", new IllegalStateException());

                  javaMethod = method;
               }

               break;
            }
         }

         if ((tmpMethod == null) && (epMetaData.getConfig().getRMMetaData() == null)) // RM hack
            throw new WSException("Cannot find java method: " + javaName);
      }
      return tmpMethod;
   }

   public boolean isJavaMethod(Method method)
   {
      boolean isJavaMethod = method.equals(javaMethod);
      if (isJavaMethod == false)
      {
         String methodName = method.getName();
         if (javaName.equals(methodName))
         {
            log.trace("Found java method: " + method);

            // compare params by java type name
            if (matchParameters(method, true))
            {
               if(log.isDebugEnabled()) log.debug("Found best matching java method: " + method);
               isJavaMethod = true;
            }

            // compare params by assignability
            if (!isJavaMethod && matchParameters(method, false))
            {
               if(log.isDebugEnabled()) log.debug("Found possible matching java method: " + method);
               isJavaMethod = true;
            }
         }
      }

      if (log.isTraceEnabled())
         log.trace("Synchronized java method:\n" + method + "\nwith: " + toString());

      return isJavaMethod;
   }

   private boolean matchParameters(Method method, boolean exact)
   {
      Class[] paramTypes = method.getParameterTypes();
      Set<Integer> matches = new HashSet<Integer>(paramTypes.length);

      for (ParameterMetaData param : getParameters())
      {
         if (!param.matchParameter(method, matches, exact))
            return false;
      }

      ParameterMetaData returnMetaData = getReturnParameter();
      if (returnMetaData != null && !returnMetaData.matchParameter(method, matches, exact))
return false;

      // We should have an entry for every parameter index if we match
      return matches.size() == paramTypes.length;
   }

   /** Return true if this is a generic message style destination that takes a org.w3c.dom.Element
    */
   public boolean isMessageEndpoint()
   {
      boolean isMessageEndpoint = false;
      if (parameters.size() == 1)
      {
         ParameterMetaData inParam = parameters.get(0);
         if (JavaUtils.isAssignableFrom(Element.class, inParam.getJavaType()))
         {
            isMessageEndpoint = true;
         }
      }
      return isMessageEndpoint;
   }

   public boolean isOneWay()
   {
      return oneWay;
   }

   public void setOneWay(boolean oneWay)
   {
      this.oneWay = oneWay;
      assertOneWayOperation();
   }

   public ParameterMetaData getParameter(QName xmlName)
   {
      ParameterMetaData paramMetaData = null;
      for (int i = 0; paramMetaData == null && i < parameters.size(); i++)
      {
         ParameterMetaData aux = parameters.get(i);
         if (xmlName.equals(aux.getXmlName()))
            paramMetaData = aux;
      }
      return paramMetaData;
   }

   /** Get the IN or INOUT parameter list */
   public List<ParameterMetaData> getInputParameters()
   {
      List<ParameterMetaData> retList = new ArrayList<ParameterMetaData>();
      for (ParameterMetaData paramMetaData : parameters)
      {
         ParameterMode mode = paramMetaData.getMode();
         if (mode == ParameterMode.IN || mode == ParameterMode.INOUT)
            retList.add(paramMetaData);
      }
      return retList;
   }

   /** Get the OUT or INOUT parameter list */
   public List<ParameterMetaData> getOutputParameters()
   {
      List<ParameterMetaData> retList = new ArrayList<ParameterMetaData>();
      for (ParameterMetaData paramMetaData : parameters)
      {
         ParameterMode mode = paramMetaData.getMode();
         if (mode == ParameterMode.OUT || mode == ParameterMode.INOUT)
            retList.add(paramMetaData);
      }
      return retList;
   }

   /** Get the non header parameter list */
   public List<ParameterMetaData> getNonHeaderParameters()
   {
      List<ParameterMetaData> retList = new ArrayList<ParameterMetaData>();
      for (ParameterMetaData paramMetaData : parameters)
      {
         if (paramMetaData.isInHeader() == false)
            retList.add(paramMetaData);
      }
      return retList;
   }

   public List<ParameterMetaData> getParameters()
   {
      return new ArrayList<ParameterMetaData>(parameters);
   }

   public void addParameter(ParameterMetaData pmd)
   {
      log.trace("addParameter: [xmlName=" + pmd.getXmlName() + ",xmlType=" + pmd.getXmlType() + "]");
      parameters.add(pmd);
      assertOneWayOperation();
   }

   public void removeAllParameters()
   {
      parameters.clear();
   }

   public ParameterMetaData getReturnParameter()
   {
      return returnParam;
   }

   public void setReturnParameter(ParameterMetaData returnParam)
   {
      log.trace("setReturnParameter: " + returnParam);
      returnParam.setMode(ParameterMode.OUT);
      returnParam.setIndex(-1);
      this.returnParam = returnParam;
      assertOneWayOperation();
   }

   public List<FaultMetaData> getFaults()
   {
      return new ArrayList<FaultMetaData>(faults);
   }

   public FaultMetaData getFault(QName xmlName)
   {
      FaultMetaData faultMetaData = null;
      for (int i = 0; faultMetaData == null && i < faults.size(); i++)
      {
         FaultMetaData aux = faults.get(i);
         if (aux.getXmlName().equals(xmlName))
            faultMetaData = aux;
      }
      return faultMetaData;
   }

   public FaultMetaData getFaultMetaData(Class javaType)
   {
      FaultMetaData faultMetaData = null;
      for (FaultMetaData aux : faults)
      {
         if (aux.getJavaType().equals(javaType))
         {
            faultMetaData = aux;
            break;
         }
      }
      return faultMetaData;
   }

   public void addFault(FaultMetaData fault)
   {
      log.trace("addFault: " + fault);
      faults.add(fault);
      assertOneWayOperation();
   }

   // A JSR-181 processor is REQUIRED to report an error if an
   // operation marked @Oneway has a return value, declares any checked exceptions or has any
   // INOUT or OUT parameters.
   private void assertOneWayOperation()
   {
      if (oneWay)
      {
         if (returnParam != null)
            throw new WSException("OneWay operations cannot have a return parameter");

         if (faults.size() > 0)
            throw new WSException("OneWay operations cannot have checked exceptions");

         for (ParameterMetaData paramMetaData : parameters)
         {
            if (paramMetaData.getMode() != ParameterMode.IN)
               throw new WSException("OneWay operations cannot have INOUT or OUT parameters");
         }
      }
   }

   public void assertDocumentBare()
   {
      if (isDocumentBare())
      {
         int in = 0;
         int out = 0;

         for (ParameterMetaData paramMetaData : parameters)
         {
            if (paramMetaData.isInHeader())
               continue;

            ParameterMode mode = paramMetaData.getMode();
            if (mode != ParameterMode.OUT)
               in++;
            if (mode != ParameterMode.IN)
               out++;
         }

         if (returnParam != null && !returnParam.isInHeader())
            out++;

         if (in > 1 || out > (oneWay ? 0 : 1))
            throw new WSException("The body of a document/literal bare message requires at most 1 input and at most 1 output (or 0 if oneway). method: " + javaName + " in: "
                  + in + " out: " + out);
      }
   }

   public void validate()
   {
      for (ParameterMetaData parameter : parameters)
         parameter.validate();

      for (FaultMetaData fault : faults)
         fault.validate();
   }

   public void eagerInitialize()
   {
      // Call eagerInitialize(List<Method> unsynchronizedMethods) instead
      throw new NotImplementedException();
   }
   
   /**
    * @see UnifiedMetaData#eagerInitialize()
    */
   public void eagerInitialize(List<Method> unsynchronizedMethods)
   {
      // reset java method
      javaMethod = null;

      for (ParameterMetaData parameter : parameters)
         parameter.eagerInitialize();

      if (returnParam != null)
         returnParam.eagerInitialize();

      for (FaultMetaData fault : faults)
         fault.eagerInitialize();

      // Method initialization
      for (Method method : unsynchronizedMethods)
      {
         if (isJavaMethod(method))
         {
            javaMethod = method;
            break;
         }
      }

      // Report unsynchronized java method
      boolean isRMMethod = RMProvider.get().getConstants().getNamespaceURI().equals(qname.getNamespaceURI());
      if ((javaMethod == null) && (isRMMethod == false)) // RM hack
      {
         StringBuilder errMsg = new StringBuilder("Cannot synchronize to any of these methods:");
         for (Method method : unsynchronizedMethods)
         {
            errMsg.append("\n" + method);
         }
         errMsg.append("\n" + toString());
         throw new IllegalStateException(errMsg.toString());
      }
   }

   public String toString()
   {
      StringBuilder buffer = new StringBuilder("\nOperationMetaData:");
      buffer.append("\n qname=" + qname);
      buffer.append("\n javaName=" + javaName);
      buffer.append("\n style=" + getStyle() + "/" + getUse());
      if (getStyle() == Style.DOCUMENT)
      {
         buffer.append("/" + getParameterStyle());
      }
      buffer.append("\n oneWay=" + oneWay);
      buffer.append("\n soapAction=" + soapAction);
      for (ParameterMetaData param : parameters)
      {
         buffer.append(param);
      }
      if (returnParam != null)
      {
         buffer.append(returnParam.toString());
      }
      for (FaultMetaData fault : faults)
      {
         buffer.append(fault);
      }
      return buffer.toString();
   }

   public String getDocumentation()
   {
      return documentation;
   }

   public void setDocumentation(String documentation)
   {
      this.documentation = documentation;
   }
}
