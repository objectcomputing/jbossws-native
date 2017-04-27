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
package org.jboss.test.ws.tools.jbws_204;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;

import javax.xml.namespace.QName;

import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.ws.Constants;
import org.jboss.ws.tools.JavaToXSD;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/**
 * Test generation of XML Schema for arrays of standard types
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jul 19, 2005
 */

public class StandardArraysTestCase extends WSToolsBase
{
   private  JavaToXSD jxsd = null;

   private String WSCOMPILE_SCHEMA = "xmlns:soap11-enc='http://schemas.xmlsoap.org/soap/encoding/' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'  xmlns='http://www.w3.org/2001/XMLSchema'";

   public void testBigDecimalArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/java/math";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='BigDecimalArray'> "+
           "<sequence>" +
          "<element name='value' type='decimal' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"BigDecimalArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,BigDecimal[].class);
      checkSchema(wscompile,xs);
   }

   public void testBigIntegerArray()  throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/java/math";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='BigIntegerArray'> "+
           "<sequence>" +
          "<element name='value' type='integer' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"BigIntegerArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,BigInteger[].class);
      checkSchema(wscompile,xs);
   }

   public void  testBooleanArray()  throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='booleanArray'> "+
           "<sequence>" +
          "<element name='value' type='boolean' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"booleanArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,boolean[].class);
      checkSchema(wscompile,xs);
   }

   public void testBooleanWrapperArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/java/lang";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='BooleanArray'> "+
           "<sequence>" +
          "<element name='value' type='boolean' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"BooleanArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,Boolean[].class);
      checkSchema(wscompile,xs);
   }

   public void testByteArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='byteArray'> "+
           "<sequence>" +
          "<element name='value' type='byte' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"byteArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,byte[].class);
      checkSchema(wscompile,xs);
   }

   public void testByteWrapperArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='ByteArray'> "+
           "<sequence>" +
          "<element name='value' type='byte' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"ByteArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,Byte[].class);
      checkSchema(wscompile,xs);
   }

   public void testqnameArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/javax/xml/namespace";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='QNameArray'> "+
           "<sequence>" +
          "<element name='value' type='QName' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"QNameArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,QName[].class);
      checkSchema(wscompile,xs);
   }

   public void testcalendarArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/java/util";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='CalendarArray'> "+
           "<sequence>" +
          "<element name='value' type='dateTime' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"CalendarArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,Calendar[].class);
      checkSchema(wscompile,xs);
   }

   public void testdoubleArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='doubleArray'> "+
           "<sequence>" +
          "<element name='value' type='double' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"doubleArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,double[].class);
      checkSchema(wscompile,xs);
   }

   public void testdoubleWrapperArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/java/lang";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='DoubleArray'> "+
           "<sequence>" +
          "<element name='value' type='double' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"DoubleArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,Double[].class);
      checkSchema(wscompile,xs);
   }

   public void testfloatArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='floatArray'> "+
           "<sequence>" +
          "<element name='value' type='float' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"floatArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,float[].class);
      checkSchema(wscompile,xs);
   }

   public void testfloatWrapperArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/java/lang";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='FloatArray'> "+
           "<sequence>" +
          "<element name='value' type='float' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"FloatArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,Float[].class);
      checkSchema(wscompile,xs);
   }

   public void testintArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='intArray'> "+
           "<sequence>" +
          "<element name='value' type='int' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"intArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,int[].class);
      checkSchema(wscompile,xs);
   }

   public void testintWrapperArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/java/lang";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='IntegerArray'> "+
           "<sequence>" +
          "<element name='value' type='int' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"IntegerArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,Integer[].class);
      checkSchema(wscompile,xs);
   }

   public void testlongArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='longArray'> "+
           "<sequence>" +
          "<element name='value' type='long' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"longArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,long[].class);
      checkSchema(wscompile,xs);
   }

   public void testlongWrapperArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/java/lang";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='LongArray'> "+
           "<sequence>" +
          "<element name='value' type='long' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"LongArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,Long[].class);
      checkSchema(wscompile,xs);
   }

   public void testshortArray()  throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='shortArray'> "+
           "<sequence>" +
          "<element name='value' type='short' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"shortArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,short[].class);
      checkSchema(wscompile,xs);

   }

   public void testshortWrapperArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/java/lang";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='ShortArray'> "+
           "<sequence>" +
          "<element name='value' type='short' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"ShortArray", Constants.PREFIX_TNS);
      String xs = generateSchema(q,Short[].class);
      checkSchema(wscompile,xs);
   }

   public void teststringArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/java/lang";
      setUpFeatures( ns);
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
            " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
           " <complexType name='StringArray'> "+
           "<sequence>" +
          "<element name='value' type='string' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" +
          "</sequence>"+
          "</complexType></schema>";

      QName q = new QName(ns,"StringArray", Constants.PREFIX_TNS);
      String xs =generateSchema(q,String[].class);
      checkSchema(wscompile,xs);
   }

   private void checkSchema(String exp,String actual) throws Exception
   {
      Element expEl = DOMUtils.parse(exp );
      Element actEl = DOMUtils.parse(actual);
      assertEquals(expEl, actEl);
   }

   private void setUpFeatures(String ns)
   {
      jxsd = new JavaToXSD();
   }

}

