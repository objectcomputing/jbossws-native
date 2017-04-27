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
package org.jboss.test.ws.jaxrpc.xop.shared;

import java.util.StringTokenizer;

import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.StubExt;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.extensions.xop.XOPContext;

/**
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Sep 25, 2006
 */
public class MTOMServiceBase {
   protected void toggleXOP(String message) {
      StringTokenizer tok = new StringTokenizer(message, "|");
      String requestOptimized = tok.nextToken();
      String responseOptimized = tok.nextToken();
      System.out.println(requestOptimized+"|"+responseOptimized);

      if(requestOptimized.equals("1") && !XOPContext.isXOPEncodedRequest())
      {
         throw new RuntimeException("Illegal state: No XOP encoded request found");
      }      

      if(responseOptimized.equals("0"))
      {
         CommonMessageContext ctx = MessageContextAssociation.peekMessageContext();
         ctx.put(StubExt.PROPERTY_MTOM_ENABLED, Boolean.FALSE);
      }
   }
}
