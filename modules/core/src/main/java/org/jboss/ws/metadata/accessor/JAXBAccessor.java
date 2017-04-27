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
package org.jboss.ws.metadata.accessor;

import org.jboss.ws.WSException;

import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.api.RawAccessor;

/**
 * A JAXB object accessor.
 *
 * @author <a href="jason.greene@jboss.com">Jason T. Greene</a>
 */
@SuppressWarnings("unchecked")
public class JAXBAccessor implements Accessor
{
   private RawAccessor accessor;

   JAXBAccessor(RawAccessor accessor)
   {
      this.accessor = accessor;
   }

   public Object get(Object bean)
   {
      try
      {
         return accessor.get(bean);
      }
      catch (AccessorException a)
      {
         WSException ex = new WSException(a.getMessage());
         ex.setStackTrace(a.getStackTrace());
         throw ex;
      }
   }

   public void set(Object bean, Object value)
   {
      try
      {
         accessor.set(bean, value);
      }
      catch (AccessorException a)
      {
         WSException ex = new WSException(a.getMessage());
         ex.setStackTrace(a.getStackTrace());
         throw ex;
      }
   }
}
