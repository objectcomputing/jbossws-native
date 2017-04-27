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
package org.jboss.test.ws.jaxrpc.samples.wsaddr.replyto;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.MessageContext;

import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.EndpointReference;
import javax.xml.ws.addressing.JAXWSAConstants;
import javax.xml.ws.addressing.soap.SOAPAddressingBuilder;
import javax.xml.ws.addressing.soap.SOAPAddressingProperties;

import org.jboss.logging.Logger;

/**
 * A client side handler for the ws-addressing
 *
 * @author mageshbk@jboss.com
 * @since 12-Dec-2007
 */
public class ClientHandler extends GenericHandler
{
   // Provide logging
   private static Logger log = Logger.getLogger(ClientHandler.class);
   protected QName[] headers;

   public boolean handleRequest(MessageContext context)
   {
      log.info("enter handleRequest");

      try
      {
         AddressingBuilder builder = SOAPAddressingBuilder.getAddressingBuilder();
         SOAPAddressingProperties outProps = (SOAPAddressingProperties)builder.newAddressingProperties();
         outProps.setTo(builder.newURI("http://" + getServerHost() + ":8080/jaxrpc-samples-wsaddr-hello"));
         outProps.setAction(builder.newURI("http://org.jboss.ws/jaxrpc/samples/wsaddr/replyto/sayHello"));
         EndpointReference rp = builder.newEndpointReference(new URI("http://" + getServerHost() + ":8080/jaxrpc-samples-wsaddr-replyto"));
         outProps.setReplyTo(rp);
         outProps.setMessageID(builder.newURI("123456"));
         context.setProperty(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_OUTBOUND, outProps);
      }
      catch(URISyntaxException e)
      {
         e.printStackTrace();
      }
      log.info("exit handleRequest");
      return true;
   }

   public QName[] getHeaders()
   {
      return headers;
   }

   /**
    * Get the JBoss server host from system property "jboss.bind.address"
    * This defaults to "localhost" if the property is not set
    */
   private String getServerHost()
   {
      String hostName = System.getProperty("jboss.bind.address", "localhost");
      return hostName;
   }

}
