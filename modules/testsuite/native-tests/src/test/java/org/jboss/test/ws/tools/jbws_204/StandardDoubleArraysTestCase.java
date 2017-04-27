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
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.ws.Constants;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/**
 *  Tests schema generation of arrays of arrays of standard types
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 31, 2005
 */
public class StandardDoubleArraysTestCase extends WSToolsBase
{
   private String WSCOMPILE_SCHEMA = "xmlns:soap11-enc='http://schemas.xmlsoap.org/soap/encoding/' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'  xmlns='http://www.w3.org/2001/XMLSchema'";

   public void testBigDecimalArrayArray() throws Exception
   {
      String ns = "http://org.jboss.ws/marshall/rpclit/types/arrays/java/math";
      String wscompile = "<schema targetNamespace='" +  ns +"'" +
      " xmlns:tns='"+ ns+"'  " +WSCOMPILE_SCHEMA +">" +
      " <complexType name='BigDecimal.Array'> "+
      "<sequence>" +
      "<element name='value' type='decimal' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" +
      "</sequence>"+
      "</complexType>" +
      " <complexType name='BigDecimal.Array.Array'> "+
      "<sequence>" +
      "<element name='value' type='tns:BigDecimal.Array' nillable='true' minOccurs='0' maxOccurs='unbounded'/>" +
      "</sequence>"+
      "</complexType>" +
      "</schema>";

      QName q = new QName(ns,"BigDecimal.Array.Array", Constants.PREFIX_TNS);
      Map packageNamespace = new HashMap();
      packageNamespace.put(BigDecimal.class.getPackage().getName(), ns);
      String xs = generateSchema(q,BigDecimal[][].class, packageNamespace);
      checkSchema(wscompile,xs);
   }

   private void checkSchema(String exp,String actual) throws Exception
   {
      Element expEl = DOMUtils.parse(exp );
      Element actEl = DOMUtils.parse(actual);
      assertEquals(expEl, actEl);
   }
}
