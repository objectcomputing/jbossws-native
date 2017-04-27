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
package org.jboss.ws.metadata.wsse;

import java.io.Serializable;

/**
 * Represents the "timestamp-verification" tag.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 * @since Aril 14 2008
 */
public class TimestampVerification implements Serializable
{

   private static final long serialVersionUID = -1097288149565604697L;

   private long createdTolerance;

   private boolean warnCreated;

   private long expiresTolerance;

   private boolean warnExpires;

   public TimestampVerification(long createdTolerance, boolean warnCreated, long expiresTolerance, boolean warnExpires)
   {
      this.createdTolerance = createdTolerance;
      this.warnCreated = warnCreated;
      this.expiresTolerance = expiresTolerance;
      this.warnExpires = warnExpires;
   }

   public long getCreatedTolerance()
   {
      return createdTolerance;
   }

   public void setCreatedTolerance(long createdTolerance)
   {
      this.createdTolerance = createdTolerance;
   }

   public boolean isWarnCreated()
   {
      return warnCreated;
   }

   public void setWarnCreated(boolean warnCreated)
   {
      this.warnCreated = warnCreated;
   }

   public long getExpiresTolerance()
   {
      return expiresTolerance;
   }

   public void setExpiresTolerance(long expiresTolerance)
   {
      this.expiresTolerance = expiresTolerance;
   }

   public boolean isWarnExpires()
   {
      return warnExpires;
   }

   public void setWarnExpires(boolean warnExpires)
   {
      this.warnExpires = warnExpires;
   }

}
