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
package org.jboss.test.ws.interop.nov2007.wsaSoap12;

import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingConstants;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.JAXWSAConstants;
import javax.xml.ws.addressing.Relationship;
import javax.xml.ws.addressing.soap.SOAPAddressingProperties;
import javax.xml.ws.soap.SOAPFaultException;

import junit.framework.Test;

import org.jboss.test.ws.interop.ClientScenario;
import org.jboss.test.ws.interop.InteropConfigFactory;
import org.jboss.ws.core.StubExt;
import org.jboss.ws.extensions.addressing.AddressingClientUtil;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.w3c.dom.Element;

/**
 * @author Alessio Soldano, alessio.soldano@jboss.com
 * @since 31-Oct-2007
 */
public class AddressingTestCase extends JBossWSTest {

   private static Echo echoPort;
   private static Notify notifyPort;

   final static String WSA_FROM = "http://example.org/node/A";
   final static String WSA_TO = "http://example.org/node/B";
//   final static String WSA_TO = "http://131.107.72.15/WSAddressingCR_Service_WCF/WSAddressing10.svc/Soap12";
   
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
      return new JBossWSTestSetup(AddressingTestCase.class, "jbossws-interop-nov2007-wsaSoap12.war, jbossws-interop-nov2007-wsaSoap12-client.jar");
   }
   
   protected void setUp() throws Exception
   {
      if (echoPort==null || notifyPort==null)
      {
         wsdlLocation = getResourceURL("interop/nov2007/wsaSoap12/WEB-INF/wsdl/service.wsdl");

         Service service = Service.create(wsdlLocation, new QName("http://tempuri.org/", "WSAddressingCR"));
         echoPort = service.getPort(Echo.class);
         notifyPort= service.getPort(Notify.class);

         ((StubExt)echoPort).setConfigName("Standard SOAP 1.2 WSAddressing Client");
         ((StubExt)notifyPort).setConfigName("Standard SOAP 1.2 WSAddressing Client");
         
         configureClient();
      }

      customerParam = DOMUtils.parse("<customer:CustomerKey xmlns:customer=\"http://example.org/customer\">Key#123456789</customer:CustomerKey>");
      faultParam = DOMUtils.parse("<customer:CustomerKey xmlns:customer=\"http://example.org/customer\">Fault#123456789</customer:CustomerKey>");
      wsdl1Param = DOMUtils.parse("<definitions xmlns=\"http://schemas.xmlsoap.org/wsdl/\">insert WSDL 1.1 here!</definitions>");
      wsdl2Param = DOMUtils.parse("<description xmlns=\"http://www.w3.org/2006/01/wsdl\">insert WSDL 2.0 here!</description>");
      extraStuff = DOMUtils.parse("<customer:extraStuff xmlns:customer=\"http://example.org/customer\">This should be ignored</customer:extraStuff>");
   }
   
   private void configureClient() {

     InteropConfigFactory factory = InteropConfigFactory.newInstance();
     ClientScenario scenario = factory.createClientScenario(System.getProperty("client.scenario"));
     if(scenario!=null)
     {
        String notifyEndpoint = scenario.getTargetEndpoint().toString();
        log.info("Using scenario: " + scenario);
        log.info("Endpoint at: " + notifyEndpoint);
        
        
        if (notifyEndpoint.contains("REPLACE_WITH_ACTUAL_HOST"))
        {
           notifyEndpoint = notifyEndpoint.replace("REPLACE_WITH_ACTUAL_HOST", getServerHost());
        }
        System.out.println("Using notify target endpoint: " + notifyEndpoint);
        String echoEndpoint = scenario.getParameter("echoPort");
        if (echoEndpoint.contains("REPLACE_WITH_ACTUAL_HOST"))
        {
           echoEndpoint = echoEndpoint.replace("REPLACE_WITH_ACTUAL_HOST", getServerHost());
        }
        System.out.println("... and echo target endpoint: " + echoEndpoint);
        
        ((BindingProvider)echoPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, echoEndpoint);
        ((BindingProvider)notifyPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, notifyEndpoint);

     }
     else
     {
        throw new IllegalStateException("Failed to load client scenario");
     } 
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
      }*/ 
   }

   public void test1200() {
      // create addressing properties
      AddressingProperties requestProps =
         AddressingClientUtil.createDefaultProps("http://example.org/action/notify", AddressingTestCase.WSA_TO);
      setRequestProperties((BindingProvider)notifyPort, requestProps);

      // invoke service
      notifyPort.notify("Message 1200");
      forceReset();
   }

   public void test1201() {
      try
      {
         // create addressing properties
         AddressingProperties requestProps =
             AddressingClientUtil.createDefaultProps("http://example.org/action/notify", AddressingTestCase.WSA_TO);
         requestProps.setMessageID( AddressingTestCase.BUILDER.newURI( AddressingClientUtil.generateMessageID()));

         setRequestProperties((BindingProvider)notifyPort, requestProps);

         // invoke service
         notifyPort.notify("Message 1201");
         forceReset();
      }
      catch (Exception e)
      {
         fail(e.getMessage());
      }
   }

   public void test1202() {
      try
      {
         // create addressing properties
         AddressingProperties requestProps =
             AddressingClientUtil.createOneWayProps("http://example.org/action/notify", AddressingTestCase.WSA_TO);

         setRequestProperties((BindingProvider)notifyPort, requestProps);

         // invoke service
         notifyPort.notify("Message 1202");
         forceReset();
      }
      catch (Exception e)
      {
         fail(e.getMessage());
      }
   }

   public void test1203() throws Exception {
      // create addressing properties
      AddressingProperties requestProps =
          AddressingClientUtil.createDefaultProps(
              "http://example.org/action/notify", AddressingTestCase.WSA_TO);

      requestProps.setFaultTo(AddressingTestCase.BUILDER.newEndpointReference(new URI(AddressingTestCase.CONSTANTS.getNoneURI())));
      setRequestProperties((BindingProvider)notifyPort, requestProps);

      notifyPort.notify("Message 1203");
      forceReset();
   }

   public void test1204() throws Exception {
      // create addressing properties
      AddressingProperties requestProps =
          AddressingClientUtil.createOneWayProps(
              "http://example.org/action/notify",
              AddressingTestCase.WSA_TO
          );

      requestProps.setFaultTo(AddressingTestCase.BUILDER.newEndpointReference(new URI(AddressingTestCase.CONSTANTS.getNoneURI())));
      setRequestProperties((BindingProvider)notifyPort, requestProps);
      notifyPort.notify("Message 1204");
      forceReset();
   }

   public void test1206() throws Exception {
      // create addressing properties
      AddressingProperties requestProps =
          AddressingClientUtil.createOneWayProps(
              "http://example.org/action/notify",
              AddressingTestCase.WSA_TO
          );

      requestProps.getReplyTo().getReferenceParameters().addElement(customerParam);
      setRequestProperties((BindingProvider)notifyPort, requestProps);

      notifyPort.notify("Message 1206");
      forceReset();
   }


   public void test1207() throws Exception {
      // create addressing properties
      AddressingProperties requestProps =
          AddressingClientUtil.createOneWayProps("http://example.org/action/notify", AddressingTestCase.WSA_TO);

      requestProps.getReplyTo().getMetadata().addElement(wsdl1Param);
      requestProps.getReplyTo().getMetadata().addElement(wsdl2Param);

      setRequestProperties((BindingProvider)notifyPort, requestProps);

      notifyPort.notify("Message 1207");
      forceReset();
   }

   //
   //  One-way message containing a ReplyTo address
   //  with an element extension and an attribute extension
   //  of the ReferenceParameters and Metadata elements.
   // /
   public void test1208() throws Exception {

      AddressingProperties requestProps =
          AddressingClientUtil.createOneWayProps("http://example.org/action/notify", AddressingTestCase.WSA_TO);

      requestProps.getReplyTo().getReferenceParameters().addElement(customerParam);
      requestProps.getReplyTo().addAttribute(new QName("http://example.org/customer","level"), "premium");

      requestProps.getReplyTo().getMetadata().addElement(extraStuff);
      requestProps.getReplyTo().getMetadata().addAttribute(new QName("http://example.org/customer", "total"), "1");

      setRequestProperties((BindingProvider)notifyPort, requestProps);

      notifyPort.notify("Message 1208");
      forceReset();
   }

   //
   //  Two-way message exchange containing an Action.
   //  All other fields are defaulted.
   //  The presence of a MessageID in the first message and
   //  of the corresponding RelatesTo in the second message is tested.
   //
   public void test1230() throws Exception {
      AddressingProperties requestProps =
          AddressingClientUtil.createDefaultProps("http://example.org/action/echoIn", AddressingTestCase.WSA_TO);
      requestProps.setMessageID(AddressingClientUtil.createMessageID());
      setRequestProperties((BindingProvider)echoPort, requestProps);

      // invoke service
      echoPort.echo("Message 1230");

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
   public void test1231 () throws Exception {
      AddressingProperties requestProps =
          AddressingClientUtil.createAnonymousProps("http://example.org/action/echoIn", AddressingTestCase.WSA_TO);
      requestProps.setMessageID(AddressingClientUtil.createMessageID());
      setRequestProperties((BindingProvider)echoPort, requestProps);

      // invoke service
      echoPort.echo("Message 1231");

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
   public void test1232() throws Exception {
      AddressingProperties requestProps =
          AddressingClientUtil.createAnonymousProps("http://example.org/action/echoIn", AddressingTestCase.WSA_TO);
      requestProps.setMessageID(AddressingClientUtil.createMessageID());
      requestProps.getReplyTo().getReferenceParameters().addElement(customerParam);

      setRequestProperties((BindingProvider)echoPort, requestProps);

      // invoke service
      echoPort.echo("Message 1232");

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
   public void test1233() throws Exception {
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
         echoPort.echo("Message 1233");
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
   public void test1234() throws Exception {
      AddressingProperties requestProps =
          AddressingClientUtil.createAnonymousProps("http://example.org/action/echoIn", AddressingTestCase.WSA_TO);
      requestProps.setMessageID(AddressingClientUtil.createMessageID());

      requestProps.getReplyTo().getReferenceParameters().addElement(customerParam);

      setRequestProperties((BindingProvider)echoPort, requestProps);

      // invoke service
      try
      {
         echoPort.echo("Message 1234");
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
   public void test1240() throws Exception {
      System.out.println("1140: Not supported on the client side");
   }

   //
   //  Two-way message exchange containing a duplicate To header.
   //
   public void test1241() throws Exception {
      System.out.println("1141: Not supported on the client side");
   }

   //
   //  Two-way message exchange containing a duplicate Fault-To header.
   //
   public void test1242() throws Exception {
      System.out.println("1142: Not supported on the client side");
   }

   //
   //  Two-way message exchange containing a duplicate action header.
   //
   public void test1243() throws Exception {
      System.out.println("1143: Not supported on the client side");
   }

   //
   //  Two-way message exchange containing a duplicate message ID header.
   //
   public void test1244() throws Exception {
      System.out.println("1144: Not supported on the client side");
   }

   //
   //  Two-way message exchange containing an
   //  Action and a ReplyTo identifying an endpoint.
   //  All other fields are defaulted.
   //
   public void test1250() throws Exception {
      AddressingProperties requestProps =
          AddressingClientUtil.createDefaultProps("http://example.org/action/echoIn", AddressingTestCase.WSA_TO);
      requestProps.setMessageID(AddressingClientUtil.createMessageID());
      requestProps.setReplyTo(
          AddressingTestCase.BUILDER.newEndpointReference(
              new URI("http://localhost:8080/nov2007/wsaSoap12/replyTo")
          )
      );

      setRequestProperties((BindingProvider)echoPort, requestProps);

      echoPort.echo("Message 1250");

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
