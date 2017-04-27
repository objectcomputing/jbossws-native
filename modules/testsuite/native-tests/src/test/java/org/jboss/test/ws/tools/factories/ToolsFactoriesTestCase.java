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
package org.jboss.test.ws.tools.factories;

import junit.framework.TestCase;

import org.jboss.ws.tools.JavaToXSD;
import org.jboss.ws.tools.exceptions.JBossWSToolsException;
import org.jboss.ws.tools.factories.JavaToXSDFactory;
import org.jboss.ws.tools.interfaces.JavaToXSDIntf;
 
/**
 * Tests the various factories under JBossWS Tools 
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jul 23, 2005 
 */

public class ToolsFactoriesTestCase extends TestCase
{
   public void testJavaToXSDFactory() throws JBossWSToolsException
   {
      String nsuri = "http://jboss.org/test";
      JavaToXSDFactory fact = JavaToXSDFactory.newInstance();
      assertNotNull("JavaToXSDFactory is null?",fact);
      assertTrue("JavaToXSDFactory is the right class?", fact instanceof JavaToXSDFactory);
      JavaToXSDIntf sj = fact.getJavaToXSD(nsuri,nsuri);
      assertTrue("JavaToXSD is the right class?", sj instanceof JavaToXSD);
   } 
}

