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
package org.jboss.ws.extensions.addressing;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingConstants;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.AttributedQName;
import javax.xml.ws.addressing.AttributedURI;
import javax.xml.ws.addressing.EndpointReference;
import javax.xml.ws.addressing.Relationship;

/** 
 * Factory for <code>AddressingElements</code>. 
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Nov-2005
 */
public class AddressingBuilderImpl extends AddressingBuilder
{
   public AttributedURI newURI(URI uri)
   {
      return new AttributedURIImpl(uri);
   }

   public AttributedURI newURI(String uri) throws URISyntaxException
   {
      return newURI(new URI(uri));
   }

   public AttributedQName newQName(QName name)
   {
      return new AttributedQNameImpl(name);
   }

   public Relationship newRelationship(URI uri)
   {
      return new RelationshipImpl(uri);
   }

   public EndpointReference newEndpointReference(URI uri)
   {
      return new EndpointReferenceImpl(uri);
   }

   public AddressingProperties newAddressingProperties()
   {
      return new AddressingPropertiesImpl();
   }

   public AddressingConstants newAddressingConstants()
   {
      return new AddressingConstantsImpl();
   }

   public String getNamespaceURI()
   {
      return new AddressingTypeImpl().getNamespaceURI();
   }
}
