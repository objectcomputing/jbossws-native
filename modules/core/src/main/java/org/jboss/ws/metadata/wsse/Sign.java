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

/**
 * <code>Sign</code> represents the sign tag, which declares that a message
 * should be signed.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class Sign extends Targetable
{
   private static final long serialVersionUID = -2645745357707804441L;

   private String type;
   private String alias;
   private boolean includeTimestamp;
   private String tokenRefType;

   public Sign(String type, String alias, boolean includeTimestamp, String tokenRefType)
   {
      this.type = type;
      this.alias = alias;
      this.includeTimestamp = includeTimestamp;
      this.tokenRefType = tokenRefType;
   }

   public String getAlias()
   {
      return alias;
   }

   public void setAlias(String alias)
   {
      this.alias = alias;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public boolean isIncludeTimestamp()
   {
      return includeTimestamp;
   }

   public void setIncludeTimestamp(boolean includeTimestamp)
   {
      this.includeTimestamp = includeTimestamp;
   }

   public String getTokenRefType()
   {
      return tokenRefType;
   }

   public void setTokenRefType(String tokenRefType)
   {
      this.tokenRefType = tokenRefType;
   }
}
