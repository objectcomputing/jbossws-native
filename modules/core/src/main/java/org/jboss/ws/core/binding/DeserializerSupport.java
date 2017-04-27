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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.Deserializer;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jboss.logging.Logger;
import org.jboss.util.NotImplementedException;
import org.jboss.ws.WSException;
import org.jboss.ws.core.soap.SOAPContentElement;
import org.jboss.ws.core.utils.XMLPredefinedEntityReferenceResolver;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Node;

/** The base class for all Deserializers.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 04-Dec-2004
 */
public abstract class DeserializerSupport implements Deserializer
{
   private static final Logger log = Logger.getLogger(DeserializerSupport.class);

   public Object deserialize(SOAPContentElement soapElement, SerializationContext serContext) throws BindingException
   {
      QName xmlName = soapElement.getElementQName();
      QName xmlType = soapElement.getXmlType();

      Source source = soapElement.getXMLFragment().getSource();
      return deserialize(xmlName, xmlType, source, serContext);
   }

   /** Deserialize an XML fragment to an object value
    *
    * @param xmlName The root element name of the resulting fragment
    * @param xmlType The associated schema type
    * @param xmlFragment The XML fragment to deserialize
    * @param serContext The serialization context
    */
   public abstract Object deserialize(QName xmlName, QName xmlType, Source xmlFragment, SerializationContext serContext) throws BindingException;

   // TODO: remove when JBossXB supports unmarshall(Source)
   // http://jira.jboss.org/jira/browse/JBXB-100
   protected static String sourceToString(Source source)
   {
      String xmlFragment = null;
      try
      {
         if (source instanceof DOMSource)
         {
            Node node = ((DOMSource)source).getNode();
            xmlFragment = DOMWriter.printNode(node, false);
         }
         else
         {
            // Note, this code will not handler namespaces correctly that 
            // are defined on a parent of the DOMSource
            //
            // <env:Envelope xmlns:xsd='http://www.w3.org/2001/XMLSchema'>
            //   <env:Body>
            //     <myMethod>
            //       <param xsi:type='xsd:string'>Hello World!</param>
            //     </myMethod>
            //   </env:Body>
            // </env:Envelope>
            //
            TransformerFactory tf = TransformerFactory.newInstance();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            StreamResult streamResult = new StreamResult(baos);
            tf.newTransformer().transform(source, streamResult);
            xmlFragment = new String(baos.toByteArray(),"UTF-8");
            if (xmlFragment.startsWith("<?xml"))
            {
               int index = xmlFragment.indexOf(">");
               xmlFragment = xmlFragment.substring(index + 1);
            }
         }
      }
      catch (TransformerException e)
      {
         WSException.rethrow(e);
      }
      catch (UnsupportedEncodingException e)
      {
         WSException.rethrow(e);
      }

      return xmlFragment;
   }

   /** Unwrap the value string from the XML fragment
    *
    * @return The value string or null if the startTag contains a xsi:nil='true' attribute
    */
   protected String unwrapValueStr(String xmlFragment)
   {
      // We only scan for :nil if the xmlFragment is an empty element
      if (isEmptyElement(xmlFragment))
      {
         return (isNil(xmlFragment) ? null : "");
      }

      int endOfStartTag = xmlFragment.indexOf(">");
      int startOfEndTag = xmlFragment.lastIndexOf("</");
      if (endOfStartTag < 0 || startOfEndTag < 0)
         throw new IllegalArgumentException("Invalid XML fragment: " + xmlFragment);

      String valueStr = xmlFragment.substring(endOfStartTag + 1, startOfEndTag);

      return XMLPredefinedEntityReferenceResolver.resolve(valueStr);
   }

   protected boolean isEmptyElement(String xmlFragment)
   {
      return xmlFragment.startsWith("<") && xmlFragment.endsWith("/>");
   }

   protected boolean isNil(String xmlFragment)
   {
      boolean isNil = false;
      if (isEmptyElement(xmlFragment))
      {
         int endOfStartTag = xmlFragment.indexOf(">");
         String startTag = xmlFragment.substring(0, endOfStartTag);
         isNil = startTag.indexOf(":nil='1'") > 0 || startTag.indexOf(":nil=\"1\"") > 0;
         isNil = isNil || startTag.indexOf(":nil='true'") > 0 || startTag.indexOf(":nil=\"true\"") > 0;
      }
      return isNil;
   }

   public String getMechanismType()
   {
      throw new NotImplementedException();
   }
}
