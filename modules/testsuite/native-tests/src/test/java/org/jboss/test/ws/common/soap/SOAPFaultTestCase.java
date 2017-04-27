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
package org.jboss.test.ws.common.soap;

import java.io.ByteArrayInputStream;

import javax.xml.namespace.QName;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.Constants;
import org.jboss.ws.core.jaxrpc.SOAPFaultHelperJAXRPC;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;

/**
 * Test the SOAPFault
 *
 * @author Thomas.Diesler@jboss.org
 * @author Ivan Neto (ivanneto@gmail.com)
 * @since 03-Feb-2004
 */
public class SOAPFaultTestCase extends JBossWSTest
{
   private String envStr =
      "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
      " <env:Header/>" +
      " <env:Body>" +
      "  <env:Fault>" +
      "   <faultcode>env:Client</faultcode>" +
      "   <faultstring>Some fault message</faultstring>" +
      "   <faultactor>Some fault actor</faultactor>" +
      "   <detail>" +
      "     <ns1:name xmlns:ns1='http://somens'>Kermit</ns1:name>" +
      "   </detail>" +
      "  </env:Fault>" +
      " </env:Body>" +
      "</env:Envelope>";

   public void testExceptionToFault() throws Exception
   {
      Detail detail = createDetailElement();
      SOAPFaultException faultEx = new SOAPFaultException(Constants.SOAP11_FAULT_CODE_CLIENT, "Some fault message", "Some fault actor", detail);
      SOAPEnvelope soapEnv = SOAPFaultHelperJAXRPC.exceptionToFaultMessage(faultEx).getSOAPPart().getEnvelope();
      assertEquals(DOMUtils.parse(envStr), soapEnv);
   }

   public void testFaultToException() throws Exception
   {
      MessageFactory factory = MessageFactory.newInstance();
      SOAPMessage soapMessage = factory.createMessage(null, new ByteArrayInputStream(envStr.getBytes()));
      SOAPBody soapBody = soapMessage.getSOAPBody();
      SOAPFault soapFault = (SOAPFault)soapBody.getChildElements(new QName("http://schemas.xmlsoap.org/soap/envelope/", "Fault")).next();
      assertEquals("env:Client", soapFault.getFaultCode());
      assertEquals("Some fault message", soapFault.getFaultString());
      assertEquals("Some fault actor", soapFault.getFaultActor());
      assertEquals(createDetailElement(), soapFault.getDetail());

      SOAPFaultException faultEx = SOAPFaultHelperJAXRPC.getSOAPFaultException((SOAPFault)soapFault);

      assertEquals(Constants.SOAP11_FAULT_CODE_CLIENT, faultEx.getFaultCode());
      assertEquals("Some fault message", faultEx.getFaultString());
      assertEquals("Some fault actor", faultEx.getFaultActor());
      assertEquals(createDetailElement(), faultEx.getDetail());
   }

   private Detail createDetailElement() throws SOAPException
   {
      SOAPFactory factory = SOAPFactory.newInstance();
      Detail detail = factory.createDetail();
      Name name = factory.createName("name", "ns1", "http://somens");
      DetailEntry detailEntry = detail.addDetailEntry(name);
      detailEntry.setValue("Kermit");
      return detail;
   }
}
