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
package org.jboss.test.ws.tools.doclit;

import java.io.File;
import java.io.Writer;

import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.ws.Constants;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.tools.JavaToWSDL;
import org.jboss.ws.tools.WSToolsConstants;
import org.jboss.ws.tools.wsdl.WSDLWriter;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.IOUtils;
import org.w3c.dom.Element;

/**
 *  Test case that deals with the generation of artifacts
 *  for document literal web services
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 3, 2005
 */
public class ToolsDocLitTestCase extends WSToolsBase
{
   public void testTrivialCase() throws Exception
   {
      Class seiClass = TrivialService.class;
      String wsdlDir = createResourceFile("tools/").getAbsolutePath();
      String sname = "SampleService";
      String wsdlPath = wsdlDir+ "/" + sname + ".wsdl";
      JavaToWSDL jwsdl = new JavaToWSDL(Constants.NS_WSDL11);
      jwsdl.setServiceName(sname);
      jwsdl.setTargetNamespace("http://org.jboss.ws/samples");
      jwsdl.setTypeNamespace("http://org.jboss.ws/samples/types");
      jwsdl.setStyle(Style.DOCUMENT);
      jwsdl.addFeature(WSToolsConstants.WSTOOLS_FEATURE_RESTRICT_TO_TARGET_NS, true);
      WSDLDefinitions wsdl = jwsdl.generate(seiClass);

      Writer fw = IOUtils.getCharsetFileWriter(new File(wsdlPath), Constants.DEFAULT_XML_CHARSET);
      new WSDLWriter(wsdl).write(fw, Constants.DEFAULT_XML_CHARSET);
      fw.close();

      String fixturefile = getResourceFile("tools/doc-lit/trivial/wsdl/SampleService.wsdl").getAbsolutePath();
      //Validate the generated WSDL
      File wsdlfix = new File(fixturefile);
      Element exp = DOMUtils.parse(wsdlfix.toURL().openStream());
      File wsdlFile = new File(wsdlPath);
      assertNotNull("Generated WSDL File exists?", wsdlFile);
      Element was = DOMUtils.parse(wsdlFile.toURL().openStream());
      //assertEquals(exp, was);
      this.semanticallyValidateWSDL(fixturefile, wsdlFile.getPath());
   }
}
