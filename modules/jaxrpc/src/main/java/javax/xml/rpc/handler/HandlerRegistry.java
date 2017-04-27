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
package javax.xml.rpc.handler;

import java.io.Serializable;
import java.util.List;

import javax.xml.namespace.QName;

/** This interface provides support for the programmatic configuration of
 * handlers in a HandlerRegistry.
 * 
 * A handler chain is registered per service endpoint, as indicated by the
 * qualified name of a port. The getHandlerChain returns the handler chain
 * (as a java.util.List) for the specified service endpoint. The returned
 * handler chain is configured using the java.util.List interface. Each element
 * in this list is required to be of the Java type
 * javax.xml.rpc.handler.HandlerInfo. 
 * 
 * @author Scott.Stark@jboss.org
 * @author Rahul Sharma (javadoc)
 */
public interface HandlerRegistry extends Serializable
{
   public List getHandlerChain(QName portName);

   public void setHandlerChain(QName portName, List chain);
}
