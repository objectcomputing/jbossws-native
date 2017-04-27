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
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxrpc.ParameterWrapping;
import org.jboss.ws.core.jaxws.DynamicWrapperGenerator;
import org.jboss.ws.core.utils.HolderUtils;
import org.jboss.ws.extensions.xop.jaxws.AttachmentScanResult;
import org.jboss.ws.extensions.xop.jaxws.ReflectiveAttachmentRefScanner;
import org.jboss.ws.metadata.accessor.AccessorFactoryCreator;
import org.jboss.ws.metadata.accessor.ReflectiveMethodAccessorFactoryCreator;
import org.jboss.ws.metadata.config.EndpointFeature;
import org.jboss.ws.metadata.umdm.EndpointMetaData.Type;
import org.jboss.wsf.common.JavaUtils;

/**
 * A request/response parameter that a given operation supports.
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @since 12-May-2005
 */
public class ParameterMetaData implements InitalizableMetaData
{
   // provide logging
   private final Logger log = Logger.getLogger(ParameterMetaData.class);

   // The parent operation
   private OperationMetaData opMetaData;

   private QName xmlName;
   private String partName;
   private QName xmlType;
   private String javaTypeName;
   private Class javaType;
   private ParameterMode mode;
   private Set<String> mimeTypes;
   private boolean inHeader;
   private boolean isSwA;
   private boolean isXOP;
   private boolean isSwaRef;
   private List<WrappedParameter> wrappedParameters;
   private int index;

   // SOAP-ENC:Array
   private boolean soapArrayParam;
   private QName soapArrayCompType;
   private AccessorFactoryCreator accessorFactoryCreator = new ReflectiveMethodAccessorFactoryCreator();

   private static final List<String> messageTypes = new ArrayList<String>();
   static
   {
      messageTypes.add("javax.xml.soap.SOAPElement");
      messageTypes.add("org.w3c.dom.Element");
   }

   public ParameterMetaData(OperationMetaData opMetaData, QName xmlName, QName xmlType, String javaTypeName)
   {
      this(opMetaData, xmlName, javaTypeName);
      setXmlType(xmlType);
   }

   public ParameterMetaData(OperationMetaData opMetaData, QName xmlName, String javaTypeName)
   {
      if (xmlName == null)
         throw new IllegalArgumentException("Invalid null xmlName argument");

      // Remove the prefixes
      if (xmlName.getNamespaceURI().length() > 0)
         xmlName = new QName(xmlName.getNamespaceURI(), xmlName.getLocalPart());

      this.xmlName = xmlName;
      this.opMetaData = opMetaData;
      this.mode = ParameterMode.IN;
      this.javaTypeName = javaTypeName;
      this.partName = xmlName.getLocalPart();
   }

   private static boolean matchParameter(Method method, int index, Class expectedType, Set<Integer> matches, boolean exact, boolean holder)
   {
      Class returnType = method.getReturnType();

      if (index == -1 && matchTypes(returnType, expectedType, exact, false))
         return true;

      Class[] classParameters = method.getParameterTypes();
      if (index < 0 || index >= classParameters.length)
         return false;

      boolean matchTypes;

      if (JavaUtils.isRetro14())
      {
         matchTypes = matchTypes(classParameters[index], expectedType, exact, holder);
      }
      else
      {
         java.lang.reflect.Type[] genericParameters = method.getGenericParameterTypes();
         matchTypes = matchTypes(genericParameters[index], expectedType, exact, holder);
      }

      if (matchTypes)
      {
         matches.add(index);
         return true;
      }

      return false;
   }

   private static boolean matchTypes(java.lang.reflect.Type actualType, Class expectedType, boolean exact, boolean holder)
   {
      if (holder && HolderUtils.isHolderType(actualType) == false)
         return false;

      java.lang.reflect.Type valueType = (holder ? HolderUtils.getValueType(actualType) : actualType);
      Class valueClass = JavaUtils.erasure(valueType);

      return matchTypesInternal(valueClass, expectedType, exact);
   }

   // This duplication is needed because Class does not implement Type in 1.4, 
   // which makes retrotranslation not possible. This takes advantage of overloading to
   // prevent the problem.
   private static boolean matchTypes(Class actualType, Class expectedType, boolean exact, boolean holder)
   {
      if (holder && HolderUtils.isHolderType(actualType) == false)
         return false;

      Class valueClass = (holder ? HolderUtils.getValueType(actualType) : actualType);

      return matchTypesInternal(valueClass, expectedType, exact);
   }

   private static boolean matchTypesInternal(Class valueClass, Class expectedType, boolean exact)
   {
      // FIXME - Why do we need this hack? The method signature should _ALWAYS_ match, else we will get ambiguous or incorrect results
      List<Class> anyTypes = new ArrayList<Class>();
      anyTypes.add(javax.xml.soap.SOAPElement.class);
      anyTypes.add(org.w3c.dom.Element.class);

      boolean matched;
      if (exact)
      {
         matched = valueClass.getName().equals(expectedType.getName());
         if (matched == false && anyTypes.contains(valueClass))
            matched = anyTypes.contains(expectedType);
      }
      else
      {
         matched = JavaUtils.isAssignableFrom(valueClass, expectedType);
      }
      return matched;
   }

   public OperationMetaData getOperationMetaData()
   {
      return opMetaData;
   }

   public QName getXmlName()
   {
      return xmlName;
   }

   public QName getXmlType()
   {
      return xmlType;
   }

   public void setXmlType(QName xmlType)
   {
      if (xmlType == null)
         throw new IllegalArgumentException("Invalid null xmlType");

      // Remove potential prefix
      if (xmlType.getNamespaceURI().length() > 0)
         this.xmlType = new QName(xmlType.getNamespaceURI(), xmlType.getLocalPart());
      else
         this.xmlType = xmlType;

      // Special case to identify attachments
      if (Constants.NS_ATTACHMENT_MIME_TYPE.equals(xmlType.getNamespaceURI()))
      {
         String mimeType = convertXmlTypeToMimeType(xmlType);
         setMimeTypes(mimeType);
         this.isSwA = true;
      }
   }

   public String getJavaTypeName()
   {
      return javaTypeName;
   }

   public void setJavaTypeName(String typeName)
   {
      // Warn if this is called after eager initialization
      UnifiedMetaData wsMetaData = opMetaData.getEndpointMetaData().getServiceMetaData().getUnifiedMetaData();
      if (wsMetaData.isEagerInitialized() && UnifiedMetaData.isFinalRelease() == false)
         log.warn("Set java type name after eager initialization", new IllegalStateException());

      javaTypeName = typeName;
      javaType = null;
   }

   public Class loadWrapperBean()
   {
      Class wrapperBean = null;
      try
      {
         ClassLoader loader = getClassLoader();
         wrapperBean = JavaUtils.loadJavaType(javaTypeName, loader);
      }
      catch (ClassNotFoundException ex)
      {
         // ignore
      }
      return wrapperBean;
   }

   /** Load the java type.
    *  It should only be cached during eager initialization.
    */
   public Class getJavaType()
   {
      Class tmpJavaType = javaType;
      if (tmpJavaType == null && javaTypeName != null)
      {
         try
         {
            ClassLoader loader = getClassLoader();
            tmpJavaType = JavaUtils.loadJavaType(javaTypeName, loader);

            UnifiedMetaData wsMetaData = opMetaData.getEndpointMetaData().getServiceMetaData().getUnifiedMetaData();
            if (wsMetaData.isEagerInitialized())
            {
               // This should not happen, see the warning in setJavaTypeName
               javaType = tmpJavaType;
            }
         }
         catch (ClassNotFoundException ex)
         {
            throw new WSException("Cannot load java type: " + javaTypeName, ex);
         }
      }
      return tmpJavaType;
   }

   public ParameterMode getMode()
   {
      return mode;
   }

   public void setMode(String mode)
   {
      if ("IN".equals(mode))
         setMode(ParameterMode.IN);
      else if ("INOUT".equals(mode))
         setMode(ParameterMode.INOUT);
      else if ("OUT".equals(mode))
         setMode(ParameterMode.OUT);
      else
         throw new IllegalArgumentException("Invalid mode: " + mode);
   }

   public void setMode(ParameterMode mode)
   {
      this.mode = mode;
   }

   public Set<String> getMimeTypes()
   {
      return mimeTypes;
   }

   public void setMimeTypes(String mimeStr)
   {
      mimeTypes = new HashSet<String>();
      StringTokenizer st = new StringTokenizer(mimeStr, ",");
      while (st.hasMoreTokens())
         mimeTypes.add(st.nextToken().trim());
   }

   public boolean isInHeader()
   {
      return inHeader;
   }

   public void setInHeader(boolean inHeader)
   {
      this.inHeader = inHeader;
   }

   public boolean isSwA()
   {
      return isSwA;
   }

   public void setSwA(boolean isSwA)
   {
      this.isSwA = isSwA;
   }

   public boolean isSwaRef()
   {
      return isSwaRef;
   }

   public void setSwaRef(boolean swaRef)
   {
      isSwaRef = swaRef;
   }

   public boolean isXOP()
   {
      return isXOP;
   }

   public void setXOP(boolean isXOP)
   {
      this.isXOP = isXOP;
   }

   public boolean isSOAPArrayParam()
   {
      return soapArrayParam;
   }

   public void setSOAPArrayParam(boolean soapArrayParam)
   {
      this.soapArrayParam = soapArrayParam;
   }

   public QName getSOAPArrayCompType()
   {
      return soapArrayCompType;
   }

   public void setSOAPArrayCompType(QName compXmlType)
   {
      if (compXmlType != null && !compXmlType.equals(soapArrayCompType))
      {
         String logmsg = "SOAPArrayCompType: [xmlType=" + xmlType + ",compType=" + compXmlType + "]";
         log.debug((soapArrayCompType == null ? "set" : "reset") + logmsg);
      }

      this.soapArrayCompType = compXmlType;
   }

   @Deprecated
   // FIXME This hack should be removed
   public boolean isMessageType()
   {
      return messageTypes.contains(javaTypeName);
   }

   @Deprecated
   public static boolean isMessageType(String javaTypeName)
   {
      return messageTypes.contains(javaTypeName);
   }

   /** Converts a proprietary JBossWS attachment xml type to the MIME type that it represents.
    */
   private String convertXmlTypeToMimeType(QName xmlType)
   {
      StringBuilder mimeName = new StringBuilder(xmlType.getLocalPart());
      int pos = mimeName.indexOf("_");
      if (pos == -1)
         throw new IllegalArgumentException("Invalid mime type: " + xmlType);

      mimeName.setCharAt(pos, '/');
      return mimeName.toString();
   }

   public int getIndex()
   {
      return index;
   }

   /**
    * Sets the method parameter index of the parameter this meta data corresponds to. A value of -1 indicates
    * that this parameter is mapped to the return value.
    *
    * @param index the method parameter offset, or -1 for a return value
    */
   public void setIndex(int index)
   {
      this.index = index;
   }

   public List<WrappedParameter> getWrappedParameters()
   {
      return wrappedParameters;
   }

   public void setWrappedParameters(List<WrappedParameter> wrappedParameters)
   {
      this.wrappedParameters = wrappedParameters;
   }

   public String getPartName()
   {
      // [JBWS-771] Use part names that are friendly to .NET
      String auxPartName = partName;
      if (opMetaData.getEndpointMetaData().getConfig().hasFeature(EndpointFeature.BINDING_WSDL_DOTNET))
      {
         if (opMetaData.isDocumentWrapped() && inHeader == false)
            auxPartName = "parameters";
      }
      return auxPartName;
   }

   public void setPartName(String partName)
   {
      this.partName = partName;
   }

   public void validate()
   {
      // nothing to do
   }

   /**
    * @see UnifiedMetaData#eagerInitialize()
    */
   public void eagerInitialize()
   {
      // reset java type
      javaType = null;

      // TODO - Remove messageType hack
      Type epType = getOperationMetaData().getEndpointMetaData().getType();
      if (getOperationMetaData().isDocumentWrapped() && !isInHeader() && !isSwA() && !isMessageType())
      {
         if (loadWrapperBean() == null)
         {
            if (epType == EndpointMetaData.Type.JAXRPC)
               throw new WSException("Autogeneration of wrapper beans not supported with JAXRPC");

            new DynamicWrapperGenerator(getClassLoader()).generate(this);
         }
      }

      javaType = getJavaType();
      if (javaType == null)
         throw new WSException("Cannot load java type: " + javaTypeName);

      initializeAttachmentParameter(epType);
   }

   /**
    * Identify MTOM and SWA:Ref parameter as these require special treatment.
    * This only affects JAX-WS endpoints.
    *
    * Note: For SEI parameter annotations this happens within the metadata builder.
    * @param epType
    */
   private void initializeAttachmentParameter(Type epType)
   {
      if (epType == Type.JAXWS)
      {
         ReflectiveAttachmentRefScanner scanner = new ReflectiveAttachmentRefScanner();
         AttachmentScanResult scanResult = scanner.scanBean(javaType);
         if (scanResult != null)
         {
            if (log.isDebugEnabled())
               log.debug("Identified attachment reference: " + xmlName + ", type=" + scanResult.getType());
            if (scanResult.getType() == AttachmentScanResult.Type.XOP)
               setXOP(true);
            else
               setSwaRef(true);
         }
      }
   }

   private ClassLoader getClassLoader()
   {
      ClassLoader loader = opMetaData.getEndpointMetaData().getClassLoader();
      if (loader == null)
         throw new WSException("ClassLoader not available");
      return loader;
   }

   public boolean matchParameter(Method method, Set<Integer> matches, boolean exact)
   {
      ClassLoader loader = getOperationMetaData().getEndpointMetaData().getClassLoader();
      List<WrappedParameter> wrappedParameters = getWrappedParameters();
      Class wrapperType = getJavaType();

      // Standard type
      if (wrappedParameters == null)
         return matchParameter(method, getIndex(), getJavaType(), matches, exact, mode != ParameterMode.IN);

      // Wrapped type
      for (WrappedParameter wrapped : wrappedParameters)
      {
         String typeName = wrapped.getType();

         try
         {
            Class type = (typeName != null) ? JavaUtils.loadJavaType(typeName, loader) : ParameterWrapping.getWrappedType(wrapped.getVariable(), wrapperType);
            if (type == null)
               return false;
            if (!matchParameter(method, wrapped.getIndex(), type, matches, exact, wrapped.isHolder()))
               return false;
         }
         catch (Exception ex)
         {
            if (log.isDebugEnabled())
               log.debug("Invalid wrapper type:" + typeName, ex);
            return false;
         }
      }

      return true;
   }

   public AccessorFactoryCreator getAccessorFactoryCreator()
   {
      return accessorFactoryCreator;
   }

   public void setAccessorFactoryCreator(AccessorFactoryCreator accessorFactoryCreator)
   {
      this.accessorFactoryCreator = accessorFactoryCreator;
   }

   public String toString()
   {
      boolean isReturn = (opMetaData.getReturnParameter() == this);
      StringBuilder buffer = new StringBuilder("\n" + (isReturn ? "ReturnMetaData:" : "ParameterMetaData:"));
      buffer.append("\n xmlName=").append(getXmlName());
      buffer.append("\n partName=").append(getPartName());
      buffer.append("\n xmlType=").append(getXmlType());

      if (soapArrayParam)
         buffer.append("\n soapArrayCompType=").append(soapArrayCompType);

      buffer.append("\n javaType=").append(getJavaTypeName());
      buffer.append("\n mode=").append(getMode());
      buffer.append("\n inHeader=").append(isInHeader());
      buffer.append("\n index=").append(index);

      if (isSwA())
      {
         buffer.append("\n isSwA=").append(isSwA());
         buffer.append("\n mimeTypes=").append(getMimeTypes());
      }

      if (isXOP())
      {
         buffer.append("\n isXOP=").append(isXOP());
         buffer.append("\n mimeTypes=").append(getMimeTypes());
      }

      if (wrappedParameters != null)
         buffer.append("\n wrappedParameters=").append(wrappedParameters);

      return buffer.toString();
   }
}
