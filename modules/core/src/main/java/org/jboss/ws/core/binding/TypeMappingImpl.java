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
package org.jboss.ws.core.binding;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;
import javax.xml.rpc.encoding.TypeMapping;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.core.jaxrpc.binding.Base64DeserializerFactory;
import org.jboss.ws.core.jaxrpc.binding.Base64SerializerFactory;
import org.jboss.ws.core.jaxrpc.binding.CalendarDeserializerFactory;
import org.jboss.ws.core.jaxrpc.binding.CalendarSerializerFactory;
import org.jboss.ws.core.jaxrpc.binding.DateDeserializerFactory;
import org.jboss.ws.core.jaxrpc.binding.DateSerializerFactory;
import org.jboss.ws.core.jaxrpc.binding.HexDeserializerFactory;
import org.jboss.ws.core.jaxrpc.binding.HexSerializerFactory;
import org.jboss.ws.core.jaxrpc.binding.QNameDeserializerFactory;
import org.jboss.ws.core.jaxrpc.binding.QNameSerializerFactory;
import org.jboss.ws.core.jaxrpc.binding.SimpleDeserializerFactory;
import org.jboss.ws.core.jaxrpc.binding.SimpleSerializerFactory;
import org.jboss.ws.core.utils.HashCodeUtil;
import org.jboss.wsf.common.JavaUtils;

/**
 * This is the representation of a type mapping.
 * This TypeMapping implementation supports the literal encoding style.
 *
 * The TypeMapping instance maintains a tuple of the type
 * {XML typeQName, Java Class, SerializerFactory, DeserializerFactory}.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Oct-2004
 */
public abstract class TypeMappingImpl implements TypeMapping
{
   // provide logging
   private static final Logger log = Logger.getLogger(TypeMappingImpl.class);

   // Map<KeyPair,FactoryPair>
   private Map<KeyPair, FactoryPair> tupleMap = new LinkedHashMap<KeyPair, FactoryPair>();

   private Map<Integer, List<KeyPair>> keyPairCache = new ConcurrentHashMap<Integer, List<KeyPair>>();

   /**
    * Gets the DeserializerFactory registered for the specified pair of Java type and XML data type.
    * @param javaType Class of the Java type
    * @param xmlType QName of the XML type
    * @return Registered DeserializerFactory or null if there is no registered factory
    */
   public DeserializerFactory getDeserializer(Class javaType, QName xmlType)
   {
      FactoryPair fPair = getFactoryPair(new IQName(xmlType), javaType);
      return (fPair != null ? fPair.getDeserializerFactory() : null);
   }

   /**
    * Gets the SerializerFactory registered for the specified pair of Java type and XML data type.
    * @param javaType Class of the Java type
    * @param xmlType QName of the XML type
    * @return Registered SerializerFactory or null if there is no registered factory
    */
   public SerializerFactory getSerializer(Class javaType, QName xmlType)
   {
      FactoryPair fPair = getFactoryPair(new IQName(xmlType), javaType);
      return (fPair != null ? fPair.getSerializerFactory() : null);
   }

   /**
    * Returns the encodingStyle URIs (as String[]) supported by this TypeMapping instance.
    * A TypeMapping that contains only encoding style independent serializers and deserializers
    * returns null from this method.
    *
    * @return Array of encodingStyle URIs for the supported encoding styles
    */
   public abstract String[] getSupportedEncodings();

   /**
    * Sets the encodingStyle URIs supported by this TypeMapping instance. A TypeMapping that contains only encoding
    * independent serializers and deserializers requires null as the parameter for this method.
    *
    * @param encodingStyleURIs Array of encodingStyle URIs for the supported encoding styles
    */
   public abstract void setSupportedEncodings(String[] encodingStyleURIs);

   /**
    * Checks whether or not type mapping between specified XML type and Java type is registered.
    * @param javaType Class of the Java type
    * @param xmlType QName of the XML type
    * @return boolean; true if type mapping between the specified XML type and Java type is registered; otherwise false
    */
   public boolean isRegistered(Class javaType, QName xmlType)
   {
      return getFactoryPair(new IQName(xmlType), javaType) != null;
   }

   /**
    * Registers SerializerFactory and DeserializerFactory for a specific type mapping between an XML type and Java type.
    * This method replaces any existing registered SerializerFactory DeserializerFactory instances.
    * @param javaType Class of the Java type
    * @param xmlType QName of the XML type
    * @param sf SerializerFactory
    * @param df DeserializerFactory
    * @throws javax.xml.rpc.JAXRPCException If any error during the registration
    */
   public void register(Class javaType, QName xmlType, SerializerFactory sf, DeserializerFactory df)
   {
      if (log.isTraceEnabled())
         log.trace("register: TypeMappingImpl@"  + hashCode() + " [xmlType=" + xmlType + ",javaType=" + javaType.getName() + ",sf=" + sf + ",df=" + df + "]");
      
      registerInternal(javaType, new IQName(xmlType), sf, df);
      keyPairCache.clear();
   }

   private void registerInternal(Class javaType, IQName xmlType, SerializerFactory sf, DeserializerFactory df)
   {
      if (javaType == null)
         throw new IllegalArgumentException("javaType cannot be null for: " + xmlType);
      if (xmlType == null)
         throw new IllegalArgumentException("xmlType cannot be null for: " + javaType);

      KeyPair kPair = new KeyPair(xmlType, javaType);
      FactoryPair fPair = new FactoryPair(sf, df);
      tupleMap.put(kPair, fPair);
   }

   /**
    * Removes the DeserializerFactory registered for the specified pair of Java type and XML data type.
    * @param javaType Class of the Java type
    * @param xmlType QName of the XML type
    * @throws javax.xml.rpc.JAXRPCException If there is error in removing the registered DeserializerFactory
    */
   public void removeDeserializer(Class javaType, QName xmlType)
   {
      FactoryPair fPair = getFactoryPair(new IQName(xmlType), javaType);
      if (fPair != null)
         fPair.setDeserializerFactory(null);
   }

   /**
    * Removes the SerializerFactory registered for the specified pair of Java type and XML data type.
    * @param javaType Class of the Java type
    * @param xmlType QName of the XML type
    * @throws javax.xml.rpc.JAXRPCException If there is error in removing the registered SerializerFactory
    */
   public void removeSerializer(Class javaType, QName xmlType)
   {
      FactoryPair fPair = getFactoryPair(new IQName(xmlType), javaType);
      if (fPair != null)
         fPair.setSerializerFactory(null);
   }

   /** Get the list of registered XML types */
   public List<QName> getRegisteredXmlTypes()
   {
      List<QName> types = new ArrayList<QName>();
      for (KeyPair keyPair : getKeyPairs(null, null))
      {
         types.add(keyPair.getXmlType().toQName());
      }
      return types;
   }

   /** Get the list of registered Java types */
   public List<Class> getRegisteredJavaTypes()
   {
      List<Class> types = new ArrayList<Class>();
      for (KeyPair keyPair : getKeyPairs(null, null))
      {
         types.add(keyPair.getJavaType());
      }
      return types;
   }

   /** Get the Class that was registered last for this xmlType */
   public Class getJavaType(QName xmlType)
   {
      Class javaType = null;

      List keyPairList = getKeyPairs(new IQName(xmlType), null);
      int size = keyPairList.size();
      if (size > 0)
      {
         KeyPair kPair = (KeyPair)keyPairList.get(size - 1);
         javaType = kPair.getJavaType();
      }

      return javaType;
   }

   /**
    * Get all of the Classes registered for this xmlType.
    */
   public List<Class> getJavaTypes(QName xmlType)
   {
      List<KeyPair> keyPairList = getKeyPairs( new IQName(xmlType), null);
      List<Class> classes = new ArrayList<Class>(keyPairList.size());

      for (KeyPair current : keyPairList)
      {
         classes.add(current.getJavaType());
      }

      return classes;
   }

   /**
    * Get the Class that was registered last for this xmlType
    * If there are two Java Types registered for the xmlType
    * return the primitive type rather than the wrapper,
    * if available
    */
   public Class getJavaType(QName xmlType,boolean getPrimitive)
   {
      //Lets get the primitive type if available
      Class javaType = null;

      List keyPairList = getKeyPairs(new IQName(xmlType), null);
      int size = keyPairList.size();
      if (size == 2 && getPrimitive)
      {
         KeyPair kPair1 = (KeyPair)keyPairList.get(0);
         Class javaType1 = kPair1.getJavaType();
         KeyPair kPair2 = (KeyPair)keyPairList.get(1);
         Class javaType2 = kPair2.getJavaType();
         if(javaType2.isPrimitive() && !javaType1.isPrimitive())
            javaType =  javaType2;
         else
         if(javaType1.isPrimitive() && !javaType2.isPrimitive())
            javaType =  javaType1;
         else
            javaType = javaType2; //Fallback on the most latest
      }
      else
         return getJavaType(xmlType);

      return javaType;
   }

   /** Get the Class name that was registered last for this xmlType */
   public String getJavaTypeName(QName xmlType)
   {
      Class javaType = getJavaType(xmlType);
      return (javaType != null ? javaType.getName() : null);
   }

   /** Get the QName that was registered last for this javaType */
   public QName getXMLType(Class javaType)
   {
      QName xmlType = null;

      List keyPairList = getKeyPairs(null, javaType);
      int size = keyPairList.size();
      if (size > 0)
      {
         KeyPair kPair = (KeyPair)keyPairList.get(size - 1);
         xmlType = kPair.getXmlType().toQName();
      }

      return xmlType;
   }

   /** Get the QNames that was registered last for this javaType */
   public List<QName> getXMLTypes(Class javaType)
   {
      List<QName> xmlTypes = new ArrayList<QName>();

      for (KeyPair kPair : getKeyPairs(null, javaType))
      {
         xmlTypes.add(kPair.getXmlType().toQName());
      }
      return xmlTypes;
   }

   /**
    * Get the QName that was registered last for this javaType
    * @param javaType class for which XML Type is needed
    * @param tryAssignable  If the xmlType is not registered for javaType
    *                                           should a base class type be checked?
    *
    */
   public QName getXMLType(Class javaType, boolean tryAssignable)
   {
      if(tryAssignable) return getXMLType(javaType);

      QName xmlType = null;

      List keyPairList = getKeyPairs(null, javaType, tryAssignable);
      int size = keyPairList.size();
      if (size > 0)
      {
         KeyPair kPair = (KeyPair)keyPairList.get(size - 1);
         xmlType = kPair.getXmlType().toQName();
      }

      return xmlType;
   }

   /**
    * Get the serializer/deserializer factory pair for the given xmlType, javaType
    * Both xmlType, javaType may be null. In that case, this implementation still
    * returns a FactoryPair if there is only one possible match.
    *
    * @param xmlType can be null
    * @param javaType can be null
    */
   private List<KeyPair> getKeyPairs(IQName xmlType, Class javaType)
   {
      Integer cacheId = cacheIdFor(javaType, xmlType);

      List<KeyPair> keyPairList = keyPairCache.get(cacheId);
      if(null == keyPairList)
      {
         keyPairList = getKeyPairsInternal(xmlType, javaType);
         keyPairCache.put(cacheId, keyPairList);
      }

      return keyPairList;
   }

   private Integer cacheIdFor(Class javaType, IQName xmlType) {
      int result = HashCodeUtil.SEED;
      int nullHash = HashCodeUtil.hash(result, "null");
      result = javaType!= null ? HashCodeUtil.hash(result, javaType.getName()) : HashCodeUtil.hash(result, nullHash);
      result = xmlType!= null ? HashCodeUtil.hash(result, xmlType.hashCode()): HashCodeUtil.hash(result, nullHash);
      return Integer.valueOf(result);
   }

   private List<KeyPair> getKeyPairsInternal(IQName xmlType, Class javaType)
   {
      List<KeyPair> keyPairList = new ArrayList<KeyPair>();

      // Getting the exact matching pair
      if (xmlType != null && javaType != null)
      {
         for (KeyPair entry : tupleMap.keySet())
         {
            if (xmlType.equals(entry.getXmlType()) && entry.getJavaType() == javaType)
            {
               keyPairList.add(entry);
            }
         }
         // No exact match, try assignable
         if (keyPairList.size() == 0)
         {
            for (KeyPair entry : tupleMap.keySet())
            {
               if (xmlType.equals(entry.getXmlType()) && JavaUtils.isAssignableFrom(entry.getJavaType(), javaType))
               {
                  keyPairList.add(entry);
               }
            }
         }
      }

      // Getting the pair for a given xmlType
      else if (xmlType != null && javaType == null)
      {
         for (KeyPair entry : tupleMap.keySet())
         {
            if (xmlType.equals(entry.getXmlType()))
            {
               keyPairList.add(entry);
            }
         }
      }

      // Getting the pair for a given javaType
      else if (xmlType == null && javaType != null)
      {
         for (KeyPair entry : tupleMap.keySet())
         {
            if (entry.getJavaType() == javaType)
            {
               keyPairList.add(entry);
            }
         }
         // No exact match, try assignable
         if (keyPairList.size() == 0)
         {
            for (KeyPair entry : tupleMap.keySet())
            {
               if (JavaUtils.isAssignableFrom(entry.getJavaType(), javaType))
               {
                  keyPairList.add(entry);
               }
            }
         }
      }

      // Getting the all pairs
      else if (xmlType == null && javaType == null)
      {
         keyPairList.addAll(tupleMap.keySet());
      }

      return keyPairList;
   }

    private List<KeyPair> getKeyPairs(IQName xmlType, Class javaType, boolean tryAssignable)
    {
      Integer cacheId = cacheIdFor(javaType, xmlType);

      List<KeyPair> keyPairList = keyPairCache.get(cacheId);
      if(null == keyPairList)
      {
         keyPairList = getKeyPairsInternal(xmlType, javaType, tryAssignable);
         keyPairCache.put(cacheId, keyPairList);
      }

      return keyPairList;
    }

   /**
    * Get the serializer/deserializer factory pair for the given xmlType, javaType
    * Both xmlType, javaType may be null. In that case, this implementation still
    * returns a FactoryPair if there is only one possible match.
    * <br>Note: This method does not try for the base class, if no keypair exists for the
    * javaType in question.
    */
   private List<KeyPair> getKeyPairsInternal(IQName xmlType, Class javaType, boolean tryAssignable)
   {
      if(tryAssignable) return getKeyPairs( xmlType, javaType );

      List<KeyPair> keyPairList = new ArrayList<KeyPair>();

      // Getting the exact matching pair
      if (xmlType != null && javaType != null)
      {
         for (KeyPair entry : tupleMap.keySet())
         {
            if (xmlType.equals(entry.getXmlType()) && entry.getJavaType() == javaType)
            {
               keyPairList.add(entry);
            }
         }
      }

      // Getting the pair for a given xmlType
      else if (xmlType != null && javaType == null)
      {
         for (KeyPair entry : tupleMap.keySet())
         {
            if (xmlType.equals(entry.getXmlType()))
            {
               keyPairList.add(entry);
            }
         }
      }

      // Getting the pair for a given javaType
      else if (xmlType == null && javaType != null)
      {
         for (KeyPair entry : tupleMap.keySet())
         {
            if (entry.getJavaType() == javaType)
            {
               keyPairList.add(entry);
            }
         }
      }

      // Getting the all pairs
      else if (xmlType == null && javaType == null)
      {
         keyPairList.addAll(tupleMap.keySet());
      }

      return keyPairList;
   }

   /**
    * Get the serializer/deserializer factory pair for the given xmlType, javaType
    * Both xmlType, javaType may be null. In that case, this implementation still
    * returns a FactoryPair that was last registered
    */
   private FactoryPair getFactoryPair(IQName xmlType, Class javaType)
   {
      FactoryPair fPair = null;

      List<KeyPair> keyPairList = getKeyPairs(xmlType, javaType);
      int size = keyPairList.size();
      if (size > 0)
      {
         KeyPair kPair = keyPairList.get(size - 1);
         fPair = (FactoryPair)tupleMap.get(kPair);
      }

      return fPair;
   }

   protected void registerStandardLiteralTypes()
   {
      register(BigDecimal.class, Constants.TYPE_LITERAL_DECIMAL, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      register(BigInteger.class, Constants.TYPE_LITERAL_POSITIVEINTEGER, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(BigInteger.class, Constants.TYPE_LITERAL_NEGATIVEINTEGER, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(BigInteger.class, Constants.TYPE_LITERAL_NONPOSITIVEINTEGER, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(BigInteger.class, Constants.TYPE_LITERAL_NONNEGATIVEINTEGER, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(BigInteger.class, Constants.TYPE_LITERAL_UNSIGNEDLONG, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(BigInteger.class, Constants.TYPE_LITERAL_INTEGER, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      register(Date.class, Constants.TYPE_LITERAL_DATETIME, new DateSerializerFactory(), new DateDeserializerFactory());

      register(Calendar.class, Constants.TYPE_LITERAL_DATE, new CalendarSerializerFactory(), new CalendarDeserializerFactory());
      register(Calendar.class, Constants.TYPE_LITERAL_TIME, new CalendarSerializerFactory(), new CalendarDeserializerFactory());
      register(Calendar.class, Constants.TYPE_LITERAL_DATETIME, new CalendarSerializerFactory(), new CalendarDeserializerFactory());

      register(QName.class, Constants.TYPE_LITERAL_QNAME, new QNameSerializerFactory(), new QNameDeserializerFactory());

      register(String.class, Constants.TYPE_LITERAL_ANYSIMPLETYPE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(String.class, Constants.TYPE_LITERAL_DURATION, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(String.class, Constants.TYPE_LITERAL_GDAY, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(String.class, Constants.TYPE_LITERAL_GMONTH, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(String.class, Constants.TYPE_LITERAL_GMONTHDAY, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(String.class, Constants.TYPE_LITERAL_GYEAR, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(String.class, Constants.TYPE_LITERAL_GYEARMONTH, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(String.class, Constants.TYPE_LITERAL_ID, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(String.class, Constants.TYPE_LITERAL_LANGUAGE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(String.class, Constants.TYPE_LITERAL_NAME, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(String.class, Constants.TYPE_LITERAL_NCNAME, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(String.class, Constants.TYPE_LITERAL_NMTOKEN, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(String.class, Constants.TYPE_LITERAL_NORMALIZEDSTRING, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(String.class, Constants.TYPE_LITERAL_TOKEN, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(String.class, Constants.TYPE_LITERAL_STRING, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      register(String[].class, Constants.TYPE_LITERAL_NMTOKENS, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      register(URI.class, Constants.TYPE_LITERAL_ANYURI, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      register(boolean.class, Constants.TYPE_LITERAL_BOOLEAN, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(Boolean.class, Constants.TYPE_LITERAL_BOOLEAN, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      register(byte.class, Constants.TYPE_LITERAL_BYTE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(Byte.class, Constants.TYPE_LITERAL_BYTE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      register(Byte[].class, Constants.TYPE_LITERAL_HEXBINARY, new HexSerializerFactory(), new HexDeserializerFactory());
      register(byte[].class, Constants.TYPE_LITERAL_HEXBINARY, new HexSerializerFactory(), new HexDeserializerFactory());

      register(Byte[].class, Constants.TYPE_LITERAL_BASE64BINARY, new Base64SerializerFactory(), new Base64DeserializerFactory());
      register(byte[].class, Constants.TYPE_LITERAL_BASE64BINARY, new Base64SerializerFactory(), new Base64DeserializerFactory());

      register(double.class, Constants.TYPE_LITERAL_DOUBLE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(Double.class, Constants.TYPE_LITERAL_DOUBLE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      register(float.class, Constants.TYPE_LITERAL_FLOAT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(Float.class, Constants.TYPE_LITERAL_FLOAT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      register(int.class, Constants.TYPE_LITERAL_UNSIGNEDSHORT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(Integer.class, Constants.TYPE_LITERAL_UNSIGNEDSHORT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(int.class, Constants.TYPE_LITERAL_INT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(Integer.class, Constants.TYPE_LITERAL_INT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      register(long.class, Constants.TYPE_LITERAL_UNSIGNEDINT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(Long.class, Constants.TYPE_LITERAL_UNSIGNEDINT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(long.class, Constants.TYPE_LITERAL_LONG, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(Long.class, Constants.TYPE_LITERAL_LONG, new SimpleSerializerFactory(), new SimpleDeserializerFactory());

      register(short.class, Constants.TYPE_LITERAL_UNSIGNEDBYTE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(Short.class, Constants.TYPE_LITERAL_UNSIGNEDBYTE, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(short.class, Constants.TYPE_LITERAL_SHORT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
      register(Short.class, Constants.TYPE_LITERAL_SHORT, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
   }

   /** A tuple of the type {XML typeQName, Java Class, SerializerFactory, DeserializerFactory}.
    */
   private static class KeyPair
   {
      private IQName xmlType;
      private Class javaType;

      public KeyPair(IQName xmlType, Class javaType)
      {
         this.javaType = javaType;
         this.xmlType = xmlType;
      }

      public Class getJavaType()
      {
         return javaType;
      }

      public IQName getXmlType()
      {
         return xmlType;
      }

      public boolean equals(Object o)
      {
         if (this == o) return true;
         if (!(o instanceof KeyPair)) return false;

         final KeyPair keyPair = (KeyPair)o;

         if (!javaType.equals(keyPair.javaType)) return false;
         if (!xmlType.equals(keyPair.xmlType)) return false;

         return true;
      }

      public int hashCode()
      {
         int result;
         result = xmlType.hashCode();
         result = 29 * result + javaType.hashCode();
         return result;
      }

      public String toString()
      {
         return "[xmlType=" + xmlType + ",javaType=" + javaType.getName() + "]";
      }
   }

   /** A tuple of the type {XML typeQName, Java Class, SerializerFactory, DeserializerFactory}.
    */
   public static class FactoryPair
   {
      private SerializerFactory serializerFactory;
      private DeserializerFactory deserializerFactory;

      FactoryPair(SerializerFactory sf, DeserializerFactory df)
      {
         this.deserializerFactory = df;
         this.serializerFactory = sf;
      }

      public DeserializerFactory getDeserializerFactory()
      {
         return deserializerFactory;
      }

      public SerializerFactory getSerializerFactory()
      {
         return serializerFactory;
      }

      public void setDeserializerFactory(DeserializerFactory df)
      {
         this.deserializerFactory = df;
      }

      public void setSerializerFactory(SerializerFactory sf)
      {
         this.serializerFactory = sf;
      }
   }

   /**
    * A duck typed QName that relies on internalized Strings.<p>
    * Taken from the {@link javax.xml.namespace.QName} docs:<br>
    * The value of a QName contains a Namespace URI, local part and prefix.
    * The prefix is included in QName to retain lexical information when present in an XML input source.
    * The prefix is NOT used in QName.equals(Object) or to compute the QName.hashCode().
    * Equality and the hash code are defined using only the Namespace URI and local part.
    * If not specified, the Namespace URI is set to "" (the empty string).
    * If not specified, the prefix is set to "" (the empty string).
    */
   private final class IQName
   {
      public String namespace;
      public String localPart;
      public String prefix;
      public int hash;

      public IQName(QName name)
      {
         namespace = name.getNamespaceURI() != null ? name.getNamespaceURI().intern() : "".intern();
         localPart = name.getLocalPart() != null ? name.getLocalPart().intern() : "".intern();
         prefix = name.getPrefix() != null ? name.getPrefix().intern() : "".intern();
         hash = name.hashCode();
      }

      public boolean equals(Object object) {
         if(!(object instanceof IQName))
            throw new IllegalArgumentException("Cannot compare IQName to " + object);

         IQName iqn = (IQName)object;
         return (iqn.namespace == this.namespace && iqn.localPart == this.localPart);
      }

      public QName toQName()
      {
         QName qname;

         if(null == namespace)
            qname = new QName(localPart);
         else if(null == prefix)
            qname = new QName(namespace, localPart);
         else
            qname = new QName(namespace, localPart, prefix);

         return qname;
      }

      /**
       * This implementation currently represents a QName as: "{" + Namespace URI + "}" + local part.
       * If the Namespace URI .equals(""), only the local part is returned.
       */
      public String toString() {
         String ns = "".equals(namespace) ? namespace : "{"+namespace+"}";
         return ns+localPart;
      }

      public int hashCode()
      {
         return this.hash;
      }
   }
}
