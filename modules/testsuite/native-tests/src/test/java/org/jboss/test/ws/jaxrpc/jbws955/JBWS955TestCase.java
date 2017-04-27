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
package org.jboss.test.ws.jaxrpc.jbws955;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Element;

/**
 * Cannot deserialize fault detail
 * 
 * http://jira.jboss.org/jira/browse/JBWS-955
 *
 * @author Thomas.Diesler@jboss.org
 * @since 31-May-2006
 */
public class JBWS955TestCase extends JBossWSTest
{

   public void testWebService() throws Exception
   {
      String msgStr = 
         "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>" +
         "<soapenv:Header/>" +
         " <soapenv:Body>" +
         "  <env:Fault xmlns:env='http://schemas.xmlsoap.org/soap/envelope/' xmlns:wsrp='urn:oasis:names:tc:wsrp:v1:types'>" +
         "   <faultcode>wsrp:InvalidRegistration</faultcode>" +
         "   <faultstring>Missing registrationHandle.</faultstring>" +
         "   <detail>" +
         "    <wsrp:InvalidRegistration/>" +
         "    <fd:FaultDetail xmlns:fd='urn:bea:wsrp:ext:v1:types'>" +
         "     <fd:created>2006-05-23T23:18:36.312-06:00</fd:created>" +
         "     <fd:trace>com.bea.wsrp.faults.v1.InvalidRegistrationExceptionImpl: Missing registrationHandle." +
         "        at com.bea.wsrp.producer.handlers.RegistrationHandleFilter.doFilter(RegistrationHandleFilter.java:121)" +
         "        at com.bea.wsrp.producer.handlers.AbstractServiceHandler.preprocess(AbstractServiceHandler.java:131)" +
         "     </fd:trace>" +
         "    </fd:FaultDetail>" +
         "   </detail>" +
         "  </env:Fault>" +
         " </soapenv:Body>" +
         "</soapenv:Envelope>";
      
      MessageFactory factory = MessageFactory.newInstance();
      SOAPMessage soapMsg = factory.createMessage(null, new ByteArrayInputStream(msgStr.getBytes()));
      SOAPFault soapFault = soapMsg.getSOAPBody().getFault();
      assertNotNull(soapFault);
      
      //SOAPFaultExceptionHelper.getSOAPFaultException
      //
      Detail detail = soapFault.getDetail();
      Iterator it = detail.getDetailEntries();
      while (it.hasNext())
      {
         DetailEntry deElement = (DetailEntry)it.next();
         Name deName = deElement.getElementName();

         String nsURI = deName.getURI();
         String prefix = deName.getPrefix();
         String attrValue = deElement.getAttribute("xmlns:" + prefix);
         if (nsURI.length() > 0 && attrValue.length() == 0)
            deElement.addNamespaceDeclaration(prefix, nsURI);
         
         String xmlFragment = DOMWriter.printNode(deElement, false);
         Element domElement = DOMUtils.parse(xmlFragment);
         assertNotNull(domElement);
      }
   }
}
