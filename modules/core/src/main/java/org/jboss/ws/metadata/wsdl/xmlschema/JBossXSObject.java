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
package org.jboss.ws.metadata.wsdl.xmlschema;

import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObject;

/**
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Apr 20, 2005
 */
public class JBossXSObject  implements XSObject
{
   protected String name;
   protected String namespace;
   private String prefix;
   private XSNamespaceItem xsNSItem;
   private short type;

   public JBossXSObject()
   {

   }

   public JBossXSObject(String name, String namespace)
   {
      this.name = name;
      this.namespace = namespace;
   }

   /**
    *  The <code>type</code> of this object, i.e.
    * <code>ELEMENT_DECLARATION</code>.
    */
   public short getType()
   {
      return type;
   }

   /**
    * The name of type <code>NCName</code>, as defined in XML Namespaces, of
    * this declaration specified in the <code>{name}</code> property of the
    * component or <code>null</code> if the definition of this component
    * does not have a <code>{name}</code> property. For anonymous types,
    * the processor must construct and expose an anonymous type name that
    * is distinct from the name of every named type and the name of every
    * other anonymous type.
    */
   public String getName()
   {
      return name;
   }

   public void setName(String n)
   {
      name = n;
   }

   /**
    *  The [target namespace] of this object, or <code>null</code> if it is
    * unspecified.
    */
   public String getNamespace()
   {
      return namespace;
   }

   public void setNamespace(String namespace)
   {
      this.namespace = namespace;
   }

   /**
    * A namespace schema information item corresponding to the target
    * namespace of the component, if it is globally declared; or
    * <code>null</code> otherwise.
    */
   public XSNamespaceItem getNamespaceItem()
   {
      return xsNSItem;
   }

   public void setNamespaceItem(XSNamespaceItem xsNSItem)
   {
      this.xsNSItem = xsNSItem;
   }

   public void setType(short t)
   {
      type = t;
   }

   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (!(o instanceof JBossXSObject)) return false;

      final JBossXSObject jBossXSObject = (JBossXSObject)o;

      if (name != null ? !name.equals(jBossXSObject.name) : jBossXSObject.name != null) return false;
      if (namespace != null ? !namespace.equals(jBossXSObject.namespace) : jBossXSObject.namespace != null) return false;

      return true;
   }

   public int hashCode()
   {
      int result;
      result = (name != null ? name.hashCode() : 0);
      result = 29 * result + (namespace != null ? namespace.hashCode() : 0);
      return result;
   }

}
