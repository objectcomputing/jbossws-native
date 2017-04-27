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
package org.jboss.ws.tools.interfaces;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.jboss.ws.core.jaxrpc.LiteralTypeMapping;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLException;

/**
 * Defines the contract for WSDL To Java Converters
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jul 23, 2005
 */

public interface WSDLToJavaIntf
{

   /**
    * Provide a WSDLFile and get back the WSDL20 Object Graph
    * @param wsdlfileurl
    * @return
    * @throws org.jboss.ws.metadata.wsdl.WSDLException
    */
   public WSDLDefinitions convertWSDL2Java(URL wsdlfileurl)
      throws WSDLException;

   /**
    * Get the features
    * <br>
    * <br>Features are:
    * <br>USE_ANNOTATIONS  : Should the generated Java Types use annotations
    * @throws IllegalStateException feature unrecognized
    */
   public boolean getFeature(String name);

   /**
    * Set one or more features of the WSDLToJava  tool
    * <br>
    * <br>Features are:
    * <br>USE_ANNOTATIONS  : Should the generated Java Types use annotations
    * @param name
    * @param value
    */
   public void setFeature(String name, boolean value);

   /**
    * Method for use by the Web Services Layer
    * @param wsdlFile URL to the WSDL file
    * @param dir Directory where the generated files should be stored
    * @param annotate Is JAX-WS 2.0 compliance needed ie. annotations needed?
    * @throws IOException
    */
   public void generateSEI(URL wsdlFile, File dir, boolean annotate)
      throws IOException;

   /**
    * Generate the SEI
    * @param wsdl The WSDL20Definitions (root of the object tree)
    * @param dir  The directory where the SEI files will be written
    * @throws IOException
    * @throws Exception
    */
   public void generateSEI(WSDLDefinitions wsdl, File dir) throws IOException;

   /**
    * Global configuration from user that defines a map of package->Namespace
    *
    * @param map
    */
   public void setNamespacePackageMap(Map<String,String> map);

   /**
    * The client can provide a type mapping
    * @param typeMapping
    */
   public void setTypeMapping(LiteralTypeMapping typeMapping);

   public void setParameterStyle(String paramStyle);

}
