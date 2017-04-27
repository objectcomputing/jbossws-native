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
package org.jboss.test.ws.interop.nov2007.wsse;

import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.ws.core.StubExt;
import org.jboss.ws.extensions.security.operation.EncryptionOperation;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * WCF Interoperability Plug-fest - November 2007
 * 
 * Scenario 3.3: X509 Mutual Authentication, Sign Then Encrypt
 * 
 * Client and Server are authenticated and messages integrity and confidentiality are provided by using
 * Asymmetric Binding from Security Policy with server X509 certificate used as Recipient Token and
 * client X509 certificate used as Initiator Token. SignedParts and EncryptedParts assertions are
 * present in the corresponding policy, indicating that the Body of the message must be signed and
 * encrypted. Protection Order property of the binding is set to SignBeforeEncrypt.
 * 
 * SOAP Version:        1.1
 * Addressing:          No
 * Client Certificate:  Alice
 * Server Certificate:  Bob
 * Timestamp:           Yes
 * Protection Order:    Sign then Encrypt
 * Signed parts:        Timestamp, Body
 * Encrypted parts:     Body
 * Key Wrap:            RSA-OAEP
 * Encryption:          AES256
 * Canonicalization:    XML-EXC-C14N
 * Signature:           SHA1
 * 
 * 
 * @author Alessio Soldano <alessio.soldano@jboss.com>
 * 
 * @since 29-Oct-2007
 */
public class EncryptTestCase extends AbstractWSSEBase
{
   public static Test suite()
   {
      return new JBossWSTestSetup
      (
         EncryptTestCase.class,
         "jbossws-interop-nov2007-wsseEncrypt.war, jbossws-interop-nov2007-wsseEncrypt-client.jar"
      );
   }
   
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
   }
   
   @Override
   protected void tearDown() throws Exception
   {
      super.tearDown();
   }

   public void testEcho() throws Exception
   {
      if (!EncryptionOperation.probeUnlimitedCrypto())
      {
         System.out.println("Please install the Unlimited Strength Java(TM) Cryptography Extension to run this test; " +
               "please note that your country might have limits on allowed crypto strength.\n Test skipped.");
         return;
      }
      super.testEcho();
   }
   
   @SuppressWarnings("unchecked")
   public void testEchoDataSet() throws Exception
   {
      if (!EncryptionOperation.probeUnlimitedCrypto())
      {
         System.out.println("Please install the Unlimited Strength Java(TM) Cryptography Extension to run this test; " +
               "please note that your country might have limits on allowed crypto strength.\n Test skipped.");
         return;
      }
      super.testEchoDataSet();
   }
   
   public void testFault() throws Exception
   {
      if (!EncryptionOperation.probeUnlimitedCrypto())
      {
         System.out.println("Please install the Unlimited Strength Java(TM) Cryptography Extension to run this test; " +
               "please note that your country might have limits on allowed crypto strength.\n Test skipped.");
         return;
      }
      super.testFault();
   }
   
   public void testHeader() throws Exception
   {
      if (!EncryptionOperation.probeUnlimitedCrypto())
      {
         System.out.println("Please install the Unlimited Strength Java(TM) Cryptography Extension to run this test; " +
               "please note that your country might have limits on allowed crypto strength.\n Test skipped.");
         return;
      }
      super.testHeader();
   }
   
   @SuppressWarnings("unchecked")
   public void testEchoXml() throws Exception
   {
      if (!EncryptionOperation.probeUnlimitedCrypto())
      {
         System.out.println("Please install the Unlimited Strength Java(TM) Cryptography Extension to run this test; " +
               "please note that your country might have limits on allowed crypto strength.\n Test skipped.");
         return;
      }
      super.testEchoXml();
   }
   
   public void testPing() throws Exception
   {
      if (!EncryptionOperation.probeUnlimitedCrypto())
      {
         System.out.println("Please install the Unlimited Strength Java(TM) Cryptography Extension to run this test; " +
               "please note that your country might have limits on allowed crypto strength.\n Test skipped.");
         return;
      }
      super.testPing();
   }

   @Override
   protected void scenarioSetup(IPingService port)
   {
      ((StubExt)port).setConfigName("Standard WSSecurity Client");
      
      System.setProperty("org.jboss.ws.wsse.keyStore", getResourceFile("interop/nov2007/wsse/shared/META-INF/alice-sign_enc.jks").getPath());
      System.setProperty("org.jboss.ws.wsse.trustStore", getResourceFile("interop/nov2007/wsse/shared/META-INF/wsse10.truststore").getPath());
      System.setProperty("org.jboss.ws.wsse.keyStorePassword", "password");
      System.setProperty("org.jboss.ws.wsse.trustStorePassword", "password");
      System.setProperty("org.jboss.ws.wsse.keyStoreType", "jks");
      System.setProperty("org.jboss.ws.wsse.trustStoreType", "jks");
   }

   @Override
   protected QName getScenarioPortQName()
   {
      return new QName("http://InteropBaseAddress/interop", "MutualCertificate10SignEncrypt_IPingService");
   }

}
