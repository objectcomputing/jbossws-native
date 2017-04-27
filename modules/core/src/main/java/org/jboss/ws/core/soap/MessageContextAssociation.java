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
package org.jboss.ws.core.soap;

import java.util.Stack;

import org.jboss.logging.Logger;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.utils.ThreadLocalAssociation;
import org.jboss.wsf.common.DOMUtils;

/**
 * A thread local association with the current message context
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 14-Dec-2004
 */
public class MessageContextAssociation
{
   // provide logging
   private static Logger log = Logger.getLogger(MessageContextAssociation.class);

   public static void pushMessageContext(CommonMessageContext msgContext)
   {
      if (log.isDebugEnabled())
         log.debug("pushMessageContext: " + msgContext + " (Thread " + Thread.currentThread().getName() + ")");
      Stack<CommonMessageContext> stack = ThreadLocalAssociation.localMsgContextAssoc().get();
      if (stack == null)
      {
         stack = new Stack<CommonMessageContext>();
         ThreadLocalAssociation.localMsgContextAssoc().set(stack);
      }
      stack.push(msgContext);
   }

   public static CommonMessageContext peekMessageContext()
   {
      CommonMessageContext msgContext = null;
      Stack<CommonMessageContext> stack = ThreadLocalAssociation.localMsgContextAssoc().get();
      if (stack != null && stack.isEmpty() == false)
      {
         msgContext = stack.peek();
      }
      return msgContext;
   }

   public static CommonMessageContext popMessageContext()
   {
      return popMessageContext(true);
   }
   
   public static CommonMessageContext popMessageContext(boolean clearDOMIfEmpty)
   {
      CommonMessageContext msgContext = null;
      Stack<CommonMessageContext> stack = ThreadLocalAssociation.localMsgContextAssoc().get();
      if (stack != null && stack.isEmpty() == false)
      {
         msgContext = stack.pop();
         if (stack.isEmpty() == true && clearDOMIfEmpty == true)
         {
            DOMUtils.clearThreadLocals();
         }
      }
      if (log.isDebugEnabled())
         log.debug("popMessageContext: " + msgContext + " (Thread " + Thread.currentThread().getName() + ")");
      return msgContext;
   }

}
