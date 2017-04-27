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
package org.jboss.test.ws.tools.utils;

import junit.framework.TestCase;

import org.jboss.ws.tools.NamespacePackageMapping;

/**
 *  Test case for the NamespacePackageMapping class.
 *  
 * @author darran.lofthouse@jboss.com
 * @since 18-Jul-2006
 */
public class NamespacePackageMappingTestCase extends TestCase
{

   public void testBadScheme()
   {
      try
      {
         NamespacePackageMapping.getJavaPackageName("ftp://www.jboss.org/schemas/schema.xsd");
         fail("Expected exception not thrown");
      }
      catch (Exception ignored)
      {
      }
   }

   /**
    * Test that the example in the JAXB specification can be converted correctly.
    */
   public void testJAXBExample()
   {
      assertEquals("com.acme.go.espeak", NamespacePackageMapping.getJavaPackageName("http://www.acme.com/go/espeak.xsd"));
   }

   public void testHTTPMappingsORG()
   {
      assertEquals("org.jboss", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org"));
      assertEquals("org.jboss", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/"));
      assertEquals("org.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/schema"));
      assertEquals("org.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/schema/"));
      assertEquals("org.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/schema.xsd"));
      assertEquals("org.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/schema.html"));
      assertEquals("org.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/schema.xf"));
      assertEquals("org.jboss.schema_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/schema.h"));
      assertEquals("org.jboss.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/schema.htmls"));
      assertEquals("org.jboss.schema.files", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/schema/files"));
      assertEquals("org.jboss.schema.files", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/schema/files/"));
      assertEquals("org.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/files/schema.xsd"));
      assertEquals("org.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/files/schema.html"));
      assertEquals("org.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/files/schema.xf"));
      assertEquals("org.jboss.files.schema_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/files/schema.h"));
      assertEquals("org.jboss.files.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/files/schema.htmls"));
   }

   public void testHTTPMappingsKeywords()
   {
      assertEquals("org.continue_", NamespacePackageMapping.getJavaPackageName("http://www.continue.org"));
      assertEquals("org.jboss.continue_", NamespacePackageMapping.getJavaPackageName("http://continue.jboss.org/"));
      assertEquals("org.jboss.continue_", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/continue"));
      assertEquals("org.jboss.continue_", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/continue/"));
      assertEquals("org.jboss.continue_", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/continue.xsd"));
      assertEquals("org.jboss.continue_", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/continue.html"));
      assertEquals("org.jboss.continue_", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/continue.xf"));
      assertEquals("org.jboss.continue_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/continue.h"));
      assertEquals("org.jboss.continue_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/continue.htmls"));
      assertEquals("org.jboss.continue_.files", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/continue/files"));
      assertEquals("org.jboss.continue_.files", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/continue/files/"));
      assertEquals("org.jboss.continue_.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/continue/schema.xsd"));
      assertEquals("org.jboss.continue_.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/continue/schema.html"));
      assertEquals("org.jboss.continue_.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/continue/schema.xf"));
      assertEquals("org.jboss.continue_.schema_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/continue/schema.h"));
      assertEquals("org.jboss.continue_.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/continue/schema.htmls"));
   }

   public void testHTTPMappingsIdentifier()
   {
      assertEquals("org.null_", NamespacePackageMapping.getJavaPackageName("http://www.null.org"));
      assertEquals("org.jboss.null_", NamespacePackageMapping.getJavaPackageName("http://null.jboss.org/"));
      assertEquals("org.jboss.null_", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/null"));
      assertEquals("org.jboss.null_", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/null/"));
      assertEquals("org.jboss.null_", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/null.xsd"));
      assertEquals("org.jboss.null_", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/null.html"));
      assertEquals("org.jboss.null_", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/null.xf"));
      assertEquals("org.jboss.null_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/null.h"));
      assertEquals("org.jboss.null_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/null.htmls"));
      assertEquals("org.jboss.null_.files", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/null/files"));
      assertEquals("org.jboss.null_.files", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/null/files/"));
      assertEquals("org.jboss.null_.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/null/schema.xsd"));
      assertEquals("org.jboss.null_.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/null/schema.html"));
      assertEquals("org.jboss.null_.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/null/schema.xf"));
      assertEquals("org.jboss.null_.schema_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/null/schema.h"));
      assertEquals("org.jboss.null_.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/null/schema.htmls"));
   }

   public void testHTTPMappingsLeadingDigit()
   {
      assertEquals("org._1jboss", NamespacePackageMapping.getJavaPackageName("http://www.1jboss.org"));
      assertEquals("org.jboss._1www", NamespacePackageMapping.getJavaPackageName("http://1www.jboss.org/"));
      assertEquals("org.jboss._1schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/1schema"));
      assertEquals("org.jboss._1schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/1schema/"));
      assertEquals("org.jboss._1schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/1schema.xsd"));
      assertEquals("org.jboss._1schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/1schema.html"));
      assertEquals("org.jboss._1schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/1schema.xf"));
      assertEquals("org.jboss._1schema_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/1schema.h"));
      assertEquals("org.jboss._1schema_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/1schema.htmls"));
      assertEquals("org.jboss._1schema.files", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/1schema/files"));
      assertEquals("org.jboss._1schema.files", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/1schema/files/"));
      assertEquals("org.jboss._1schema.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/1schema/schema.xsd"));
      assertEquals("org.jboss._1schema.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/1schema/schema.html"));
      assertEquals("org.jboss._1schema.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/1schema/schema.xf"));
      assertEquals("org.jboss._1schema.schema_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/1schema/schema.h"));
      assertEquals("org.jboss._1schema.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/1schema/schema.htmls"));
   }

   public void testHTTPMappingsBadCharacter()
   {
      assertEquals("org.jb_oss", NamespacePackageMapping.getJavaPackageName("http://www.jb*oss.org"));
      assertEquals("org.jboss.w_ww", NamespacePackageMapping.getJavaPackageName("http://w*ww.jboss.org/"));
      assertEquals("org.jboss.sc_hema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/sc*hema"));
      assertEquals("org.jboss.sc_hema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/sc*hema/"));
      assertEquals("org.jboss.sc_hema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/sc*hema.xsd"));
      assertEquals("org.jboss.sc_hema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/sc*hema.html"));
      assertEquals("org.jboss.sc_hema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/sc*hema.xf"));
      assertEquals("org.jboss.sc_hema_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/sc*hema.h"));
      assertEquals("org.jboss.sc_hema_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/sc*hema.htmls"));
      assertEquals("org.jboss.schema.fi_les", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/schema/fi*les"));
      assertEquals("org.jboss.schema.fi_les", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/schema/fi*les/"));
      assertEquals("org.jboss.fi_les.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/fi*les/schema.xsd"));
      assertEquals("org.jboss.fi_les.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/fi*les/schema.html"));
      assertEquals("org.jboss.fi_les.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/fi*les/schema.xf"));
      assertEquals("org.jboss.fi_les.schema_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/fi*les/schema.h"));
      assertEquals("org.jboss.fi_les.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.org/fi*les/schema.htmls"));
   }

   public void testURNMappingsORG()
   {
      assertEquals("org.jboss", NamespacePackageMapping.getJavaPackageName("urn:jboss-org"));
      assertEquals("org.jboss.schema", NamespacePackageMapping.getJavaPackageName("urn:jboss-org:schema"));
      assertEquals("org.jboss.schema.files", NamespacePackageMapping.getJavaPackageName("urn:jboss-org:schema:files"));
   }

   public void testHTTPMappingsORGNoWWW()
   {
      assertEquals("org.jboss", NamespacePackageMapping.getJavaPackageName("http://jboss.org"));
      assertEquals("org.jboss", NamespacePackageMapping.getJavaPackageName("http://jboss.org/"));
      assertEquals("org.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.org/schema"));
      assertEquals("org.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.org/schema/"));
      assertEquals("org.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.org/schema.xsd"));
      assertEquals("org.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.org/schema.html"));
      assertEquals("org.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.org/schema.xf"));
      assertEquals("org.jboss.schema_h", NamespacePackageMapping.getJavaPackageName("http://jboss.org/schema.h"));
      assertEquals("org.jboss.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://jboss.org/schema.htmls"));
      assertEquals("org.jboss.schema.files", NamespacePackageMapping.getJavaPackageName("http://jboss.org/schema/files"));
      assertEquals("org.jboss.schema.files", NamespacePackageMapping.getJavaPackageName("http://jboss.org/schema/files/"));
      assertEquals("org.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.org/files/schema.xsd"));
      assertEquals("org.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.org/files/schema.html"));
      assertEquals("org.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.org/files/schema.xf"));
      assertEquals("org.jboss.files.schema_h", NamespacePackageMapping.getJavaPackageName("http://jboss.org/files/schema.h"));
      assertEquals("org.jboss.files.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://jboss.org/files/schema.htmls"));
   }

   public void testHTTPMappingsDE()
   {
      assertEquals("de.jboss", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de"));
      assertEquals("de.jboss", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/"));
      assertEquals("de.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/schema"));
      assertEquals("de.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/schema/"));
      assertEquals("de.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/schema.xsd"));
      assertEquals("de.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/schema.html"));
      assertEquals("de.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/schema.xf"));
      assertEquals("de.jboss.schema_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/schema.h"));
      assertEquals("de.jboss.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/schema.htmls"));
      assertEquals("de.jboss.schema.files", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/schema/files"));
      assertEquals("de.jboss.schema.files", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/schema/files/"));
      assertEquals("de.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/files/schema.xsd"));
      assertEquals("de.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/files/schema.html"));
      assertEquals("de.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/files/schema.xf"));
      assertEquals("de.jboss.files.schema_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/files/schema.h"));
      assertEquals("de.jboss.files.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.de/files/schema.htmls"));
   }

   public void testHTTPMappingsNoWWW()
   {
      assertEquals("de.jboss", NamespacePackageMapping.getJavaPackageName("http://jboss.de"));
      assertEquals("de.jboss", NamespacePackageMapping.getJavaPackageName("http://jboss.de/"));
      assertEquals("de.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.de/schema"));
      assertEquals("de.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.de/schema/"));
      assertEquals("de.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.de/schema.xsd"));
      assertEquals("de.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.de/schema.html"));
      assertEquals("de.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.de/schema.xf"));
      assertEquals("de.jboss.schema_h", NamespacePackageMapping.getJavaPackageName("http://jboss.de/schema.h"));
      assertEquals("de.jboss.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://jboss.de/schema.htmls"));
      assertEquals("de.jboss.schema.files", NamespacePackageMapping.getJavaPackageName("http://jboss.de/schema/files"));
      assertEquals("de.jboss.schema.files", NamespacePackageMapping.getJavaPackageName("http://jboss.de/schema/files/"));
      assertEquals("de.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.de/files/schema.xsd"));
      assertEquals("de.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.de/files/schema.html"));
      assertEquals("de.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.de/files/schema.xf"));
      assertEquals("de.jboss.files.schema_h", NamespacePackageMapping.getJavaPackageName("http://jboss.de/files/schema.h"));
      assertEquals("de.jboss.files.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://jboss.de/files/schema.htmls"));
   }

   public void testHTTPMappingsCOM()
   {
      assertEquals("com.jboss", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com"));
      assertEquals("com.jboss", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/"));
      assertEquals("com.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/schema"));
      assertEquals("com.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/schema/"));
      assertEquals("com.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/schema.xsd"));
      assertEquals("com.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/schema.html"));
      assertEquals("com.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/schema.xf"));
      assertEquals("com.jboss.schema_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/schema.h"));
      assertEquals("com.jboss.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/schema.htmls"));
      assertEquals("com.jboss.schema.files", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/schema/files"));
      assertEquals("com.jboss.schema.files", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/schema/files/"));
      assertEquals("com.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/files/schema.xsd"));
      assertEquals("com.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/files/schema.html"));
      assertEquals("com.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/files/schema.xf"));
      assertEquals("com.jboss.files.schema_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/files/schema.h"));
      assertEquals("com.jboss.files.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.com/files/schema.htmls"));
   }

   public void testHTTPMappingsCOMNoWWW()
   {
      assertEquals("com.jboss", NamespacePackageMapping.getJavaPackageName("http://jboss.com"));
      assertEquals("com.jboss", NamespacePackageMapping.getJavaPackageName("http://jboss.com/"));
      assertEquals("com.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.com/schema"));
      assertEquals("com.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.com/schema/"));
      assertEquals("com.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.com/schema.xsd"));
      assertEquals("com.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.com/schema.html"));
      assertEquals("com.jboss.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.com/schema.xf"));
      assertEquals("com.jboss.schema_h", NamespacePackageMapping.getJavaPackageName("http://jboss.com/schema.h"));
      assertEquals("com.jboss.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://jboss.com/schema.htmls"));
      assertEquals("com.jboss.schema.files", NamespacePackageMapping.getJavaPackageName("http://jboss.com/schema/files"));
      assertEquals("com.jboss.schema.files", NamespacePackageMapping.getJavaPackageName("http://jboss.com/schema/files/"));
      assertEquals("com.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.com/files/schema.xsd"));
      assertEquals("com.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.com/files/schema.html"));
      assertEquals("com.jboss.files.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.com/files/schema.xf"));
      assertEquals("com.jboss.files.schema_h", NamespacePackageMapping.getJavaPackageName("http://jboss.com/files/schema.h"));
      assertEquals("com.jboss.files.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://jboss.com/files/schema.htmls"));
   }

   public void testHTTPMappingsZZZ()
   {
      assertEquals("www.jboss.zzz", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz"));
      assertEquals("www.jboss.zzz", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/"));
      assertEquals("www.jboss.zzz.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/schema"));
      assertEquals("www.jboss.zzz.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/schema/"));
      assertEquals("www.jboss.zzz.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/schema.xsd"));
      assertEquals("www.jboss.zzz.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/schema.html"));
      assertEquals("www.jboss.zzz.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/schema.xf"));
      assertEquals("www.jboss.zzz.schema_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/schema.h"));
      assertEquals("www.jboss.zzz.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/schema.htmls"));
      assertEquals("www.jboss.zzz.schema.files", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/schema/files"));
      assertEquals("www.jboss.zzz.schema.files", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/schema/files/"));
      assertEquals("www.jboss.zzz.files.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/files/schema.xsd"));
      assertEquals("www.jboss.zzz.files.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/files/schema.html"));
      assertEquals("www.jboss.zzz.files.schema", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/files/schema.xf"));
      assertEquals("www.jboss.zzz.files.schema_h", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/files/schema.h"));
      assertEquals("www.jboss.zzz.files.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://www.jboss.zzz/files/schema.htmls"));
   }

   public void testHTTPMappingsZZZNoWWW()
   {
      assertEquals("jboss.zzz", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz"));
      assertEquals("jboss.zzz", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/"));
      assertEquals("jboss.zzz.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/schema"));
      assertEquals("jboss.zzz.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/schema/"));
      assertEquals("jboss.zzz.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/schema.xsd"));
      assertEquals("jboss.zzz.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/schema.html"));
      assertEquals("jboss.zzz.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/schema.xf"));
      assertEquals("jboss.zzz.schema_h", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/schema.h"));
      assertEquals("jboss.zzz.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/schema.htmls"));
      assertEquals("jboss.zzz.schema.files", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/schema/files"));
      assertEquals("jboss.zzz.schema.files", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/schema/files/"));
      assertEquals("jboss.zzz.files.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/files/schema.xsd"));
      assertEquals("jboss.zzz.files.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/files/schema.html"));
      assertEquals("jboss.zzz.files.schema", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/files/schema.xf"));
      assertEquals("jboss.zzz.files.schema_h", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/files/schema.h"));
      assertEquals("jboss.zzz.files.schema_htmls", NamespacePackageMapping.getJavaPackageName("http://jboss.zzz/files/schema.htmls"));

   }

}
