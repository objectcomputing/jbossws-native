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
package org.jboss.test.ws.jaxws.wsdd;

import java.io.InputStream;
import java.net.URL;

import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData;
import org.jboss.wsf.spi.metadata.webservices.PortComponentMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebserviceDescriptionMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebservicesFactory;
import org.jboss.wsf.spi.metadata.webservices.WebservicesMetaData;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.xb.binding.ObjectModelFactory;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;

/**
 * Tets webservice.xml additions that are related to JAX-WS
 *
 * @author Heiko.Braun@jboss.org
 * @since Mar 12, 2007
 */
public class TestWSDDParser extends JBossWSTest
{

   public void testJAXWSElementParsing() throws Exception
   {
      URL webservicesURL = getResourceURL("jaxws/wsdd/WEB-INF/webservices.xml");
      // Unmarshall webservices.xml
      WebservicesMetaData webservices = null;
      InputStream is = webservicesURL.openStream();
      try
      {
         Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
         ObjectModelFactory factory = new WebservicesFactory(webservicesURL);
         webservices = (WebservicesMetaData)unmarshaller.unmarshal(is, factory, null);
      }
      finally
      {
         is.close();
      }

      assertNotNull(webservices);

      WebserviceDescriptionMetaData wsDesc = webservices.getWebserviceDescriptions()[0];
      assertNotNull(wsDesc);
      assertTrue(wsDesc.getPortComponents().length == 1);

      PortComponentMetaData portComp = wsDesc.getPortComponents()[0];
      assertNotNull(portComp);
      assertTrue(portComp.getHandlerChains().getHandlerChains().size() > 0);

      // wsdlService
      assertTrue(portComp.getWsdlService().getLocalPart().equals("WSDDEndpointImplService"));

      // mtom
      assertTrue(portComp.isEnableMtom());

      // handler chains
      UnifiedHandlerChainMetaData handlerChain = portComp.getHandlerChains().getHandlerChains().get(0);
      assertNotNull(handlerChain);
      assertTrue(((UnifiedHandlerMetaData)handlerChain.getHandlers().get(0)).getHandlerName().equals("CustomHandler"));

   }

}
