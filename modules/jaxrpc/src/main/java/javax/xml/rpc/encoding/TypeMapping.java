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
package javax.xml.rpc.encoding;

import javax.xml.namespace.QName;

/**
 * This is the base interface for the representation of a type mapping. A
 * TypeMapping implementation class may support one or more encoding styles.
 * 
 * For its supported encoding styles, a TypeMapping instance maintains a set of
 * tuples of the type {Java Class, SerializerFactory, DeserializerFactory, XML type-QName}.
 * 
 * @author Scott.Stark@jboss.org
 */
public interface TypeMapping
{

   /**
    * Gets the DeserializerFactory registered for the specified pair of Java type and XML data type.
    * @param javaType Class of the Java type
    * @param xmlType QName of the XML type
    * @return Registered DeserializerFactory or null if there is no registered factory
    */
   public DeserializerFactory getDeserializer(Class javaType, QName xmlType);

   /**
    * Gets the SerializerFactory registered for the specified pair of Java type and XML data type.
    * @param javaType Class of the Java type
    * @param xmlType QName of the XML type
    * @return Registered SerializerFactory or null if there is no registered factory
    */
   public SerializerFactory getSerializer(Class javaType, QName xmlType);

   /**
    * Returns the encodingStyle URIs (as String[]) supported by this TypeMapping instance.
    * A TypeMapping that contains only encoding style independent serializers and deserializers
    * returns null from this method.
    *
    * @return Array of encodingStyle URIs for the supported encoding styles
    */
   public String[] getSupportedEncodings();

   /**
    * Sets the encodingStyle URIs supported by this TypeMapping instance. A TypeMapping that contains only encoding
    * independent serializers and deserializers requires null as the parameter for this method.
    *
    * @param encodingStyleURIs Array of encodingStyle URIs for the supported encoding styles
    */
   public void setSupportedEncodings(String[] encodingStyleURIs);

   /**
    * Checks whether or not type mapping between specified XML type and Java type is registered.
    * @param javaType Class of the Java type
    * @param xmlType QName of the XML type
    * @return boolean; true if type mapping between the specified XML type and Java type is registered; otherwise false
    */
   public boolean isRegistered(Class javaType, QName xmlType);

   /**
    * Registers SerializerFactory and DeserializerFactory for a specific type mapping between an XML type and Java type.
    * This method replaces any existing registered SerializerFactory DeserializerFactory instances.
    * @param javaType Class of the Java type
    * @param xmlType QName of the XML type
    * @param sf SerializerFactory
    * @param dsf DeserializerFactory
    * @throws javax.xml.rpc.JAXRPCException If any error during the registration
    */
   public void register(Class javaType, QName xmlType, SerializerFactory sf, DeserializerFactory dsf);

   /**
    * Removes the DeserializerFactory registered for the specified pair of Java type and XML data type.
    * @param javaType Class of the Java type
    * @param xmlType QName of the XML type
    * @throws javax.xml.rpc.JAXRPCException If there is error in removing the registered DeserializerFactory
    */
   public void removeDeserializer(Class javaType, QName xmlType);

   /**
    * Removes the SerializerFactory registered for the specified pair of Java type and XML data type.
    * @param javaType Class of the Java type
    * @param xmlType QName of the XML type
    * @throws javax.xml.rpc.JAXRPCException If there is error in removing the registered SerializerFactory
    */
   public void removeSerializer(Class javaType, QName xmlType);
}
