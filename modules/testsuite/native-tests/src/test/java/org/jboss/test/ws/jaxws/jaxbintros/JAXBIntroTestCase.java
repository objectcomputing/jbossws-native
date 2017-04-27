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
package org.jboss.test.ws.jaxws.jaxbintros;

import junit.framework.Test;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayInputStream;
import java.net.URL;

/**
 * Test the JAXBIntroduction features.
 * 
 * jaxb-intros.xml can reside under META-INF or WEB-INF and should be
 * picked up by JAXBIntroduction deployment aspect.
 *
 * On the server side the default JAXBContextFactory takes it into consideration.
 * The client side is still missing this feature.
 * 
 * @author heiko.braun@jboss.com
 */
public class JAXBIntroTestCase extends JBossWSTest
{
   public static Test suite()
   {
      return new JBossWSTestSetup(JAXBIntroTestCase.class, "jaxws-jaxbintros.war");
   }
                                     
   public void testWSDLAccess() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jaxws-jaxbintros/ProviderEndpoint?wsdl");
      Element wsdl = DOMUtils.parse(wsdlURL.openStream());
      assertNotNull(wsdl);
   }

   public void testProviderMessage() throws Exception
   {
      String reqString =
         "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" +
         "  <env:Header/>" +
         "  <env:Body>" +
         "    <ns1:user xmlns:ns1='http://org.jboss.ws/provider' string='Kermit'>" +       
         "      <qname>The Frog</qname>" +
         "    </ns1:user>" +
         "  </env:Body>" +
         "</env:Envelope>";

      MessageFactory msgFactory = MessageFactory.newInstance();
      SOAPConnection con = SOAPConnectionFactory.newInstance().createConnection();
      SOAPMessage reqMsg = msgFactory.createMessage(null, new ByteArrayInputStream(reqString.getBytes()));

      URL epURL = new URL("http://" + getServerHost() + ":8080/jaxws-jaxbintros/ProviderEndpoint");
      SOAPMessage resMsg = con.call(reqMsg, epURL);
      SOAPEnvelope resEnv = resMsg.getSOAPPart().getEnvelope();

      Element child = (Element)resEnv.getBody().getChildElements().next();
      JAXBContext jc = JAXBContext.newInstance(new Class[]{AnnotatedUserType.class});
      AnnotatedUserType user = (AnnotatedUserType)jc.createUnmarshaller().unmarshal(new DOMSource(child));

      assertEquals("Kermit", user.getString());
      assertEquals(new QName("The Frog"), user.getQname());
   }
}
