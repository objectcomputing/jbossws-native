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
package org.jboss.test.ws.jaxws.jbws871;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.jboss.ws.core.jaxws.JAXBContextFactory;

/**
 * Arrays with JSR181 endpoints
 *
 * http://jira.jboss.com/jira/browse/JBWS-871
 *
 * @author Thomas.Diesler@jboss.com
 * @since 30-Apr-2006
 */
public class JBWS871TestCase extends JBossWSTest
{
   private static RpcArrayEndpoint endpoint;

   public static Test suite()
   {
      return new JBossWSTestSetup(JBWS871TestCase.class, "jaxws-jbws871-rpc.war");
   }

   protected void setUp() throws Exception
   {
      super.setUp();

      if (endpoint == null)
      {
         URL wsdlURL = getResourceURL("jaxws/jbws871/META-INF/wsdl/TestEndpoint.wsdl");
         QName serviceName = new QName("http://jbws871.jaxws.ws.test.jboss.org/", "RpcArrayEndpointService");
         Service service = Service.create(wsdlURL, serviceName);
         endpoint = (RpcArrayEndpoint)service.getPort(RpcArrayEndpoint.class);
      }
   }

   public void testNullArray() throws Exception
   {
      Integer[] intArr = null;

      JAXBContext jbc = JAXBContextFactory.newInstance().createContext(Integer[].class);
      Marshaller m = jbc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FRAGMENT, true);
      StringWriter strw = new StringWriter();
      m.marshal(new JAXBElement(new QName("myarr"), Integer[].class, intArr), strw);

      String xmlFragment = strw.toString();
      // <myarr xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>
      // System.out.println(xmlFragment);

      Unmarshaller um = jbc.createUnmarshaller();
      Source source = new StreamSource(new ByteArrayInputStream(xmlFragment.getBytes()));
      JAXBElement jbel = um.unmarshal(source, Integer[].class);
      assertNull("Null value expected", jbel.getValue());
   }

   public void testEmptyArray() throws Exception
   {
      Integer[] intArr = new Integer[0];

      JAXBContext jbc = JAXBContextFactory.newInstance().createContext(Integer[].class);
      Marshaller m = jbc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FRAGMENT, true);
      StringWriter strw = new StringWriter();
      m.marshal(new JAXBElement(new QName("myarr"), Integer[].class, intArr), strw);

      String xmlFragment = strw.toString();
      // <myarr/>
      // System.out.println(xmlFragment);

      Unmarshaller um = jbc.createUnmarshaller();
      Source source = new StreamSource(new ByteArrayInputStream(xmlFragment.getBytes()));
      JAXBElement jbel = um.unmarshal(source, Integer[].class);
      assertEquals(intArr, jbel.getValue());
   }

   public void testSingleValueArray() throws Exception
   {
      Integer[] intArr = new Integer[] { new Integer(1) };

      JAXBContext jbc = JAXBContextFactory.newInstance().createContext(Integer[].class);
      Marshaller m = jbc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FRAGMENT, true);
      StringWriter strw = new StringWriter();
      m.marshal(new JAXBElement(new QName("myarr"), Integer[].class, intArr), strw);

      String xmlFragment = strw.toString();
      // <myarr><item>1</item></myarr>
      // System.out.println(xmlFragment);

      Unmarshaller um = jbc.createUnmarshaller();
      Source source = new StreamSource(new ByteArrayInputStream(xmlFragment.getBytes()));
      JAXBElement jbel = um.unmarshal(source, Integer[].class);
      assertEquals(intArr, jbel.getValue());
   }

   public void testMultipleValueArray() throws Exception
   {
      Integer[] intArr = new Integer[] { new Integer(1), new Integer(2), new Integer(3) };

      JAXBContext jbc = JAXBContextFactory.newInstance().createContext(Integer[].class);
      Marshaller m = jbc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FRAGMENT, true);
      StringWriter strw = new StringWriter();
      m.marshal(new JAXBElement(new QName("myarr"), Integer[].class, intArr), strw);

      String xmlFragment = strw.toString();
      // <myarr><item>1</item><item>2</item><item>3</item></myarr>
      // System.out.println(xmlFragment);

      Unmarshaller um = jbc.createUnmarshaller();
      Source source = new StreamSource(new ByteArrayInputStream(xmlFragment.getBytes()));
      JAXBElement jbel = um.unmarshal(source, Integer[].class);
      assertEquals(intArr, jbel.getValue());
   }

   public void testEchoNullArray() throws Exception
   {
      Integer[] outArr = endpoint.intArr("null", null);
      assertNull(outArr);
   }

   public void testEchoEmptyArray() throws Exception
   {
      Integer[] outArr = endpoint.intArr("empty", new Integer[]{});
      assertEquals(0, outArr.length);
   }

   public void testEchoSingleValueArray() throws Exception
   {
      Integer[] outArr = endpoint.intArr("single", new Integer[] {new Integer(1)} );
      assertEquals(1, outArr.length);
      assertEquals(new Integer(1), outArr[0]);
   }

   public void testEchoMultipleValueArray() throws Exception
   {
      Integer[] outArr = endpoint.intArr("multi", new Integer[] { new Integer(1), new Integer(2), new Integer(3) });
      assertEquals(3, outArr.length);
      assertEquals(new Integer(1), outArr[0]);
      assertEquals(new Integer(2), outArr[1]);
      assertEquals(new Integer(3), outArr[2]);
   }
}
