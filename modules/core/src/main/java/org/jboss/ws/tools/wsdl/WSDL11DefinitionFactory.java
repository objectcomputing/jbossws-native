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
package org.jboss.ws.tools.wsdl;

import java.net.URL;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.jboss.logging.Logger;
import org.jboss.ws.core.utils.JBossWSEntityResolver;
import org.xml.sax.EntityResolver;

/**
 * A factory that creates a WSDL-1.1 <code>Definition</code> from an URL.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 15-April-2004
 */
public class WSDL11DefinitionFactory
{
   // provide logging
   private static Logger log = Logger.getLogger(WSDL11DefinitionFactory.class);

   // This feature is set by default in wsdl4j, it means the object structore contains the imported arguments
   public static final String FEATURE_IMPORT_DOCUMENTS = "javax.wsdl.importDocuments";
   // Set this feature for additional debugging output
   public static final String FEATURE_VERBOSE = "javax.wsdl.verbose";

   // The WSDLReader that is used by this factory
   private WSDLReader wsdlReader;

   // Hide constructor
   private WSDL11DefinitionFactory() throws WSDLException
   {
      WSDLFactory wsdlFactory = WSDLFactory.newInstance();
      wsdlReader = wsdlFactory.newWSDLReader();
      // Allow unknown extensions (jaxws/jaxb binding elements)
      wsdlReader.setExtensionRegistry(new ExtensionRegistry());
      wsdlReader.setFeature(WSDL11DefinitionFactory.FEATURE_VERBOSE, false);
   }

   /** Create a new instance of a wsdl factory */
   public static WSDL11DefinitionFactory newInstance() throws WSDLException
   {
      return new WSDL11DefinitionFactory();
   }

   /** Set a feature on the underlying reader */
   public void setFeature(String name, boolean value) throws IllegalArgumentException
   {
      wsdlReader.setFeature(name, value);
   }

   /**
    * Read the wsdl document from the given URL
    */
   public Definition parse(URL wsdlLocation) throws WSDLException
   {
      if (wsdlLocation == null)
         throw new IllegalArgumentException("URL cannot be null");

      log.trace("parse: " + wsdlLocation.toExternalForm());
      
      EntityResolver entityResolver = new JBossWSEntityResolver();
      
      // Set EntityResolver in patched version of wsdl4j-1.5.2jboss
      // [TODO] show the usecase that needs this
      // ((WSDLReaderImpl)wsdlReader).setEntityResolver(entityResolver);
      Definition wsdlDefinition = wsdlReader.readWSDL(new WSDLLocatorImpl(entityResolver, wsdlLocation));
     
      return wsdlDefinition;
   }
}
