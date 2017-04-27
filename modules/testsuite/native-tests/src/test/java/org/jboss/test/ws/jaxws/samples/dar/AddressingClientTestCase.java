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
package org.jboss.test.ws.jaxws.samples.dar;

import java.net.URL;
import java.util.Date;

import javax.management.ObjectName;

import junit.framework.Test;

import org.jboss.wsf.common.ObjectNameFactory;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * DAR addressing client; invokes the DAR addressing endpoint (sync, asynch and oneway)
 *
 * @author Thomas.Diesler@jboss.org
 * @since 24-Nov-2005
 */
public class AddressingClientTestCase extends JBossWSTest
{
   private static final int TEST_RUN_TIME = 6000;
   
   public static Test suite()
   {
      return new JBossWSTestSetup(AddressingClientTestCase.class, "jaxws-samples-dar-addressing-client.war,jaxws-samples-dar-addressing.jar");
   }

   public void testSync() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/dar?wsdl");
      AddressingClient client = new AddressingClient(wsdlURL, getServerHost());
      Date start = new Date();
      client.run(false);
      Date stop = new Date();
      assertTrue(stop.getTime() - start.getTime() > TEST_RUN_TIME / 2);
   }
   
   public void testAsync() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/dar?wsdl");
      AddressingClient client = new AddressingClient(wsdlURL, getServerHost());
      Date start = new Date();
      client.run(true);
      Date stop = new Date();
      assertTrue(stop.getTime() - start.getTime() > TEST_RUN_TIME / 2);
   }
   
   public void testOneWay() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/dar?wsdl");
      long count = getRequestCount("dar-client", "ReplyService");
      AddressingClient client = new AddressingClient(wsdlURL, getServerHost());
      Date start = new Date();
      client.runOneway();
      Date stop = new Date();
      assertTrue(stop.getTime() - start.getTime() < TEST_RUN_TIME / 2);
      Thread.sleep(TEST_RUN_TIME);
      assertEquals(count + 1, getRequestCount("dar-client", "ReplyService"));
   }
   
   private long getRequestCount(String context, String endpoint) throws Exception
   {
      ObjectName oname = ObjectNameFactory.create("jboss.ws:context=" + context + ",endpoint=" + endpoint);
      return (Long)getServer().getAttribute(oname, "RequestCount");
   }
}
