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

import java.io.File;

import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.tools.WSDLToJava;
import org.jboss.ws.tools.interfaces.WSDLToJavaIntf;
import org.jboss.wsf.test.JBossWSTest;

/** Testcase that tests WSDL 2.0 Assertions for the JBossWS Tools project
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Feb 4, 2005
 */

public class WSDL20AssertionsTestCase extends JBossWSTest
{
   /**
    * WSDL2.0 Assertions (http://www.w3.org/TR/wsdl20-primer)
    * The value of the WSDL target namespace MUST be an absolute URI.
    */
   public void testWSDLTargetNameSpace()
   {
      doWSDLTest("WSDLTargetNS20.wsdl");
   }

   public void testUniqueInterfaceName()
   {
      doWSDLTest("UniqueInterfaceName.wsdl");
   }

   public void testUniqueInterfaceOperationName()
   {
      doWSDLTest("UniqueInterfaceOperationName.wsdl");
   }

   public void testUniqueBindingName()
   {
      doWSDLTest("UniqueBindingName.wsdl");
   }

   private void doWSDLTest(String wsdlname)
   {
      // System property passed through the ant build script
      String wsdldir = System.getProperty("wsdl.dir", getResourceFile("tools").getAbsolutePath());

      File wsdlFile = createResourceFile(wsdldir + "/wsdl20assertions/" + wsdlname);
 
      WSDLToJavaIntf wsdljava = new WSDLToJava();
      try
      {
         WSDLDefinitions wsdl = wsdljava.convertWSDL2Java(wsdlFile.toURL());
         fail("Test Should not have passed");
      }
      catch (RuntimeException ie)
      {
         // ignore expected exception
      }
      catch (Exception e)
      {
         fail(e.getLocalizedMessage());
      }

   }
}

