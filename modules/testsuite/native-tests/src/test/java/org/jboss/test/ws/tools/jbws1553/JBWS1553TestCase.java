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
package org.jboss.test.ws.tools.jbws1553;

import java.net.URL;

import javax.xml.namespace.QName;

import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;
import org.jboss.wsf.test.JBossWSTest;

/**
 * [JBWS-1553] fails to read operations for portType from different namespace
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1553
 * 
 * @author <a href="mailto:alex.guizar@jboss.com">Alejandro Guizar</a>
 */
public class JBWS1553TestCase extends JBossWSTest
{
   private WSDLDefinitions definitions;

   protected void setUp() throws Exception
   {
      URL wsdlLocation = getResourceURL("tools/jbws1553/atm-service.wsdl");
      definitions = WSDLDefinitionsFactory.newInstance().parse(wsdlLocation);
   }

   public void testPortType()
   {
      WSDLService wsdlService = definitions.getService("AtmFrontEndService");
      WSDLEndpoint wsdlEndpoint = wsdlService.getEndpoint(new QName("urn:samples:atm", "FrontEndPort"));
      assertEquals(new QName("urn:samples:atm2", "FrontEnd"), wsdlEndpoint.getInterface().getName());
   }
}
