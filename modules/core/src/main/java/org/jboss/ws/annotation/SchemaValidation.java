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
package org.jboss.ws.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.xml.ws.spi.WebServiceFeatureAnnotation;

import org.jboss.ws.extensions.validation.StrictlyValidErrorHandler;
import org.jboss.ws.feature.SchemaValidationFeature;

/**
 * This feature represents the use of schema validation with a 
 * web service.
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 29-Feb-2008
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
@EndpointFeature( id = SchemaValidationFeature.ID)
@WebServiceFeatureAnnotation(id = SchemaValidationFeature.ID, bean = SchemaValidationFeature.class)
public @interface SchemaValidation 
{
   /**
    * Optional property for the schema location. If this is not specified the schema
    * will be attempted to extract from the WSDL.
    * 
    * The syntax is the same as for schemaLocation attributes in instance documents: e.g, "http://www.example.com file_name.xsd".
    */
   String schemaLocation() default "";
   
   /**
    * Optional property for the error handler. 
    * If this is not specified the @{ValidationErrorHandler} will be used.
    */
   Class errorHandler() default StrictlyValidErrorHandler.class;
   
   /**
    * Specifies if the feature is enabled or disabled
    */
   boolean enabled() default true;
}
