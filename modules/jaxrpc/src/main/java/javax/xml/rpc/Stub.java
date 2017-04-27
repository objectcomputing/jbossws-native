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
package javax.xml.rpc;

import java.util.Iterator;

/**
 * The interface javax.xml.rpc.Stub is the common base interface for the stub classes.
 * All generated stub classes are required to implement the javax.xml.rpc.Stub interface.
 * An instance of a stub class represents a client side proxy or stub instance for the target service endpoint.
 *
 * The javax.xml.rpc.Stub interface provides an extensible property mechanism for the dynamic configuration of a stub instance.
 *
 * @author Scott.Stark@jboss.org
 * @author Thomas.Diesler@jboss.org
 */
public interface Stub
{
   /** Standard property: User name for authentication. */
   public static final String USERNAME_PROPERTY = "javax.xml.rpc.security.auth.username";
   /**  Standard property: Password for authentication. */
   public static final String PASSWORD_PROPERTY = "javax.xml.rpc.security.auth.password";
   /** Standard property: Target service endpoint address.  */
   public static final String ENDPOINT_ADDRESS_PROPERTY = "javax.xml.rpc.service.endpoint.address";
   /** Standard property: This boolean property is used by a service client to indicate whether or not it wants to participate in a session with a service endpoint. */
   public static final String SESSION_MAINTAIN_PROPERTY = "javax.xml.rpc.session.maintain";

   /** Sets the name and value of a configuration property for this Stub instance.
    *
    * If the Stub instances contains a value of the same property, the old value is replaced.
    * Note that the _setProperty method may not perform validity check on a configured property value.
    * An example is the standard property for the target service endpoint address that is not checked for validity in
    * the _setProperty method. In this case, stub configuration errors are detected at the remote method invocation.
    *
    * @param name Name of the configuration property
    * @param value Value of the property
    * @throws  JAXRPCException
    *    If an optional standard property name is specified, however this Stub implementation class does not support the configuration of this property.
    *    If an invalid or unsupported property name is specified or if a value of mismatched property type is passed.
    *    If there is any error in the configuration of a valid property.
    */
   public void _setProperty(String name, Object value);

   /** Gets the value of a specific configuration property.
    * @param name Name of the property whose value is to be retrieved
    * @return Value of the configuration property
    * @throws  JAXRPCException if an invalid or unsupported property name is passed.
    */
   public Object _getProperty(String name);

   /** Returns an Iterato view of the names of the properties that can be configured on this stub instance.
    * @return Iterator for the property names of the type java.lang.String
    */
   public Iterator _getPropertyNames();
}
