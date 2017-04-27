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
package org.jboss.test.ws.jaxws.epr;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import junit.framework.Test;

import org.jboss.ws.core.StubExt;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * [JBWS-1844] Implement Provider.createW3CEndpointReference()
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1844
 *
 * @author Thomas.Diesler@jboss.com
 * @since 25-Feb-2007
 */
public class EndpointReferenceTestCase extends JBossWSTest
{
   public static Test suite()
   {
      return new JBossWSTestSetup(EndpointReferenceTestCase.class, "jaxws-epr.jar");
   }

   public void testSimple() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jaxws-epr/TestEndpointImpl?wsdl");
      QName serviceName = new QName("http://org.jboss.ws/epr", "TestEndpointService");
      Service service = Service.create(wsdlURL, serviceName);
      TestEndpoint port = service.getPort(TestEndpoint.class);
      ((StubExt)port).setConfigName("Standard WSAddressing Client");
      String retStr = port.echo("hello");
      assertEquals("hello", retStr);
   }

   public void testEndpointReference() throws Exception
   {
      String address = "http://" + getServerHost() + ":8080/jaxws-epr/TestEndpointImpl";
      URL wsdlURL = new URL(address + "?wsdl");
      QName serviceName = new QName("http://org.jboss.ws/epr", "TestEndpointService");

      W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
      builder = builder.address(address);
      builder = builder.serviceName(serviceName);
      builder.referenceParameter(DOMUtils.parse("<fabrikam:CustomerKey xmlns:fabrikam='http://example.com/fabrikam'>123456789</fabrikam:CustomerKey>"));
      builder.referenceParameter(DOMUtils.parse("<fabrikam:ShoppingCart xmlns:fabrikam='http://example.com/fabrikam'>ABCDEFG</fabrikam:ShoppingCart>"));
      W3CEndpointReference epr = builder.build();

      Service service = Service.create(wsdlURL, serviceName);
      TestEndpoint port = service.getPort(epr, TestEndpoint.class);
      ((StubExt)port).setConfigName("Standard WSAddressing Client");
      String retStr = port.echo("hello");
      assertEquals("hello|123456789|ABCDEFG", retStr);
   }
   
   public void testEndpointReferenceFromSource() throws Exception
   {
      String address = "http://" + getServerHost() + ":8080/jaxws-epr/TestEndpointImpl";
      URL wsdlURL = new URL(address + "?wsdl");
      QName serviceName = new QName("http://org.jboss.ws/epr", "TestEndpointService");

      StringBuilder sb = new StringBuilder();
      sb.append("<EndpointReference xmlns=\"http://www.w3.org/2005/08/addressing\">");
      sb.append("<Address>").append(address).append("</Address>");
      sb.append("<ServiceName>").append(serviceName).append("</ServiceName>");
      sb.append("<ReferenceParameters>");
      sb.append("<fabrikam:CustomerKey xmlns:fabrikam='http://example.com/fabrikam'>123456789</fabrikam:CustomerKey>");
      sb.append("<fabrikam:ShoppingCart xmlns:fabrikam='http://example.com/fabrikam'>ABCDEFG</fabrikam:ShoppingCart>");
      sb.append("</ReferenceParameters>");
      sb.append("</EndpointReference>");
      Source eprInfoset = new StreamSource(new java.io.StringReader(sb.toString()));
      EndpointReference epr = EndpointReference.readFrom(eprInfoset);

      Service service = Service.create(wsdlURL, serviceName);
      TestEndpoint port = service.getPort(epr, TestEndpoint.class);
      ((StubExt)port).setConfigName("Standard WSAddressing Client");
      String retStr = port.echo("hello");
      assertEquals("hello|123456789|ABCDEFG", retStr);
   }
}
