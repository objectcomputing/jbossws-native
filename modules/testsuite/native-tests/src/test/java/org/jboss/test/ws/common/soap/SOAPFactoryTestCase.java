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

import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;

import org.jboss.wsf.test.JBossWSTest;

/**
 * Test the SOAPFactory
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Oct-2004
 */
public class SOAPFactoryTestCase extends JBossWSTest
{
   public void testSOAPFactoryCreation() throws Exception
   {
      SOAPFactory factory = SOAPFactory.newInstance();
      assertEquals(org.jboss.ws.core.soap.SOAPFactoryImpl.class, factory.getClass());
   }

   public void testCreateDetail() throws Exception
   {
      SOAPFactory factory = SOAPFactory.newInstance();
      Detail el = factory.createDetail();
      assertEquals(factory.createName("detail"), el.getElementName());
   }

   public void testCreateElement() throws Exception
   {
      SOAPFactory factory = SOAPFactory.newInstance();

      Name shortName = factory.createName("localName");
      Name longName = factory.createName("localName", "pre", "http://someURI");

      SOAPElement el = factory.createElement("localName");
      assertEquals(shortName, el.getElementName());

      el = factory.createElement("localName", "pre", "http://someURI");
      assertEquals(longName, el.getElementName());

      el = factory.createElement(shortName);
      assertEquals(shortName, el.getElementName());

      el = factory.createElement(longName);
      assertEquals(longName, el.getElementName());
   }

   public void testCreateName() throws Exception
   {
      SOAPFactory factory = SOAPFactory.newInstance();
      Name name = factory.createName("localName");
      assertEquals("localName", name.getLocalName());
      assertEquals("localName", name.getQualifiedName());
      assertEquals("", name.getPrefix());
      assertEquals("", name.getURI());

      name = factory.createName("localName", "pre", "http://someURI");
      assertEquals("localName", name.getLocalName());
      assertEquals("pre:localName", name.getQualifiedName());
      assertEquals("pre", name.getPrefix());
      assertEquals("http://someURI", name.getURI());
   }

}
