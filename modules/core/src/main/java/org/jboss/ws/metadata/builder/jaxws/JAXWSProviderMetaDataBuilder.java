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

import java.io.IOException;
import java.net.URL;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Provider;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.Service.Mode;

import org.jboss.ws.Constants;
import org.jboss.ws.core.soap.SOAPContentElement;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.metadata.builder.MetaDataBuilder;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData.Type;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.wsf.common.JavaUtils;
import org.jboss.wsf.spi.deployment.ArchiveDeployment;
import org.jboss.wsf.spi.deployment.Endpoint;

/**
 * A server side meta data builder that is based on JSR-181 annotations
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @since 23-Jul-2005
 */
public class JAXWSProviderMetaDataBuilder extends JAXWSServerMetaDataBuilder
{
   public ServerEndpointMetaData buildProviderMetaData(ArchiveDeployment dep, UnifiedMetaData wsMetaData, Class<?> sepClass, String linkName) throws IOException
   {
      // 5.3 Conformance (Provider implementation): A Provider based service endpoint implementation MUST
      // implement a typed Provider interface.
      if (JavaUtils.isAssignableFrom(Provider.class, sepClass) == false)
         throw new WebServiceException("Endpoint implementation does not implement javax.xml.ws.Provider: " + sepClass.getName());

      // 5.4 Conformance (WebServiceProvider annotation): A Provider based service endpoint implementation
      // MUST carry a WebServiceProvider annotation
      WebServiceProvider anWebServiceProvider = (WebServiceProvider)sepClass.getAnnotation(WebServiceProvider.class);
      if (anWebServiceProvider == null)
         throw new WebServiceException("Cannot obtain @WebServiceProvider annotation from: " + sepClass.getName());

      // 7.3 Conformance (WebServiceProvider and WebService): A class annotated with the WebServiceProvider
      // annotation MUST NOT carry a WebService annotation
      if (sepClass.isAnnotationPresent(WebService.class))
         throw new WebServiceException("Provider cannot carry @WebService annotation: " + sepClass.getName());

      WSDLUtils wsdlUtils = WSDLUtils.getInstance();

      String name = wsdlUtils.getJustClassName(sepClass);

      String serviceName = anWebServiceProvider.serviceName();
      if (serviceName.length() == 0)
         serviceName = name + "Service";

      String targetNS = anWebServiceProvider.targetNamespace();
      if (targetNS.length() == 0)
         targetNS = wsdlUtils.getTypeNamespace(sepClass);

      String portName = anWebServiceProvider.portName();
      if (portName.length() == 0)
         portName = name + "Port";

      ServiceMetaData serviceMetaData = new ServiceMetaData(wsMetaData, new QName(targetNS, serviceName));
      wsMetaData.addService(serviceMetaData);

      // Setup the ServerEndpointMetaData
      QName portQName = new QName(targetNS, portName);
      QName portTypeQName = new QName(targetNS, name);
      
      Endpoint ep = dep.getService().getEndpointByName(linkName);
      ServerEndpointMetaData sepMetaData = new ServerEndpointMetaData(serviceMetaData, ep, portQName, portTypeQName, Type.JAXWS);
      sepMetaData.setLinkName(linkName);

      sepMetaData.setStyle(Style.DOCUMENT);
      sepMetaData.setParameterStyle(ParameterStyle.BARE);

      sepMetaData.setServiceEndpointImplName(sepClass.getName());
      sepMetaData.setServiceEndpointInterfaceName(sepClass.getName());

      ServiceMode anServiceMode = sepClass.getAnnotation(ServiceMode.class);
      sepMetaData.setServiceMode(anServiceMode != null ? anServiceMode.value() : Mode.PAYLOAD);

      serviceMetaData.addEndpoint(sepMetaData);

      // Process invoke method
      processInvokeMethod(sepMetaData);

      // Process WSDL
      String wsdlLocation = anWebServiceProvider.wsdlLocation();
      if (wsdlLocation.length() > 0)
      {
         URL wsdlURL = dep.getMetaDataFileURL(wsdlLocation);
         serviceMetaData.setWsdlLocation(wsdlURL);
      }

      // process binding type
      processBindingType(sepMetaData, sepClass);

      // process handler chain
      processHandlerChain(sepMetaData, sepClass);

      // process config
      processEndpointConfig(dep, sepMetaData, sepClass, linkName);

      // Set the endpoint address
      processWebContext(dep, sepClass, linkName, sepMetaData);

      // Init the endpoint address
      initEndpointAddress(dep, sepMetaData);

      // A provider may not have a WSDL file
      if (sepMetaData.getServiceMetaData().getWsdlLocation() != null)
         MetaDataBuilder.replaceAddressLocation(sepMetaData);

      // init service endpoint id
      ObjectName sepID = MetaDataBuilder.createServiceEndpointID(dep, sepMetaData);
      sepMetaData.setServiceEndpointID(sepID);

      return sepMetaData;
   }

   private void processInvokeMethod(ServerEndpointMetaData epMetaData)
   {
      String javaName = "invoke";
      String targetNS = epMetaData.getPortName().getNamespaceURI();
      OperationMetaData opMetaData = new OperationMetaData(epMetaData, new QName(targetNS, javaName), javaName);
      epMetaData.addOperation(opMetaData);

      // Setup invoke param
      Class paramType = Source.class;
      QName xmlName = SOAPContentElement.GENERIC_PARAM_NAME;
      QName xmlType = Constants.TYPE_LITERAL_ANYTYPE;
      ParameterMetaData pmd = new ParameterMetaData(opMetaData, xmlName, xmlType, paramType.getName());
      opMetaData.addParameter(pmd);

      // Setup invoke return
      xmlName = SOAPContentElement.GENERIC_RETURN_NAME;
      ParameterMetaData retMetaData = new ParameterMetaData(opMetaData, xmlName, xmlType, paramType.getName());
      opMetaData.setReturnParameter(retMetaData);
   }
}
