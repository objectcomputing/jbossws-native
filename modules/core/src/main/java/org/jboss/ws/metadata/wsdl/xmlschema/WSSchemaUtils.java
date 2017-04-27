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
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSNamespaceItemList;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.wsdl.xsd.SchemaUtils;
import org.jboss.wsf.common.DOMWriter;
import org.jboss.xb.binding.NamespaceRegistry;

/**
 * Singleton class that works on the JBoss version of XSModel
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jun 3, 2005
 */

public class WSSchemaUtils
{
   private static final String xsNS = Constants.NS_SCHEMA_XSD;
   private static SchemaUtils utils = SchemaUtils.getInstance();

   private NamespaceRegistry namespaceRegistry;

   private String targetNamespace = null;

   public static WSSchemaUtils getInstance(NamespaceRegistry namespaceRegistry, String targetNamespace)
   {
      return new WSSchemaUtils(namespaceRegistry, targetNamespace);
   }

   private WSSchemaUtils(NamespaceRegistry namespaceRegistry, String targetNamespace)
   {
      this.namespaceRegistry = namespaceRegistry;
      this.targetNamespace = targetNamespace;
   }

   /**
    * Checks whether given a targetNS and other regular schema namespaces,
    * the passed "checkNS" is a custom namespace
    * @param targetNS Target Namespace of the schema
    * @param checkNS Namespace that needs to be checked if its a custom namespace
    * @return true - if checkNS is a custom namespace, false - otherwise
    */
   public boolean checkCustomNamespace(String targetNS, String checkNS)
   {
      String[] nsarr = new String[] { xsNS, Constants.NS_SCHEMA_XSI };
      List<String> knownNamespaces = Arrays.asList(nsarr);
      boolean isCustom = false;
      if (xsNS.equals(targetNS))
         throw new IllegalArgumentException("targetNamespace cannot be " + xsNS);
      if (checkNS == null)
         throw new IllegalArgumentException("checkNS is null");
      if (knownNamespaces.contains(checkNS) == false && targetNS.equals(checkNS) == false)
         isCustom = true;
      return isCustom;
   }

   /**
    * Create a global XSElementDeclaration object
    * @param name
    * @param xstype
    * @param targetNS
    * @return
    */

   public JBossXSElementDeclaration createGlobalXSElementDeclaration(String name, XSTypeDefinition xstype, String targetNS)
   {
      JBossXSElementDeclaration xsel = new JBossXSElementDeclaration();
      xsel.setName(name);
      xsel.setTypeDefinition(xstype);
      xsel.setTargetNamespace(targetNS);
      xsel.setNamespace(targetNS);
      xsel.setScope(XSConstants.SCOPE_GLOBAL);
      return xsel;
   }

   /**
    * Generate a Schema Model for a namespace
    * @return
    */

   public JBossXSModel createXSModel()
   {
      return new JBossXSModel();
   }

   public JBossXSComplexTypeDefinition createXSComplexTypeDefinition(String name, XSTypeDefinition baseType, List<XSParticle> xsparts, String typens)
   {
      //No complex type if particles are null
      if (xsparts == null)
         return null;

      JBossXSComplexTypeDefinition ct = new JBossXSComplexTypeDefinition();
      ct.setName(name);
      ct.setNamespace(typens);
      JBossXSModelGroup group = new JBossXSModelGroup();

      group.setCompositor(XSModelGroup.COMPOSITOR_SEQUENCE);
      group.setParticles(xsparts);
      // Plug the particle array into the modelgroup
      JBossXSParticle xspa = new JBossXSParticle(null, typens);
      xspa.setTerm(group);
      ((JBossXSComplexTypeDefinition)ct).setParticle(xspa);

      if (baseType != null)
      {
         ((JBossXSComplexTypeDefinition)ct).setDerivationMethod(XSConstants.DERIVATION_EXTENSION);
         ((JBossXSComplexTypeDefinition)ct).setBaseType(baseType);
      }

      return ct;
   }

   /**
    * Create a local XSElementDeclaration object
    * @param name
    * @param xstype
    * @param targetNS
    * @param isNillable
    * @return
    */

   public JBossXSElementDeclaration createXSElementDeclaration(String name, XSTypeDefinition xstype, boolean isNillable)
   {
      JBossXSElementDeclaration xsel = new JBossXSElementDeclaration();
      xsel.setName(name);
      xsel.setTypeDefinition(xstype);
      xsel.setNillable(isNillable);
      return xsel;
   }

   public JBossXSParticle createXSParticle(String targetNS, boolean isArray, XSTerm xsterm)
   {
      JBossXSParticle xsp = new JBossXSParticle(null, targetNS);
      if (isArray)
         xsp.setMaxOccurs(-1);
      xsp.setTerm(xsterm);
      return xsp;
   }

   /**
    * Creates a XSTypeDefinition object given a QName
    *
    * @param qname
    * @return a XSTypeDefinition
    */
   public JBossXSTypeDefinition createXSTypeDefinition(QName qname)
   {
      JBossXSTypeDefinition jbxs = new JBossXSTypeDefinition();
      jbxs.setName(qname.getLocalPart());
      jbxs.setNamespace(qname.getNamespaceURI());
      return jbxs;
   }

   /**
    * Generate a complex type for a custom exception
    * @param exname
    * @param ns
    * @return
    */

   public XSComplexTypeDefinition getExceptionType(String exname, String ns)
   {
      JBossXSParticle xsp = new JBossXSParticle();
      /*xsp.setType(XSConstants.ELEMENT_DECLARATION);
       xsp.setMaxOccurs(-1);
       JBossXSElementDeclaration xsel = (JBossXSElementDeclaration)createXSElementDeclaration("name", utils.getSchemaBasicType("string"),  true);

       xsp.setTerm(xsel); */
      XSComplexTypeDefinition ct = new JBossXSComplexTypeDefinition(exname, ns);

      ((JBossXSComplexTypeDefinition)ct).setParticle(xsp);
      return ct;
   }

   /**
    * Convert the read-only Xerces implementation of XSModel
    * into JBossXSModel.
    * If the input is JBossXSModel, it will be returned back.
    * @param xsmodel XSModel object
    * @return JBossXSModel (RW Model)
    */
   public JBossXSModel getJBossXSModel(XSModel xsmodel)
   {
      if (xsmodel instanceof JBossXSModel)
         return (JBossXSModel)xsmodel;
      JBossXSModel jbxs = new JBossXSModel();
      copyXSModel(xsmodel, jbxs);
      return jbxs;
   }

   /**
    * Checks if the XSModel is empty given a namespace
    * @param xsmodel Schema Model to check
    * @param namespace namespace to check components for
    * @return  true (if empty) and false (if not empty)
    */
   public boolean isEmptySchema(JBossXSModel xsmodel, String namespace)
   {
      if (xsmodel == null)
         return true;
      if (namespace == null)
         throw new WSException("Target Namespace of xsmodel is null");
      XSNamedMap tmap = xsmodel.getComponentsByNamespace(XSConstants.TYPE_DEFINITION, namespace);
      XSNamedMap emap = xsmodel.getComponentsByNamespace(XSConstants.ELEMENT_DECLARATION, namespace);

      if (tmap != null && tmap.getLength() > 0)
         return false;
      if (emap != null && emap.getLength() > 0)
         return false;

      return true;
   }

   /**
    *  Serialize the SchemaModel into a  Writer
    * @param xsmodel  Schema Model which needs to be serialized
    * @param writer a Writer to which serialization should happen
    * @throws IOException
    */
   public void serialize(XSModel xsmodel, Writer writer) throws IOException
   {
      StringBuilder buffer = new StringBuilder();
      if (xsmodel instanceof JBossXSModel)
      {
         String str = ((JBossXSModel)xsmodel).serialize();
         buffer.append(str);
      }
      else
      {
         buffer.append("<schema ");
         XSNamespaceItemList itemlist = xsmodel.getNamespaceItems();
         appendSchemaDefinitions(buffer, itemlist);
         appendTypes(buffer, xsmodel);
         appendGlobalElements(buffer, xsmodel);
         buffer.append("</schema>");
      }
      writer.write(buffer.toString());
   }

   /**
    *  Serialize the SchemaModel (with no types and elements)  into a  Writer
    * @param xsmodel  Schema Model which needs to be serialized
    * @param writer a Writer to which serialization should happen
    * @throws IOException
    */

   public void serializeEmptySchema(XSModel xsmodel, Writer writer) throws IOException
   {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<schema ");
      XSNamespaceItemList itemlist = xsmodel.getNamespaceItems();
      appendSchemaDefinitions(buffer, itemlist);
      appendTypes(buffer, xsmodel);
      appendGlobalElements(buffer, xsmodel);
      buffer.append("</schema>");
      writer.write(buffer.toString());
   }

   /**
    * Return a string for the element declaration
    * @param xsel
    * @param xsp
    * @return
    */

   public String write(XSElementDeclaration xsel, XSParticle xsp)
   {
      XSTypeDefinition xst = xsel.getTypeDefinition();
      if (xst == null)
         throw new IllegalStateException("Type xst is null");

      boolean isGlobalRef = (xsel.getScope() == XSConstants.SCOPE_GLOBAL);
      boolean isAnonType = xst.getAnonymous();

      StringBuilder buf = new StringBuilder();
      String elname = xsel.getName();
      String prefix = null;

      if (isGlobalRef)
      {
         String namespace = xsel.getNamespace();
         prefix = getPrefix(namespace);
         buf.append("<element ref='" + prefix + ":" + elname + "'");
      }
      else
      {
         String namespace = xst.getNamespace();
         String typename = xst.getName();

         if (!Constants.NS_SCHEMA_XSD.equals(namespace))
         {
            prefix = getPrefix(namespace);
            typename = prefix + ":" + typename;
         }
         buf.append("<element name='" + elname + "'");

         if (!isAnonType)
            buf.append(" type='" + typename + "' ");
      }

      if (xsel.getNillable())
         buf.append(" nillable='true' ");

      int minoccurs = xsp.getMinOccurs();
      int maxoccurs = xsp.getMaxOccurs();
      if (!(minoccurs == 0 && maxoccurs == 0) && !(minoccurs == 1 && maxoccurs == 1))
      {
         if (xsp.getMaxOccursUnbounded())
            buf.append(" maxOccurs='unbounded' ");
         else buf.append(" maxOccurs='" + xsp.getMaxOccurs() + "'");

         buf.append(" minOccurs='" + xsp.getMinOccurs() + "'");
      }

      if (isAnonType == false || isGlobalRef == true)
         buf.append("/>");
      else buf.append(">").append(write(xst)).append("</element>");

      return buf.toString();
   }

   public String write(XSAttributeDeclaration decl)
   {
      XSTypeDefinition xst = decl.getTypeDefinition();
      if (xst == null)
         throw new IllegalStateException("Type xst is null");

      boolean isGlobalRef = (decl.getScope() == XSConstants.SCOPE_GLOBAL);
      boolean isAnonType = xst.getAnonymous();

      StringBuilder buf = new StringBuilder();
      String name = decl.getName();
      String prefix = null;

      if (isGlobalRef)
      {
         String namespace = decl.getNamespace();
         prefix = getPrefix(namespace);
         buf.append("<attribute ref='" + prefix + ":" + name + "'");
      }
      else
      {
         String namespace = xst.getNamespace();
         String typename = xst.getName();

         if (!Constants.NS_SCHEMA_XSD.equals(namespace))
         {
            prefix = getPrefix(namespace);
            typename = prefix + ":" + typename;
         }
         buf.append("<attribute name='" + name + "'");

         if (!isAnonType)
            buf.append(" type='" + typename + "' ");
      }

      if (!isAnonType)
         buf.append("/>");
      else buf.append(">").append(write(xst)).append("</attribute>");

      return buf.toString();
   }

   /**
    * Return a string for the global element declaration
    * @param xsel
    * @param xsp
    * @return
    */
   public String write(XSElementDeclaration xsel)
   {
      boolean isAnonType = false;
      if (XSConstants.SCOPE_GLOBAL != xsel.getScope())
         throw new IllegalArgumentException("Element is not a global element");

      StringBuilder buf = new StringBuilder();
      String elname = xsel.getName();
      XSTypeDefinition xst = xsel.getTypeDefinition();
      isAnonType = xst.getAnonymous();
      String typename = xst.getName();
      String namespace = xst.getNamespace();
      String prefix = null;

      if (!Constants.NS_SCHEMA_XSD.equals(namespace))
      {
         prefix = getPrefix(namespace);
         typename = prefix + ":" + typename;
      }

      buf.append("<element name='" + elname + "'");
      if (!isAnonType)
         buf.append(" type='" + typename + "' ");
      else buf.append(">").append(write(xst));

      if (xsel.getNillable() && xsel.getScope() != XSConstants.SCOPE_GLOBAL)
         buf.append(" nillable='true' ");
      if (!isAnonType)
         buf.append("/>");
      else buf.append("</element>");

      return buf.toString();
   }

   /**
    * Return a string for the xs model group
    * @param xstype
    * @return
    */
   public String write(XSModelGroup xsm)
   {
      StringBuilder buf = new StringBuilder();
      XSObjectList objlist = xsm.getParticles();

      int lenobj = objlist != null ? objlist.getLength() : 0;

      for (int i = 0; i < lenobj; i++)
      {
         XSParticle jxsp = (XSParticle)objlist.item(i);
         XSTerm xterm = jxsp.getTerm();
         short termType = xterm.getType();
         if (termType == XSConstants.ELEMENT_DECLARATION)
         {
            XSElementDeclaration xsel = (XSElementDeclaration)jxsp.getTerm();
            buf.append(this.write(xsel, jxsp));
         }
         else if (termType == XSConstants.MODEL_GROUP)
         {
            XSObjectList olist = ((XSModelGroup)xterm).getParticles();
            int lobj = olist != null ? olist.getLength() : 0;
            for (int k = 0; k < lobj; k++)
            {
               XSParticle jxp = (XSParticle)olist.item(k);
               XSTerm xsterm = jxp.getTerm();
               termType = xsterm.getType();
               if (termType == XSConstants.ELEMENT_DECLARATION)
                  buf.append(write((XSElementDeclaration)xsterm, jxsp));
               else if (termType == XSConstants.MODEL_GROUP && k > 0)
                  buf.append(write((XSModelGroup)xsterm));
            }
         }
      }
      return buf.toString();
   }

   /**
    * Return a string for the xs type
    * @param xstype
    * @return
    */
   public String write(XSTypeDefinition xstype)
   {
      StringBuilder buf = new StringBuilder();

      //Handle Complex Type
      if (xstype instanceof XSComplexTypeDefinition)
      {
         XSComplexTypeDefinition jxstype = (XSComplexTypeDefinition)xstype;
         String jxsTypeName = jxstype.getName();
         boolean isSimple = jxstype.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_SIMPLE;

         if (xstype.getAnonymous())
            buf.append("<complexType>");
         else buf.append("<complexType name='" + jxsTypeName + "'>");

         XSTypeDefinition xsbase = (XSTypeDefinition)jxstype.getBaseType();
         String baseType = null;
         if (xsbase != null && !("anyType".equals(xsbase.getName())))
            baseType = getPrefix(xsbase.getNamespace()) + ":" + xsbase.getName();

         if (baseType != null)
         {
            buf.append((isSimple) ? "<simpleContent>" : "<complexContent>");
            buf.append("<extension base='" + baseType + "'>");
         }

         XSParticle xsp = jxstype.getParticle();
         if (xsp != null)
            appendComplexTypeDefinition(buf, jxstype);

         XSObjectList list = jxstype.getAttributeUses();
         for (int i = 0; i < list.getLength(); i++)
         {
            XSAttributeUse use = (XSAttributeUse)list.item(i);
            XSAttributeDeclaration decl = use.getAttrDeclaration();
            buf.append(write(decl));
         }

         if (baseType != null)
         {
            buf.append("</extension>");
            buf.append((isSimple) ? "</simpleContent>" : "</complexContent>");
         }

         buf.append("</complexType>");

      }
      else if (xstype instanceof XSSimpleTypeDefinition)
      {
         XSTypeDefinition xsbase = (XSTypeDefinition)xstype.getBaseType();
         buf.append("<simpleType name='" + xstype.getName() + "'>");
         if (xsbase != null && !"anyType".equals(xsbase.getName()))
         {
            String baseType = xsbase.getName();
            String ns = xsbase.getNamespace();
            if (!Constants.NS_SCHEMA_XSD.equals(ns))
            {
               String prefix = getPrefix(ns);
               baseType = prefix + ":" + baseType;
            }

            // currently only handle enumerations
            buf.append("<restriction base='" + baseType + "'>");
            StringList list = ((XSSimpleTypeDefinition)xstype).getLexicalEnumeration();
            for (int i = 0; i < list.getLength(); i++)
            {
               String listItem = DOMWriter.normalize(list.item(i), false);
               buf.append("<enumeration value='" + listItem + "'/>");
            }
            buf.append("</restriction>");
         }
         buf.append("</simpleType>");
      }

      return buf.toString();
   }

   // Private methods

   private void appendSchemaDefinitions(StringBuilder buffer, XSNamespaceItemList itemlist)
   {
      int len = itemlist != null ? itemlist.getLength() : 0;

      for (int i = 0; i < len; i++)
      {
         XSNamespaceItem nsitem = (XSNamespaceItem)itemlist.item(i);
         String ns = nsitem.getSchemaNamespace();
         //Ignore the one for xsd

         if (Constants.NS_SCHEMA_XSD.equals(ns))
            continue;
         buffer.append(utils.getSchemaDefinitions(ns));

      } //end for
   }

   private void appendComplexTypeDefinition(StringBuilder buf, XSComplexTypeDefinition jxstype)
   {
      XSParticle xsp = jxstype.getParticle();
      XSTerm xsterm = xsp.getTerm();
      short deriveMethod = jxstype.getDerivationMethod();

      if (xsterm instanceof XSElementDeclaration)
      {
         // FIXME This is horribly wrong, but too much depends on this broken behavior
         buf.append("<sequence>");

         XSElementDeclaration xsel = (XSElementDeclaration)xsterm;
         buf.append(this.write(xsel, xsp));
         buf.append("</sequence>");
      }
      else if (xsterm instanceof XSModelGroup)
      {
         XSModelGroup jmg = (XSModelGroup)xsterm;
         XSObjectList objlist = jmg.getParticles();
         String end = null;

         switch (jmg.getCompositor())
         {
            case XSModelGroup.COMPOSITOR_ALL:
               buf.append("<all>");
               end = "</all>";
               break;
            case XSModelGroup.COMPOSITOR_CHOICE:
               buf.append("<choice>");
               end = "</choice>";
               break;
            default:
            case XSModelGroup.COMPOSITOR_SEQUENCE:
               buf.append("<sequence>");
               end = "</sequence>";
               break;
         }

         int lenobj = objlist != null ? objlist.getLength() : 0;

         for (int i = 0; i < lenobj; i++)
         {
            XSParticle jxsp = (XSParticle)objlist.item(i);
            XSTerm xterm = jxsp.getTerm();
            if (xterm instanceof XSElementDeclaration)
            {
               XSElementDeclaration xsel = (XSElementDeclaration)jxsp.getTerm();
               buf.append(this.write(xsel, jxsp));
            }
            else if (xterm instanceof XSModelGroup)
            {
               if (deriveMethod == XSConstants.DERIVATION_EXTENSION && i != lenobj - 1)
                  continue;

               if (i == 0)
                  continue;//Ignore as it provides the baseclass sequence of elements
               XSObjectList olist = ((XSModelGroup)xterm).getParticles();
               int lobj = olist != null ? olist.getLength() : 0;
               for (int k = 0; k < lobj; k++)
               {
                  XSParticle jxp = (XSParticle)olist.item(k);
                  XSTerm jxpterm = jxp.getTerm();
                  short termType = jxpterm.getType();
                  if (termType == XSConstants.ELEMENT_DECLARATION)
                     buf.append(write((XSElementDeclaration)jxpterm, jxsp));
                  else if (termType == XSConstants.MODEL_GROUP)
                     buf.append(write((XSModelGroup)jxpterm));
               }
            }
         }

         buf.append(end);
      }
   }

   private void appendTypes(StringBuilder buffer, XSModel xsmodel)
   {
      XSNamedMap xsmap = xsmodel.getComponents(XSConstants.TYPE_DEFINITION);
      int len = xsmap != null ? xsmap.getLength() : 0;
      for (int i = 0; i < len; i++)
      {
         XSTypeDefinition xstype = (XSTypeDefinition)xsmap.item(i);
         if (Constants.NS_SCHEMA_XSD.equals(xstype.getNamespace()))
            continue;
         buffer.append(this.write(xstype));
      }
   }

   private void appendGlobalElements(StringBuilder buffer, XSModel xsmodel)
   {
      XSNamedMap xsmap = xsmodel.getComponents(XSConstants.ELEMENT_DECLARATION);
      int len = xsmap != null ? xsmap.getLength() : 0;
      for (int i = 0; i < len; i++)
      {
         XSElementDeclaration xsel = (XSElementDeclaration)xsmap.item(i);
         if (Constants.NS_SCHEMA_XSD.equals(xsel.getNamespace()))
            continue;
         buffer.append(this.write(xsel));
      }
   }

   //Copy the Xerces implementation of XSModel into JBossXSModel
   public void copyXSModel(XSModel xsmodel, JBossXSModel jb)
   {
      if (xsmodel == null)
         throw new IllegalArgumentException("Illegal Null Argument:xsmodel");
      if (jb == null)
         throw new IllegalArgumentException("Illegal Null Argument:jb");
      //Copy all the Namespace Items
      jb.setXSNamespaceItemList(xsmodel.getNamespaceItems());
      //Copy all the elements
      XSNamedMap xsmp = xsmodel.getComponents(XSConstants.ELEMENT_DECLARATION);
      int len = xsmp != null ? xsmp.getLength() : 0;

      for (int i = 0; i < len; i++)
      {
         XSElementDeclaration xsel = (XSElementDeclaration)xsmp.item(i);
         jb.addXSElementDeclaration(xsel);
      }
      //Copy all the types
      xsmp = xsmodel.getComponents(XSConstants.TYPE_DEFINITION);
      len = xsmp != null ? xsmp.getLength() : 0;
      for (int i = 0; i < len; i++)
      {
         XSTypeDefinition xstype = (XSTypeDefinition)xsmp.item(i);
         if (!this.xsNS.equals(xstype.getNamespace()))
            jb.addXSTypeDefinition(xstype);
      }

      //Copy all the attributes
      xsmp = xsmodel.getComponents(XSConstants.ATTRIBUTE_DECLARATION);
      len = xsmp != null ? xsmp.getLength() : 0;
      for (int i = 0; i < len; i++)
      {
         XSAttributeDeclaration xsattr = (XSAttributeDeclaration)xsmp.item(i);
         jb.addXSAttributeDeclaration(xsattr);
      }

      //copy all the global annotations
      //xsmp = xsmodel.getComponents(XSConstants.ANNOTATION);
      XSObjectList xo = xsmodel.getAnnotations();
      len = xo != null ? xo.getLength() : 0;
      //len = xsmp != null ? xsmp.getLength() : 0;
      for (int i = 0; i < len; i++)
      {
         //XSAnnotation xa = (XSAnnotation)xsmp.item(i);
         XSAnnotation xa = (XSAnnotation)xo.item(i);
         jb.addXSAnnotation(xa);
      }
   }

   private String getPrefix(String namespace)
   {
      if (namespaceRegistry == null)
         throw new IllegalArgumentException("nameespaceRegistry can not be null!");

      if (namespace == null)
         throw new IllegalArgumentException("namespace can not be null");

      // XML Namespace can only legally be assigned the XML prefix
      if (namespace.equals(Constants.NS_XML))
         return Constants.PREFIX_XML;
      if (namespace.equals(targetNamespace))
         return Constants.PREFIX_TNS;
      if (namespace.equals(Constants.URI_SOAP11_ENC))
         return Constants.PREFIX_SOAP11_ENC;
      if (namespace.equals(Constants.NS_SCHEMA_XSI))
         return Constants.PREFIX_XSI;

      String prefix = namespaceRegistry.getPrefix(namespace);

      // Assume target namespace
      return (prefix == null) ? Constants.PREFIX_TNS : prefix;
   }
}
