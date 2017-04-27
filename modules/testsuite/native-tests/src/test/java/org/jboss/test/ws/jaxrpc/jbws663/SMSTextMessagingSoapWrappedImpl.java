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
package org.jboss.test.ws.jaxrpc.jbws663;

import org.jboss.logging.Logger;

/**
 * document/literal wrapped, headers not bound to the endpoint  
 */
public class SMSTextMessagingSoapWrappedImpl implements SMSTextMessagingSoapWrapped
{
   private Logger log = Logger.getLogger(SMSTextMessagingSoapWrappedImpl.class);
   
   public SMSTextMessageTargetStatus sendMessage(String toNumber, String fromNumber, String fromName, String messageText)
   {
      log.debug("sendMessage: " + messageText);
      MessageStatus msgStatus = new MessageStatus(100, "ok", "extra");
      SMSTextMessageTargetStatus status = new SMSTextMessageTargetStatus(toNumber, "track001", msgStatus);
      return status;
   }

   public ArrayOfSMSTextMessageTargetStatus sendMessagesBulk(ArrayOfString toNumbers, String fromNumber, String fromName, String messageText)
   {
      ArrayOfSMSTextMessageTargetStatus retObj = null;
      return retObj;
   }

   public SMSTextMessageTargetStatus trackMessage(String trackingTag)
   {
      SMSTextMessageTargetStatus retObj = null;
      return retObj;
   }

   public ArrayOfSMSTextMessageTargetStatus trackMessagesBulk(ArrayOfString trackingTags)

   {
      ArrayOfSMSTextMessageTargetStatus retObj = null;
      return retObj;
   }

   public ArrayOfCarrier getSupportedCarriers()
   {
      ArrayOfCarrier retObj = null;
      return retObj;
   }

   public ArrayOfCountryCode getCountryCodes()
   {
      ArrayOfCountryCode retObj = null;
      return retObj;
   }

   public GetRemainingHitsResponse getRemainingHits()
   {
      GetRemainingHitsResponse retObj = null;
      return retObj;
   }
}
