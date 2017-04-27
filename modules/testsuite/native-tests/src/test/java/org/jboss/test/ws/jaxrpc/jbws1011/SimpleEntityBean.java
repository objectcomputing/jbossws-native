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
package org.jboss.test.ws.jaxrpc.jbws1011;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

/**
 * 
 * @author darran.lofthouse@jboss.com
 * @since 03-July-2006
 */
public abstract class SimpleEntityBean implements EntityBean
{

   private EntityContext context;

   public String ejbCreate(final String id) throws CreateException
   {
      setId(id);
      return null;
   }

   public void ejbPostCreate(final String id)
   {
   }

   public abstract void setId(final String id);

   public abstract String getId();

   public void setEntityContext(final EntityContext context)
   {
      this.context = context;
   }

   public void unsetEntityContext()
   {
      context = null;
   }

   public void ejbRemove()
   {
   }

   public void ejbActivate()
   {
   }

   public void ejbPassivate()
   {
   }

   public void ejbLoad()
   {
   }

   public void ejbStore()
   {
   }

}
