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
package org.jboss.ws.core;

import org.jboss.logging.Logger;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.core.soap.attachment.SwapableMemoryDataSource;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.xb.binding.NamespaceRegistry;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext.Scope;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * The common JAXRPC/JAXWS MessageContext
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 1-Sep-2006
 */
public abstract class CommonMessageContext implements Map<String, Object>
{
   private static Logger log = Logger.getLogger(CommonMessageContext.class);

   // expandToDOM in the SOAPContentElement should not happen during normal operation 
   // This property should be set the message context when it is ok to do so.
   public static final String ALLOW_EXPAND_TO_DOM = "org.jboss.ws.allow.expand.dom";

   public static final String REMOTING_METADATA = "org.jboss.ws.remoting.metadata";

   // The serialization context for this message ctx
   private SerializationContext serContext;
   // The operation for this message ctx
   private EndpointMetaData epMetaData;
   // The operation for this message ctx
   private OperationMetaData opMetaData;
   // The Message in this message context
   private MessageAbstraction message;
   // The map of scoped properties
   protected Map<String, ScopedProperty> scopedProps = new HashMap<String, ScopedProperty>();
   // The current property scope
   protected Scope currentScope = Scope.APPLICATION;

   private boolean isModified;

   public CommonMessageContext()
   {
   }

   // Copy constructor
   public CommonMessageContext(CommonMessageContext msgContext)
   {
      this.epMetaData = msgContext.epMetaData;
      this.opMetaData = msgContext.opMetaData;
      this.message = msgContext.message;
      this.serContext = msgContext.serContext;
      this.scopedProps = new HashMap<String, ScopedProperty>(msgContext.scopedProps);
      this.currentScope = msgContext.currentScope;
   }

   public Scope getCurrentScope()
   {
      return currentScope;
   }

   public void setCurrentScope(Scope currentScope)
   {
      this.currentScope = currentScope;
   }

   public EndpointMetaData getEndpointMetaData()
   {
      if (epMetaData == null && opMetaData != null)
         epMetaData = opMetaData.getEndpointMetaData();

      return epMetaData;
   }

   public void setEndpointMetaData(EndpointMetaData epMetaData)
   {
      this.epMetaData = epMetaData;
   }

   public OperationMetaData getOperationMetaData()
   {
      return opMetaData;
   }

   public void setOperationMetaData(OperationMetaData opMetaData)
   {
      this.opMetaData = opMetaData;
   }

   public SOAPMessage getSOAPMessage()
   {
      if(message!=null && ((message instanceof SOAPMessage) == false))
         throw new UnsupportedOperationException("No SOAPMessage avilable. Current message context carries " + message.getClass());
      return (SOAPMessage)message;
   }

   public void setSOAPMessage(SOAPMessage soapMessage)
   {
      this.message = (MessageAbstraction)soapMessage;
      this.setModified(true);
   }

   public MessageAbstraction getMessageAbstraction()
   {
      return message;
   }

   public void setMessageAbstraction(MessageAbstraction message)
   {
      this.message = message;
   }

   public SerializationContext getSerializationContext()
   {
      if (serContext == null)
      {
         serContext = createSerializationContext();
      }
      return serContext;
   }

   public abstract SerializationContext createSerializationContext();

   public void setSerializationContext(SerializationContext serContext)
   {
      this.serContext = serContext;
   }

   /** Gets the namespace registry for this message context */
   public NamespaceRegistry getNamespaceRegistry()
   {
      return getSerializationContext().getNamespaceRegistry();
   }

   // Map interface

   public int size()
   {
      return scopedProps.size();
   }

   public boolean isEmpty()
   {
      return scopedProps.isEmpty();
   }

   public boolean containsKey(Object key)
   {
      ScopedProperty prop = scopedProps.get(key);
      return isValidInScope(prop);
   }

   public boolean containsValue(Object value)
   {
      boolean valueFound = false;
      for (ScopedProperty prop : scopedProps.values())
      {
         if (prop.getValue().equals(value) && isValidInScope(prop))
         {
            valueFound = true;
            break;
         }
      }
      return valueFound;
   }

   public Object get(Object key)
   {
      Object value = null;

      ScopedProperty scopedProp = scopedProps.get(key);
      if (log.isTraceEnabled())
         log.trace("get(" + key + "): " + scopedProp);

      if (isValidInScope(scopedProp))
         value = scopedProp.getValue();

      return value;
   }

   public Object put(String key, Object value)
   {
      ScopedProperty prevProp = scopedProps.get(key);
      if (prevProp != null && !isValidInScope(prevProp))
         throw new IllegalArgumentException("Cannot set value for HANDLER scoped property: " + key);

      ScopedProperty newProp = new ScopedProperty(key, value, currentScope);
      if (log.isTraceEnabled())
         log.trace("put: " + newProp);

      scopedProps.put(key, newProp);
      return prevProp != null ? prevProp.getValue() : null;
   }

   public Object remove(Object key)
   {
      ScopedProperty prevProp = scopedProps.get(key);
      if (prevProp != null && !isValidInScope(prevProp))
         throw new IllegalArgumentException("Cannot set remove for HANDLER scoped property: " + key);

      return scopedProps.remove(key);
   }

   public void putAll(Map<? extends String, ? extends Object> srcMap)
   {
      for (String key : srcMap.keySet())
      {
         try
         {
            Object value = srcMap.get(key);
            put(key, value);
         }
         catch (IllegalArgumentException ex)
         {
            log.debug("Ignore: " + ex.getMessage());
         }
      }
   }

   public void clear()
   {
      scopedProps.clear();
   }

   public boolean isModified()
   {
      // skip changes from XOP handler interactions
      if (XOPContext.isXOPEncodedRequest() && !XOPContext.isXOPMessage())
      {
         log.debug("Disregard changes from XOP/Handler interactions");
         return false;
      }
      return isModified;
   }

   /**
    * Mark a message as 'modified' when the SAAJ model becomes stale.    
    * This may be the case when:
    * <ul>
    * <li>the complete message is replaced at MessageContext level
    * <li>the payload is set on a LogicalMessage
    * <li>The SAAJ model is changed though the DOM or SAAJ API (handler)
    * </ul>
    *
    * In any of these cases another 'unbind' invocation is required.
    */
   public void setModified(boolean modified)
   {
      isModified = modified;
   }

   public Set<String> keySet()
   {
      Set<String> keys = new HashSet<String>(scopedProps.size());
      for (ScopedProperty prop : scopedProps.values())
      {
         if (isValidInScope(prop))
            keys.add(prop.getName());
      }
      return keys;
   }

   public Collection<Object> values()
   {
      Collection<Object> values = new HashSet<Object>(scopedProps.size());
      for (ScopedProperty prop : scopedProps.values())
      {
         if (isValidInScope(prop))
            values.add(prop.getValue());
      }
      return values;
   }

   public Set<Entry<String, Object>> entrySet()
   {
      Set<Entry<String, Object>> entries = new HashSet<Entry<String, Object>>();
      for (ScopedProperty prop : scopedProps.values())
      {
         if (isValidInScope(prop))
         {
            String name = prop.getName();
            Object value = prop.getValue();
            Entry<String, Object> entry = new ImmutableEntry<String, Object>(name, value);
            entries.add(entry);
         }
      }
      return entries;
   }

   private boolean isValidInScope(ScopedProperty prop)
   {
      // A property of scope APPLICATION is always visible
      boolean valid = (prop != null && (prop.getScope() == Scope.APPLICATION || currentScope == Scope.HANDLER));
      return valid;
   }

   public static void cleanupAttachments(CommonMessageContext messageContext)
   {
      // cleanup attachments
      MessageAbstraction msg = messageContext.getMessageAbstraction();

      if(msg!=null && (msg instanceof SOAPMessage)) // in case of http binding
      {
         Iterator it = ((SOAPMessage)msg).getAttachments();
         while(it.hasNext())
         {
            AttachmentPart attachment = (AttachmentPart)it.next();
            try
            {
               if(attachment.getDataHandler().getDataSource() instanceof SwapableMemoryDataSource)
               {
                  SwapableMemoryDataSource swapFile = (SwapableMemoryDataSource)attachment.getDataHandler().getDataSource();
                  swapFile.cleanup();
               }
            }
            catch (SOAPException e)
            {
               log.warn("Failed to cleanup attachment part", e);
            }
         }
      }
   }

   private static class ImmutableEntry<K, V> implements Map.Entry<K, V>
   {
      final K k;
      final V v;

      ImmutableEntry(K key, V value)
      {
         k = key;
         v = value;
      }

      public K getKey()
      {
         return k;
      }

      public V getValue()
      {
         return v;
      }

      public V setValue(V value)
      {
         throw new UnsupportedOperationException();
      }
   }

   public static class ScopedProperty
   {
      private Scope scope;
      private String name;
      private Object value;

      public ScopedProperty(String name, Object value, Scope scope)
      {
         this.scope = scope;
         this.name = name;
         this.value = value;
      }

      public String getName()
      {
         return name;
      }

      public Scope getScope()
      {
         return scope;
      }

      public Object getValue()
      {
         return value;
      }

      public String toString()
      {
         return scope + ":" + name + "=" + value;
      }
   }
}
