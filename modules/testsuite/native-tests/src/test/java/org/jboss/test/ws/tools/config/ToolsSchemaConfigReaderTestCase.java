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
package org.jboss.test.ws.tools.config;

import java.io.IOException;
import java.util.List;

import org.jboss.ws.tools.Configuration;
import org.jboss.ws.tools.WSTools;
import org.jboss.ws.tools.Configuration.JavaToWSDLConfig;
import org.jboss.ws.tools.Configuration.OperationConfig;
import org.jboss.ws.tools.Configuration.WSDLToJavaConfig;
import org.jboss.ws.tools.config.ToolsSchemaConfigReader;
import org.jboss.wsf.test.JBossWSTest;

/**
 *  TestCase that tests the parsing of the xml configuration file
 *  used as input to jbossws
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 11, 2005
 */
public class ToolsSchemaConfigReaderTestCase extends JBossWSTest
{
   public void testJavaToWSDL() throws Exception
   {
      String j2wConfigFile = getResourceFile("tools/config/java2wsdl.xml").getAbsolutePath();
      ToolsSchemaConfigReader configReader = new ToolsSchemaConfigReader();
      Configuration config = configReader.readConfig(j2wConfigFile);
      assertNotNull("config is null?", config);
      checkJavaToWSDL(config);
   }

   public void testWSDLToJava() throws Exception
   {
      String w2jConfigFile = getResourceFile("tools/config/wsdl2java.xml").getAbsolutePath();
      ToolsSchemaConfigReader configReader = new ToolsSchemaConfigReader();
      Configuration config = configReader.readConfig(w2jConfigFile);
      assertNotNull("config is null?", config);
      checkWSDLToJava(config);
   }

   // [JBWS-719] Enable schema validation on wstools config
   public void testInvalidConfig() throws IOException, ClassNotFoundException
   {
      String[] args = new String[] { "-dest", "tools/globalconfig", "-config", getResourceFile("tools/config/invalidConfig.xml").getAbsolutePath() };
      WSTools tools = new WSTools();
      try
      {
         tools.generate(args);
         fail("Expected to fail on invalid config");
      }
      catch (Exception ex)
      {
         String msg = ex.getMessage();
         assertTrue("Unexpected message: " + msg, msg.indexOf("service-typo") > 0);
      }
   }

   private void checkJavaToWSDL(Configuration config)
   {
      JavaToWSDLConfig j2wc = config.getJavaToWSDLConfig(false);
      assertNotNull("JavaToWSDLConfig is null?", j2wc);
      //Assert the Service Element
      assertEquals("HelloService", j2wc.serviceName);
      assertEquals("org.jboss.test.HelloRemote", j2wc.endpointName);
      assertEquals("document", j2wc.wsdlStyle);
      assertEquals("http://jboss.org/types", j2wc.targetNamespace);
      assertEquals("http://jboss.org/types", j2wc.typeNamespace);
      assertTrue(j2wc.mappingFileNeeded);
      assertEquals("mapping.xml", j2wc.mappingFileName);
      assertEquals("MyServlet", j2wc.servletLink);
      //Assert the Operation element
      assertTrue(j2wc.operations.size() > 0);
      OperationConfig opc = (OperationConfig)((List)j2wc.operations.get("helloString")).get(0);
      assertEquals("helloString", opc.name);
      assertTrue(opc.isOneWay);
   }

   private void checkWSDLToJava(Configuration config)
   {
      WSDLToJavaConfig w2jc = config.getWSDLToJavaConfig(false);
      assertNotNull("WSDLToJavaConfig is null?", w2jc);
      assertEquals("dummylocation", w2jc.wsdlLocation);
   }
}
