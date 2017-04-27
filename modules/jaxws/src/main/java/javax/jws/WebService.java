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
 * Marks a Java class as implementing a Web Service, or a Java interface as defining a Web Service interface.
 *
 * @author thomas.diesler@jboss.org
 * @since 26-Apr-2005
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface WebService
{

   /**
    * The name of the Web Service.
    * Used as the name of the wsdl:portType when mapped to WSDL 1.1.
    * Specification Default:
    *   The simple name of the Java class or interface. 
    */
   String name() default "";

   /**
    * The port name of the Web Service.
    * Used as the name of the wsdl:port when mapped to WSDL 1.1.
    * This member-value is not allowed on endpoint interfaces.
    * Specification Default:
    *   @WebService.name + ”Port”. 
    */
   String portName() default "";

   /**
    * If the @WebService.targetNamespace annotation is on a service endpoint interface, the targetNamespace is used for the 
    * namespace for the wsdl:portType (and associated XML elements).
    * 
    * If the @WebService.targetNamespace annotation is on a service implementation bean that does NOT reference a service 
    * endpoint interface (through the endpointInterface attribute), the targetNamespace is used for both the wsdl:portType 
    * and the wsdl:service (and associated XML elements).
    * 
    * If the @WebService.targetNamespace annotation is on a service implementation bean that does reference a service 
    * endpoint interface (through the endpointInterface attribute), the targetNamespace is used for only the wsdl:service 
    * (and associated XML elements). 
    */
   String targetNamespace() default "";

   /**
    * The service name of the Web Service.
    * 
    * Used as the name of the wsdl:service when mapped to WSDL 1.1.
    * 
    * This member-value is not allowed on endpoint interfaces.
    * 
    * Specification Default:
    *   The simple name of the Java class + “Service". 
    */
   String serviceName() default "";

   /**
    * The location of a pre-defined WSDL describing the service.
    * 
    * The wsdlLocation is a URL (relative or absolute) that refers to a pre-existing WSDL file. 
    * The presence of a wsdlLocation value indicates that the service implementation bean is implementing a 
    * pre-defined WSDL contract. The JSR-181 tool MUST provide feedback if the service implementation bean is 
    * inconsistent with the portType and bindings declared in this WSDL. Note that a single WSDL file might 
    * contain multiple portTypes and multiple bindings. The annotations on the service implementation bean 
    * determine the specific portType and bindings that correspond to the Web Service. 
    */
   String wsdlLocation() default "";

   /**
    * The complete name of the service endpoint interface defining the service’s abstract Web Service contract.
    * 
    * This annotation allows the developer to separate the interface contract from the implementation. 
    * If this annotation is present, the service endpoint interface is used to determine the abstract 
    * WSDL contract (portType and bindings). The service endpoint interface MAY include JSR-181 annotations 
    * to customize the mapping from Java to WSDL.
    * 
    * The service implementation bean MAY implement the service endpoint interface, but is not REQUIRED to do so.
    * If this member-value is not present, the Web Service contract is generated from annotations on the service 
    * implementation bean. If a service endpoint interface is required by the target environment, it will be generated into an implementation-defined package with an implementation- defined name
    * 
    * This member-value is not allowed on endpoint interfaces. 
    */
   String endpointInterface() default "";
};
