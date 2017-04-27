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
package org.jboss.ws.core;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.soap.AttachmentPart;

import org.jboss.ws.metadata.umdm.EndpointMetaData;

/**
 * An extension of the standard JAXRPC/JAXWS stubs.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 17-Jan-2007
 */
public interface StubExt extends ConfigProvider
{
   /** ClientTimeout property: org.jboss.ws.timeout */
   static final String PROPERTY_CLIENT_TIMEOUT = "org.jboss.ws.timeout";
   /** Key Alias property: org.jboss.ws.keyAlias */
   static final String PROPERTY_KEY_ALIAS = "org.jboss.ws.keyAlias";
   /** KeyStore property: org.jboss.ws.keyStore */
   static final String PROPERTY_KEY_STORE = "org.jboss.ws.keyStore";
   /** KeyStore Management Algorithm property: org.jboss.ws.keyStoreAlgorithm */
   static final String PROPERTY_KEY_STORE_ALGORITHM = "org.jboss.ws.keyStoreAlgorithm";
   /** KeyStorePassword property: org.jboss.ws.keyStorePassword */
   static final String PROPERTY_KEY_STORE_PASSWORD = "org.jboss.ws.keyStorePassword";
   /** KeyStoreType property: org.jboss.ws.keyStoreType */
   static final String PROPERTY_KEY_STORE_TYPE = "org.jboss.ws.keyStoreType";
   /** Remoting SSL Protocol property: org.jboss.ws.sslProtocol */
   static final String PROPERTY_SSL_PROTOCOL = "org.jboss.ws.sslProtocol";
   /** Remoting SSL Provider Name property: org.jboss.ws.sslProviderName */
   static final String PROPERTY_SSL_PROVIDER_NAME = "org.jboss.ws.sslProviderName";
   /** TrustStore property: org.jboss.ws.trustStore */
   static final String PROPERTY_TRUST_STORE = "org.jboss.ws.trustStore";
   /** TrustStore Management Algorithm property: org.jboss.ws.trustStoreAlgorithm */
   static final String PROPERTY_TRUST_STORE_ALGORITHM = "org.jboss.ws.trustStoreAlgorithm";
   /** TrustStorePassword property: org.jboss.ws.trustStorePassword */
   static final String PROPERTY_TRUST_STORE_PASSWORD = "org.jboss.ws.trustStorePassword";
   /** TrustStoreType property: org.jboss.ws.trustStoreType */
   static final String PROPERTY_TRUST_STORE_TYPE = "org.jboss.ws.trustStoreType";
   /** Authentication type, used to specify basic, etc) */
   static final String PROPERTY_AUTH_TYPE = "org.jboss.ws.authType";
   /** Authentication type, BASIC */
   static final String PROPERTY_AUTH_TYPE_BASIC = "org.jboss.ws.authType.basic";
   /** Authentication type, WSEE */
   static final String PROPERTY_AUTH_TYPE_WSSE = "org.jboss.ws.authType.wsse";
   /** Enable MTOM on the stub */
   static final String PROPERTY_MTOM_ENABLED= "org.jboss.ws.mtom.enabled";

   /**
    * Get the endpoint meta data for this stub
    */
   EndpointMetaData getEndpointMetaData();
   
   /**
    * Add a header that is not bound to an input parameter.
    * A propriatory extension, that is not part of JAXRPC.
    *
    * @param xmlName The XML name of the header element
    * @param xmlType The XML type of the header element
    */
   void addUnboundHeader(QName xmlName, QName xmlType, Class javaType, ParameterMode mode);

   /**
    * Get the header value for the given XML name.
    * A propriatory extension, that is not part of JAXRPC.
    *
    * @param xmlName The XML name of the header element
    * @return The header value, or null
    */
   Object getUnboundHeaderValue(QName xmlName);

   /**
    * Set the header value for the given XML name.
    * A propriatory extension, that is not part of JAXRPC.
    *
    * @param xmlName The XML name of the header element
    */
   void setUnboundHeaderValue(QName xmlName, Object value);

   /**
    * Clear all registered headers.
    * A propriatory extension, that is not part of JAXRPC.
    */
   void clearUnboundHeaders();

   /**
    * Remove the header for the given XML name.
    * A propriatory extension, that is not part of JAXRPC.
    */
   void removeUnboundHeader(QName xmlName);

   /**
    * Get an Iterator over the registered header XML names.
    * A propriatory extension, that is not part of JAXRPC.
    */
   Iterator getUnboundHeaders();
   
   /**
    * Adds the given AttachmentPart object to the outgoing SOAPMessage.
    * An AttachmentPart object must be created before it can be added to a message.
    */
   void addAttachmentPart(AttachmentPart attachmentpart);

   /**
    * Clears the list of attachment parts.
    */
   void clearAttachmentParts();
   
   /**
    * Creates a new empty AttachmentPart object.
    */
   AttachmentPart createAttachmentPart();
}
