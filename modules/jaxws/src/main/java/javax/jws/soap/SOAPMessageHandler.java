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
package javax.jws.soap;

/**
 * A single SOAP message handler
 *
 * @author thomas.diesler@jboss.org
 * @since 26-Apr-2005
 * @deprecated
 */
@Deprecated
public @interface SOAPMessageHandler
{

   /**
    * Name of the handler.  Defaults to the name of the handler class.
    */
   String name() default "";

   /**
    * Name of the handler class.
    */
   String className();

   /**
    * Array of name/value pairs that should be passed to the handler during initialization.
    */
   InitParam[] initParams() default {};

   /**
    * List of SOAP roles/actors implemented by the handler
    */
   String[] roles() default {};

   /**
    * List of SOAP headers processed by the handler.  Each element in this array contains a QName which defines the
    * header element processed by the handler.  The QNames are specified using the string notation described in the
    * documentation for javax.xml.namespace.QName.valueOf(String qNameAsString)
    */
   String[] headers() default {};
};
