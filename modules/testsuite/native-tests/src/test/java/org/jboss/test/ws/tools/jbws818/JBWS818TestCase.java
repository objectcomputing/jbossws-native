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
package org.jboss.test.ws.tools.jbws818;

import java.io.FileInputStream;

import org.jboss.ws.tools.WSTools;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/**
 * Test cross package inheritence (JBWS-818)
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class JBWS818TestCase extends JBossWSTest
{

   //Set up the test
   protected void setUp()
   {
   }

   public final void testEnumWsdl() throws Exception
   {
      String config = getResourceFile("tools/jbws818/wstools-config.xml").getAbsolutePath();
      String[] args= new String[]{"-dest", "tools/jbws818", "-config", config};
      new WSTools().generate(args);

      Element exp = DOMUtils.parse(new FileInputStream(getResourceFile("tools/jbws818/CrossPackageInheritenceService.wsdl").getAbsolutePath()));
      Element was = DOMUtils.parse(new FileInputStream("tools/jbws818/wsdl/CrossPackageInheritenceService.wsdl"));
      assertEquals(exp, was);
   }
}
