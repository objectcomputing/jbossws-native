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

import junit.framework.TestCase;
import org.jboss.ws.core.soap.XMLFragment;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Element;

import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * @author Heiko.Braun@jboss.org
 * @since 05.02.2007
 */
public class XMLFragmentTestCase extends TestCase {

   public static String XML_STRING = "<parent><child/></parent>";

   public static String JAXB_FRAGMENT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
         "<ns2:sayHelloResponse xmlns:ns2=\"http://hello.org/wsdl\">\n" +
         "<return>WSEjbWebServiceProvider-SayHello</return></ns2:sayHelloResponse>";
   
   public void testDOMSourceFragment() throws Exception
   {
      Element srcElement = DOMUtils.parse(XML_STRING);
      XMLFragment xmlFragment = new XMLFragment( new DOMSource(srcElement) );
      testSourceAPI(xmlFragment);
   }

   private void testSourceAPI(XMLFragment xmlFragment) throws Exception
   {
      assertEquals(DOMWriter.printNode( xmlFragment.toElement(), false), XML_STRING);

      Element reparsed = xmlFragment.toElement();
      assertEquals("parent", reparsed.getNodeName());
      assertEquals("child", reparsed.getFirstChild().getNodeName());

      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter( bout);
      xmlFragment.writeTo(writer);
      assertEquals(XML_STRING, new String(bout.toByteArray()));
   }

   public void testJAXBFragments() throws Exception
   {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter( bout);

      XMLFragment fragment = new XMLFragment(JAXB_FRAGMENT);
      fragment.writeTo(writer);
      writer.flush();

      String s = bout.toString();
      //System.out.println(s);

      assertTrue("Empty result returned", s.length()>0);
      assertFalse("Should not contain processing instruction", s.startsWith("<?xml"));
   }

   public void testJAXBFragments2() throws Exception
   {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter( bout);

      XMLFragment fragment = new XMLFragment(XML_STRING);
      fragment.writeTo(writer);
      writer.flush();

      String s = bout.toString();

      assertTrue("Empty result returned", s.length()>0);
      assertFalse("Should not contain processing instruction", s.startsWith("<?xml"));
      assertEquals(XML_STRING, s);
   }
}
