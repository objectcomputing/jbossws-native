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

import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.ws.WSException;

/**
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Apr 20, 2005
 */
public class JBossXSTypeDefinition extends JBossXSObject implements XSTypeDefinition
{
   private static final long serialVersionUID = -3317350531846406564L;
   
   protected boolean anonymous = false;
   protected JBossXSTypeDefinition baseType;
   protected boolean isFinal = false;

   public JBossXSTypeDefinition()
   {
   }

   public JBossXSTypeDefinition(String name, String namespace)
   {
      super(name, namespace);
   }

   /**
    * Create a new JBossXSTypeDefinition
    * while reading data from a XSTypeDefinition
    * 
    * @param xt
    */
   public JBossXSTypeDefinition(XSTypeDefinition xt)
   {
      super(xt.getName(), xt.getNamespace());
      setAnonymous(xt.getAnonymous());

      XSTypeDefinition xbase = xt.getBaseType();
      if (xbase != null)
      {
         if (xbase instanceof JBossXSTypeDefinition == false && !("anyType".equals(xbase.getName())))
            baseType = new JBossXSTypeDefinition(xbase);
      }

   }

   /**
    * Return whether this type definition is a simple type or complex type.
    */
   public short getTypeCategory()
   {
      throw new WSException("Type unidentified");
   }

   /**
    * {base type definition}: either a simple type definition or a complex
    * type definition.
    */
   public XSTypeDefinition getBaseType()
   {
      return baseType;
   }

   /**
    * {final}. For a complex type definition it is a subset of {extension,
    * restriction}. For a simple type definition it is a subset of
    * {extension, list, restriction, union}.
    * @param restriction  Extension, restriction, list, union constants
    *   (defined in <code>XSConstants</code>).
    * @return True if <code>restriction</code> is in the final set,
    *   otherwise false.
    */
   public boolean isFinal(short restriction)
   {
      return isFinal;
   }

   /**
    * For complex types the returned value is a bit combination of the subset
    * of {<code>DERIVATION_EXTENSION, DERIVATION_RESTRICTION</code>}
    * corresponding to <code>final</code> set of this type or
    * <code>DERIVATION_NONE</code>. For simple types the returned value is
    * a bit combination of the subset of {
    * <code>DERIVATION_RESTRICTION, DERIVATION_EXTENSION, DERIVATION_UNION, DERIVATION_LIST</code>
    * } corresponding to <code>final</code> set of this type or
    * <code>DERIVATION_NONE</code>.
    */
   public short getFinal()
   {
      return 0;
   }

   /** Convenience attribute. A boolean that specifies if the type definition is anonymous.
    */
   public boolean getAnonymous()
   {
      return anonymous;
   }

   public void setAnonymous(boolean anonymous)
   {
      this.anonymous = anonymous;
   }

   /**
    * Convenience method which checks if this type is derived from the given
    * <code>ancestorType</code>.
    * @param ancestorType  An ancestor type definition.
    * @param derivationMethod  A bit combination representing a subset of {
    *   <code>DERIVATION_RESTRICTION, DERIVATION_EXTENSION, DERIVATION_UNION, DERIVATION_LIST</code>
    *   }.
    * @return  True if this type is derived from <code>ancestorType</code>
    *   using only derivation methods from the <code>derivationMethod</code>
    *   .
    */
   public boolean derivedFromType(XSTypeDefinition ancestorType, short derivationMethod)
   {
      return false;
   }

   /**
    * Convenience method which checks if this type is derived from the given
    * ancestor type.
    * @param namespace  An ancestor type namespace.
    * @param name  An ancestor type name.
    * @param derivationMethod  A bit combination representing a subset of {
    *   <code>DERIVATION_RESTRICTION, DERIVATION_EXTENSION, DERIVATION_UNION, DERIVATION_LIST</code>
    *   }.
    * @return  True if this type is derived from <code>ancestorType</code>
    *   using only derivation methods from the <code>derivationMethod</code>
    *   .
    */
   public boolean derivedFrom(String namespace, String name, short derivationMethod)
   {
      return false;
   }

   public void setBaseType(XSTypeDefinition baseT)
   {
      if (baseT instanceof JBossXSTypeDefinition)
      {
         this.baseType = (JBossXSTypeDefinition)baseT;
      }
      else
      {
         this.baseType = new JBossXSTypeDefinition(baseT);
      }
   }

   public void setFinal(boolean aFinal)
   {
      isFinal = aFinal;
   }

}
