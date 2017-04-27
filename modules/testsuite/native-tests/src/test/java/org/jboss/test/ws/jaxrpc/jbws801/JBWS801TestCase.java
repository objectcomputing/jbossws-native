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
package org.jboss.test.ws.jaxrpc.jbws801;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

public class JBWS801TestCase extends JBossWSTest
{
   private static final String NS_PREFIX = "ns1";
   private static final String NS_URI = "http://org.jboss.webservice/attachment";
   private static final String CID_MIMEPART = "big";

   /** Deploy the test ear */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS801TestCase.class, "jaxrpc-jbws801.war");
   }

   public void testLargeFile() throws Exception
   {
      long size = 6L * 1024L * 1024L;

      String methodName = "sendLargeFile";
      SOAPMessage msg = setupMimeMessage(methodName);
      DataHandler handler = new DataHandler(new GeneratorDataSource(size));
      addAttachmentPart(msg, handler);
      sendAndValidateMimeMessage(methodName, msg, size);
   }

   private static class GeneratorDataSource implements DataSource
   {
      private long size;

      public GeneratorDataSource(long size)
      {
         this.size = size;
      }

      public String getContentType()
      {
         return "application/octet-stream";
      }

      public InputStream getInputStream() throws IOException
      {
         return new FakeInputStream(size);
      }

      public String getName()
      {
         return null;
      }

      public OutputStream getOutputStream() throws IOException
      {
         return null;
      }
   }

   private static class FakeInputStream extends InputStream
   {
      private long size;

      public FakeInputStream(long size)
      {
         this.size = size;
      }

      public int read(byte[] b, int off, int len) throws IOException
      {
         if (len < 1)
            return 0;

         if (size == 0)
            return -1;

         int ret = (int)Math.min(size, len);
         Arrays.fill(b, off, off + ret, (byte)1);
         size -= ret;

         return ret;
      }

      public int read() throws IOException
      {
         if (size > 0)
         {
            size--;
            return 1;
         }

         return -1;
      }
   }

   private SOAPMessage setupMimeMessage(String rpcMethodName) throws Exception
   {
      MessageFactory mf = MessageFactory.newInstance();

      // Create a soap message from the message factory.
      SOAPMessage msg = mf.createMessage();

      // Message creation takes care of creating the SOAPPart - a required part of the message as per the SOAP 1.1 spec.
      SOAPPart sp = msg.getSOAPPart();

      // Retrieve the envelope from the soap part to start building the soap message.
      SOAPEnvelope envelope = sp.getEnvelope();

      // Create a soap body from the envelope.
      SOAPBody bdy = envelope.getBody();

      // Add a soap body element
      bdy.addBodyElement(envelope.createName(rpcMethodName, NS_PREFIX, NS_URI));

      return msg;
   }

   private void addAttachmentPart(SOAPMessage msg, DataHandler dataHandler)
   {
      // Create the attachment part
      AttachmentPart ap = msg.createAttachmentPart(dataHandler);
      ap.setContentId(CID_MIMEPART);

      // Add the attachments to the message.
      msg.addAttachmentPart(ap);
   }

   /** Send the message and validate the result
    */
   private void sendAndValidateMimeMessage(String rpcMethodName, SOAPMessage msg, long count) throws SOAPException, MalformedURLException
   {
      SOAPConnectionFactory conFactory = SOAPConnectionFactory.newInstance();
      SOAPConnection con = conFactory.createConnection();
      SOAPMessage resMessage = con.call(msg, new URL("http://" + getServerHost() + ":8080/jaxrpc-jbws801"));
      SOAPBody soapBody = resMessage.getSOAPBody();
      SOAPEnvelope soapEnvelope = (SOAPEnvelope)soapBody.getParentElement();

      Name rpcName = soapEnvelope.createName(rpcMethodName + "Response", NS_PREFIX, NS_URI);
      Iterator childElements = soapBody.getChildElements(rpcName);
      assertTrue("Expexted child: " + rpcName, childElements.hasNext());

      SOAPElement bodyChild = (SOAPElement)childElements.next();
      Name resName = soapEnvelope.createName("result");
      SOAPElement resElement = (SOAPElement)bodyChild.getChildElements(resName).next();
      String value = resElement.getValue();

      assertEquals(count, Long.parseLong(value));
   }
}
