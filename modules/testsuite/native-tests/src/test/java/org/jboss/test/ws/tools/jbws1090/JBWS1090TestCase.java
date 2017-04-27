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
package org.jboss.test.ws.tools.jbws1090;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.jboss.util.xml.DOMUtils;
import org.jboss.ws.tools.WSTools;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.IOUtils;
import org.w3c.dom.Element;

/**
 * Test append web service descriptions to existing webservices.xml (JBWS-1090)
 * 
 * @author <a href="mailto:alex.guizar@jboss.com">Alejandro Guizar</a>
 */
public class JBWS1090TestCase extends JBossWSTest
{
   public void testWebServicesAppend() throws Exception
   {
      // create output dir
      File resourceDir = createResourceFile("tools/jbws1090");      
      resourceDir.mkdirs();
      
      // copy webservices.xml fixture to output folder 
      FileInputStream src = new FileInputStream(getResourceFile("tools/metadatafixture/webservices.xml").getPath());
      FileOutputStream dest = new FileOutputStream(createResourceFile("tools/jbws1090/webservices.xml"));
      IOUtils.copyStream(dest, src);
      src.close();
      dest.close();

      // run wstools
      String[] args = { "-dest", "tools/jbws1090", "-config", getResourceFile("tools/jbws1090/wstools-config.xml").getPath() };
      new WSTools().generate(args);

      Element expected = DOMUtils.parse(new FileInputStream(getResourceFile("tools/jbws1090/webservices.xml").getPath()));
      Element was = DOMUtils.parse(new FileInputStream(getResourceFile("tools/jbws1090/webservices.xml")));
      assertEquals(expected, was);
   }
}
