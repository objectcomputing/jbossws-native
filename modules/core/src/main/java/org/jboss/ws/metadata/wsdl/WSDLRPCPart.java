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

import javax.xml.namespace.QName;

/**
 * Represents a child part of a RPC style message reference. This is currently
 * only used for WSDL 1.1 compatibility.
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class WSDLRPCPart
{
   private final String name;
   private final QName type;

   public WSDLRPCPart(String name, QName type)
   {
      this.name = name;
      this.type = type;
   }

   /**
    * Gets the XML local name of this part.
    *
    * @return the XML local name
    */
   public String getName()
   {
      return name;
   }

   /**
    * Gets the XML type of this part.
    *
    * @return the XML type
    */
   public QName getType()
   {
      return type;
   }
}
