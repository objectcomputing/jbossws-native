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
package org.jboss.ws.core.jaxrpc.binding.jbossxb;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

import org.apache.xerces.xs.XSModel;
import org.jboss.logging.Logger;
import org.jboss.util.xml.JBossEntityResolver;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.utils.JBossWSEntityResolver;
import org.jboss.ws.extensions.xop.jaxrpc.JBossXBContentAdapter;
import org.jboss.ws.metadata.jaxrpcmapping.ExceptionMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping;
import org.jboss.ws.metadata.jaxrpcmapping.JavaXmlTypeMapping;
import org.jboss.ws.metadata.jaxrpcmapping.PackageMapping;
import org.jboss.ws.metadata.jaxrpcmapping.VariableMapping;
import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.PackageMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SimpleTypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.WildcardBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.unmarshalling.impl.runtime.RtElementHandler;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;

/**
 * Create SchemaBinding from XSModel and jaxrpc-mapping.
 *
 * @author Thomas.Diesler@jboss.org
 * @author Alexey.Loubyansky@jboss.org
 * @since 18-Oct-2004
 * @see XSModel
 * @see JavaWsdlMapping
 */
public class SchemaBindingBuilder
{
   // provide logging
   private static final Logger log = Logger.getLogger(SchemaBindingBuilder.class);

   /**
    * Creates and initializes an instance of SchemaBinding
    */
   public SchemaBinding buildSchemaBinding(XSModel model, JavaWsdlMapping wsdlMapping)
   {
      JBossEntityResolver resolver = new JBossWSEntityResolver();
      SchemaBinding schemaBinding = XsdBinder.bind(model, new DefaultSchemaResolver(resolver));

      schemaBinding.setIgnoreLowLine(false);
      schemaBinding.setIgnoreUnresolvedFieldOrClass(false);
      schemaBinding.setUnmarshalListsToArrays(true); // note: default jaxb2.0 is false!
      schemaBinding.setSimpleContentProperty("_value");
      schemaBinding.setUseNoArgCtorIfFound(true);
      schemaBinding.setReplacePropertyRefs(false);
      if (wsdlMapping != null)
      {
         bindSchemaToJava(schemaBinding, wsdlMapping);
      }

      // setup MTOM handler
      JBossXBContentAdapter.register(schemaBinding);

      return schemaBinding;
   }

   /** Merges JavaWsdlMapping into SchemaBinding
    */
   private void bindSchemaToJava(SchemaBinding schemaBinding, JavaWsdlMapping wsdlMapping)
   {
      if (log.isTraceEnabled())
         log.trace("bindSchemaToJava: " + schemaBinding);

      for (PackageMapping packageMapping : wsdlMapping.getPackageMappings())
      {
         processPackageMapping(schemaBinding, packageMapping);
      }

      for (ExceptionMapping exceptionMapping : wsdlMapping.getExceptionMappings())
      {
         processExceptionMapping(schemaBinding, exceptionMapping);
      }
      
      for (JavaXmlTypeMapping typeMapping : wsdlMapping.getJavaXmlTypeMappings())
      {
         processJavaXmlTypeMapping(schemaBinding, typeMapping);
      }
   }

   private void processPackageMapping(SchemaBinding schemaBinding, PackageMapping packageMapping)
   {
      PackageMetaData packageMetaData = schemaBinding.getPackageMetaData();
      if (packageMetaData == null)
      {
         packageMetaData = new PackageMetaData();
         schemaBinding.setPackageMetaData(packageMetaData);
      }

      if (log.isTraceEnabled())
         log.trace("Bound namespace " + packageMapping.getNamespaceURI() + " to package " + packageMapping.getPackageType());

      packageMetaData.setName(packageMapping.getPackageType());
   }

   private void processJavaXmlTypeMapping(SchemaBinding schemaBinding, JavaXmlTypeMapping typeMapping)
   {
      String javaType = typeMapping.getJavaType();
      if (javaType.endsWith("[]"))
      {
         processArrayType(schemaBinding, typeMapping);
      }
      else
      {
         processNonArrayType(schemaBinding, typeMapping);
      }
   }

   private void processExceptionMapping(SchemaBinding schemaBinding, ExceptionMapping exceptionMapping)
   {
      QName xmlType = exceptionMapping.getWsdlMessage();
      String javaType = exceptionMapping.getExceptionType();
      log.trace("processExceptionMapping: [xmlType=" + xmlType + ",javaType=" + javaType + "]");
      
      if (schemaBinding.getType(xmlType) == null)
      {
         TypeBinding typeBinding = new TypeBinding(xmlType);
         ClassMetaData cmd = new ClassMetaData();
         cmd.setUseNoArgCtor(Boolean.FALSE);
         cmd.setImpl(javaType);
         typeBinding.setClassMetaData(cmd);
         typeBinding.setSimple(false);
         schemaBinding.addType(typeBinding);
      }
   }

   private void processArrayType(SchemaBinding schemaBinding, JavaXmlTypeMapping typeMapping)
   {
      QName xmlType = getXmlType(typeMapping);
      log.trace("Ignore array type: " + xmlType);
   }

   private void processNonArrayType(SchemaBinding schemaBinding, JavaXmlTypeMapping typeMapping)
   {
      QName xmlType = getXmlType(typeMapping);
      String javaType = typeMapping.getJavaType();
      log.trace("processNonArrayType: [xmlType=" + xmlType + ",javaType=" + javaType + "]");

      TypeBinding typeBinding = getTypeBinding(schemaBinding, typeMapping);
      if (typeBinding != null)
      {
         // Set the java type, but skip SimpleTypes
         boolean isSimpleTypeBinding = (typeBinding instanceof SimpleTypeBinding);
         if(isSimpleTypeBinding == false)
         {
         ClassMetaData classMetaData = typeBinding.getClassMetaData();
         if (classMetaData == null)
         {
            classMetaData = new ClassMetaData();
            typeBinding.setClassMetaData(classMetaData);
         }
         classMetaData.setImpl(javaType);

         // exception mapping drives whether we should use the noarg ctor
         JavaWsdlMapping wsdlMapping = typeMapping.getJavaWsdlMapping();
         for (ExceptionMapping aux : wsdlMapping.getExceptionMappings())
         {
            if (javaType.equals(aux.getExceptionType()))
            {
               classMetaData.setUseNoArgCtor(false);
               break;
            }
         }

         if (log.isTraceEnabled())
         {
            QName typeQName = typeBinding.getQName();
            log.trace("Bound: [xmlType=" + typeQName + ",javaType=" + javaType + "]");
            }
         }

         VariableMapping[] variableMappings = typeMapping.getVariableMappings();
         for (VariableMapping varMapping : variableMappings)
         {
            if (varMapping.getXmlElementName() != null)
            {
               processXmlElementName(typeBinding, varMapping);
            }
            else if (varMapping.getXmlAttributeName() != null)
            {
               processXmlAttributeName(typeBinding, varMapping);
            }
            else if (varMapping.getXmlWildcard())
            {
               processWildcard(typeBinding, varMapping);
            }
         }
      }
      else
      {
         log.warn("Cannot obtain type binding for: " + xmlType);
      }
   }

   private void processXmlAttributeName(TypeBinding typeBinding, VariableMapping varMapping)
   {
      String xmlAttrName = varMapping.getXmlAttributeName();
      log.trace("processXmlAttributeName: " + xmlAttrName);

      QName xmlName = new QName(xmlAttrName);
      AttributeBinding attrBinding = typeBinding.getAttribute(xmlName);
      if (attrBinding == null)
      {
         Iterator i = typeBinding.getAttributes().iterator();
         while (i.hasNext())
         {
            AttributeBinding auxBinding = (AttributeBinding)i.next();
            if (auxBinding.getQName().getLocalPart().equals(xmlAttrName))
            {
               if (attrBinding != null)
                  log.warn("Ambiguous binding for attribute: " + xmlAttrName);

               attrBinding = auxBinding;
            }
         }
      }

      if (attrBinding == null)
      {
         // attributeFormDefault="qualified"
         String nsURI = typeBinding.getQName().getNamespaceURI();
         if (Constants.SOAP11_ATTR_MUST_UNDERSTAND.equals(xmlAttrName) || Constants.SOAP11_ATTR_ACTOR.equals(xmlAttrName)
               || Constants.SOAP12_ATTR_ROLE.equals(xmlAttrName))
         {
            nsURI = Constants.NS_SOAP11_ENV;
         }
         QName auxName = new QName(nsURI, xmlAttrName);
         attrBinding = typeBinding.getAttribute(auxName);
      }

      if (attrBinding == null)
      {
         QName typeQName = typeBinding.getQName();
         throw new WSException("Attribute " + xmlName + " found in jaxrpc-mapping but not in the schema: " + typeQName);
      }

      String javaVariableName = varMapping.getJavaVariableName();
      PropertyMetaData prop = new PropertyMetaData();
      prop.setName(javaVariableName);
      attrBinding.setPropertyMetaData(prop);

      if (log.isTraceEnabled())
         log.trace("Bound attribute " + xmlName + " to property " + prop.getName());
   }

   private void processXmlElementName(TypeBinding typeBinding, VariableMapping varMapping)
   {
      QName xmlName = new QName(varMapping.getXmlElementName());
      log.trace("processXmlElementName: " + xmlName);

      ElementBinding element = typeBinding.getElement(xmlName);
      QName typeQName = typeBinding.getQName();
      if (element == null && typeQName != null)
      {
         // elementFormDefault="qualified"
         String nsURI = typeQName.getNamespaceURI();
         QName auxName = new QName(nsURI, varMapping.getXmlElementName());
         element = typeBinding.getElement(auxName);
      }

      if (element == null)
      {
         // <element ref=
         ParticleBinding particle = typeBinding.getParticle();
         if (particle != null)
         {
            TermBinding term = particle.getTerm();
            if (term instanceof ModelGroupBinding)
            {
               Iterator iterator = ((ModelGroupBinding)term).getParticles().iterator();
               element = findLocalPathElement(iterator, new String[] { varMapping.getXmlElementName() }, 0);
            }
         }
      }

      if (element == null)
         throw new WSException("Element " + xmlName + " found in jaxrpc-mapping but not in the schema: " + typeQName);

      String javaVariableName = varMapping.getJavaVariableName();
      if (javaVariableName != null)
      {
         PropertyMetaData prop = new PropertyMetaData();
         prop.setName(javaVariableName);
         element.setPropertyMetaData(prop);

         if (log.isTraceEnabled())
            log.trace("Bound element " + xmlName + " to property " + prop.getName());
      }
   }

   private void processWildcard(TypeBinding typeBinding, VariableMapping varMapping)
   {
      log.trace("processWildcard: " + typeBinding.getQName());

      PropertyMetaData prop = null;
      String javaVariableName = varMapping.getJavaVariableName();
      if (javaVariableName != null)
      {
         prop = new PropertyMetaData();
         prop.setName(javaVariableName);
      }

      if (prop == null)
      {
         prop = new PropertyMetaData();
         prop.setName("_any");
      }

      WildcardBinding wildcard = typeBinding.getWildcard();
      wildcard.setUnresolvedElementHandler(new SoapElementHandler());
      wildcard.setUnresolvedCharactersHandler(new SoapCharactersHandler());
      wildcard.setPropertyMetaData(prop);

      if (log.isTraceEnabled())
         log.trace("Bound wildcard of " + typeBinding.getQName() + " to property " + prop.getName());
   }

   private TypeBinding getTypeBinding(SchemaBinding schemaBinding, JavaXmlTypeMapping typeMapping)
   {
      String qnameScope = typeMapping.getQnameScope();
      QName anonymousTypeQName = typeMapping.getAnonymousTypeQName();
      if (anonymousTypeQName != null)
      {
         return getAnonymousTypeBinding(schemaBinding, anonymousTypeQName);
      }

      QName xmlType = typeMapping.getRootTypeQName();

      TypeBinding typeBinding = null;
      if ("complexType".equals(qnameScope) || "simpleType".equals(qnameScope))
      {
         typeBinding = schemaBinding.getType(xmlType);
         if (typeBinding == null)
         {
            log.warn("Type definition not found in schema: " + xmlType);
         }
      }
      else if ("element".equals(qnameScope))
      {
         ElementBinding element = schemaBinding.getElement(xmlType);
         if (element != null)
         {
            typeBinding = element.getType();
         }
         else
         {
            log.warn("Global element not found in schema: " + xmlType);
         }
      }
      else
      {
         throw new WSException("Unexpected qname-scope for " + typeMapping.getJavaType() + ": " + qnameScope);
      }
      return typeBinding;
   }

   public TypeBinding getAnonymousTypeBinding(SchemaBinding schemaBinding, QName typeQName)
   {
      String expression = typeQName.getLocalPart();
      if (log.isTraceEnabled())
         log.trace("Searching for anonymous expression: " + expression);

      ArrayList list = new ArrayList(10);

      for (int i = 0, begin = -1; i < expression.length(); i++)
      {
         if (expression.charAt(i) == '>')
         {
            if (begin != -1)
            {
               list.add(expression.substring(begin, i));
               begin = -1;
            }
         }
         else
         {
            if (begin == -1)
               begin = i;
            else if (i == expression.length() - 1)
               list.add(expression.substring(begin));
         }
      }

      ElementBinding element = findLocalPathElement(schemaBinding.getElements(), ((String[])list.toArray(new String[0])));
      if (element == null)
         element = findLocalPathElementInTypes(schemaBinding.getTypes(), ((String[])list.toArray(new String[0])));

      if (element == null)
         return null;

      return element.getType();
   }

   public void bindParameterToElement(SchemaBinding schemaBinding, QName xmlName, QName xmlType)
   {
      TypeBinding typeBinding;
      boolean isAnonymousType = xmlType.getLocalPart().startsWith(">");
      if (isAnonymousType)
      {
         typeBinding = getAnonymousTypeBinding(schemaBinding, xmlType);
      }
      else
      {
         typeBinding = schemaBinding.getType(xmlType);
      }

      if (typeBinding != null)
      {
         if(!isAnonymousType)
            schemaBinding.addElement(xmlName, typeBinding);
      }
      else if (xmlType.equals(Constants.TYPE_LITERAL_ANYTYPE) == false)
      {
         throw new WSException("Root type " + xmlType + " not found in the schema.");
      }
   }

   private ElementBinding findLocalPathElement(Iterator elements, String[] path)
   {
      while (elements.hasNext())
      {
         ElementBinding element = (ElementBinding)elements.next();
         element = findLocalPathElement(element, path, 0);
         if (element != null)
            return element;
      }

      return null;
   }

   private ElementBinding findLocalPathElementInTypes(Iterator types, String[] path)
   {
      while (types.hasNext())
      {
         TypeBinding type = (TypeBinding)types.next();
         if (type.getQName().getLocalPart().equals(path[0]))
         {
            ParticleBinding particle = type.getParticle();
            if (particle == null)
               continue;

            TermBinding term = particle.getTerm();
            if (!term.isModelGroup())
               continue;

            return findLocalPathElement(((ModelGroupBinding)term).getParticles().iterator(), path, 1);
         }
      }

      return null;
   }

   private ElementBinding findLocalPathElement(ElementBinding element, String[] path, int pos)
   {
      String name = path[pos];
      if (!name.equals(element.getQName().getLocalPart()))
         return null;

      // End of path
      if (path.length - 1 == pos)
         return element;

      ParticleBinding particle = element.getType().getParticle();
      if (particle == null)
         return null;

      TermBinding term = particle.getTerm();
      if (!term.isModelGroup())
         return null;

      ModelGroupBinding group = (ModelGroupBinding)term;
      Iterator i = group.getParticles().iterator();

      // Increase depth
      return findLocalPathElement(i, path, pos + 1);
   }

   private ElementBinding findLocalPathElement(Iterator particles, String[] path, int pos)
   {
      while (particles.hasNext())
      {
         TermBinding term = ((ParticleBinding)particles.next()).getTerm();
         if (term instanceof ElementBinding)
         {
            ElementBinding element = (ElementBinding)term;
            element = findLocalPathElement(element, path, pos);
            if (element != null)
               return element;

         }
         else if (term instanceof ModelGroupBinding)
         {
            Iterator i = ((ModelGroupBinding)term).getParticles().iterator();
            ElementBinding element = findLocalPathElement(i, path, pos);
            if (element != null)
               return element;
         }
      }

      return null;
   }

   /** Get the <root-type-qname>, fall back to <anonymous-type-qname>
    */
   private QName getXmlType(JavaXmlTypeMapping typeMapping)
   {
      QName xmlType = typeMapping.getRootTypeQName();
      if (xmlType == null && typeMapping.getAnonymousTypeQName() != null)
         xmlType = typeMapping.getAnonymousTypeQName();

      return xmlType;
   }

   // Inner

   public static class SoapCharactersHandler extends CharactersHandler
   {
      public Object unmarshalEmpty(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, ValueMetaData valueMetaData)
      {
         return "";
      }

      public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, ValueMetaData valueMetaData, String value)
      {
         return value;
      }

      public void setValue(QName qName, ElementBinding element, Object owner, Object value)
      {
         SOAPElement e = (SOAPElement)owner;
         Text textNode = e.getOwnerDocument().createTextNode((String)value);
         e.appendChild(textNode);
      }
   }

   public static class SoapElementHandler extends RtElementHandler implements ParticleHandler
   {
      private SOAPFactory factory;

      public Object startParticle(Object parent, QName elementName, ParticleBinding particle, Attributes attrs, NamespaceContext nsCtx)
      {
         SOAPFactory factory = getFactory();
         SOAPElement element = null;
         try
         {
            String prefix = elementName.getPrefix();
            String ns = elementName.getNamespaceURI();
            if (ns != null && ns.length() > 0)
            {
               prefix = nsCtx.getPrefix(ns);
            }

            element = factory.createElement(elementName.getLocalPart(), prefix, ns);
         }
         catch (SOAPException e)
         {
            throw new IllegalStateException("Failed to create SOAPElement", e);
         }

         if (attrs != null)
         {
            for (int i = 0; i < attrs.getLength(); ++i)
            {
               element.setAttribute(attrs.getLocalName(i), attrs.getValue(i));
            }
         }

         return element;
      }

      public Object endParticle(Object o, QName elementName, ParticleBinding particle)
      {
         return o;
      }

      public void setParent(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
      {
         if (parent instanceof SOAPElement)
         {
            ((SOAPElement)parent).appendChild((Element)o);
         }
         else
         {
            super.setParent(parent, o, elementName, particle, parentParticle);
         }
      }

      private SOAPFactory getFactory()
      {
         if (factory == null)
         {
            try
            {
               factory = SOAPFactory.newInstance();
            }
            catch (SOAPException e)
            {
               throw new IllegalStateException("Failed to create soap element factory", e);
            }
         }
         return factory;
      }
   }
}
