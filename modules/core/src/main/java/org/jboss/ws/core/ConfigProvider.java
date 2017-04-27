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
package org.jboss.ws.core;

/**
 * Provides configuration for JAXRPC/JAXWS ports
 *  
 * @author Thomas.Diesler@jboss.com
 * @since 17-Jan-2006
 */
public interface ConfigProvider
{
   /** 
    * Get the port configuration file 
    */
   String getConfigFile();

   /** 
    * Get the port configuration name 
    */
   String getConfigName();

   /** 
    * Set the port configuration name 
    */
   void setConfigName(String configName);

   /** 
    * Set the port configuration name and file
    */
   void setConfigName(String configName, String configFile);

   /** 
    * Get the WS-Security configuration  
    */
   String getSecurityConfig();

   /** 
    * Set the WS-Security configuration  
    */
   void setSecurityConfig(String securityConfig);
}
