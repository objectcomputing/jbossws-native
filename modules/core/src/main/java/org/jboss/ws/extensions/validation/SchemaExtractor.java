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
package org.jboss.ws.extensions.validation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.jboss.wsf.common.IOUtils;
import org.w3c.dom.Element;

/**
 * Extracts the schema from a given WSDL
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 29-Feb-2008
 */
public class SchemaExtractor
{
   // provide logging
   private static Logger log = Logger.getLogger(SchemaExtractor.class);

   private File xsdFile;
   
   public URL getSchemaUrl(URL wsdlURL) throws IOException
   {
      // parse the wsdl
      Element root = DOMUtils.parse(wsdlURL.openStream());

      // get the types element
      QName typesQName = new QName(root.getNamespaceURI(), "types");
      Element typesEl = DOMUtils.getFirstChildElement(root, typesQName);
      if (typesEl == null)
      {
         log.warn("Cannot find element: " + typesQName);
         return null;
      }

      // get the schema element
      QName schemaQName = new QName("http://www.w3.org/2001/XMLSchema", "schema");
      List<Element> schemaElements = DOMUtils.getChildElementsAsList(typesEl, schemaQName);
      if (schemaElements.size() == 0)
      {
         log.warn("Cannot find element: " + schemaQName);
         return null;
      }
      if (schemaElements.size() > 1)
      {
         log.warn("Multiple schema elements not supported.");
      }
      Element schemaElement = schemaElements.get(0);

      File tmpdir = IOUtils.createTempDirectory();
      xsdFile = File.createTempFile("jbossws_schema", ".xsd", tmpdir);
      xsdFile.deleteOnExit();

      OutputStreamWriter outwr = new OutputStreamWriter(new FileOutputStream(xsdFile));
      DOMWriter domWriter = new DOMWriter(outwr);
      domWriter.setPrettyprint(true);
      domWriter.print(schemaElement);
      outwr.close();

      return xsdFile.toURL();
   }
   
   public void close()
   {
      if (xsdFile != null)
      {
         xsdFile.delete();
         xsdFile = null;
      }
   }
}
