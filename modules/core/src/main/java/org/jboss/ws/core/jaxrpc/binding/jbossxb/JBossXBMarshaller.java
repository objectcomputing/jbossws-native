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
package org.jboss.ws.core.jaxrpc.binding.jbossxb;

import java.io.OutputStream;
import java.io.Writer;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

/**
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Jul 5, 2006
 */
public interface JBossXBMarshaller {

   void marshal(Object obj, Writer writer) throws MarshalException;

   void marshal(Object obj, ContentHandler handler);

   void marshal(Object obj, Node node);

   void marshal(Object obj, OutputStream os) throws MarshalException;

   Object getProperty(String name);

   void setProperty(String name, Object value);
}
