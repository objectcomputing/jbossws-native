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
package org.jboss.ws.extensions.security;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.jboss.ws.metadata.wsse.Config;
import org.jboss.ws.metadata.wsse.WSSecurityConfiguration;

/**
 * WS-Security functionalities interface
 * 
 * @author alessio.soldano@jboss.com
 * @since 06-Mar-2008
 */
public interface WSSecurityAPI
{
   /**
    * Decodes a message using the specified configuration. This includes
    * unencrypt, signature verification, etc. according to the requirements.
    * 
    * @param configuration
    *           The WSSecurityConfiguration to use
    * @param message
    *           The message that needs to be handled
    * @param operationConfig
    *           The config object defining the operation (requirement checks)
    *           that should be performed on this message; overrides the default
    *           config contained in the provided WSSecurityConfiguration.
    * @throws SOAPException
    */
   public void decodeMessage(WSSecurityConfiguration configuration, SOAPMessage message, Config operationConfig) throws SOAPException;

   /**
    * Encodes a message using the specified configuration. This includes
    * encrypt, signature, username profile, etc. according to the config.
    * 
    * @param configuration
    *           The WSSecurityConfiguration to use
    * @param message
    *           The message that needs to be handled
    * @param operationConfig
    *           The config object defining the operation that should be
    *           performed on this message; overrides the default config
    *           contained in the provided WSSecurityConfiguration.
    * @param user
    *           The username to use if Username Token is needed.
    * @param password
    *           The password to use if Username Token is needed.
    * @throws SOAPException
    */
   public void encodeMessage(WSSecurityConfiguration configuration, SOAPMessage message, Config operationConfig, String user, String password) throws SOAPException;
   
   /**
    * Cleanup shared resources 
    */
   public void cleanup();
   
}
