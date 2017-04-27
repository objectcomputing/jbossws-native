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

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;

/**
 *  XSSimpleTypeDefinition
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 4, 2005
 */
public class JBossXSSimpleTypeDefinition
extends JBossXSTypeDefinition
implements XSSimpleTypeDefinition
{
   private XSSimpleTypeDefinition xsSimple = null;

   private List<String> enumerations = new ArrayList<String>();

   public JBossXSSimpleTypeDefinition()
   {
   }

   public JBossXSSimpleTypeDefinition(XSSimpleTypeDefinition xs)
   {
      this.xsSimple = xs;
   }

   public short getVariety()
   {
      return xsSimple.getVariety();
   }

   public XSSimpleTypeDefinition getPrimitiveType()
   {
      return xsSimple.getPrimitiveType();
   }

   public short getBuiltInKind()
   {
      return xsSimple.getBuiltInKind();
   }

   public XSSimpleTypeDefinition getItemType()
   {
      return xsSimple.getItemType();
   }

   public XSObjectList getMemberTypes()
   {
      return xsSimple.getMemberTypes();
   }

   public short getDefinedFacets()
   {
      return xsSimple.getDefinedFacets();
   }

   public boolean isDefinedFacet(short arg0)
   {
      return xsSimple.isDefinedFacet(arg0);
   }

   public short getFixedFacets()
   {
      return xsSimple.getFixedFacets();
   }

   public boolean isFixedFacet(short arg0)
   {
      return xsSimple.isFixedFacet(arg0);
   }

   public String getLexicalFacetValue(short arg0)
   {
      return xsSimple.getLexicalFacetValue(arg0);
   }

   public StringList getLexicalEnumeration()
   {
      if (xsSimple == null)
         return new JBossXSStringList(enumerations);

      return xsSimple.getLexicalEnumeration();
   }

   public void addLexicalEnumeration(String enumeration)
   {
      this.enumerations.add(enumeration);
   }

   public StringList getLexicalPattern()
   {
      return xsSimple.getLexicalPattern();
   }

   public short getOrdered()
   {
      return xsSimple.getOrdered();
   }

   public boolean getFinite()
   {
       return xsSimple.getFinite();
   }

   public boolean getBounded()
   {
      return xsSimple.getBounded();
   }

   public boolean getNumeric()
   {
      return xsSimple.getNumeric();
   }

   public XSObjectList getFacets()
   {
      return xsSimple.getFacets();
   }

   public XSObjectList getMultiValueFacets()
   {
      return xsSimple.getMultiValueFacets();
   }

   public XSObjectList getAnnotations()
   {
      return xsSimple.getAnnotations();
   }

   /**
    * Get the type
    */
   @Override
   public short getType()
   {
      return XSTypeDefinition.SIMPLE_TYPE;
   }

   /**
    * Return whether this type definition is a simple type or complex type.
    */
   @Override
   public short getTypeCategory()
   {
      return XSTypeDefinition.SIMPLE_TYPE;
   }


   //***************************
   //Override base class methods
   //****************************
   @Override
   public String getName()
   {
      if (xsSimple == null)
         return name;
      return xsSimple.getName();
   }

   @Override
   public String getNamespace()
   {
      if (xsSimple == null)
         return namespace;
      return xsSimple.getNamespace();
   }
}
