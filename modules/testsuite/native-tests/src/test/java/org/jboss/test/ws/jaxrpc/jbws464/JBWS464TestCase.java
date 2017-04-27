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
package org.jboss.test.ws.jaxrpc.jbws464;

import java.rmi.RemoteException;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;



/**
 * SAAJ: SOAPEnvelope.getOwnerDocument() returns null
 * 
 * http://jira.jboss.com/jira/browse/JBWS-464
 * 
 * @author <a href="mailto:anil.saldhana@jboss.com">Anil Saldhana</a>
 * @since 24-Oct-2005
 */
public class JBWS464TestCase extends JBossWSTest
{
   private static Hello hello;

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS464TestCase.class, "jaxrpc-jbws464.war, jaxrpc-jbws464-client.jar");
   }
   
   public void setUp() throws Exception
   {
      super.setUp();
      if (hello == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/HelloService");
         hello = (Hello)service.getPort(Hello.class);
      }
   }
   
   public void testValidAccess() throws Exception
   {  
      String retObj = hello.hello("Hello Server");
      assertEquals("Hello Server", retObj); 
   }


   // This tests access from the SOAPFault message */ 
   public void testInvalidAccess() throws Exception
   {  
      try
      {
        hello.hello(null);
        fail("Test should have thrown an exception");
      }catch(RemoteException e)
      {
         //pass
      }
   } 
}
