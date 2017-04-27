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
package org.jboss.test.ws.tools.metadata;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;

import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMappingFactory;
import org.jboss.ws.metadata.jaxrpcmapping.MethodParamPartsMapping;
import org.jboss.ws.metadata.jaxrpcmapping.PackageMapping;
import org.jboss.ws.metadata.jaxrpcmapping.PortMapping;
import org.jboss.ws.metadata.jaxrpcmapping.ServiceEndpointInterfaceMapping;
import org.jboss.ws.metadata.jaxrpcmapping.ServiceEndpointMethodMapping;
import org.jboss.ws.metadata.jaxrpcmapping.ServiceInterfaceMapping;
import org.jboss.ws.metadata.jaxrpcmapping.WsdlMessageMapping;
import org.jboss.ws.metadata.jaxrpcmapping.WsdlReturnValueMapping;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/**
 *  Test the construction/serialization of jaxrpc-mapping metadata
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 29, 2005
 */
public class JavaWsdlMappingMetaDataTestCase extends JBossWSTest
{
   
   public void testJavaWsdlMappingMetaDataRead() throws Exception
   {
      URL jwmURL = getResourceURL("tools/jbws-161/wscompile/simple/mapping/jaxrpc-mapping.xml"); 
       
      JavaWsdlMappingFactory mappingFactory = JavaWsdlMappingFactory.newInstance();
      JavaWsdlMapping javaWsdlMapping = mappingFactory.parse(jwmURL); 
      assertNotNull("MappingMetaData is null?",javaWsdlMapping);
   }
   
   public void testJavaWsdlMappingMetaDataWrite() throws Exception
   {
      URL jwmURL = getResourceURL("tools/jbws-161/wscompile/simple/mapping/jaxrpc-mapping.xml"); 
    
      JavaWsdlMapping javaWsdlMapping = constructMappingMetaData();
      assertNotNull("MappingMetaData is null?",javaWsdlMapping);
      String wmdata = javaWsdlMapping.serialize();  
      Element exp = DOMUtils.parse(jwmURL.openStream());
      Element act = DOMUtils.parse(wmdata);
      assertEquals(exp,act); 
   }
   
   //PRIVATE METHODS
   private JavaWsdlMapping constructMappingMetaData()
   {
      JavaWsdlMapping jwm =  new JavaWsdlMapping();
      //Construct package mapping
      jwm.addPackageMapping(constructPackageMapping(jwm,
         "org.jboss.test.ws.tools.jbws_161.simple",  "http://org.jboss/types"));
      jwm.addPackageMapping(constructPackageMapping(jwm,
            "org.jboss.test.ws.tools.jbws_161.simple",  "http://org.jboss/types"));
      jwm.addServiceInterfaceMappings(constructServiceInterfaceMapping(jwm));
      jwm.addServiceEndpointInterfaceMappings(constructServiceEndpointInterfaceMapping(jwm));
      return jwm;
   }
   
   private PackageMapping constructPackageMapping(JavaWsdlMapping jwm,
         String packageType, String ns)
   {
      PackageMapping pk = new PackageMapping(jwm);
      pk.setPackageType(packageType);
      pk.setNamespaceURI(ns);
      return pk;
   }
   
   private ServiceInterfaceMapping constructServiceInterfaceMapping(JavaWsdlMapping jwm)
   {
      ServiceInterfaceMapping sim = new ServiceInterfaceMapping(jwm);
      sim.setServiceInterface("org.jboss.test.ws.tools.jbws_161.simple.HelloWsService");
      sim.setWsdlServiceName(new QName("http://org.jboss/types", "HelloWsService", "serviceNS") );
      //port mapping
      PortMapping pm = new PortMapping(sim);
      pm.setPortName("HelloWsPort");
      pm.setJavaPortName("HelloWsPort");
      sim.addPortMapping(pm);
      return sim;
   }
   
   private ServiceEndpointInterfaceMapping constructServiceEndpointInterfaceMapping(JavaWsdlMapping jwm)
   {
      ServiceEndpointInterfaceMapping seim = new ServiceEndpointInterfaceMapping(jwm);
      seim.setServiceEndpointInterface("org.jboss.test.ws.tools.jbws_161.simple.HelloWs");
      seim.setWsdlPortType(new QName("http://org.jboss/types","HelloWs","portTypeNS"));
      seim.setWsdlBinding(new QName("http://org.jboss/types","HelloWsBinding","bindingNS"));
      seim.addServiceEndpointMethodMapping(constructServiceEndpointMethodMapping(seim));
      return seim; 
   }
   
   private ServiceEndpointMethodMapping constructServiceEndpointMethodMapping(ServiceEndpointInterfaceMapping seim)
   {
      ServiceEndpointMethodMapping semm = new ServiceEndpointMethodMapping(seim);
      semm.setJavaMethodName("sayHello");
      semm.setWsdlOperation("sayHello");
      semm.addMethodParamPartsMapping(constructMethodParamPartsMapping(semm));
      semm.setWsdlReturnValueMapping(constructWsdlReturnValueMapping(semm));
      return semm;
   }
   
   private MethodParamPartsMapping constructMethodParamPartsMapping(ServiceEndpointMethodMapping semm)
   {
      MethodParamPartsMapping mppm = new MethodParamPartsMapping(semm);
      mppm.setParamPosition(0);
      mppm.setParamType("java.lang.String");
      mppm.setWsdlMessageMapping(constructWsdlMessageMapping(mppm));
      return mppm;
   }
   
   private WsdlMessageMapping constructWsdlMessageMapping(MethodParamPartsMapping mppm)
   {
      WsdlMessageMapping wmm = new WsdlMessageMapping(mppm);
      wmm.setWsdlMessage(new QName("http://org.jboss/types","HelloWs_sayHello","wsdlMsgNS"));
      wmm.setWsdlMessagePartName("String_1");
      wmm.setParameterMode("IN");
      return wmm;
   }
   
   private WsdlReturnValueMapping constructWsdlReturnValueMapping(ServiceEndpointMethodMapping semm)
   {
      WsdlReturnValueMapping wrvm = new WsdlReturnValueMapping(semm);
      wrvm.setMethodReturnValue("java.lang.String");
      wrvm.setWsdlMessage(new QName("http://org.jboss/types","HelloWs_sayHelloResponse","wsdlMsgNS"));
      wrvm.setWsdlMessagePartName("result"); 
      return wrvm;
   }
}
