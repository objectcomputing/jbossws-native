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
package org.jboss.test.ws.tools.jbws2411;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.wsdl.Definition; 
import javax.wsdl.WSDLException;

import org.jboss.wsf.test.JBossWSTest;

import org.jboss.ws.tools.wsdl.WSDL11DefinitionFactory;
import org.jboss.ws.tools.wsdl.WSDL11Reader;

/**
 * [JBWS-2411] OutOfMemoryExecption in WSDL11Reader when loading xml schemas with circular references
 *
 * @author ropalka@redhat.com
 */
public class JBWS2411TestCase extends JBossWSTest
{

   public void testIssue() throws Exception
   {
      URL wsdlFile = getResourceFile("tools/jbws2411/wsdl/PRPA_AR201102UV01.wsdl").toURL();

      WSDL11DefinitionFactory factory = WSDL11DefinitionFactory.newInstance();
      Definition impWsdl = factory.parse(wsdlFile);

      WSDL11Reader reader = new WSDL11Reader();
      reader.processDefinition(impWsdl, wsdlFile);
   }

}
