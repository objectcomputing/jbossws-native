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
package org.jboss.test.ws.jaxrpc.jbws720;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/** 
 * Handling of xml:lang and any namespace="##other"
 * 
 * http://jira.jboss.com/jira/browse/JBWS-720
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 04-Mar-2006
 */
public class JBWS720TestCase extends JBossWSTest
{
   private static TestEndpoint port;

   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS720TestCase.class, "jaxrpc-jbws720.war, jaxrpc-jbws720-client.jar");
   }

   public void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         port = (TestEndpoint)service.getPort(TestEndpoint.class);
      }
   }

   public void testLangEmptyAny() throws Exception
   {
      GetProperty inObj = new GetProperty();
      inObj.setStrElement("someElement");
      inObj.setStrAttr("someAttr");
      inObj.setLang("en");
      inObj.set_any(new SOAPElement[] {});

      GetPropertyResponse outObj = port.getProperty(inObj);
      assertEquals(inObj.toString(), outObj.getResult());
   }

   public void testEmptyLangEmptyAny() throws Exception
   {
      GetProperty inObj = new GetProperty();
      inObj.setStrElement("someElement");
      inObj.setStrAttr("someAttr");
      inObj.set_any(new SOAPElement[] {});

      GetPropertyResponse outObj = port.getProperty(inObj);
      assertEquals(inObj.toString(), outObj.getResult());
   }

   public void testLangAnyWithNamespace() throws Exception
   {
      SOAPFactory factory = SOAPFactory.newInstance();

      GetProperty inObj = new GetProperty();
      inObj.setStrElement("someElement");
      inObj.setStrAttr("someAttr");
      inObj.setLang("en");

      // test any element with namespace
      SOAPElement el1 = factory.createElement("el1", "nsany", "http://somens");
      SOAPElement el2 = factory.createElement("el2", "nsany", "http://somens");
      inObj.set_any(new SOAPElement[] { el1, el2 });

      GetPropertyResponse outObj = port.getProperty(inObj);
      assertEquals(inObj.toString(), outObj.getResult());
   }

   public void testLangAnyWithoutNamespace() throws Exception
   {
      SOAPFactory factory = SOAPFactory.newInstance();

      GetProperty inObj = new GetProperty();
      inObj.setStrElement("someElement");
      inObj.setStrAttr("someAttr");
      inObj.setLang("en");

      // test any element without namespace
      SOAPElement el1 = factory.createElement("el1");
      SOAPElement el2 = factory.createElement("el2");
      inObj.set_any(new SOAPElement[] { el1, el2 });

      GetPropertyResponse outObj = port.getProperty(inObj);
      assertEquals(inObj.toString(), outObj.getResult());
   }

   public void testAllNull() throws Exception
   {
      GetProperty inObj = new GetProperty();

      GetPropertyResponse outObj = port.getProperty(inObj);
      assertEquals(inObj.toString(), outObj.getResult());
   }
}
