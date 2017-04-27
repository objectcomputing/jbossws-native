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
package javax.xml.ws.addressing;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

public class JAXWSAConstants
{

   private JAXWSAConstants()
   {
   }

   public static final String ADDRESSING_BUILDER_PROPERTY = "javax.xml.ws.addressing.AddressingBuilder";

   public static final String DEFAULT_ADDRESSING_BUILDER = "org.jboss.ws.extensions.addressing.soap.SOAPAddressingBuilderImpl";

   public static final String SOAP11_NAMESPACE_NAME = "http://schemas.xmlsoap.org/soap/envelope/";

   public static final String SOAP12_NAMESPACE_NAME = "http://www.w3.org/2003/05/soap-envelope";

   public static final QName SOAP11_SENDER_QNAME = new QName(SOAP11_NAMESPACE_NAME, "Client");

   public static final QName SOAP11_RECEIVER_QNAME = new QName(SOAP11_NAMESPACE_NAME, "Server");

   public static final QName SOAP12_SENDER_QNAME = new QName(SOAP12_NAMESPACE_NAME, "Sender");

   public static final QName SOAP12_RECEIVER_QNAME = new QName(SOAP12_NAMESPACE_NAME, "Receiver");

   public static final String SOAP11HTTP_ADDRESSING_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http?addressing=1.0";

   public static final String SOAP12HTTP_ADDRESSING_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/?addressing=1.0";

   public static final String CLIENT_ADDRESSING_PROPERTIES = "javax.xml.ws.addressing.context";

   public static final String CLIENT_ADDRESSING_PROPERTIES_INBOUND = "javax.xml.ws.addressing.context.inbound";

   public static final String CLIENT_ADDRESSING_PROPERTIES_OUTBOUND = "javax.xml.ws.addressing.context.outbound";

   public static final String SERVER_ADDRESSING_PROPERTIES_INBOUND = "javax.xml.ws.addressing.context.inbound";

   public static final String SERVER_ADDRESSING_PROPERTIES_OUTBOUND = "javax.xml.ws.addressing.context.outbound";

   public static SOAPFactory SOAP_FACTORY = null;

   static
   {
      try
      {
         SOAP_FACTORY = SOAPFactory.newInstance();
      }
      catch (SOAPException e)
      {
      }
   }

}
