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
package org.jboss.test.ws.jaxrpc.jbws637;

import javax.naming.InitialContext;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;



/**
 * Multiple bindings are not supported
 * 
 * http://jira.jboss.org/jira/browse/JBWS-637
 *
 * @author Thomas.Diesler@jboss.org
 * @since 21-Jan-2006
 */
public class JBWS637TestCase extends JBossWSTest
{
   private static CheckSoap port;
   
   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS637TestCase.class, "jaxrpc-jbws637-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         InitialContext iniCtx = getInitialContext();
         Check service = (Check)iniCtx.lookup("java:comp/env/service/Check");
         port = service.getCheckSoap();
      }
   }

   public void testCreateService() throws Exception
   {
      assertNotNull("port not null", port);
   }
   
   public void _testSuggestWord() throws Exception
   {
      String retStr = port.suggestWord("table");
      assertTrue(retStr.startsWith("Word already in CDYNE's Database."));
   }
   
   public void _testCheckTextBody() throws Exception
   {
      String pangram = "Six big devils from Japan quickly forgot how to waltz.";
      DocumentSummary sum = port.checkTextBody(pangram, "0");
      assertEquals(pangram, sum.getBody());
   }
}
