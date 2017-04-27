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
package org.jboss.ws.metadata.wsdl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.ejb.SessionBean;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.rpc.holders.BigDecimalHolder;
import javax.xml.rpc.holders.BigIntegerHolder;
import javax.xml.rpc.holders.BooleanHolder;
import javax.xml.rpc.holders.BooleanWrapperHolder;
import javax.xml.rpc.holders.ByteArrayHolder;
import javax.xml.rpc.holders.ByteHolder;
import javax.xml.rpc.holders.ByteWrapperHolder;
import javax.xml.rpc.holders.CalendarHolder;
import javax.xml.rpc.holders.DoubleHolder;
import javax.xml.rpc.holders.DoubleWrapperHolder;
import javax.xml.rpc.holders.FloatHolder;
import javax.xml.rpc.holders.FloatWrapperHolder;
import javax.xml.rpc.holders.Holder;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.IntegerWrapperHolder;
import javax.xml.rpc.holders.LongHolder;
import javax.xml.rpc.holders.LongWrapperHolder;
import javax.xml.rpc.holders.ObjectHolder;
import javax.xml.rpc.holders.QNameHolder;
import javax.xml.rpc.holders.ShortHolder;
import javax.xml.rpc.holders.ShortWrapperHolder;
import javax.xml.rpc.holders.StringHolder;

import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.jaxrpc.ParameterWrapping;
import org.jboss.ws.core.utils.HolderUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.wsf.common.JavaUtils;
import org.jboss.xb.binding.Util;

/**
 * Singleton utils class used for Java2WSDL and WSDL2Java operations
 *
 * @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 * @since Nov 3, 2004
 */

public class WSDLUtils
{
   private String newline = "\n";
   private static WSDLUtils instance = new WSDLUtils();

   private List wrapperlist = null;
   private List primlist = null;

   private final Map primitiveMap = new HashMap();

   private final static Map<String, Class> schemaBasicTypes = new HashMap<String, Class>();

   private final static Map<Class, Class> holderTypes = new HashMap<Class, Class>();
   private final static Map<Class, Class> reverseHolderTypes = new HashMap<Class, Class>();

   // Skip these methods when generating wsdl operations
   private List<String> ignoredMethods;

   static
   {
      schemaBasicTypes.put("anyURI", java.net.URI.class);
      schemaBasicTypes.put("boolean", Boolean.TYPE);
      schemaBasicTypes.put("byte", Byte.TYPE);
      schemaBasicTypes.put("decimal", java.math.BigDecimal.class);
      schemaBasicTypes.put("double", Double.TYPE);
      schemaBasicTypes.put("dateTime", Calendar.class);
      schemaBasicTypes.put("float", Float.TYPE);
      schemaBasicTypes.put("int", Integer.TYPE);
      schemaBasicTypes.put("integer", BigInteger.class);
      schemaBasicTypes.put("long", Long.TYPE);
      schemaBasicTypes.put("QName", QName.class);
      schemaBasicTypes.put("short", Short.TYPE);
      schemaBasicTypes.put("String", String.class);
   }

   static
   {
      holderTypes.put(BigDecimal.class, BigDecimalHolder.class);
      holderTypes.put(BigInteger.class, BigIntegerHolder.class);
      holderTypes.put(boolean.class, BooleanHolder.class);
      holderTypes.put(Boolean.class, BooleanWrapperHolder.class);
      holderTypes.put(byte.class, ByteHolder.class);
      holderTypes.put(Byte.class, ByteWrapperHolder.class);
      holderTypes.put(Byte[].class, ByteArrayHolder.class);
      holderTypes.put(Calendar.class, CalendarHolder.class);
      holderTypes.put(double.class, DoubleHolder.class);
      holderTypes.put(Double.class, DoubleWrapperHolder.class);
      holderTypes.put(float.class, FloatHolder.class);
      holderTypes.put(Float.class, FloatWrapperHolder.class);
      holderTypes.put(int.class, IntHolder.class);
      holderTypes.put(Integer.class, IntegerWrapperHolder.class);
      holderTypes.put(long.class, LongHolder.class);
      holderTypes.put(Long.class, LongWrapperHolder.class);
      holderTypes.put(Object.class, ObjectHolder.class);
      holderTypes.put(QName.class, QNameHolder.class);
      holderTypes.put(short.class, ShortHolder.class);
      holderTypes.put(Short.class, ShortWrapperHolder.class);
      holderTypes.put(String.class, StringHolder.class);
   }

   static
   {
      reverseHolderTypes.put(BigDecimalHolder.class, BigDecimal.class);
      reverseHolderTypes.put(BigIntegerHolder.class, BigInteger.class);
      reverseHolderTypes.put(BooleanHolder.class, boolean.class);
      reverseHolderTypes.put(BooleanWrapperHolder.class, Boolean.class);
      reverseHolderTypes.put(ByteArrayHolder.class, Byte[].class);
      reverseHolderTypes.put(ByteHolder.class, byte.class);
      reverseHolderTypes.put(ByteWrapperHolder.class, Byte.class);
      reverseHolderTypes.put(CalendarHolder.class, Calendar.class);
      reverseHolderTypes.put(DoubleHolder.class, double.class);
      reverseHolderTypes.put(DoubleWrapperHolder.class, Double.class);
      reverseHolderTypes.put(FloatHolder.class, float.class);
      reverseHolderTypes.put(FloatWrapperHolder.class, Float.class);
      reverseHolderTypes.put(IntHolder.class, int.class);
      reverseHolderTypes.put(IntegerWrapperHolder.class, Integer.class);
      reverseHolderTypes.put(LongHolder.class, long.class);
      reverseHolderTypes.put(LongWrapperHolder.class, Long.class);
      reverseHolderTypes.put(ObjectHolder.class, Object.class);
      reverseHolderTypes.put(QNameHolder.class, QName.class);
      reverseHolderTypes.put(ShortHolder.class, short.class);
      reverseHolderTypes.put(ShortWrapperHolder.class, Short.class);
      reverseHolderTypes.put(StringHolder.class, String.class);
   }

   /**
    * Singleton - Only way to get an instance
    * @return
    */
   public static WSDLUtils getInstance()
   {
      return instance;
   }

   private WSDLUtils()
   {
      wrapperlist = new ArrayList();
      primlist = new ArrayList();
      populatePrimList();
      populateWrapperList();
      createPrimitiveMap();
   }

   /**
    * Check if it is of Primitive Type
    * @param str
    * @return
    */
   public boolean isPrimitive(String str)
   {
      return primlist.contains(str);
   }

   /**
    * Check if it is of Wrapper Type
    * @param str
    * @return
    */
   public boolean isWrapper(String str)
   {
      return wrapperlist.contains(str);
   }

   /**
    * For the given complex type, check if its base type is the regular xsd type (xsd:anyType)
    * which can be ignored
    * @param baseType
    * @param t ComplexTypeDefinition
    * @return true:ignorable, false-otherwise(user defined base type)
    */
   public boolean isBaseTypeIgnorable(XSTypeDefinition baseType, XSComplexTypeDefinition t)
   {
      boolean bool = false;

      //Check baseType is xsd:anyType
      if (baseType != null)
      {
         if (baseType.getNamespace() == Constants.NS_SCHEMA_XSD && baseType.getName().equals("anyType"))
            bool = true; //Ignore this baseType
      }
      if (XSComplexTypeDefinition.CONTENTTYPE_SIMPLE == t.getContentType())
      {
         bool = true; //ComplexType has a simplecontent
      }

      return bool;

   }

   /**
    * Check if the class is a Java standard class
    * @param cls
    * @return true if class belongs to java.* or javax.* package
    */
   public boolean checkIgnoreClass(Class cls)
   {
      if (cls == null)
         throw new IllegalArgumentException("Illegal null argument:cls");
      //if (cls.isArray()) cls = cls.getComponentType();
      if (!cls.isArray())
      {
         String pkgname = cls.getPackage() != null ? cls.getPackage().getName() : null;
         if (pkgname != null && pkgname.startsWith("java"))
            return true;

         if (ParameterWrapping.WrapperType.class.isAssignableFrom(cls))
            return true;

      }
      return false;
   }

   /** Check if this method should be ignored
    */
   public boolean checkIgnoreMethod(Method method)
   {
      String methodname = method.getName();
      if (ignoredMethods == null)
      {
         ignoredMethods = new ArrayList<String>();
         Method[] objMethods = Object.class.getMethods();
         for (int i = 0; i < objMethods.length; i++)
         {
            ignoredMethods.add(objMethods[i].getName());
         }
         //Add the SessionBean Methods to the ignore list
         Method[] sbMethods = SessionBean.class.getMethods();
         for (int i = 0; i < sbMethods.length; i++)
         {
            ignoredMethods.add(sbMethods[i].getName());
         }
      }

      boolean ignoreMethod = ignoredMethods.contains(methodname);

      // FIXME: This code is a duplicate, it should read from the UMDM
      if (method.getDeclaringClass().isAnnotationPresent(WebService.class) && method.isAnnotationPresent(WebMethod.class) == false)
         ignoreMethod = true;

      return ignoreMethod;
   }

   /**
    * Chop "PortType" at the end of the String
    * @param name
    * @return
    */
   public String chopPortType(String name)
   {
      int index = name.lastIndexOf("PortType");
      if (index > 0)
         return name.substring(0, index);

      return name;
   }

   /**
    * Chop chopstr at the end of the String
    * @param name
    * @param chopstr the string that is the key Eg: Fault
    * @return
    */
   public String chop(String name, String chopstr)
   {
      int index = name.lastIndexOf(chopstr);
      if (index > 0)
         return name.substring(0, index);

      return name;
   }

   /**
    * Given a packageName, start creating the package structure
    * @param packageName
    */
   public File createPackage(String path, String packageName)
   {
      if (packageName == null)
         throw new IllegalArgumentException("Illegal Null Argument: packageName");
      if (path == null)
         throw new IllegalArgumentException("Illegal Null Argument: path");
      String pac = packageName.replace('.', '/');
      File dir = new File(path + "/" + pac);
      dir.mkdirs();
      return dir;
   }

   /**
    * Create a file on the disk
    * @param loc Location where the file has to be created
    * @param fname  File Name to which '.java' will be appended
    * @return
    * @throws IOException  Problem creating the file
    */
   public File createPhysicalFile(File loc, String fname) throws IOException
   {
      if (loc == null)
         throw new IllegalArgumentException("Illegal Null Argument: loc");
      if (fname == null)
         throw new IllegalArgumentException("Illegal Null Argument: fname");
      File javaFile = new File(loc.getAbsolutePath() + "/" + fname + ".java");
      //Delete the javaFile if already exists
      if (javaFile.exists())
         javaFile.delete();
      boolean boolCreate = javaFile.createNewFile();
      if (!boolCreate)
         throw new WSException(fname + ".java cannot be created");
      return javaFile;
   }

   /**
    * Create the basic template of a class
    * @param pkgname Package Name
    * @param fname File Name to which '.java' will be appended
    * @param type  XSDType to obtain base class info (if Complex Type)
    * @param importList  Strings representing imports
    * @return
    */
   public StringBuilder createClassBasicStructure(String pkgname, String fname, XSTypeDefinition type, List importList, String baseName)
   {
      StringBuilder buf = new StringBuilder();
      writeJbossHeader(buf);
      buf.append(newline);
      buf.append("package " + pkgname + ";");
      buf.append(newline);
      buf.append(newline);
      if (importList != null)
      {
         Iterator iter = importList.iterator();
         while (iter.hasNext())
         {
            buf.append("import " + (String)iter.next() + ";");
            buf.append(newline);
         }
      }
      buf.append(newline);
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

      buf.append("public class  " + fname);
      if (baseName == null && baseType != null)
         baseName = baseType.getName();
      if (baseName != null)
         buf.append(" extends " + baseName);
      buf.append(newline);
      buf.append("{");
      buf.append(newline);

      return buf;
   }

   /**
    * Checks whether there exists a public field with the given name
    * @param javaType  Class  Object
    * @param name  Field name to check
    * @return true - if public field exists, false-otherwise
    */
   public boolean doesPublicFieldExist(Class javaType, String name)
   {
      Field fld = null;
      try
      {
         fld = javaType.getField(name);
      }
      catch (SecurityException e)
      {
         throw e;
      }
      catch (NoSuchFieldException e)
      {
         return false;
      }
      int mod = fld.getModifiers();
      if ((mod == Modifier.PUBLIC))
         return true;
      return false;
   }

   /**
    * Ensure that the first alphabet is uppercase
    * @param fname
    * @return
    */
   public String firstLetterUpperCase(String fname)
   {
      if (fname == null || fname.length() == 0)
         throw new WSException("String passed is null");
      //Ensure that the first character is uppercase
      final char firstChar = fname.charAt(0);
      if (Character.isLowerCase(firstChar))
      {
         final StringBuilder sb = new StringBuilder(fname);
         sb.setCharAt(0, Character.toUpperCase(firstChar));
         fname = sb.toString();
      }
      return fname;
   }

   /**
    * Get the dimension of an array
    * @param arr
    * @return dimension of an array
    */
   public int getArrayDimension(Class arr)
   {
      if (arr == null || arr.isArray() == false)
         throw new IllegalArgumentException("Illegal null or array arg:arr");
      int counter = 0;
      while (arr.isArray())
      {
         counter += 1;
         arr = arr.getComponentType();
      }
      return counter;
   }

   /**
    * Return the Jaxrpc holder that
    * represents the class
    * @param cls
    * @return Jaxrpc Holder object if exists
    */
   public Class getHolder(Class cls)
   {
      return holderTypes.get(cls);
   }

   /**
    * Return the Java class that is represented by
    * the Jaxrpc holder
    * @param cls The jaxrpc holder type
    * @return Jaxrpc Holder object if exists
    */
   public Class getJavaTypeForHolder(Class cls)
   {
      if (Holder.class.isAssignableFrom(cls))
         return HolderUtils.getValueType(cls);
      else return cls;
      //return reverseHolderTypes.get(cls);
   }

   /**
    * An input of "HelloObjArray"  is converted into arrayOfHelloObj
    * Applied in the input part for WSDL 1.1 Messages
    * @param arrayStr
    * @return
    */

   public String getMessagePartForArray(Class javaType)
   {
      StringBuilder sb = new StringBuilder();
      while (javaType.isArray())
      {
         sb.append("arrayOf");
         javaType = javaType.getComponentType();
      }

      sb.append(getJustClassName(javaType));
      return sb.toString();
   }

   /**
    * Given a class, strip out the package name
    *
    * @param cls
    * @return just the classname
    */

   public static String getJustClassName(Class cls)
   {
      if (cls == null)
         return null;
      if (cls.isArray())
      {
         Class c = cls.getComponentType();
         return getJustClassName(c.getName());
      }

      return getJustClassName(cls.getName());
   }

   /**
    * Given a FQN of a class, strip out the package name
    *
    * @param classname
    * @return just the classname
    */
   public static String getJustClassName(String classname)
   {
      int index = classname.lastIndexOf('.');
      if (index < 0)
         index = 0;
      else index = index + 1;
      return classname.substring(index);
   }

   /**
    * From the list of fields defined by this class (not superclasses)
    * get the fields that are relevant (public)
    */
   public Field[] getPublicFields(Class cls)
   {
      ArrayList list = new ArrayList();

      Field[] fld = cls.getDeclaredFields();
      for (int i = 0; i < fld.length; i++)
      {
         Field field = fld[i];
         int mod = field.getModifiers();
         if ((mod == Modifier.PUBLIC))
            list.add(field);
      }//end for

      Field[] retarr = new Field[list.size()];
      list.toArray(retarr);

      return retarr;
   }

   /**
    * From the list of fields defined by this class (not superclasses)
    * get the fields that are relevant (public/protected)
    *
    * @param methods
    * @return
    */
   public Method[] getPublicProtectedMethods(Method[] methods)
   {
      ArrayList list = new ArrayList();
      int len = methods.length;

      for (int i = 0; i < len; i++)
      {
         Method method = methods[i];
         int mod = method.getModifiers();
         if ((mod == Modifier.PUBLIC || mod == Modifier.PROTECTED))
            list.add(method);
      }//end for

      Method[] retarr = new Method[list.size()];
      list.toArray(retarr);
      return retarr;
   }

   /**
    * Given the XMLType, we will check if it is of basic schema
    * types that are mapped to Java primitives and wrappers
    * Jaxrpc 1.1 Section 5.3
    * @param xmlType
    * @return
    */
   public Class getJavaType(QName xmlType)
   {
      if (xmlType == null)
         return null;
      String localPart = xmlType.getLocalPart();
      return (Class)schemaBasicTypes.get(localPart);
   }

   /**
    * Change the first character to uppercase
    * @param str
    * @return
    */
   public String getMixedCase(String str)
   {
      if (str == null || str.length() == 0)
         throw new IllegalArgumentException("String passed to WSDLUtils.getMixedCase is null");

      if (str.length() == 1)
         return str.toUpperCase();
      char[] charr = str.toCharArray();
      charr[0] = Character.toUpperCase(charr[0]);
      return new String(charr);
   }

   /**
    * Given a QName, provide a string that is prefix:localpart
    * @param qn
    * @return formatted string
    */
   public String getFormattedString(QName qn)
   {
      if (qn == null)
         throw new IllegalArgumentException(" QName passed is null");
      StringBuilder sb = new StringBuilder();
      String prefix = qn.getPrefix();
      String localpart = qn.getLocalPart();
      if (prefix == null || prefix.length() == 0)
         prefix = Constants.PREFIX_TNS;
      sb.append(prefix).append(':');
      sb.append(localpart);

      return sb.toString();
   }

   /**
    * Return a QName given a formatted string
    * @param formattedStr string that is prefix:localpart
    * @return QName
    */
   public QName getQName(String formattedStr)
   {
      QName qn = null;
      int ind = formattedStr.lastIndexOf(':');
      if (ind < 0)
         throw new IllegalArgumentException("Formatted String is not of format prefix:localpart");
      String prefix = formattedStr.substring(0, ind);
      String nsuri = null;
      if (Constants.PREFIX_XSD.equals(prefix))
         nsuri = Constants.NS_SCHEMA_XSD;
      if (nsuri == null)
         qn = new QName(formattedStr.substring(ind + 1));
      else qn = new QName(nsuri, formattedStr.substring(ind + 1), prefix);
      return qn;
   }

   /**
    * Return the primitive for a wrapper equivalent (Integer -> int)
    * @param str
    * @return
    */
   public String getPrimitive(String str)
   {
      return (String)primitiveMap.get(str);
   }

   /**
    * Extracts the package name from the typeNS
    * @param typeNS
    * @return
    */
   public String getPackageName(String typeNS)
   {
      String pkgname = Util.xmlNamespaceToJavaPackage(typeNS);
      return pkgname;
   }

   public static String getTypeNamespace(Class javaType)
   {
      return getTypeNamespace(JavaUtils.getPackageName(javaType));
   }

   /**
    * Extracts the typeNS given the package name
    * Algorithm is based on the one specified in JAWS v2.0 spec
    */
   public static String getTypeNamespace(String packageName)
   {
      StringBuilder sb = new StringBuilder("http://");

      //Generate tokens with '.' as delimiter
      StringTokenizer st = new StringTokenizer(packageName, ".");

      //Have a LIFO queue for the tokens
      Stack<String> stk = new Stack<String>();
      while (st != null && st.hasMoreTokens())
      {
         stk.push(st.nextToken());
      }

      String next;
      while (!stk.isEmpty() && (next = stk.pop()) != null)
      {
         if (sb.toString().equals("http://") == false)
            sb.append('.');
         sb.append(next);
      }

      // trailing slash
      sb.append('/');

      return sb.toString();
   }

   /**
    * Given WSDLDefinitions, detect the wsdl style
    *
    * @param wsdl
    * @return Constants.RPC_LITERAL or Constants.DOCUMENT_LITERAL
    */
   public String getWSDLStyle(WSDLDefinitions wsdl)
   {
      WSDLInterface wi = wsdl.getInterfaces()[0];
      WSDLInterfaceOperation wio = wi.getOperations()[0];
      String style = wio.getStyle();
      if (style == null || style.equals(Constants.URI_STYLE_RPC) || "rpc".equalsIgnoreCase(style))
         return Constants.RPC_LITERAL;
      else return Constants.DOCUMENT_LITERAL;
   }

   public static JBossXSModel getSchemaModel(WSDLTypes types)
   {
      if (types instanceof XSModelTypes)
         return ((XSModelTypes)types).getSchemaModel();

      throw new WSException("WSDLTypes is not an XSModelTypes");
   }

   public static void addSchemaModel(WSDLTypes types, String namespace, JBossXSModel model)
   {
      if (!(types instanceof XSModelTypes))
         throw new WSException("WSDLTypes is not an XSModelTypes");

      XSModelTypes modelTypes = (XSModelTypes)types;
      modelTypes.addSchemaModel(namespace, model);
   }

   /**
    * Checks whether the class is a standard jaxrpc holder
    *
    * @param cls a Class object
    * @return true: A Standard jaxrpc holder
    */
   public boolean isStandardHolder(Class cls)
   {
      if (Holder.class.isAssignableFrom(cls) == false)
         return false; //Not even a holder
      //It is a holder.  Is it a standard holder?
      if (cls.getPackage().getName().startsWith("javax.xml.rpc"))
         return true;
      return false;
   }

   /**
    * Write the JBoss License Header at the top of generated class source files
    * @param buf
    */
   public void writeJbossHeader(StringBuilder buf)
   {
      buf.append("/*").append(newline);
      buf.append(" * JBossWS WS-Tools Generated Source").append(newline);
      buf.append(" *").append(newline);
      buf.append(" * Generation Date: " + new Date() + newline);
      buf.append(" *").append(newline);
      buf.append(" * This generated source code represents a derivative work of the input to").append(newline);
      buf.append(" * the generator that produced it. Consult the input for the copyright and").append(newline);
      buf.append(" * terms of use that apply to this source code.").append(newline);
      buf.append(" */").append(newline);
   }

   protected void populatePrimList()
   {
      primlist.add("int");
      primlist.add("boolean");
      primlist.add("short");
      primlist.add("byte");
      primlist.add("long");
      primlist.add("float");
      primlist.add("double");
   }

   protected void populateWrapperList()
   {
      wrapperlist.add("java.lang.Integer");
      wrapperlist.add("java.lang.Boolean");
      wrapperlist.add("java.lang.Short");
      wrapperlist.add("java.lang.Byte");
      wrapperlist.add("java.lang.Long");
      wrapperlist.add("java.lang.Float");
      wrapperlist.add("java.lang.Double");
      wrapperlist.add("java.lang.String");

      wrapperlist.add("java.math.BigInteger");
      wrapperlist.add("java.math.BigDecimal");
      wrapperlist.add("java.util.Calendar");
      wrapperlist.add("javax.xml.namespace.QName");
   }

   private void createPrimitiveMap()
   {
      primitiveMap.put("Integer", "int");
      primitiveMap.put("Float", "float");
      primitiveMap.put("Long", "long");
      primitiveMap.put("Double", "double");
      primitiveMap.put("Short", "short");
      primitiveMap.put("Boolean", "boolean");
      primitiveMap.put("Byte", "byte");
      primitiveMap.put("java.lang.Integer", "int");
      primitiveMap.put("java.lang.Float", "float");
      primitiveMap.put("java.lang.Long", "long");
      primitiveMap.put("java.lang.Double", "double");
      primitiveMap.put("java.lang.Short", "short");
      primitiveMap.put("java.lang.Boolean", "boolean");
      primitiveMap.put("java.lang.Byte", "byte");
   }

   public static WSDLInterfaceOperationOutput getWsdl11Output(WSDLInterfaceOperation operation)
   {
      WSDLInterfaceOperationOutput[] outputs = operation.getOutputs();
      if (outputs == null)
         return null;

      switch (outputs.length)
      {
         case 0:
            return null;
         case 1:
            return outputs[0];
      }

      throw new WSException("Only Request-Only and Request-Response MEPs are allowed");
   }

   public static WSDLInterfaceOperationInput getWsdl11Input(WSDLInterfaceOperation operation)
   {
      WSDLInterfaceOperationInput[] inputs = operation.getInputs();
      if (inputs == null)
         return null;

      switch (inputs.length)
      {
         case 0:
            return null;
         case 1:
            return inputs[0];
      }

      throw new WSException("Only Request-Only and Request-Response MEPs are allowed");
   }
}
