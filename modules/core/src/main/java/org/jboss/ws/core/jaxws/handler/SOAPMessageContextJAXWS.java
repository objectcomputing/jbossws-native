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
package org.jboss.ws.core.jaxws.handler;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jboss.ws.core.CommonMessageContext;

/**
 * The interface SOAPMessageContext provides access to the SOAP message for either RPC request or response. 
 * The javax.xml.soap.SOAPMessage specifies the standard Java API for the representation of a SOAP 1.1 message with attachments.
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 25-Jul-2006
 */
public class SOAPMessageContextJAXWS extends MessageContextJAXWS implements SOAPMessageContext
{
   // The SOAP actor roles 
   private Set<String> roles = new HashSet<String>();

   /** Default ctor */
   public SOAPMessageContextJAXWS()
   {
   }

   public SOAPMessageContextJAXWS(CommonMessageContext msgContext)
   {
      super(msgContext);      
   }

   /**
    * Gets the SOAPMessage from this message context. 
    * Modifications to the returned SOAPMessage change the message in-place, there is no need to susequently call setMessage.
    */
   public SOAPMessage getMessage()
   {
      return getSOAPMessage();
   }

   /**
    * Sets the SOAPMessage in this message context
    */
   public void setMessage(SOAPMessage soapMessage)
   {
      setSOAPMessage(soapMessage);
   }

   /** 
    * Gets headers that have a particular qualified name from the message in the message context. 
    * Note that a SOAP message can contain multiple headers with the same qualified name.
    */
   public Object[] getHeaders(QName qname, JAXBContext context, boolean allRoles)
   {
      List<Object> headers = new ArrayList<Object>();

      if (getSOAPMessage() != null)
      {
         try
         {
            SOAPHeader soapHeader = getSOAPMessage().getSOAPHeader();
            Iterator<SOAPHeaderElement> headerElements = soapHeader.examineAllHeaderElements();
            while (headerElements.hasNext())
            {
               SOAPHeaderElement hElement = headerElements.next();
               Name hName = hElement.getElementName();
               if (qname.equals(new QName(hName.getURI(), hName.getLocalName())))
               {
                  URI actor = null;
                  if (hElement.getActor() != null)
                  {
                     actor = new URI(hElement.getActor());
                  }
                  if (actor != null)
                  {
                     if (roles.contains(actor.toString()) || allRoles)
                     {
                        headers.add(hElement);
                     }
                  }
               }
            }
         }
         catch (RuntimeException rte)
         {
            throw rte;
         }
         catch (Exception ex)
         {
            throw new WebServiceException("Cannot get headers", ex);
         }
      }

      Object[] arr = new Object[headers.size()];
      headers.toArray(arr);
      return arr;
   }

   /**
    * Gets the SOAP actor roles associated with an execution of the handler chain. 
    * Note that SOAP actor roles apply to the SOAP node and are managed using SOAPBinding.setRoles and SOAPBinding.getRoles. 
    * Handler instances in the handler chain use this information about the SOAP actor roles to process the SOAP header blocks. 
    * Note that the SOAP actor roles are invariant during the processing of SOAP message through the handler chain.
    */
   public Set<String> getRoles()
   {
      return roles;
   }
}
