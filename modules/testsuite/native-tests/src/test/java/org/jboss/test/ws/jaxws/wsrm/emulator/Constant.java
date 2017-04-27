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
package org.jboss.test.ws.jaxws.wsrm.emulator;

/**
 * Emulator framework constants
 *
 * @author richard.opalka@jboss.com
 *
 * @since Nov 9, 2007
 */
public final class Constant
{
   
   private Constant()
   {
      // forbidden inheritance
   }
   
   public static final String CONFIG_FILE = "config.file";
   public static final String SEPARATOR = "|";
   public static final String PARAGRAPH = "$";
   public static final String LEFT_BRACKET = "{";
   public static final String RIGHT_BRACKET = "}";
   public static final String HTTP_POST = "POST";
   public static final String HTTP_GET = "GET";
   public static final String EQUAL = "="; 
   public static final String COMMA = ",";
   public static final String SPACE = " ";
   public static final String PROPERTIES = "properties";
   public static final String MATCHES = "matches";
   // XML configuration elements and attributes
   public static final String NAMESPACES_ELEMENT = "namespaces";
   public static final String NAMESPACE_ELEMENT = "namespace";
   public static final String VIEW_ELEMENT = "view";
   public static final String REQUEST_ELEMENT = "request";
   public static final String RESPONSE_ELEMENT = "response";
   public static final String SET_ELEMENT = "set";
   public static final String CONTAINS_ELEMENT = "contains";
   public static final String NODE_ELEMENT = "node";
   public static final String ID_ATTRIBUTE = "id";
   public static final String NAME_ATTRIBUTE = "name";
   public static final String EQUALS_ATTRIBUTE = "equals";
   public static final String RESOURCE_ATTRIBUTE = "resource";
   public static final String STATUS_CODE_ATTRIBUTE = "statusCode";
   public static final String CONTENT_TYPE_ATTRIBUTE = "contentType";
   public static final String PROPERTY_ATTRIBUTE = "property";
   public static final String VALUE_ATTRIBUTE = "value";
   public static final String HTTP_METHOD_ATTRIBUTE = "httpMethod";
   public static final String PATH_INFO_ATTRIBUTE = "pathInfo";
   // XML configuration referencable entities
   public static final String RESPONSE_TO = PARAGRAPH + LEFT_BRACKET + "res.wsa.to" + RIGHT_BRACKET;
   public static final String ADDRESSING_ANONYMOUS_URI = "http://www.w3.org/2005/08/addressing/anonymous";
}
