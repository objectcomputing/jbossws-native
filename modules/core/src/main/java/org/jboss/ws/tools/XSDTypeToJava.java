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
package org.jboss.ws.tools;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xsd.SchemaUtils;
import org.jboss.wsf.common.JavaUtils;

/** Class that converts a XSD Type into Java class
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @author mageshbk@jboss.com
 *  @since  Apr 4, 2005
 */
public class XSDTypeToJava
{
   protected LiteralTypeMapping typeMapping = null;
   protected WSDLUtils utils = WSDLUtils.getInstance();
   protected SchemaUtils schemautils = SchemaUtils.getInstance();
   protected WSDLUtils wsdlUtils = WSDLUtils.getInstance();

   protected JavaWriter jwriter = new JavaWriter();

   //Additional variables
   protected String containingElement = "";
   protected String fname = "";
   protected String loc = null;
   protected String pkgname = "";

   private Set<String> generatedFiles = new HashSet<String>();

   protected Map<String, String> namespacePackageMap = null;
   protected boolean serializableTypes;

   /**
    * List that is used for exception inheritance case wherein the variables
    * obtained from the base class go into the generated constructor alone and
    * not as accessors
    */
   private Map<String, List> typeNameToBaseVARList = new HashMap<String, List>();

   public XSDTypeToJava(Map<String, String> map, boolean serializableTypes)
   {
      this.namespacePackageMap = map;
      this.serializableTypes = serializableTypes;
   }

   public void createJavaFile(XSComplexTypeDefinition type, String loc, String pkgname, XSModel schema) throws IOException
   {
      if (typeMapping == null)
         throw new WSException("TypeMapping has not been set");
      this.fname = type.getName();
      if (fname == null)
         throw new WSException("File Name is null");
      this.loc = loc;
      this.pkgname = pkgname;

      createJavaFile(type, schema, false);
   }

   public void createJavaFile(XSComplexTypeDefinition type, String containingElement, String loc, String pkgname, XSModel schema, boolean isExceptionType)
         throws IOException
   {
      if (typeMapping == null)
         throw new WSException("TypeMapping has not been set");
      this.fname = type.getName();
      this.containingElement = containingElement;
      if (fname == null)
         fname = containingElement;
      this.loc = loc;
      this.pkgname = pkgname;

      createJavaFile(type, schema, isExceptionType);
   }

   public void createJavaFile(XSSimpleTypeDefinition type, String loc, String pkgname, XSModel schema) throws IOException
   {
      if (typeMapping == null)
         throw new WSException("TypeMapping has not been set");
      this.fname = type.getName();
      this.loc = loc;
      this.pkgname = pkgname;

      createJavaFile(type, schema);
   }

   public void createJavaFile(XSComplexTypeDefinition type, XSModel schema, boolean isExceptionType) throws IOException
   {
      if (typeMapping == null)
         throw new WSException("TypeMapping has not been set");
      XSTypeDefinition baseType = type.getBaseType();
      // Ensure the characters in the name are valid
      fname = ToolsUtils.convertInvalidCharacters(fname);
      //Ensure that the first character is uppercase
      fname = utils.firstLetterUpperCase(fname);
      List vars = new ArrayList();

      String baseName = getBaseTypeName(type);
      if (baseName != null && baseType != null && !generatedFiles.contains(baseName))
      {
         if (baseType instanceof XSComplexTypeDefinition)
         {
            generatedFiles.add(baseName);
            String pushName = fname;
            fname = baseName;
            this.createJavaFile((XSComplexTypeDefinition)baseType, schema, isExceptionType);
            fname = pushName;
         }
      }

      vars = this.getVARList(type, schema, isExceptionType);

      if (baseName == null && isExceptionType)
      {
         baseName = "Exception";
      }
      else if (baseName != null)
      {
         baseName = getPackageName(baseType.getNamespace()) + "." + baseName;
      }
      String packageName = getPackageName(type.getNamespace());
      jwriter.createJavaFile(getLocationForJavaGeneration(packageName), fname, packageName, vars, null, baseName, isExceptionType, serializableTypes,
            typeNameToBaseVARList);
   }

   public void createJavaFile(XSSimpleTypeDefinition xsSimple, XSModel schema) throws IOException
   {
      if (typeMapping == null)
         throw new WSException("TypeMapping has not been set");
      XSTypeDefinition baseType = xsSimple.getBaseType();

      short variety = xsSimple.getVariety();
      if (XSSimpleTypeDefinition.VARIETY_ATOMIC == variety)
      {
         //If the simple type has an enumerated list, then just use its type
         StringList slist = xsSimple.getLexicalEnumeration();
         if (slist != null && slist.getLength() > 0)
         {
            //Enumerated List
            String packageName = getPackageName(xsSimple.getNamespace());
            jwriter.createJavaFileForEnumeratedValues(fname, slist, getLocationForJavaGeneration(packageName), packageName, xsSimple);
         }
         else
         {
            if (Constants.NS_SCHEMA_XSD.equals(xsSimple.getNamespace()))
               return;
            //TODO: Take care of other cases
            return;
         }
      }
   }

   public List<VAR> getVARList(XSComplexTypeDefinition type, XSModel schema, boolean isExceptionType) throws IOException
   {
      if (typeMapping == null)
         throw new WSException("TypeMapping has not been set");
      XSTypeDefinition baseType = type.getBaseType();
      List vars = new ArrayList();

      vars = handleAttributes(type, vars);
      //TODO:Handle xsd:group

      short contentType = type.getContentType();

      //simplecontenttype
      if (XSComplexTypeDefinition.CONTENTTYPE_SIMPLE == contentType)
      {
         short der = type.getDerivationMethod();

         if (XSConstants.DERIVATION_EXTENSION == der)
         {
            XSSimpleTypeDefinition xssimple = type.getSimpleType();
            QName q = new QName(xssimple.getNamespace(), xssimple.getName());
            QName qn = schemautils.patchXSDQName(q);
            Class javaType = typeMapping.getJavaType(qn);
            String jtype = null;
            if (javaType.isArray())
            {
               jtype = JavaUtils.getSourceName(javaType);
            }
            else
            {
               jtype = javaType.getName();
            }
            VAR v = new VAR("_value", jtype, false);
            vars.add(v);
         }
      }
      else if (XSComplexTypeDefinition.CONTENTTYPE_EMPTY == contentType)
      {
         short der = type.getDerivationMethod();

         if (XSConstants.DERIVATION_RESTRICTION == der)
         {
            vars.addAll(createVARsforXSParticle(type, schema));
         }
      }
      else if (XSComplexTypeDefinition.CONTENTTYPE_ELEMENT == contentType)
      { //Element Only content type
         if (!utils.isBaseTypeIgnorable(baseType, type))
         {
            short der = type.getDerivationMethod();

            if (XSConstants.DERIVATION_NONE == der)
            {
               handleContentTypeElementsWithDerivationNone(type, schema, isExceptionType, vars, type.getParticle());
            }
            if (XSConstants.DERIVATION_EXTENSION == der)
            {
               handleContentTypeElementsWithDerivationExtension(type, schema, isExceptionType, vars, type.getParticle());
            }
            else if (XSConstants.DERIVATION_RESTRICTION == der)
            {
               handleContentTypeElementsWithDerivationRestriction(type, schema, vars, type.getParticle());
            }
         }
         else
         {
            //Base Type is ignorable
            vars.addAll(createVARsforXSParticle(type, schema));
         }
      }

      return vars;

   }

   public void setPackageName(String packageName)
   {
      this.pkgname = packageName;
   }

   public void setTypeMapping(LiteralTypeMapping tm)
   {
      this.typeMapping = tm;
   }

   public Map<String, String> getNamespacePackageMap()
   {
      return namespacePackageMap;
   }

   public void setNamespacePackageMap(Map<String, String> map)
   {
      this.namespacePackageMap = map;
   }

   //PRIVATE METHODS

   private void handleContentTypeElementsWithDerivationRestriction(XSComplexTypeDefinition type, XSModel schema, List vars, XSParticle xsparticle) throws IOException
   {
      if (xsparticle != null)
      {
         XSTerm xsterm = xsparticle.getTerm();
         if (xsterm instanceof XSModelGroup)
         {
            XSModelGroup xsm = (XSModelGroup)xsterm;
            XSObjectList xparts = xsm.getParticles();
            //Ignore the first item - that will be a modelgroup for basetype
            XSParticle xspar = (XSParticle)xparts.item(1);
            XSTerm xterm = xspar.getTerm();
            if (xterm instanceof XSElementDeclaration)
            {
               vars.addAll(createVARforXSElementDeclaration(xterm, schemautils.isArrayType(xspar), schema, type));
            }
            else if (xterm instanceof XSModelGroup)
            {
               XSModelGroup xsmodelgrp = (XSModelGroup)xterm;
               vars.addAll(createVARsforXSModelGroup(xsmodelgrp, schema, type));
            }
         }
      }
   }

   private void handleContentTypeElementsWithDerivationExtension(XSComplexTypeDefinition type, XSModel schema, boolean isExceptionType, List vars, XSParticle xsparticle)
         throws IOException
   {
      if (xsparticle != null)
      {
         XSTerm xsterm = xsparticle.getTerm();
         if (xsterm instanceof XSModelGroup)
         {
            XSModelGroup xsm = (XSModelGroup)xsterm;
            XSObjectList xparts = xsm.getParticles();

            int length = xparts.getLength();

            XSTypeDefinition baseType = type.getBaseType();

            if (baseType instanceof XSComplexTypeDefinition && ((XSComplexTypeDefinition)baseType).getContentType() != XSComplexTypeDefinition.CONTENTTYPE_EMPTY)
            {
               XSTerm baseTerm = ((XSComplexTypeDefinition)baseType).getParticle().getTerm();
               if (isExceptionType && baseTerm instanceof XSModelGroup)
               {
                  typeNameToBaseVARList.put(type.getName(), createVARsforXSModelGroup((XSModelGroup)baseTerm, schema, type));
               }

               // HACK - The only way to know what elements are local to the subclass, and not inherited, is to compare to the base class
               // THIS TIES US TO XERCES
               if (baseType instanceof XSComplexTypeDefinition == false || ((XSComplexTypeDefinition)baseType).getParticle().getTerm() == xsterm)
                  return;
            }

            XSParticle xspar;

            if (baseType instanceof XSComplexTypeDefinition && ((XSComplexTypeDefinition)baseType).getContentType() == XSComplexTypeDefinition.CONTENTTYPE_EMPTY)
            {
               // If the base type is empty there will not have been a particle to ignore.
               xspar = xsparticle;
            }
            else
            {
               xspar = (XSParticle)xparts.item(length - 1);
            }

            XSTerm xsparTerm = xspar.getTerm();
            if (xsparTerm instanceof XSModelGroup)
            {
               XSModelGroup xsmodelgrp = (XSModelGroup)xspar.getTerm();
               vars.addAll(createVARsforXSModelGroup(xsmodelgrp, schema, type));
            }
            else if (xsparTerm instanceof XSElementDeclaration)
               vars.addAll(createVARforXSElementDeclaration(xsparTerm, schemautils.isArrayType(xspar), schema, type));

         }
      }
   }

   private void handleContentTypeElementsWithDerivationNone(XSComplexTypeDefinition type, XSModel schema, boolean isExceptionType, List vars, XSParticle xsparticle)
         throws IOException
   {
      if (xsparticle != null)
      {
         XSTerm xsterm = xsparticle.getTerm();
         if (xsterm instanceof XSModelGroup)
         {
            XSModelGroup xsm = (XSModelGroup)xsterm;
            XSObjectList xparts = xsm.getParticles();
            int len = xparts != null ? xparts.getLength() : 0;
            int diff = len - 0;

            for (int i = 0; i < len; i++)
            {
               if (isExceptionType && type.getBaseType() != null)
               {
                  List<VAR> baseList = new ArrayList<VAR>();

                  //The first few xsterms are modelgroups for base class
                  for (int j = 0; j < diff - 1; j++)
                  {
                     XSParticle xspar = (XSParticle)xparts.item(j);
                     XSTerm xsparTerm = xspar.getTerm();
                     if (xsparTerm instanceof XSModelGroup)
                     {
                        XSModelGroup xsmodelgrp = (XSModelGroup)xspar.getTerm();
                        baseList.addAll(createVARsforXSModelGroup(xsmodelgrp, schema, type));
                     }

                  }
                  if (baseList.size() > 0)
                     this.typeNameToBaseVARList.put(type.getName(), baseList);
                  //Now take the modelgroup for the type in question
                  XSParticle xspar = (XSParticle)xparts.item(len - 1);
                  XSTerm xsparTerm = xspar.getTerm();
                  if (xsparTerm instanceof XSModelGroup)
                  {
                     XSModelGroup xsmodelgrp = (XSModelGroup)xspar.getTerm();
                     vars.addAll(createVARsforXSModelGroup(xsmodelgrp, schema, type));
                  }
                  break;
               }
               XSParticle xspar = (XSParticle)xparts.item(i);
               XSTerm xsparTerm = xspar.getTerm();
               if (xsparTerm instanceof XSModelGroup)
               {
                  XSModelGroup xsmodelgrp = (XSModelGroup)xspar.getTerm();
                  vars.addAll(createVARsforXSModelGroup(xsmodelgrp, schema, type));
               }
               else if (xsparTerm instanceof XSElementDeclaration)
                  vars.addAll(createVARforXSElementDeclaration(xsparTerm, schemautils.isArrayType(xspar), schema, type));
            }
         }
         else if (xsterm instanceof XSElementDeclaration)
         {
            vars.addAll(createVARforXSElementDeclaration(xsterm, schemautils.isArrayType(xsparticle), schema, type));
         }
         else throw new WSException("Unhandled Type");
      }

   }

   private List handleAttributes(XSComplexTypeDefinition type, List vars)
   {
      XSObjectList xsobjlist = type.getAttributeUses();
      if (xsobjlist != null)
      {
         int len = xsobjlist.getLength();
         for (int i = 0; i < len; i++)
         {
            XSAttributeUse obj = (XSAttributeUse)xsobjlist.item(i);
            XSAttributeDeclaration att = obj.getAttrDeclaration();
            XSSimpleTypeDefinition xstype = att.getTypeDefinition();
            QName qn = SchemaUtils.handleSimpleType(xstype);
            boolean primitive = obj.getRequired();
            VAR v = createVAR(qn, att.getName(), getPackageName(xstype.getNamespace()), primitive);
            if (vars == null)
               vars = new ArrayList();
            vars.add(v);
         }
      }
      return vars;
   }

   private List createVARsforXSParticle(XSComplexTypeDefinition type, XSModel schema) throws IOException
   {
      List<VAR> list = new ArrayList<VAR>();
      XSParticle xsparticle = type.getParticle();

      if (xsparticle != null)
      {
         short xsptype = xsparticle.getType();
         XSTerm xsterm = xsparticle.getTerm();
         if (xsptype == XSConstants.ELEMENT_DECLARATION)
         {

         }
         else if (xsterm instanceof XSModelGroup)
         {
            XSModelGroup xsm = (XSModelGroup)xsterm;
            XSObjectList xparts = xsm.getParticles();
            list.addAll(createVARsForElements(xparts, schema, type));
         }
      }
      return list;
   }

   private List<VAR> createVARsforXSModelGroup(XSModelGroup xsm, XSModel schema, XSComplexTypeDefinition origType) throws IOException
   {
      List<VAR> vars = new ArrayList<VAR>();
      short compositor = xsm.getCompositor();

      if (XSModelGroup.COMPOSITOR_SEQUENCE == compositor)
      {
         XSObjectList xsobjlist = xsm.getParticles();
         int len = xsobjlist.getLength();

         for (int i = 0; i < len; i++)
         {
            XSParticle xsparticle = (XSParticle)xsobjlist.item(i);
            XSTerm term = xsparticle.getTerm();

            if (term instanceof XSElementDeclaration)
            {
               vars.addAll(createVARforXSElementDeclaration(term, schemautils.isArrayType(xsparticle), schema, origType));
            }
            else if (term instanceof XSModelGroup)
            {
               vars.addAll(createVARsforXSModelGroup((XSModelGroup)term, schema, origType));
            }
         }
      }

      return vars;
   }

   private VAR createVAR(QName qn, String varstr, String pkgname, boolean primitive)
   {
      String clname = "";
      Class cls = typeMapping.getJavaType(qn);
      VAR v = null;
      if (cls != null)
      {
         clname = cls.getName();
         if (primitive)
         {
            String primName = utils.getPrimitive(clname);
            if (primName != null)
               clname = primName;
         }

      }
      else
      {
         if (qn != null)
         {
            if (!Constants.NS_SCHEMA_XSD.equals(qn.getNamespaceURI()))
               clname = pkgname + ".";
            clname += qn.getLocalPart();
         }
      }
      v = new VAR(varstr, clname, false);
      return v;
   }

   private List createVARsForElements(XSObjectList xsobjlist, XSModel schema, XSComplexTypeDefinition origType) throws IOException
   {
      List<VAR> list = new ArrayList<VAR>();
      int len = xsobjlist.getLength();
      for (int i = 0; i < len; i++)
      {
         XSParticle xsparticle = (XSParticle)xsobjlist.item(i);
         XSTerm xsterm = xsparticle.getTerm();

         list.addAll(createVARforXSElementDeclaration(xsterm, schemautils.isArrayType(xsparticle), schema, origType));
         continue;
      }

      return list;
   }

   private List createVARforXSElementDeclaration(XSTerm xsterm, boolean arrayType, XSModel schema, XSComplexTypeDefinition origType) throws IOException
   {
      List<VAR> vars = new ArrayList<VAR>();
      // Handle xsd:any elements
      if (xsterm instanceof XSWildcard)
      {
         VAR v = new VAR("_any", "javax.xml.soap.SOAPElement", arrayType);
         vars.add(v);
         return vars;
      }

      // Handle xsd:group
      if (xsterm instanceof XSModelGroup)
      {
         vars.addAll(createVARsforXSModelGroup((XSModelGroup)xsterm, schema, origType));
         return vars;
      }

      XSElementDeclaration elem = (XSElementDeclaration)xsterm;

      //    TODO: Check if the element contains any anon complex type
      //    TODO: If yes, create class that is ComplexTypeName+ElementName
      //    TODO: ItemsItem  If the elem contains anon simpletype
      //    TODO: No need to create seperate Java class

      String tname = elem.getName();
      XSTypeDefinition xstypedef = elem.getTypeDefinition();

      String xstypename = xstypedef.getName();
      // Check if it is a composite type
      if (xstypename != null && xstypedef.getName().equals(origType.getName()) && xstypedef.getNamespace().equals(origType.getNamespace()))
      {
         // it is a composite type
         QName qn = new QName(origType.getNamespace(), origType.getName());
         VAR vr = createVAR(qn, elem, (XSComplexTypeDefinition)xstypedef, tname, getPackageName(origType.getNamespace()), arrayType);
         vars.add(vr);
         return vars;
      }
      else

      if (xstypename == null && xstypedef instanceof XSComplexTypeDefinition)
      {
         XSComplexTypeDefinition xsc = (XSComplexTypeDefinition)xstypedef;
         String subname = utils.firstLetterUpperCase(tname);
         // Save the fname in a temp var
         String tempfname = this.fname;
         // it will be an anonymous type
         if (containingElement == null || containingElement.length() == 0)
            containingElement = origType.getName();

         String anonName;
         if (elem.getScope() == XSConstants.SCOPE_GLOBAL)
         {
            anonName = subname;
         }
         else
         {
            anonName = containingElement + subname;
         }

         anonName = utils.firstLetterUpperCase(anonName);
         this.fname = anonName;

         if (!generatedFiles.contains(this.fname))
         {
            generatedFiles.add(this.fname);
            this.createJavaFile((XSComplexTypeDefinition)xstypedef, schema, false);
         }

         // Restore the fname
         this.fname = tempfname;
         // Bypass rest of processing
         QName anonqn = new QName(anonName);
         VAR vr = createVAR(anonqn, elem, xsc, tname, getPackageName(xsc.getNamespace()), arrayType);
         vars.add(vr);
         return vars;
      }
      else
      {
         String temp = this.fname;
         // Unwrap any array wrappers
         if (SchemaUtils.isWrapperArrayType(xstypedef))
         {
            XSComplexTypeDefinition complex = (XSComplexTypeDefinition)xstypedef;
            XSModelGroup group = (XSModelGroup)complex.getParticle().getTerm();
            XSElementDeclaration element = (XSElementDeclaration)((XSParticle)group.getParticles().item(0)).getTerm();
            xstypedef = element.getTypeDefinition();
            xstypename = xstypedef.getName();
            arrayType = true;
         }

         QName qn = null;
         if (xstypedef instanceof XSSimpleTypeDefinition)
         {
            qn = SchemaUtils.handleSimpleType((XSSimpleTypeDefinition)xstypedef);
         }
         else
         {
            qn = new QName(xstypedef.getNamespace(), xstypename);
         }

         if (xstypename != null && xstypedef instanceof XSComplexTypeDefinition)
         {
            this.fname = ToolsUtils.convertInvalidCharacters(utils.firstLetterUpperCase(xstypename));
            if (!generatedFiles.contains(this.fname))
            {
               generatedFiles.add(this.fname);
               this.createJavaFile((XSComplexTypeDefinition)xstypedef, schema, false);
            }
            this.fname = temp;
         }

         VAR v = createVAR(qn, elem, xstypedef, tname, getPackageName(xstypedef.getNamespace()), arrayType);
         vars.add(v);
      }
      return vars;
   }

   private VAR createVAR(QName qn, XSElementDeclaration elem, XSTypeDefinition t, String varstr, String pkgname, boolean arrayType)
   {
      if (t instanceof XSSimpleTypeDefinition)
      {
         QName tempqn = schemautils.handleSimpleType((XSSimpleTypeDefinition)t);
         if (tempqn != null)
            qn = tempqn;
      }
      String qualifiedClassName = "";
      Class cls = typeMapping.getJavaType(qn);
      VAR v = null;
      if (cls != null)
      {
         qualifiedClassName = JavaUtils.getSourceName(cls);
         String primitive = utils.getPrimitive(qualifiedClassName);
         if ((!elem.getNillable()) && primitive != null)
            qualifiedClassName = primitive;
      }
      else
      {
         QName typename = null;

         if (t.getName() == null)
            typename = qn;
         else typename = new QName(t.getName());
         if (typename != null)
         {
            String nsuri = typename.getNamespaceURI();
            if (!nsuri.equals(Constants.NS_SCHEMA_XSD))
               qualifiedClassName = pkgname + ".";
            String className = wsdlUtils.firstLetterUpperCase(ToolsUtils.convertInvalidCharacters(typename.getLocalPart()));
            qualifiedClassName += className;
         }
         else if (qn != null)
            qualifiedClassName = qn.getLocalPart();
      }

      v = new VAR(Introspector.decapitalize(varstr), qualifiedClassName, arrayType);
      return v;
   }

   private String getBaseTypeName(XSComplexTypeDefinition type)
   {
      String baseName = null;
      XSTypeDefinition baseType = null;
      if (type instanceof XSComplexTypeDefinition)
      {
         XSComplexTypeDefinition t = (XSComplexTypeDefinition)type;

         baseType = t.getBaseType();
         //Check baseType is xsd:anyType
         if (baseType != null)
         {
            if (baseType.getNamespace() == Constants.NS_SCHEMA_XSD && baseType.getName().equals("anyType"))
               baseType = null; //Ignore this baseType
         }
         if (XSComplexTypeDefinition.CONTENTTYPE_SIMPLE == t.getContentType())
         {
            baseType = null; //ComplexType has a simplecontent
         }
      }

      if (baseName == null && baseType != null)
         baseName = ToolsUtils.firstLetterUpperCase(baseType.getName());

      return baseName;
   }

   /**
    * Inner Class that just stores a custom object called VAR
    * that is used in creating of the Java class
    */
   public static class VAR
   {
      String varname;
      String vartype;
      boolean isArrayType = false;

      public VAR(String varname, String vartype, boolean arrayType)
      {
         this.varname = varname;
         this.vartype = vartype;
         isArrayType = arrayType;
      }

      public String getVarname()
      {
         return varname;
      }

      public void setVarname(String varname)
      {
         this.varname = varname;
      }

      public String getVartype()
      {
         return vartype;
      }

      public void setVartype(String vartype)
      {
         this.vartype = vartype;
      }

      public boolean isArrayType()
      {
         return isArrayType;
      }

      public void setArrayType(boolean arrayType)
      {
         isArrayType = arrayType;
      }
   }

   private File getLocationForJavaGeneration(String packageName)
   {
      File locdir = new File(this.loc);
      locdir = wsdlUtils.createPackage(locdir.getAbsolutePath(), packageName);
      return locdir;
   }

   private String getPackageName(String targetNamespace)
   {
      //Get it from global config
      if (namespacePackageMap != null)
      {
         String pkg = namespacePackageMap.get(targetNamespace);
         if (pkg != null)
         {
            return pkg;
         }
      }
      return this.pkgname;
   }
}
