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
package org.jboss.ws.core.server;

import javax.xml.soap.MimeHeaders;

/**
 * Generic abstraction of a source that allows getting and setting
 * of transport specific MIME headers.
 *
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 */
public interface MimeHeaderSource
{
   /**
    * Gets the MIME headers from an inbound source. An implementation may
    * return <code>null</code> if the transport does not provide headers.
    *
    * @return the MIME headers
    */
   public MimeHeaders getMimeHeaders();

   /**
    * Sets the MIME headers for an outbound source. An implementation may
    * return <code>null</code> if the transport does not provide headers.
    *
    * @param headers the MIME headers
    */
   public void setMimeHeaders(MimeHeaders headers);
}
