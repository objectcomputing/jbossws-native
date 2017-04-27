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
package org.jboss.ws.tools.helpers;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaXmlTypeMapping;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.ws.metadata.umdm.WrappedParameter;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.tools.Configuration;
import org.jboss.ws.tools.JavaToWSDL;
import org.jboss.ws.tools.JavaWriter;
import org.jboss.ws.tools.NamespacePackageMapping;
import org.jboss.ws.tools.WSDLToJava;
import org.jboss.ws.tools.WebservicesXMLCreatorImpl;
import org.jboss.ws.tools.Configuration.GlobalConfig;
import org.jboss.ws.tools.Configuration.JavaToWSDLConfig;
import org.jboss.ws.tools.Configuration.WSDLToJavaConfig;
import org.jboss.ws.tools.XSDTypeToJava.VAR;
import org.jboss.ws.tools.client.ServiceCreator;
import org.jboss.ws.tools.interfaces.WebservicesXMLCreator;
import org.jboss.ws.tools.mapping.MappingFileGenerator;
import org.jboss.ws.tools.wsdl.WSDLWriter;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.jboss.wsf.common.IOUtils;
import org.jboss.wsf.common.JavaUtils;

/**
 *  Helper class used by the cmd line tool "jbossws"
 *  and ant task "wstools"
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 19, 2005
 */
public class ToolsHelper
{
   private static Logger log = Logger.getLogger(ToolsHelper.class);

   /**
    * Java To WSDL Generation [Serverside Generation]
    *
    * @param config
    * @param outDir
    * @throws IOException
    */
   public void handleJavaToWSDLGeneration(Configuration config, String outDir) throws IOException
   {
      JavaToWSDLConfig j2wc = config.getJavaToWSDLConfig(false);
      JavaToWSDL jwsdl = new JavaToWSDL(Constants.NS_WSDL11);
      jwsdl.setServiceName(j2wc.serviceName);
      jwsdl.setTargetNamespace(j2wc.targetNamespace);
      jwsdl.setTypeNamespace(j2wc.typeNamespace);
      jwsdl.setOperationMap(j2wc.operations);

      if ("document".equals(j2wc.wsdlStyle))
         jwsdl.setStyle(Style.DOCUMENT);
      else if ("rpc".equals(j2wc.wsdlStyle))
         jwsdl.setStyle(Style.RPC);
      else throw new WSException("Unrecognized Style:" + j2wc.wsdlStyle);

      if ("wrapped".equals(j2wc.parameterStyle))
         jwsdl.setParameterStyle(ParameterStyle.WRAPPED);
      else if ("bare".equals(j2wc.parameterStyle))
         jwsdl.setParameterStyle(ParameterStyle.BARE);
      else throw new WSException("Unrecognized Parameter Style:" + j2wc.parameterStyle);

      Class endpointClass = loadClass(j2wc.endpointName);

      if (endpointClass == null)
         throw new WSException("Endpoint " + j2wc.endpointName + " cannot be loaded");

      //Take care of passing global config details
      GlobalConfig gcfg = config.getGlobalConfig(false);
      if (gcfg != null)
      {
         if (gcfg.packageNamespaceMap != null)
            jwsdl.setPackageNamespaceMap(gcfg.packageNamespaceMap);
      }
      WSDLDefinitions wsdl = jwsdl.generate(endpointClass);
      //Create the WSDL Directory
      createDir(outDir + "/wsdl");
      String wsdlPath = outDir + "/wsdl/" + j2wc.serviceName + ".wsdl";
      //Generate the WSDL
      Writer fw = IOUtils.getCharsetFileWriter(new File(wsdlPath), Constants.DEFAULT_XML_CHARSET);
      new WSDLWriter(wsdl).write(fw, Constants.DEFAULT_XML_CHARSET);
      fw.close();

      //Generate the Mapping File
      if (j2wc.mappingFileNeeded)
      {
         UnifiedMetaData unifiedMetaData = jwsdl.getUnifiedMetaData();
         JavaWsdlMapping mapping = jwsdl.getJavaWsdlMapping();

         createWrapperTypes(j2wc, outDir, unifiedMetaData, mapping, endpointClass);
         Writer writer = IOUtils.getCharsetFileWriter(new File(outDir + "/" + j2wc.mappingFileName), Constants.DEFAULT_XML_CHARSET);
         writer.write(Constants.XML_HEADER);
         writer.write(DOMWriter.printNode(DOMUtils.parse(mapping.serialize()), true));
         writer.close();
      }

      //Generate the webservices.xml file
      if (j2wc.wsxmlFileNeeded)
      {
         WebservicesXMLCreator wscr = new WebservicesXMLCreatorImpl();
         wscr.setTargetNamespace(j2wc.targetNamespace);
         //wscr.setLocation(new File(outDir).toURL());
         wscr.setSeiName(j2wc.endpointName);
         wscr.setServiceName(j2wc.serviceName);
         //Get the portname from wsdl definitions
         WSDLService wsdlService = wsdl.getService(j2wc.serviceName);
         String portName = wsdlService.getEndpoints()[0].getName().getLocalPart();
         //wscr.setPortName(j2wc.serviceName + "Port");
         wscr.setPortName(portName);
         //wscr.setMappingFileName(j2wc.mappingFileName);
         if (j2wc.servletLink != null)
         {
            wscr.setMappingFile("WEB-INF/" + j2wc.mappingFileName);
            wscr.setWsdlFile("WEB-INF/wsdl/" + j2wc.serviceName + ".wsdl");
            wscr.setServletLink(j2wc.servletLink);
         }
         else
         {
            wscr.setMappingFile("META-INF/" + j2wc.mappingFileName);
            wscr.setWsdlFile("META-INF/wsdl/" + j2wc.serviceName + ".wsdl");
            wscr.setEjbLink(j2wc.ejbLink);
         }
         wscr.setAppend(j2wc.wsxmlFileAppend);
         wscr.generateWSXMLDescriptor(new File(outDir + "/webservices.xml"));
      }
   }

   private void createWrapperTypes(JavaToWSDLConfig j2wc, String outDir, UnifiedMetaData wsMetaData, JavaWsdlMapping mapping, Class endpointClass) throws IOException
   {
      Map<QName, JavaXmlTypeMapping> index = indexMappingTypes(mapping);

      EndpointMetaData epMetaData = null;
      for (ServiceMetaData service : wsMetaData.getServices())
      {
         epMetaData = service.getEndpointByServiceEndpointInterface(j2wc.endpointName);
         if (epMetaData != null)
            break;
      }

      if (epMetaData == null)
         throw new WSException("Could not find endpoint in metadata: " + j2wc.endpointName);

      String packageName = endpointClass.getPackage().getName();
      ClassLoader classLoader = wsMetaData.getClassLoader();

      for (OperationMetaData opMetaData : epMetaData.getOperations())
      {
         if (opMetaData.isDocumentWrapped())
         {
            for (ParameterMetaData parameter : opMetaData.getParameters())
            {
               String name = endpointClass.getSimpleName() + "_" + opMetaData.getQName().getLocalPart() + "_RequestStruct";
               createWrapperType(parameter, name, packageName, index, classLoader, outDir);
            }

            ParameterMetaData returnParameter = opMetaData.getReturnParameter();
            if (returnParameter != null)
            {
               String name = endpointClass.getSimpleName() + "_" + opMetaData.getQName().getLocalPart() + "_ResponseStruct";
               createWrapperType(returnParameter, name, packageName, index, classLoader, outDir);
            }
         }
      }
   }

   private void createWrapperType(ParameterMetaData parameter, String name, String packageName, Map<QName, JavaXmlTypeMapping> mappingIndex, ClassLoader classLoader,
                                  String outDir) throws IOException
   {
      List<WrappedParameter> wrappedParameters = parameter.getWrappedParameters();

      if (wrappedParameters == null)
         return;

      List<VAR> vars = new ArrayList<VAR>();
      for (WrappedParameter wrapped : wrappedParameters)
      {
         String typeName = JavaUtils.convertJVMNameToSourceName(wrapped.getType(), classLoader);
         vars.add(new VAR(wrapped.getVariable(), typeName, false));
      }

      JavaWriter writer = new JavaWriter();
      writer.createJavaFile(new File(outDir), name + ".java", packageName, vars, null, null, false, false, null);

      JavaXmlTypeMapping type = mappingIndex.get(parameter.getXmlType());
      if (type == null)
         throw new WSException("JAX-RPC mapping metadata is missing a wrapper type: " + parameter.getXmlType());

      type.setJavaType(packageName + "." + name);
   }

   private Map<QName, JavaXmlTypeMapping> indexMappingTypes(JavaWsdlMapping mapping)
   {
      Map<QName, JavaXmlTypeMapping> index = new HashMap<QName, JavaXmlTypeMapping>();
      for (JavaXmlTypeMapping type : mapping.getJavaXmlTypeMappings())
      {
         QName qname = type.getRootTypeQName();
         if (qname == null)
            continue;

         index.put(qname, type);
      }

      return index;
   }

   /**
    * Client Side Generation [WSDL To Java]
    *
    * @param config
    * @param outDir
    */
   public void handleWSDLToJavaGeneration(Configuration config, String outDir)
   {
      WSDLToJavaConfig w2jc = config.getWSDLToJavaConfig(false);
      GlobalConfig glc = config.getGlobalConfig(false);

      WSDLToJava wsdlToJava = new WSDLToJava();
      wsdlToJava.setTypeMapping(new LiteralTypeMapping());
      wsdlToJava.setGenerateSerializableTypes(w2jc.serializableTypes);

      WSDLDefinitions wsdl = null;
      try
      {
         URL wsdlURL = null;
         try
         {
            wsdlURL = new URL(w2jc.wsdlLocation);
         }
         catch (MalformedURLException e)
         {
            // ignore
         }

         if (wsdlURL == null)
         {
            File wsdlFile = new File(w2jc.wsdlLocation);
            if (wsdlFile.exists())
            {
               wsdlURL = wsdlFile.toURL();
            }
         }

         if (wsdlURL == null)
         {
            ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
            wsdlURL = ctxLoader.getResource(w2jc.wsdlLocation);
         }

         if (wsdlURL == null)
            throw new IllegalArgumentException("Cannot load wsdl: " + w2jc.wsdlLocation);

         wsdl = wsdlToJava.convertWSDL2Java(wsdlURL);
         if (glc != null)
            wsdlToJava.setNamespacePackageMap(glc.packageNamespaceMap);

         wsdlToJava.setParameterStyle(w2jc.parameterStyle);
         wsdlToJava.generateSEI(wsdl, new File(outDir));

         //Generate the Service File
         this.generateServiceFile(getPackageName(wsdl, glc), wsdl, outDir);

         //Generate the Mapping File
         if (w2jc.mappingFileNeeded)
         {
            MappingFileGenerator mgf = new MappingFileGenerator(wsdl, new LiteralTypeMapping());
            if (glc != null && glc.packageNamespaceMap != null)
               mgf.setNamespacePackageMap(glc.packageNamespaceMap);        
            mgf.setServiceName(wsdl.getServices()[0].getName().getLocalPart());
            mgf.setParameterStyle(w2jc.parameterStyle);

            JavaWsdlMapping jwm = mgf.generate();
            Writer writer = IOUtils.getCharsetFileWriter(new File(outDir + "/" + w2jc.mappingFileName), Constants.DEFAULT_XML_CHARSET);
            writer.write(Constants.XML_HEADER);
            writer.write(DOMWriter.printNode(DOMUtils.parse(jwm.serialize()), true));
            writer.close();
         }

         //Generate the webservices.xml file
         if (w2jc.wsxmlFileNeeded)
         {
            String seiName = "mypackage.MyServiceEndpointInterface";
            String serviceName = "MyServiceName";

            if (wsdl.getServices().length == 1)
               serviceName = wsdl.getServices()[0].getName().getLocalPart();

            if (wsdl.getInterfaces().length == 1)
            {
               String seiPackage = getPackageName(wsdl, glc);
               seiName = seiPackage + "." + wsdlToJava.getServiceEndpointInterfaceName(wsdl.getInterfaces()[0]);
            }

            WebservicesXMLCreator wscr = new WebservicesXMLCreatorImpl();
            wscr.setTargetNamespace(wsdl.getTargetNamespace());
            wscr.setSeiName(seiName);
            wscr.setServiceName(serviceName);
            WSDLService wsdlService = wsdl.getService(serviceName);
            String portName = wsdlService.getEndpoints()[0].getName().getLocalPart();
            wscr.setPortName(portName);

            String wsdlShortName = wsdlURL.getPath();
            wsdlShortName = wsdlShortName.substring(wsdlShortName.lastIndexOf("/"));

            if (w2jc.servletLink != null)
            {
               wscr.setMappingFile("WEB-INF/" + w2jc.mappingFileName);
               wscr.setWsdlFile("WEB-INF/wsdl" + wsdlShortName);
               wscr.setServletLink(w2jc.servletLink);
            }
            else
            {
               wscr.setMappingFile("META-INF/" + w2jc.mappingFileName);
               wscr.setWsdlFile("META-INF/wsdl" + wsdlShortName);
               wscr.setEjbLink(w2jc.ejbLink);
            }
            wscr.generateWSXMLDescriptor(new File(outDir + "/webservices.xml"));
         }
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception ex)
      {
         throw new WSException(ex);
      }
   }

   //PRIVATE METHODS
   private Class loadClass(String cls)
   {
      Class clazz = null;
      try
      {
         clazz = Thread.currentThread().getContextClassLoader().loadClass(cls);
      }
      catch (ClassNotFoundException e)
      {
         log.error("Cannot load endpoint:" + e.getLocalizedMessage());
      }
      return clazz;
   }

   private void generateServiceFile(String packageName, WSDLDefinitions wsdl, String location) throws IOException
   {
      ServiceCreator sc = new ServiceCreator();
      sc.setPackageName(packageName);
      sc.setDirLocation(new File(location));
      sc.setWsdl(wsdl);
      sc.createServiceDescriptor();
   }

   private String getPackageName(WSDLDefinitions wsdl, GlobalConfig glc)
   {
      String targetNamespace = wsdl.getTargetNamespace();
      //Get it from global config if it is overriden
      if (glc != null && glc.packageNamespaceMap != null)
      {
         String pkg = glc.packageNamespaceMap.get(targetNamespace);
         if (pkg != null)
         {
            return pkg;
         }
      }
      return NamespacePackageMapping.getJavaPackageName(targetNamespace);
   }

   private void createDir(String path)
   {
      File file = new File(path);
      if (file.exists() == false)
         file.mkdirs();
   }
}
