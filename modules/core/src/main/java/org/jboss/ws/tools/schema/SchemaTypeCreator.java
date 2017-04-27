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
package org.jboss.ws.tools.schema;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.SessionBean;
import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.core.jaxrpc.ParameterWrapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaXmlTypeMapping;
import org.jboss.ws.metadata.jaxrpcmapping.PackageMapping;
import org.jboss.ws.metadata.jaxrpcmapping.VariableMapping;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSComplexTypeDefinition;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSElementDeclaration;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModelGroup;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSParticle;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSSimpleTypeDefinition;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSTypeDefinition;
import org.jboss.ws.metadata.wsdl.xmlschema.WSSchemaUtils;
import org.jboss.ws.metadata.wsdl.xsd.SchemaUtils;
import org.jboss.ws.tools.interfaces.SchemaCreatorIntf;
import org.jboss.xb.binding.NamespaceRegistry;
import org.jboss.xb.binding.SimpleTypeBindings;

/**
 *  Implementation for creation of schema types
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 31, 2005
 */
public class SchemaTypeCreator implements SchemaCreatorIntf
{
   protected Logger log = Logger.getLogger(SchemaTypeCreator.class);
   protected WSDLUtils utils = WSDLUtils.getInstance();

   protected SchemaUtils schemautils = SchemaUtils.getInstance();
   protected WSSchemaUtils sutils = null;

   protected LiteralTypeMapping typeMapping = new LiteralTypeMapping();

   // Zero or more namespace definitions
   private NamespaceRegistry namespaces = null;

   private JavaWsdlMapping javaWsdlMapping = new JavaWsdlMapping();

   protected String xsNS = Constants.NS_SCHEMA_XSD;

   private int maxPrefix = 1;

   protected JBossXSModel xsModel = null;

   //A Map of custom package->namespace mapping provided by the user
   protected Map<String, String> packageNamespaceMap = new HashMap<String, String>();

   public SchemaTypeCreator()
   {
      xsModel = new JBossXSModel();
      namespaces = xsModel.getNamespaceRegistry();
      sutils = WSSchemaUtils.getInstance(namespaces, null);
   }

   /* (non-Javadoc)
    * @see org.jboss.ws.tools.schema.SchemaCreatorIntf#addPackageNamespaceMapping(
    java.lang.String,java.lang.String)
    */
   public void addPackageNamespaceMapping(String pkgname, String ns)
   {
      if (pkgname == null)
         throw new IllegalArgumentException("Illegal Null Argument:pkgname");
      if (ns == null)
         throw new IllegalArgumentException("Illegal Null Argument:ns");
      packageNamespaceMap.put(pkgname, ns);

   }

   public JBossXSTypeDefinition generateType(QName xmlType, Class javaType)
   {
      return generateType(xmlType, javaType, null);
   }

   public JBossXSTypeDefinition generateType(QName xmlType, Class javaType, Map<String, QName> elementNames)
   {
      return getType(xmlType, javaType, elementNames);
   }

   public QName getXMLSchemaType(Class javaType)
   {
      QName xmlt = schemautils.getToolsOverrideInTypeMapping(javaType);
      //Check if it is already registered
      if (xmlt == null)
         xmlt = typeMapping.getXMLType(javaType, false);
      return xmlt;
   }

   public JavaWsdlMapping getJavaWsdlMapping()
   {
      return javaWsdlMapping;
   }

   /* (non-Javadoc)
    * @see org.jboss.ws.tools.schema.SchemaCreatorIntf#getXSModel()
    */
   public JBossXSModel getXSModel()
   {
      return xsModel;
   }

   /* (non-Javadoc)
    * @see org.jboss.ws.tools.schema.SchemaCreatorIntf#getCustomNamespaceMap()
    */
   public HashMap getCustomNamespaceMap()
   {
      HashMap<String, String> map = null;
      if (namespaces != null)
      {
         Iterator iter = namespaces.getRegisteredPrefixes();
         while (iter != null && iter.hasNext())
         {
            String prefix = (String)iter.next();
            if (prefix.startsWith("ns"))
            {
               if (map == null)
                  map = new HashMap<String, String>();
               map.put(prefix, namespaces.getNamespaceURI(prefix));
            }
         }
      }
      return map;
   }

   /* (non-Javadoc)
    * @see org.jboss.ws.tools.schema.SchemaCreatorIntf#getPackageNamespaceMap()
    */
   public Map<String, String> getPackageNamespaceMap()
   {
      return this.packageNamespaceMap;
   }

   /* (non-Javadoc)
    * @see org.jboss.ws.tools.schema.SchemaCreatorIntf#getTypeMapping()
    */
   public LiteralTypeMapping getTypeMapping()
   {
      return typeMapping;
   }

   /* (non-Javadoc)
    * @see org.jboss.ws.tools.schema.SchemaCreatorIntf#getJavaType(javax.xml.namespace.QName)
    */
   public Class getJavaType(QName xmlType)
   {
      Class retType = typeMapping.getJavaType(xmlType);
      if (retType == null)
         throw new IllegalArgumentException("Unsupported type: " + xmlType);
      return retType;
   }

   /* (non-Javadoc)
    * @see org.jboss.ws.tools.schema.SchemaCreatorIntf#setXSModel(org.jboss.ws.wsdl.xmlschema.JBossXSModel)
    */
   public void setXSModel(JBossXSModel xsm)
   {
      xsModel = xsm;
   }

   /**
    * If there is a type being used from a different namespace, a custom
    * prefix will be needed
    * @param nsuri
    * @return New Prefix
    */
   public String allocatePrefix(String nsURI)
   {
      String prefix = namespaces.getPrefix(nsURI);
      return (prefix == null) ? namespaces.registerURI(nsURI, null) : prefix;
   }

   /* (non-Javadoc)
    * @see org.jboss.ws.tools.schema.SchemaCreatorIntf#setPackageNamespaceMap(java.util.Map)
    */
   public void setPackageNamespaceMap(Map<String, String> packageNamespaceMap)
   {
      this.packageNamespaceMap = packageNamespaceMap;
   }

   //PRIVATE METHODS
   private JBossXSTypeDefinition checkTypeExistsInXSModel(QName xmlt)
   {
      JBossXSTypeDefinition ct = null;
      if (xmlt != null && xsModel != null)
         ct = getXSTypeDefinitionIfPresent(xmlt);
      return ct;
   }

   private JBossXSTypeDefinition getType(QName xmlType, Class javaType, Map<String, QName> elementNames)
   {
      JBossXSTypeDefinition ct = null;
      boolean registered = false;

      // Step 1: Check if the javaType is registered in the typeMapping
      if (xmlType == null)
      {
         xmlType = getXMLSchemaType(javaType);
         registered = xmlType != null;
      }
      else
      {
         registered = typeMapping.isRegistered(javaType, xmlType);
      }

      // Step 2: if the javatype is registered, search the xsmodel to obtain the type
      if (registered)
         ct = checkTypeExistsInXSModel(xmlType);

      // If the type is still unavailable, see xmlt represents schema basic type
      if (ct == null && xmlType != null && Constants.NS_SCHEMA_XSD.equals(xmlType.getNamespaceURI()) && !javaType.isArray())
         ct = schemautils.getSchemaBasicType(xmlType.getLocalPart());

      if (ct == null && Exception.class.isAssignableFrom(javaType))
         ct = this.getComplexTypeForJavaException(xmlType, javaType);

      // Step 3: If the type does not exist in the xsmodel, create a new type
      if (ct == null)
         ct = generateNewType(xmlType, javaType, elementNames);

      return ct;
   }

   private JBossXSTypeDefinition getXSTypeDefinitionIfPresent(QName qname)
   {
      return (JBossXSTypeDefinition)xsModel.getTypeDefinition(qname.getLocalPart(), qname.getNamespaceURI());
   }

   private JBossXSTypeDefinition generateNewType(QName xmlType, Class javaType, Map<String, QName> elementNames)
   {
      //Step 1: Take care of superclass (if any)::Generate Type for the base class, if any
      Class superclass = javaType.getSuperclass();
      JBossXSTypeDefinition baseType = null;
      List<XSParticle> particles = new ArrayList<XSParticle>();
      if (superclass != null && !utils.checkIgnoreClass(superclass))
      {
         baseType = generateType(null, superclass);
         if (baseType != null)
         {
            addBaseTypeParts(baseType, particles);
         }
      }
      //Check if the javaType is an array
      if (javaType.isArray())
         return handleArray(xmlType, javaType);

      String name;
      String namespace;
      if (xmlType != null)
      {
         name = xmlType.getLocalPart();
         namespace = getNamespace(javaType, xmlType.getNamespaceURI());
      }
      else
      {
         name = WSDLUtils.getJustClassName(javaType);
         namespace = getNamespace(javaType, null);
      }

      // Check if it is a JAX-RPC enumeration
      Class valueType = getEnumerationValueType(javaType);
      if (valueType != null)
         return handleJAXRPCEnumeration(name, namespace, javaType, valueType);

      // Generate and register the complex type before building it's particles
      // This solves circular reference problems
      JBossXSComplexTypeDefinition complexType = sutils.createXSComplexTypeDefinition(name, baseType, particles, namespace);
      QName registerQName = new QName(namespace, name);
      typeMapping.register(javaType, registerQName, null, null);
      xsModel.addXSComplexTypeDefinition(complexType);

      //Step 2: Generate XSParticles for all the public fields
      particles.addAll(getXSParticlesForPublicFields(namespace, javaType, elementNames));

      //Step 3: Generate XSParticles for the properties
      try
      {
         particles.addAll(introspectJavaProperties(namespace, javaType, elementNames));
      }
      catch (IntrospectionException e)
      {
         log.error("Problem in introspection of the Java Type during type generation", e);
         throw new WSException(e);
      }

      if (elementNames instanceof LinkedHashMap)
         particles = sortNamedOuterParticles(particles, (LinkedHashMap<String, QName>)elementNames);
      else Collections.sort((List)particles);

      registerJavaTypeMapping(registerQName, javaType, "complexType", particles, elementNames);

      JBossXSModelGroup group = (JBossXSModelGroup)complexType.getParticle().getTerm();
      group.setParticles(particles);

      if (log.isDebugEnabled())
         log.debug("generateNewType: " + sutils.write(complexType));
      return complexType;
   }

   private JBossXSTypeDefinition handleJAXRPCEnumeration(String name, String namespace, Class<?> javaType, Class<?> valueType)
   {
      JBossXSTypeDefinition enumType = generateType(null, valueType);
      JBossXSSimpleTypeDefinition simpleType = new JBossXSSimpleTypeDefinition();
      simpleType.setBaseType(enumType);
      simpleType.setName(name);
      simpleType.setNamespace(namespace);
      try
      {
         Method getValue = javaType.getMethod("getValue");
         for (Field field : javaType.getFields())
         {
            if (Modifier.isStatic(field.getModifiers()) && field.getType().equals(javaType))
            {
               Object ret = getValue.invoke(field.get(null));
               String item = SimpleTypeBindings.marshal(enumType.getName(), ret, new NamespaceRegistry());
               simpleType.addLexicalEnumeration(item);
            }
         }
      }
      catch (Exception e)
      {
         throw new WSException("JAX-RPC Enumeration type did not conform to expectations");
      }

      xsModel.addXSTypeDefinition(simpleType);
      registerJavaTypeMapping(new QName(namespace, name), javaType, "simpleType", new ArrayList<XSParticle>(), null);

      return simpleType;
   }

   private Class<?> getEnumerationValueType(Class javaType)
   {
      try
      {
         javaType.getConstructor();
         return null;
      }
      catch (NoSuchMethodException e)
      {
      }

      for (Method method : javaType.getMethods())
      {
         if (Modifier.isStatic(method.getModifiers()) && method.getName().equals("fromValue") && method.getParameterTypes().length == 1)
            return method.getParameterTypes()[0];
      }

      return null;
   }

   private List<XSParticle> sortNamedOuterParticles(List<XSParticle> particles, LinkedHashMap<String, QName> elementNames)
   {
      Map<String, XSParticle> index = new LinkedHashMap<String, XSParticle>();
      List<XSParticle> newList = new ArrayList<XSParticle>();
      for (XSParticle particle : particles)
      {
         XSTerm term = particle.getTerm();
         if (term instanceof XSElementDeclaration)
            index.put(((XSElementDeclaration)term).getName(), particle);
         else newList.add(particle);
      }

      // Follow the order of elementNames
      for (QName name : elementNames.values())
      {
         XSParticle found = index.get(name.getLocalPart());
         if (found != null)
         {
            index.remove(name.getLocalPart());
            newList.add(found);
         }
      }

      // Add everything else
      for (XSParticle particle : index.values())
      {
         newList.add(particle);
      }

      return newList;
   }

   private void registerJavaTypeMapping(QName registerQName, Class javaType, String scope, List<XSParticle> particles, Map<String, QName> elementNames)
   {
      QName qname = new QName(registerQName.getNamespaceURI(), registerQName.getLocalPart(), "typeNS");

      // Add package mapping if needed
      String packageName = javaWsdlMapping.getPackageNameForNamespaceURI(qname.getNamespaceURI());
      if (packageName == null && javaType.getPackage() != null)
      {
         PackageMapping packageMapping = new PackageMapping(javaWsdlMapping);
         packageMapping.setNamespaceURI(qname.getNamespaceURI());
         packageMapping.setPackageType(javaType.getPackage().getName());
         javaWsdlMapping.addPackageMapping(packageMapping);
      }

      JavaXmlTypeMapping javaXmlTypeMapping = new JavaXmlTypeMapping(javaWsdlMapping);
      javaXmlTypeMapping.setJavaType(javaType.getName());
      javaXmlTypeMapping.setQNameScope(scope);
      javaXmlTypeMapping.setRootTypeQName(qname);

      // JSR-109 specifies an element name as a string, not a qname, so we need to strip the namespace
      Map<String, String> reversedNames = null;
      if (elementNames != null)
      {
         reversedNames = new HashMap<String, String>();
         for (String variable : elementNames.keySet())
            reversedNames.put(elementNames.get(variable).getLocalPart(), variable);
      }

      addVariableMappings(javaType, javaXmlTypeMapping, particles, reversedNames);

      javaWsdlMapping.addJavaXmlTypeMappings(javaXmlTypeMapping);
   }

   private void addVariableMappings(Class javaType, JavaXmlTypeMapping javaXmlTypeMapping, List<XSParticle> particles, Map<String, String> reversedNames)
   {
      for (XSParticle particle : particles)
      {
         XSTerm term = particle.getTerm();
         if (term.getType() == XSConstants.MODEL_GROUP)
         {
            XSModelGroup group = (XSModelGroup)term;
            XSObjectList list = group.getParticles();
            ArrayList<XSParticle> baseParticles = new ArrayList<XSParticle>();
            for (int i = 0; i < list.getLength(); i++)
               baseParticles.add((XSParticle)list.item(i));

            addVariableMappings(javaType, javaXmlTypeMapping, baseParticles, null);

            continue;
         }

         String name = term.getName();
         String variableName = name;
         if (reversedNames != null && reversedNames.get(name) != null)
            variableName = reversedNames.get(name);

         VariableMapping mapping = new VariableMapping(javaXmlTypeMapping);

         mapping.setJavaVariableName(variableName);
         mapping.setXmlElementName(name);
         mapping.setDataMember(utils.doesPublicFieldExist(javaType, variableName));

         if (isAlreadyMapped(javaXmlTypeMapping, mapping) == false)
         {
            javaXmlTypeMapping.addVariableMapping(mapping);
         }
      }
   }

   private boolean isAlreadyMapped(JavaXmlTypeMapping jxtm, VariableMapping vm)
   {
      String javaVariableName = vm.getJavaVariableName();
      String attributeName = vm.getXmlAttributeName();
      String elementName = vm.getXmlElementName();

      for (VariableMapping current : jxtm.getVariableMappings())
      {
         boolean matched = checkStringEquality(javaVariableName, current.getJavaVariableName());

         if (matched)
         {
            matched = checkStringEquality(attributeName, current.getXmlAttributeName());
         }

         if (matched)
         {
            matched = checkStringEquality(elementName, current.getXmlElementName());
         }

         if (matched)
         {
            return true;
         }
      }

      return false;
   }

   private List<XSParticle> getXSParticlesForPublicFields(String typeNamespace, Class javaType, Map<String, QName> elementNames)
   {
      List<XSParticle> particles = new ArrayList<XSParticle>();

      for (Field field : utils.getPublicFields(javaType))
      {
         // Skip collections
         if (Collection.class.isAssignableFrom(field.getType()))
         {
            log.warn("JAX-RPC does not allow collection types skipping field: " + javaType.getName() + "." + field.getName());
            continue;
         }

         JBossXSParticle particle = createFieldParticle(typeNamespace, field.getName(), field.getType(), elementNames);
         particles.add(particle);
      }

      return particles;
   }

   private JBossXSTypeDefinition handleArray(QName xmlType, Class javaType)
   {
      Class componentType = javaType.getComponentType();
      boolean isComponentArray = componentType.isArray();

      // Do not allow overrides i.e. byte[][] should not be base64Binary[]
      JBossXSTypeDefinition xst = (isComponentArray) ? handleArray(null, componentType) : generateType(null, componentType);

      String name;
      String namespace;

      if (xmlType != null)
      {
         name = xmlType.getLocalPart();
         namespace = getNamespace(componentType, xmlType.getNamespaceURI()); //xmlType.getNamespaceURI();
      }
      else
      {
         if (isComponentArray == false)
         {
            name = utils.getJustClassName(componentType.getName()) + ".Array";
            namespace = getNamespace(componentType, null);
         }
         else
         {
            name = xst.getName() + ".Array";
            namespace = getNamespace(componentType, xst.getNamespace());
         }
      }

      JBossXSParticle xsp = new JBossXSParticle();
      xsp.setTerm(sutils.createXSElementDeclaration("value", xst, !componentType.isPrimitive()));
      xsp.setMaxOccurs(-1);
      List<XSParticle> particles = new ArrayList<XSParticle>();
      particles.add(xsp);

      JBossXSComplexTypeDefinition complex = sutils.createXSComplexTypeDefinition(name, null, particles, namespace);
      xsModel.addXSComplexTypeDefinition(complex);
      typeMapping.register(javaType, new QName(namespace, name), null, null);

      return complex;
   }

   private List<XSParticle> introspectJavaProperties(String typeNamespace, Class javaType, Map<String, QName> elementNames) throws IntrospectionException
   {
      List<XSParticle> xsparts = new ArrayList<XSParticle>();

      Class superClass = javaType.getSuperclass();
      BeanInfo beanInfo = Introspector.getBeanInfo(javaType, superClass);
      PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
      int len = props != null ? props.length : 0;

      for (int i = 0; i < len && SessionBean.class.isAssignableFrom(javaType) == false; i++)
      {
         PropertyDescriptor prop = props[i];
         String fieldname = prop.getName();
         Class fieldType = prop.getPropertyType();

         if (prop instanceof IndexedPropertyDescriptor && fieldType == null)
         {
            log.warn("Indexed Properties without non-indexed accessors are not supported skipping: " + javaType.getName() + "." + fieldname);
            continue;
         }

         // Skip magic work around property used in ParameterWrapping
         if (fieldType.equals(ParameterWrapping.WrapperType.class))
            continue;

         // Skip collections
         if (Collection.class.isAssignableFrom(fieldType))
         {
            log.warn("JAX-RPC does not allow collection types skipping: " + javaType.getName() + "." + fieldname);
            continue;
         }

         //Check if the property conflicts with a public member variable
         if (utils.doesPublicFieldExist(javaType, fieldname))
            throw new WSException("Class " + javaType.getName() + " has a public field & property :" + fieldname);

         JBossXSParticle particle = createFieldParticle(typeNamespace, fieldname, fieldType, elementNames);
         xsparts.add(particle);
      }

      return xsparts;
   }

   private JBossXSParticle createFieldParticle(String typeNamespace, String fieldName, Class fieldType, Map<String, QName> elementNames)
   {
      // There should be some override mechanism, but we want byte[] to resolve to base64binary (the default)
      boolean isArray = fieldType.isArray() && fieldType != byte[].class;
      if (isArray)
         fieldType = fieldType.getComponentType();

      //  Do not allow byte[][] to become base64binary[]
      XSTypeDefinition xst = (isArray && fieldType.isArray()) ? handleArray(null, fieldType) : generateType(null, fieldType);

      String elementNamespace = null;
      if (elementNames != null)
      {
         QName name = elementNames.get(fieldName);
         if (name != null)
         {
            fieldName = name.getLocalPart();
            elementNamespace = name.getNamespaceURI();
            if (typeNamespace.equals(elementNamespace))
               elementNamespace = null;
         }
      }

      JBossXSElementDeclaration xsel;
      if (elementNamespace == null || elementNamespace.length() == 0)
      {
         xsel = sutils.createXSElementDeclaration(fieldName, xst, !fieldType.isPrimitive());
      }
      else
      {
         // If an element has a different namespace than the owning complexType,
         // then it must be created as a global element in another schema, and then
         // referenced in the complex type.
         xsel = sutils.createGlobalXSElementDeclaration(fieldName, xst, elementNamespace);
         xsModel.addXSElementDeclaration(xsel);
      }

      JBossXSParticle particle = sutils.createXSParticle(typeNamespace, isArray, xsel);
      return particle;
   }

   private void addBaseTypeParts(XSTypeDefinition baseType, List xsparts)
   {
      if (baseType == null)
         throw new IllegalArgumentException("Illegal Null Argument:baseType");
      if (XSTypeDefinition.COMPLEX_TYPE == baseType.getTypeCategory())
      {
         XSTypeDefinition btype = baseType.getBaseType();
         if (btype != null)
            addBaseTypeParts(btype, xsparts); //Recurse
         //Just add the particles from this basetype as a ModelGroup sequence
         XSParticle part = ((XSComplexTypeDefinition)baseType).getParticle();
         XSTerm term = part.getTerm();
         if (term instanceof XSModelGroup)
         {
            JBossXSParticle p = new JBossXSParticle();
            p.setTerm(term);
            xsparts.add(p);
         }
      }
   }

   private String getNamespace(Class javaType, String defaultNS)
   {
      String retNS = defaultNS;
      if (javaType.isPrimitive() && retNS == null)
      {
         retNS = Constants.NS_JBOSSWS_URI + "/primitives";
      }
      else
      {
         while (javaType.isArray())
         {
            javaType = javaType.getComponentType();
         }

         Package javaPackage = javaType.getPackage();
         if (javaPackage != null)
         {
            String packageName = javaPackage.getName();
            String ns = packageNamespaceMap.get(packageName);
            if (ns != null)
            {
               retNS = ns;
            }
            else if (retNS == null)
            {
               retNS = utils.getTypeNamespace(packageName);
            }

            allocatePrefix(retNS);
         }
         else if (retNS == null)
         {
            throw new WSException("Cannot determine namespace, Class had no package");
         }
      }
      return retNS;
   }

   private JBossXSComplexTypeDefinition getComplexTypeForJavaException(QName xmlType, Class javaType)
   {
      if (!Exception.class.isAssignableFrom(javaType))
         throw new IllegalArgumentException("Type is not an excpetion");
      if (RuntimeException.class.isAssignableFrom(javaType))
         throw new IllegalArgumentException("JAX-RPC violation, the following exception extends RuntimeException: " + javaType.getName());

      String name;
      String namespace;
      if (xmlType != null)
      {
         namespace = getNamespace(javaType, xmlType.getNamespaceURI());
         name = xmlType.getLocalPart();
      }
      else
      {
         namespace = getNamespace(javaType, null);
         name = WSDLUtils.getJustClassName(javaType);
      }

      List<XSParticle> particles = new ArrayList<XSParticle>(0);
      List<Class> types = new ArrayList<Class>(0);
      JBossXSComplexTypeDefinition complexType = new JBossXSComplexTypeDefinition();
      complexType.setName(name);
      complexType.setNamespace(namespace);

      xsModel.addXSComplexTypeDefinition(complexType);
      xsModel.addXSElementDeclaration(sutils.createGlobalXSElementDeclaration(name, complexType, namespace));
      typeMapping.register(javaType, new QName(namespace, name), null, null);
      generateExceptionParticles(namespace, javaType, types, particles);

      registerJavaTypeMapping(new QName(namespace, name), javaType, "complexType", particles, null);

      Class superClass = javaType.getSuperclass();
      if (!Exception.class.equals(superClass) || Throwable.class.equals(superClass))
      {
         JBossXSTypeDefinition baseType = generateType(null, superClass);
         complexType.setBaseType(baseType);
      }


      boolean found = hasConstructor(javaType, types);
      boolean noarg = found && types.size() == 0;

      if (!found || noarg)
      {
         // Look for a message constructor if a matching constructor could not be found.
         // We also prefer message constructors over a noarg constructor
         ArrayList<Class> newTypes = new ArrayList<Class>(types);
         newTypes.add(0, String.class);
         found = hasConstructor(javaType, newTypes);
         if (found)
         {
            insertBaseParticle(particles, "message", String.class, namespace);
         }
         else
         {
            // If we have a default (0 argument) constructor, fall back to it
            if (!noarg)
               throw new IllegalArgumentException("Could not locate a constructor with the following types: " + javaType + ' ' + types);
         }
      }

      complexType.setParticle(createGroupParticle(namespace, particles));

      return complexType;
   }

   private void insertBaseParticle(List<XSParticle> particles, String name, Class type, String targetNS)
   {
      if (particles.size() == 0)
      {
         particles.add(getXSParticle(name, type, targetNS));
         return;
      }

      XSParticle particle = particles.get(0);
      XSTerm term = particle.getTerm();
      if (term.getType() == XSConstants.MODEL_GROUP)
      {
         JBossXSModelGroup group = (JBossXSModelGroup)term;
         XSObjectList list = group.getParticles();
         ArrayList<XSParticle> baseParticles = new ArrayList<XSParticle>();
         for (int i = 0; i < list.getLength(); i++)
            baseParticles.add((XSParticle)list.item(i));

         insertBaseParticle(baseParticles, name, type, targetNS);

         if (baseParticles.size() > list.getLength())
            group.setParticles(baseParticles);
      }
      else
      {
         particles.add(0, getXSParticle(name, type, targetNS));
      }
   }

   private boolean hasConstructor(Class javaType, List<Class> types)
   {
      boolean found = true;

      try
      {
         javaType.getConstructor(types.toArray(new Class[0]));
      }
      catch (NoSuchMethodException e)
      {
         found = false;
      }

      return found;
   }

   private void generateExceptionParticles(String typeNamespace, Class javaType, List<Class> types, List<XSParticle> particles)
   {
      /*
       * JAX-RPC 1.1 states that properties of an exception are determined by
       * the combination of a public getter, and a matching constructor. Since
       * Reflection can not tell us parameter names, the only way to locate the
       * propper constructor is by type. Since there can be multiple properties
       * with the same type, we must somehow establish a proper order of the
       * constructor. Code declaration is not a possibility since this
       * information is also not retained. This leaves us with only one option,
       * sorting.
       *
       * So the matching constructor must be sorted by two indicies in the
       * following order: 1. declaring class, super type first 2. property name
       * in ascending order
       */
      if (Exception.class.equals(javaType))
         return;

      Class superClass = javaType.getSuperclass();
      if (!Exception.class.equals(superClass))
      {
         List<XSParticle> superParticles = new ArrayList<XSParticle>(0);
         generateExceptionParticles(typeNamespace, superClass, types, superParticles);
         particles.add(createGroupParticle(typeNamespace, superParticles));
      }

      TreeMap<String, Class> sortedGetters = new TreeMap<String, Class>();
      for (Method method : javaType.getDeclaredMethods())
      {
         int modifiers = method.getModifiers();
         if ((!Modifier.isPublic(modifiers)) || Modifier.isStatic(modifiers))
            continue;

         String name = method.getName();
         Class returnType = method.getReturnType();
         if (name.startsWith("get") && returnType != void.class)
         {
            name = Introspector.decapitalize(name.substring(3));
            sortedGetters.put(name, returnType);
         }
      }

      for (String name : sortedGetters.keySet())
      {
         Class type = sortedGetters.get(name);
         types.add(type);
         JBossXSParticle particle = createFieldParticle(typeNamespace, name, type, null);
         particles.add(particle);
      }
   }

   private JBossXSParticle createGroupParticle(String targetNS, List<XSParticle> memberParticles)
   {
      JBossXSParticle groupParticle = new JBossXSParticle(null, targetNS);
      JBossXSModelGroup group = new JBossXSModelGroup();
      group.setParticles(memberParticles, false);
      groupParticle.setTerm(group);

      return groupParticle;
   }

   private JBossXSParticle getXSParticle(String name, Class fieldType, String targetNS)
   {
      XSTypeDefinition xstype = null;
      boolean isArray = fieldType.isArray();

      if (isArray)
         fieldType = fieldType.getComponentType();

      xstype = this.generateType(null, fieldType);
      boolean isNillable = !fieldType.isPrimitive();
      JBossXSElementDeclaration xsel = (JBossXSElementDeclaration)sutils.createXSElementDeclaration(name, xstype, isNillable);

      return sutils.createXSParticle(targetNS, isArray, xsel);
   }

   private boolean checkStringEquality(String str1, String str2)
   {
      if (str1 == null && str2 == null)
         return true;
      if (str1 == null && str2 != null)
         return false;
      if (str1 != null && str2 == null)
         return false;
      return str1.equals(str2);
   }
}
