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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeGroupDefinition;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSNamespaceItemList;
import org.apache.xerces.xs.XSNotationDeclaration;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.logging.Logger;
import org.jboss.util.NotImplementedException;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.jboss.xb.binding.NamespaceRegistry;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *  Represents a schema model
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Apr 20, 2005
 */

public class JBossXSModel implements XSModel, Cloneable
{
   // provide logging
   private static final Logger log = Logger.getLogger(JBossXSModel.class);

   private AnonymousMapper anonymousMapper = new AnonymousMapper();

   private boolean qualifiedElements = false;

   private NamespaceRegistry namespaceRegistry = new NamespaceRegistry();

   protected XSNamespaceItemList nslist = null;

   protected HashMap<String, JBossXSNamespaceItem> nsimap = new HashMap<String, JBossXSNamespaceItem>();

   public JBossXSModel()
   {
   }

   @Override
   public JBossXSModel clone() throws CloneNotSupportedException
   {
      return (JBossXSModel)super.clone();
   }

   /**
    * Convenience method. Returns a list of all namespaces that belong to
    * this schema. The value <code>null</code> is not a valid namespace
    * name, but if there are components that do not have a target namespace
    * , <code>null</code> is included in this list.
    */
   public StringList getNamespaces()
   {
      return new JBossXSStringList(nsimap.keySet());
   }

   /**
    * A set of namespace schema information information items (of type
    * <code>XSNamespaceItem</code>), one for each namespace name which
    * appears as the target namespace of any schema component in the schema
    * used for that assessment, and one for absent if any schema component
    * in the schema had no target namespace. For more information see
    * schema information.
    */
   public XSNamespaceItemList getNamespaceItems()
   {

      nslist = new JBossXSNamespaceItemList(nsimap.values());

      //One for the default xsd
      JBossXSNamespaceItem nsxsd = new JBossXSNamespaceItem(Constants.NS_SCHEMA_XSD, namespaceRegistry, qualifiedElements);
      ((JBossXSNamespaceItemList)nslist).addItem(nsxsd);
      return nslist;
   }

   /**
    * Returns a list of top-level components, i.e. element declarations,
    * attribute declarations, etc.
    * @param objectType The type of the declaration, i.e.
    *   <code>ELEMENT_DECLARATION</code>. Note that
    *   <code>XSTypeDefinition.SIMPLE_TYPE</code> and
    *   <code>XSTypeDefinition.COMPLEX_TYPE</code> can also be used as the
    *   <code>objectType</code> to retrieve only complex types or simple
    *   types, instead of all types.
    * @return  A list of top-level definitions of the specified type in
    *   <code>objectType</code> or an empty <code>XSNamedMap</code> if no
    *   such definitions exist.
    */
   public XSNamedMap getComponents(short objectType)
   {
      JBossXSNamedMap map = new JBossXSNamedMap();
      JBossXSStringList sl = (JBossXSStringList)getNamespaces();
      int len = sl != null ? sl.getLength() : 0;

      for (int i = 0; i < len; i++)
      {
         String ns = sl.item(i);
         JBossXSNamespaceItem ni = nsimap.get(ns);
         JBossXSNamedMap nm = null;
         if (ni != null)
         {
            nm = (JBossXSNamedMap)ni.getComponents(objectType);
            map.addItems(nm.toList());
         }
      }

      return map;
   }

   /**
    * Convenience method. Returns a list of top-level component declarations
    * that are defined within the specified namespace, i.e. element
    * declarations, attribute declarations, etc.
    * @param objectType The type of the declaration, i.e.
    *   <code>ELEMENT_DECLARATION</code>.
    * @param namespace The namespace to which the declaration belongs or
    *   <code>null</code> (for components with no target namespace).
    * @return  A list of top-level definitions of the specified type in
    *   <code>objectType</code> and defined in the specified
    *   <code>namespace</code> or an empty <code>XSNamedMap</code>.
    */
   public XSNamedMap getComponentsByNamespace(short objectType, String namespace)
   {
      JBossXSNamedMap map = new JBossXSNamedMap();

      JBossXSNamespaceItem ni = nsimap.get(namespace);
      if (ni == null)
         return map;

      return ni.getComponents(objectType);
   }

   /**
    *  [annotations]: a set of annotations if it exists, otherwise an empty
    * <code>XSObjectList</code>.
    */
   public XSObjectList getAnnotations()
   {
      List lst = new ArrayList();
      JBossXSObjectList objlist = new JBossXSObjectList(lst);
      Set<String> keyset = nsimap.keySet();
      for (String ns : keyset)
      {
         XSNamespaceItem xs = nsimap.get(ns);
         objlist.addObjects(xs.getAnnotations());
      }
      return objlist;
   }

   /**
    * Convenience method. Returns a top-level element declaration.
    * @param name The name of the declaration.
    * @param namespace The namespace of the declaration, otherwise
    *   <code>null</code>.
    * @return A top-level element declaration or <code>null</code> if such a
    *   declaration does not exist.
    */
   public XSElementDeclaration getElementDeclaration(String name, String namespace)
   {
      if (name == null)
         return null;

      if (name.startsWith(">") || name.endsWith("]"))
         return anonymousMapper.getElementDeclaration(name, namespace);

      JBossXSNamespaceItem ni = nsimap.get(namespace);
      if (ni == null)
         return null;
      return ni.getElementDeclaration(name);
   }

   /**
    * Convenience method. Returns a top-level attribute declaration.
    * @param name The name of the declaration.
    * @param namespace The namespace of the declaration, otherwise
    *   <code>null</code>.
    * @return A top-level attribute declaration or <code>null</code> if such
    *   a declaration does not exist.
    */
   public XSAttributeDeclaration getAttributeDeclaration(String name, String namespace)
   {
      JBossXSNamespaceItem ni = nsimap.get(namespace);
      if (ni == null)
         return null;
      return ni.getAttributeDeclaration(name);
   }

   /**
    * Convenience method. Returns a top-level simple or complex type
    * definition.
    * @param name The name of the definition.
    * @param namespace The namespace of the declaration, otherwise
    *   <code>null</code>.
    * @return An <code>XSTypeDefinition</code> or <code>null</code> if such
    *   a definition does not exist.
    */
   public XSTypeDefinition getTypeDefinition(String name, String namespace)
   {
      if (name == null)
         return null;

      if (name.startsWith(">") || name.endsWith("]"))
         return anonymousMapper.getTypeDefinition(name, namespace);

      JBossXSNamespaceItem ni = nsimap.get(namespace);
      if (ni == null)
         return null;
      return ni.getTypeDefinition(name);
   }

   /**
    * Convenience method. Returns a top-level attribute group definition.
    * @param name The name of the definition.
    * @param namespace The namespace of the definition, otherwise
    *   <code>null</code>.
    * @return A top-level attribute group definition or <code>null</code> if
    *   such a definition does not exist.
    */
   public XSAttributeGroupDefinition getAttributeGroup(String name, String namespace)
   {
      return null;
   }

   /**
    * Convenience method. Returns a top-level model group definition.
    * @param name The name of the definition.
    * @param namespace The namespace of the definition, otherwise
    *   <code>null</code>.
    * @return A top-level model group definition or <code>null</code> if
    *   such a definition does not exist.
    */
   public XSModelGroupDefinition getModelGroupDefinition(String name, String namespace)
   {
      return null;
   }

   /**
    * Convenience method. Returns a top-level notation declaration.
    * @param name The name of the declaration.
    * @param namespace The namespace of the declaration, otherwise
    *   <code>null</code>.
    * @return A top-level notation declaration or <code>null</code> if such
    *   a declaration does not exist.
    */
   public XSNotationDeclaration getNotationDeclaration(String name, String namespace)
   {
      return null;
   }

   public void addXSAnnotation(XSAnnotation xa)
   {
      String ns = xa.getNamespace();
      if (ns == null && nsimap.keySet().size() == 1)
      {
         ns = nsimap.keySet().iterator().next();
      }
      if (ns != null)
      {
         createNamespaceItemIfNotExistent(ns);
         JBossXSNamespaceItem jbnm = nsimap.get(ns);
         jbnm.addXSAnnotation(xa);
      }
      else
      {
         log.trace("Cannot assign XSAnnotation to null namespace");
      }
   }

   public void addXSAttributeDeclaration(XSAttributeDeclaration attr)
   {
      //Add attribute to the namespace item
      String ns = attr.getNamespace();
      JBossXSNamespaceItem jbnm = createNamespaceItemIfNotExistent(ns);
      jbnm.addXSAttributeDeclaration(attr);
   }

   public void addXSTypeDefinition(XSTypeDefinition xst)
   {
      //Add type to the namespace item
      String ns = xst.getNamespace();
      if (ns == null)
         throw new WSException("Illegal namespace:null");
      JBossXSNamespaceItem jbnm = createNamespaceItemIfNotExistent(ns);
      jbnm.addXSTypeDefinition(xst);

      anonymousMapper.rebuild();
   }

   public void addXSComplexTypeDefinition(XSTypeDefinition xst)
   {
      this.addXSTypeDefinition(xst);

      anonymousMapper.rebuild();
   }

   public void addXSElementDeclaration(XSElementDeclaration xsel)
   {
      //Add element to the namespace item
      String ns = xsel.getNamespace();
      JBossXSNamespaceItem jbnm = createNamespaceItemIfNotExistent(ns);
      jbnm.addXSElementDeclaration(xsel);

      anonymousMapper.rebuild();
   }

   public void addSchemaLocation(String nsURI, URL locationURL)
   {
      JBossXSNamespaceItem ni = createNamespaceItemIfNotExistent(nsURI);
      ni.addDocumentLocation(locationURL.toExternalForm());
   }

   public void addXSNamespaceItem(XSNamespaceItem xsitem)
   {
      ((JBossXSNamespaceItemList)nslist).addItem(xsitem);

      anonymousMapper.rebuild();
   }

   public void setXSNamespaceItemList(XSNamespaceItemList list)
   {
      this.nslist = list;
   }

   public void merge(JBossXSModel xsm)
   {
      JBossXSNamespaceItemList jxsm = (JBossXSNamespaceItemList)xsm.getNamespaceItems();
      int len = jxsm.getLength();
      for (int i = 0; i < len; i++)
      {
         JBossXSNamespaceItem ni = (JBossXSNamespaceItem)jxsm.item(i);
         String sns = ni.getSchemaNamespace();
         JBossXSNamespaceItem mynsi = nsimap.get(sns);
         if (mynsi != null)
            mynsi.merge(ni);
         else
         {
            //add the namespaceitem
            nsimap.put(sns, ni);
            ni.setNamespaceRegistry(namespaceRegistry);
         }
      }

      NamespaceRegistry xsmRegistry = xsm.getNamespaceRegistry();
      Iterator iter = xsmRegistry.getRegisteredPrefixes();
      while (iter.hasNext())
      {
         String prefix = (String)iter.next();
         String ns = xsmRegistry.getNamespaceURI(prefix);
         this.namespaceRegistry.registerURI(ns, prefix);
      }

      anonymousMapper.rebuild();
   }

   public void removeXSTypeDefinition(XSTypeDefinition xst)
   {
      String ns = xst.getNamespace();
      JBossXSNamespaceItem ni = nsimap.get(ns);
      ni.removeXSTypeDefinition(xst);

      anonymousMapper.rebuild();
   }

   /**
    * Given a namespaceuri, return the NamespaceItem that represents it
    * @param nsuri Namespace URI
    * @return JBossXSNamespaceItem
    */
   public JBossXSNamespaceItem getNamespaceItem(String nsuri)
   {
      return nsimap.get(nsuri);
   }

   public void writeTo(OutputStream out) throws IOException
   {
      out.write(serialize().getBytes());
   }

   public String serialize()
   {
      StringBuilder sb = serializeNamespaceItems();

      /**
       * Since the serialized string can contain multiple schema
       * definitions, we have to embed in a root element before
       * parsing for layout
       */
      sb.insert(0, "<root>");
      sb.append("</root>");
      // Layout schema
      String xsModelString = sb.toString();

      if (xsModelString.length() > 0)
      {
         try
         {
            Element root = DOMUtils.parse(xsModelString);
            //xsModelString = DOMWriter.printNode(root, true);
            xsModelString = this.getChildNodesSerialized(root);
         }
         catch (IOException e)
         {
            log.error("Cannot parse xsModelString: " + xsModelString, e);
         }

      }

      return xsModelString;
   }

   public Map<String, XSTypeDefinition> getAnonymousTypes()
   {
      return anonymousMapper.getTypes();
   }

   public Map<String, XSElementDeclaration> getAnonymousElements()
   {
      return anonymousMapper.getElements();
   }

   public boolean isQualifiedElements()
   {
      return qualifiedElements;
   }

   public void setQualifiedElements(boolean qualifiedElements)
   {
      this.qualifiedElements = qualifiedElements;
      for (JBossXSNamespaceItem item : nsimap.values())
         item.setQualifiedElements(qualifiedElements);
   }

   public NamespaceRegistry getNamespaceRegistry()
   {
      return namespaceRegistry;
   }

   public void eagerInitialize()
   {
      anonymousMapper.build();
   }

   private String registerNamespace(String ns)
   {
      String prefix = namespaceRegistry.getPrefix(ns);

      if (prefix != null)
         return prefix;

      // XML Namespace MUST ALWAYS BE the "xml" prefix
      if (Constants.NS_XML.equals(ns))
         prefix = Constants.PREFIX_XML;

      return namespaceRegistry.registerURI(ns, prefix);
   }

   private JBossXSNamespaceItem createNamespaceItemIfNotExistent(String ns)
   {
      if (ns == null)
         throw new IllegalArgumentException("Illegal null argument:ns");

      JBossXSNamespaceItem jbnm = nsimap.get(ns);
      if (jbnm == null)
      {
         jbnm = new JBossXSNamespaceItem(ns, namespaceRegistry, qualifiedElements);
         nsimap.put(ns, jbnm);
         registerNamespace(ns);
      }

      return jbnm;
   }

   private StringBuilder serializeNamespaceItems()
   {
      StringBuilder sb = new StringBuilder();
      //Write a schema definition for each namespaceitem that is custom
      Collection<JBossXSNamespaceItem> col = nsimap.values();
      for (JBossXSNamespaceItem i : col)
      {
         String nameS = i.getSchemaNamespace();
         if (Constants.NS_SCHEMA_XSD.equals(nameS) || Constants.URI_SOAP11_ENC.equals(nameS))
            continue;

         sb.append(i.toString());
      }

      return sb;
   }

   private String getChildNodesSerialized(Element root)
   {
      StringBuilder sb = new StringBuilder();
      Iterator iter = DOMUtils.getChildElements(root);
      while (iter != null && iter.hasNext())
      {
         Node n = (Node)iter.next();
         sb.append(DOMWriter.printNode(n, true));
         sb.append("\n");
      }
      return sb.toString();
   }

   private class AnonymousMapper implements Serializable
   {
      private static final long serialVersionUID = 5572350092914194023L;

      private HashMap<String, XSTypeDefinition> anonymousTypeMap;

      private HashMap<String, XSElementDeclaration> anonymousElementMap;

      // not really a stack, but it does contain items on the stack
      private HashSet<XSComplexTypeDefinition> processed = new HashSet<XSComplexTypeDefinition>();
      
      /**
       * Triggers a rebuild of anonymous types only if a build has occured before.
       */
      public void rebuild()
      {
         if (anonymousTypeMap != null)
            build();
      }

      /**
       * Builds the anonymous type mapping. This is intended to be called lazily.
       */
      public void build()
      {
         XSModel model = JBossXSModel.this;

         anonymousTypeMap = new HashMap<String, XSTypeDefinition>();

         anonymousElementMap = new HashMap<String, XSElementDeclaration>();

         
         processed.clear();
         
         XSNamedMap namedMap = model.getComponents(XSConstants.TYPE_DEFINITION);
         for (int i = 0; i < namedMap.getLength(); i++)
         {
            XSTypeDefinition type = (XSTypeDefinition)namedMap.item(i);
            if (type.getTypeCategory() != XSTypeDefinition.COMPLEX_TYPE)
               continue;

            analyzeComplexType((XSComplexTypeDefinition)type, null, type.getNamespace());
         }

         namedMap = model.getComponents(XSConstants.ELEMENT_DECLARATION);
         for (int i = 0; i < namedMap.getLength(); i++)
         {
            XSElementDeclaration element = (XSElementDeclaration)namedMap.item(i);
            analyzeElement(element, null, element.getNamespace(), null, null);
         }
         processed.clear();
      }

      private void analyzeElement(XSElementDeclaration element, String parentName, String namespace, Integer minOccurs, Integer maxOccurs)
      {
         String name = element.getName();

         if (element.getScope() != XSConstants.SCOPE_GLOBAL)
         {
            name = parentName + ">" + name;
            anonymousElementMap.put(namespace + ":" + name, element);
         }

         if (maxOccurs != null && maxOccurs.intValue() > 1)
         {
            String key = namespace + ":" + name + "[" + minOccurs.intValue() + "," + maxOccurs.intValue() + "]";
            anonymousTypeMap.put(key, createArrayWrapperComplexType(element, name, namespace, minOccurs, maxOccurs));
            if (minOccurs.intValue() == 1)
            {
               key = namespace + ":" + name + "[" + "," + maxOccurs.intValue() + "]";
               anonymousTypeMap.put(key, createArrayWrapperComplexType(element, name, namespace, minOccurs, maxOccurs));
            }
         }

         XSTypeDefinition type = element.getTypeDefinition();
         if (type.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE)
            analyzeComplexType((XSComplexTypeDefinition)type, name, namespace);

         if (type.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE)
            analyzeSimpleType((XSSimpleTypeDefinition)type, name, namespace);
      }

      private XSComplexTypeDefinition createArrayWrapperComplexType(XSElementDeclaration element, String name, String namespace, Integer minOccurs, Integer maxOccurs)
      {
         JBossXSComplexTypeDefinition definition = new JBossXSComplexTypeDefinition(name, namespace);
         definition.setAnonymous(true);

         JBossXSModelGroup group = new JBossXSModelGroup();
         group.setCompositor(XSModelGroup.COMPOSITOR_SEQUENCE);
         List<XSParticle> particles = new ArrayList<XSParticle>(1);
         JBossXSParticle particle = new JBossXSParticle();
         particle.setMaxOccurs(maxOccurs);
         particle.setMinOccurs(minOccurs);
         particle.setTerm(element);
         particles.add(particle);
         group.setParticles(particles);

         particle = new JBossXSParticle();
         particle.setTerm(group);
         definition.setParticle(particle);

         return definition;
      }

      private String analyzeType(XSTypeDefinition type, String parentName, String namespace)
      {
         String name;
         if (type.getAnonymous())
            name = ">" + parentName;
         else name = type.getName();

         if (type.getAnonymous())
         {
            anonymousTypeMap.put(namespace + ":" + name, type);
            if(log.isDebugEnabled()) log.debug("Registered as anon type: {" + namespace + ":" + name + "} -> " + type);
         }
         return name;
      }

      private void analyzeSimpleType(XSSimpleTypeDefinition simpleType, String parentName, String namespace)
      {
         analyzeType(simpleType, parentName, namespace);
      }

      private void analyzeComplexType(XSComplexTypeDefinition complexType, String parentName, String namespace)
      {
         // Prevent reentrancy
         if (processed.contains(complexType))
         {
            return;
         }

         processed.add(complexType);
         String name = analyzeType(complexType, parentName, namespace);
         analyzeParticle(complexType.getParticle(), name, namespace);
      }

      private void analyzeParticle(XSParticle particle, String parentName, String namespace)
      {
         // Is this right, can a particle be null?
         if (particle == null)
            return;
         XSTerm term = particle.getTerm();

         // Is this right, can a term be null?
         if (term == null)
            return;
         switch (term.getType())
         {
            case XSConstants.MODEL_GROUP:
               XSModelGroup group = (XSModelGroup)term;
               XSObjectList list = group.getParticles();
               for (int i = 0; i < list.getLength(); i++)
                  analyzeParticle((XSParticle)list.item(i), parentName, namespace);
               break;
            case XSConstants.ELEMENT_DECLARATION:
               XSElementDeclaration decl = (XSElementDeclaration)term;
               analyzeElement(decl, parentName, namespace, new Integer(particle.getMinOccurs()), new Integer(particle.getMaxOccurs()));
         }
      }

      public XSTypeDefinition getTypeDefinition(String name, String namespace)
      {
         // We lazily build this, after the first anonymous type name lookup
         if (anonymousTypeMap == null)
            build();

         return anonymousTypeMap.get(namespace + ":" + name);
      }

      public XSElementDeclaration getElementDeclaration(String name, String namespace)
      {
         // We lazily build this, after the first anonymous type name lookup
         if (anonymousElementMap == null)
            build();

         return anonymousElementMap.get(namespace + ":" + name);
      }

      public Map<String, XSElementDeclaration> getElements()
      {
         if (anonymousElementMap == null)
            build();

         // avoid the copy, trust the client
         return anonymousElementMap;
      }

      public Map<String, XSTypeDefinition> getTypes()
      {
         if (anonymousTypeMap == null)
            build();

         // avoid the copy, trust the client
         return anonymousTypeMap;
      }
   }

   public XSObjectList getSubstitutionGroup(XSElementDeclaration arg0)
   {
      throw new NotImplementedException();
   }
}
