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
package org.jboss.test.ws.tools.validation;

import java.io.File;

import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.tools.exceptions.JBossWSToolsException;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.wsf.test.JBossWSTest;

/**
 *  Testcase to test the WSDL Validator
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 1, 2005
 */
public class WSDL11ValidationTestCase extends JBossWSTest
{
   /**
    * Test equality of the same wsdl through the wsdlvalidator
    * @throws Exception
    */
   public void testSimpleCase() throws Exception
   {
      //File wsdlFile = getResourceFile("wsdl11/DocLitSimple.wsdl");
      File wsdlFile = getResourceFile("tools/wsdlfixture/CustomInterfaceService_RPC_11.wsdl");
      assertTrue(wsdlFile.exists());

      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlExp = factory.parse(wsdlFile.toURL());

      WSDLDefinitions wsdlActual = factory.parse(wsdlFile.toURL());
      WSDLValidator validator = new WSDL11Validator();
      try
      {
         boolean bool = validator.validate(wsdlExp, wsdlActual);
         if (bool == false)
            fail("WSDL do not match");
      }
      catch (JBossWSToolsException e)
      {
         fail(e.getLocalizedMessage());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail("Unknown exception:" + e.getLocalizedMessage());
      }
   }

   /**
    * Test equality of the same wsdl through the wsdlvalidator
    * @throws Exception
    */
   public void testDocLitCase() throws Exception
   {
      File wsdlFile = getResourceFile("common/wsdl11/DocLitSimple.wsdl");
      assertTrue(wsdlFile.exists());

      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      WSDLDefinitions wsdlExp = factory.parse(wsdlFile.toURL());

      WSDLDefinitions wsdlActual = factory.parse(wsdlFile.toURL());
      WSDLValidator validator = new WSDL11Validator();
      try
      {
         boolean bool = validator.validate(wsdlExp, wsdlActual);
         if (bool == false)
            fail("WSDL do not match");
      }
      catch (JBossWSToolsException e)
      {
         fail(e.getLocalizedMessage());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail("Unknown exception:" + e.getLocalizedMessage());
      }
   }
}
