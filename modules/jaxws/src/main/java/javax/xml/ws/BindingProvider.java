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
package javax.xml.ws;

import java.util.Map;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

/** The <code>BindingProvider</code> interface provides access to the
 *  protocol binding and associated context objects for request and
 *  response message processing.
 *
 *  @since JAX-WS 2.0
 *
 *  @see javax.xml.ws.Binding
 **/
public interface BindingProvider
{
   /** Standard property: User name for authentication.
    *  <p>Type: java.lang.String
    **/
   public static final String USERNAME_PROPERTY = "javax.xml.ws.security.auth.username";

   /** Standard property: Password for authentication.
    *  <p>Type: java.lang.String
    **/
   public static final String PASSWORD_PROPERTY = "javax.xml.ws.security.auth.password";

   /** Standard property: Target service endpoint address. The
    *  URI scheme for the endpoint address specification MUST
    *  correspond to the protocol/transport binding for the
    *  binding in use.
    *  <p>Type: java.lang.String
    **/
   public static final String ENDPOINT_ADDRESS_PROPERTY = "javax.xml.ws.service.endpoint.address";

   /** Standard property: This boolean property is used by a service
    *  client to indicate whether or not it wants to participate in
    *  a session with a service endpoint. If this property is set to
    *  true, the service client indicates that it wants the session
    *  to be maintained. If set to false, the session is not maintained.
    *  The default value for this property is false.
    *  <p>Type: java.lang.Boolean
    **/
   public static final String SESSION_MAINTAIN_PROPERTY = "javax.xml.ws.session.maintain";

   /** Standard property for SOAPAction. This boolean property
    *  indicates whether or not SOAPAction is to be used. The
    *  default value of this property is false indicating that
    *  the SOAPAction is not used.
    *  <p>Type: <code>java.lang.Boolean</code>
    **/
   public static final String SOAPACTION_USE_PROPERTY = "javax.xml.ws.soap.http.soapaction.use";

   /** Standard property for SOAPAction. Indicates the SOAPAction
    *  URI if the <code>javax.xml.ws.soap.http.soapaction.use</code>
    *  property is set to <code>true</code>.
    *  <p>Type: <code>java.lang.String</code>
    **/
   public static final String SOAPACTION_URI_PROPERTY = "javax.xml.ws.soap.http.soapaction.uri";

   /** Get the context that is used to initialize the message context
    *  for request messages.
    *
    * Modifications to the request context do not affect the message context of
    * either synchronous or asynchronous operations that have already been
    * started.
    *
    * @return The context that is used in processing request messages.
    **/
   Map<String, Object> getRequestContext();

   /** Get the context that resulted from processing a response message.
    *
    * The returned context is for the most recently completed synchronous
    * operation. Subsequent synchronous operation invocations overwrite the
    * response context. Asynchronous operations return their response context
    * via the Response interface.
    *
    * @return The context that resulted from processing the latest
    * response messages.
    **/
   Map<String, Object> getResponseContext();

   /** Get the Binding for this binding provider.
    *
    * @return The Binding for this binding provider.
    **/
   Binding getBinding();
   
   /**
    * Returns the <code>EndpointReference</code> associated with
    * this <code>BindingProvider</code> instance.
    * <p>
    * If the Binding for this <code>bindingProvider</code> is
    * either SOAP1.1/HTTP or SOAP1.2/HTTP, then a
    * <code>W3CEndpointReference</code> MUST be returned.
    * If the returned <code>EndpointReference</code> is a
    * <code>W3CEndpointReference</code> it MUST contain
    * the <code>wsaw:ServiceName</code> element and the
    * <code>wsaw:EndpointName</code> attribute on the
    * <code>wsaw:ServiceName</code>. It SHOULD contain
    * the embedded WSDL in the <code>wsa:Metadata</code> element
    * if there is an associated WSDL. The
    * <code>wsaw:InterfaceName</code> MAY also be present.
    * <br>
    * See <a href="http://www.w3.org/TR/2006/CR-ws-addr-wsdl-20060529/">
    * WS-Addressing - WSDL 1.0</a>.
    *
    * @return EndpointReference of the target endpoint associated with this
    * <code>BindingProvider</code> instance.
    *
    * @throws java.lang.UnsupportedOperationException If this
    * <code>BindingProvider</code> uses the XML/HTTP binding.
    *
    * @see W3CEndpointReference
    *
    * @since JAX-WS 2.1
    */
   public EndpointReference getEndpointReference();

   /**
    * Returns the <code>EndpointReference</code> associated with
    * this <code>BindingProvider</code> instance.  The instance
    * returned will be of type <code>clazz</code>.
    * <p>
    * If the returned <code>EndpointReference</code> is a
    * <code>W3CEndpointReference</code> it MUST contain
    * the <code>wsaw:ServiceName</code> element and the
    * <code>wsaw:EndpointName</code> attribute on the
    * <code>wsaw:ServiceName</code>. It SHOULD contain
    * the embedded WSDL in the <code>wsa:Metadata</code> element
    * if there is an associated WSDL. The
    * <code>wsaw:InterfaceName</code> MAY also be present.
    * <br>
    * See <a href="http://www.w3.org/TR/2006/CR-ws-addr-wsdl-20060529/">
    * WS-Addressing - WSDL 1.0</a>.
    *
    * @param clazz Specifies the type of <code>EndpointReference</code>
    * that MUST be returned.

    * @return EndpointReference of the target endpoint associated with this
    * <code>BindingProvider</code> instance. MUST be of type
    * <code>clazz</code>.

    * @throws javax.xml.ws.WebServiceException If the Class <code>clazz</code>
    * is not supported by this implementation.
    * @throws java.lang.UnsupportedOperationException If this
    * <code>BindingProvider</code> uses the XML/HTTP binding.
    *
    * @since JAX-WS 2.1
    */
   public <T extends EndpointReference> T getEndpointReference(Class<T> clazz);
   
}
