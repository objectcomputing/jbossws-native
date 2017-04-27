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
package org.jboss.test.ws.tools.jbpapp921;

import java.io.File;

import org.jboss.test.ws.tools.fixture.JBossSourceComparator;
import org.jboss.test.ws.tools.validation.JaxrpcMappingValidator;
import org.jboss.ws.tools.WSTools;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.test.JBossWSTest;
import org.w3c.dom.Element;

public class JBPAPP921TestCase extends JBossWSTest
{
   private static final String resourceDir = "tools/jbpapp921";
   private static final String toolsDir = "target/wstools/jbpapp921/output";

   public void testGenerate() throws Exception
   {
      String realResourceDir = getResourceFile("tools/jbpapp921").getAbsolutePath();
      String[] args = new String[] { "-dest", toolsDir, "-config", realResourceDir + "/wstools-config.xml" };
      new WSTools().generate(args);
      compareSource("Models_ServiceIM_ServiceProxyService.java");
      compareSource("ValidationEvent_test.java");

      JaxrpcMappingValidator mappingValidator = new JaxrpcMappingValidator();
      File jaxrpcMapping = getResourceFile(resourceDir + "/jaxrpc-mapping.xml");
      mappingValidator.validate(jaxrpcMapping.getAbsolutePath(), toolsDir + "/jaxrpc-mapping.xml");

      Element exp = DOMUtils.parse(getResourceFile(resourceDir + "/webservices.xml").toURL().openStream());
      Element act = DOMUtils.parse(new File(toolsDir + "/webservices.xml").toURL().openStream());
      assertEquals(exp, act);
   }

   private void compareSource(final String fileName) throws Exception
   {
      File expected = getResourceFile(resourceDir + "/" + fileName);
      File generated = new File(toolsDir + "/org/jboss/test/ws/jbpapp921/" + fileName);

      JBossSourceComparator sc = new JBossSourceComparator(expected, generated);
      sc.validate();
      sc.validateImports();
   }
}
