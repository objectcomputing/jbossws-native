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
 * The <code>FaultAction</code> annotation is used inside an <a href="Action.html">
 * Action</a> annotation to allow an explicit association of <code>Action</code> message
 * addressing property with the <code>fault</code> messages of the WSDL operation mapped from
 * the exception class.
 * <p>
 * In this version of JAX-WS there is no standard way to specify Action values in a WSDL and
 * there is no standard default value. It is intended that, after the W3C WG on WS-Addressing
 * has defined these items in a recommendation, a future version of JAX-WS will require the
 * new standards.
 * 
 * @since JAX-WS 2.1
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FaultAction {
   /**
    * Name of the exception class
    */
   Class className();

   /**
    * Value of <code>Action</code> message addressing property for the exception
    */
   String value() default "";
}
