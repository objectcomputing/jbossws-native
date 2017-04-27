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

/**
 * Defines an endpoint or client configuration. 
 * This annotation is valid on an endpoint implementaion bean or a SEI.
 * 
 * @author Heiko.Braun@jboss.org
 * @since 16.01.2007
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface EndpointConfig {

   /**
    * The optional config-name element gives the configuration name that must be present in
    * the configuration given by element config-file.
    *
    * Server side default: Standard Endpoint
    * Client side default: Standard Client
    */
   String configName() default "";

   /**
    * The optional config-file element is a URL or resource name for the configuration.
    *
    * Server side default: standard-jaxws-endpoint-config.xml
    * Client side default: standard-jaxws-client-config.xml
    */
   String configFile() default "";
}
