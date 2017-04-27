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
package javax.xml.ws;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 *  Used to annotate a generated service interface.
 *
 *  <p>The information specified in this annotation is sufficient
 *  to uniquely identify a <code>wsdl:service</code>
 *  element inside a WSDL document. This <code>wsdl:service</code>
 *  element represents the Web service for which the generated
 *  service interface provides a client view.
 *
 *  @since JAX-WS 2.0
 **/
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebServiceClient {
   /**
    *  The local name of the Web service.
    **/
   String name() default "";

   /**
    *  The namespace for the Web service.
    **/
   String targetNamespace() default "";

   /**
    *  The location of the WSDL document for the service (a URL).
    **/
   String wsdlLocation() default "";
}
