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
import java.util.Iterator;

/** A factory of deserializers. A DeserializerFactory is registered with a
 * TypeMapping instance as part of the TypeMappingRegistry.
 *
 * @see Deserializer
 * @see TypeMapping 
 * @see TypeMappingRegistry
 * 
 * @author Scott.Stark@jboss.org
 */
public interface DeserializerFactory extends Serializable
{
   /**
    * Returns a Deserializer for the specified XML processing mechanism type.
    * @param mechanismType XML processing mechanism type [TBD: definition of valid constants]
    * @return a Deserializer
    * @throws javax.xml.rpc.JAXRPCException If DeserializerFactory does not support the specified XML processing mechanism
    */
   public Deserializer getDeserializerAs(String mechanismType);

   /** Returns a list of all XML processing mechanism types supported by this DeserializerFactory.
    *
    * @return List of unique identifiers for the supported XML processing mechanism types
    */
   public Iterator getSupportedMechanismTypes();
}
