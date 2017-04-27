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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.rmi.Remote;
import java.util.Calendar;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.rpc.holders.ByteArrayHolder;
import javax.xml.rpc.holders.Holder;

import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.core.jaxrpc.binding.SimpleDeserializerFactory;
import org.jboss.ws.core.jaxrpc.binding.SimpleSerializerFactory;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xsd.SchemaUtils;
import org.jboss.wsf.common.JavaUtils;

/**
 * Util class for the JBossWS Tools project
 * Contains important methods that deal with JAXB 1, JAXB2
 * and JAXWS 2.0 specs
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jun 25, 2005
 */
public class ToolsUtils
{

   /**
    * Maintains a static reference to the TypeMapping just for
    * the standard type mappings
    */
   private static LiteralTypeMapping mapping = new LiteralTypeMapping();

   //Hide the constructor - this is a class with static methods
   private ToolsUtils()
   {
      throw new WSException("Cannot instantiate ToolsUtils.");
   }

   /**
    * Checks whether the class extends java.rmi.Remote
    *
    * @param paramType
    */
   public static void checkParameterType( Class paramType)
   {
      if (Remote.class.isAssignableFrom(paramType))
         throw new IllegalArgumentException("JAXWS-2.0 Assertion::" + "Method param shouldn't extend Remote");
   }

   /**
    * Ensure that the first alphabet is uppercase
    * @param fname
    * @return String that has first character upper case
    */
   public static String firstLetterUpperCase(String fname)
   {
      if (fname == "" || fname == null)
         throw new WSException("String passed is null");
      //Ensure that the first character is uppercase
      if (Character.isLowerCase(fname.charAt(0)))
      {
         char[] chars = fname.toCharArray();
         char f = Character.toUpperCase(chars[0]);
         chars[0] = f;
         fname = new String(chars);
      }
      return fname;
   }

   /**
    * Ensure that the first alphabet is lowercase
    * @param fname
    * @return String that has first character lower case
    */
   public static String firstLetterLowerCase(String fname)
   {
      if (fname == "" || fname == null)
         throw new WSException("String passed is null");
      //Ensure that the first character is lowercase
      if (Character.isUpperCase(fname.charAt(0)))
      {
         char[] chars = fname.toCharArray();
         char f = Character.toLowerCase(chars[0]);
         chars[0] = f;
         fname = new String(chars);
      }
      return fname;
   }

   /**
    * Returns a Java Identifier from a XML Name
    * as specified by both JAXB 1 and JAXB 2 specs.
    * Used for deriving class names, method names etc
    * @param xmlName XML Name
    * @return Legal Java Identifier
    */
   public static String getJavaIdentifier(String xmlName)
   {
      if(xmlName == null || xmlName == "")
         throw new IllegalArgumentException("xmlName is null");
      xmlName = xmlName.trim(); //Get rid of whitespaces

      //Remove leading and trailing punctuation marks
      xmlName = trimPunctuationMarks( xmlName);

      if(xmlName == null)
         throw new WSException("xmlName has become null");

      //Get rid of characters that are not legal characters
      int lenStr = xmlName.length();
      char[] mainArr = new char[lenStr];
      int j = 0;
      for(int i = 0; i < lenStr;i++)
      {
         char ch = xmlName.charAt(i);
         if(Character.isJavaIdentifierPart(ch))
         {
            mainArr[j] = ch;
            j++;
         }
      }

      //Copy into a new array
      char[] secArr = new char[j];
      for(int x = 0; x < j ; x++)
      {
         secArr[x] = mainArr[x];
      }

      return new String(secArr);
   }

   /**
    * Get valid Java Method name as per JAXB 1 and JAXB2.0 spec
    * @param xmlName XML Name
    * @param setter is this method for which the name is sought, a setter?
    * @return Valid Java method name
    */
   public static String getJavaMethodName( String xmlName, boolean setter)
   {
      xmlName = getJavaIdentifier(xmlName);
      xmlName = firstLetterUpperCase(xmlName);
      String lcase = xmlName.toLowerCase();
      if( setter && !lcase.startsWith("set"))
      {
         xmlName = "set" + xmlName;
      }
      else
         if(!setter && !lcase.startsWith("get"))
      {
         xmlName = "get" + xmlName;
      }
      return firstLetterLowerCase(xmlName);
   }


   /**
    * Get a valid Java class name as per JAXB 1 and JAXB2.0 spec
    * @param xmlName XML Name
    * @return Valid class name
    */
   public static String getValidClassName( String xmlName)
   {
      xmlName = getJavaIdentifier(xmlName);
      //Ensure the first character is uppercase
      return firstLetterUpperCase(xmlName);
   }

   /**
    * Given a Java Class, return the xmlType
    *
    * @param javaClass
    * @param ns
    * @return
    */
   public static QName getXMLType(Class javaClass, String ns)
   {
      WSDLUtils utils = WSDLUtils.getInstance();
      QName xmlType = null;
      if(void.class == javaClass)
         return null;

      /**
       * Special boundary condition: When the javaClass is ByteArrayHolder,
       * the xmlType should be xsd:base64Binary, but because of our algorithm,
       * it will become xsd:byte - so need to bypass this.
       */
      if(ByteArrayHolder.class == javaClass)
      {
         return Constants.TYPE_LITERAL_BASE64BINARY;
      }

      if(Holder.class.isAssignableFrom(javaClass))
         javaClass = utils.getJavaTypeForHolder(javaClass);
      xmlType = SchemaUtils.getInstance().getToolsOverrideInTypeMapping(javaClass);
      if(xmlType != null)
         return xmlType;

      // Byte[] should always be mapped to an array of bytes
      // String[] should always be mapped to an array of strings
      if (javaClass != Byte[].class && javaClass != String[].class)
      {
         xmlType = mapping.getXMLType(javaClass,false);
         if(xmlType != null)
            return xmlType;
      }
      //Else create a QName
      String typeName = utils.getJustClassName(javaClass);;
      if(javaClass.isArray())
      {
         int dim = utils.getArrayDimension(javaClass);
         Class cls = javaClass;
         while(cls.isArray())
         {
            cls = cls.getComponentType();
         }
         typeName = WSDLUtils.getInstance().getJustClassName(cls);
         while(dim-- > 0)
            typeName += ".Array";
      }

      return new QName(ns, typeName);

      /*SchemaCreatorIntf sc = new SchemaTypeCreator();
      sc.setRestrictToTargetNamespace(restrictNS);
      //For basic types, the following algorithm will return the simple types
      JBossXSTypeDefinition xt = sc.generateTypeWithoutRegistration(null, javaClass,ns);
      return new QName(xt.getNamespace(), xt.getName());*/
   }

   /**
    * Get the XML Type for an attachment mime type
    * (will be xsd:hexbinary)
    *
    * @param mimetype
    * @return
    */
   public static QName getXMLTypeForAttachmentType( String mimetype)
   {
      return Constants.TYPE_LITERAL_HEXBINARY;
   }


   /**
    * Returns whether a character is a punctuation
    * @param c
    * @return true:if punctuation, false - otherwise
    */
   public static boolean isPunctuation(char c)
   {
      boolean ret  = true;
      //Idea is if it is not a letter, a digit, a white space, it is a punct
      if(Character.isDigit(c)) ret = false;
      else
         if(Character.isLetter(c)) ret = false;
         else
            if(Character.isISOControl(c))  ret = false;
            else
               if(Character.isWhitespace(c))  ret = false;
      return ret;
   }

   /**
    * Checks whether a class is a legal jaxrpc value type
    * as defined by jaxrpc 1.l specification
    *
    * @param cls
    * @return
    */
   public static boolean isJaxrpcValueType(Class cls)
   {
      boolean result = true;
      if(isJaxrpcRegularType(cls))
         return false;
      boolean defaultCTR = false;

      //Does it define a default constructor?
      Constructor[] cons = cls.getDeclaredConstructors();
      for (int i=0; i<cons.length; i++)
      {
          Class[] paramTypes = cons[i].getParameterTypes();
          if(paramTypes == null || paramTypes.length == 0)
          {
             defaultCTR = true;
             break;
          }
      }
      if(defaultCTR == false)
         return false;

      //Does the class extend Remote?
      if(Remote.class.isAssignableFrom(cls))
         return false;

      //Check the public fields are Jaxprc types
      Field[] fieldArr = cls.getDeclaredFields();
      int length = Array.getLength(fieldArr);
      for(int i = 0 ; i < length ; i++)
      {
         if(!isJaxrpcPermittedType(fieldArr[i].getType()) )
               return false;
      }
      return result;
   }


   /**
    * Basic check whether the passed argument: class
    * contain properties
    *
    * @param cls
    * @return
    */
   public static boolean isJavaBean(Class cls)
   {
      boolean result = false;
      if(isJaxrpcRegularType(cls))
         return false;
      try
      {
         BeanInfo bi = Introspector.getBeanInfo(cls);
         PropertyDescriptor[] pds = bi.getPropertyDescriptors();
         if(pds != null && pds.length > 0)
            result = true;
      }
      catch (java.beans.IntrospectionException e)
      {
      }
      return result;
   }

   public static int getNumberOfParticles(Class javaBean)
   {
      if(isJavaBean(javaBean) == false)
         throw new IllegalArgumentException("Illegal JavaBean argument");

      //Get number of public fields
      Field[] pubFields = javaBean.getFields();
      int pub = pubFields != null ? pubFields.length : 0;

      int props = 0;
      //Get number of properties via bean introspection
      try
      {
         BeanInfo bi = Introspector.getBeanInfo(javaBean);
         PropertyDescriptor[] pds = bi.getPropertyDescriptors();
         props = pds != null ? Array.getLength(pds) : 0;
         int len = 0;
         for(int i = 0; i < props ; i++)
         {
            PropertyDescriptor pd = pds[i];
            Method readMethod = pd.getReadMethod();
            if("getClass".equals(readMethod.getName()))
               continue;
            len++;
         }
         props = len;
      }
      catch (java.beans.IntrospectionException e)
      {
      }

      return pub + props;
   }

   /**
    * Determines whether a Java Class can be unwrapped
    *
    * @param javaType
    * @param isReturnType Does the javaType represent a return type
    *                     of a method?
    * @return
    */
   public static boolean canUnWrap(Class javaType, boolean isReturnType)
   {
      boolean bool = false;

      if(isReturnType)
      {
         bool = ( ToolsUtils.isJavaBean(javaType)
         && ToolsUtils.getNumberOfParticles(javaType) == 1 );
      }
      else
         bool = ToolsUtils.isJaxrpcValueType(javaType);

      return bool;
   }

   /**
    * Checks whether the class type that is passed as argument is
    * a legal Jaxrpc type as defined by jaxrpc 1.1 spec
    *
    * @param type
    * @return
    */
   public static boolean isJaxrpcPermittedType(Class type)
   {
      if(isJaxrpcRegularType(type) == false)
         return isJaxrpcValueType(type);
      else
         return true;
   }

   /**
    * Checks whether the class type that is passed as argument is
    * a legal Jaxrpc type (and not a Value Type) as defined by jaxrpc 1.1 spec
    *
    * @param type
    * @return
    */
   public static boolean isJaxrpcRegularType(Class type)
   {
      boolean result = false;
      //Check if it is a primitive type
      if(JavaUtils.isPrimitive(type))
         return true;
      //Check if it is an array of primitive types
      Class wrapperType = JavaUtils.getWrapperType(type);
      if( wrapperType != null && wrapperType != type )
         return true;
      //Check if it is a wrapper type or array
      Class primType = JavaUtils.getPrimitiveType(type);
      if( primType != null && primType != type)
         return true;
      //Check standard jaxrpc 1.1 classes
      if(String.class == type || Date.class == type || Calendar.class == type ||
           BigInteger.class == type || BigDecimal.class == type || QName.class == type ||
           URI.class == type)
         return true;
      return result;
   }

   public synchronized static void registerJavaType(LiteralTypeMapping typeMapping,
         Class clazz, QName xmlType)
   {
      if(typeMapping.isRegistered(clazz,xmlType) == false )
            typeMapping.register(clazz,xmlType, new SimpleSerializerFactory(), new SimpleDeserializerFactory());
   }

   /**
    * Check all of the characters in the component and for any that are not valid
    * in Java identifiers replace with an underscore.
    */
   public static String convertInvalidCharacters(final String component)
   {
      String result = component;
      for (int i = 0; i < result.length(); i++)
      {
         if (Character.isJavaIdentifierPart(result.charAt(i)) == false)
         {
            result = result.replace(result.charAt(i), '_');
         }
      }

      return result;
   }

   //Private methods
   private static String trimPunctuationMarks( String str)
   {
      if(str  == null)
         throw new IllegalArgumentException("Str is null");
      //Check if the first character is permissible
      if(Character.isJavaIdentifierStart(str.charAt(0)) == false)
          str = str.substring(1);
      else
      //Check if the first character is a punctuation
      if(isPunctuation(str.charAt( 0 )) )
         str = str.substring(1);
      int len = str.length();
      //Check if the last character is a punctuation
      if(isPunctuation(str.charAt( len -1 )) )
          str = str.substring(0,len-1);
      return str;
   }
}
