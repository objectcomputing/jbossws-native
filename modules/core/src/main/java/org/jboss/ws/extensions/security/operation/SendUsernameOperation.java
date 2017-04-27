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

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

import org.jboss.logging.Logger;
import org.jboss.security.Base64Encoder;
import org.jboss.ws.extensions.security.SecurityStore;
import org.jboss.ws.extensions.security.auth.callback.UsernameTokenCallback;
import org.jboss.ws.extensions.security.auth.callback.UsernameTokenCallbackHandler;
import org.jboss.ws.extensions.security.element.SecurityHeader;
import org.jboss.ws.extensions.security.element.UsernameToken;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.jboss.ws.extensions.security.nonce.NonceGenerator;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.w3c.dom.Document;

public class SendUsernameOperation implements EncodingOperation
{
   private static Logger log = Logger.getLogger(SendUsernameOperation.class);
   
   private String username;
   private String credential;
   private boolean digestPassword;
   private boolean useNonce;
   private boolean useCreated;
   private NonceGenerator nonceGenerator;
   
   public SendUsernameOperation(String username, String credential, boolean digestPassword, boolean useNonce, boolean useCreated, NonceGenerator nonceGenerator)
   {
      this.username = username;
      this.credential = credential;
      this.digestPassword = digestPassword;
      this.useNonce = useNonce;
      this.useCreated = useCreated;
      this.nonceGenerator = nonceGenerator;
   }

   public void process(Document message, SecurityHeader header, SecurityStore store) throws WSSecurityException
   {
      String created = useCreated ? getCurrentTimestampAsString() : null;
      String nonce = useNonce ? nonceGenerator.generateNonce() : null;
      String password = digestPassword ? createPasswordDigest(nonce, created, credential) : credential;
      header.addToken(new UsernameToken(username, password, message, digestPassword, nonce, created));
   }
   
   private static String getCurrentTimestampAsString()
   {
      Calendar timestamp = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
      return SimpleTypeBindings.marshalDateTime(timestamp);
   }
   
   /**
    * Calculate the password digest using a MessageDigest and the UsernameTokenCallback/CallbackHandler
    */
   @SuppressWarnings("unchecked")
   public static String createPasswordDigest(String nonce, String created, String password)
   {
      String passwordHash = null;
      try
      {
         // convert password to byte data
         byte[] passBytes = password.getBytes("UTF-8");
         // prepare the username token digest callback
         UsernameTokenCallback callback = new UsernameTokenCallback();
         Map options = new HashMap();
         callback.init(options);
         // add the username token callback handler to provide the parameters
         CallbackHandler handler = new UsernameTokenCallbackHandler(nonce, created);
         handler.handle((Callback[])options.get("callbacks"));
         // calculate the hash and apply the encoding.
         MessageDigest md = MessageDigest.getInstance("SHA");
         callback.preDigest(md);
         md.update(passBytes);
         callback.postDigest(md);
         byte[] hash = md.digest();
         passwordHash =  Base64Encoder.encode(hash);
      }
      catch(Exception e)
      {
         log.error("Password hash calculation failed ", e);
      }
      return passwordHash;
   }
}
