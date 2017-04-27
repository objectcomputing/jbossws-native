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
package org.jboss.ws.core.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.binding.BindingException;
import org.jboss.ws.core.binding.AbstractDeserializerFactory;
import org.jboss.ws.core.binding.DeserializerSupport;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.core.binding.TypeMappingImpl;
import org.jboss.ws.core.jaxws.SerializationContextJAXWS;
import org.jboss.ws.core.soap.attachment.SwapableMemoryDataSource;
import org.jboss.ws.core.utils.MimeUtils;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.JavaUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents the XML_VALID state of an {@link SOAPContentElement}.<br>
 * Aggregates a {@link XMLFragment}.
 *
 * @author Heiko.Braun@jboss.org
 * @since 05.02.2007
 */
class XMLContent extends SOAPContent
{
   private static final Logger log = Logger.getLogger(XMLContent.class);

   // The well formed XML content of this element.
   private XMLFragment xmlFragment;
   private Document document = DOMUtils.getOwnerDocument();

   protected XMLContent(SOAPContentElement container)
   {
      super(container);
   }

   State getState()
   {
      return State.XML_VALID;
   }

   SOAPContent transitionTo(State nextState)
   {
      SOAPContent next;

      if (nextState == State.XML_VALID)
      {
         next = this;
      }
      else if (nextState == State.OBJECT_VALID)
      {
         Object obj = unmarshallObjectContents();
         SOAPContent objectValid = new ObjectContent(container);
         objectValid.setObjectValue(obj);
         next = objectValid;
      }
      else if (nextState == State.DOM_VALID)
      {
         try
         {
            expandContainerChildren();
         }
         catch (SOAPException ex)
         {
            throw new WSException("Cannot expand container children", ex);
         }
         next = new DOMContent(container);
      }
      else
      {
         throw new IllegalArgumentException("Illegal state requested: " + nextState);
      }

      return next;
   }

   public Source getPayload()
   {
      return xmlFragment.getSource();
   }

   public void setPayload(Source source)
   {
      xmlFragment = new XMLFragment(source);
   }

   public XMLFragment getXMLFragment()
   {
      return xmlFragment;
   }

   public void setXMLFragment(XMLFragment xmlFragment)
   {
      this.xmlFragment = xmlFragment;
   }

   public Object getObjectValue()
   {
      throw new IllegalStateException("Object value not available");
   }

   public void setObjectValue(Object objValue)
   {
      throw new IllegalStateException("Object value not available");
   }

   private Object unmarshallObjectContents()
   {

      Object obj;
      QName xmlType = container.getXmlType();
      Class javaType = container.getJavaType();

      log.debug("getObjectValue [xmlType=" + xmlType + ",javaType=" + javaType + "]");

      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      if (msgContext == null)
         throw new WSException("MessageContext not available");

      SerializationContext serContext = msgContext.getSerializationContext();
      ParameterMetaData pmd = container.getParamMetaData();
      OperationMetaData opMetaData = pmd.getOperationMetaData();
      serContext.setProperty(ParameterMetaData.class.getName(), pmd);
      serContext.setJavaType(javaType);
      List<Class> registeredTypes = opMetaData.getEndpointMetaData().getRegisteredTypes();
      serContext.setProperty(SerializationContextJAXWS.JAXB_CONTEXT_TYPES, registeredTypes.toArray(new Class[0]));

      try
      {
         // Get the deserializer from the type mapping
         TypeMappingImpl typeMapping = serContext.getTypeMapping();
         AbstractDeserializerFactory deserializerFactory = getDeserializerFactory(typeMapping, javaType, xmlType);
         DeserializerSupport des = (DeserializerSupport)deserializerFactory.getDeserializer();

         obj = des.deserialize(container, serContext);
         if (obj != null)
         {
            Class objType = obj.getClass();
            boolean isAssignable = JavaUtils.isAssignableFrom(javaType, objType);
            if (!isAssignable && javaType.isArray())
            {
               try
               {
                  Method toArrayMethod = objType.getMethod("toArray");
                  Class returnType = toArrayMethod.getReturnType();
                  if (JavaUtils.isAssignableFrom(javaType, returnType))
                  {
                     Method getValueMethod = objType.getMethod("getValue");
                     Object value = getValueMethod.invoke(obj);
                     if (value != null)
                     {
                        // Do not invoke toArray if getValue returns null
                        obj = toArrayMethod.invoke(obj);
                     }
                     else
                     {
                        // if the fragment did not indicate a null return
                        // by an xsi:nil we return an empty array
                        Class componentType = javaType.getComponentType();
                        obj = Array.newInstance(componentType, 0);
                     }
                     isAssignable = true;
                  }
               }
               catch (Exception e)
               {
                  // ignore
               }
            }

            if (!isAssignable)
            {
               // handle XOP simple types, i.e. in RPC/LIT
               try
               {
                  String contentType = MimeUtils.resolveMimeType(javaType);
                  log.debug("Adopt DataHandler to " + javaType + ", contentType " + contentType);

                  DataSource ds = new SwapableMemoryDataSource(((DataHandler)obj).getInputStream(), contentType);
                  DataHandler dh = new DataHandler(ds);
                  obj = dh.getContent();

                  // 'application/octet-stream' will return a byte[] instead fo the stream
                  if (obj instanceof InputStream)
                  {
                     ByteArrayOutputStream bout = new ByteArrayOutputStream();
                     dh.writeTo(bout);
                     obj = bout.toByteArray();
                  }
               }
               catch (IOException e)
               {
                  throw new WSException("Failed to adopt XOP content type", e);
               }

               if (!JavaUtils.isAssignableFrom(javaType, obj.getClass()))
               {
                  throw new WSException("Java type '" + javaType + "' is not assignable from: " + objType.getName());
               }
            }
         }
      }
      catch (BindingException e)
      {
         throw new WSException(e);
      }

      log.debug("objectValue: " + (obj != null ? obj.getClass().getName() : null));

      return obj;
   }

   // Get the deserializer factory for a given javaType and xmlType
   private static AbstractDeserializerFactory getDeserializerFactory(TypeMappingImpl typeMapping, Class javaType, QName xmlType)
   {
      AbstractDeserializerFactory deserializerFactory = (AbstractDeserializerFactory)typeMapping.getDeserializer(javaType, xmlType);

      // The type mapping might contain a mapping for the array wrapper bean
      if (deserializerFactory == null && javaType.isArray())
      {
         Class arrayWrapperType = typeMapping.getJavaType(xmlType);
         if (arrayWrapperType != null)
         {
            try
            {
               Method toArrayMethod = arrayWrapperType.getMethod("toArray");
               Class returnType = toArrayMethod.getReturnType();
               if (JavaUtils.isAssignableFrom(javaType, returnType))
               {
                  deserializerFactory = (AbstractDeserializerFactory)typeMapping.getDeserializer(arrayWrapperType, xmlType);
               }
            }
            catch (NoSuchMethodException e)
            {
               // ignore
            }
         }
      }

      if (deserializerFactory == null)
         throw new WSException("Cannot obtain deserializer factory for: [xmlType=" + xmlType + ",javaType=" + javaType + "]");

      return deserializerFactory;
   }

   /**
    * Turn the xml fragment into a DOM repersentation and append
    * all children to the container.
    */
   private void expandContainerChildren() throws SOAPException
   {
      // Do nothing if the source of the XMLFragment is the container itself
      Element domElement = xmlFragment.toElement();
      if (domElement == container)
         return;

      // Make sure the content root element name matches this element name
      QName qname = container.getElementQName();
      QName contentRootName = DOMUtils.getElementQName(domElement);
      boolean artificalElement = (SOAPContentElement.GENERIC_PARAM_NAME.equals(qname) || SOAPContentElement.GENERIC_RETURN_NAME.equals(qname));

      if (!artificalElement && !contentRootName.equals(qname))
         throw new WSException("Content root name does not match element name: " + contentRootName + " != " + qname);

      // Remove all child nodes
      container.removeContents();

      // In case of dispatch and provider we use artifical element names
      // These need to be replaced (costly!)
      if (artificalElement)
      {
         container.setElementQNameInternal(contentRootName);
      }

      Document ownerDoc = container.getOwnerDocument();
      DOMUtils.copyAttributes(container, domElement);

      SOAPFactoryImpl soapFactory = new SOAPFactoryImpl();

      NodeList nlist = domElement.getChildNodes();
      for (int i = 0; i < nlist.getLength(); i++)
      {
         Node child = nlist.item(i);
         short childType = child.getNodeType();
         if (childType == Node.ELEMENT_NODE)
         {

            boolean setOwnerDocument = (DOMUtils.peekOwnerDocument() == null);

            try
            {
               if (setOwnerDocument)
               {                 
                  DOMUtils.setOwnerDocument(document);
               }
               SOAPElement soapElement = soapFactory.createElement((Element)child);
               container.addChildElement(soapElement);
               if (Constants.NAME_XOP_INCLUDE.equals(qname) || container.isXOPParameter())
                  XOPContext.inlineXOPData(soapElement);

            }
            finally
            {
               if (setOwnerDocument)
               {
                  DOMUtils.clearThreadLocals();
               }
            }

         }
         else if (childType == Node.TEXT_NODE)
         {
            String nodeValue = child.getNodeValue();
            container.addTextNode(nodeValue);
         }
         else if (childType == Node.COMMENT_NODE)
         {
            String nodeValue = child.getNodeValue();
            Comment comment = ownerDoc.createComment(nodeValue);
            container.appendChild(comment);
         }
         else if (childType == Node.CDATA_SECTION_NODE)
         {
            String nodeValue = child.getNodeValue();
            container.addTextNode(nodeValue);
         }
         else
         {
            log.trace("Ignore child type: " + childType);
         }
      }
   }
}
