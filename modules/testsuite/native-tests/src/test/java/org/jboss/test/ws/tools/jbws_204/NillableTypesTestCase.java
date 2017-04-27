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
package org.jboss.test.ws.tools.jbws_204;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.test.ws.tools.jbws_204.wscompile.nillabletypes.NillableType;
import org.jboss.ws.Constants;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsdl.xmlschema.WSSchemaUtils;
import org.jboss.ws.tools.JavaToXSD;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;


/**
 * JBWS 204: Java To XSD Test Collection
 * Tests conversion into xsd nillable types
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jun 30, 2005
 */

public class NillableTypesTestCase extends JBossWSTest
{
    public void testNillableTypes() throws Exception
    {
       String targetNS = "http://org.jboss.ws";
       JavaToXSD jxsd = new JavaToXSD();
       QName q = new QName(targetNS,"NillableType", Constants.PREFIX_TNS);
       XSTypeDefinition xst = jxsd.getSchemaCreator().generateType(q,NillableType.class);
       WSSchemaUtils sutils = WSSchemaUtils.getInstance(null, null);
       JBossXSModel xsmodel = sutils.createXSModel();
       xsmodel.addXSTypeDefinition(xst);
       StringWriter sw = new StringWriter();
       sutils.serialize(xsmodel,sw);
       File xsdFile = getResourceFile("tools/jbws-204/wscompile/nillable.xsd");
       checkXMLFiles(xsdFile.toURL(),sw.toString());
    }

    private void checkXMLFiles(URL exp,String actual) throws Exception
    {
       Element expEl = DOMUtils.parse(exp.openStream() );
       Element actEl = DOMUtils.parse(actual);
       assertEquals(expEl, actEl);
    }
}

