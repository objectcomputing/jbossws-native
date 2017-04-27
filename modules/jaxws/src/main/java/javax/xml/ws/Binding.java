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

/** The <code>Binding</code> interface is the base interface
 *  for JAX-WS protocol bindings.
 *
 *  @since JAX-WS 2.0
 **/
public interface Binding
{
   /** 
    * Gets a copy of the handler chain for a protocol binding instance.
    * If the returned chain is modified a call to <code>setHandlerChain</code>
    * is required to configure the binding instance with the new chain.
    *
    *  @return java.util.List<javax.xml.ws.handler.HandlerInfo> Handler chain
    */
   public java.util.List<javax.xml.ws.handler.Handler> getHandlerChain();

   /** 
    * Sets the handler chain for the protocol binding instance.
    *
    *  @param chain    A List of handler configuration entries
    *  @throws WebServiceException On an error in the configuration of
    *                  the handler chain
    *  @throws java.lang.UnsupportedOperationException If this
    *          operation is not supported. This may be done to
    *          avoid any overriding of a pre-configured handler
    *          chain.
    */
   public void setHandlerChain(java.util.List<javax.xml.ws.handler.Handler> chain);
   
   /**
    * Get the URI for this binding instance.
    *
    * @return String The binding identifier for the port.
    *    Never returns <code>null</code>
    *
    * @since JAX-WS 2.1
    */
   String getBindingID();
}
