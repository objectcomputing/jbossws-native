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
 * <code>Config</code> represents the config tag.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class Config implements Serializable
{
   private static final long serialVersionUID = 4219543242657899910L;

   private Timestamp timestamp;
   private Username username;
   private Sign sign;
   private Encrypt encrypt;
   private Requires requires;
   private Authenticate authenticate;
   private Authorize authorize;

   public Encrypt getEncrypt()
   {
      return encrypt;
   }

   public void setEncrypt(Encrypt encrypt)
   {
      this.encrypt = encrypt;
   }

   public Sign getSign()
   {
      return sign;
   }

   public void setSign(Sign sign)
   {
      this.sign = sign;
   }

   public Timestamp getTimestamp()
   {
      return timestamp;
   }

   public void setTimestamp(Timestamp timestamp)
   {
      this.timestamp = timestamp;
   }

   public Username getUsername()
   {
      return username;
   }

   public void setUsername(Username username)
   {
      this.username = username;
   }

   public Requires getRequires()
   {
      return requires;
   }

   public void setRequires(Requires requires)
   {
      this.requires = requires;
   }

   public Authenticate getAuthenticate()
   {
      return authenticate;
   }

   public void setAuthenticate(Authenticate authenticate)
   {
      this.authenticate = authenticate;
   }
   
   public Authorize getAuthorize()
   {
      return this.authorize;
   }
   
   public void setAuthorize(Authorize authorize)
   {
      this.authorize = authorize;
   }
}
