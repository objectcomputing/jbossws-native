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

import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;

/**
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Apr 21, 2005
 */
public class JBossXSComplexTypeDefinition
        extends JBossXSTypeDefinition
        implements XSComplexTypeDefinition
{
   private boolean isAbstract;
   private XSSimpleTypeDefinition xsSimple;
   private XSParticle xspart;
   private XSObjectList annots;

   private String prefix;

   private List<XSAttributeUse> attList = new ArrayList<XSAttributeUse>();

   //Content Type
   private short contentType = XSComplexTypeDefinition.CONTENTTYPE_ELEMENT;

   private short deriveMethod = XSConstants.DERIVATION_NONE;

   public JBossXSComplexTypeDefinition( )
   {
   }

   public JBossXSComplexTypeDefinition(String name, String namespace)
   {
      super(name, namespace);
   }

   public JBossXSComplexTypeDefinition(XSComplexTypeDefinition xc)
   {
      super(xc.getName(),xc.getNamespace());
      setAnonymous(xc.getAnonymous());
      xspart = xc.getParticle();
      xsSimple = xc.getSimpleType();
      deriveMethod = xc.getDerivationMethod();
      this.baseType = new JBossXSTypeDefinition(xc.getBaseType());
   }

   /**
    * [derivation method]: either <code>DERIVATION_EXTENSION</code>,
    * <code>DERIVATION_RESTRICTION</code>, or <code>DERIVATION_NONE</code>
    * (see <code>XSConstants</code>).
    */
   public short getDerivationMethod()
   {
      return deriveMethod;
   }

   /**
    * [abstract]: a boolean. Complex types for which <code>abstract</code> is
    * true must not be used as the type definition for the validation of
    * element information items.
    */
   public boolean getAbstract()
   {
      return isAbstract;
   }

   /**
    *  A set of attribute uses if it exists, otherwise an empty
    * <code>XSObjectList</code>.
    */
   public XSObjectList getAttributeUses()
   {
       JBossXSObjectList oblist = new JBossXSObjectList();
       for(XSAttributeUse xa:attList)
       {
          oblist.addItem(xa);
       }
       return oblist;
   }

   /**
    * An attribute wildcard if it exists, otherwise <code>null</code>.
    */
   public XSWildcard getAttributeWildcard()
   {
      return null;
   }

   /**
    * [content type]: one of empty (<code>CONTENTTYPE_EMPTY</code>), a simple
    * type definition (<code>CONTENTTYPE_SIMPLE</code>), mixed (
    * <code>CONTENTTYPE_MIXED</code>), or element-only (
    * <code>CONTENTTYPE_ELEMENT</code>).
    */
   public short getContentType()
   {
      return contentType;
   }

   /**
    * A simple type definition corresponding to a simple content model,
    * otherwise <code>null</code>.
    */
   public XSSimpleTypeDefinition getSimpleType()
   {
      return xsSimple;
   }

   /**
    * A particle for a mixed or element-only content model, otherwise
    * <code>null</code>.
    */
   public XSParticle getParticle()
   {
      return xspart;
   }

   /**
    * [prohibited substitutions]: a subset of {extension, restriction}
    * @param restriction  Extension or restriction constants (see
    *   <code>XSConstants</code>).
    * @return True if <code>restriction</code> is a prohibited substitution,
    *   otherwise false.
    */
   public boolean isProhibitedSubstitution(short restriction)
   {
      return false;
   }

   /**
    *  [prohibited substitutions]: A subset of {extension, restriction} or
    * <code>DERIVATION_NONE</code> represented as a bit flag (see
    * <code>XSConstants</code>).
    */
   public short getProhibitedSubstitutions()
   {
      return 0;
   }

   /**
    * A set of [annotations] if it exists, otherwise an empty
    * <code>XSObjectList</code>.
    */
   public XSObjectList getAnnotations()
   {
      return annots;
   }

   /**
    * Get the type
    */
   @Override
   public short getType()
   {
      return XSTypeDefinition.COMPLEX_TYPE;
   }

   /**
    * Return whether this type definition is a simple type or complex type.
    */
   @Override
   public short getTypeCategory()
   {
      return XSTypeDefinition.COMPLEX_TYPE;
   }

   public void setAbstract(boolean anAbstract)
   {
      isAbstract = anAbstract;
   }

   public void setDerivationMethod(short deriveMethod)
   {
      this.deriveMethod = deriveMethod;
   }

   /**
    * A simple type definition corresponding to a simple content model,
    * otherwise <code>null</code>.
    */
   public void setSimpleType(XSSimpleTypeDefinition xsSimple)
   {
      this.xsSimple = xsSimple;
   }

   /**
    * A particle for a mixed or element-only content model, otherwise
    * <code>null</code>.
    */
   public void setParticle(XSParticle xspart)
   {
      this.xspart = xspart;
   }

   /**
    * A set of [annotations] if it exists, otherwise an empty
    * <code>XSObjectList</code>.
    */
   public void setAnnotations(XSObjectList annots)
   {
      this.annots = annots;
   }

   /**
    * @see XSComplexTypeDefinition.CONTENTTYPE_EMPTY
    * @see XSComplexTypeDefinition.CONTENTTYPE_SIMPLE
    * @see XSComplexTypeDefinition.CONTENTTYPE_MIXED
    * @see XSComplexTypeDefinition.CONTENTTYPE_ELEMENT
    *
    * @param contentType
    */
   public void setContentType(short contentType)
   {
      this.contentType = contentType;
   }

   //*********************************************************************
   //                       Custom Methods
   //*********************************************************************
   public void addXSAttributeUse(XSAttributeUse at)
   {
      attList.add(at);
   }

   public String toString()
   {
      return "";
   }
}
