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
package org.jboss.ws.extensions.security.auth.callback;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

import org.jboss.crypto.digest.DigestCallback;
import org.jboss.security.Base64Encoder;
import org.jboss.security.auth.callback.MapCallback;
import org.jboss.ws.WSException;

/**
 * An implementation of DigestCallback that generates password
 * digests according to the UsernameTokenProfile 1.0 specification.
 * 
 * @author alessio.soldano@jboss.com
 * @since 12-Mar-2008
 *
 */
public class UsernameTokenCallback implements DigestCallback
{
   public static final String NONCE = "nonce";
   public static final String CREATED = "created";
   
   private MapCallback info;

   @SuppressWarnings("unchecked")
   public void init(Map options)
   {
      // Ask for MapCallback to obtain the digest parameters
      info = new MapCallback();
      Callback[] callbacks = { info };
      options.put("callbacks", callbacks);
   }

   public void preDigest(MessageDigest digest)
   {
      try
      {
         String nonce = (String)info.getInfo(NONCE);
         if (nonce != null)
            digest.update(nonce.getBytes("UTF-8"));
         String created = (String)info.getInfo(CREATED);
         if (created != null)
            digest.update(created.getBytes("UTF-8"));
      }
      catch (UnsupportedEncodingException e)
      {
         throw new WSException(e);
      }
   }

   public void postDigest(MessageDigest digest)
   {
   }
  
   @SuppressWarnings("unchecked")
   public static void main(String[] args) throws Exception
   {
      if (args.length != 3)
      {
         System.err.println("Usage: UsernameTokenCallback nonce created password");
         System.err.println(" - nonce : the nonce");
         System.err.println(" - created : the creation timestamp");
         System.err.println(" - password : the plain text password");
         System.exit(1);
      }
      String nonce = args[0];
      String created = args[1];
      String password = args[2];
      
      MessageDigest digest = MessageDigest.getInstance("SHA");
      UsernameTokenCallback utc = new UsernameTokenCallback();
      Map options = new HashMap();
      utc.init(options);
      CallbackHandler cbh = new UsernameTokenCallbackHandler(nonce, created);
      cbh.handle((Callback[])options.get("callbacks"));
      utc.preDigest(digest);
      byte[] result = digest.digest(password.getBytes("UTF-8"));
      System.out.println("UsernameToken password digest: " + Base64Encoder.encode(result));
   }

}
