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
package org.jboss.test.ws.jaxrpc.jbws434;

import java.util.Iterator;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

/**
 * [JBWS-434] Support sequences of anys
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 11-Nov-2005
 */
public class JBWS434TestCase extends JBossWSTest
{
   private static TestServiceEndpoint port;

   public static Test suite()
   {
      return new JBossWSTestSetup(JBWS434TestCase.class, "jaxrpc-jbws434.war, jaxrpc-jbws434-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/TestService");
         port = (TestServiceEndpoint)service.getPort(TestServiceEndpoint.class);
      }
   }

   public void testWildCardArrayWithOtherNS() throws Exception
   {
      SOAPFactory factory = SOAPFactory.newInstance();

      SOAPElement el1 = factory.createElement("name", "ns1", "http://somens");
      el1.setValue("Kermmit");
      SOAPElement el2 = factory.createElement("product", "ns1", "http://somens");
      el2.setValue("Ferrari");

      ArrayOfAny inObj = new ArrayOfAny(new SOAPElement[] { el1, el2 });
      ArrayOfAny retObj = port.echo(inObj);

      assertNotNull(retObj);
      assertNotNull(retObj._any);
      assertEquals(inObj._any.length, retObj._any.length);

      for(int i = 0; i < inObj._any.length; ++i)
      {
         SOAPElement inE = inObj._any[i];
         SOAPElement retE = retObj._any[i];
         assertEquals(inE, retE);
      }
   }

   public void testWildCardArrayWithAnyNS() throws Exception
   {
      SOAPFactory factory = SOAPFactory.newInstance();

      SOAPElement el1 = factory.createElement("name", "ns1", "http://somens");
      el1.setValue("Kermmit");
      SOAPElement el2 = factory.createElement("product", "ns1", "http://somens");
      el2.setValue("Ferrari");

      ArrayOfAny2 inObj = new ArrayOfAny2(new SOAPElement[] { el1, el2 });
      ArrayOfAny2 retObj = port.echo2(inObj);

      assertNotNull(retObj);
      assertNotNull(retObj._any);
      assertEquals(inObj._any.length, retObj._any.length);

      for(int i = 0; i < inObj._any.length; ++i)
      {
         SOAPElement inE = inObj._any[i];
         SOAPElement retE = retObj._any[i];
         assertEquals(inE, retE);
      }
   }

   public void testWildCardArrayWithMaxOccurance() throws Exception
   {
      SOAPFactory factory = SOAPFactory.newInstance();

      SOAPElement el1 = factory.createElement("name", "ns1", "http://somens");
      el1.setValue("Kermmit");

      TypeOfAny3 inObj = new TypeOfAny3(el1);
      TypeOfAny3 retObj = port.echo3(inObj);

      assertNotNull(retObj);
      assertNotNull(retObj._any);

      SOAPElement inE = inObj._any;
      SOAPElement retE = retObj._any;
      assertEquals(inE, retE);
   }

   /**
    * An element declared as wildcard uses an element
    * declared in schema as wildcard contents. 
    */
   public void testWildCardContentsDeclared() throws Exception
   {
      SOAPFactory factory = SOAPFactory.newInstance();

      SOAPElement el1 = factory.createElement("knownWildcard", "ns1", "http://somens");
      el1.setValue("Kermmit");

      TypeOfAny3 inObj = new TypeOfAny3(el1);
      TypeOfAny3 retObj = port.echo3(inObj);

      assertNotNull(retObj);
      assertNotNull(retObj._any);

      SOAPElement inE = inObj._any;
      SOAPElement retE = retObj._any;
      assertEquals(inE, retE);
   }

   private static void assertEquals(SOAPElement myE, SOAPElement otherE)
   {
      assertEquals(otherE.getLocalName(), myE.getLocalName());
      assertEquals(myE.getNamespaceURI(), otherE.getNamespaceURI());

      NamedNodeMap myAttrs = myE.getAttributes();
      int myTotalAttrs = myAttrs == null ? 0 : myAttrs.getLength();
      NamedNodeMap otherAttrs = otherE.getAttributes();
      int otherTotalAttrs = otherAttrs == null ? 0 : otherAttrs.getLength();
      assertEquals(myTotalAttrs, otherTotalAttrs);

      if(myTotalAttrs > 0)
      {
         for(int attrIndex = 0; attrIndex < myAttrs.getLength(); ++attrIndex)
         {
            Attr myAttr = (Attr)myAttrs.item(attrIndex);
            String myValue = myAttr.getValue();
            String otherValue = otherE.getAttributeNS(myAttr.getNamespaceURI(), myAttr.getLocalName());
            assertEquals(myValue, otherValue);
         }
      }

      boolean myHasChildren = DOMUtils.hasChildElements(myE);
      boolean otherHasChildren = DOMUtils.hasChildElements(otherE);
      assertEquals(myHasChildren, otherHasChildren);

      if(myHasChildren)
      {
         Iterator myChildren = DOMUtils.getChildElements(myE);
         Iterator otherChildren = DOMUtils.getChildElements(otherE);

         int myChildrenTotal = 0;
         int otherChildrenTotal = 0;
         while(myChildren.hasNext() && otherChildren.hasNext())
         {
            SOAPElement myChild = (SOAPElement)myChildren.next();
            ++myChildrenTotal;
            SOAPElement otherChild = (SOAPElement)myChildren.next();
            ++otherChildrenTotal;

            assertEquals(myChild, otherChild);
         }

         assertEquals(myChildrenTotal, otherChildrenTotal);
      }

      String myText = DOMUtils.getTextContent(myE);
      String otherText = DOMUtils.getTextContent(otherE);
      assertEquals(myText, otherText);
   }
}
