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
package org.jboss.ws.core.jaxws;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.binding.SerializationContext;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.metadata.umdm.EndpointMetaData;

/**
 * The serialization context for JAXWS endpoints/clients
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 03-Jul-2006
 */
public class SerializationContextJAXWS extends SerializationContext
{
   public static final String JAXB_CONTEXT_TYPES = "org.jboss.ws.jaxb.context.types";   
}
