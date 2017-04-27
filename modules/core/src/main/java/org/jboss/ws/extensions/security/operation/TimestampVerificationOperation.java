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
package org.jboss.ws.extensions.security.operation;

import java.util.Calendar;

import org.jboss.logging.Logger;
import org.jboss.ws.extensions.security.element.Timestamp;
import org.jboss.ws.extensions.security.exception.FailedCheckException;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.jboss.ws.metadata.wsse.TimestampVerification;
import org.w3c.dom.Document;

public class TimestampVerificationOperation
{
   private static final Logger log = Logger.getLogger(TimestampVerificationOperation.class);

   private TimestampVerification timestampVerification;

   private Calendar now = null;

   public TimestampVerificationOperation(TimestampVerification timestampVerification)
   {
      this.timestampVerification = timestampVerification;
   }

   /**
    * A special constructor that allows you to use a different value when validating the message.
    * DO NOT USE THIS UNLESS YOU REALLY KNOW WHAT YOU ARE DOING!.
    *
    * @param now The timestamp to use as the current time when validating a message expiration
    */
   public TimestampVerificationOperation(Calendar now)
   {
      this.now = now;
   }

   public void process(Document message, Timestamp timestamp) throws WSSecurityException
   {
      Calendar expires = timestamp.getExpires();
      Calendar created = timestamp.getCreated();

      Calendar now = (this.now == null) ? Calendar.getInstance() : this.now;

      boolean rejectCreated = created.after(now);
      if (rejectCreated && timestampVerification != null && timestampVerification.getCreatedTolerance() > 0)
      {
         Calendar tolerantCreatedNow = (Calendar)now.clone();
         tolerantCreatedNow.add(Calendar.SECOND, (int)timestampVerification.getCreatedTolerance());

         rejectCreated = created.after(tolerantCreatedNow);

         if (rejectCreated == false && timestampVerification.isWarnCreated())
         {
            log.warn("Accepting Timestamp with 'Created' after now but within configured tolerance.");
         }
      }

      if (rejectCreated)
      {
         throw new WSSecurityException("Invalid timestamp, message claimed to be created after now");
      }

      boolean rejectExpires = expires != null && !now.before(expires);
      if (rejectExpires && timestampVerification != null && timestampVerification.getExpiresTolerance() > 0)
      {
         Calendar tolerantExpiresNow = (Calendar)now.clone();         
         tolerantExpiresNow.add(Calendar.SECOND, (int)timestampVerification.getExpiresTolerance() * -1);
         
         rejectExpires = !tolerantExpiresNow.before(expires);

         if (rejectExpires == false && timestampVerification.isWarnExpires())
         {
            log.warn("Accepting Timestamp with 'Expires' in past but within configured tolerance.");
         }
      }

      if (rejectExpires)
      {
         throw new FailedCheckException("Expired message.");
      }

   }
}
