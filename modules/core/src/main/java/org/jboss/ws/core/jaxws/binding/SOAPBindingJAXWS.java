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
package org.jboss.ws.core.jaxws.binding;

import java.util.HashSet;
import java.util.Set;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;

import org.jboss.logging.Logger;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.metadata.umdm.OperationMetaData;

/**
 * The SOAPBinding interface is an abstraction for the SOAP binding. 
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 04-Jul-2006
 */
class SOAPBindingJAXWS extends BindingImpl
{
   // provide logging
   private static Logger log = Logger.getLogger(SOAPBindingJAXWS.class);

   private Set<String> roles = new HashSet<String>();

   public Set<String> getRoles()
   {
      return roles;
   }

   public void setRoles(Set<String> roles)
   {
      this.roles = roles;
   }

   public void setSOAPActionHeader(OperationMetaData opMetaData, SOAPMessage reqMessage)
   {
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      MimeHeaders mimeHeaders = reqMessage.getMimeHeaders();
      String soapAction = opMetaData.getSOAPAction();

      // R2744 A HTTP request MESSAGE MUST contain a SOAPAction HTTP header field
      // with a quoted value equal to the value of the soapAction attribute of
      // soapbind:operation, if present in the corresponding WSDL description.

      // R2745 A HTTP request MESSAGE MUST contain a SOAPAction HTTP header field
      // with a quoted empty string value, if in the corresponding WSDL description,
      // the soapAction attribute of soapbind:operation is either not present, or
      // present with an empty string as its value.

      if (msgContext.get(BindingProvider.SOAPACTION_USE_PROPERTY) != null)
         log.info("Ignore BindingProvider.SOAPACTION_USE_PROPERTY because of BP-1.0 R2745, R2745");

      String soapActionProperty = (String)msgContext.get(BindingProvider.SOAPACTION_URI_PROPERTY);
      if (soapActionProperty != null)
         soapAction = soapActionProperty;

      mimeHeaders.addHeader("SOAPAction", soapAction != null ? soapAction : "");
   }
}
