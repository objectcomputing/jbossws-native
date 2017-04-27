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

import javax.xml.namespace.QName;
import javax.xml.ws.addressing.Relationship;

/** 
 * Abstraction of the RelatesToType defined by WS-Addressing. Includes an ID
 * property that references the MessageID of the related message and a Type
 * property corresponding to the RelationshipType attribute of the
 * RelatesToType. Implementing classes must supply a single argument constructor
 * with parameter type <code>java.net.URI</code>, which is used to initialize
 * the <b>ID</b> property.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Nov-2005
 */
public class RelationshipImpl extends AttributeExtensibleImpl implements Relationship
{
   private URI uri;
   private QName type;

   public RelationshipImpl(URI uri)
   {
      this.uri = uri;
   }

   public URI getID()
   {
      return uri;
   }

   public QName getType()
   {
      return type;
   }

   public void setType(QName type)
   {
      this.type = type;
   }

}
