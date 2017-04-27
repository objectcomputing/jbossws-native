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
package org.jboss.ws.metadata.builder.jaxws;

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.jws.HandlerChain;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPMessageHandlers;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.ws.BindingType;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebFault;
import javax.xml.ws.Action;
import javax.xml.ws.addressing.AddressingProperties;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.annotation.Documentation;
import org.jboss.ws.core.jaxws.DynamicWrapperGenerator;
import org.jboss.ws.core.jaxws.JAXBContextFactory;
import org.jboss.ws.core.jaxws.WrapperGenerator;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.core.soap.Use;
import org.jboss.ws.core.utils.HolderUtils;
import org.jboss.ws.core.utils.JBossWSEntityResolver;
import org.jboss.ws.extensions.addressing.AddressingPropertiesImpl;
import org.jboss.ws.extensions.addressing.metadata.AddressingOpMetaExt;
import org.jboss.ws.extensions.xop.jaxws.AttachmentScanResult;
import org.jboss.ws.extensions.xop.jaxws.ReflectiveAttachmentRefScanner;
import org.jboss.ws.metadata.accessor.JAXBAccessorFactoryCreator;
import org.jboss.ws.metadata.builder.MetaDataBuilder;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.FaultMetaData;
import org.jboss.ws.metadata.umdm.HandlerMetaDataJAXWS;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.ws.metadata.umdm.TypeMappingMetaData;
import org.jboss.ws.metadata.umdm.TypesMetaData;
import org.jboss.ws.metadata.umdm.WrappedParameter;
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLBindingMessageReference;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperation;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLMIMEPart;
import org.jboss.wsf.common.JavaUtils;
import org.jboss.wsf.spi.binding.BindingCustomization;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.HandlerChainsObjectFactory;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainsMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;
import org.jboss.xb.binding.ObjectModelFactory;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;

import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.bind.api.TypeReference;

/**
 * Abstract class that represents a JAX-WS metadata builder.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @author Thomas.Diesler@jboss.com
 */
@SuppressWarnings("deprecation")
public class JAXWSMetaDataBuilder extends MetaDataBuilder
{

   protected static final Logger log = Logger.getLogger(JAXWSWebServiceMetaDataBuilder.class);
   protected List<Class<?>> javaTypes = new ArrayList<Class<?>>();
   protected JAXBRIContext jaxbCtx;
   protected List<TypeReference> typeRefs = new ArrayList<TypeReference>();
   protected WrapperGenerator wrapperGenerator;

   protected void processBindingType(EndpointMetaData epMetaData, Class<?> wsClass)
   {
      if (wsClass.isAnnotationPresent(BindingType.class))
      {
         log.debug("processBindingType on: " + wsClass.getName());
         BindingType anBindingType = (BindingType)wsClass.getAnnotation(BindingType.class);
         epMetaData.setBindingId(anBindingType.value());
      }
   }

   protected void processSOAPBinding(EndpointMetaData epMetaData, Class<?> wsClass)
   {
      if (wsClass.isAnnotationPresent(SOAPBinding.class))
      {
         log.debug("processSOAPBinding on: " + wsClass.getName());
         SOAPBinding anSoapBinding = wsClass.getAnnotation(SOAPBinding.class);

         SOAPBinding.Style attrStyle = anSoapBinding.style();
         Style style = (attrStyle == SOAPBinding.Style.RPC ? Style.RPC : Style.DOCUMENT);
         epMetaData.setStyle(style);

         SOAPBinding.Use attrUse = anSoapBinding.use();
         if (attrUse == SOAPBinding.Use.ENCODED)
            throw new WSException("SOAP encoding is not supported for JSR-181 deployments");

         epMetaData.setEncodingStyle(Use.LITERAL);

         ParameterStyle paramStyle = anSoapBinding.parameterStyle();
         epMetaData.setParameterStyle(paramStyle);
      }
   }

   /**
    * Process an optional @HandlerChain annotation
    *
    * Location of the handler chain file. The location supports 2 formats.
    *
    *    1. An absolute java.net.URL in externalForm.
    *    (ex: http://myhandlers.foo.com/handlerfile1.xml)
    *
    *    2. A relative path from the source file or class file.
    *    (ex: bar/handlerfile1.xml)
    */
   protected void processHandlerChain(EndpointMetaData epMetaData, Class<?> wsClass)
   {
      if (wsClass.isAnnotationPresent(SOAPMessageHandlers.class))
         throw new WSException("Cannot combine @HandlerChain with @SOAPMessageHandlers");

      if (wsClass.isAnnotationPresent(HandlerChain.class))
      {
         HandlerChain anHandlerChain = wsClass.getAnnotation(HandlerChain.class);
         String filename = anHandlerChain.file();

         // Setup the endpoint handlers
         UnifiedHandlerChainsMetaData handlerChainsMetaData = getHandlerChainsMetaData(wsClass, filename);
         for (UnifiedHandlerChainMetaData UnifiedHandlerChainMetaData : handlerChainsMetaData.getHandlerChains())
         {
            for (UnifiedHandlerMetaData uhmd : UnifiedHandlerChainMetaData.getHandlers())
            {
               HandlerMetaDataJAXWS hmd = HandlerMetaDataJAXWS.newInstance(uhmd, HandlerType.ENDPOINT);
               epMetaData.addHandler(hmd);
            }
         }
      }
   }

   public static UnifiedHandlerChainsMetaData getHandlerChainsMetaData(Class<?> wsClass, String filename)
   {
      URL fileURL = null;
      log.debug("processHandlerChain [" + filename + "] on: " + wsClass.getName());

      // Try the filename as URL
      try
      {
         fileURL = new URL(filename);
      }
      catch (MalformedURLException ex)
      {
         // ignore
      }

      // Try the filename as File
      if (fileURL == null)
      {
         try
         {
            File file = new File(filename);
            if (file.exists())
               fileURL = file.toURL();
         }
         catch (MalformedURLException e)
         {
            // ignore
         }
      }

      // Try the filename as Resource
      if (fileURL == null)
      {
         log.debug(wsClass.getProtectionDomain().getCodeSource());
         log.debug(wsClass.getClassLoader());
         fileURL = wsClass.getClassLoader().getResource(filename);
      }

      // Try the filename relative to class
      if (fileURL == null)
      {
         String filepath = filename;
         String packagePath = wsClass.getPackage().getName().replace('.', '/');
         String resourcePath = packagePath + "/" + filepath;
         while (filepath.startsWith("../"))
         {
            packagePath = packagePath.substring(0, packagePath.lastIndexOf('/'));
            filepath = filepath.substring(3);
            resourcePath = packagePath + '/' + filepath;
         }
         fileURL = wsClass.getClassLoader().getResource(resourcePath);
      }

      if (fileURL == null)
         throw new WSException("Cannot resolve handler file '" + filename + "' on " + wsClass.getName());

      log.debug("Loading handler chain: " + fileURL);

      UnifiedHandlerChainsMetaData handlerChainsMetaData = null;
      try
      {
         InputStream is = fileURL.openStream();
         try
         {
            Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
            unmarshaller.setValidation(true);
            unmarshaller.setSchemaValidation(true);
            unmarshaller.setEntityResolver(new JBossWSEntityResolver());
            ObjectModelFactory factory = new HandlerChainsObjectFactory();
            handlerChainsMetaData = (UnifiedHandlerChainsMetaData)unmarshaller.unmarshal(is, factory, null);
         }
         finally
         {
            is.close();
         }

      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception ex)
      {
         throw new WSException("Cannot process handler chain: " + filename, ex);
      }

      return handlerChainsMetaData;
   }

   private void addFault(OperationMetaData opMetaData, Class<?> exception)
   {
      if (opMetaData.isOneWay())
         throw new IllegalStateException("JSR-181 4.3.1 - A JSR-181 processor is REQUIRED to report an error if an operation marked "
               + "@Oneway has a return value, declares any checked exceptions or has any INOUT or OUT parameters.");

      WebFault anWebFault = exception.getAnnotation(WebFault.class);

      // Only the element name is effected by @WebFault, the type uses the same convention
      QName xmlType = new QName(opMetaData.getQName().getNamespaceURI(), exception.getSimpleName());

      String name = xmlType.getLocalPart();
      String namespace = xmlType.getNamespaceURI();

      String faultBean = null;
      Class<?> faultBeanClass = getFaultInfo(exception);
      if (faultBeanClass != null)
         faultBean = faultBeanClass.getName();

      /*
       * If @WebFault is present, and the exception contains getFaultInfo, the
       * return value should be used. Otherwise we need to generate the bean.
       */
      if (anWebFault != null)
      {
         if (anWebFault.name().length() > 0)
            name = anWebFault.name();

         if (anWebFault.targetNamespace().length() > 0)
         {
            namespace = anWebFault.targetNamespace();
            XmlType anXmlType = exception.getAnnotation(XmlType.class);
            if (anXmlType != null)
               xmlType = new QName(anXmlType.namespace(), exception.getSimpleName());
         }

         if (anWebFault.faultBean().length() > 0)
            faultBean = anWebFault.faultBean();
      }

      if (faultBean == null)
         faultBean = JavaUtils.getPackageName(opMetaData.getEndpointMetaData().getServiceEndpointInterface()) + ".jaxws." + exception.getSimpleName() + "Bean";

      QName xmlName = new QName(namespace, name);

      FaultMetaData fmd = new FaultMetaData(opMetaData, xmlName, xmlType, exception.getName());
      fmd.setFaultBeanName(faultBean);

      if (fmd.loadFaultBean() == null)
         wrapperGenerator.generate(fmd);

      javaTypes.add(fmd.getFaultBean());
      typeRefs.add(new TypeReference(fmd.getXmlName(), fmd.getFaultBean()));

      opMetaData.addFault(fmd);
   }

   private String convertToVariable(String localName)
   {
      return JAXBRIContext.mangleNameToVariableName(localName.intern());
   }

   private String[] convertTypeArguments(Class<?> rawType, Type type)
   {
      if (!Collection.class.isAssignableFrom(rawType) && !Map.class.isAssignableFrom(rawType))
         return null;

      if (!(type instanceof ParameterizedType))
         return null;

      ParameterizedType paramType = (ParameterizedType)type;
      Type[] arguments = paramType.getActualTypeArguments();
      String[] ret = new String[arguments.length];
      for (int i = 0; i < arguments.length; i++)
         ret[i] = JavaUtils.erasure(arguments[i]).getName();

      return ret;
   }

   private ParameterMetaData createRequestWrapper(OperationMetaData operation, Method method)
   {
      String requestWrapperType = null;
      QName xmlName = operation.getQName();
      QName xmlType = xmlName;
      if (method.isAnnotationPresent(RequestWrapper.class))
      {
         RequestWrapper anReqWrapper = method.getAnnotation(RequestWrapper.class);

         String localName = anReqWrapper.localName().length() > 0 ? anReqWrapper.localName() : xmlName.getLocalPart();
         String targetNamespace = anReqWrapper.targetNamespace().length() > 0 ? anReqWrapper.targetNamespace() : xmlName.getNamespaceURI();
         xmlName = new QName(targetNamespace, localName);

         if (anReqWrapper.className().length() > 0)
            requestWrapperType = anReqWrapper.className();
      }

      // Conformance 3.18, the default value must be the same as the method name
      if (requestWrapperType == null)
      {
         String packageName = JavaUtils.getPackageName(method.getDeclaringClass());
         requestWrapperType = packageName + ".jaxws." + JavaUtils.capitalize(method.getName());
      }

      // JAX-WS p.37 pg.1, the annotation only affects the element name, not the type name
      ParameterMetaData wrapperParameter = new ParameterMetaData(operation, xmlName, xmlType, requestWrapperType);
      wrapperParameter.setAccessorFactoryCreator(new JAXBAccessorFactoryCreator());
      operation.addParameter(wrapperParameter);

      return wrapperParameter;
   }

   private ParameterMetaData createResponseWrapper(OperationMetaData operation, Method method)
   {
      QName operationQName = operation.getQName();
      QName xmlName = new QName(operationQName.getNamespaceURI(), operationQName.getLocalPart() + "Response");
      QName xmlType = xmlName;

      String responseWrapperType = null;
      if (method.isAnnotationPresent(ResponseWrapper.class))
      {
         ResponseWrapper anResWrapper = method.getAnnotation(ResponseWrapper.class);

         String localName = anResWrapper.localName().length() > 0 ? anResWrapper.localName() : xmlName.getLocalPart();
         String targetNamespace = anResWrapper.targetNamespace().length() > 0 ? anResWrapper.targetNamespace() : xmlName.getNamespaceURI();
         xmlName = new QName(targetNamespace, localName);

         if (anResWrapper.className().length() > 0)
            responseWrapperType = anResWrapper.className();
      }

      if (responseWrapperType == null)
      {
         String packageName = JavaUtils.getPackageName(method.getDeclaringClass());
         responseWrapperType = packageName + ".jaxws." + JavaUtils.capitalize(method.getName()) + "Response";
      }

      ParameterMetaData retMetaData = new ParameterMetaData(operation, xmlName, xmlType, responseWrapperType);
      retMetaData.setAccessorFactoryCreator(new JAXBAccessorFactoryCreator());
      operation.setReturnParameter(retMetaData);

      return retMetaData;
   }

   private Class<?> getFaultInfo(Class<?> exception)
   {
      try
      {
         Method method = exception.getMethod("getFaultInfo");
         Class<?> returnType = method.getReturnType();
         if (returnType == void.class)
            return null;

         return returnType;
      }
      catch (SecurityException e)
      {
         throw new WSException("Unexpected security exception: " + e.getMessage(), e);
      }
      catch (NoSuchMethodException e)
      {
         return null;
      }
   }

   private ParameterMode getParameterMode(WebParam anWebParam, Class<?> javaType)
   {
      if (anWebParam != null)
      {
         if (anWebParam.mode() == WebParam.Mode.INOUT)
            return ParameterMode.INOUT;
         if (anWebParam.mode() == WebParam.Mode.OUT)
            return ParameterMode.OUT;
      }

      return HolderUtils.isHolderType(javaType) ? ParameterMode.INOUT : ParameterMode.IN;
   }

   @SuppressWarnings("unchecked")
   private <T extends Annotation> T getAnnotation(Class<T> annotation, Method method, int pos)
   {
      for (Annotation an : method.getParameterAnnotations()[pos])
      {
         if (annotation.isAssignableFrom(an.annotationType()))
         {
            return (T)an;
         }
      }
      return null;
   }

   private QName getWebParamName(OperationMetaData opMetaData, int index, WebParam webParam)
   {
      String namespace = null;
      String name = null;
      boolean header = false;

      if (webParam != null)
      {
         if (webParam.targetNamespace().length() > 0)
            namespace = webParam.targetNamespace();

         // RPC types use the partName for their XML name
         if (webParam.partName().length() > 0 && opMetaData.isRPCLiteral())
            name = webParam.partName();
         else if (webParam.name().length() > 0)
            name = webParam.name();

         header = webParam.header();
      }

      // Bare and headers must be qualified
      if (namespace == null && (opMetaData.isDocumentBare() || header))
         namespace = opMetaData.getQName().getNamespaceURI();

      // RPC body parts must have no namespace
      else if (opMetaData.isRPCLiteral() && !header)
         namespace = null;

      // Bare uses the operation name as the default, everything else is generated
      if (name == null)
         name = opMetaData.isDocumentBare() && !header ? opMetaData.getQName().getLocalPart() : "arg" + index;

      return (namespace != null) ? new QName(namespace, name) : new QName(name);
   }

   private QName getWebResultName(OperationMetaData opMetaData, WebResult anWebResult)
   {
      String name = null;
      String namespace = null;
      boolean header = false;

      if (anWebResult != null)
      {
         if (anWebResult.targetNamespace().length() > 0)
            namespace = anWebResult.targetNamespace();

         // RPC types use the partName for their XML name
         if (anWebResult.partName().length() > 0 && opMetaData.isRPCLiteral())
            name = anWebResult.partName();
         else if (anWebResult.name().length() > 0)
            name = anWebResult.name();

         header = anWebResult.header();
      }

      // Bare and headers must be qualified
      if (namespace == null && (opMetaData.isDocumentBare() || header))
         namespace = opMetaData.getQName().getNamespaceURI();

      // RPC body parts must have no namespace
      else if (opMetaData.isRPCLiteral() && !header)
         namespace = null;

      // Bare uses the operation name as the default, everything else is generated
      if (name == null)
         name = opMetaData.isDocumentBare() && !header ? opMetaData.getResponseName().getLocalPart() : "return";

      return (namespace != null) ? new QName(namespace, name) : new QName(name);
   }

   /**
    * Process operation meta data extensions.
    */
   private void processMetaExtensions(Method method, EndpointMetaData epMetaData, OperationMetaData opMetaData)
   {
      AddressingProperties ADDR = new AddressingPropertiesImpl();
      AddressingOpMetaExt addrExt = new AddressingOpMetaExt(ADDR.getNamespaceURI());

      Action anAction = method.getAnnotation(Action.class);
      if (anAction != null)
      {
         addrExt.setInboundAction(anAction.input());
         addrExt.setOutboundAction(anAction.output());
      }
      else
      // default action values
      {
         String tns = epMetaData.getPortName().getNamespaceURI();
         String portTypeName = epMetaData.getPortName().getLocalPart();
         String opName = opMetaData.getQName().getLocalPart();
         addrExt.setInboundAction(tns + "/" + portTypeName + "/" + opName + "Request");

         if (!opMetaData.isOneWay())
            addrExt.setOutboundAction(tns + "/" + portTypeName + "/" + opName + "Response");
      }

      opMetaData.addExtension(addrExt);
   }

   private void processWebMethod(EndpointMetaData epMetaData, Method method)
   {
      String javaName = method.getName();

      // Methods added by JBoss AOP will be marked as synthetic and should be skipped.
      if (method.isSynthetic() == true)
    	  return;
      
      // skip asnyc methods, they dont need meta data representation
      if (method.getName().endsWith(Constants.ASYNC_METHOD_SUFFIX))
         return;

      // reflection defaults
      String soapAction = "";
      String operationName = method.getName();

      // annotation values that override defaults
      if (method.isAnnotationPresent(WebMethod.class))
      {
         WebMethod anWebMethod = method.getAnnotation(WebMethod.class);
         soapAction = anWebMethod.action();
         if (anWebMethod.operationName().length() > 0)
         {
            operationName = anWebMethod.operationName();
         }
      }

      String targetNS = epMetaData.getPortTypeName().getNamespaceURI();
      OperationMetaData opMetaData = new OperationMetaData(epMetaData, new QName(targetNS, operationName), javaName);
      opMetaData.setOneWay(method.isAnnotationPresent(Oneway.class));
      opMetaData.setSOAPAction(soapAction);

      if (method.isAnnotationPresent(SOAPBinding.class))
      {
         SOAPBinding anBinding = method.getAnnotation(SOAPBinding.class);
         if (anBinding.style() != SOAPBinding.Style.DOCUMENT || epMetaData.getStyle() != Style.DOCUMENT)
            throw new IllegalArgumentException("@SOAPBinding must be specified using DOCUMENT style when placed on a method");
         opMetaData.setParameterStyle(anBinding.parameterStyle());
      }

      if (method.isAnnotationPresent(Documentation.class))
      {
         opMetaData.setDocumentation(method.getAnnotation(Documentation.class).content());
      }

      epMetaData.addOperation(opMetaData);

      // Build parameter meta data
      // Attachment annotations on SEI parameters
      List<AttachmentScanResult> scanResult = ReflectiveAttachmentRefScanner.scanMethod(method);

      Class<?>[] parameterTypes = method.getParameterTypes();
      Type[] genericTypes = method.getGenericParameterTypes();
      Annotation[][] parameterAnnotations = method.getParameterAnnotations();
      ParameterMetaData wrapperParameter = null, wrapperOutputParameter = null;
      List<WrappedParameter> wrappedParameters = null, wrappedOutputParameters = null;

      // Force paramter style to wrapped
      if (method.isAnnotationPresent(RequestWrapper.class) || method.isAnnotationPresent(ResponseWrapper.class))
      {
         epMetaData.setParameterStyle(ParameterStyle.WRAPPED);
      }

      if (opMetaData.isDocumentWrapped())
      {
         wrapperParameter = createRequestWrapper(opMetaData, method);
         wrappedParameters = new ArrayList<WrappedParameter>(parameterTypes.length);
         wrapperParameter.setWrappedParameters(wrappedParameters);

         if (!opMetaData.isOneWay())
         {
            wrapperOutputParameter = createResponseWrapper(opMetaData, method);
            wrappedOutputParameters = new ArrayList<WrappedParameter>(parameterTypes.length + 1);
            wrapperOutputParameter.setWrappedParameters(wrappedOutputParameters);
         }
      }

      for (int i = 0; i < parameterTypes.length; i++)
      {
         Class<?> javaType = parameterTypes[i];
         Type genericType = genericTypes[i];
         String javaTypeName = javaType.getName();
         WebParam anWebParam = getAnnotation(WebParam.class, method, i);
         boolean isHeader = anWebParam != null && anWebParam.header();
         boolean isWrapped = opMetaData.isDocumentWrapped() && !isHeader;
         ParameterMode mode = getParameterMode(anWebParam, javaType);

         // Assert one-way
         if (opMetaData.isOneWay() && mode != ParameterMode.IN)
            throw new IllegalArgumentException("A one-way operation can not have output parameters [" + "method = " + method.getName() + ", parameter = " + i + "]");

         if (HolderUtils.isHolderType(javaType))
         {
            genericType = HolderUtils.getGenericValueType(genericType);
            javaType = JavaUtils.erasure(genericType);
            javaTypeName = javaType.getName();
         }

         if (isWrapped)
         {
            QName wrappedElementName = getWebParamName(opMetaData, i, anWebParam);
            String variable = convertToVariable(wrappedElementName.getLocalPart());

            WrappedParameter wrappedParameter = new WrappedParameter(wrappedElementName, javaTypeName, variable, i);
            wrappedParameter.setTypeArguments(convertTypeArguments(javaType, genericType));
            wrappedParameter.setXmlList(getAnnotation(XmlList.class, method, i) != null);
            XmlJavaTypeAdapter xmlJavaTypeAdapter = getAnnotation(XmlJavaTypeAdapter.class, method, i);
            if (xmlJavaTypeAdapter != null)
            {
               //XmlJavaTypeAdapter.type() is for package only
               wrappedParameter.setAdapter(xmlJavaTypeAdapter.value().getName());
            }

            if (mode != ParameterMode.OUT)
               wrappedParameters.add(wrappedParameter);

            if (mode != ParameterMode.IN)
            {
               wrappedParameter.setHolder(true);

               // WrappedParameters can not be shared between request/response objects (accessors)
               if (mode == ParameterMode.INOUT)
                  wrappedParameter = new WrappedParameter(wrappedParameter);
               wrappedOutputParameters.add(wrappedParameter);
            }

            processAttachmentAnnotationsWrapped(scanResult, i, wrappedParameter);
         }
         else
         {
            QName xmlName = getWebParamName(opMetaData, i, anWebParam);

            ParameterMetaData paramMetaData = new ParameterMetaData(opMetaData, xmlName, javaTypeName);
            paramMetaData.setInHeader(isHeader);
            paramMetaData.setIndex(i);
            paramMetaData.setMode(mode);

            /*
             * Note: The TCK enforces the following rule in the spec regarding
             * partName: "This is only used if the operation is rpc style or if
             * the operation is document style and the parameter style is BARE."
             *
             * This seems to be a flaw in the spec, because the intention is
             * obviously to prevent the ambiguity of wrapped parameters that
             * specify different partName values. There is, however, no reason
             * that this limitation should apply to header parameters since they
             * are never wrapped. In order to comply we adhere to this confusing
             * rule, although I will ask for clarification.
             */
            if (anWebParam != null && !opMetaData.isDocumentWrapped() && anWebParam.partName().length() > 0)
               paramMetaData.setPartName(anWebParam.partName());

            opMetaData.addParameter(paramMetaData);
            javaTypes.add(javaType);
            typeRefs.add(new TypeReference(xmlName, genericType, parameterAnnotations[i]));

            processAttachmentAnnotations(scanResult, i, paramMetaData);
            processMIMEBinding(epMetaData, opMetaData, paramMetaData);
         }
      }

      // Build result meta data
      Class<?> returnType = method.getReturnType();
      Type genericReturnType = method.getGenericReturnType();
      String returnTypeName = returnType.getName();
      if (!(returnType == void.class))
      {
         if (opMetaData.isOneWay())
            throw new IllegalArgumentException("[JSR-181 2.5.1] The method '" + method.getName() + "' can not have a return value if it is marked OneWay");

         WebResult anWebResult = method.getAnnotation(WebResult.class);
         boolean isHeader = anWebResult != null && anWebResult.header();
         boolean isWrappedBody = opMetaData.isDocumentWrapped() && !isHeader;
         QName xmlName = getWebResultName(opMetaData, anWebResult);

         if (isWrappedBody)
         {
            WrappedParameter wrapped = new WrappedParameter(xmlName, returnTypeName, convertToVariable(xmlName.getLocalPart()), -1);
            wrapped.setTypeArguments(convertTypeArguments(returnType, genericReturnType));
            wrapped.setXmlList(method.getAnnotation(XmlList.class) != null);
            XmlJavaTypeAdapter xmlJavaTypeAdapter = method.getAnnotation(XmlJavaTypeAdapter.class);
            if (xmlJavaTypeAdapter != null)
            {
               //XmlJavaTypeAdapter.type() is for package only
               wrapped.setAdapter(xmlJavaTypeAdapter.value().getName());
            }

            // insert at the beginning just for prettiness
            wrappedOutputParameters.add(0, wrapped);

            processAttachmentAnnotationsWrapped(scanResult, -1, wrapped);
         }
         else
         {
            ParameterMetaData retMetaData = new ParameterMetaData(opMetaData, xmlName, returnTypeName);
            retMetaData.setInHeader(isHeader);
            retMetaData.setIndex(-1);
            retMetaData.setMode(ParameterMode.OUT);

            // Special case: If we have a document/literal wrapped message, then
            // the return metadata must be the wrapper type that is sent in the
            // body. So, in order to handle headers that are mapped to the java
            // return value, we have to add them to a parameter with an index of
            // -1 to signify the return value. All other binding styles use the
            // expected return value mechanism.
            if (opMetaData.isDocumentWrapped())
            {
               opMetaData.addParameter(retMetaData);
            }
            else
            {
               // See above comment in the parameter for loop section as to why
               // we prevent customization of part names on document wrapped
               // header parameters.
               if (anWebResult != null && anWebResult.partName().length() > 0)
                  retMetaData.setPartName(anWebResult.partName());

               opMetaData.setReturnParameter(retMetaData);
            }

            javaTypes.add(returnType);
            typeRefs.add(new TypeReference(xmlName, genericReturnType, method.getAnnotations()));

            processAttachmentAnnotations(scanResult, -1, retMetaData);
            processMIMEBinding(epMetaData, opMetaData, retMetaData);
         }
      }

      // Generate wrapper beans
      if (opMetaData.isDocumentWrapped())
      {
         if (wrapperParameter.loadWrapperBean() == null)
            wrapperGenerator.generate(wrapperParameter);

         Class<?> wrapperClass = wrapperParameter.getJavaType();
         javaTypes.add(wrapperClass);

         // In case there is no @XmlRootElement
         typeRefs.add(new TypeReference(wrapperParameter.getXmlName(), wrapperClass));
         if (!opMetaData.isOneWay())
         {
            if (wrapperOutputParameter.loadWrapperBean() == null)
               wrapperGenerator.generate(wrapperOutputParameter);

            wrapperClass = wrapperOutputParameter.getJavaType();
            javaTypes.add(wrapperClass);

            // In case there is no @XmlRootElement
            typeRefs.add(new TypeReference(wrapperOutputParameter.getXmlName(), wrapperClass));
         }
      }

      // Add faults
      for (Class<?> exClass : method.getExceptionTypes())
      {
         // Conformance 3.25 (java.lang.RuntimeExceptions and java.rmi.RemoteExceptions):
         // java.lang.RuntimeException and java.rmi.RemoteException and their subclasses
         // MUST NOT be treated as service specific exceptions and MUST NOT be mapped to WSDL.
         if (!RemoteException.class.isAssignableFrom(exClass) && !RuntimeException.class.isAssignableFrom(exClass))
         {
            addFault(opMetaData, exClass);
         }
      }

      // process operation meta data extension
      processMetaExtensions(method, epMetaData, opMetaData);
   }

   /**
    * @see org.jboss.ws.metadata.builder.jaxws.JAXWSMetaDataBuilder#processAttachmentAnnotations(java.util.List, int, org.jboss.ws.metadata.umdm.ParameterMetaData) 
    * @param scanResult
    * @param i
    * @param wrappedParameter
    */
   private void processAttachmentAnnotationsWrapped(List<AttachmentScanResult> scanResult, int i, WrappedParameter wrappedParameter)
   {
      AttachmentScanResult asr = ReflectiveAttachmentRefScanner.getResultByIndex(scanResult, i);
      if (asr != null)
      {
         if (AttachmentScanResult.Type.SWA_REF == asr.getType())
            wrappedParameter.setSwaRef(true);
         else
            wrappedParameter.setXOP(true);
      }
   }

   /**
    * Update PMD according to attachment annotations that might be in place
    * @param scanResult
    * @param i
    * @param parameter
    */
   private void processAttachmentAnnotations(List<AttachmentScanResult> scanResult, int i, ParameterMetaData parameter)
   {
      AttachmentScanResult asr = ReflectiveAttachmentRefScanner.getResultByIndex(scanResult, i);
      if (asr != null)
      {
         if (AttachmentScanResult.Type.SWA_REF == asr.getType())
            parameter.setSwaRef(true);
         else
            parameter.setXOP(true);
      }
   }

   private void processMIMEBinding(EndpointMetaData epMetaData, OperationMetaData opMetaData, ParameterMetaData paramMetaData)
   {
      // process SWA metadata
      WSDLDefinitions wsdlDef = epMetaData.getServiceMetaData().getWsdlDefinitions();
      if (wsdlDef != null)
      {
         for (WSDLBinding binding : wsdlDef.getBindings())
         {
            for (WSDLBindingOperation bindingOp : binding.getOperations())
            {
               // it might an input or output parameter
               WSDLBindingMessageReference[] inOrOutPut = (paramMetaData.getMode().equals(ParameterMode.IN) || paramMetaData.getMode().equals(ParameterMode.INOUT)) ? (WSDLBindingMessageReference[])bindingOp
                     .getInputs()
                     : (WSDLBindingMessageReference[])bindingOp.getOutputs();

               if (inOrOutPut.length > 0)
               {
                  // find matching operation
                  if (bindingOp.getRef().equals(opMetaData.getQName()))
                  {
                     WSDLBindingMessageReference bindingInput = inOrOutPut[0];
                     for (WSDLMIMEPart mimePart : bindingInput.getMimeParts())
                     {
                        String partName = mimePart.getPartName();
                        if (paramMetaData.getPartName().equals(partName))
                        {
                           log.debug("Identified 'mime:content' binding: " + partName + ", mimeTypes=" + mimePart.getMimeTypes());
                           paramMetaData.setSwA(true);
                           paramMetaData.setMimeTypes(mimePart.getMimeTypes());
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

   }

   protected void processWebMethods(EndpointMetaData epMetaData, Class<?> wsClass)
   {
      epMetaData.clearOperations();

      // Process @WebMethod annotations
      int webMethodCount = 0;
      for (Method method : wsClass.getMethods())
      {
         WebMethod annotation = method.getAnnotation(WebMethod.class);
         boolean exclude = annotation != null && annotation.exclude();
         if (!exclude && (annotation != null || wsClass.isInterface()))
         {
            processWebMethod(epMetaData, method);
            webMethodCount++;
         }
      }

      // @WebService should expose all inherited methods if @WebMethod is never specified
      if (webMethodCount == 0 && !wsClass.isInterface())
      {
         for (Method method : wsClass.getMethods())
         {
            WebMethod annotation = method.getAnnotation(WebMethod.class);
            boolean exclude = annotation != null && annotation.exclude();
            if (!exclude && method.getDeclaringClass() != Object.class)
            {
               processWebMethod(epMetaData, method);
               webMethodCount++;
            }
         }
      }

      if (webMethodCount == 0)
         throw new WSException("No exposable methods found");
   }

   protected void initWrapperGenerator(ClassLoader loader)
   {
      // Use the dynamic generator by default. Otherwise reset the last
      if (wrapperGenerator == null)
         wrapperGenerator = new DynamicWrapperGenerator(loader);
      else
         wrapperGenerator.reset(loader);
   }

   protected void resetMetaDataBuilder(ClassLoader loader)
   {
      initWrapperGenerator(loader);
      javaTypes.clear();
      typeRefs.clear();
      jaxbCtx = null;
   }

   protected void createJAXBContext(EndpointMetaData epMetaData)
   {
      try
      {
         String targetNS = epMetaData.getPortTypeName().getNamespaceURI().intern();
         if (log.isDebugEnabled())
            log.debug("JAXBContext [types=" + javaTypes + ",tns=" + targetNS + "]");

         JAXBContextFactory factory = JAXBContextFactory.newInstance();

         // JAXBIntros may mofiy the WSDL being generated
         // only true for server side invocation, tooling (WSProvide) doesnt support this
         BindingCustomization bindingCustomization = null;
         if (epMetaData instanceof ServerEndpointMetaData)
         {
            Endpoint endpoint = ((ServerEndpointMetaData)epMetaData).getEndpoint();
            bindingCustomization = endpoint != null ? endpoint.getAttachment(BindingCustomization.class) : null;
         }

         jaxbCtx = factory.createContext(javaTypes.toArray(new Class[0]), typeRefs, targetNS, false, bindingCustomization);
      }
      catch (WSException ex)
      {
         throw new IllegalStateException("Cannot build JAXB context", ex);
      }
   }

   private void populateXmlType(FaultMetaData faultMetaData)
   {
      EndpointMetaData epMetaData = faultMetaData.getOperationMetaData().getEndpointMetaData();
      TypesMetaData types = epMetaData.getServiceMetaData().getTypesMetaData();

      QName xmlType = faultMetaData.getXmlType();
      String faultBeanName = faultMetaData.getFaultBeanName();

      types.addTypeMapping(new TypeMappingMetaData(types, xmlType, faultBeanName));
   }

   private void populateXmlType(ParameterMetaData paramMetaData)
   {
      EndpointMetaData epMetaData = paramMetaData.getOperationMetaData().getEndpointMetaData();
      TypesMetaData types = epMetaData.getServiceMetaData().getTypesMetaData();

      QName xmlName = paramMetaData.getXmlName();
      QName xmlType = paramMetaData.getXmlType();
      Class<?> javaType = paramMetaData.getJavaType();
      String javaName = paramMetaData.getJavaTypeName();

      if (xmlType == null)
      {
         try
         {
            xmlType = jaxbCtx.getTypeName(new TypeReference(xmlName, javaType));
         }
         catch (IllegalArgumentException e)
         {
            throw new IllegalStateException("Cannot obtain xml type for: [xmlName=" + xmlName + ",javaName=" + javaName + "]");
         }

         /* Anonymous type.
          *
          * Currently the design of our stack is based on the
          * notion of their always being a unique type. In order to lookup the
          * appropriate (de)serializer you must have a type. So we use a fake
          * name. This is an illegal NCName, so it shouldn't collide.
          */
         if (xmlType == null)
            xmlType = new QName(xmlName.getNamespaceURI(), ">" + xmlName.getLocalPart());

         paramMetaData.setXmlType(xmlType);
      }

      types.addTypeMapping(new TypeMappingMetaData(types, xmlType, javaName));
   }

   protected void populateXmlTypes(EndpointMetaData epMetaData)
   {
      for (OperationMetaData operation : epMetaData.getOperations())
      {
         // parameters
         for (ParameterMetaData paramMetaData : operation.getParameters())
         {
            populateXmlType(paramMetaData);
         }

         // return value
         ParameterMetaData returnParameter = operation.getReturnParameter();
         if (returnParameter != null)
            populateXmlType(returnParameter);

         // faults
         for (FaultMetaData faultMetaData : operation.getFaults())
         {
            populateXmlType(faultMetaData);
         }
      }
   }

   /**
    * Set the wrapper generator for this builder.
    */
   public void setWrapperGenerator(WrapperGenerator wrapperGenerator)
   {
      this.wrapperGenerator = wrapperGenerator;
   }
}
