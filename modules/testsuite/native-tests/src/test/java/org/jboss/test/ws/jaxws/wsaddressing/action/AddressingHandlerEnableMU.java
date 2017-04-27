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
package org.jboss.test.ws.jaxws.wsaddressing.action;

import java.net.URISyntaxException;

import javax.xml.namespace.QName;

import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.JAXWSAConstants;
import javax.xml.ws.addressing.soap.SOAPAddressingProperties;
import javax.xml.ws.handler.MessageContext;

import org.jboss.logging.Logger;
import org.jboss.wsf.common.handler.GenericHandler;

/**
 * A client side handler that disables mustUnderstand processing
 *
 * @author Thomas.Diesler@jboss.org
 * @since 29-Nov-2005
 */
public class AddressingHandlerEnableMU extends GenericHandler
{
   // Provide logging
   private static Logger log = Logger.getLogger(CustomAddressingHandler.class);

   public QName[] getHeaders()
   {
      return new QName[] {};
   }

	protected boolean handleOutbound(MessageContext msgContext)
   {
      log.info("handleRequest" + this);
      try
      {
         AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
         SOAPAddressingProperties outProps = (SOAPAddressingProperties)builder.newAddressingProperties();
			outProps.setMu(true);
			outProps.setTo(builder.newURI("http://localhost:8080/jaxrpc-addressing-action-rpc/ActionService"));
         outProps.setAction(builder.newURI("urn:wsa-action-bar"));
         msgContext.put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_OUTBOUND, outProps);
      }
      catch (URISyntaxException ex)
      {
         throw new IllegalStateException("Cannot handle request", ex);
      }
      return true;
   }
}
