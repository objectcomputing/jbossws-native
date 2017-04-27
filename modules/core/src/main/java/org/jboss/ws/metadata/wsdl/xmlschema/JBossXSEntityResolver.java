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
package org.jboss.ws.metadata.wsdl.xmlschema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.core.utils.ResourceURL;
import org.jboss.wsf.common.IOUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *  Entity Resolver for the default Xerces Reference parser
 *  @author <a href="mailto:Anil.Saldhana@jboss.org">Anil Saldhana</a>
 *  @author Thomas.Diesler@jboss.org
 *  @since  12-Aug-2005
 */
public class JBossXSEntityResolver implements XMLEntityResolver
{
   // provide logging
   private static final Logger log = Logger.getLogger(JBossXSEntityResolver.class);

   private Map<String, URL> schemaLocationByNamespace = new HashMap<String, URL>();

   private EntityResolver delegate;

   public JBossXSEntityResolver(EntityResolver resolver, Map<String, URL> schemaLocationByNamespace)
   {
      this.schemaLocationByNamespace = schemaLocationByNamespace;
      this.delegate = resolver;
   }

   /**
    * Resolves an external parsed entity. If the entity cannot be
    * resolved, this method should return null.
    */
   public XMLInputSource resolveEntity(XMLResourceIdentifier resId) throws XNIException, IOException
   {
      log.trace("Resolve entity: " + resId);

      // First try the JBossEntityResolver
      String publicId = resId.getPublicId();
      String systemId = resId.getLiteralSystemId();
      String namespace = resId.getNamespace();
      try
      {
         String publicURI = (publicId != null ? publicId : namespace);
         InputSource inputSource = delegate.resolveEntity(publicURI, systemId);
         if (inputSource != null)
         {
            XMLInputSource source = getXMLInputSource(inputSource, resId);
            return source;
         }
      }
      catch (Exception ex)
      {
         log.trace(ex);
      }

      try
      {
         String expandedSysId = resId.getExpandedSystemId();
         if (expandedSysId != null)
         {
            log.trace("Use ExpandedSystemId: " + expandedSysId);
            return getXMLInputSource(new URL(expandedSysId), resId);
         }
      }
      catch (IOException e)
      {
         log.trace(e);
      }

      try
      {
         if (systemId != null)
         {
            log.trace("Use LiteralSystemId: " + systemId);
            return getXMLInputSource(new URL(systemId), resId);
         }
      }
      catch (IOException e)
      {
         log.trace(e);
      }

      // in case of a DOCTYPE declaration ew refer to systemId
      String namespaceURI = resId.getNamespace() != null ? resId.getNamespace() : resId.getLiteralSystemId();

      // The schema parser will obviously know this schema already
      if (Constants.NS_SCHEMA_XSD.equals(namespaceURI))
         return null;

      try
      {
         URL url = schemaLocationByNamespace.get(namespaceURI);
         if (url != null)
         {
            log.trace("Use SchemaLocationByNamespace: " + url);
            return getXMLInputSource(url, resId);
         }

         // Delegate to JBoss Entity Resolver
         XMLInputSource source = getXMLInputSource(delegate.resolveEntity(null, namespaceURI), resId);
         if (source != null)
            return source;
      }
      catch (SAXException e)
      {
         log.trace(e);
      }

      try
      {
         log.trace("Use NamespaceURI: " + namespaceURI);
         return getXMLInputSource(new URL(namespaceURI), resId);
      }
      catch (IOException e)
      {
         log.trace(e);
      }

      log.trace("Cannot obtain XMLInputSource for: " + resId);
      return null;
   }

   private XMLInputSource getXMLInputSource(URL url, XMLResourceIdentifier resId) throws IOException
   {
      InputStream urlStream = new ResourceURL(url).openStream();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      IOUtils.copyStream(baos, urlStream); // [JBWS-2325]
      InputSource inputSource = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
      return getXMLInputSource(inputSource, resId);
   }

   private XMLInputSource getXMLInputSource(InputSource inputSource, XMLResourceIdentifier resId) throws IOException
   {
      String encoding = inputSource.getEncoding();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      IOUtils.copyStream(baos, inputSource.getByteStream()); // [JBWS-2325]
      InputStream is = new ByteArrayInputStream(baos.toByteArray());
      return new XMLInputSource(resId.getPublicId(), resId.getExpandedSystemId(), resId.getBaseSystemId(), is, encoding);
   }
}
