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
package javax.xml.ws.handler;

import javax.xml.namespace.QName;

/** 
 *  The <code>PortInfo</code> interface is used by a
 *  <code>HandlerResolver</code> to query information about
 *  the port it is being asked to create a handler chain for.
 *  <p>
 *  This interface is never implemented by an application,
 *  only by a JAX-WS implementation.
 *
 *  @since JAX-WS 2.0
**/
public interface PortInfo {

  /** 
   *  Gets the qualified name of the WSDL service name containing
   *  the port being accessed.
   *
   *  @return javax.xml.namespace.QName The qualified name of the WSDL service.
  **/
  public QName getServiceName();

  /** 
   *  Gets the qualified name of the WSDL port being accessed.
   *
   *  @return javax.xml.namespace.QName The qualified name of the WSDL port.
  **/
  public QName getPortName();

  /** 
   *  Gets the URI identifying the binding used by the port being accessed.
   *
   *  @return String The binding identifier for the port.
   *
   *  @see javax.xml.ws.Binding
  **/
  public String getBindingID();

}
