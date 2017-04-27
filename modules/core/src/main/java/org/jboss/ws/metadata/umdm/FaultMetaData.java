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
package org.jboss.ws.metadata.umdm;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxws.DynamicWrapperGenerator;
import org.jboss.ws.metadata.accessor.AccessorFactory;
import org.jboss.ws.metadata.accessor.ReflectiveFieldAccessorFactoryCreator;
import org.jboss.ws.metadata.accessor.ReflectiveMethodAccessorFactoryCreator;
import org.jboss.ws.metadata.umdm.EndpointMetaData.Type;
import org.jboss.wsf.common.JavaUtils;

/**
 * A Fault component describes a fault that a given operation supports.
 *
 * @author Thomas.Diesler@jboss.org
 * @author jason.greene@jboss.com
 * @since 12-May-2005
 */
public class FaultMetaData implements InitalizableMetaData
{
   // provide logging
   private final Logger log = Logger.getLogger(FaultMetaData.class);

   // The parent operation
   private OperationMetaData opMetaData;

   private QName xmlName;
   private QName xmlType;
   private String javaTypeName;
   private String faultBeanName;
   private Class javaType;
   private Class faultBean;

   private Method faultInfoMethod;
   private Constructor serviceExceptionConstructor;
   private Method[] serviceExceptionGetters;

   private WrappedParameter[] faultBeanProperties;

   private Class[] propertyTypes;

   public FaultMetaData(OperationMetaData operation, QName xmlName, QName xmlType, String javaTypeName)
   {
      this(operation, xmlName, javaTypeName);
      setXmlType(xmlType);
   }

   public FaultMetaData(OperationMetaData operation, QName xmlName, String javaTypeName)
   {
      if (xmlName == null)
         throw new IllegalArgumentException("Invalid null xmlName argument");
      if (javaTypeName == null)
         throw new IllegalArgumentException("Invalid null javaTypeName argument, for: " + xmlName);

      this.opMetaData = operation;
      this.xmlName = xmlName;
      this.javaTypeName = javaTypeName;
   }

   public OperationMetaData getOperationMetaData()
   {
      return opMetaData;
   }

   public QName getXmlName()
   {
      return xmlName;
   }

   public QName getXmlType()
   {
      return xmlType;
   }

   public void setXmlType(QName xmlType)
   {
      if (xmlType == null)
         throw new IllegalArgumentException("Invalid null xmlType argument, for: " + xmlName);

      this.xmlType = xmlType;
   }

   public String getJavaTypeName()
   {
      return javaTypeName;
   }

   /** Load the java type.
    *  It should only be cached during eager initialization.
    */
   public Class getJavaType()
   {
      if (javaType != null)
         return javaType;

      if (javaTypeName == null)
         return null;

      try
      {
         ClassLoader loader = opMetaData.getEndpointMetaData().getClassLoader();
         Class exceptionType = JavaUtils.loadJavaType(javaTypeName, loader);
         if (Exception.class.isAssignableFrom(exceptionType) == false)
            throw new IllegalStateException("Is not assignable to exception: " + exceptionType);

         if (opMetaData.getEndpointMetaData().getServiceMetaData().getUnifiedMetaData().isEagerInitialized())
         {
            log.warn("Loading java type after eager initialization");
            javaType = exceptionType;
         }
         return exceptionType;
      }
      catch (ClassNotFoundException ex)
      {
         throw new WSException("Cannot load java type: " + javaTypeName, ex);
      }
   }

   public String getFaultBeanName()
   {
      return faultBeanName;
   }

   public void setFaultBeanName(String faultBeanName)
   {
      this.faultBeanName = faultBeanName;
   }

   public Class loadFaultBean()
   {
      Class faultBean = null;
      try
      {
         ClassLoader loader = getOperationMetaData().getEndpointMetaData().getClassLoader();
         faultBean = JavaUtils.loadJavaType(faultBeanName, loader);
      }
      catch (ClassNotFoundException ex)
      {
         // ignore
      }
      return faultBean;
   }

   public Class getFaultBean()
   {
      Class tmpFaultBean = faultBean;
      if (tmpFaultBean == null && faultBeanName != null)
      {
         try
         {
            ClassLoader loader = opMetaData.getEndpointMetaData().getClassLoader();
            tmpFaultBean = JavaUtils.loadJavaType(faultBeanName, loader);
         }
         catch (ClassNotFoundException ex)
         {
            throw new WSException("Cannot load fault bean: " + faultBeanName, ex);
         }
      }
      return tmpFaultBean;
   }

   public void validate()
   {
      // nothing to do
   }

   public void eagerInitialize()
   {
      Type epType = getOperationMetaData().getEndpointMetaData().getType();
      if (epType == EndpointMetaData.Type.JAXWS && faultBeanName != null)
      {
         if (loadFaultBean() == null)
         {
            ClassLoader loader = opMetaData.getEndpointMetaData().getClassLoader();
            new DynamicWrapperGenerator(loader).generate(this);
         }
      }

      // Initialize the cache
      javaType = getJavaType();
      if (javaType == null)
         throw new WSException("Cannot load java type: " + javaTypeName);

      if (JavaUtils.isAssignableFrom(Exception.class, javaType) == false)
         throw new WSException("Fault java type is not a java.lang.Exception: " + javaTypeName);

      if (epType == EndpointMetaData.Type.JAXWS)
      {
         faultBean = getFaultBean();
         if (faultBean != null)
            initializeFaultBean();
      }
   }

   private void initializeFaultBean()
   {
      /* JAX-WS 3.7: For exceptions that match the pattern described in section
       * 2.5 (i.e. exceptions that have a getFaultInfo method), the FaultBean
       * is used as input to JAXB */
      try
      {
         /* JAX-WS 2.5: A wsdl:fault element refers to a wsdl:message that contains
          * a single part. The global element declaration referred to by that part
          * is mapped to a Java bean. A wrapper exception class contains the
          * following methods:
          * . WrapperException(String message, FaultBean faultInfo)
          * . WrapperException(String message, FaultBean faultInfo, Throwable cause)
          * . FaultBean getFaultInfo() */
         serviceExceptionConstructor = javaType.getConstructor(String.class, faultBean);
         faultInfoMethod = javaType.getMethod("getFaultInfo");
      }
      /* JAX-WS 3.7: For exceptions that do not match the pattern described in
       * section 2.5, JAX-WS maps those exceptions to Java beans and then uses
       * those Java beans as input to the JAXB mapping. */
      catch (NoSuchMethodException nsme)
      {
         /* For each getter in the exception and its superclasses, a property of
          * the same type and name is added to the bean. */
         XmlType xmlType = (XmlType)faultBean.getAnnotation(XmlType.class);
         if (xmlType == null)
            throw new WebServiceException("@XmlType missing from fault bean: " + faultBeanName);

         AccessorFactory accessorFactory = getAccessorFactory(faultBean);

         String[] propertyNames = xmlType.propOrder();
         int propertyCount = propertyNames.length;
         propertyTypes = new Class[propertyCount];
         faultBeanProperties = new WrappedParameter[propertyCount];
         serviceExceptionGetters = new Method[propertyCount];

         for (int i = 0; i < propertyCount; i++)
         {
            String propertyName = propertyNames[i];
            // extract property metadata from the fault bean
            try
            {
               PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, faultBean);
               Class propertyType = propertyDescriptor.getPropertyType();

               WrappedParameter faultBeanProperty = new WrappedParameter(null, propertyType.getName(), propertyName, i);
               faultBeanProperty.setAccessor(accessorFactory.create(faultBeanProperty));
               faultBeanProperties[i] = faultBeanProperty;

               propertyTypes[i] = propertyType;
            }
            catch (IntrospectionException ie)
            {
               throw new WSException("Property '" + propertyName + "' not found in fault bean '" + faultBeanName + "'", ie);
            }

            // extract property metadata from the service exception
            try
            {
               /* use PropertyDescriptor(String, Class, String, String) instead
                * of PropertyDescriptor(String, Class) because the latter requires
                * the setter method to be present */
               PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, javaType, "is" + JavaUtils.capitalize(propertyName), null);
               serviceExceptionGetters[i] = propertyDescriptor.getReadMethod();
            }
            catch (IntrospectionException ie)
            {
               throw new WSException("Property '" + propertyName + "' not found in service exception '" + javaTypeName, ie);
            }
         }

         try
         {
            // Attempt to locate a usable constructor
            serviceExceptionConstructor = javaType.asSubclass(Exception.class).getConstructor(propertyTypes);
         }
         catch (NoSuchMethodException e)
         {
            // Only needed for client side. The spec does not clarify this, and the TCK makes use of non matching constructors,
            // so we allow them for server side usage and only fail when used by the client.
         }
      }
   }

   private AccessorFactory getAccessorFactory(Class faultBean)
   {
      // This should catch all cases due to the constraints that JAX-WS puts on the fault bean
      // However, if issues arrise then switch this to a full jaxb reflection library
      XmlAccessorType type = (XmlAccessorType)faultBean.getAnnotation(XmlAccessorType.class);
      if (type != null && type.value() == XmlAccessType.FIELD)
         return new ReflectiveFieldAccessorFactoryCreator().create(this);

      return new ReflectiveMethodAccessorFactoryCreator().create(this);
   }

   public Object toFaultBean(Exception serviceException)
   {
      Object faultBeanInstance;
      try
      {
         /* is the service exception a wrapper
          * (i.e. does it match the pattern in JAX-WS 2.5)? */
         if (faultInfoMethod != null)
         {
            // extract the fault bean from the wrapper exception
            faultBeanInstance = faultInfoMethod.invoke(serviceException);
         }
         else
         {
            // instantiate the fault bean
            try
            {
               faultBeanInstance = faultBean.newInstance();
            }
            catch (InstantiationException e)
            {
               throw new WebServiceException("Fault bean class is not instantiable", e);
            }

            // copy the properties from the service exception to the fault bean
            for (int i = 0; i < serviceExceptionGetters.length; i++)
            {
               Object propertyValue = serviceExceptionGetters[i].invoke(serviceException);

               WrappedParameter faultBeanProperty = faultBeanProperties[i];
               if (log.isTraceEnabled())
                  log.trace("copying from " + javaType.getSimpleName() + '.' + serviceExceptionGetters[i].getName()
                     + " to " + faultBean.getSimpleName() + '.' + faultBeanProperty.getVariable() + "<->" + faultBeanProperty.getName()
                     + ": " + propertyValue);
               faultBeanProperty.accessor().set(faultBeanInstance, propertyValue);
            }
         }
      }
      catch (IllegalAccessException e)
      {
         throw new WebServiceException(e);
      }
      catch (InvocationTargetException e)
      {
         throw new WebServiceException(e.getTargetException());
      }
      return faultBeanInstance;
   }

   public Exception toServiceException(Object faultBean, String message)
   {
      Exception serviceException;

      try
      {
         /* is the service exception a wrapper
          * (i.e. does it match the pattern in JAX-WS 2.5)? */
         if (faultInfoMethod != null)
         {
            serviceException = (Exception)serviceExceptionConstructor.newInstance(message, faultBean);
         }
         else
         {
            if (serviceExceptionConstructor == null)
               throw new WSException("Could not instantiate service exception (" + javaType.getSimpleName()  +"), since neither a faultInfo nor sorted constructor is present: " + Arrays.toString(propertyTypes));

            // extract the properties from the fault bean
            int propertyCount = faultBeanProperties.length;
            Object[] propertyValues = new Object[propertyCount];

            for (int i = 0; i < propertyCount; i++)
               propertyValues[i] = faultBeanProperties[i].accessor().get(faultBean);

            log.debug("constructing " + javaType.getSimpleName() + ": " + Arrays.toString(propertyValues));
            serviceException = (Exception)serviceExceptionConstructor.newInstance(propertyValues);
         }
      }
      catch (InstantiationException e)
      {
         throw new WebServiceException("Service exception is not instantiable", e);
      }
      catch (IllegalAccessException e)
      {
         throw new WebServiceException(e);
      }
      catch (InvocationTargetException e)
      {
         throw new WebServiceException(e.getTargetException());
      }
      return serviceException;
   }

   public String toString()
   {
      StringBuilder buffer = new StringBuilder("\nFaultMetaData");
      buffer.append("\n xmlName=" + xmlName);
      buffer.append("\n xmlType=" + xmlType);
      buffer.append("\n javaType=" + javaTypeName);
      buffer.append("\n faultBean=" + faultBeanName);
      return buffer.toString();
   }
}
