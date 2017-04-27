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
package org.jboss.test.ws.jaxws.jbws1814;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.jboss.ws.core.StubExt;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Dynamic Encryption based on clients input
 *
 * @author alessio.soldano@jboss.com
 * @since 19-02-2008
 */
public class SimpleEncryptTestCase extends JBossWSTest
{
   private String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxws-jbws1814";
   
   private String keyStore;
   private String trustStore;
   private String keyStorePassword;
   private String trustStorePassword;
   private String keyStoreType;
   private String trustStoreType;
   
   
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(SimpleEncryptTestCase.class, "jaxws-jbws1814.war");
   }

   public void testAlice() throws Exception
   {
      try
      {
         setEnvironment("alice");
         Hello hello = getPort();
         String result = hello.echo("alice");
         assertEquals("alice", result);
      }
      finally
      {
         restoreEnvironment();
      }
   }
   
   public void testJohn() throws Exception
   {
      try
      {
         setEnvironment("john");
         Hello hello = getPort();
         String result = hello.echo("john");
         assertEquals("john", result);
      }
      finally
      {
         restoreEnvironment();
      }
   }
   
   private Hello getPort() throws Exception
   {
      URL wsdlURL = new URL(TARGET_ENDPOINT_ADDRESS + "?wsdl");
      QName serviceName = new QName("http://org.jboss.ws/jbws1814", "HelloService");
      Hello port = Service.create(wsdlURL, serviceName).getPort(Hello.class);
      URL securityURL = getResourceURL("jaxws/jbws1814/META-INF/jboss-wsse-client.xml");
      ((StubExt)port).setSecurityConfig(securityURL.toExternalForm());
      ((StubExt)port).setConfigName("Standard WSSecurity Client");
      return port;
   }

   private void setEnvironment(String client)
   {
      //Backup values
      keyStore = System.getProperty("org.jboss.ws.wsse.keyStore");
      keyStorePassword = System.getProperty("org.jboss.ws.wsse.keyStorePassword");
      keyStoreType = System.getProperty("org.jboss.ws.wsse.keyStoreType");
      trustStore = System.getProperty("org.jboss.ws.wsse.trustStore");
      trustStorePassword = System.getProperty("org.jboss.ws.wsse.trustStorePassword");
      trustStoreType = System.getProperty("org.jboss.ws.wsse.trustStoreType");
      //Set values
      System.setProperty("org.jboss.ws.wsse.keyStore", getResourceFile("jaxws/jbws1814/META-INF/" + client + "-sign_enc.jks").getPath());
      System.setProperty("org.jboss.ws.wsse.trustStore", getResourceFile("jaxws/jbws1814/META-INF/wsse10.truststore").getPath());
      System.setProperty("org.jboss.ws.wsse.keyStorePassword", "password");
      System.setProperty("org.jboss.ws.wsse.trustStorePassword", "password");
      System.setProperty("org.jboss.ws.wsse.keyStoreType", "jks");
      System.setProperty("org.jboss.ws.wsse.trustStoreType", "jks");
   }
   
   private void restoreEnvironment()
   {
      System.setProperty("org.jboss.ws.wsse.keyStore", keyStore);
      System.setProperty("org.jboss.ws.wsse.trustStore", trustStore);
      System.setProperty("org.jboss.ws.wsse.keyStorePassword", keyStorePassword);
      System.setProperty("org.jboss.ws.wsse.trustStorePassword", trustStorePassword);
      System.setProperty("org.jboss.ws.wsse.keyStoreType", keyStoreType);
      System.setProperty("org.jboss.ws.wsse.trustStoreType", trustStoreType);
   }  
}
