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
package org.jboss.ws.tools.jaxws.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxws.AbstractWrapperGenerator;
import org.jboss.ws.metadata.umdm.FaultMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.ws.metadata.umdm.WrappedParameter;
import org.jboss.wsf.common.JavaUtils;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

/**
 * Generates source for wrapper beans
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class SourceWrapperGenerator extends AbstractWrapperGenerator implements WritableWrapperGenerator
{
   private static Logger log = Logger.getLogger(SourceWrapperGenerator.class);
   private PrintStream stream;
   private JCodeModel codeModel;
   
   public SourceWrapperGenerator(ClassLoader loader, PrintStream stream)
   {
      super(loader);
      this.stream = stream;
      codeModel = new JCodeModel();
   }
   
   @Override
   public void reset(ClassLoader loader)
   {
      super.reset(loader);
      codeModel = new JCodeModel();
   }
   
   public void write(File directory) throws IOException
   {
      stream.println("Writing Source:");
      codeModel.build(directory, stream);
   }

   public void generate(ParameterMetaData parameterMD)
   {
      List<WrappedParameter> wrappedParameters = parameterMD.getWrappedParameters();
      OperationMetaData operationMetaData = parameterMD.getOperationMetaData();

      if (operationMetaData.isDocumentWrapped() == false)
      {
         throw new WSException("Operation is not document/literal (wrapped)");
      }

      if (wrappedParameters == null)
      {
         throw new WSException("Cannot generate a type when there is no type information");
      }

      String wrapperName = parameterMD.getJavaTypeName();
      log.debug("Generating wrapper: " + wrapperName);

      try
      {
         JDefinedClass clazz = codeModel._class(wrapperName);
         addClassAnnotations(clazz, parameterMD.getXmlName(), parameterMD.getXmlType(), null);
         for (WrappedParameter wrapped : wrappedParameters)
         {
            addProperty(clazz, wrapped.getType(), wrapped.getName(), wrapped.getVariable(), wrapped.getTypeArguments(), false, wrapped.isXmlList(), wrapped.getAdapter(), loader);
         }
      }
      catch (Exception e)
      {
         throw new WSException("Could not generate wrapper type: " + wrapperName, e);
      }
   }
   
   public void generate(FaultMetaData faultMD)
   {
      String faultBeanName = faultMD.getFaultBeanName();
      Class<?> exception = faultMD.getJavaType();

      try
      {
         SortedMap<String, ExceptionProperty> properties = getExceptionProperties(exception);
         String[] propertyOrder = properties.keySet().toArray(new String[0]);

         JDefinedClass clazz = codeModel._class(faultBeanName);
         addClassAnnotations(clazz, faultMD.getXmlName(), faultMD.getXmlType(), propertyOrder);

         for (String property : propertyOrder)
         {
            ExceptionProperty p = properties.get(property);
            addProperty(clazz, p.getReturnType().getName(), new QName(property), property, null, p.isTransientAnnotated(), false, null, loader);
         }
      }
      catch (Exception e)
      {
         throw new WSException("Could not generate wrapper type: " + faultBeanName, e);
      }
   }

   private static String getterPrefix(Class<?> type)
   {
      return (Boolean.TYPE == type || Boolean.class == type) ? "is" : "get";
   }
   
   private void addProperty(JDefinedClass clazz, String typeName, QName name, String variable, String[] typeArguments, boolean xmlTransient, boolean xmlList, String adapter, ClassLoader loader)
   throws Exception
   {
      // define variable
      Class<?> javaType = JavaUtils.loadJavaType(typeName, loader);
      if (JavaUtils.isPrimitive(javaType))
      {
         addPrimitiveProperty(clazz, javaType, name, variable, xmlTransient);
      }
      else
      {
         addProperty(clazz, javaType, name, variable, typeArguments, xmlTransient, xmlList, adapter, codeModel);
      }
   }

   private static void addProperty(JDefinedClass clazz, Class<?> javaType, QName name, String variable, String[] typeArguments, boolean xmlTransient, boolean xmlList,
         String adapter, JCodeModel codeModel) throws Exception
   {
      // be careful about reserved keywords when generating variable names
      String realVariableName = JavaUtils.isReservedKeyword(variable) ? "_" + variable : variable; 
      
      //use narrow() for generics: http://forums.java.net/jive/thread.jspa?messageID=209333&#209333
      JClass type = codeModel.ref(javaType);
      if (typeArguments != null)
      {
         LinkedList<JClass> jclasses = new LinkedList<JClass>();
         for (String tp : typeArguments)
         {
            jclasses.add(codeModel.ref(tp));
         }
         type = type.narrow(jclasses);
      }
      JFieldVar field = clazz.field(JMod.PRIVATE, type, realVariableName);
      
      if (xmlTransient == false)
      {
         // define XmlElement annotation for variable
         JAnnotationUse annotation = field.annotate(XmlElement.class);
         annotation.param("name", name.getLocalPart());
         if (name.getNamespaceURI() != null)
         {
            annotation.param("namespace", name.getNamespaceURI());
         }
      }
      else
      {
         //XmlTransient
         field.annotate(XmlTransient.class);
      }
      
      if (xmlList)
      {
         field.annotate(XmlList.class);
      }
      
      if (adapter != null)
      {
         JAnnotationUse xmlJavaTypeAdapter = field.annotate(XmlJavaTypeAdapter.class);
         xmlJavaTypeAdapter.param("value", codeModel.ref(adapter));
      }

      // generate acessor get method for variable
      JMethod method = clazz.method(JMod.PUBLIC, type, getterPrefix(javaType) + JavaUtils.capitalize(variable));
      method.body()._return(JExpr._this().ref(realVariableName));
      
      // generate acessor set method for variable
      method = clazz.method(JMod.PUBLIC, void.class, "set" + JavaUtils.capitalize(variable));
      method.body().assign(JExpr._this().ref(realVariableName), method.param(type, realVariableName));
   }
   
   private static void addPrimitiveProperty(JDefinedClass clazz, Class<?> javaType, QName name, String variable, boolean xmlTransient)
   {
      // be careful about reserved keywords when generating variable names
      String realVariableName = JavaUtils.isReservedKeyword(variable) ? "_" + variable : variable; 
      
      JFieldVar field = clazz.field(JMod.PRIVATE, javaType, realVariableName);
      
      if (xmlTransient == false)
      {
         // define XmlElement annotation for variable
         JAnnotationUse annotation = field.annotate(XmlElement.class);
         annotation.param("name", name.getLocalPart());
         if (name.getNamespaceURI() != null)
         {
            annotation.param("namespace", name.getNamespaceURI());
         }
      }
      else
      {
         //XmlTransient
         field.annotate(XmlTransient.class);
      }

      // generate acessor get method for variable
      JMethod method = clazz.method(JMod.PUBLIC, javaType, getterPrefix(javaType) + JavaUtils.capitalize(variable));
      method.body()._return(JExpr._this().ref(realVariableName));
      
      // generate acessor set method for variable
      method = clazz.method(JMod.PUBLIC, void.class, "set" + JavaUtils.capitalize(variable));
      method.body().assign(JExpr._this().ref(realVariableName), method.param(javaType, realVariableName));
   }

   private static void addClassAnnotations(JDefinedClass clazz, QName xmlName, QName xmlType, String[] propertyOrder)
   {
      // define XmlRootElement class annotation
      JAnnotationUse xmlRootElementAnnotation = clazz.annotate(XmlRootElement.class);
      xmlRootElementAnnotation.param("name", xmlName.getLocalPart());
      String xmlNameNS = xmlName.getNamespaceURI(); 
      if (xmlNameNS != null && xmlNameNS.length() > 0)
      {
         xmlRootElementAnnotation.param("namespace", xmlNameNS);
      }

      // define XmlType class annotation
      JAnnotationUse xmlTypeAnnotation = clazz.annotate(XmlType.class); 
      xmlTypeAnnotation.param("name", xmlType.getLocalPart());
      String xmlTypeNS = xmlType.getNamespaceURI();
      if (xmlTypeNS != null && xmlTypeNS.length() > 0)
      {
         xmlTypeAnnotation.param("namespace", xmlTypeNS);
      }
      if (propertyOrder != null)
      {
         JAnnotationArrayMember paramArray = xmlTypeAnnotation.paramArray("propOrder");
         for (String property : propertyOrder)
         {
            paramArray.param(property);
         }
      }

      // define XmlAccessorType class annotation
      JAnnotationUse xmlAccessorTypeAnnotation = clazz.annotate(XmlAccessorType.class);
      xmlAccessorTypeAnnotation.param("value", XmlAccessType.FIELD);
   }
   
}
