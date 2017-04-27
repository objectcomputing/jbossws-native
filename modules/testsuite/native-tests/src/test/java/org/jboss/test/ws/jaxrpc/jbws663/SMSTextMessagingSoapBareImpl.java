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
 * document/literal bare, headers not bound to the endpoint  
 */
public class SMSTextMessagingSoapBareImpl implements SMSTextMessagingSoapBare
{
   private Logger log = Logger.getLogger(SMSTextMessagingSoapBareImpl.class);
   
   public SendMessageResponse sendMessage(SendMessage parameters)
   {
      log.debug("sendMessage: " + parameters.getMessageText());
      MessageStatus msgStatus = new MessageStatus(100, "ok", "extra");
      SMSTextMessageTargetStatus status = new SMSTextMessageTargetStatus(parameters.getToNumber(), "track001", msgStatus);
      SendMessageResponse retObj = new SendMessageResponse(status);
      return retObj;
   }

   public SendMessagesBulkResponse sendMessagesBulk(SendMessagesBulk parameters)

   {
      SendMessagesBulkResponse retObj = null;
      return retObj;
   }

   public TrackMessageResponse trackMessage(TrackMessage parameters)
   {
      TrackMessageResponse retObj = null;
      return retObj;
   }

   public TrackMessagesBulkResponse trackMessagesBulk(TrackMessagesBulk parameters)
   {
      TrackMessagesBulkResponse retObj = null;
      return retObj;
   }

   public GetSupportedCarriersResponse getSupportedCarriers(GetSupportedCarriers parameters)
   {
      GetSupportedCarriersResponse retObj = null;
      return retObj;
   }

   public GetCountryCodesResponse getCountryCodes(GetCountryCodes parameters)
   {
      GetCountryCodesResponse retObj = null;
      return retObj;
   }

   public GetRemainingHitsResponse getRemainingHits(GetRemainingHits parameters)
   {
      GetRemainingHitsResponse retObj = null;
      return retObj;
   }
}
