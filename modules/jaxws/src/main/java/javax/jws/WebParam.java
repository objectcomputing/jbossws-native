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
 * Customizes the mapping of an individual parameter to a Web Service message part and XML element.
 *
 * @author thomas.diesler@jboss.org
 * @since 26-Apr-2005
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.PARAMETER })
public @interface WebParam
{
   /**
    * The direction in which the parameter flows
    */
   public enum Mode
   {
      IN, OUT, INOUT
   };

   /**
    * Name of the parameter.
    * 
    * If the operation is rpc style and @WebParam.partName has not been specified, this is name of the wsdl:part representing the parameter.
    * If the operation is document style or the parameter maps to a header, this is the local name of the XML element representing the parameter.
    * 
    * A name MUST be specified if the operation is document style, the parameter style is BARE, and the mode is OUT or INOUT.
    * 
    * Specification Default:
    *   If the operation is document style and the parameter style is BARE, @WebMethod.operationName.
    *   Otherwise, argN, where N represents the index of the parameter in the method signature (starting at arg0). 
    */
   String name() default "";

   /**
    * The name of the wsdl:part representing this return value.
    * This is only used if the operation is rpc style, or if the operation is document style and the parameter style is BARE. 
    */
   String partName() default "";

   /**
    * The XML namespace for the parameter.
    * 
    * Only used if the operation is document style or the paramater maps to a header. If the target namespace is set to “”, this represents the empty namespace.
    * 
    * Specification Default:
    *   If the operation is document style, the parameter style is WRAPPED, and the parameter does not map to a header, the empty namespace.
    *   Otherwise, the targetNamespace for the Web Service. 
    */
   String targetNamespace() default "";

   /**
    * The direction in which the parameter is flowing.  One of IN, OUT, or INOUT.  The OUT and INOUT modes may only be
    * specified for parameter types that conform to the JAX-RPC definition of Holder types.  See JAX-RPC 1.1, section
    * 4.3.5. OUT and INOUT modes are only supported for RPC bindings or for parameters that map to headers.
    */
   Mode mode() default Mode.IN;

   /**
    * If true, the parameter is pulled from a message header rather then the message body.
    */
   boolean header() default false;
};
