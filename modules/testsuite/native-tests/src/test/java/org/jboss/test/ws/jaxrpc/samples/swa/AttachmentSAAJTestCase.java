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
package org.jboss.test.ws.jaxrpc.samples.swa;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
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

import org.jboss.ws.core.soap.SOAPBodyElementRpc;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Test SOAP with Attachements (SwA) through the SAAJ layer.
 * 
 * This tests the server side handling of Attachments.
 * The JAXRPC client is not tested.
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 * @since Nov 16, 2004
 */
public class AttachmentSAAJTestCase extends JBossWSTest
{
   private final String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxrpc-samples-swa";
   private static final String NS_PREFIX = "ns1";
   private static final String TARGET_NAMESPACE = "http://org.jboss.ws/samples/swa";
   private static final String CID_MIMEPART = "<mimepart=3234287987979.24237@jboss.org>";

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(AttachmentSAAJTestCase.class, "jaxrpc-samples-swa.war");
   }
   
   /** Send a multipart message with a text/plain attachment part
    */
   public void testSendMimeImageGIF() throws Exception
   {
      String rpcMethodName = "sendMimeImageGIF";
      SOAPMessage msg = setupMimeMessage(rpcMethodName);

      URL url = getResourceURL("jaxrpc/samples/swa/attach.gif");

      // On Linux the X11 server must be installed properly to create images successfully.
      // If the image cannot be created in the test VM, we assume it cannot be done on the
      // server either, so we just skip the test
      Image image = null;
      try
      {
         image = Toolkit.getDefaultToolkit().createImage(url);
      }
      catch (Throwable th)
      {
         //log.warn("Cannot create Image: " + th);
      }

      if (image != null)
      {
         addAttachmentPart(msg, "image/gif", new DataHandler(url));
         //msg.writeTo(new FileOutputStream(rpcMethodName + "_RequestMessage.txt"));
         sendAndValidateMimeMessage(rpcMethodName, msg);
      }
   }

   /** Send a multipart message with a text/plain attachment part
    */
   public void testSendMimeImageJPEG() throws Exception
   {
      String rpcMethodName = "sendMimeImageJPEG";
      SOAPMessage msg = setupMimeMessage(rpcMethodName);

      URL url = getResourceURL("jaxrpc/samples/swa/attach.jpeg");

      // On Linux the X11 server must be installed properly to create images successfully.
      // If the image cannot be created in the test VM, we assume it cannot be done on the
      // server either, so we just skip the test
      Image image = null;
      try
      {
         image = Toolkit.getDefaultToolkit().createImage(url);
      }
      catch (Throwable th)
      {
         //log.warn("Cannot create Image: " + th);
      }

      if (image != null)
      {
         addAttachmentPart(msg, "image/jpeg", new DataHandler(url));
         //msg.writeTo(new FileOutputStream(rpcMethodName + "_RequestMessage.txt"));
         sendAndValidateMimeMessage(rpcMethodName, msg);
      }
   }

   /** Send a multipart message with a text/plain attachment part
    */
   public void testSendMimeTextPlain() throws Exception
   {
      String rpcMethodName = "sendMimeTextPlain";
      SOAPMessage msg = setupMimeMessage(rpcMethodName);

      URL url = getResourceURL("jaxrpc/samples/swa/attach.txt");
      addAttachmentPart(msg, "text/plain", new DataHandler(url));

      //msg.writeTo(new FileOutputStream(rpcMethodName + "_RequestMessage.txt"));

      sendAndValidateMimeMessage(rpcMethodName, msg);
   }

   /** Send a multipart message with a text/plain attachment part
    */
   public void testSendMimeMultipart() throws Exception
   {
      String rpcMethodName = "sendMimeMultipart";
      SOAPMessage msg = setupMimeMessage(rpcMethodName);

      URL url = getResourceURL("jaxrpc/samples/swa/attach.txt");
      MimeMultipart multipart = new MimeMultipart("mixed");
      MimeBodyPart bodyPart = new MimeBodyPart();
      bodyPart.setDataHandler(new DataHandler(url));
      String bpct = bodyPart.getContentType();
      bodyPart.setHeader("Content-Type", bpct);

      File file = new File(rpcMethodName + "_Multipart.txt");
      file.deleteOnExit();
      multipart.addBodyPart(bodyPart);
      multipart.writeTo(new FileOutputStream(file));

      String contentType = multipart.getContentType();
      AttachmentPart ap = msg.createAttachmentPart(multipart, contentType);
      ap.setContentId(CID_MIMEPART);
      msg.addAttachmentPart(ap);

      //msg.writeTo(new FileOutputStream(rpcMethodName + "_RequestMessage.txt"));

      sendAndValidateMimeMessage(rpcMethodName, msg);
   }

   /** Send a multipart message with a text/plain attachment part
    */
   public void testSendMimeTextXML() throws Exception
   {
      String rpcMethodName = "sendMimeTextXML";
      SOAPMessage msg = setupMimeMessage(rpcMethodName);

      URL url = getResourceURL("jaxrpc/samples/swa/attach.xml");
      addAttachmentPart(msg, "text/xml", new DataHandler(url));

      //msg.writeTo(new FileOutputStream(rpcMethodName + "_RequestMessage.txt"));
      sendAndValidateMimeMessage(rpcMethodName, msg);
   }

   /** Send a multipart message with a text/plain attachment part
    */
   public void testSendMimeApplicationXML() throws Exception
   {
      String rpcMethodName = "sendMimeApplicationXML";
      SOAPMessage msg = setupMimeMessage(rpcMethodName);

      URL url = getResourceURL("jaxrpc/samples/swa/attach.xml");
      addAttachmentPart(msg, "application/xml", new DataHandler(url));

      //msg.writeTo(new FileOutputStream(rpcMethodName + "_RequestMessage.txt"));
      sendAndValidateMimeMessage(rpcMethodName, msg);
   }

   /** Setup the multipart/related MIME message
    */
   private SOAPMessage setupMimeMessage(String rpcMethodName)
           throws Exception
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
      SOAPElement sbe = bdy.addChildElement(new SOAPBodyElementRpc(envelope.createName(rpcMethodName, NS_PREFIX, TARGET_NAMESPACE)));

      // Add a some child elements
      sbe.addChildElement(envelope.createName("message")).addTextNode("Some text message");
      return msg;
   }

   private void addAttachmentPart(SOAPMessage msg, String contentType, DataHandler dataHandler)
   {
      // Create the attachment part
      AttachmentPart ap = msg.createAttachmentPart(dataHandler);
      ap.setContentType(contentType);
      ap.setContentId(CID_MIMEPART);

      // Add the attachments to the message.
      msg.addAttachmentPart(ap);
   }

   /** Send the message and validate the result
    */
   private void sendAndValidateMimeMessage(String rpcMethodName, SOAPMessage msg)
           throws SOAPException, MalformedURLException, IOException
   {
      SOAPConnectionFactory conFactory = SOAPConnectionFactory.newInstance();
      SOAPConnection con = conFactory.createConnection();
      //msg.writeTo(System.out);
      SOAPMessage resMessage = con.call(msg, new URL(TARGET_ENDPOINT_ADDRESS));
      SOAPBody soapBody = resMessage.getSOAPBody();
      SOAPEnvelope soapEnvelope = (SOAPEnvelope)soapBody.getParentElement();

      Name rpcName = soapEnvelope.createName(rpcMethodName + "Response", NS_PREFIX, TARGET_NAMESPACE);
      Iterator childElements = soapBody.getChildElements(rpcName);
      assertTrue("Expexted child: " + rpcName, childElements.hasNext());

      SOAPElement bodyChild = (SOAPElement)childElements.next();
      Name resName = soapEnvelope.createName("result");
      SOAPElement resElement = (SOAPElement)bodyChild.getChildElements(resName).next();
      String value = resElement.getValue();

      assertEquals("[pass]", value);
   }
}
