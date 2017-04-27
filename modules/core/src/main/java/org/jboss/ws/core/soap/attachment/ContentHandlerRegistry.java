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
package org.jboss.ws.core.soap.attachment;

import java.awt.datatransfer.DataFlavor;
import java.util.HashSet;
import java.util.Iterator;

import javax.activation.CommandMap;
import javax.activation.DataContentHandler;
import javax.activation.MailcapCommandMap;

import com.sun.mail.handlers.multipart_mixed;
import com.sun.mail.handlers.text_html;
import com.sun.mail.handlers.text_plain;

/**
 * <code>ContentHandlerRegistry</code> is responsible for registering
 * JBossWS data content handlers in JAF.
 * 
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 */
public class ContentHandlerRegistry
{
   private static final String JAF_CONTENT_HANDLER = "x-java-content-handler";
   
   private static HashSet handlerRegistry = new HashSet();
   
   static
   {
      addRegistryEntry(XmlDataContentHandler.class);
      addRegistryEntry(ImageDataContentHandler.class);
      addRegistryEntry(ByteArrayContentHandler.class);
      addRegistryEntry(text_plain.class);
      addRegistryEntry(text_html.class);
      addRegistryEntry(multipart_mixed.class);
   }
   
   private static void addRegistryEntry(Class contentHandler) 
   {
      handlerRegistry.add(contentHandler);
   }
   
   private static void registerContentHandler(Class contentHandler)
   {
      DataContentHandler handler;
      MailcapCommandMap mailcap;
      
      try 
      {
         mailcap = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
         handler = (DataContentHandler) contentHandler.newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException("Can not register content handler:" + e.getMessage());
      }
      
      DataFlavor[] flavors = handler.getTransferDataFlavors();
      if (flavors == null)
         return;
      
      for (int i = 0; i < flavors.length; i++)
      {
         DataFlavor flavor = flavors[i];
         String entry = flavor.getMimeType() + ";;" + JAF_CONTENT_HANDLER + "=" + contentHandler.getName();
         mailcap.addMailcap(entry);
      }      
   }
   
   /**
    * Loads all JBossWS content handlers.
    */
   public static void register()
   {
      Iterator i = handlerRegistry.iterator();
      while (i.hasNext())
         registerContentHandler((Class) i.next());
   }   
}
