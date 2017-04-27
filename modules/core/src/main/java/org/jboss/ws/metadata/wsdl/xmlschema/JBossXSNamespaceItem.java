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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeGroupDefinition;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSNotationDeclaration;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.xb.binding.NamespaceRegistry;

/**
 * Xerces Schema API Implementation
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Apr 20, 2005
 */
public class JBossXSNamespaceItem implements XSNamespaceItem
{
   // provide logging
   private static final Logger log = Logger.getLogger(JBossXSNamespaceItem.class);

   private String schemaNamespace = null;
   private List<String> docLocations = null;

   private Map<String,XSAnnotation> anns = new HashMap<String,XSAnnotation>();
   private Map<String,XSAttributeDeclaration> attrs = new HashMap<String,XSAttributeDeclaration>();
   private Map<String,XSElementDeclaration> elements = new HashMap<String,XSElementDeclaration>();
   private Map<String,XSTypeDefinition> types = new HashMap<String,XSTypeDefinition>();

   private boolean qualifiedElements = false;

   private NamespaceRegistry namespaceRegistry;

   public JBossXSNamespaceItem(String ns, NamespaceRegistry namespaceRegistry, boolean qualifiedElements)
   {
      this.schemaNamespace = ns;
      this.namespaceRegistry = namespaceRegistry;
      this.qualifiedElements = qualifiedElements;
   }

   /**
    * [schema namespace]: A namespace name or <code>null</code> if absent.
    */
   public String getSchemaNamespace()
   {
      return schemaNamespace;
   }

   /**
    * [schema components]: a list of top-level components, i.e. element
    * declarations, attribute declarations, etc.
    * @param objectType The type of the declaration, i.e.
    *   <code>ELEMENT_DECLARATION</code>. Note that
    *   <code>XSTypeDefinition.SIMPLE_TYPE</code> and
    *   <code>XSTypeDefinition.COMPLEX_TYPE</code> can also be used as the
    *   <code>objectType</code> to retrieve only complex types or simple
    *   types, instead of all types.
    * @return  A list of top-level definition of the specified type in
    *   <code>objectType</code> or an empty <code>XSNamedMap</code> if no
    *   such definitions exist.
    */
   public XSNamedMap getComponents(short objectType)
   {
      JBossXSNamedMap jbnm = new JBossXSNamedMap();
      if(objectType == XSConstants.ELEMENT_DECLARATION && elements.size() > 0)
              jbnm.addItems(elements.values());
      else
      {
         if(objectType == XSConstants.ATTRIBUTE_DECLARATION && attrs.size() > 0)
         {
            jbnm.addItems(attrs.values());
         }
         else
            if(objectType == XSConstants.TYPE_DEFINITION && types.size() > 0)
            {
               jbnm.addItems(types.values());
            }
            else
               if(objectType == XSTypeDefinition.COMPLEX_TYPE)
               {
                  Collection col = types.values();
                  jbnm.addItems(getTypes(col,XSTypeDefinition.COMPLEX_TYPE));
               }
            else
            if(objectType == XSTypeDefinition.SIMPLE_TYPE)
            {
               Collection col = types.values();
               jbnm.addItems(getTypes(col,XSTypeDefinition.SIMPLE_TYPE));
            }
      }

      return jbnm;
   }

   /**
    *  [annotations]: a set of annotations if it exists, otherwise an empty
    * <code>XSObjectList</code>.
    */
   public XSObjectList getAnnotations()
   {
      List lst = new ArrayList();
      lst.addAll(anns.values());
      return new JBossXSObjectList(lst);
   }

   /**
    * Convenience method. Returns a top-level element declaration.
    * @param name The name of the declaration.
    * @return A top-level element declaration or <code>null</code> if such a
    *   declaration does not exist.
    */
   public XSElementDeclaration getElementDeclaration(String name)
   {
      JBossXSElementDeclaration jbe = null;
      //Ensure we return JBossXSElementDeclaration
      if(elements != null)
      {
         XSElementDeclaration xe = elements.get(name);
         if(xe != null && !(xe instanceof JBossXSElementDeclaration))
            jbe = new JBossXSElementDeclaration(xe);
         else
            return xe;
      }
      return jbe;
   }

   /**
    * Convenience method. Returns a top-level attribute declaration.
    * @param name The name of the declaration.
    * @return A top-level attribute declaration or <code>null</code> if such
    *   a declaration does not exist.
    */
   public XSAttributeDeclaration getAttributeDeclaration(String name)
   {
      if(attrs != null)  return attrs.get(name);
      return null;
   }

   /**
    * Convenience method. Returns a top-level simple or complex type
    * definition.
    * @param name The name of the definition.
    * @return An <code>XSTypeDefinition</code> or <code>null</code> if such
    *   a definition does not exist.
    */
   public XSTypeDefinition getTypeDefinition(String name)
   {
      if(types != null) return types.get(name);
      return null;
   }

   /**
    * Convenience method. Returns a top-level attribute group definition.
    * @param name The name of the definition.
    * @return A top-level attribute group definition or <code>null</code> if
    *   such a definition does not exist.
    */
   public XSAttributeGroupDefinition getAttributeGroup(String name)
   {
      return null;
   }

   /**
    * Convenience method. Returns a top-level model group definition.
    * @param name The name of the definition.
    * @return A top-level model group definition definition or
    *   <code>null</code> if such a definition does not exist.
    */
   public XSModelGroupDefinition getModelGroupDefinition(String name)
   {
      return null;
   }

   /**
    * Convenience method. Returns a top-level notation declaration.
    * @param name The name of the declaration.
    * @return A top-level notation declaration or <code>null</code> if such
    *   a declaration does not exist.
    */
   public XSNotationDeclaration getNotationDeclaration(String name)
   {
      return null;
   }

   /**
    * [document location] - a list of location URIs for the documents that
    * contributed to the <code>XSModel</code>.
    */
   public StringList getDocumentLocations()
   {
      if(docLocations  == null) return new JBossXSStringList();
      JBossXSStringList strList = new JBossXSStringList(docLocations);
      return strList;
   }

   //Setters

   //Custom methods
   public void addDocumentLocation(String uri)
   {
     if(docLocations == null)
        docLocations = new ArrayList<String>();
     docLocations.add(uri);
   }

   public void addDocumentLocations(List<String> uri)
   {
     if(docLocations == null)
        docLocations = new ArrayList<String>();
     docLocations.addAll(uri);
   }

   /**
    * Add an XSAnnotation
    */
   public void addXSAnnotation(XSAnnotation xa)
   {
      if(xa == null)
         throw new IllegalArgumentException("Illegal Null Argument:xa");
      anns.put(xa.getName(),xa);
   }

   /**
    * Add an XSAttributeDeclaration
    */
   public void addXSAttributeDeclaration(XSAttributeDeclaration att)
   {
      if(att == null)
         throw new IllegalArgumentException("att is null");
      attrs.put(att.getName(),att);
   }

   /**
    * Add an XSElementDeclaration
    * @param el
    */
   public void addXSElementDeclaration(XSElementDeclaration el)
   {
      if(el == null)
         throw new IllegalArgumentException("Element is null");
      elements.put(el.getName(),el);
   }

   /**
    * Add an XSTypeDefinition
    * @param xsType
    */
   public void addXSTypeDefinition(XSTypeDefinition xsType)
   {
      if(xsType == null)
         throw new IllegalArgumentException("type is null");

      String xsTypeName = xsType.getName();
      log.trace("addXSTypeDefinition: " + xsTypeName);

      types.put(xsTypeName,xsType);
   }


   /**
    * Overrides the toString method
    */
   @Override
   public String toString()
   {
      /*
       * FIXME - The SOAP Encoding namespace handling should be revisited. This
       * routine should really operate purely off of the registry, instead of
       * printing constant declarations, and having to double check that the
       * registry does not contain them.
       *
       * This order is currently maintained to preserve the format of the
       * generated schema files in the test harness.
       */
      if(isEmpty()) return "";

      WSSchemaUtils sutils = WSSchemaUtils.getInstance(namespaceRegistry, schemaNamespace);

      StringBuilder buffer = new StringBuilder();
      buffer.append( "<schema  targetNamespace='" + schemaNamespace  + "'");
      buffer.append(" xmlns:" + Constants.PREFIX_SOAP11_ENC + "='" + Constants.URI_SOAP11_ENC + "'");
      // XML Namespace can only be assigned to the XML prefix
      if (! schemaNamespace.equals(Constants.NS_XML))
         buffer.append(" xmlns:" + Constants.PREFIX_TNS + "='" +  schemaNamespace  + "'");
      buffer.append(" xmlns:" + Constants.PREFIX_XSI + "='" + Constants.NS_SCHEMA_XSI + "'");
      buffer.append(" xmlns='" + Constants.NS_SCHEMA_XSD + "'");

      //Append any custom prefixes
      Iterator iter = namespaceRegistry.getRegisteredPrefixes();
      while (iter.hasNext())
      {
         String prefix = (String) iter.next();
         String ns = namespaceRegistry.getNamespaceURI(prefix);
         if (ns.equals(schemaNamespace) && ns.equals(Constants.NS_XML) == false)
            continue;
         if (ns.equals(Constants.URI_SOAP11_ENC))
            continue;
         if (ns.equals(Constants.NS_SCHEMA_XSI))
            continue;
         buffer.append(" xmlns:" + prefix + "='" + ns +"'");
      }

      if (qualifiedElements)
         buffer.append(" elementFormDefault='qualified'");

      buffer.append(">");

      //Write the import namespaces
      iter = namespaceRegistry.getRegisteredURIs();
      while (iter.hasNext())
      {
         String ns = (String) iter.next();
         if (ns.equals(schemaNamespace))
            continue;
         if (ns.equals(Constants.URI_SOAP11_ENC))
            continue;
         if (ns.equals(Constants.NS_SCHEMA_XSI))
            continue;
         buffer.append("<import namespace='" + ns + "'/>");
      }

      //Sort the types
      //TreeSet<String> treeset = new TreeSet<String>(types.keySet());
      TreeSet<String> treeset = new TreeSet<String>(new XSComparator());
      treeset.addAll(types.keySet());
      for(String key: treeset)
      {
         buffer.append(sutils.write(types.get(key)));
      }

      // Serialize the elements
      //treeset = new TreeSet<String>(elements.keySet());
      treeset = new TreeSet<String>(new XSComparator());
      treeset.addAll(elements.keySet());
      for( String key: treeset)
      {
         buffer.append(sutils.write(elements.get(key) ));
      }
      buffer.append("</schema>");
      return buffer.toString();
   }

   public void merge(JBossXSNamespaceItem nsi)
   {
      //Merge the attributes
      JBossXSNamedMap nmap = (JBossXSNamedMap)nsi.getComponents(XSConstants.ATTRIBUTE_DECLARATION);
      int len = nmap.getLength();
      for(int i=0;i<len;i++)
      {
         XSAttributeDeclaration xatt = (XSAttributeDeclaration)nmap.item(i);
         this.addXSAttributeDeclaration(xatt);
      }

      //Merge the types
      nmap = (JBossXSNamedMap)nsi.getComponents(XSConstants.TYPE_DEFINITION);
      len = nmap.getLength();
      for(int i=0;i<len;i++)
      {
         XSTypeDefinition xt = (XSTypeDefinition)nmap.item(i);
         this.addXSTypeDefinition(xt);
      }

      //    Merge the elements
      nmap = (JBossXSNamedMap)nsi.getComponents(XSConstants.ELEMENT_DECLARATION);
      len = nmap.getLength();
      for(int i=0;i<len;i++)
      {
         XSElementDeclaration xe = (XSElementDeclaration)nmap.item(i);
         this.addXSElementDeclaration(xe);
      }
   }

   /**
    * Delete a XSTypeDefinition
    *
    * @param xst
    */
   public void removeXSTypeDefinition(XSTypeDefinition xst)
   {
      String name = xst.getName();
      types.remove(name);
   }

   /**
    * Delete a XSTypeDefinition
    *
    * @param xst
    */
   public void removeXSElementDeclaration(XSElementDeclaration xel)
   {
      String name = xel.getName();
      elements.remove(name);
   }

   public boolean isQualifiedElements()
   {
      return qualifiedElements;
   }

   public void setQualifiedElements(boolean qualifiedElements)
   {
      this.qualifiedElements = qualifiedElements;
   }

   public NamespaceRegistry getNamespaceRegistry()
   {
      return namespaceRegistry;
   }

   public void setNamespaceRegistry(NamespaceRegistry namespaceRegistry)
   {
      this.namespaceRegistry = namespaceRegistry;
   }

   //PRIVATE METHODS
   private Collection getTypes(Collection col, short objectType)
   {
      if(objectType != XSTypeDefinition.SIMPLE_TYPE && objectType != XSTypeDefinition.COMPLEX_TYPE)
         throw new IllegalArgumentException("objectType should be simple type or complex type");
      Collection values = new ArrayList();
      for(Object  obj : col)
      {
         if(objectType == XSTypeDefinition.COMPLEX_TYPE &&
               obj instanceof XSComplexTypeDefinition )
            values.add(obj);
         else
            if(objectType == XSTypeDefinition.SIMPLE_TYPE &&
                  obj instanceof XSSimpleTypeDefinition )
               values.add(obj);
      }
      return values;
   }

   private boolean isEmpty()
   {
      boolean bool = true;
      if(attrs.size() > 0) return false;
      if(types.size() > 0 ) return false;
      if(elements.size()>0) return false;
      return bool;
   }

   private class XSComparator implements Comparator
   {
      public int compare(Object arg0, Object arg1)
      {
         String str1 = (String)arg0;
         String str2 = (String)arg1;

         if(Character.isUpperCase(str1.charAt(0))
            && Character.isLowerCase(str2.charAt(0)) )
            return 1;
         return str1.compareTo(str2);
      }
   }
}
