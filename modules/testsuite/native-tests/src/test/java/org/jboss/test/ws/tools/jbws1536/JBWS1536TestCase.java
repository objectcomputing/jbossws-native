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
package org.jboss.test.ws.tools.jbws1536;

import java.io.FileInputStream;

import org.jboss.util.xml.DOMUtils;
import org.jboss.ws.tools.WSTools;
import org.jboss.wsf.test.JBossWSTest;
import org.w3c.dom.Element;

/**
 * [JBWS-1536] NPE in WSDL to Java when webservices.xml requested but no global package mapping
 * 
 * @see http://jira.jboss.com/jira/browse/JBWS-1536
 * 
 * @author darran.lofthouse@jboss.com
 * @since Aug 1, 2007
 */
public class JBWS1536TestCase extends JBossWSTest
{

   public final void testGenerate() throws Exception
   {
      String resourceDir = createResourceFile("tools/jbws1536").getAbsolutePath();
      String toolsDir = resourceDir; //"tools/jbws1536";
      String[] args = new String[] { "-dest", toolsDir, "-config", resourceDir + "/wstools-config.xml" };

      new WSTools().generate(args);
      
      Element expected = DOMUtils.parse(new FileInputStream(getResourceFile("tools/jbws1536/webservices.xml").getAbsolutePath()));
      Element was = DOMUtils.parse(new FileInputStream(getResourceFile("tools/jbws1536/webservices.xml")));
      assertEquals(expected, was);      
   }

}
