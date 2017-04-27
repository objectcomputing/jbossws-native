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
package org.jboss.ws.metadata.wsdl.xsd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import org.apache.xerces.xs.XSModel;
import org.jboss.logging.Logger;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsdl.xmlschema.WSSchemaUtils;

/**
 * XML Schema File Writer
 * @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 */
public class XSDWriter
{
   // provide logging
   protected static final Logger log = Logger.getLogger(XSDWriter.class);
   
   protected WSDLDefinitions wsdl = null;
   
   protected SchemaUtils schemautils = SchemaUtils.getInstance();
   
   protected WSSchemaUtils sutils = WSSchemaUtils.getInstance(null, null); 
   
   public void writeXSD(Writer writer, XSModel xsmodel, WSDLDefinitions wsdl)
   {
      if (xsmodel == null)
         throw new IllegalArgumentException("xsmodel is NULL");
      
      this.wsdl = wsdl;
      
      if (schemautils.isEmptySchema(xsmodel)) return; 
   }
   
   /**
    * Serialize the schema into a temp file
    * @param xsmodel Schema model
    * @param ns Target Namespace
    * @return URL of the temp file where the schema exists
    * @throws IOException
    */
   public URL serialize( XSModel xsmodel, String ns)
   throws IOException
   {
      if(ns == null)
         throw new IllegalArgumentException("Illegal Null Argument:ns");
      String xsdString = "";
      if(xsmodel instanceof JBossXSModel)
      {
         JBossXSModel jbxs = (JBossXSModel)xsmodel;
         xsdString = jbxs.serialize();
      }
      else
      {
         //       Serialize XSD model
         StringWriter strwr = new StringWriter();
         sutils.serialize(xsmodel, strwr);
         xsdString = strwr.toString();
      }
      
      log.trace("serialize:\n" + xsdString); 
      
      // Write updated xsd file
      File xsdFile = SchemaUtils.getSchemaTempFile(ns);
      FileWriter writer = new FileWriter(xsdFile);
      try
      {
         writer.write(xsdString);
      }
      finally
      {
         writer.close();
      }
      return xsdFile.toURL(); 
   }
   
   /**
    * Serialize the schema into a output stream
    * @param xsmodel Schema model
    * @param ns Target Namespace
    * @param os OutputStream to write into 
    * @throws IOException
    */
   public void serialize( XSModel xsmodel, String ns, OutputStream os)
   throws IOException
   {
      // Serialize XSD model
      StringWriter strwr = new StringWriter();
      sutils.serialize(xsmodel, strwr);
      String xsdString = strwr.toString();
      log.trace("serialize:" + xsdString); 
      
      os.write(xsdString.getBytes()); 
   } 
}
