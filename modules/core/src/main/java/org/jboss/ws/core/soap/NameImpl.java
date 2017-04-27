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
package org.jboss.ws.core.soap;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;

/**
 * An implementation of a Name
 * <p/>
 * At this time of writing, the spec does not say anything about null values.
 * We assume emty string for any null value.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 01-June-2004
 */
public class NameImpl implements Name
{
   private QName qname;

   public NameImpl(QName qname)
   {
      this.qname = qname;
   }

   public NameImpl(String local)
   {
      qname = new QName(local);
   }

   public NameImpl(String local, String prefix, String uri)
   {
      if (prefix != null)
         qname = new QName(uri, local, prefix);
      else
         qname = new QName(uri, local);
   }

   /**
    * Gets the local name part of the XML name that this Name object represents.
    *
    * @return a string giving the local name
    */
   public String getLocalName()
   {
      return qname.getLocalPart();
   }

   /**
    * Returns the prefix that was specified when this Name object was initialized.
    * This prefix is associated with the namespace for the XML name that this Name object represents.
    *
    * @return the prefix as a string
    */
   public String getPrefix()
   {
      return qname.getPrefix();
   }

   /**
    * Gets the namespace-qualified name of the XML name that this Name object represents.
    *
    * @return the namespace-qualified name as a string
    */
   public String getQualifiedName()
   {
      String prefix = getPrefix();
      if (prefix.length() > 0)
         return prefix + ":" + qname.getLocalPart();
      else
         return qname.getLocalPart();
   }

   /**
    * Returns the URI of the namespace for the XML name that this Name object represents.
    *
    * @return the URI as a string
    */
   public String getURI()
   {
      return qname.getNamespaceURI();
   }

   public int hashCode()
   {
      return qname.hashCode();
   }

   public boolean equals(Object obj)
   {
      if (!(obj instanceof NameImpl)) return false;
      if (obj == this) return true;
      NameImpl other = (NameImpl)obj;
      return qname.equals(other.qname);
   }

   public QName toQName()
   {
      return qname;
   }

   public String toString()
   {
      return qname.toString();
   }
}
