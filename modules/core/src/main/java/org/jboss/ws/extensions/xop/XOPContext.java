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
package org.jboss.ws.extensions.xop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.MessageAbstraction;
import org.jboss.ws.core.StubExt;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.core.soap.NameImpl;
import org.jboss.ws.core.soap.SOAPElementImpl;
import org.jboss.ws.core.soap.SOAPMessageImpl;
import org.jboss.ws.core.soap.attachment.MimeConstants;
import org.jboss.ws.core.utils.MimeUtils;
import org.jboss.ws.extensions.xop.jaxrpc.XOPMarshallerImpl;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.JavaUtils;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.sunday.xop.XOPMarshaller;
import org.jboss.xb.binding.sunday.xop.XOPObject;

/**
 * XOP context associated with a message context.
 * Acts as a facade to the current soap message and supports the various XOP transitions.<p>
 * A good starting point to understand how MTOM in JBossWS works is to take a
 * look at the SOAPContentElement implementation.
 * 
 * @see org.jboss.ws.core.soap.SOAPContentElement#handleMTOMTransitions() 
 * @see org.jboss.ws.extensions.xop.jaxrpc.XOPUnmarshallerImpl
 * @see XOPMarshallerImpl
 *
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since May 10, 2006
 */
public class XOPContext
{
   // provide logging
   private static final Logger log = Logger.getLogger(XOPContext.class);

   private static final String NS_XOP_JBOSSWS = "http://org.jboss.ws/xop";
   
   private static final String NS_XOP_JBOSSWS_CONTENT_TYPE = NS_XOP_JBOSSWS + ":content-type";

   /**
    * Check if the current soap message flagged as a XOP package.
    * This may differ from the wire format when jaxrpc handlers are in place.
    */
   public static boolean isXOPMessage()
   {
      boolean isXOP = false;
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      if (msgContext != null)
      {
         SOAPMessageImpl soapMessage = (SOAPMessageImpl)msgContext.getSOAPMessage();
         isXOP = (soapMessage != null && soapMessage.isXOPMessage());
      }
      return isXOP;
   }

   public static boolean isSWARefMessage()
   {
      boolean isSWARef = false;
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      if (msgContext != null)
      {
         SOAPMessageImpl soapMessage = (SOAPMessageImpl)msgContext.getSOAPMessage();
         isSWARef = (soapMessage != null && soapMessage.isSWARefMessage());
      }
      return isSWARef;
   }

   /**
    * Check if the wire format is actually a xop encoded multipart message
    */
   public static boolean isXOPEncodedRequest()
   {
      boolean isMultippartXOP = false;
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      if (msgContext != null)
      {
         MessageAbstraction message = msgContext.getMessageAbstraction();
         String[] contentType = message.getMimeHeaders().getHeader("content-type");
         if (contentType != null)
         {
            for (String value : contentType)
            {
               if (value.indexOf(MimeConstants.TYPE_APPLICATION_XOP_XML) != -1)
               {
                  isMultippartXOP = true;
                  break;
               }
            }
         }
      }

      return isMultippartXOP;
   }

   /**
    * Check if MTOM is enabled.<br>
    * 
    * Even though the client API to enable/disable MTOM is different
    * between JAX-WS and JAXRPC, both do will cases will propagate a message context property. 
    * (<code>org.jboss.ws.mtom.enabled</code>)<br>
    */
   public static boolean isMTOMEnabled()
   {
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      Boolean mtomEnabled = (Boolean)msgContext.get(StubExt.PROPERTY_MTOM_ENABLED);
      return Boolean.TRUE.equals(mtomEnabled);
   }

   public static void setMTOMEnabled(boolean b)
   {
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      msgContext.put(StubExt.PROPERTY_MTOM_ENABLED, Boolean.valueOf(b));
   }

   /**
    * Replace all <code>xop:Include</code> elements with it's base64 representation.
    * This happens when the associated SOAPContentElement transitions to state dom-valid.<br>
    * All attachement parts will be removed.
    */
   public static void inlineXOPData(SOAPElement xopElement)
   {
      String ns = xopElement.getNamespaceURI() != null ? xopElement.getNamespaceURI() : "";
      String localName = xopElement.getLocalName();

      // rpc/lit
      if (ns.equals(Constants.NS_XOP) && localName.equals("Include"))
      {
         replaceXOPInclude(xopElement.getParentElement(), xopElement);
      }
      else
      {
         // doc/lit
         Iterator it = DOMUtils.getChildElements(xopElement);
         while (it.hasNext())
         {
            SOAPElement childElement = (SOAPElement)it.next();
            String childNS = childElement.getNamespaceURI() != null ? childElement.getNamespaceURI() : "";
            String childName = childElement.getLocalName();
            if (childNS.equals(Constants.NS_XOP) && childName.equals("Include"))
            {
               replaceXOPInclude(xopElement, childElement);
            }
            else
            {
               inlineXOPData(childElement);
            }
         }
      }
   }

   /**
    * When handlers jump in, the SOAPMessage flag that indicates
    * a xop encoded message (derived from wire format) becomes stale.
    *
    * @param isXOPMessage
    */
   private static void setXOPMessage(boolean isXOPMessage)
   {
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      SOAPMessageImpl soapMsg = (SOAPMessageImpl)msgContext.getSOAPMessage();
      soapMsg.setXOPMessage(isXOPMessage);
   }

   /**
    * The XOP attachments need to be created before the actual message is written
    * to an output stream. This is necessary because it changes the overall message content-type.
    * If we would do this lazily (i.e. upon remoting callback) the previous content-type
    * would already have been written.
    *
    * @see org.jboss.ws.core.soap.SOAPConnectionImpl#callInternal(javax.xml.soap.SOAPMessage, Object, boolean)
    * @see org.jboss.ws.core.soap.SOAPMessageMarshaller#write(Object, java.io.OutputStream)
    */
   public static void eagerlyCreateAttachments()
   {
      if (!isXOPMessage() && !isSWARefMessage())
         return;

      try
      {
         CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
         SOAPMessage soapMessage = msgContext != null ? msgContext.getSOAPMessage() : null;
         SOAPBody body = soapMessage != null ? soapMessage.getSOAPBody() : null;

         if (body != null)
         {
            CreateAttachmentVisitor visitor = new CreateAttachmentVisitor();
            visitor.visitXOPElements((SOAPElementImpl)body);
         }
      }
      catch (SOAPException e)
      {
         throw new WSException("Failed to eagerly create XOP attachments", e);
      }
   }

   /**
    * Visit the soap object model elements and restore xop data.
    */
   public static void visitAndRestoreXOPData()
   {
      try
      {
         if (!isXOPMessage() && isMTOMEnabled())
         {
            CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
            SOAPBody body = msgContext.getSOAPMessage().getSOAPBody();
            RestoreXOPElementVisitor visitor = new RestoreXOPElementVisitor();
            visitor.visitXOPElements((SOAPElementImpl)body);
         }
      }
      catch (SOAPException e)
      {
         throw new WSException("Failed to restore XOP data", e);
      }
   }

   /**
    * Restore previously inlined XOP elements.
    * All base64 representations will be replaced by <code>xop:Include</code>
    * elements and the attachment parts will be recreated. <br>
    * This happens when a SOAPContentElement is written to an output stream.
    */
   public static void restoreXOPDataDOM(SOAPElement xopElement)
   {
      String contentType = (String)xopElement.getUserData(NS_XOP_JBOSSWS_CONTENT_TYPE);
      if (contentType != null && contentType.length() > 0)
      {
         replaceBase64Representation(xopElement, contentType);
         xopElement.setUserData(NS_XOP_JBOSSWS_CONTENT_TYPE, null, null);
      }
      else
      {
         Iterator it = DOMUtils.getChildElements(xopElement);
         while (it.hasNext())
         {
            SOAPElement childElement = (SOAPElement)it.next();
            restoreXOPDataDOM(childElement);
         }
      }
   }

   private static void replaceBase64Representation(SOAPElement xopElement, String contentType)
   {

      SOAPElement parentElement = xopElement.getParentElement();
      if (log.isDebugEnabled())
         log.debug("Replace base64 representation on element [xmlName=" + parentElement.getLocalName() + "]");

      String base64 = xopElement.getValue();
      byte[] data = SimpleTypeBindings.unmarshalBase64(base64);

      MimeUtils.ByteArrayConverter converter = MimeUtils.getConverterForContentType(contentType);
      Object converted = converter.readFrom(new ByteArrayInputStream(data));

      XOPObject xopObject = new XOPObject(converted);
      xopObject.setContentType(contentType);

      XOPMarshaller xopMarshaller = new XOPMarshallerImpl();
      String cid = xopMarshaller.addMtomAttachment(xopObject, xopElement.getNamespaceURI(), xopElement.getLocalName());

      // remove base64 node with the xop:Include element
      org.w3c.dom.Node child = (org.w3c.dom.Node)xopElement.getFirstChild();
      xopElement.removeChild(child);

      try
      {
         SOAPElement xopInclude = xopElement.addChildElement(Constants.NAME_XOP_INCLUDE);
         xopInclude.setAttribute("href", cid);
         if (log.isDebugEnabled())
            log.debug("Restored xop:Include element on [xmlName=" + xopElement.getLocalName() + "]");

         XOPContext.setXOPMessage(true);
      }
      catch (SOAPException e)
      {
         throw new WSException("Failed to create XOP include element", e);
      }

   }

   private static void replaceXOPInclude(SOAPElement parent, SOAPElement xopIncludeElement)
   {

      if (log.isDebugEnabled())
         log.debug("Replace xop:Include on element [xmlName=" + parent.getLocalName() + "]");

      String cid = xopIncludeElement.getAttribute("href");
      byte[] data;
      String contentType;

      try
      {
         AttachmentPart part = XOPContext.getAttachmentByCID(cid);
         DataHandler dh = part.getDataHandler();
         contentType = dh.getContentType();

         // TODO: can't we create base64 directly from stream?
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         dh.writeTo(bout);
         data = bout.toByteArray();

      }
      catch (Exception e)
      {
         throw new WSException("Failed to inline XOP data", e);
      }

      // create base64 contents
      String base64 = SimpleTypeBindings.marshalBase64(data);
      parent.removeChild(xopIncludeElement);
      parent.setValue(base64);
      parent.setUserData(NS_XOP_JBOSSWS_CONTENT_TYPE, contentType, null);

      if (log.isDebugEnabled())
         log.debug("Created base64 representation for content-type " + contentType);

      // cleanup the attachment part
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      SOAPMessageImpl soapMessage = (SOAPMessageImpl)msgContext.getSOAPMessage();

      if (cid.startsWith("cid:"))
         cid = cid.substring(4);
      cid = '<' + cid + '>';

      AttachmentPart removedPart = soapMessage.removeAttachmentByContentId(cid);
      if (null == removedPart)
         throw new WSException("Unable to remove attachment part " + cid);

      if (log.isDebugEnabled())
         log.debug("Removed attachment part " + cid);

      // leave soap object model in a valid state
      setXOPMessage(false);

   }

   /**
    * Access an XOP attachment part by content id (CID).
    */
   public static AttachmentPart getAttachmentByCID(String cid) throws SOAPException
   {
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      SOAPMessageImpl soapMessage = (SOAPMessageImpl)msgContext.getSOAPMessage();

      // RFC2392 requires the 'cid:' part to be stripped from the cid
      if (cid.startsWith("cid:"))
         cid = cid.substring(4);
      cid = '<' + cid + '>';

      AttachmentPart part = soapMessage.getAttachmentByContentId(cid);
      if (part == null)
         throw new WSException("Cannot find attachment part for: " + cid);

      return part;
   }

   /**
    * Create a <code>DataHandler</code> for an object.
    * The handlers content type is based on the java type.
    */
   public static DataHandler createDataHandler(XOPObject xopObject)
   {
      DataHandler dataHandler;
      Object o = xopObject.getContent();

      if (o instanceof DataHandler)
      {
         dataHandler = (DataHandler)o;
      }
      else if (xopObject.getContentType() != null)
      {
         dataHandler = new DataHandler(o, xopObject.getContentType());
      }
      else
      {
         dataHandler = new DataHandler(o, getContentTypeForClazz(o.getClass()));
      }

      return dataHandler;
   }

   public static String getContentTypeForClazz(Class clazz)
   {
      if (JavaUtils.isAssignableFrom(java.awt.Image.class, clazz))
      {
         return "image/jpeg";
      }
      else if (JavaUtils.isAssignableFrom(javax.xml.transform.Source.class, clazz))
      {
         return "application/xml";
      }
      else if (JavaUtils.isAssignableFrom(java.lang.String.class, clazz))
      {
         return "text/plain";
      }
      else
      {
         return "application/octet-stream";
      }
   }
}
