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
package org.jboss.ws.feature;

import javax.xml.ws.WebServiceFeature;

import org.jboss.ws.Constants;
import org.jboss.ws.extensions.validation.StrictlyValidErrorHandler;
import org.xml.sax.ErrorHandler;

/**
 * This feature represents the use of schema validation with a 
 * web service.
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 29-Feb-2008
 */
public final class SchemaValidationFeature extends WebServiceFeature
{
   /** 
    * Constant value identifying the SchemaValidationFeature
    */
   public static final String ID = Constants.NS_JBOSSWS_URI + "/features/schema-validation";

   /**
    * Optional property for the schema location. If this is not specified the schema
    * will be attempted to extract from the WSDL.
    * 
    * The syntax is the same as for schemaLocation attributes in instance documents: e.g, "http://www.example.com file_name.xsd".
    */
   protected String schemaLocation;
   
   /**
    * Optional property for the error handler. 
    * If this is not specified the @{ValidationErrorHandler} will be used.
    */
   protected ErrorHandler errorHandler = new StrictlyValidErrorHandler(); 

   /**
    * Create an <code>SchemaValidationFeature</code>.
    * The instance created will be enabled.
    */
   public SchemaValidationFeature()
   {
      this.enabled = true;
   }

   /**
    * Creates an <code>SchemaValidationFeature</code>.
    * 
    * @param enabled specifies if this feature should be enabled or not
    */
   public SchemaValidationFeature(boolean enabled)
   {
      this.enabled = enabled;
   }

   /**
    * Creates an <code>SchemaValidationFeature</code>.
    * 
    * @param schemaLocation specifies the schema location for this feature
    */
   public SchemaValidationFeature(String schemaLocation)
   {
      this.schemaLocation = schemaLocation;
      this.enabled = true;
   }

   /**
    * Creates an <code>SchemaValidationFeature</code>.
    * 
    * @param enabled specifies if this feature should be enabled or not
    * @param schemaLocation specifies the schema location for this feature
    */
   public SchemaValidationFeature(boolean enabled, String schemaLocation)
   {
      this.schemaLocation = schemaLocation;
      this.enabled = enabled;
   }

   /**
    * {@inheritDoc}
    */
   public String getID()
   {
      return ID;
   }

   public String getSchemaLocation()
   {
      return schemaLocation;
   }

   public void setSchemaLocation(String schemaLocation)
   {
      this.schemaLocation = schemaLocation;
   }

   public ErrorHandler getErrorHandler()
   {
      return errorHandler;
   }

   public void setErrorHandler(ErrorHandler errorHandler)
   {
      this.errorHandler = errorHandler;
   }
}
