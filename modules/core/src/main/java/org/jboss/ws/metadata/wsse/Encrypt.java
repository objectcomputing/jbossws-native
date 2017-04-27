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
 * <code>Encrypt</code> represents the encrypt tag, which signifies that the
 * message should be encrypted.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class Encrypt extends Targetable implements Serializable
{
   private static final long serialVersionUID = -2802677183149218760L;

   private String type;
   private String alias;
   private String algorithm;
   private String keyWrapAlgorithm;
   private String tokenRefType;

   public Encrypt(String type, String alias, String algorithm, String wrap, String tokenRefType)
   {
      this.type = type;
      this.alias = alias;
      this.algorithm = algorithm;
      this.keyWrapAlgorithm = wrap;
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

   public String getAlgorithm()
   {
      return algorithm;
   }

   public void setAlgorithm(String algorithm)
   {
      this.algorithm = algorithm;
   }

   public String getWrap()
   {
      return keyWrapAlgorithm;
   }

   public void setWrap(String wrap)
   {
      this.keyWrapAlgorithm = wrap;
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
