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
package org.jboss.ws.core.jaxws.handler;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.xml.ws.LogicalMessage;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;

import org.jboss.ws.core.MessageAbstraction;
import org.jboss.ws.core.soap.Style;

/**
 * The LogicalMessageContext interface extends MessageContext to provide access to a the 
 * contained message as a protocol neutral LogicalMessage.
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 31-Aug-2006
 */
public class LogicalMessageContextImpl implements LogicalMessageContext
{
   // The LogicalMessage in this message context
   private LogicalMessage logicalMessage;
   private MessageContext delegate;

   public LogicalMessageContextImpl(MessageContextJAXWS msgContext)
   {
      this.delegate = msgContext;

      Style style = msgContext.getEndpointMetaData().getStyle();
      MessageAbstraction message = msgContext.getMessageAbstraction();
      logicalMessage = new LogicalMessageImpl(message, style);
   }

   /**
    * Gets the message from this message context
    * @return  The contained message; returns null if no message is present in this message context
    */
   public LogicalMessage getMessage()
   {
      return logicalMessage;
   }

   // MessageContext delegation

   public void clear()
   {
      delegate.clear();
   }

   public boolean containsKey(Object key)
   {
      return delegate.containsKey(key);
   }

   public boolean containsValue(Object value)
   {
      return delegate.containsValue(value);
   }

   public Set<Entry<String, Object>> entrySet()
   {
      return delegate.entrySet();
   }

   public boolean equals(Object o)
   {
      return delegate.equals(o);
   }

   public Object get(Object key)
   {
      return delegate.get(key);
   }

   public Scope getScope(String name)
   {
      return delegate.getScope(name);
   }

   public int hashCode()
   {
      return delegate.hashCode();
   }

   public boolean isEmpty()
   {
      return delegate.isEmpty();
   }

   public Set<String> keySet()
   {
      return delegate.keySet();
   }

   public Object put(String key, Object value)
   {
      return delegate.put(key, value);
   }

   public void putAll(Map<? extends String, ? extends Object> t)
   {
      delegate.putAll(t);
   }

   public Object remove(Object key)
   {
      return delegate.remove(key);
   }

   public void setScope(String name, Scope scope)
   {
      delegate.setScope(name, scope);
   }

   public int size()
   {
      return delegate.size();
   }

   public Collection<Object> values()
   {
      return delegate.values();
   }
}
