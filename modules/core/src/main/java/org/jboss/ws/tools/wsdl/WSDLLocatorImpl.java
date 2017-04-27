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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.wsdl.xml.WSDLLocator;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.utils.ResourceURL;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/* A WSDLLocator that can handle wsdl imports
 */
class WSDLLocatorImpl implements WSDLLocator
{
   // provide logging
   private static final Logger log = Logger.getLogger(WSDLDefinitionsFactory.class);

   private EntityResolver entityResolver;
   private URL wsdlLocation;
   private String latestImportURI;

   public WSDLLocatorImpl(EntityResolver entityResolver, URL wsdlLocation)
   {
      if (wsdlLocation == null)
         throw new IllegalArgumentException("WSDL file argument cannot be null");

      this.entityResolver = entityResolver;
      this.wsdlLocation = wsdlLocation;
   }

   public InputSource getBaseInputSource()
   {
      log.trace("getBaseInputSource [wsdlUrl=" + wsdlLocation + "]");
      try
      {
         InputStream inputStream = new ResourceURL(wsdlLocation).openStream();
         if (inputStream == null)
            throw new IllegalArgumentException("Cannot obtain wsdl from [" + wsdlLocation + "]");

         return new InputSource(inputStream);
      }
      catch (IOException e)
      {
         throw new RuntimeException("Cannot access wsdl from [" + wsdlLocation + "], " + e.getMessage());
      }
   }

   public String getBaseURI()
   {
      return wsdlLocation.toExternalForm();
   }

   public InputSource getImportInputSource(String parent, String resource)
   {
      log.trace("getImportInputSource [parent=" + parent + ",resource=" + resource + "]");

      URL parentURL = null;
      try
      {
         parentURL = new URL(parent);
      }
      catch (MalformedURLException e)
      {
         log.error("Not a valid URL: " + parent);
         return null;
      }

      String wsdlImport = null;
      String external = parentURL.toExternalForm();

      // An external URL
      if (resource.startsWith("http://") || resource.startsWith("https://"))
      {
         wsdlImport = resource;
      }

      // Absolute path
      else if (resource.startsWith("/"))
      {
         String beforePath = external.substring(0, external.indexOf(parentURL.getPath()));
         wsdlImport = beforePath + resource;
      }

      // A relative path
      else
      {
         String parentDir = external.substring(0, external.lastIndexOf("/"));

         // remove references to current dir
         while (resource.startsWith("./"))
            resource = resource.substring(2);

         // remove references to parentdir
         while (resource.startsWith("../"))
         {
            parentDir = parentDir.substring(0, parentDir.lastIndexOf("/"));
            resource = resource.substring(3);
         }

         wsdlImport = parentDir + "/" + resource;
      }

      try
      {
         log.trace("Trying to resolve: " + wsdlImport);
         InputSource inputSource = entityResolver.resolveEntity(wsdlImport, wsdlImport);
         if (inputSource != null)
         {
            latestImportURI = wsdlImport;
         }
         else
         {
            throw new IllegalArgumentException("Cannot resolve imported resource: " + wsdlImport);
         }

         return inputSource;
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception e)
      {
         throw new WSException("Cannot access imported wsdl [" + wsdlImport + "], " + e.getMessage());
      }
   }

   public String getLatestImportURI()
   {
      return latestImportURI;
   }

   public void close()
   {
   }
}
