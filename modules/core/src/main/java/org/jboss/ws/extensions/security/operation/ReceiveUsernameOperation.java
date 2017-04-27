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

import javax.security.auth.callback.CallbackHandler;

import org.jboss.logging.Logger;
import org.jboss.security.auth.callback.CallbackHandlerPolicyContextHandler;
import org.jboss.ws.extensions.security.SecurityStore;
import org.jboss.ws.extensions.security.SimplePrincipal;
import org.jboss.ws.extensions.security.auth.callback.UsernameTokenCallbackHandler;
import org.jboss.ws.extensions.security.element.SecurityHeader;
import org.jboss.ws.extensions.security.element.Token;
import org.jboss.ws.extensions.security.element.UsernameToken;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.jboss.ws.extensions.security.nonce.NonceStore;
import org.jboss.wsf.spi.SPIProvider;
import org.jboss.wsf.spi.SPIProviderResolver;
import org.jboss.wsf.spi.invocation.SecurityAdaptor;
import org.jboss.wsf.spi.invocation.SecurityAdaptorFactory;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.w3c.dom.Document;

public class ReceiveUsernameOperation implements TokenOperation
{
   private SecurityHeader header;
   private SecurityStore store;
   private NonceStore nonceStore;
   private static final int TIMESTAMP_FRESHNESS_THRESHOLD = 300;
   
   private SecurityAdaptorFactory secAdapterfactory;

   public ReceiveUsernameOperation(SecurityHeader header, SecurityStore store, NonceStore nonceStore)
   {
      this.header = header;
      this.store = store;
      this.nonceStore = nonceStore;

      SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
      secAdapterfactory = spiProvider.getSPI(SecurityAdaptorFactory.class);
   }

   public void process(Document message, Token token) throws WSSecurityException
   {
      UsernameToken user = (UsernameToken)token;
      SecurityAdaptor securityAdaptor = secAdapterfactory.newSecurityAdapter();
      if (user.isDigest())
      {
         verifyUsernameToken(user);
         CallbackHandler handler = new UsernameTokenCallbackHandler(user.getNonce(), user.getCreated());
         CallbackHandlerPolicyContextHandler.setCallbackHandler(handler);
      }
      securityAdaptor.setPrincipal(new SimplePrincipal(user.getUsername()));
      securityAdaptor.setCredential(user.getPassword());
   }
   
   private void verifyUsernameToken(UsernameToken token) throws WSSecurityException
   {
      if (token.getCreated() != null)
      {
         Calendar cal = SimpleTypeBindings.unmarshalDateTime(token.getCreated());
         Calendar ref = Calendar.getInstance();
         ref.add(Calendar.SECOND, -TIMESTAMP_FRESHNESS_THRESHOLD);
         if (ref.after(cal))
            throw new WSSecurityException("Request rejected since a stale timestamp has been provided: " + token.getCreated());
      }
      String nonce = token.getNonce();
      if (nonce != null)
      {
         if (nonceStore.hasNonce(nonce))
            throw new WSSecurityException("Request rejected since a message with the same nonce has been recently received; nonce = " + nonce);
         nonceStore.putNonce(nonce);
      }
   }
}
