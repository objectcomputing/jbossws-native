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
package org.jboss.test.ws.common.soap.attachment;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.Constants;
import org.jboss.ws.core.CommonBinding;
import org.jboss.ws.core.CommonBindingProvider;
import org.jboss.ws.core.CommonSOAPBinding;
import org.jboss.ws.core.EndpointInvocation;
import org.jboss.ws.core.jaxrpc.client.CallImpl;
import org.jboss.ws.core.jaxrpc.handler.SOAPMessageContextJAXRPC;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.core.soap.MessageFactoryImpl;
import org.jboss.ws.core.soap.SOAPMessageImpl;
import org.jboss.ws.core.soap.attachment.MimeConstants;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.EndpointMetaData.Type;
import org.jboss.wsf.test.JBossWSTest;

/**
 * TODO
 *
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 */
public class GenericAttachmentTestCase extends JBossWSTest
{
   public void testEncodingDecoding() throws Exception
   {
      MessageFactoryImpl factory = new MessageFactoryImpl();
      SOAPMessage msg1 = factory.createMessage();
      AttachmentPart attachment1 = msg1.createAttachmentPart();
      attachment1.setContent("this is a test", "text/plain; charset=UTF-8");
      attachment1.setContentId("<attachment1@test.ws.jboss.org>");
      msg1.addAttachmentPart(attachment1);

      BufferedImage img = new BufferedImage(2, 2, 1);
      img.setRGB(0, 0, 5);
      img.setRGB(0, 1, 6);
      img.setRGB(1, 0, 101);
      img.setRGB(1, 1, 102);
      AttachmentPart attachment2 = msg1.createAttachmentPart();
      attachment2.setContent(img, "image/png");
      attachment2.setContentId("<attachment2@test.ws.jboss.org>");
      msg1.addAttachmentPart(attachment2);

      if (msg1.saveRequired())
         msg1.saveChanges();

      String type = msg1.getMimeHeaders().getHeader(MimeConstants.CONTENT_TYPE)[0];
      File file = File.createTempFile("JBossWSAttachmentTest", ".dat");
      file.deleteOnExit();

      FileOutputStream os = new FileOutputStream(file);
      msg1.writeTo(os);
      os.flush();
      os.close();

      FileInputStream is = new FileInputStream(file);
      MimeHeaders headers = new MimeHeaders();
      headers.addHeader(MimeConstants.CONTENT_TYPE, type);

      SOAPMessage msg2 = new MessageFactoryImpl().createMessage(headers, is);

      // Verify SOAP body is the same
      SOAPEnvelope expEnv = msg1.getSOAPPart().getEnvelope();
      SOAPEnvelope wasEnv = msg2.getSOAPPart().getEnvelope();
      assertEquals(expEnv, wasEnv);

      Iterator i = msg2.getAttachments();
      while (i.hasNext())
      {
         AttachmentPart attachment = (AttachmentPart)i.next();

         if (attachment.getContentId().equals(attachment1.getContentId()))
         {
            String dest = (String)attachment.getContent();
            String source = (String)attachment1.getContent();
            assertEquals(source, dest);
         }
         else if (attachment.getContentId().equals(attachment2.getContentId()))
         {
            BufferedImage dest = (BufferedImage)attachment.getContent();
            BufferedImage source = (BufferedImage)attachment2.getContent();

            assertEquals(dest.getWidth(), source.getWidth());
            assertEquals(dest.getHeight(), source.getHeight());
            for (int x = 0; x < dest.getWidth(); x++)
               for (int y = 0; y < dest.getHeight(); y++)
                  assertEquals(dest.getRGB(x, y), source.getRGB(x, y));
         }
      }
   }

   public static void testDIIBinding() throws Exception
   {
      Service service = ServiceFactory.newInstance().createService(new QName("testService"));
      CallImpl call = (CallImpl)service.createCall();
      call.setOperationName(new QName("http://org.jboss.ws/2004", "testAttachment"));

      QName xmlType = Constants.TYPE_LITERAL_STRING;
      
      call.addParameter("String_1", xmlType, String.class, ParameterMode.IN);
      call.addParameter("foo", Constants.TYPE_MIME_TEXT_PLAIN, String.class, ParameterMode.IN);
      call.setReturnType(Constants.TYPE_MIME_TEXT_PLAIN, String.class);

      OperationMetaData opMetaData = call.getOperationMetaData();

      try
      {
         // Associate a message context with the current thread
         SOAPMessageContextJAXRPC messageContext = new SOAPMessageContextJAXRPC();
         MessageContextAssociation.pushMessageContext(messageContext);
         messageContext.setOperationMetaData(opMetaData);
   
         CommonBindingProvider bindingProvider = new CommonBindingProvider(CommonSOAPBinding.SOAP11HTTP_BINDING, Type.JAXRPC);
         CommonBinding binding = (CommonBinding)bindingProvider.getCommonBinding();
   
         EndpointInvocation epInv = new EndpointInvocation(opMetaData);
         epInv.initInputParams(new Object[]{"Hello World!", "hi"});
         
         SOAPMessage reqMessage = (SOAPMessage)binding.bindRequestMessage(opMetaData, epInv, null);
   
         ByteArrayOutputStream stream = new ByteArrayOutputStream();
   
         reqMessage.saveChanges();
         reqMessage.writeTo(stream);
   
         ByteArrayInputStream in = new ByteArrayInputStream(stream.toByteArray());
   
         String type = reqMessage.getMimeHeaders().getHeader(MimeConstants.CONTENT_TYPE)[0];
   
         MimeHeaders headers = new MimeHeaders();
         headers.addHeader(MimeConstants.CONTENT_TYPE, type);
         SOAPMessageImpl msg2 = (SOAPMessageImpl)new MessageFactoryImpl().createMessage(headers, in);
   
         epInv = binding.unbindRequestMessage(opMetaData, msg2);
   
         assertEquals(epInv.getRequestParamValue(new QName("String_1")).toString(), "Hello World!");
         assertEquals(epInv.getRequestParamValue(new QName("foo")).toString(), "hi");
   
         epInv.setReturnValue("test");
   
         SOAPMessage responseMessage = (SOAPMessage)binding.bindResponseMessage(opMetaData, epInv);
   
         stream = new ByteArrayOutputStream();
         responseMessage.writeTo(stream);
   
         in = new ByteArrayInputStream(stream.toByteArray());
   
         type = responseMessage.getMimeHeaders().getHeader(MimeConstants.CONTENT_TYPE)[0];
   
         headers = new MimeHeaders();
         headers.addHeader(MimeConstants.CONTENT_TYPE, type);
         SOAPMessageImpl msg3 = (SOAPMessageImpl)new MessageFactoryImpl().createMessage(headers, in);
   
         binding.unbindResponseMessage(opMetaData, msg3, epInv, null);
   
         assertEquals("test", epInv.getReturnValue());
      }
      finally
      {
         MessageContextAssociation.popMessageContext();
      }
   }

   public void testMimeMatchingAttachments() throws Exception
   {
      MessageFactoryImpl factory = new MessageFactoryImpl();
      SOAPMessage msg1 = factory.createMessage();

      AttachmentPart part1 = msg1.createAttachmentPart();
      AttachmentPart part2 = msg1.createAttachmentPart();
      AttachmentPart part3 = msg1.createAttachmentPart();
      AttachmentPart part4 = msg1.createAttachmentPart();

      msg1.addAttachmentPart(part1);
      msg1.addAttachmentPart(part2);
      msg1.addAttachmentPart(part3);
      msg1.addAttachmentPart(part4);

      part1.setContentId("1");
      part1.setContentId("2");
      part1.setContentId("3");
      part1.setContentId("4");

      part1.addMimeHeader("TestMe", "alpha");
      part2.addMimeHeader("Test", "gamma");
      part3.addMimeHeader("TestMe", "beta");
      part3.addMimeHeader("TestMe", "alpha");
      part3.addMimeHeader("TestMe", "gamma");
      part4.addMimeHeader("TestMe", "alpha");
      part4.addMimeHeader("TestMe", "beta");

      MimeHeaders headers = new MimeHeaders();
      headers.addHeader("TestMe", "alpha");
      headers.addHeader("TestMe", "beta");

      HashSet matches = new HashSet();

      Iterator i = msg1.getAttachments(headers);
      while (i.hasNext())
      {
         matches.add(i.next());
      }

      assertTrue(matches.contains(part3));
      assertTrue(matches.contains(part4));
      assertTrue(!matches.contains(part1));
      assertTrue(!matches.contains(part2));
   }
}
