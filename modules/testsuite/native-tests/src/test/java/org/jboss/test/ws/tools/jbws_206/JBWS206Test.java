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
package org.jboss.test.ws.tools.jbws_206;

import java.io.File;

import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.ws.tools.WSTools;

/**
 *  Base class for the
 *  JBWS-206: WSDL 1.1 to Java Test Collection
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Sep 25, 2005
 */
public abstract class JBWS206Test extends WSToolsBase
{
   public abstract String getBase();

   public abstract String getFixMe();

   public abstract void checkGeneratedUserTypes() throws Exception;

   public abstract String getSEIName();

   public abstract String getServiceName();

   //Set up the test
   protected void setUp()
   {
      String out_dir = "tools/jbws-206/jbossws/" + getBase();
      mkdirs(out_dir + "/client");
   }

   public final void checkSourceFiles(File file1, File file2) throws Exception
   {
      assertExactSourceFiles(file1, file2);
   }

   public void checkServiceEndpointInterface() throws Exception
   {
      String out_dir = getResourceFile("tools/jbws-206/jbossws/" + getBase()).getAbsolutePath();
      String fixBase = getResourceFile("tools/jbws-206/wscompileArtifacts/" + getBase()).getAbsolutePath();

      String packageDir = "org/jboss/test/webservice/" + getBase().toLowerCase();
      String seiName = getSEIName() + ".java";
      String sei = fixBase + "/sei/" + seiName;
      //    Check the sei
      // assertExactSourceFiles(createResourceFile(sei), createResourceFile(out_dir + "/client/" + packageDir + "/" + seiName ));
      assertExactSourceFiles(new File(sei), new File(out_dir + "/" + packageDir + "/" + seiName));
   }

   public void checkServiceInterface() throws Exception
   {
      String out_dir = getResourceFile("tools/jbws-206/jbossws/" + getBase()).getAbsolutePath();
      String fixBase = getResourceFile("tools/jbws-206/wscompileArtifacts/" + getBase()).getAbsolutePath();

      String packageDir = "org/jboss/test/webservice/" + getBase().toLowerCase();
      String serviceName = getServiceName();

      //    Check the Service File
      assertExactSourceFiles(new File(fixBase + "/service/" + serviceName + ".java"), new File(out_dir + "/" + packageDir + "/" + serviceName + ".java"));
      //createResourceFile(out_dir + "/client/" + packageDir + "/" + serviceName + ".java" ));
   }

   public final void checkUserType(String name) throws Exception
   {
      String out_dir = getResourceFile("tools/jbws-206/jbossws/" + getBase()).getAbsolutePath();
      String fixBase = getResourceFile("tools/jbws-206/wscompileArtifacts/" + getBase()).getAbsolutePath();
      String packageDir = "org/jboss/test/webservice/" + getBase().toLowerCase();

      //    Check User Types
      assertExactSourceFiles(new File(fixBase + "/usertypes/" + name), new File(out_dir + "/" + packageDir + "/" + name));
      //createResourceFile(out_dir + "/client/" + packageDir + "/" + name ));
   }

   protected final void generate() throws Exception
   {
      String out_dir = getResourceFile("tools/jbws-206/jbossws/" + getBase()).getAbsolutePath();
      String configloc = getResourceFile("tools/jbws-206/jbosswsConfig/" + getBase() + "/" + getBase() + "wsdl2java.xml").getAbsolutePath();

      String[] args = new String[] { "-dest", out_dir, "-config", configloc };
      WSTools tools = new WSTools();
      tools.generate(args);
      checkServiceEndpointInterface();
      checkServiceInterface();
      checkGeneratedUserTypes();
   }

   public final void testWSDL2Java() throws Exception
   {
      if (getFixMe() != null)
      {
         System.out.println(getFixMe());
         return;
      }
      generate();
   }

}
