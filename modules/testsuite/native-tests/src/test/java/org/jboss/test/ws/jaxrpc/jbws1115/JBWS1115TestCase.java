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
package org.jboss.test.ws.jaxrpc.jbws1115;

import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.ObjectNameFactory;

/**
 * Auto discover HTTP(S) port configuration from Tomcat
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1115
 * 
 * @author darran.lofthouse@jboss.com
 * @author Thomas.Diesler@jboss.com
 * @since 15-October-2006
 */
public class JBWS1115TestCase extends JBossWSTest
{
   private final ObjectName manager = ObjectNameFactory.create("jboss.ws:service=ServerConfig");

   public void testDiscoverWebServicePort() throws Exception
   {
      MBeanServerConnection server = getServer();

      String attrName = "WebServicePort";
      Integer orgPort = (Integer)server.getAttribute(manager, attrName);
      server.setAttribute(manager, new Attribute(attrName, new Integer(0)));
      Integer newPort = (Integer)server.getAttribute(manager, attrName);

      assertEquals(attrName, orgPort, newPort);
   }

   public void testDiscoverWebServiceSecurePort() throws Exception
   {
      MBeanServerConnection server = getServer();

      String attrName = "WebServiceSecurePort";
      Integer orgPort = (Integer)server.getAttribute(manager, attrName);
      server.setAttribute(manager, new Attribute(attrName, new Integer(0)));
      Integer newPort = (Integer)server.getAttribute(manager, attrName);

      assertEquals(attrName, orgPort, newPort);
   }
}
