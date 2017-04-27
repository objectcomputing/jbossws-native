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
package org.jboss.test.ws.jaxrpc.xop;

import java.io.File;

import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.ws.core.jaxrpc.handler.SOAPMessageContextJAXRPC;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.extensions.xop.XOPContext;
import org.jboss.ws.extensions.xop.jaxrpc.XOPScanner;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsdl.xmlschema.WSSchemaUtils;
import org.jboss.ws.metadata.wsdl.xsd.SchemaUtils;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.xb.binding.NamespaceRegistry;

/**
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Jun 9, 2006
 */
public class XOPTypeDefTestCase extends JBossWSTest
{
   public void testCircularReferences() throws Exception
   {
      SchemaUtils utils = SchemaUtils.getInstance();
      File f = getResourceFile("jaxrpc/xop/circular.xsd");
      assertTrue("Unable to load schema file " + f.getAbsolutePath(), f.exists());

      XSModel xsModel = utils.parseSchema(f.toURL());
      assertNotNull(xsModel);
      WSSchemaUtils wsUtil = WSSchemaUtils.getInstance(new NamespaceRegistry(), "http://complex.jsr181.jaxws.ws.test.jboss.org/jaws");
      JBossXSModel schemaModel = wsUtil.getJBossXSModel(xsModel);

      XSTypeDefinition xsType = schemaModel.getTypeDefinition("Customer", "http://complex.jsr181.jaxws.ws.test.jboss.org/jaws");

      assertNotNull("Root type def not found", xsType);
      XOPScanner scanner = new XOPScanner();

      if (xsType instanceof XSComplexTypeDefinition)
      {
         XSComplexTypeDefinition xsComplexType = (XSComplexTypeDefinition)xsType;
         XSTypeDefinition resultType = scanner.findXOPTypeDef(xsComplexType);

         // it fails when getting a stack overflow ;)
      }
   }

   public void testXOPElementScan() throws Exception
   {
      SchemaUtils utils = SchemaUtils.getInstance();
      File f = getResourceFile("jaxrpc/xop/schema.xsd");
      assertTrue("Unable to load schema file " + f.getAbsolutePath(), f.exists());

      XSModel xsModel = utils.parseSchema(f.toURL());
      assertNotNull(xsModel);
      WSSchemaUtils wsUtil = WSSchemaUtils.getInstance(new NamespaceRegistry(), "http://jboss.org/test/ws/xop/doclit");
      JBossXSModel schemaModel = wsUtil.getJBossXSModel(xsModel);

      // test custom binary declaration
      XSTypeDefinition xsType = schemaModel.getTypeDefinition(">PingMsg", "http://jboss.org/test/ws/xop/doclit");

      assertNotNull("Root type def not found", xsType);
      XOPScanner scanner = new XOPScanner();

      if (xsType instanceof XSComplexTypeDefinition)
      {
         XSComplexTypeDefinition xsComplexType = (XSComplexTypeDefinition)xsType;
         XSTypeDefinition resultType = scanner.findXOPTypeDef(xsComplexType);
         assertNotNull("Unable to find xop typedef in schema", resultType);
      }

      scanner.reset();

      // test the xmime binary declaration
      xsType = schemaModel.getTypeDefinition(">PingMsgResponse", "http://jboss.org/test/ws/xop/doclit");
      assertNotNull("Root type def not found", xsType);
      if (xsType instanceof XSComplexTypeDefinition)
      {
         XSComplexTypeDefinition xsComplexType = (XSComplexTypeDefinition)xsType;
         XSTypeDefinition resultType = scanner.findXOPTypeDef(xsComplexType);
         assertNotNull("Unable to find XOP typedef in schema", resultType);
      }

   }

   public void testMSFTElementScan() throws Exception
   {

      SchemaUtils utils = SchemaUtils.getInstance();
      File f = getResourceFile("jaxrpc/xop/schema.xsd");
      assertTrue("Unable to load schema file " + f.getAbsolutePath(), f.exists());

      XSModel xsModel = utils.parseSchema(f.toURL());
      assertNotNull(xsModel);
      WSSchemaUtils wsUtil = WSSchemaUtils.getInstance(new NamespaceRegistry(), "http://jboss.org/test/ws/xop/doclit");
      JBossXSModel schemaModel = wsUtil.getJBossXSModel(xsModel);

      XSTypeDefinition xsType = schemaModel.getTypeDefinition(">MSFTBinary", "http://jboss.org/test/ws/xop/doclit");

      assertNotNull("Root type def not found", xsType);
      XOPScanner scanner = new XOPScanner();

      if (xsType instanceof XSComplexTypeDefinition)
      {
         XSComplexTypeDefinition xsComplexType = (XSComplexTypeDefinition)xsType;
         XSTypeDefinition resultType = scanner.findXOPTypeDef(xsComplexType);
         assertNotNull("Unable to find xop typedef in schema", resultType);
      }
   }

   public void testXOPContext()
   {
      SOAPMessageContextJAXRPC messageContext = new SOAPMessageContextJAXRPC();
      try
      {
         MessageContextAssociation.pushMessageContext(messageContext);
         assertFalse("MTOM should be disabled", XOPContext.isMTOMEnabled());
      }
      finally
      {
         MessageContextAssociation.popMessageContext();
      }
   }
}
