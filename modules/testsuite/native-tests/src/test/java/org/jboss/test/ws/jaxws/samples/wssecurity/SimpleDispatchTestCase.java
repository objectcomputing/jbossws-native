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
package org.jboss.test.ws.jaxws.samples.wssecurity;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.Service.Mode;

import junit.framework.Test;

import org.jboss.ws.core.ConfigProvider;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.w3c.dom.Element;

/**
 * WS-Security with JAX-WS Dispatch
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1817
 *
 * @author Thomas.Diesler@jboss.com
 * @author alessio.soldano@jboss.com
 * @since 02-Oct-2007
 */
public class SimpleDispatchTestCase extends JBossWSTest
{
   private static Dispatch usernameDispatch;
   private static Dispatch encryptDispatch;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(SimpleDispatchTestCase.class, "jaxws-samples-wssecurity-username.war jaxws-samples-wssecurity-encrypt.war");
   }

   @Override
   protected void setUp() throws Exception
   {
      if (usernameDispatch == null)
      {
         URL wsdlURL = getResourceURL("jaxws/samples/wssecurity/simple-username/META-INF/wsdl/UsernameService.wsdl");
         URL securityURL = getResourceURL("jaxws/samples/wssecurity/simple-username/META-INF/jboss-wsse-client.xml");
         QName serviceName = new QName("http://org.jboss.ws/samples/wssecurity", "UsernameService");
         QName portName = new QName("http://org.jboss.ws/samples/wssecurity", "UsernameEndpointPort");

         Service service = Service.create(wsdlURL, serviceName);
         usernameDispatch = service.createDispatch(portName, Source.class, Mode.PAYLOAD);

         ((ConfigProvider)usernameDispatch).setSecurityConfig(securityURL.toExternalForm());
         ((ConfigProvider)usernameDispatch).setConfigName("Standard WSSecurity Client");
      }
      if (encryptDispatch == null)
      {
         URL wsdlURL = getResourceURL("wsprovide/jaxws/samples/wssecurity/HelloService.wsdl");
         URL securityURL = getResourceURL("jaxws/samples/wssecurity/simple-encrypt/META-INF/jboss-wsse-client.xml");
         QName serviceName = new QName("http://org.jboss.ws/samples/wssecurity", "HelloService");
         QName portName = new QName("http://org.jboss.ws/samples/wssecurity", "HelloPort");

         Service service = Service.create(wsdlURL, serviceName);
         encryptDispatch = service.createDispatch(portName, Source.class, Mode.PAYLOAD);

         ((ConfigProvider)encryptDispatch).setSecurityConfig(securityURL.toExternalForm());
         ((ConfigProvider)encryptDispatch).setConfigName("Standard WSSecurity Client");
         
         encryptDispatch.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
               "http://" + getServerHost() + ":8080/jaxws-samples-wssecurity-encrypt");
      }
   }

   public void testUsernameTokenNegative() throws Exception
   {
      try
      {
         String payload = "<ns1:getUsernameToken xmlns:ns1='http://org.jboss.ws/samples/wssecurity'/>";
         usernameDispatch.invoke(new StreamSource(new StringReader(payload)));
         fail("Server should respond with [401] - Unauthorized");
      }
      catch (Exception ex)
      {
         // this should be ok
      }
   }

   public void testUsernameToken() throws Exception
   {
      Map<String, Object> reqContext = usernameDispatch.getRequestContext();
      reqContext.put(BindingProvider.USERNAME_PROPERTY, "kermit");
      reqContext.put(BindingProvider.PASSWORD_PROPERTY, "thefrog");

      String payload = "<ns1:getUsernameToken xmlns:ns1='http://org.jboss.ws/samples/wssecurity'/>";
      Source retObj = (Source)usernameDispatch.invoke(new StreamSource(new StringReader(payload)));
      
      Element docElement = DOMUtils.sourceToElement(retObj);
      Element retElement = DOMUtils.getFirstChildElement(docElement);
      String retPayload = DOMWriter.printNode(retElement, false);
      assertEquals("<return>kermit</return>", retPayload);
   }
   
   public void testEncrypt() throws Exception
   {
      String payload = "<ns1:echoUserType xmlns:ns1='http://org.jboss.ws/samples/wssecurity'><user><msg>Kermit</msg></user></ns1:echoUserType>";
      Source retObj = (Source)encryptDispatch.invoke(new StreamSource(new StringReader(payload)));
      
      Element docElement = DOMUtils.sourceToElement(retObj);
      Element retElement = DOMUtils.getFirstChildElement(docElement);
      String retPayload = DOMWriter.printNode(retElement, false);
      assertEquals("<return><msg>Kermit</msg></return>", retPayload);
   }
}
