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
package javax.jws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the given method is exposed as a Web Service operation, making it part of the Web Service's public
 * contract.  A WebMethod annotation is required for each method that is published by the Web Service.  The associated
 * method must be public and its parameters return value, and exceptions must follow the rules defined in JAX-RPC 1.1,
 * section 5.  The method is not required to throw java.rmi.RemoteException.
 *
 * @author thomas.diesler@jboss.org
 * @since 26-Apr-2005
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
public @interface WebMethod
{
   /**
    * Indicate that a method should not be exposed on the Web Service
    */
   boolean exclude() default false;

   /**
    * Name of the wsdl:operation matching this method.  By default the WSDL operation name will be the same
    * as the Java method name.
    */
   String operationName() default "";

   /**
    *  The action for this operation.  For SOAP bindings, this determines the value of the SOAPAction header.
    */
   String action() default "";
};
