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

import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSException;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.util.NotImplementedException;

/**
 * Represents an XS Element Declaration
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  May 3, 2005
 */
public class JBossXSElementDeclaration extends JBossXSObject implements XSElementDeclaration
{
   protected String targetNamespace = null;
   protected JBossXSTypeDefinition xstype = null;
   protected boolean nillable = false;
   private XSElementDeclaration xsed;

   /**
    * Sole Annotation object
    */
   protected XSAnnotation annotation = null;

   /**
    *
    */
   public JBossXSElementDeclaration()
   {
      super();
   }

   /**
    * @param name
    * @param namespace
    */
   public JBossXSElementDeclaration(String name, String namespace)
   {
      super(name, namespace);
   }

   /**
    * Create a new JBossXSElementDeclaration while
    * reading data from XSElementDeclaration.
    * @param xe
    */
   public JBossXSElementDeclaration(XSElementDeclaration xe)
   {
      if (xe == null)
         throw new IllegalArgumentException("Illegal Null Argument:xe");

      xsed = xe;
      name = xe.getName();
      namespace = xe.getNamespace();
      XSTypeDefinition xt = xe.getTypeDefinition();
      if (xt instanceof JBossXSTypeDefinition == false && xt instanceof XSComplexTypeDefinition)
      {
         xstype = new JBossXSComplexTypeDefinition((XSComplexTypeDefinition)xt);
      }
      else if (xt instanceof JBossXSTypeDefinition == false)
      {
         xstype = new JBossXSTypeDefinition(xt);
      }
      this.annotation = xe.getAnnotation();
   }

   public XSElementDeclaration getXSElementDeclaration()
   {
      return xsed;
   }

   /**
    * One of XSConstants.SCOPE_GLOBAL, XSConstants.SCOPE_LOCAL
    * or XSConstants.SCOPE_ABSENT
    */
   protected short scope = 0;

   protected XSComplexTypeDefinition enclosingCTDefinition;

   public XSTypeDefinition getTypeDefinition()
   {
      return this.xstype;
   }

   public void setTypeDefinition(XSTypeDefinition xst)
   {
      if (xst instanceof JBossXSTypeDefinition)
         this.xstype = (JBossXSTypeDefinition)xst;
      else if (xst instanceof XSComplexTypeDefinition)
         this.xstype = new JBossXSComplexTypeDefinition((XSComplexTypeDefinition)xst);
   }

   /**
    * One of XSConstants.SCOPE_GLOBAL, XSConstants.SCOPE_LOCAL
    * or XSConstants.SCOPE_ABSENT
    * @return
    */
   public short getScope()
   {
      return this.scope;
   }

   public void setScope(short scope)
   {
      this.scope = scope;
   }

   public XSComplexTypeDefinition getEnclosingCTDefinition()
   {
      return this.enclosingCTDefinition;
   }

   public void setEnclosingCTDefinition(XSComplexTypeDefinition enclosingCTDefinition)
   {
      this.enclosingCTDefinition = enclosingCTDefinition;
   }

   public String getTargetNamespace()
   {
      return targetNamespace;
   }

   public void setTargetNamespace(String targetNamespace)
   {
      this.targetNamespace = targetNamespace;
      this.setNamespace(targetNamespace);
   }

   public short getConstraintType()
   {
      return 0;
   }

   public String getConstraintValue()
   {
      return null;
   }

   public Object getActualVC() throws XSException
   {
      return null;
   }

   public short getActualVCType() throws XSException
   {
      return 0;
   }

   public ShortList getItemValueTypes() throws XSException
   {
      return null;
   }

   public boolean getNillable()
   {
      return this.nillable;
   }

   public void setNillable(boolean nillable)
   {
      this.nillable = nillable;
   }

   public XSNamedMap getIdentityConstraints()
   {
      return null;
   }

   public XSElementDeclaration getSubstitutionGroupAffiliation()
   {
      return null;
   }

   public boolean isSubstitutionGroupExclusion(short i)
   {
      return false;
   }

   public short getSubstitutionGroupExclusions()
   {
      return 0;
   }

   public boolean isDisallowedSubstitution(short i)
   {
      return false;
   }

   public short getDisallowedSubstitutions()
   {
      return 0;
   }

   public boolean getAbstract()
   {
      return false;
   }

   public XSAnnotation getAnnotation()
   {
      return this.annotation;
   }

   /**
    * Get the type
    */
   @Override
   public short getType()
   {
      return XSConstants.ELEMENT_DECLARATION;
   }

   public XSObjectList getAnnotations()
   {
      throw new NotImplementedException();
   }
}
