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
 *  The <code>BindingType</code> annotation is used to
 *  specify the binding to use for a web service
 *  endpoint implementation class. As well as specify
 *  additional features that may be enabled.
 *
 *  @since JAX-WS 2.0
 *
 **/

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BindingType {
   /**
    * A binding identifier (a URI).
    * If not specified, the default is the SOAP 1.1 / HTTP binding.
    * <p>
    * See the <code>SOAPBinding</code> and <code>HTTPBinding</code>
    * for the definition of the standard binding identifiers.
    *
    * @see javax.xml.ws.Binding
    * @see javax.xml.ws.soap.SOAPBinding#SOAP11HTTP_BINDING
    * @see javax.xml.ws.soap.SOAPBinding#SOAP12HTTP_BINDING
    * @see javax.xml.ws.http.HTTPBinding#HTTP_BINDING
    */
   String value() default "";
}
