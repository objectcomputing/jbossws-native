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

/** A point-to-point connection that a client can use for sending messages directly to a remote
 * party (represented by a URL, for instance).
 *
 * The SOAPConnection class is optional. Some implementations may not implement this interface in which case the call
 * to SOAPConnectionFactory.newInstance() (see below) will throw an UnsupportedOperationException.
 *
 * A client can obtain a SOAPConnection object using a SOAPConnectionFactory object as in the following example:
 *
 *    SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
 *    SOAPConnection con = factory.createConnection();
 *
 * A SOAPConnection object can be used to send messages directly to a URL following the request/response paradigm.
 * That is, messages are sent using the method call, which sends the message and then waits until it gets a reply.
 *
 * @author Scott.Stark@jboss.org
 */
public abstract class SOAPConnection
{
   public SOAPConnection()
   {  
   }

   /** Sends the given message to the specified endpoint and blocks until it has returned the response.
    *
    * @param request the SOAPMessage object to be sent
    * @param to an Object that identifies where the message should be sent.
    * It is required to support Objects of type java.lang.String, java.net.URL, and when JAXM is present javax.xml.messaging.URLEndpoint
    * @return the SOAPMessage object that is the response to the message that was sent
    * @throws SOAPException  if there is a SOAP error
    */
   public abstract SOAPMessage call(SOAPMessage request, Object to) throws SOAPException;

   /**
    * Gets a message from a specific endpoint and blocks until it receives,
    * @param to an Object that identifies where the request should be sent. Objects of type java.lang.String and java.net.URL must be supported.
    * @return the SOAPMessage object that is the response to the get message request
    * @throws SOAPException if there is a SOAP error
    * @since SAAJ 1.3
    */
   public SOAPMessage get(Object to) throws SOAPException
   {
      throw new IllegalStateException("Should be implemented by concrete implementation of this class");
   }
   
   /** Closes this SOAPConnection object.
    *
    * @throws SOAPException if there is a SOAP error
    */
   public abstract void close() throws SOAPException;
}
