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
package org.jboss.test.ws.jaxws.jbws1857;

import java.io.File;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.jboss.test.ws.jaxws.jbws1857.types.Stammdaten;
import org.jboss.ws.core.jaxws.JAXBContextFactory;
import org.jboss.wsf.test.JBossWSTest;

/**
 * [JBWS-1857] JAXBContext created for every wrapper type
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1857
 *
 * @author Thomas.Diesler@jboss.com
 * @since 28-Feb-2008
 */
public class JBWS1857TestCase extends JBossWSTest
{
   public void setUp() throws Exception
   {
      JAXBContextFactory.resetContextCount();
   }
   
   public void testPortCreation() throws Exception
   {
      File wsdlFile = getResourceFile("jaxws/jbws1857/StammdatenService.wsdl");
      QName serviceName = new QName("http://example.com", "StammdatenService");
      Service service = Service.create(wsdlFile.toURL(), serviceName);
      
      service.getPort(Stammdaten.class);
      Integer ctxCount = JAXBContextFactory.getContextCount();
      assertTrue("Too many JAXB context: " + ctxCount, ctxCount < 3);
   }
}
