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
package org.jboss.ws.metadata.wsdl.xmlschema;

import java.util.Iterator;
import java.util.List;

import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.jboss.util.NotImplementedException;

/**
 * Implements a ModelGroup of the Xerces Schema API
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Apr 21, 2005
 */
public class JBossXSModelGroup extends JBossXSObject implements XSModelGroup
{
   private JBossXSObjectList xsparts = new JBossXSObjectList();;
   protected short compositor = 0;

   /**
    * [compositor]: one of all, choice or sequence. The valid constant values
    * are:
    * <code>COMPOSITOR_SEQUENCE, COMPOSITOR_CHOICE, COMPOSITOR_ALL</code>.
    */
   public short getCompositor()
   {
      return compositor;
   }

   /**
    *  A list of [particles] if it exists, otherwise an empty
    * <code>XSObjectList</code>.
    */
   public XSObjectList getParticles()
   {
      return xsparts;
   }

   /**
    * An [annotation] if it exists, otherwise <code>null</code>.
    */
   public XSAnnotation getAnnotation()
   {
      return null;
   }

   public void setParticles(List p)
   {
      xsparts = new JBossXSObjectList();
      Iterator iter = p.iterator();
      while (iter.hasNext())
      {
         xsparts.addItem((JBossXSParticle)iter.next());
      }
   }

   public void setParticles(List p, boolean shouldSort)
   {
      xsparts = new JBossXSObjectList();
      if (shouldSort)
         setParticles(p);
      else
      {
         Iterator iter = p.iterator();
         while (iter.hasNext())
         {
            xsparts.addItem((JBossXSParticle)iter.next(), false);
         }
      }

   }

   public void setCompositor(short compositor)
   {
      this.compositor = compositor;
   }

   /**
    * Get the type
    */
   @Override
   public short getType()
   {
      return XSConstants.MODEL_GROUP;
   }

   public XSObjectList getAnnotations()
   {
      throw new NotImplementedException();
   }
}
