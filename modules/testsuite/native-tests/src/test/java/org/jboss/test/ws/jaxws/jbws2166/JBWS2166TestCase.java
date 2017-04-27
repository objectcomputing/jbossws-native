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
package org.jboss.test.ws.jaxws.jbws2166;

import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.EndpointReference;
import javax.xml.ws.addressing.JAXWSAConstants;
import javax.xml.ws.soap.AddressingFeature;

import junit.framework.Test;

import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.w3c.dom.Element;

/**
 * [JBWS-2166] WSA client handler throws exception when installing reference parameters
 * 
 * @author alessio.soldano@jboss.com
 * @since 24-Mar-2009
 *
 */
public class JBWS2166TestCase extends JBossWSTest
{
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS2166TestCase.class, "jaxws-jbws2166-A.jar jaxws-jbws2166-B.jar");
   }
   
   public void test() throws Exception
   {
      String endpointAUrl = "http://" + getServerHost() + ":8080/jaxws-jbws2166-A";
      String endpointBUrl = "http://" + getServerHost() + ":8080/jaxws-jbws2166-B";
      QName serviceNameA = new QName("http://org.jboss.ws/jbws2166", "EndpointAService");
      QName serviceNameB = new QName("http://org.jboss.ws/jbws2166", "EndpointBService");
      Service serviceA = Service.create(new URL(endpointAUrl + "?wsdl"), serviceNameA);
      Service serviceB = Service.create(new URL(endpointBUrl + "?wsdl"), serviceNameB);
      EndpointA portA = (EndpointA)serviceA.getPort(EndpointA.class, new AddressingFeature());
      EndpointB portB = (EndpointB)serviceB.getPort(EndpointB.class, new AddressingFeature());
      BindingProvider bindingProviderA = (BindingProvider)portA;
      
      AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
      AddressingProperties props = builder.newAddressingProperties();
      props.setAction(builder.newURI("echo"));
      EndpointReference epr = builder.newEndpointReference(new URI(endpointBUrl));
      Element element = DOMUtils.parse("<wsarj:identifier xmlns:wsarj='myNS'>MyIdentifier</wsarj:identifier>");
      epr.getReferenceParameters().addElement(element);
      props.setReplyTo(epr);
      props.setTo(builder.newURI(new URI(endpointAUrl)));
      props.setMessageID(builder.newURI("TestMessageID"));
      
      bindingProviderA.getRequestContext().put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_OUTBOUND, props);
      portA.echo("Hi!");
      
      Thread.sleep(2000);
      String result = portB.getString();
      
      assertEquals("Hi!", result);
   }
}
