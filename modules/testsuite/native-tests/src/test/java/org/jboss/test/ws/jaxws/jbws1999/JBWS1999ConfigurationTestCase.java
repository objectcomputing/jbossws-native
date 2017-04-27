/*
* JBoss, Home of Professional Open Source.
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.ws.jaxws.jbws1999;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.ws.metadata.wsse.Authorize;
import org.jboss.ws.metadata.wsse.Config;
import org.jboss.ws.metadata.wsse.Port;
import org.jboss.ws.metadata.wsse.Role;
import org.jboss.ws.metadata.wsse.WSSecurityConfiguration;
import org.jboss.ws.metadata.wsse.WSSecurityOMFactory;
import org.jboss.wsf.test.JBossWSTest;

/**
 * Test case to test reading the 'authorize' configuration.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 * @since December 18th 2008
 */
public class JBWS1999ConfigurationTestCase extends JBossWSTest
{

   private WSSecurityConfiguration load(final String fileName) throws IOException
   {
      File configFile = getResourceFile("jaxws/jbws1999/config/" + fileName);
      WSSecurityOMFactory factory = WSSecurityOMFactory.newInstance();

      return factory.parse(configFile.toURL());
   }

   /**
    * Test loading a configuration with a default 'authorize' definition
    * which contains two roles.
    */
   public void testDefaultRoles() throws Exception
   {
      WSSecurityConfiguration wsConfig = load("jboss-wsse-default-roles.xml");

      Config config = wsConfig.getDefaultConfig();
      Authorize authorize = config.getAuthorize();
      assertFalse("Unchecked", authorize.isUnchecked());
      List<Role> roles = authorize.getRoles();

      assertEquals("Expected 2 roles", 2, roles.size());

      List<String> roleNames = new ArrayList<String>(roles.size());
      for (Role current : roles)
      {
         roleNames.add(current.getName());
      }
      assertTrue("Expected 'Trader' role.", roleNames.contains("Trader"));
      assertTrue("Expected 'Banker' role.", roleNames.contains("Banker"));
   }

   /**
    * Test loading a configuration with a port 'authorize' definition
    * which contains two roles.
    */
   public void testPortRoles() throws Exception
   {
      WSSecurityConfiguration wsConfig = load("jboss-wsse-port-roles.xml");

      Port port = wsConfig.getPorts().get("TestPort");
      Config config = port.getDefaultConfig();
      Authorize authorize = config.getAuthorize();
      assertFalse("Unchecked", authorize.isUnchecked());
      List<Role> roles = authorize.getRoles();

      assertEquals("Expected 2 roles", 2, roles.size());

      List<String> roleNames = new ArrayList<String>(roles.size());
      for (Role current : roles)
      {
         roleNames.add(current.getName());
      }
      assertTrue("Expected 'Trader' role.", roleNames.contains("Trader"));
      assertTrue("Expected 'Banker' role.", roleNames.contains("Banker"));
   }

   /**
    * Test loading a configuration with a default 'authorize' definition
    * which contains one role.
    */
   public void testDefaultRole() throws Exception
   {
      WSSecurityConfiguration wsConfig = load("jboss-wsse-default-role.xml");

      Config config = wsConfig.getDefaultConfig();
      Authorize authorize = config.getAuthorize();
      assertFalse("Unchecked", authorize.isUnchecked());
      List<Role> roles = authorize.getRoles();

      assertEquals("Expected 1 roles", 1, roles.size());

      Role role = roles.get(0);
      assertEquals("Expected 'Trader' role.", "Trader", role.getName());
   }

   /**
    * Test loading a configuration with a port 'authorize' definition
    * which contains one role.
    */
   public void testPortRole() throws Exception
   {
      WSSecurityConfiguration wsConfig = load("jboss-wsse-port-role.xml");

      Port port = wsConfig.getPorts().get("TestPort");
      Config config = port.getDefaultConfig();
      Authorize authorize = config.getAuthorize();
      assertFalse("Unchecked", authorize.isUnchecked());
      List<Role> roles = authorize.getRoles();

      assertEquals("Expected 1 roles", 1, roles.size());

      Role role = roles.get(0);
      assertEquals("Expected 'Trader' role.", "Trader", role.getName());
   }

   /**
    * Test loading a configuration with a default 'authorize' definition
    * with unchecked.
    */
   public void testDefaultUnchecked() throws Exception
   {
      WSSecurityConfiguration wsConfig = load("jboss-wsse-default-unchecked.xml");

      Config config = wsConfig.getDefaultConfig();
      Authorize authorize = config.getAuthorize();
      assertTrue("Unchecked", authorize.isUnchecked());
      List<Role> roles = authorize.getRoles();

      assertEquals("Expected 0 roles", 0, roles.size());
   }

   /**
    * Test loading a configuration with a port 'authorize' definition
    * with unchecked.
    */
   public void testPortUnchecked() throws Exception
   {
      WSSecurityConfiguration wsConfig = load("jboss-wsse-port-unchecked.xml");

      Port port = wsConfig.getPorts().get("TestPort");
      Config config = port.getDefaultConfig();
      Authorize authorize = config.getAuthorize();
      assertTrue("Unchecked", authorize.isUnchecked());
      List<Role> roles = authorize.getRoles();

      assertEquals("Expected 0 roles", 0, roles.size());
   }

   /**
    * Test loading a configuration with a default 'authorize' definition
    * with unchecked and a role defined, parsing should fail.
    */
   public void testDefaultRoleUnchecked() throws Exception
   {
      try
      {
         WSSecurityConfiguration wsConfig = load("jboss-wsse-default-role-unchecked.xml");
         fail("Expected exception not thrown.");
      }
      catch (IOException expected)
      {
         Throwable cause = expected.getCause();
         assertEquals(IllegalStateException.class, cause.getClass());
      }
   }

   /**
    * Test loading a configuration with a port 'authorize' definition
    * with unchecked and a role defined, parsing should fail.
    */
   public void testPortRoleUnchecked() throws Exception
   {
      try
      {
         WSSecurityConfiguration wsConfig = load("jboss-wsse-port-role-unchecked.xml");
         fail("Expected exception not thrown.");
      }
      catch (IOException expected)
      {
         Throwable cause = expected.getCause();
         assertEquals(IllegalStateException.class, cause.getClass());
      }
   }

}
