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
package org.jboss.ws.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.impl.xs.XSModelImpl;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.XSModel;
import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.utils.JBossWSEntityResolver;
import org.jboss.ws.core.utils.ResourceURL;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSEntityResolver;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSErrorHandler;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsdl.xmlschema.WSSchemaUtils;
import org.jboss.ws.metadata.wsdl.xsd.SchemaUtils;
import org.jboss.ws.tools.helpers.JavaToXSDHelper;
import org.jboss.ws.tools.interfaces.JavaToXSDIntf;
import org.jboss.ws.tools.interfaces.SchemaCreatorIntf;
import org.jboss.xb.binding.sunday.unmarshalling.LSInputAdaptor;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;

/**
 * <P>
 *  Handles the conversion of Java classes to XML Schema
 *  This class is the key class to use for all Java to Schema requirements.
 *  </P>
 *  <P>
 *  Approach 1: Starting from scratch.
 *  You can generate an empty schema model by providing a target namespace.
 *  To this empty schema model, Complex Types and Global Elements can be added.
 *   <br>{@link #createSchema(String typens)  createSchema}
 *  </P>
 *  <P>
 *  Approach 2: You want to generate a complex type as a string given a xmltype and a Java Class.
 *  <br>{@link #generateForSingleType(QName xmlType, Class javaType) generateForSingleType}
 *  <br>{@link #generateForEndpoint(Class endpointOrServiceInt)  generateForEndpoint}
 * </P>
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   May 6, 2005
 */
public class JavaToXSD implements JavaToXSDIntf
{
   // provide logging
   private static final Logger log = Logger.getLogger(JavaToXSD.class);

   protected WSDLUtils utils = WSDLUtils.getInstance();
   protected SchemaUtils schemautils = SchemaUtils.getInstance();
   protected String jaxrpcAssert = "JAXRPC2.0 Assertion:";

   protected JavaToXSDHelper helper = null;

   /**
    * Constructor
    */
   public JavaToXSD()
   {
      helper = new JavaToXSDHelper();
      SchemaCreatorIntf creator = helper.getSchemaCreator();
      JBossXSModel xsmodel = creator.getXSModel();
      if (xsmodel == null)
         creator.setXSModel(new JBossXSModel());
   }

   /*
    * @see org.jboss.ws.tools.interfaces.JavaToXSDIntf#generateForSingleType()
    */
   public JBossXSModel generateForSingleType(QName xmlType, Class javaType) throws IOException
   {
      SchemaCreatorIntf creator = helper.getSchemaCreator();
      creator.generateType(xmlType, javaType);
      return creator.getXSModel();
   }

   public JBossXSModel generateForSingleType(QName xmlType, Class javaType, Map<String, QName> elementNames) throws IOException
   {
      SchemaCreatorIntf creator = helper.getSchemaCreator();
      creator.generateType(xmlType, javaType, elementNames);
      return creator.getXSModel();
   }

   /*
    * @see org.jboss.ws.tools.interfaces.JavaToXSDIntf#getSchemaCreator()
    */
   public SchemaCreatorIntf getSchemaCreator()
   {
      return helper.getSchemaCreator();
   }

   /**
    * Given a schema file in a file, return a Schema Model
    * @param xsdURL Location of the schema file
    * @return Xerces XSModel which represents the schema
    */
   public JBossXSModel parseSchema(URL xsdURL)
   {
      JBossXSErrorHandler xserr = new JBossXSErrorHandler();
      JBossWSEntityResolver resolver = new JBossWSEntityResolver();
      JBossXSEntityResolver xsresolve = new JBossXSEntityResolver(resolver, new HashMap<String, URL>());
      XMLSchemaLoader loader = (XMLSchemaLoader)schemautils.getXSLoader(xserr, xsresolve);

      XSModel xsmodel = loader.loadURI(xsdURL.toExternalForm());
      if (xsmodel == null)
         throw new WSException("Cannot load schema: " + xsdURL);

      WSSchemaUtils sutils = WSSchemaUtils.getInstance(null, null);
      JBossXSModel jbxs = new JBossXSModel();
      sutils.copyXSModel(xsmodel, jbxs);
      return jbxs;
   }

   /**
    * Given a set of schema files, parse them to yield an unified JBossXSModel
    * @param locs a map of schema namespace to schema location
    * @return unified JBossXSModel
    */
   public JBossXSModel parseSchema(Map<String, URL> locs)
   {
      if (locs == null || locs.size() == 0)
         throw new IllegalArgumentException("Illegal schema location map");

      JBossXSErrorHandler xserr = new JBossXSErrorHandler();
      JBossWSEntityResolver resolver = new JBossWSEntityResolver();
      JBossXSEntityResolver xsresolve = new JBossXSEntityResolver(resolver, locs);
      XMLSchemaLoader loader = (XMLSchemaLoader)schemautils.getXSLoader(xserr, xsresolve);

      int index = 0;
      SchemaGrammar[] gs = new SchemaGrammar[locs.size()];
      Iterator<String> it = locs.keySet().iterator();
      while (it.hasNext())
      {
         InputStream in = null;
         try
         {
            String nsURI = it.next();
            URL orgURL = locs.get(nsURI); 
            URL resURL = resolveNamespaceURI(resolver, nsURI);
            URL url = resURL != null ? resURL : orgURL;
            
            log.debug("Load schema: " + nsURI + "=" + url);
            XMLInputSource inputSource = new XMLInputSource(null, url.toExternalForm(), null);

            InputSource tmpSrc = resolver.resolveEntity(null, url.toExternalForm());
            in = tmpSrc.getByteStream() != null ? tmpSrc.getByteStream() : new ResourceURL(url).openStream();
            inputSource.setByteStream(in);
            
            SchemaGrammar grammar = (SchemaGrammar)loader.loadGrammar(inputSource);
            if (grammar == null)
               throw new IllegalStateException("Cannot load grammar: " + url);
            
            gs[index++] = grammar;
         }
         catch (RuntimeException rte)
         {
            throw rte;
         }
         catch (Exception ex)
         {
            throw new IllegalStateException("Cannot parse schema", ex);
         }
         finally
         {
            if (in != null)
            {
               try
               {
                  in.close();
               }
               catch (IOException ioe)
               {
                  log.warn(ioe.getMessage(), ioe);
               }
            }
         }
      }
      XSModel xsmodel = new XSModelImpl(gs);

      // Convert Xerces XSModel into r/w JBossXSModel 
      WSSchemaUtils sutils = WSSchemaUtils.getInstance(null, null);
      JBossXSModel jbxs = new JBossXSModel();
      sutils.copyXSModel(xsmodel, jbxs);

      return jbxs;
   }

   private URL resolveNamespaceURI(JBossWSEntityResolver resolver, String nsURI)
   {
      URL url = null;

      String resource = (String)resolver.getEntityMap().get(nsURI);
      if (resource != null)
      {
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         url = loader.getResource(resource);
         if (url == null)
         {
            if (resource.endsWith(".dtd"))
               resource = "dtd/" + resource;
            else if (resource.endsWith(".xsd"))
               resource = "schema/" + resource;
            url = loader.getResource(resource);
         }
      }

      return url;
   }

   /**
    * @see org.jboss.ws.tools.interfaces.JavaToXSDIntf#setPackageNamespaceMap(java.util.Map)
    */
   public void setPackageNamespaceMap(Map<String, String> map)
   {
      helper.setPackageNamespaceMap(map);
   }

   /**
    * Set the WSDL Style
    */
   public void setWSDLStyle(String style)
   {
      helper.setWsdlStyle(style);
   }

}
