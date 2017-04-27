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
package org.jboss.test.ws.interop.wsa;

import junit.framework.Test;
import org.jboss.ws.core.StubExt;
import org.jboss.ws.extensions.addressing.AddressingClientUtil;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.addressing.*;
import javax.xml.ws.addressing.soap.SOAPAddressingProperties;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Sep 28, 2006
 */
public class AddressingTestCase extends JBossWSTest {

   private static EchoPortType echoPort;
   private static Notify notifyPort;

   final static String WSA_FROM = "http://example.org/node/A";
   final static String WSA_TO = "http://example.org/node/B";

   private static URL wsdlLocation;

   private static AddressingBuilder BUILDER;
   private static AddressingConstants CONSTANTS;

   static
   {
      AddressingTestCase.BUILDER = AddressingBuilder.getAddressingBuilder();
      AddressingTestCase.CONSTANTS = AddressingTestCase.BUILDER.newAddressingConstants();
   }

   private Element customerParam;
   private Element faultParam;
   private Element extraStuff;
   private Element wsdl1Param;
   private Element wsdl2Param;

   public static Test suite()
   {
      return new JBossWSTestSetup(AddressingTestCase.class, "jbossws-interop-wsa10.war");
   }

   protected void setUp() throws Exception
   {
      
      if (echoPort==null || notifyPort==null)
      {
         this.wsdlLocation = getResourceURL("interop/wsa/WEB-INF/wsdl/service.wsdl");

         Service service = Service.create(wsdlLocation, new QName("http://tempuri.org/", "WSAddressingCR"));
         echoPort = service.getPort(EchoPortType.class);
         notifyPort= service.getPort(Notify.class);

         ((StubExt)echoPort).setConfigName("Standard WSAddressing Client");
         ((StubExt)notifyPort).setConfigName("Standard WSAddressing Client");

         ((BindingProvider)echoPort).getRequestContext().put(
					BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					"http://"+getServerHost()+":8080/wsa10/echo"
			);

			((BindingProvider)notifyPort).getRequestContext().put(
					BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					"http://"+getServerHost()+":8080/wsa10/notify"
			);

         configureClient();
      }


      customerParam = DOMUtils.parse("<customer:CustomerKey xmlns:customer=\"http://example.org/customer\">Key#123456789</customer:CustomerKey>");
      faultParam = DOMUtils.parse("<customer:CustomerKey xmlns:customer=\"http://example.org/customer\">Fault#123456789</customer:CustomerKey>");
      wsdl1Param = DOMUtils.parse("<definitions xmlns=\"http://schemas.xmlsoap.org/wsdl/\">insert WSDL 1.1 here!</definitions>");
      wsdl2Param = DOMUtils.parse("<description xmlns=\"http://www.w3.org/2006/01/wsdl\">insert WSDL 2.0 here!</description>");
      extraStuff = DOMUtils.parse("<customer:extraStuff xmlns:customer=\"http://example.org/customer\">This should be ignored</customer:extraStuff>");
   }

   private void configureClient() {

      ((BindingProvider)echoPort).getRequestContext().put(
         BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://"+getServerHost()+":8080/wsa10/echo"         
      );

      ((BindingProvider)notifyPort).getRequestContext().put(
         BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://"+getServerHost()+":8080/wsa10/notify"
      );

      /*
     TODO: Fix scenario DD loading
     IteropConfigFactory factory = InteropConfigFactory.newInstance();
     ClientScenario scenario = factory.createClientScenario(System.getProperty("client.scenario"));
     if(scenario!=null)
     {
        String notifyEndpoint = scenario.getTargetEndpoint().toString();
        log.info("Using scenario: " + scenario);
        log.info("Endpoint at: " + notifyEndpoint);

        ((BindingProvider)echoPort).getRequestContext().put(
           BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
           scenario.getParameter("echoPort")
        );

        ((BindingProvider)notifyPort).getRequestContext().put(
           BindingProvider.ENDPOINT_ADDRESS_PROPERTY, notifyEndpoint
        );

     }
     else
     {
        throw new IllegalStateException("Failed to load client scenario");
     } */
   }


   public String getServerHost()
   {
      String host = null;
      if("true".equals( System.getProperty("interop") ) )
      {
         host = "jbossws.demo.jboss.com";   // ask heiko about details for this service
         System.out.println("Using interop host: jbossws.demo.jboss.com" );
      }
      else
      {
         host = super.getServerHost();
      }
      return host;
   }

   /**
    * Certain MSFT testcases require the connection to be closed
    * since remoting cannot work with particluar HTTP response codes.
    */
   private void forceReset() {
      /*try
      {
         echoPort = null;
         notifyPort = null;
         setUp();
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Failed to reset connection");
      } */
   }

   public void test1100() {
      // create addressing properties
      AddressingProperties requestProps =
         AddressingClientUtil.createDefaultProps("http://example.org/action/notify", AddressingTestCase.WSA_TO);
      setRequestProperties((BindingProvider)notifyPort, requestProps);

      // invoke service
      notifyPort.notify( new NotifyMessage("Message 1100"));
      forceReset();
   }

   public void test1101() {
      try
      {
         // create addressing properties
         AddressingProperties requestProps =
             AddressingClientUtil.createDefaultProps("http://example.org/action/notify", AddressingTestCase.WSA_TO);
         requestProps.setMessageID( AddressingTestCase.BUILDER.newURI( AddressingClientUtil.generateMessageID()));

         setRequestProperties((BindingProvider)notifyPort, requestProps);

         // invoke service
         notifyPort.notify(new NotifyMessage("Message 1101"));
         forceReset();
      }
      catch (Exception e)
      {
         fail(e.getMessage());
      }
   }

   public void test1102() {
      try
      {
         // create addressing properties
         AddressingProperties requestProps =
             AddressingClientUtil.createOneWayProps("http://example.org/action/notify", AddressingTestCase.WSA_TO);

         setRequestProperties((BindingProvider)notifyPort, requestProps);

         // invoke service
         notifyPort.notify(new NotifyMessage("Message 1102"));
         forceReset();
      }
      catch (Exception e)
      {
         fail(e.getMessage());
      }
   }

   public void test1103() throws Exception {
      // create addressing properties
      AddressingProperties requestProps =
          AddressingClientUtil.createDefaultProps(
              "http://example.org/action/notify", AddressingTestCase.WSA_TO);

      requestProps.setFaultTo(AddressingTestCase.BUILDER.newEndpointReference(new URI(AddressingTestCase.CONSTANTS.getNoneURI())));
      setRequestProperties((BindingProvider)notifyPort, requestProps);

      notifyPort.notify(new NotifyMessage("Message 1103"));
      forceReset();
   }

   public void test1104() throws Exception {
      // create addressing properties
      AddressingProperties requestProps =
          AddressingClientUtil.createOneWayProps(
              "http://example.org/action/notify",
              AddressingTestCase.WSA_TO
          );

      requestProps.setFaultTo(AddressingTestCase.BUILDER.newEndpointReference(new URI(AddressingTestCase.CONSTANTS.getNoneURI())));
      setRequestProperties((BindingProvider)notifyPort, requestProps);
      notifyPort.notify(new NotifyMessage("Message 1104"));
      forceReset();
   }

   public void test1106() throws Exception {
      // create addressing properties
      AddressingProperties requestProps =
          AddressingClientUtil.createOneWayProps(
              "http://example.org/action/notify",
              AddressingTestCase.WSA_TO
          );

      requestProps.getReplyTo().getReferenceParameters().addElement(customerParam);
      setRequestProperties((BindingProvider)notifyPort, requestProps);

      notifyPort.notify(new NotifyMessage("Message 1106"));
      forceReset();
   }


   public void test1107() throws Exception {
      // create addressing properties
      AddressingProperties requestProps =
          AddressingClientUtil.createOneWayProps("http://example.org/action/notify", AddressingTestCase.WSA_TO);

      requestProps.getReplyTo().getMetadata().addElement(wsdl1Param);
      requestProps.getReplyTo().getMetadata().addElement(wsdl2Param);

      setRequestProperties((BindingProvider)notifyPort, requestProps);

      notifyPort.notify(new NotifyMessage("Message 1107"));
      forceReset();
   }

   //
   //  One-way message containing a ReplyTo address
   //  with an element extension and an attribute extension
   //  of the ReferenceParameters and Metadata elements.
   // /
   public void test1108() throws Exception {

      AddressingProperties requestProps =
          AddressingClientUtil.createOneWayProps("http://example.org/action/notify", AddressingTestCase.WSA_TO);

      requestProps.getReplyTo().getReferenceParameters().addElement(customerParam);
      requestProps.getReplyTo().addAttribute(new QName("http://example.org/customer","level"), "premium");

      requestProps.getReplyTo().getMetadata().addElement(extraStuff);
      requestProps.getReplyTo().getMetadata().addAttribute(new QName("http://example.org/customer", "total"), "1");

      setRequestProperties((BindingProvider)notifyPort, requestProps);

      notifyPort.notify(new NotifyMessage("Message 1108"));
      forceReset();
   }

   //
   //  Two-way message exchange containing an Action.
   //  All other fields are defaulted.
   //  The presence of a MessageID in the first message and
   //  of the corresponding RelatesTo in the second message is tested.
   //
   public void test1130() throws Exception {
      AddressingProperties requestProps =
          AddressingClientUtil.createDefaultProps("http://example.org/action/echoIn", AddressingTestCase.WSA_TO);
      requestProps.setMessageID(AddressingClientUtil.createMessageID());
      setRequestProperties((BindingProvider)echoPort, requestProps);

      // invoke service
      echoPort.echoOp(new EchoInMessage("Message 1130"));

      SOAPAddressingProperties responseProperties = (SOAPAddressingProperties)
          getResponseProperties((BindingProvider)echoPort);

      forceReset();

      Relationship rel = responseProperties.getRelatesTo()[0];
      assertEquals(rel.getID().toString(), requestProps.getMessageID().getURI().toString());

   }

   //
   //  Two-way message exchange containing
   //  an Action, MessageID and a ReplyTo of anonymous.
   //  All other fields are defaulted.
   //
   public void test1131 () throws Exception {
      AddressingProperties requestProps =
          AddressingClientUtil.createAnonymousProps("http://example.org/action/echoIn", AddressingTestCase.WSA_TO);
      requestProps.setMessageID(AddressingClientUtil.createMessageID());
      setRequestProperties((BindingProvider)echoPort, requestProps);

      // invoke service
      echoPort.echoOp(new EchoInMessage("Message 1131"));

      SOAPAddressingProperties responseProperties = (SOAPAddressingProperties)
          getResponseProperties((BindingProvider)echoPort);
      forceReset();

      Relationship rel = responseProperties.getRelatesTo()[0];
      assertEquals(rel.getID().toString(), requestProps.getMessageID().getURI().toString());
      assertTrue(null == responseProperties.getReplyTo());

   }

   //
   //  Two-way message exchange containing an Action and a ReplyTo
   //  with the address set to anonymous.
   //  The ReplyTo contains at least one Reference Parameter value.
   //  The reply message is returned on the HTTP response with
   //  the Reference Parameter value as a first class SOAP header.
   //
   public void test1132() throws Exception {
      AddressingProperties requestProps =
          AddressingClientUtil.createAnonymousProps("http://example.org/action/echoIn", AddressingTestCase.WSA_TO);
      requestProps.setMessageID(AddressingClientUtil.createMessageID());
      requestProps.getReplyTo().getReferenceParameters().addElement(customerParam);

      setRequestProperties((BindingProvider)echoPort, requestProps);

      // invoke service
      echoPort.echoOp(new EchoInMessage("Message 1132"));

      SOAPAddressingProperties responseProperties = (SOAPAddressingProperties)
          getResponseProperties((BindingProvider)echoPort);

      forceReset();

      Relationship rel = responseProperties.getRelatesTo()[0];
      assertEquals(rel.getID().toString(), requestProps.getMessageID().getURI().toString());
      assertTrue(null == responseProperties.getReplyTo());
      List<Object> returnParameters = responseProperties.getReferenceParameters().getElements();
      assertFalse("Reference parameter is missing", returnParameters.isEmpty());

   }

   //
   //  Two-way message exchange containing an Action.
   //  The ReplyTo and FaultTo addresses are both anonymous.
   //  The ReplyTo and FaultTo contain at least one Reference Parameter value
   //  which are different.
   //  A fault message is returned on the HTTP response with the
   //  FaultTo Reference Parameter value as a first class SOAP header.
   //
   public void test1133() throws Exception {
      AddressingProperties requestProps =
          AddressingClientUtil.createAnonymousProps("http://example.org/action/echoIn", AddressingTestCase.WSA_TO);
      requestProps.setMessageID(AddressingClientUtil.createMessageID());
      requestProps.setFaultTo(AddressingTestCase.BUILDER.newEndpointReference(new URI(AddressingTestCase.CONSTANTS.getAnonymousURI())));

      requestProps.getReplyTo().getReferenceParameters().addElement(customerParam);
      requestProps.getFaultTo().getReferenceParameters().addElement(faultParam);

      setRequestProperties((BindingProvider)echoPort, requestProps);

      // invoke service
      try
      {
         echoPort.echoOp(new EchoInMessage("Message 1133"));
      }
      catch (Exception e)
      {
         boolean isSoapFault = (e.getCause() instanceof SOAPFaultException);
         if(!isSoapFault) throw e;
      }

      SOAPAddressingProperties responseProperties = (SOAPAddressingProperties)
          getResponseProperties((BindingProvider)echoPort);

      forceReset();

      Relationship rel = responseProperties.getRelatesTo()[0];
      assertEquals(rel.getID().toString(), requestProps.getMessageID().getURI().toString());
      assertTrue(null == responseProperties.getReplyTo());
      List<Object> returnParameters = responseProperties.getReferenceParameters().getElements();
      assertFalse("Reference parameter is missing", returnParameters.isEmpty());
   }

   //
   //  Two-way message exchange containing an Action and a ReplyTo address,
   //  but no FaultTo EPR. The ReplyTo address is anonymous.
   //  The ReplyTo contains at least one Reference Parameter value.
   //  A fault message is returned on the HTTP response with the
   //  ReplyTo Reference Parameter value as a first class SOAP header.
   //
   public void test1134() throws Exception {
      AddressingProperties requestProps =
          AddressingClientUtil.createAnonymousProps("http://example.org/action/echoIn", AddressingTestCase.WSA_TO);
      requestProps.setMessageID(AddressingClientUtil.createMessageID());

      requestProps.getReplyTo().getReferenceParameters().addElement(customerParam);

      setRequestProperties((BindingProvider)echoPort, requestProps);

      // invoke service
      try
      {
         echoPort.echoOp(new EchoInMessage("Message 1134"));
      }
      catch (Exception e)
      {
         boolean isSoapFault = (e.getCause() instanceof SOAPFaultException);
         if(!isSoapFault) throw e;
      }

      SOAPAddressingProperties responseProperties = (SOAPAddressingProperties)
          getResponseProperties((BindingProvider)echoPort);

      forceReset();

      Relationship rel = responseProperties.getRelatesTo()[0];
      assertEquals(rel.getID().toString(), requestProps.getMessageID().getURI().toString());
      assertTrue(null == responseProperties.getReplyTo());
      List<Object> returnParameters = responseProperties.getReferenceParameters().getElements();
      assertFalse("Reference parameter is missing", returnParameters.isEmpty());
   }

   //
   //  Two-way message exchange containing a duplicate Reply-To header.
   //
   public void test1140() throws Exception {
      System.out.println("1140: Not supported on the client side");
   }

   //
   //  Two-way message exchange containing a duplicate To header.
   //
   public void test1141() throws Exception {
      System.out.println("1141: Not supported on the client side");
   }

   //
   //  Two-way message exchange containing a duplicate Fault-To header.
   //
   public void test1142() throws Exception {
      System.out.println("1142: Not supported on the client side");
   }

   //
   //  Two-way message exchange containing a duplicate action header.
   //
   public void test1143() throws Exception {
      System.out.println("1143: Not supported on the client side");
   }

   //
   //  wo-way message exchange containing a duplicate message ID header.
   //
   public void test1144() throws Exception {
      System.out.println("1144: Not supported on the client side");
   }

   //
   //  Two-way message exchange containing an
   //  Action and a ReplyTo identifying an endpoint.
   //  All other fields are defaulted.
   //
   public void test1150() throws Exception {
      AddressingProperties requestProps =
          AddressingClientUtil.createDefaultProps("http://example.org/action/echoIn", AddressingTestCase.WSA_TO);
      requestProps.setMessageID(AddressingClientUtil.createMessageID());
      requestProps.setReplyTo(
          AddressingTestCase.BUILDER.newEndpointReference(
              new URI("http://localhost:8080/wsa10/replyTo")
          )
      );

      setRequestProperties((BindingProvider)echoPort, requestProps);

      echoPort.echoOp(new EchoInMessage("Messsage 1150"));

      forceReset();

      // todo: check echOut results
   }

   //
   //  customize a stubs endpoint url
   //
   private static void setTargetAddress(BindingProvider bp, String url) {
      bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
   }

   private void setRequestProperties(BindingProvider bp, AddressingProperties props) {
      bp.getRequestContext().put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_OUTBOUND, props);
   }

   private AddressingProperties getResponseProperties(BindingProvider bp) {
      return (AddressingProperties)bp.getResponseContext().get(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_INBOUND);
   }
}
