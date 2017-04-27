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
package org.jboss.test.ws.jaxrpc.jbws958;

import java.io.File;
import java.net.URL;

import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.wsf.test.JBossWSTest;

/**
 * XML entity usage in wsdl contained schema
 * 
 * http://jira.jboss.org/jira/browse/JBWS-958
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Oct-2006
 */
public class JBWS958TestCase extends JBossWSTest
{
   public void testWSDLReader() throws Exception
   {
      WSDLDefinitionsFactory factory = WSDLDefinitionsFactory.newInstance();
      URL wsdlURL = getResourceURL("jaxrpc/jbws958/WEB-INF/wsdl/IPLSProvisioning.wsdl");
      WSDLDefinitions wsdlDefs = factory.parse(wsdlURL);
      assertNotNull(wsdlDefs);
   }
}
