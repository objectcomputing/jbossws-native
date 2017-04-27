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
package javax.xml.soap;

import javax.xml.namespace.QName;

/** The definition of constants pertaining to the SOAP protocol. 
 * @author Scott.Stark@jboss.org
 * @author Thomas.Diesler@jboss.org
 */
public interface SOAPConstants
{
   /** Used to create MessageFactory instances that create SOAPMessages whose behavior supports the SOAP 1.1 specification */
   String SOAP_1_1_PROTOCOL = "SOAP 1.1 Protocol";
   /** The media type of the Content-Type MIME header in SOAP 1.2. */
   String SOAP_1_2_CONTENT_TYPE = "application/soap+xml";
   /** The default protocol: SOAP 1.1 for backwards compatibility. */
   String DEFAULT_SOAP_PROTOCOL = SOAP_1_1_PROTOCOL;
   /** Used to create MessageFactory instances that create SOAPMessages whose concrete type is based on the Content-Type MIME header passed to the createMessage method. */
   String DYNAMIC_SOAP_PROTOCOL = "Dynamic Protocol";
   /** The media type of the Content-Type MIME header in SOAP 1.1. */
   String SOAP_1_1_CONTENT_TYPE = "text/xml";
   /** Used to create MessageFactory instances that create SOAPMessages whose behavior supports the SOAP 1.2 specification */
   String SOAP_1_2_PROTOCOL = "SOAP 1.2 Protocol";
   /** The default namespace prefix for http://www.w3.org/2003/05/soap-envelope */
   String SOAP_ENV_PREFIX = "env";
   /** The namespace identifier for the SOAP 1.1 envelope. */
   String URI_NS_SOAP_1_1_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";
   /** The namespace identifier for the SOAP 1.2 encoding. */
   String URI_NS_SOAP_1_2_ENCODING = "http://www.w3.org/2003/05/soap-encoding";
   /** The namespace identifier for the SOAP 1.2 envelope. */
   String URI_NS_SOAP_1_2_ENVELOPE = "http://www.w3.org/2003/05/soap-envelope";
   /** The namespace identifier for the SOAP 1.1 encoding. */
   String URI_NS_SOAP_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/";
   /** The namespace identifier for the SOAP 1.1 envelope, All SOAPElements in this namespace are defined by the SOAP 1.1 specification. */
   String URI_NS_SOAP_ENVELOPE = URI_NS_SOAP_1_1_ENVELOPE;
   /** The URI identifying the next application processing a SOAP request as the intended role for a SOAP 1.2 header entry (see section 2.2 of part 1 of the SOAP 1.2 specification). */
   String URI_SOAP_1_2_ROLE_NEXT = "http://www.w3.org/2003/05/soap-envelope/role/next";
   /** The URI specifying the role None in SOAP 1.2. */
   String URI_SOAP_1_2_ROLE_NONE = "http://www.w3.org/2003/05/soap-envelope/role/none";
   /** The URI identifying the ultimate receiver of the SOAP 1.2 message. */
   String URI_SOAP_1_2_ROLE_ULTIMATE_RECEIVER = "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver";
   /** The URI identifying the next application processing a SOAP request as the intended actor for a SOAP 1.1 header entry (see section 4.2.2 of the SOAP 1.1 specification). */
   String URI_SOAP_ACTOR_NEXT = "http://schemas.xmlsoap.org/soap/actor/next";
   /** SOAP 1.2 VersionMismatch Fault */
   QName SOAP_VERSIONMISMATCH_FAULT = new QName(URI_NS_SOAP_1_2_ENVELOPE, "VersionMismatch", SOAP_ENV_PREFIX);
   /** SOAP 1.2 MustUnderstand Fault */
   QName SOAP_MUSTUNDERSTAND_FAULT = new QName(URI_NS_SOAP_1_2_ENVELOPE, "MustUnderstand", SOAP_ENV_PREFIX);
   /** SOAP 1.2 DataEncodingUnknown Fault */
   QName SOAP_DATAENCODINGUNKNOWN_FAULT = new QName(URI_NS_SOAP_1_2_ENVELOPE, "DataEncodingUnknown", SOAP_ENV_PREFIX);
   /** SOAP 1.2 Sender Fault */
   QName SOAP_SENDER_FAULT = new QName(URI_NS_SOAP_1_2_ENVELOPE, "Sender", SOAP_ENV_PREFIX);
   /** SOAP 1.2 Receiver Fault */
   QName SOAP_RECEIVER_FAULT = new QName(URI_NS_SOAP_1_2_ENVELOPE, "Receiver", SOAP_ENV_PREFIX);
}
