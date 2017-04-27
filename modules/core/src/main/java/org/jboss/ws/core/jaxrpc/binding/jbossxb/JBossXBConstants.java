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

/** JBossXB Constants
 *
 * @author Thomas.Diesler@jboss.org
 * @since 18-Oct-2004
 */
public interface JBossXBConstants
{
   /** Set this property with a Reader to the xsdSchema */
   String JBXB_SCHEMA_READER = "org.jboss.xb.xsd.reader";
   /** Set this property with a the QName of the root element */
   String JBXB_ROOT_QNAME = "org.jboss.xb.root.qname";
   /** Set this property with a the QName of the root type */
   String JBXB_TYPE_QNAME = "org.jboss.xb.type.qname";
   /** Set this property with an instance of JavaWsdlMapping */
   String JBXB_JAVA_MAPPING = "org.jboss.xb.java.mapping";
   /** Set this property to the XSModel to pull schema info from */
   String JBXB_XS_MODEL = "org.jboss.xb.xsd.xsmodel";
}
