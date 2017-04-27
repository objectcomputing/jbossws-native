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

import javax.xml.bind.JAXBContext;
import javax.xml.transform.Source;

/** The <code>LogicalMessage</code> interface represents a
 *  protocol agnostic XML message and contains methods that
 *  provide access to the payload of the message.
 *
 *  @since JAX-WS 2.0
 **/
public interface LogicalMessage
{

   /** Gets the message payload as an XML source, may be called
    *  multiple times on the same LogicalMessage instance, always
    *  returns a new Source that may be used to retrieve the entire
    *  message payload.
    *
    *  <p>If the returned Source is an instance of DOMSource, then
    *  modifications to the encapsulated DOM tree change the message
    *  payload in-place, there is no need to susequently call
    *  <code>setPayload</code>. Other types of Source provide only
    *  read access to the message payload.
    *
    *  @return The contained message payload; returns null if no 
    *          payload is present in this message.
    **/
   public Source getPayload();

   /** Sets the message payload
    *
    *  @param  payload message payload
    *  @throws WebServiceException If any error during the setting
    *          of the payload in this message
    *  @throws java.lang.UnsupportedOperationException If this
    *          operation is not supported
    **/
   public void setPayload(Source payload);

   /** Gets the message payload as a JAXB object. Note that there is no
    *  connection between the returned object and the message payload,
    *  changes to the payload require calling <code>setPayload</code>.
    *
    *  @param  context The JAXBContext that should be used to unmarshall
    *          the message payload
    *  @return The contained message payload; returns null if no 
    *          payload is present in this message
    *  @throws WebServiceException If an error occurs when using a supplied
    *     JAXBContext to unmarshall the payload. The cause of
    *     the WebServiceException is the original JAXBException.
    **/
   public Object getPayload(JAXBContext context);

   /** Sets the message payload
    *
    *  @param  payload message payload
    *  @param  context The JAXBContext that should be used to marshall 
    *          the payload
    *  @throws java.lang.UnsupportedOperationException If this
    *          operation is not supported
    *  @throws WebServiceException If an error occurs when using the supplied
    *     JAXBContext to marshall the payload. The cause of
    *     the WebServiceException is the original JAXBException.
    **/
   public void setPayload(Object payload, JAXBContext context);
}
