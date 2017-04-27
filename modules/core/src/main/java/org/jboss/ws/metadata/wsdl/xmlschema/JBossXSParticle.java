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

import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.jboss.util.NotImplementedException;

/**
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Apr 21, 2005
 */
public class JBossXSParticle extends JBossXSObject implements XSParticle, Comparable
{
   protected int minOccurs = 0;
   protected int maxOccurs = 0;
   protected XSTerm term = null;

   public JBossXSParticle()
   {
   }

   public JBossXSParticle(String name, String namespace)
   {
      super(name, namespace);
   }

   /**
    * [min occurs]: determines the minimum number of terms that can occur.
    */
   public int getMinOccurs()
   {
      return minOccurs;
   }

   /**
    *  [max occurs]: determines the maximum number of terms that can occur.
    * To query for the value of unbounded use
    * <code>maxOccursUnbounded</code>. When the value of
    * <code>maxOccursUnbounded</code> is <code>true</code>, the value of
    * <code>maxOccurs</code> is unspecified.
    */
   public int getMaxOccurs()
   {
      return maxOccurs;
   }

   /**
    * [max occurs]: whether the maxOccurs value is unbounded.
    */
   public boolean getMaxOccursUnbounded()
   {
      return (maxOccurs == -1);
   }

   /**
    * [term]: one of a model group, a wildcard, or an element declaration.
    */
   public XSTerm getTerm()
   {
      return term;
   }

   /**
    * Get the type
    */
   @Override
   public short getType()
   {
      return XSConstants.PARTICLE;
   }

   public void setMinOccurs(int minOccurs)
   {
      this.minOccurs = minOccurs;
   }

   public void setMaxOccurs(int maxOccurs)
   {
      this.maxOccurs = maxOccurs;
   }

   public void setTerm(XSTerm term)
   {
      this.term = term;
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(T)
    */
   public int compareTo(Object o)
   {
      int c = -1;
      if (o instanceof JBossXSParticle)
      {
         JBossXSParticle w = (JBossXSParticle)o;
         String oname = w.getTerm().getName();
         String termName = term.getName();
         if (termName != null)
            c = termName.compareTo(oname);
         //In the case of doclit, need to be careful about String_1,SimpleType_2
         if (termName != null)
         {
            char num1 = termName.charAt(termName.length() - 1);
            char num2 = oname.charAt(oname.length() - 1);
            if (Character.isDigit(num1) && Character.isDigit(num2))
               c = ("" + num1).compareTo(("" + num2));
         }
      }
      return c;
   }

   public XSObjectList getAnnotations()
   {
      throw new NotImplementedException();
   }
}
