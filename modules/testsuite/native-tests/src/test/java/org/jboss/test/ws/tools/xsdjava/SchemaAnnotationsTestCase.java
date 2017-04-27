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
package org.jboss.test.ws.tools.xsdjava;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.test.ws.tools.WSToolsBase;

/**
 *  Tests handling of schema annotations by JBossWS Tools
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 17, 2005
 */
public class SchemaAnnotationsTestCase extends WSToolsBase
{
   /**
    * Check XSD Annotation
    * @throws MalformedURLException 
    */
   public void testXSDAnnotations() throws MalformedURLException
   {
      String filename = "xsdAnnotation.xsd";
      File xsdFile = getResourceFile("tools/xsd/annotations/" + filename);  
      XSModel xsmodel = parseSchema(xsdFile.toURL());
      assertNotNull("XSModel is null?", xsmodel);
      XSObjectList xsobjlist = xsmodel.getAnnotations();
      assertNotNull("Schema Annotation is null?", xsobjlist);
      assertTrue("Schema Annotations length > 0", xsobjlist.getLength()>0);
      
      //Test Annotation at the Complex Type Level
      XSTypeDefinition xt = xsmodel.getTypeDefinition("USAddress","http://org.jboss.ws/types");
      assertTrue("USAddress is a complex type?",xt instanceof XSComplexTypeDefinition);
      XSComplexTypeDefinition xc = (XSComplexTypeDefinition)xt;
      XSObjectList xo = xc.getAnnotations();
      assertTrue("There is one annotation", xo.getLength() == 1);
      
      //Test Annotation at the Global Element Level
      XSElementDeclaration xe = xsmodel.getElementDeclaration("myAddress","http://org.jboss.ws/types");
      XSAnnotation xa = xe.getAnnotation();
      assertNotNull("Annotation at element level is not null?",xa);
   }
}
