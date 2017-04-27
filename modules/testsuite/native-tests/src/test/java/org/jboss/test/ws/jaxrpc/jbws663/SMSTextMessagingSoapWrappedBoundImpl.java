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

import java.rmi.RemoteException;

import org.jboss.logging.Logger;
import org.jboss.test.ws.jaxrpc.jbws663.holders.ResponseInfoHolder;
import org.jboss.test.ws.jaxrpc.jbws663.holders.SubscriptionInfoHolder;


/**
 * document/literal wrapped, headers bound to the endpoint  
 */
public class SMSTextMessagingSoapWrappedBoundImpl implements SMSTextMessagingSoapWrappedBound
{
   private Logger log = Logger.getLogger(SMSTextMessagingSoapWrappedBoundImpl.class);

   public SMSTextMessageTargetStatus sendMessage(String toNumber, String fromNumber, String fromName, String messageText, LicenseInfo licenseInfo, ResponseInfoHolder responseInfo, SubscriptionInfoHolder subscriptionInfo) throws RemoteException
   {
      log.debug("sendMessage: " + messageText);
      MessageStatus msgStatus = new MessageStatus(100, "ok", licenseInfo.getRegisteredUser().getUserID());
      SMSTextMessageTargetStatus status = new SMSTextMessageTargetStatus(toNumber, "track001", msgStatus);
      responseInfo.value = new ResponseInfo(0, "all ok");
      subscriptionInfo.value = new SubscriptionInfo(0, "valid", 0, null, 0, null);
      return status;
   }

   public ArrayOfSMSTextMessageTargetStatus sendMessagesBulk(ArrayOfString toNumbers, String fromNumber, String fromName, String messageText, LicenseInfo licenseInfo, ResponseInfoHolder responseInfo, SubscriptionInfoHolder subscriptionInfo) throws RemoteException
   {
      return null;
   }

   public SMSTextMessageTargetStatus trackMessage(String trackingTag, LicenseInfo licenseInfo, ResponseInfoHolder responseInfo, SubscriptionInfoHolder subscriptionInfo) throws RemoteException
   {
      return null;
   }

   public ArrayOfSMSTextMessageTargetStatus trackMessagesBulk(ArrayOfString trackingTags, LicenseInfo licenseInfo, ResponseInfoHolder responseInfo, SubscriptionInfoHolder subscriptionInfo) throws RemoteException
   {
      return null;
   }

   public ArrayOfCarrier getSupportedCarriers(LicenseInfo licenseInfo, SubscriptionInfoHolder subscriptionInfo) throws RemoteException
   {
      return null;
   }

   public ArrayOfCountryCode getCountryCodes(LicenseInfo licenseInfo, SubscriptionInfoHolder subscriptionInfo) throws RemoteException
   {
      return null;
   }

   public GetRemainingHitsResponse getRemainingHits(LicenseInfo licenseInfo, SubscriptionInfoHolder subscriptionInfo) throws RemoteException
   {
      return null;
   }
}
