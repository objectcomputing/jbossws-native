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
package org.jboss.test.ws.jaxrpc.wsdlpublish;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.tools.wsdl.WSDLDefinitionsFactory;

public class TestEndpointImpl implements TestEndpoint
{
   private Logger log = Logger.getLogger(TestEndpointImpl.class);

   public String echoSimple(String wsdlPath)
   {
      log.info("echoSimple: " + wsdlPath);
      
      // Parse the wsdl from the expected publish location
      // Append the portType name to the return string
      QName portType = null;
      try
      {
         URL wsdlURL = new File(wsdlPath).toURL();
         WSDLDefinitions wsdl = WSDLDefinitionsFactory.newInstance().parse(wsdlURL);
         portType = wsdl.getInterfaces()[0].getName();
      }
      catch (Exception ex)
      {
         WSException.rethrow(ex);
      }
      
      return portType.toString();
   }
}
