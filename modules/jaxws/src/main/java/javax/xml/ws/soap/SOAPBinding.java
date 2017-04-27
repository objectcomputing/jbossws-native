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
package javax.xml.ws.soap;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.Binding;
import javax.xml.ws.WebServiceException;
import java.util.Set;

/** The <code>SOAPBinding</code> interface is an abstraction for
 *  the SOAP binding.
 * 
 *  @since JAX-WS 2.0
 **/
public interface SOAPBinding extends Binding
{
   /**
    * A constant representing the identity of the SOAP 1.1 over HTTP binding.
    */
   public static final String SOAP11HTTP_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http";

   /**
    * A constant representing the identity of the SOAP 1.2 over HTTP binding.
    */
   public static final String SOAP12HTTP_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/";

   /**
    * A constant representing the identity of the SOAP 1.1 over HTTP binding
    * with MTOM enabled by default.
    */
   public static final String SOAP11HTTP_MTOM_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true";

   /**
    * A constant representing the identity of the SOAP 1.2 over HTTP binding
    * with MTOM enabled by default.
    */
   public static final String SOAP12HTTP_MTOM_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true";

   /** Gets the roles played by the SOAP binding instance.
    *
    *  @return Set<String> The set of roles played by the binding instance.
    **/
   public Set<String> getRoles();

   /** Sets the roles played by the SOAP binding instance.
    *
    *  @param roles    The set of roles played by the binding instance.
    *  @throws WebServiceException On an error in the configuration of
    *                  the list of roles.
    **/
   public void setRoles(Set<String> roles);

   /**
    * Returns <code>true</code> if the use of MTOM is enabled.
    *
    * @return <code>true</code> if and only if the use of MTOM is enabled.
    **/

   public boolean isMTOMEnabled();

   /**
    * Enables or disables use of MTOM.
    *
    * @param flag   A <code>boolean</code> specifying whether the use of MTOM should
    *               be enabled or disabled.
    *  @throws WebServiceException If the specified setting is not supported
    *                  by this binding instance.
    *   *
    **/
   public void setMTOMEnabled(boolean flag);

   /**
    * Gets the SAAJ <code>SOAPFactory</code> instance used by this SOAP binding.
    *
    * @return SOAPFactory instance used by this SOAP binding.
    **/
   public SOAPFactory getSOAPFactory();

   /**
    * Gets the SAAJ <code>MessageFactory</code> instance used by this SOAP binding.
    *
    * @return MessageFactory instance used by this SOAP binding.
    **/
   public MessageFactory getMessageFactory();
}
