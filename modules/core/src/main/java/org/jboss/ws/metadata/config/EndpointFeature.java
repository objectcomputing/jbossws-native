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
package org.jboss.ws.metadata.config;

/**
 * @author Heiko.Braun@jboss.org
 * @author Thomas.Diesler@jboss.org
 * @since 14.12.2006
 */
public interface EndpointFeature
{
   /** Enable MTOM per endpoint */
   final static String MTOM = "http://org.jboss.ws/mtom";

   /** 
    * Validate the XML stream upon dispatch.
    * Introduces an additional parsing overhead and could be disabled.
    */
   final static String VALIDATE_DISPATCH = "http://org.jboss.ws/dispatch/validate";

   /** Generates message part names 'parameters' in WSDL for document/literal/wapped */
   final static String BINDING_WSDL_DOTNET = "http://org.jboss.ws/binding/wsdl/dotnet";
}
