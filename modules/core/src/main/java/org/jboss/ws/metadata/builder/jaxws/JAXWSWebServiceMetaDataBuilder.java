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
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.net.URL;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.soap.SOAPMessageHandlers;
import javax.management.ObjectName;
import javax.xml.namespace.QName;

import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.annotation.Documentation;
import org.jboss.ws.extensions.policy.annotation.PolicyAttachment;
import org.jboss.ws.extensions.policy.metadata.PolicyMetaDataBuilder;
import org.jboss.ws.metadata.builder.MetaDataBuilder;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.HandlerMetaDataJAXWS;
import org.jboss.ws.metadata.umdm.ServerEndpointMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsse.WSSecurityConfigFactory;
import org.jboss.ws.metadata.wsse.WSSecurityConfiguration;
import org.jboss.ws.metadata.wsse.WSSecurityOMFactory;
import org.jboss.ws.tools.ToolsUtils;
import org.jboss.ws.tools.wsdl.JAXBWSDLGenerator;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.ws.tools.wsdl.WSDLGenerator;
import org.jboss.ws.tools.wsdl.WSDLWriter;
import org.jboss.ws.tools.wsdl.WSDLWriterResolver;
import org.jboss.wsf.common.IOUtils;
import org.jboss.wsf.spi.deployment.ArchiveDeployment;
import org.jboss.wsf.spi.deployment.Deployment;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainsMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData.HandlerType;
import org.jboss.wsf.spi.metadata.webservices.PortComponentMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebserviceDescriptionMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebservicesFactory;
import org.jboss.wsf.spi.metadata.webservices.WebservicesMetaData;

/**
 * An abstract annotation meta data builder.
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @author Heiko.Braun@jboss.org
 *
 * @since 15-Oct-2005
 */
@SuppressWarnings("deprecation")
public class JAXWSWebServiceMetaDataBuilder extends JAXWSServerMetaDataBuilder
{
   private boolean generateWsdl = true;
   private boolean toolMode = false;
   private File wsdlDirectory = null;
   private PrintStream messageStream = null;

   private static class EndpointResult
   {
      private Class<?> epClass;
      private ServerEndpointMetaData sepMetaData;
      private ServiceMetaData serviceMetaData;
      private URL wsdlLocation;
      private URL policyLocation;
   }

   public void setGenerateWsdl(boolean generateWsdl)
   {
      this.generateWsdl = generateWsdl;
   }

   public ServerEndpointMetaData buildWebServiceMetaData(Deployment dep, UnifiedMetaData wsMetaData, Class<?> sepClass, String linkName)
   {
      try
      {
         EndpointResult result = processWebService(dep, wsMetaData, sepClass, linkName);

         // Clear the java types, etc.
         ClassLoader runtimeClassLoader = dep.getRuntimeClassLoader();
         if(null == runtimeClassLoader)
            throw new IllegalArgumentException("Runtime loader cannot be null");
         
         resetMetaDataBuilder(runtimeClassLoader);

         ServerEndpointMetaData sepMetaData = result.sepMetaData;
         ServiceMetaData serviceMetaData = result.serviceMetaData;
         serviceMetaData.setWsdlLocation(result.wsdlLocation);
         Class<?> seiClass = result.epClass;

         sepMetaData.setLinkName(linkName);
         sepMetaData.setServiceEndpointImplName(sepClass.getName());
         sepMetaData.setServiceEndpointInterfaceName(seiClass.getName());

         // Assign the WS-Security configuration,
         WSSecurityConfigFactory wsseConfFactory = WSSecurityConfigFactory.newInstance();
         WSSecurityConfiguration securityConfiguration = wsseConfFactory.createConfiguration(wsMetaData.getRootFile(), WSSecurityOMFactory.SERVER_RESOURCE_NAME);
         serviceMetaData.setSecurityConfiguration(securityConfiguration);

         // Process an optional @SOAPBinding annotation
         processSOAPBinding(sepMetaData, seiClass);

         // Process an optional @BindingType annotation
         processBindingType(sepMetaData, sepClass);

         // process config
         processEndpointConfig(dep, sepMetaData, sepClass, linkName);

         // process web service features
         EndpointFeatureProcessor epFeatureProcessor = new EndpointFeatureProcessor();
         epFeatureProcessor.processEndpointFeatures(dep, sepMetaData, sepClass);

         // Process endpoint documentation
         if (seiClass.isAnnotationPresent(Documentation.class))
            sepMetaData.setDocumentation(seiClass.getAnnotation(Documentation.class).content());
         
         // Process web methods
         processWebMethods(sepMetaData, seiClass);

         // Init the transport guarantee
         initTransportGuaranteeJSE(dep, sepMetaData, linkName);

         // Initialize types
         createJAXBContext(sepMetaData);
         populateXmlTypes(sepMetaData);

         //Process an optional @PolicyAttachment annotation
         if (sepClass.isAnnotationPresent(PolicyAttachment.class))
         {
            PolicyMetaDataBuilder policyBuilder = PolicyMetaDataBuilder.getServerSidePolicyMetaDataBuilder(toolMode);
            policyBuilder.processPolicyAnnotations(sepMetaData, sepClass);
         }

         // The server must always generate WSDL
         if (generateWsdl || !toolMode)
            processOrGenerateWSDL(seiClass, serviceMetaData, result.wsdlLocation, sepMetaData);

         // No need to process endpoint items if we are in tool mode
         if (toolMode)
            return sepMetaData;

         // Sanity check: read the generated WSDL and initialize the schema model
         // Note, this should no longer be needed, look into removing it
         WSDLDefinitions wsdlDefinitions = serviceMetaData.getWsdlDefinitions();
         JBossXSModel schemaModel = WSDLUtils.getSchemaModel(wsdlDefinitions.getWsdlTypes());
         serviceMetaData.getTypesMetaData().setSchemaModel(schemaModel);

         // Note, that @WebContext needs to be defined on the endpoint not the SEI
         processWebContext(dep, sepClass, linkName, sepMetaData);

         // setup handler chain from config
         sepMetaData.initEndpointConfig();

         // Process an optional @HandlerChain annotation
         if (sepClass.isAnnotationPresent(HandlerChain.class))
            processHandlerChain(sepMetaData, sepClass);
         else if (seiClass.isAnnotationPresent(HandlerChain.class))
            processHandlerChain(sepMetaData, seiClass);
         
         //setup web service feature contributions
         epFeatureProcessor.setupEndpointFeatures(sepMetaData);

         // process webservices.xml contributions
         processWSDDContribution(sepMetaData);

         // Init the endpoint address
         initEndpointAddress(dep, sepMetaData);

         // Process an optional @SOAPMessageHandlers annotation
         if (sepClass.isAnnotationPresent(SOAPMessageHandlers.class) || seiClass.isAnnotationPresent(SOAPMessageHandlers.class))
            log.warn("@SOAPMessageHandlers is deprecated as of JAX-WS 2.0 with no replacement.");

         MetaDataBuilder.replaceAddressLocation(sepMetaData);
         processEndpointMetaDataExtensions(sepMetaData, wsdlDefinitions);

         // init service endpoint id
         ObjectName sepID = MetaDataBuilder.createServiceEndpointID(dep, sepMetaData);
         sepMetaData.setServiceEndpointID(sepID);

         return sepMetaData;
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception ex)
      {
         WSException.rethrow("Cannot build meta data: " + ex.getMessage(), ex);
         return null;
      }
   }

   /**
    * With JAX-WS the use of webservices.xml is optional since the annotations can be used
    * to specify most of the information specified in this deployment descriptor file.
    * The deployment descriptors are only used to override or augment the annotation member attributes.
    * @param sepMetaData
    */
   private void processWSDDContribution(ServerEndpointMetaData sepMetaData)
   {
      WebservicesMetaData webservices = WebservicesFactory.loadFromVFSRoot(sepMetaData.getRootFile());
      if (webservices != null)
      {
         for (WebserviceDescriptionMetaData wsDesc : webservices.getWebserviceDescriptions())
         {
            for (PortComponentMetaData portComp : wsDesc.getPortComponents())
            {
               // We match portComp's by SEI first and portQName second
               // In the first case the portComp may override the portQName that derives from the annotation
               String portCompSEI = portComp.getServiceEndpointInterface();
               boolean doesMatch = portCompSEI != null ? portCompSEI.equals(sepMetaData.getServiceEndpointInterfaceName()) : false;
               if (!doesMatch)
               {
                  doesMatch = portComp.getWsdlPort().equals(sepMetaData.getPortName());
               }

               if (doesMatch)
               {

                  log.debug("Processing 'webservices.xml' contributions on EndpointMetaData");

                  // PortQName overrides
                  if (portComp.getWsdlPort() != null)
                  {
                     log.debug("Override EndpointMetaData portName " + sepMetaData.getPortName() + " with " + portComp.getWsdlPort());
                     sepMetaData.setPortName(portComp.getWsdlPort());
                  }

                  // HandlerChain contributions
                  UnifiedHandlerChainsMetaData chainWrapper = portComp.getHandlerChains();
                  if (chainWrapper != null)
                  {
                     for (UnifiedHandlerChainMetaData handlerChain : chainWrapper.getHandlerChains())
                     {
                        for (UnifiedHandlerMetaData uhmd : handlerChain.getHandlers())
                        {
                           log.debug("Contribute handler from webservices.xml: " + uhmd.getHandlerName());
                           HandlerMetaDataJAXWS hmd = HandlerMetaDataJAXWS.newInstance(uhmd, HandlerType.ENDPOINT);
                           sepMetaData.addHandler(hmd);
                        }
                     }
                  }

                  // MTOM settings
                  if (portComp.isEnableMtom())
                  {
                     log.debug("Enabling MTOM");

                     String bindingId = sepMetaData.getBindingId();
                     if (bindingId.equals(Constants.SOAP11HTTP_BINDING))
                        sepMetaData.setBindingId(Constants.SOAP11HTTP_MTOM_BINDING);
                     else if (bindingId.equals(Constants.SOAP12HTTP_BINDING))
                        sepMetaData.setBindingId(Constants.SOAP12HTTP_MTOM_BINDING);

                  }

               }
            }
         }

      }
   }

   private EndpointResult processWebService(Deployment dep, UnifiedMetaData wsMetaData, Class<?> sepClass, String linkName) throws ClassNotFoundException, IOException
   {
      WebService anWebService = sepClass.getAnnotation(WebService.class);
      if (anWebService == null)
         throw new WSException("Cannot obtain @WebService annotation from: " + sepClass.getName());

      Endpoint ep = dep.getService().getEndpointByName(linkName);
      
      Class<?> seiClass = null;
      String seiName;
      WSDLUtils wsdlUtils = WSDLUtils.getInstance();

      String name = anWebService.name();
      if (name.length() == 0)
         name = WSDLUtils.getJustClassName(sepClass);

      String serviceName = anWebService.serviceName();
      if (serviceName.length() == 0)
         serviceName = WSDLUtils.getJustClassName(sepClass) + "Service";

      String serviceNS = anWebService.targetNamespace();
      if (serviceNS.length() == 0)
         serviceNS = wsdlUtils.getTypeNamespace(sepClass);

      String portName = anWebService.portName();
      if (portName.length() == 0)
         portName = name + "Port";

      String wsdlLocation = anWebService.wsdlLocation();
      String interfaceNS = serviceNS; // the default, but a SEI annotation may override this

      if (anWebService.endpointInterface().length() > 0)
      {
         seiName = anWebService.endpointInterface();
         ClassLoader runtimeClassLoader = dep.getRuntimeClassLoader();
         if(null == runtimeClassLoader)
            throw new IllegalArgumentException("Runtime loader cannot be null");
         
         seiClass = runtimeClassLoader.loadClass(seiName);
         WebService seiAnnotation = seiClass.getAnnotation(WebService.class);

         if (seiAnnotation == null)
            throw new WSException("Interface does not have a @WebService annotation: " + seiName);

         if (seiAnnotation.portName().length() > 0 || seiAnnotation.serviceName().length() > 0 || seiAnnotation.endpointInterface().length() > 0)
            throw new WSException("@WebService cannot have attribute 'portName', 'serviceName', 'endpointInterface' on: " + seiName);

         // Redefine the interface or "PortType" name
         name = seiAnnotation.name();
         if (name.length() == 0)
            name = WSDLUtils.getJustClassName(seiClass);

         interfaceNS = seiAnnotation.targetNamespace();
         if (interfaceNS.length() == 0)
            interfaceNS = wsdlUtils.getTypeNamespace(seiClass);

         // The spec states that WSDL location should be allowed on an SEI, although it
         // makes far more sense on the implementation bean, so we ONLY consider the SEI
         // wsdlLocation when it is not defined on the bean already

         if (wsdlLocation.length() == 0)
            wsdlLocation = seiAnnotation.wsdlLocation();
      }

      // Setup the ServerEndpointMetaData
      QName portQName = new QName(serviceNS, portName);
      QName portTypeQName = new QName(interfaceNS, name);

      EndpointResult result = new EndpointResult();
      result.serviceMetaData = new ServiceMetaData(wsMetaData, new QName(serviceNS, serviceName));
      result.sepMetaData = new ServerEndpointMetaData(result.serviceMetaData, ep, portQName, portTypeQName, EndpointMetaData.Type.JAXWS);
      result.epClass = (seiClass != null ? seiClass : sepClass);
      result.serviceMetaData.addEndpoint(result.sepMetaData);
      wsMetaData.addService(result.serviceMetaData);

      if (dep instanceof ArchiveDeployment)
         result.wsdlLocation = ((ArchiveDeployment)dep).getMetaDataFileURL(wsdlLocation);

      return result;
   }

   private void processOrGenerateWSDL(Class wsClass, ServiceMetaData serviceMetaData, URL wsdlLocation, EndpointMetaData epMetaData)
   {
      PolicyMetaDataBuilder policyBuilder = PolicyMetaDataBuilder.getServerSidePolicyMetaDataBuilder(toolMode);
      try
      {
         WSDLGenerator generator = new JAXBWSDLGenerator(jaxbCtx);
         WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
         if (wsdlLocation != null)
         {
            //we can no longer use the user provided wsdl without parsing it right now, since we
            //need to look for policies and eventually choose the supported policy alternatives
            WSDLDefinitions wsdlDefinitions = factory.parse(wsdlLocation);
            policyBuilder.processPolicyExtensions(epMetaData, wsdlDefinitions);
            //now we have the UMDM containing policy data; anyway we can't write a new wsdl file with
            //the supported alternatives and so on, since we need to publish the file the user provided
            serviceMetaData.setWsdlLocation(wsdlLocation);
         }
         else
         {
            WSDLDefinitions wsdlDefinitions = generator.generate(serviceMetaData);
            writeWsdl(serviceMetaData, wsdlDefinitions, epMetaData);
         }
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (IOException e)
      {
         throw new WSException("Cannot write generated wsdl", e);
      }
   }

   private void writeWsdl(ServiceMetaData serviceMetaData, WSDLDefinitions wsdlDefinitions, EndpointMetaData epMetaData) throws IOException
   {
      // The RI uses upper case, and the TCK expects it, so we just mimic this even though we don't really have to
      String wsdlName = ToolsUtils.firstLetterUpperCase(serviceMetaData.getServiceName().getLocalPart());
      // Ensure that types are only in the interface qname
      wsdlDefinitions.getWsdlTypes().setNamespace(epMetaData.getPortTypeName().getNamespaceURI());

      final File dir, wsdlFile;

      if (wsdlDirectory != null)
      {
         dir = wsdlDirectory;
         wsdlFile = new File(dir, wsdlName + ".wsdl");
      }
      else
      {
         dir = IOUtils.createTempDirectory();
         wsdlFile = File.createTempFile(wsdlName, ".wsdl", dir);
         wsdlFile.deleteOnExit();
      }

      message(wsdlFile.getName());
      Writer writer = IOUtils.getCharsetFileWriter(wsdlFile, Constants.DEFAULT_XML_CHARSET);
      new WSDLWriter(wsdlDefinitions).write(writer, Constants.DEFAULT_XML_CHARSET, new WSDLWriterResolver() {
         public WSDLWriterResolver resolve(String suggestedFile) throws IOException
         {
            File file;
            if (wsdlDirectory != null)
            {
               file = new File(dir, suggestedFile + ".wsdl");
            }
            else
            {
               file = File.createTempFile(suggestedFile, ".wsdl", dir);
               file.deleteOnExit();
            }
            actualFile = file.getName();
            message(actualFile);
            charset = Constants.DEFAULT_XML_CHARSET;
            writer = IOUtils.getCharsetFileWriter(file, Constants.DEFAULT_XML_CHARSET);
            return this;
         }
      });
      writer.close();

      serviceMetaData.setWsdlLocation(wsdlFile.toURL());
   }

   private void message(String msg)
   {
      if (messageStream != null)
         messageStream.println(msg);
   }

   public void setToolMode(boolean toolMode)
   {
      this.toolMode = toolMode;
   }

   public void setWsdlDirectory(File wsdlDirectory)
   {
      this.wsdlDirectory = wsdlDirectory;
   }

   public void setMessageStream(PrintStream messageStream)
   {
      this.messageStream = messageStream;
   }
}
