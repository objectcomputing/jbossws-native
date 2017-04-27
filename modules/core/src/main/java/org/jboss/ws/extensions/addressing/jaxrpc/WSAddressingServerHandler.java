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
package org.jboss.ws.extensions.addressing.jaxrpc;

import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.JAXWSAConstants;
import javax.xml.ws.addressing.soap.SOAPAddressingBuilder;
import javax.xml.ws.addressing.soap.SOAPAddressingProperties;

import org.jboss.logging.Logger;
import org.jboss.ws.core.jaxrpc.handler.SOAPMessageContextJAXRPC;
import org.jboss.ws.extensions.addressing.AddressingConstantsImpl;
import org.jboss.ws.extensions.addressing.metadata.AddressingOpMetaExt;
import org.jboss.ws.metadata.umdm.OperationMetaData;

/**
 * A server side handler that reads/writes the addressing properties
 * and puts then into the message context.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 24-Nov-2005
 */
public class WSAddressingServerHandler extends GenericHandler
{
   // Provide logging
   private static Logger log = Logger.getLogger(WSAddressingServerHandler.class);

   private static AddressingBuilder ADDR_BUILDER;
   private static AddressingConstantsImpl ADDR_CONSTANTS;
	private static QName[] HEADERS = new QName[2];
	
	static
   {
      ADDR_CONSTANTS = new AddressingConstantsImpl();
      ADDR_BUILDER = AddressingBuilder.getAddressingBuilder();

		HEADERS[0] = ADDR_CONSTANTS.getActionQName();
		HEADERS[1] = ADDR_CONSTANTS.getToQName();
	}

   public QName[] getHeaders()
   {
      return HEADERS;
   }

   public void init(HandlerInfo handlerInfo)
   {
      super.init(handlerInfo);           
   }

   /**
    * Read the addressing headers from the incomming message and put a
    * SOAPAddressingProperties object into the message context
    */
   public boolean handleRequest(MessageContext msgContext)
   {
      if(log.isDebugEnabled()) log.debug("handleRequest");

      SOAPAddressingProperties addrProps = (SOAPAddressingProperties)ADDR_BUILDER.newAddressingProperties();
      SOAPMessage soapMessage = ((SOAPMessageContext)msgContext).getMessage();
      addrProps.readHeaders(soapMessage);
      msgContext.setProperty(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND, addrProps);
      return true;
   }

   /**
    * Get a SOAPAddressingProperties object from the message context
    * and write the adressing headers
    */
   public boolean handleResponse(MessageContext msgContext)
   {
      if(log.isDebugEnabled()) log.debug("handleResponse");
      handleResponseOrFault(msgContext, false);
      return true;
   }

   /**
    * Get a SOAPAddressingProperties object from the message context
    * and write the adressing headers
    */
   public boolean handleFault(MessageContext msgContext)
   {
      if(log.isDebugEnabled()) log.debug("handleFault");
      handleResponseOrFault(msgContext, true);
      return true;
   }

   private void handleResponseOrFault(MessageContext msgContext, boolean isFault)
   {
      SOAPAddressingBuilder builder = (SOAPAddressingBuilder)SOAPAddressingBuilder.getAddressingBuilder();
      SOAPMessage soapMessage = ((SOAPMessageContext)msgContext).getMessage();

      SOAPAddressingProperties inProps = (SOAPAddressingProperties)msgContext.getProperty(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
      SOAPAddressingProperties outProps = (SOAPAddressingProperties)msgContext.getProperty(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_OUTBOUND);

      if (outProps == null)
      {
         // create new response properties
         outProps = (SOAPAddressingProperties)builder.newAddressingProperties();
         msgContext.setProperty(JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_OUTBOUND, outProps);
      }

      outProps.initializeAsReply(inProps, isFault);

      try
      {
         // supply the response action

         OperationMetaData opMetaData = ((SOAPMessageContextJAXRPC)msgContext).getOperationMetaData();

         if (!isFault && !opMetaData.isOneWay())
         {

            AddressingOpMetaExt addrExt = (AddressingOpMetaExt)opMetaData.getExtension(ADDR_CONSTANTS.getNamespaceURI());
            if (addrExt != null)
            {
               outProps.setAction(ADDR_BUILDER.newURI(addrExt.getOutboundAction()));
            }
            else
            {
               log.warn("Unable to resolve replyAction for " + opMetaData.getQName());
            }

         }
         else if (isFault)
         {
            outProps.setAction(ADDR_BUILDER.newURI(ADDR_CONSTANTS.getDefaultFaultAction()));
         }

      }
      catch (URISyntaxException e)
      {
         log.error("Error setting response action", e);
      }

      outProps.writeHeaders(soapMessage);
   }
}
