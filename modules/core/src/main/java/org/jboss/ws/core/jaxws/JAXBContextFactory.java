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
package org.jboss.ws.core.jaxws;

import java.util.Collection;

import javax.xml.bind.JAXBContext;

import org.jboss.ws.WSException;
import org.jboss.wsf.spi.binding.BindingCustomization;
import org.jboss.wsf.spi.util.ServiceLoader;

import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.bind.api.TypeReference;

/**
 * Creates JAXBContext's.<p>
 *
 * @author Heiko.Braun@jboss.com
 *         Created: Jun 26, 2007
 */
public abstract class JAXBContextFactory
{
   public final static String DEFAULT_JAXB_CONTEXT_FACTORY = "org.jboss.ws.core.jaxws.CustomizableJAXBContextFactory";
   
   private static ThreadLocal<Integer> threadContextCount = new ThreadLocal<Integer>(); 

   public abstract JAXBContext createContext(Class[] clazzes, BindingCustomization bindingCustomization) throws WSException;

   public abstract JAXBContext createContext(Class[] clazzes) throws WSException;

   public abstract JAXBContext createContext(Class clazz) throws WSException;

   public abstract JAXBRIContext createContext(Class[] classes, Collection<TypeReference> typeReferences, String defaultNamespaceRemap, boolean c14nSupport,
         BindingCustomization bindingCustomization);

   protected void incrementContextCount()
   {
      Integer count = getContextCount();
      threadContextCount.set(Integer.valueOf(count + 1));
   }
   
   public static Integer getContextCount()
   {
      Integer count = (Integer)threadContextCount.get();
      if (count == null)
         count = Integer.valueOf(0);
      
      return count;
   }
   
   public static void resetContextCount()
   {
      threadContextCount.set(Integer.valueOf(0));
   }
   
   /**
    * Retrieve JAXBContextFactory instance through the {@link org.jboss.wsf.spi.util.ServiceLoader}.
    * Defaults to {@link CustomizableJAXBContextFactory}
    * @return JAXBContextFactory
    */
   public static JAXBContextFactory newInstance()
   {
      return (JAXBContextFactory)ServiceLoader.loadService(JAXBContextFactory.class.getName(), DEFAULT_JAXB_CONTEXT_FACTORY);
   }
}
