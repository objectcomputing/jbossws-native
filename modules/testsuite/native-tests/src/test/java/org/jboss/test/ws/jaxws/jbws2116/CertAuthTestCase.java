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
package org.jboss.test.ws.jaxws.jbws2116;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.jboss.ws.core.StubExt;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test case for certificate authentication & authorization with WS-Security
 * http://jira.jboss.org/jira/browse/JBWS-2116
 *
 * @author alessio.soldano@jboss.com
 * @since 24-May-2008
 */
public class CertAuthTestCase extends JBossWSTest
{
   private String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxws-jbws2116";
   private String keyStore;
   private String trustStore;
   private String keyStorePassword;
   private String trustStorePassword;
   private String keyStoreType;
   private String trustStoreType;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(CertAuthTestCase.class, "jaxws-jbws2116.sar jaxws-jbws2116.jar");
   }
   
   protected void setUp() throws Exception
   {
      super.setUp();
      //Backup values
      keyStore = System.getProperty("org.jboss.ws.wsse.keyStore");
      keyStorePassword = System.getProperty("org.jboss.ws.wsse.keyStorePassword");
      keyStoreType = System.getProperty("org.jboss.ws.wsse.keyStoreType");
      trustStore = System.getProperty("org.jboss.ws.wsse.trustStore");
      trustStorePassword = System.getProperty("org.jboss.ws.wsse.trustStorePassword");
      trustStoreType = System.getProperty("org.jboss.ws.wsse.trustStoreType");
      
   }
   
   protected void tearDown() throws Exception
   {
      //Restore environment
      System.setProperty("org.jboss.ws.wsse.keyStore", keyStore);
      System.setProperty("org.jboss.ws.wsse.trustStore", trustStore);
      System.setProperty("org.jboss.ws.wsse.keyStorePassword", keyStorePassword);
      System.setProperty("org.jboss.ws.wsse.trustStorePassword", trustStorePassword);
      System.setProperty("org.jboss.ws.wsse.keyStoreType", keyStoreType);
      System.setProperty("org.jboss.ws.wsse.trustStoreType", trustStoreType);
      super.tearDown();
   }
   
   public void testAuthAlice() throws Exception
   {
      setEnvironment("alice");
      Hello port = getPort();
      String msg = "Hi!";
      try
      {
         String result = port.echo(msg);
         assertEquals(msg, result);
         result = port.echo2(msg);
         assertEquals(msg, result);
      }
      catch (Exception e)
      {
         fail();
      }
   }
   
   public void testAuthJohn() throws Exception
   {
      setEnvironment("john");
      Hello port = getPort();
      String msg = "Hi!";
      try
      {
         String result = port.echo(msg);
         assertEquals(msg, result);
      }
      catch (Exception e)
      {
         fail();
      }
      try
      {
         port.echo2(msg);
         fail("John shouldn't be allowed to run this method!");
      }
      catch (Exception e)
      {
         //OK
      }
   }
   
   private void setEnvironment(String name)
   {
      //Setup values
      System.setProperty("org.jboss.ws.wsse.keyStore", getResourceFile("jaxws/jbws2116/" + name + "-sign.jks").getPath());
      System.setProperty("org.jboss.ws.wsse.trustStore", getResourceFile("jaxws/jbws2116/wsse10.truststore").getPath());
      System.setProperty("org.jboss.ws.wsse.keyStorePassword", "password");
      System.setProperty("org.jboss.ws.wsse.trustStorePassword", "password");
      System.setProperty("org.jboss.ws.wsse.keyStoreType", "jks");
      System.setProperty("org.jboss.ws.wsse.trustStoreType", "jks");
   }

   private Hello getPort() throws Exception
   {
      URL wsdlURL = new URL(TARGET_ENDPOINT_ADDRESS + "?wsdl");
      QName serviceName = new QName("http://org.jboss.ws/jbws2116", "HelloService");
      Hello port = Service.create(wsdlURL, serviceName).getPort(Hello.class);
      URL securityURL = getResourceURL("jaxws/jbws2116/META-INF/jboss-wsse-client.xml");
      ((StubExt)port).setSecurityConfig(securityURL.toExternalForm());
      ((StubExt)port).setConfigName("Standard WSSecurity Client");
      ((BindingProvider)port).getRequestContext().put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
      return port;
   }
}
