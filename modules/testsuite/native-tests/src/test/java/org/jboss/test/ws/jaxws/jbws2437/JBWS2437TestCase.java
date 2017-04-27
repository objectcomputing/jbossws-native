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
package org.jboss.test.ws.jaxws.jbws2437;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;


/**
 * Disallow access to directories other than "data/wsdl"
 * 
 * http://jira.jboss.org/jira/browse/JBWS-2437
 *
 * @author mageshbk@jboss.com
 * @since 04-Jan-2009
 */
public class JBWS2437TestCase extends JBossWSTest
{
   public final String WSDL_LOCATION = "http://" + getServerHost() + ":8080/jaxws-jbws2437?wsdl";
   public final String WSDL_RESOURCE = "&resource=../../ejb-deployer.xml";

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS2437TestCase.class, "jaxws-jbws2437.jar");
   }

   public void testWSDLAccess() throws Exception
   {
      HttpURLConnection connection = (HttpURLConnection)new URL(WSDL_LOCATION).openConnection();
      InputStream in = connection.getInputStream();
      int fileSize = in.available();
      in.close();
      assertTrue("WSDL cannot be accessed", fileSize > 0);
   }

   public void testOtherFileAccess() throws Exception
   {
      HttpURLConnection connection = (HttpURLConnection)new URL(WSDL_LOCATION + WSDL_RESOURCE).openConnection();
      InputStream in = connection.getInputStream();
      int fileSize = in.available();
      in.close();
      assertTrue("Unrestricted access to xml files found", fileSize == 0);
   }
}
