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
package org.jboss.ws.metadata.wsdl;

import java.io.Serializable;

/**
 * A feature component describes an abstract piece of functionality typically associated with the exchange of
 * messages between communicating parties. Although WSDL poses no constraints on the potential scope of
 * such features, examples might include "reliability", "security", "correlation", and "routing". The presence
 * of a feature component in a WSDL description indicates that the service supports the feature and may
 * require a requester agent that interacts with the service to use that feature. Each Feature is identified by its
 * URI.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Oct-2004
 */
public class WSDLFeature implements Serializable
{
   private static final long serialVersionUID = 8096857958254222743L;
   
   /** A REQUIRED uri attribute information item */
   private String uri;
   /** An OPTIONAL required attribute information item */
   private boolean required;

   public WSDLFeature(String uri)
   {
      if (uri == null)
         throw new IllegalArgumentException("Illegal feature URI: " + uri);

      this.uri = uri;
   }

   public WSDLFeature(String uri, boolean required)
   {
      if (uri == null)
         throw new IllegalArgumentException("Illegal feature URI: " + uri);

      this.uri = uri;
      this.required = required;
   }

   public String getURI()
   {
      return uri;
   }

   public boolean isRequired()
   {
      return required;
   }

   public void setRequired(boolean required)
   {
      this.required = required;
   }
   
   public String toString()
   {
      return uri;
   }
}
