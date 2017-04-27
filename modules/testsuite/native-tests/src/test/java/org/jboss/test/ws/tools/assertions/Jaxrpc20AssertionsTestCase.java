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
package org.jboss.test.ws.tools.assertions;

import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.test.ws.tools.sei.SomeException;
import org.jboss.test.ws.tools.sei.assertions.AssertMethodParamExtendRemote;
import org.jboss.test.ws.tools.sei.assertions.AssertRemoteExceptions;
import org.jboss.test.ws.tools.sei.assertions.AssertRemoteType;
import org.jboss.test.ws.tools.sei.assertions.AssertReturnTypeExtendRemote;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.tools.JavaToWSDL;

/**
 * Testcase that handles Jaxrpc 2.0 assertions
 *
 * @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 * @since Feb 3, 2005
 */
public class Jaxrpc20AssertionsTestCase extends WSToolsBase
{
   public void testIsInterface() throws Exception
   {
      assertWSDL11Test(SomeException.class);
   }

   public void testRemoteType() throws Exception
   {
      assertWSDL11Test(AssertRemoteType.class);
   }

   public void testRemoteExceptionsDeclared() throws Exception
   {
      assertWSDL11Test(AssertRemoteExceptions.class);
   }

   public void testAssertMethodParamExtendRemote() throws Exception
   {
      assertWSDL11Test(AssertMethodParamExtendRemote.class);
   }

   public void testAssertReturnTypeExtendRemote() throws Exception
   {
      assertWSDL11Test(AssertReturnTypeExtendRemote.class);
   }

   /**
    * Test an invalid Value Types case where the value type defines both
    * a public member and a public property
    * @throws Exception
    */
   public void testMultiXSD() throws Exception
   {
      String seiName = "org.jboss.test.ws.tools.assertions.sei.MultiXSDInvalidInterface";
      Class seiClass = loadClass(seiName);
      JavaToWSDL jwsdl = new JavaToWSDL(Constants.NS_WSDL11);
      jwsdl.setTargetNamespace("http://org.jboss.ws/types");
      jwsdl.setServiceName(WSDLUtils.getInstance().getJustClassName(seiClass) + "Service");
      try
      {
         jwsdl.generate(loadClass(seiName));
         fail("Test should have failed");
      }
      catch (WSException is)
      {
         //pass
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail("Test should not have passed");
      }
   }

   /**
    * The test is to see if the JavaToWSDL subsystem throws Jaxrpc 2.0 Assertions
    * for WSDL 1.1
    * @param seiClass
    * @throws Exception
    */
   private void assertWSDL11Test(Class seiClass) throws Exception
   {
      String seiName = seiClass.getName();
      JavaToWSDL jwsdl = new JavaToWSDL(Constants.NS_WSDL11);
      jwsdl.setTargetNamespace("http://org.jboss.ws/types");
      jwsdl.setServiceName(WSDLUtils.getInstance().getJustClassName(seiClass) + "Service");
      try
      {
         jwsdl.generate(loadClass(seiName));
         fail("Test should have failed");
      }
      catch (IllegalArgumentException iae)
      {
         //pass
      }
      catch (WSException ise)
      {
         //pass
      }
   }
}
