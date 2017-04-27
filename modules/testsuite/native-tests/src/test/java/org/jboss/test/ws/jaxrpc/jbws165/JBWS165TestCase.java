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
package org.jboss.test.ws.jaxrpc.jbws165;

import java.io.File;
import java.net.URL;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.Stub;

import junit.framework.Test;

import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;
import org.jboss.ws.core.jaxrpc.client.ServiceImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;



/**
 * Tests <context-root> and <port-component-root> elements in EJB endpoints
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 10-Jun-2005
 */
public class JBWS165TestCase extends JBossWSTest
{
   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS165TestCase.class, "jaxrpc-jbws165.ear");
   }

   public void testNone() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jaxrpc-jbws165-jaxrpc-jbws165-none/HelloNone?wsdl");
      URL mappingURL = getResourceURL("jaxrpc/jbws165/META-INF/jaxrpc-mapping.xml");
      QName serviceName = new QName("http://org.jboss.test.webservice/jbws165", "HelloServiceNone");
      Service service = new ServiceFactoryImpl().createService(wsdlURL, serviceName, mappingURL);
      Hello port = (Hello) service.getPort(Hello.class);
      String retObj = port.hello(getName());
      assertEquals(getName(), retObj);
   }
   
   public void testPortComponentURIOne() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/Explicit/Path/HelloPCOne?wsdl");
      
      URL mappingURL = getResourceURL("jaxrpc/jbws165/META-INF/jaxrpc-mapping.xml");
      QName serviceName = new QName("http://org.jboss.test.webservice/jbws165", "HelloServicePcURI");
      Service service = new ServiceFactoryImpl().createService(wsdlURL, serviceName, mappingURL);
      QName portName = new QName("http://org.jboss.test.webservice/jbws165", "HelloPortOne");
      Hello port = (Hello) service.getPort(portName, Hello.class);
      String retObj = port.hello(getName());
      assertEquals(getName(), retObj);
   }
   
   public void testPortComponentURITwo() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/Explicit/Path/HelloPCTwo?wsdl");
      
      URL mappingURL = getResourceURL("jaxrpc/jbws165/META-INF/jaxrpc-mapping.xml");
      QName serviceName = new QName("http://org.jboss.test.webservice/jbws165", "HelloServicePcURI");
      Service service = new ServiceFactoryImpl().createService(wsdlURL, serviceName, mappingURL);
      QName portName = new QName("http://org.jboss.test.webservice/jbws165", "HelloPortTwo");
      Hello port = (Hello) service.getPort(portName, Hello.class);
      String retObj = port.hello(getName());
      assertEquals(getName(), retObj);
   }
   
   public void testContextRoot() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/Explicit/Context/HelloContextRoot?wsdl");
      URL mappingURL = getResourceURL("jaxrpc/jbws165/META-INF/jaxrpc-mapping.xml");
      QName serviceName = new QName("http://org.jboss.test.webservice/jbws165", "HelloServiceCtxRoot");
      Service service = new ServiceFactoryImpl().createService(wsdlURL, serviceName, mappingURL);
      Hello port = (Hello) service.getPort(Hello.class);
      String retObj = port.hello(getName());
      assertEquals(getName(), retObj);
   }
   
   public void testBoth() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/Explicit/Both/Explicit/Path?wsdl");
      URL mappingURL = getResourceURL("jaxrpc/jbws165/META-INF/jaxrpc-mapping.xml");
      QName serviceName = new QName("http://org.jboss.test.webservice/jbws165", "HelloServiceBoth");
      Service service = new ServiceFactoryImpl().createService(wsdlURL, serviceName, mappingURL);
      Hello port = (Hello) service.getPort(Hello.class);
      String retObj = port.hello(getName());
      assertEquals(getName(), retObj);
   }
}
