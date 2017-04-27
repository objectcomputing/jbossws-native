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

/**
 *  <p>Service endpoints may implement the <code>Provider</code>
 *  interface as a dynamic alternative to an SEI.
 *
 *  <p>Implementations are required to support <code>Provider&lt;Source&gt;</code>,
 *  <code>Provider&lt;SOAPMessage&gt;</code> and
 *  <code>Provider&lt;DataSource&gt;</code>, depending on the binding
 *  in use and the service mode.
 *
 *  <p>The <code>ServiceMode</code> annotation can be used to control whether
 *  the <code>Provider</code> instance will receive entire protocol messages
 *  or just message payloads.
 *
 *  @since JAX-WS 2.0
 *
 *  @see javax.xml.transform.Source
 *  @see javax.xml.soap.SOAPMessage
 *  @see javax.xml.ws.ServiceMode
 **/
public interface Provider<T>
{

   /** Invokes an operation occording to the contents of the request
    *  message.
    *
    *  @param  request The request message or message payload.
    *  @return The response message or message payload. May be null if
    there is no response.
    *  @throws WebServiceException If there is an error processing request.
    *          The cause of the WebServiceException may be set to a subclass
    *          of ProtocolException to control the protocol level
    *          representation of the exception.
    *  @see javax.xml.ws.handler.MessageContext
    *  @see javax.xml.ws.ProtocolException
    **/
   public T invoke(T request);
}
