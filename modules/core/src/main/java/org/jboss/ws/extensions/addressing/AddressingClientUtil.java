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
package org.jboss.ws.extensions.addressing;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.rpc.Stub;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingConstants;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.AttributedURI;

import org.jboss.wsf.common.utils.UUIDGenerator;

/**
 * @author Heiko Braun, <heiko@openj.net>
 * @since 04-Mar-2006
 */
public class AddressingClientUtil
{
   private static AddressingBuilder BUILDER;
   private static AddressingConstants CONSTANTS;
   static
   {
      BUILDER = AddressingBuilder.getAddressingBuilder();
      CONSTANTS = BUILDER.newAddressingConstants();
   }

   /* creates outbound addressing properties */
   public static AddressingProperties createRequestProperties()
   {
      AddressingProperties addrProps = BUILDER.newAddressingProperties();
      addrProps.setMessageID(BUILDER.newURI(generateMessageID()));
      return addrProps;
   }

   /**
    * create default outbound addressing properties.
    */
   public static AddressingProperties createDefaultProps(String wsaAction, String wsaTo)
   {
      try
      {
         AddressingProperties addrProps = createRequestProperties();
         addrProps.setMessageID(BUILDER.newURI(generateMessageID()));
         addrProps.setAction(BUILDER.newURI(wsaAction));
         addrProps.setTo(BUILDER.newURI(wsaTo));
         return addrProps;
      }
      catch (URISyntaxException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   /**
    * create anonymous request properties.
    * wsa:ReplyTo is set to anonymous and a messageID is supplied.
    */
   public static AddressingProperties createAnonymousProps(String wsaAction, String wsaTo)
   {
      try
      {
         AddressingProperties addrProps = createDefaultProps(wsaAction, wsaTo);
         addrProps.setReplyTo(BUILDER.newEndpointReference(new URI(CONSTANTS.getAnonymousURI())));
         return addrProps;
      }
      catch (URISyntaxException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   /**
    * one-way properties cary a  wsa:ReplyTo of none
    * upon which no response is expected.
    */
   public static AddressingProperties createOneWayProps(String wsaAction, String wsaTo)
   {
      try
      {
         AddressingProperties addrProps = createDefaultProps(wsaAction, wsaTo);
         addrProps.setReplyTo(BUILDER.newEndpointReference(new URI(CONSTANTS.getNoneURI())));
         return addrProps;
      }
      catch (URISyntaxException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   /**
    * customize a stubs endpooint url
    */
   public static void setTargetAddress(Stub stub, String url)
   {
      stub._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, url);
   }

   /**
    * generate a UUID based message id.
    */
   public static URI generateMessageID()
   {
      URI messageId = null;
      try
      {
         messageId = new URI("urn:uuid:" + UUIDGenerator.generateRandomUUIDString());
      }
      catch (URISyntaxException e)
      {
         // Doesnt happen
      }
      return messageId;
   }

   public static AttributedURI createMessageID()
   {
      return BUILDER.newURI(generateMessageID());
   }
}
