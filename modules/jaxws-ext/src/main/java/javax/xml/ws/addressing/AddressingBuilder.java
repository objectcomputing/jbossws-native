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
package javax.xml.ws.addressing;

import static javax.xml.ws.addressing.JAXWSAConstants.ADDRESSING_BUILDER_PROPERTY;
import static javax.xml.ws.addressing.JAXWSAConstants.DEFAULT_ADDRESSING_BUILDER;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

public abstract class AddressingBuilder implements AddressingType
{
   // provide logging
   private static Logger log = Logger.getLogger(AddressingBuilder.class.getName());

   protected AddressingBuilder()
   {
   }

   public static AddressingBuilder getAddressingBuilder()
   {
      ClassLoader classLoader;
      try
      {
         classLoader = Thread.currentThread().getContextClassLoader();
      }
      catch (Exception x)
      {
         throw new AddressingException(x.toString(), x);
      }

      String name = null;

      // Use the system property first
      try
      {
         name = System.getProperty(ADDRESSING_BUILDER_PROPERTY);
         if (name != null)
         {
            return newInstance(name, classLoader);
         }
      }
      catch (Exception e)
      {
         log.warning("Could not create and instance of " + name + " trying " + DEFAULT_ADDRESSING_BUILDER);
      }

      // default builder
      return newInstance(DEFAULT_ADDRESSING_BUILDER, classLoader);
   }

   private static AddressingBuilder newInstance(String className, ClassLoader classLoader)
   {
      try
      {
         Class cls;
         if (classLoader == null)
         {
            cls = Class.forName(className);
         }
         else
         {
            cls = classLoader.loadClass(className);
         }
         return (AddressingBuilder)cls.newInstance();
      }
      catch (ClassNotFoundException x)
      {
         throw new AddressingException("Provider " + className + " not found", x);
      }
      catch (Exception x)
      {
         throw new AddressingException("Provider " + className + " could not be instantiated: " + x, x);
      }
   }

   public abstract AttributedURI newURI(URI uri);

   public abstract AttributedURI newURI(String uri) throws URISyntaxException;

   public abstract AttributedQName newQName(QName name);

   public abstract Relationship newRelationship(URI uri);

   public abstract EndpointReference newEndpointReference(URI uri);

   public abstract AddressingProperties newAddressingProperties();

   public abstract AddressingConstants newAddressingConstants();
}
