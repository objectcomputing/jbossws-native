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
package org.jboss.test.ws.jaxrpc.jbws1316;

import java.io.File;
import java.io.IOException;

import org.jboss.ws.metadata.wsse.TimestampVerification;
import org.jboss.ws.metadata.wsse.WSSecurityConfiguration;
import org.jboss.ws.metadata.wsse.WSSecurityOMFactory;
import org.jboss.wsf.test.JBossWSTest;

/**
 * Test case to test reading the TimestampVerification configuration.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 * @since Aril 14 2008
 */
public class JBWS1316ConfigurationTestCase extends JBossWSTest
{

   private WSSecurityConfiguration load(final String fileName) throws IOException
   {
      File configFile = getResourceFile("jaxrpc/jbws1316/config/" + fileName);
      WSSecurityOMFactory factory = WSSecurityOMFactory.newInstance();

      return factory.parse(configFile.toURL());
   }

   /**
    * Test loading a wsse configuration with no timestamp-verification
    * element.
    * 
    * This test case verifies that no TimestampVerification will be set
    * and matches the scenario that would be encountered when reading
    * existing descriptors.
    */
   public void testLoadNoTimestampVerification() throws Exception
   {
      WSSecurityConfiguration config = load("jboss-wsse-no-tv.xml");
      assertNull("No TimestampVerification expected.", config.getTimestampVerification());
   }

   /**
    * Test loading a wsse configuration with an empty timestamp-verification
    * element.
    * 
    * This test case verifies the default values that will be used when missing from
    * the timestamp-verification element.
    */
   public void testLoadEmptyTimestampVerification() throws Exception
   {
      WSSecurityConfiguration config = load("jboss-wsse-empty-tv.xml");
      assertNotNull("TimestampVerification Missing", config.getTimestampVerification());

      TimestampVerification tv = config.getTimestampVerification();
      assertEquals("Expected 'createdTolerance' to be '0'", 0, tv.getCreatedTolerance());
      assertTrue("Expected 'warnCreated' to default to 'true'", tv.isWarnCreated());
      assertEquals("Expected 'expiresTolerance' to be '0'", 0, tv.getExpiresTolerance());
      assertTrue("Expected 'warnExpires' to default to 'true'", tv.isWarnExpires());
   }

   /**
    * Test loading a wsse configuration with a full timestamp-verification
    * element.
    * 
    * This test case verifies all the values are correctly loaded from the
    * timestamp-verification element.
    */
   public void testLoadFullTimestampVerification() throws Exception
   {
      WSSecurityConfiguration config = load("jboss-wsse-full-tv.xml");
      assertNotNull("TimestampVerification Missing", config.getTimestampVerification());

      TimestampVerification tv = config.getTimestampVerification();
      assertEquals("Expected 'createdTolerance' to be '5'", 5, tv.getCreatedTolerance());
      assertFalse("Expected 'warnCreated' to default to 'false'", tv.isWarnCreated());
      assertEquals("Expected 'expiresTolerance' to be '10'", 10, tv.getExpiresTolerance());
      assertFalse("Expected 'warnExpires' to default to 'false'", tv.isWarnExpires());
   }

}
