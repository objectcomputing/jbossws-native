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
package org.jboss.ws.extensions.addressing;

import javax.xml.namespace.QName;
import javax.xml.ws.addressing.AddressingConstants;

import org.jboss.util.NotImplementedException;
import org.jboss.ws.Constants;

/**
 * Encapsulation for version-specific WS-Addressing constants.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Nov-2005
 */
public class AddressingConstantsImpl implements AddressingConstants
{
   static final String URI_ADDRESSING = "http://www.w3.org/2005/08/addressing";
   static final String PREFIX_ADDRESSING = "wsa";

   public String getNamespaceURI()
   {
      return URI_ADDRESSING;
   }

   public String getNamespacePrefix()
   {
      return PREFIX_ADDRESSING;
   }

   public String getWSDLNamespaceURI()
   {
      return Constants.NS_WSDL11;
   }

   public String getWSDLNamespacePrefix()
   {
      return Constants.PREFIX_WSDL;
   }

   public QName getWSDLExtensibilityQName()
   {
      return null;
   }

   public QName getWSDLActionQName()
   {
      return new QName(URI_ADDRESSING, "Action", "wsa");
   }

   public String getAnonymousURI()
   {
      return "http://www.w3.org/2005/08/addressing/anonymous";
   }

   public String getNoneURI()
   {
      return "http://www.w3.org/2005/08/addressing/none";
   }

   public QName getFromQName()
   {
      return new QName(URI_ADDRESSING, "From", PREFIX_ADDRESSING);
   }

   public QName getToQName()
   {
      return new QName(URI_ADDRESSING, "To", PREFIX_ADDRESSING);
   }

   public QName getReplyToQName()
   {
      return new QName(URI_ADDRESSING, "ReplyTo", PREFIX_ADDRESSING);
   }

   public QName getFaultToQName()
   {
      return new QName(URI_ADDRESSING, "FaultTo", PREFIX_ADDRESSING);
   }

   public QName getActionQName()
   {
      return new QName(URI_ADDRESSING, "Action", PREFIX_ADDRESSING);
   }

   public QName getMessageIDQName()
   {
      return new QName(URI_ADDRESSING, "MessageID", PREFIX_ADDRESSING);
   }

   public QName getRelationshipReplyQName()
   {
      return new QName(URI_ADDRESSING, "Reply", PREFIX_ADDRESSING);
   }

   public QName getRelatesToQName()
   {
      return new QName(URI_ADDRESSING, "RelatesTo", PREFIX_ADDRESSING);
   }

   public String getRelationshipTypeName()
   {
      return "RelationshipType";
   }

   public QName getMetadataQName()
   {
      return new QName(URI_ADDRESSING, "Metadata", PREFIX_ADDRESSING);
   }

   public QName getAddressQName()
   {
      return new QName(URI_ADDRESSING, "Address", PREFIX_ADDRESSING);
   }

   public QName getReferenceParametersQName()
   {
      return new QName(URI_ADDRESSING, "ReferenceParameters", PREFIX_ADDRESSING);
   }
   
   public String getPackageName()
   {
      return getClass().getPackage().getName();
   }

   public String getIsReferenceParameterName()
   {
      throw new NotImplementedException();
   }

   public QName getInvalidMapQName()
   {
      return new QName(URI_ADDRESSING, "InvalidMessageInformationHeader", PREFIX_ADDRESSING);
   }

   public QName getMapRequiredQName()
   {
      return new QName(URI_ADDRESSING, "MessageInformationHeaderRequired", PREFIX_ADDRESSING);
   }

   public QName getDestinationUnreachableQName()
   {
      return new QName(URI_ADDRESSING, "DestinationUnreachable", PREFIX_ADDRESSING);
   }

   public QName getActioNotSupportedQName()
   {
      return new QName(URI_ADDRESSING, "ActionNotSupported", PREFIX_ADDRESSING);
   }

   public QName getEndpointUnavailableQName()
   {
      return new QName(URI_ADDRESSING, "EndpointUnavailable", PREFIX_ADDRESSING);
   }

   public String getDefaultFaultAction()
   {
      return "http://www.w3.org/2005/08/addressing/fault";
   }

   public String getActionNotSupportedText()
   {
      return "Action not supported";
   }

   public String getDestinationUnreachableText()
   {
      return "Destination unreachable";
   }

   public String getEndpointUnavailableText()
   {
      return "Endpoint unavailable";
   }

   public String getInvalidMapText()
   {
      return "Invalid Map";
   }

   public String getMapRequiredText()
   {
      return "Map Required";
   }
}
