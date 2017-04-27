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

import java.io.Serializable;

/**
 * This defines a registry of TypeMapping instances for encoding styles.
 *  
 * @author Scott.Stark@jboss.org
 * @author Thomas.Diesler@jboss.org
 */
public interface TypeMappingRegistry extends Serializable
{
   /**
    * Removes all registered TypeMappings and encodingStyleURIs from this TypeMappingRegistry.
    */
   public void clear();

   /**
    * Gets the registered default TypeMapping instance.
    * This method returns null if there is no registered default TypeMapping in the registry.
    * @return The registered default TypeMapping instance or null
    */
   public TypeMapping getDefaultTypeMapping();

   /**
    * Registers the TypeMapping instance that is default for all encoding styles supported by the TypeMappingRegistry.
    * A default TypeMapping should include serializers and deserializers that are independent of and usable with any
    * encoding style. Successive invocations of the registerDefault method replace any existing default TypeMapping instance.
    *
    * If the default TypeMapping is registered, any other TypeMapping instances registered through the
    * TypeMappingRegistry.register method (for a set of encodingStyle URIs) override the default TypeMapping.
    *
    * @param mapping TypeMapping instance
    * @throws javax.xml.rpc.JAXRPCException If there is an error in the registration of the default TypeMapping
    */
   public void registerDefault(TypeMapping mapping);

   /**
    * Creates a new empty TypeMapping object.
    * @return TypeMapping instance
    */
   public TypeMapping createTypeMapping();

   /**
    * Returns the registered TypeMapping for the specified encodingStyle URI. If there is no registered TypeMapping for
    * the specified encodingStyleURI, this method returns null.
    * @param encodingStyleURI Encoding style specified as an URI
    * @return TypeMapping for the specified encodingStyleURI or null
    */
   public TypeMapping getTypeMapping(String encodingStyleURI);

   /**
    * Returns a list of registered encodingStyle URIs in this TypeMappingRegistry instance.
    * @return Array of the registered encodingStyle URIs
    */
   public String[] getRegisteredEncodingStyleURIs();

   /**
    * Registers a TypeMapping instance with the TypeMappingRegistry.
    * This method replaces any existing registered TypeMapping instance for the specified encodingStyleURI.
    *
    * @param encodingStyleURI An encoding style specified as an URI.
    * @param mapping TypeMapping instance
    * @return Previous TypeMapping associated with the specified encodingStyleURI, or null if there was no
    * TypeMapping associated with the specified encodingStyleURI
    * @throws javax.xml.rpc.JAXRPCException If there is an error in the registration of the TypeMapping for the specified encodingStyleURI.
    */
   public TypeMapping register(String encodingStyleURI, TypeMapping mapping);

   /**
    * Unregisters a TypeMapping instance, if present, from the specified encodingStyleURI.
    * @param encodingStyleURI Encoding style specified as an URI
    * @return TypeMapping instance that has been unregistered or null if there was no
    * TypeMapping registered for the specified encodingStyleURI
    */
   public TypeMapping unregisterTypeMapping(String encodingStyleURI);

   /**
    * Removes a TypeMapping from the TypeMappingRegistry.
    * A TypeMapping is associated with 1 or more encodingStyleURIs. This method unregisters the specified
    * TypeMapping instance from all associated encodingStyleURIs and then removes this TypeMapping
    * instance from the registry.
    *
    * @param mapping TypeMapping to be removed
    * @return true if specified TypeMapping is removed from the TypeMappingRegistry;
    * false if the specified TypeMapping was not in the TypeMappingRegistry
    */
   public boolean removeTypeMapping(TypeMapping mapping);
}
