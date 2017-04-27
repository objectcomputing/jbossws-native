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

import java.util.Collection;
import java.util.LinkedHashMap;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;

/**
 * A Message Reference component associates a defined type with a message
 * exchanged in an operation. By default, the type system is based upon the XML
 * Infoset
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @since 10-Oct-2004
 */
public abstract class WSDLInterfaceMessageReference extends Extendable implements Comparable
{
   // provide logging
   protected Logger log = Logger.getLogger(getClass());

   // The parent interface operation
   private WSDLInterfaceOperation wsdlOperation;

   /**
    * The OPTIONAL messageLabel attribute information item identifies the role
    * of this message in the message exchange pattern of the given operation
    * element information item.
    */
   private String messageLabel;

   /**
    * The OPTIONAL element attribute information item is the element declaration
    * from the {element declarations} property resolved by the value of the
    * element attribute information item, otherwise empty.
    */
   private QName element;

   /**
    * Used mainly for WSDL 1.1 compatibility, indicates rpc parts.
    * Although, this could be used to represent WSDL 2.0 RPC style.
    */
   private LinkedHashMap<String, WSDLRPCPart> childParts = new LinkedHashMap<String, WSDLRPCPart>();

   /**
    * Used for WSDL 1.1
    */
   private String partName;

   /**
    * Used for WSDL 1.1
    */
   private QName messageName;

   public WSDLInterfaceMessageReference(WSDLInterfaceOperation wsdlOperation)
   {
      log.trace("New part for wsdlOperation: " + wsdlOperation.getName());
      this.wsdlOperation = wsdlOperation;
   }

   public WSDLInterfaceOperation getWsdlOperation()
   {
      return wsdlOperation;
   }

   public String getMessageLabel()
   {
      return messageLabel;
   }

   public void setMessageLabel(String messageLabel)
   {
      this.messageLabel = messageLabel;
   }

   public QName getElement()
   {
      return element;
   }

   public void setElement(QName element)
   {
      log.trace("setElement: " + element);
      this.element = element;
   }

   /**
    * Get the xmlType for this operation part.
    */
   public QName getXMLType()
   {
      QName xmlType = null;

      // First try to read it from the schema
      WSDLDefinitions wsdlDefinitions = wsdlOperation.getWsdlInterface().getWsdlDefinitions();
      WSDLTypes wsdlTypes = wsdlDefinitions.getWsdlTypes();
      xmlType = wsdlTypes.getXMLType(element);

      // Fall back to the property
      if (xmlType == null)
      {
         WSDLProperty property = getProperty(Constants.WSDL_PROPERTY_PART_XMLTYPE);
         if (property != null)
         {
            String qnameRef = property.getValue();
            int colIndex = qnameRef.indexOf(':');
            String prefix = qnameRef.substring(0, colIndex);
            String localPart = qnameRef.substring(colIndex + 1);
            String nsURI = wsdlDefinitions.getNamespaceURI(prefix);
            xmlType = new QName(nsURI, localPart, prefix);
         }
      }

      if (xmlType == null)
         throw new WSException("Cannot obtain xmlType for element: " + element);

      return xmlType;
   }

   /**
    * Gets the child parts associated with this message reference. This is only
    * used for RPC style, and currently only supported by WSDL 1.1.
    *
    * @return the list of rpc parts that make up the message
    */
   public Collection<WSDLRPCPart> getChildParts()
   {
      return childParts.values();
   }

   /**
    * Gets the child part associated with this message reference by part name.
    * This is only used for RPC style, and currently only supported by WSDL 1.1.
    *
    * @param name the part name
    * @return the part or null if not found
    */
   public WSDLRPCPart getChildPart(String name)
   {
      return childParts.get(name);
   }

   /**
    * Adds a child part to this mesage reference. This is only used for RPC
    * style, and currently only supported by WSDL 1.1.
    *
    * @param childPart the list of rpc parts that make up the message
    */
   public void addChildPart(WSDLRPCPart childPart)
   {
      this.childParts.put(childPart.getName(), childPart);
   }

   /**
    * Removes a speficied child part. This is This is only used for RPC
    * style, and currently only supported by WSDL 1.1.
    *
    * @param name the name of the part
    */
   public void removeChildPart(String name)
   {
      this.childParts.remove(name);
   }

   /**
    * Gets the WSDL 1.1 part name.
    *
    * @return the part name
    */
   public String getPartName()
   {
      return partName;
   }

   /**
    * Sets the WSDL 1.1 message name.
    *
    * @param messageName The part name
    */
   public void setMessageName(QName messageName)
   {
      this.messageName = messageName;
   }

   /**
    * Gets the WSDL 1.1 message name.
    *
    * @return the message name
    */
   public QName getMessageName()
   {
      return messageName;
   }

   /**
    * Sets the WSDL 1.1 part name.
    *
    * @param partName The part name
    */
   public void setPartName(String partName)
   {
      this.partName = partName;
   }

   public int compareTo(Object o)
   {
      int c = -1;
      if (o instanceof WSDLInterfaceMessageReference)
      {
         WSDLInterfaceMessageReference w = (WSDLInterfaceMessageReference) o;
         String oname = w.getElement().getLocalPart();
         String myname = getElement().getLocalPart();
         c = myname.compareTo(oname);
      }
      return c;
   }
}
