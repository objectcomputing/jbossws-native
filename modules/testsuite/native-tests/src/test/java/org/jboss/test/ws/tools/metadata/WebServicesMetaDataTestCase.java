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
package org.jboss.test.ws.tools.metadata;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.xml.namespace.QName;

import org.jboss.wsf.spi.metadata.webservices.PortComponentMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebserviceDescriptionMetaData;
import org.jboss.wsf.spi.metadata.webservices.WebservicesFactory;
import org.jboss.wsf.spi.metadata.webservices.WebservicesMetaData;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.xb.binding.ObjectModelFactory;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.w3c.dom.Element;

/**
 *  Testcase that tests the construction of WebServicesMetaData
 *  that represents webservices.xml and its serialization
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 10, 2005
 */
public class WebServicesMetaDataTestCase extends JBossWSTest
{
   public void testWebServicesMetaDataRead() throws Exception
   {
      URL webservicesURL = getResourceURL("tools/metadatafixture/webservices.xml"); 
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
      assertNotNull("WebServicesMetaData is null?",webservices);
   }
   
   public void testWebServicesMetaDataWrite() throws Exception
   {
      URL webservicesURL = getResourceURL("tools/metadatafixture/webservices.xml"); 
      // Unmarshall webservices.xml
      WebservicesMetaData webservices = constructWSMetaData(); 
      assertNotNull("WebServicesMetaData is null?",webservices);
      String wmdata = webservices.serialize();
      Element exp = DOMUtils.parse(webservicesURL.openStream());
      Element act = DOMUtils.parse(wmdata);
      assertEquals(exp,act);
   }
   
   private WebservicesMetaData constructWSMetaData()
   {
      WebservicesMetaData wm = new WebservicesMetaData(null);
      WebserviceDescriptionMetaData wsdm = new WebserviceDescriptionMetaData(wm);
      populateWebserviceDescriptionMetaData(wsdm);
      wm.addWebserviceDescription(wsdm);
      return wm; 
   }
   
   private void populateWebserviceDescriptionMetaData(WebserviceDescriptionMetaData wsdm)
   {
      wsdm.setWebserviceDescriptionName("HelloWsService");
      wsdm.setWsdlFile("WEB-INF/wsdl/HelloService.wsdl");
      wsdm.setJaxrpcMappingFile("WEB-INF/jaxrpc-mapping.xml");
      //create 1 of 4 PortComponents
      PortComponentMetaData pm1 = new PortComponentMetaData(wsdm);
      pm1.setPortComponentName("ValidURL"); 
      pm1.setWsdlPort(new QName("http://test.jboss.org/ws4eesimple",
            "ValidURLPort","impl") );
      pm1.setServiceEndpointInterface("org.jboss.test.webservice.ws4eesimple.HelloWs");
      pm1.setServletLink("HelloJavaBean");
      wsdm.addPortComponent(pm1);

      //create 2 of 4 PortComponents
      PortComponentMetaData pm2 = new PortComponentMetaData(wsdm);
      pm2.setPortComponentName("InvalidURL"); 
      pm2.setWsdlPort(new QName("http://test.jboss.org/ws4eesimple",
            "InvalidURLPort","impl") );
      pm2.setServiceEndpointInterface("org.jboss.test.webservice.ws4eesimple.HelloWs");
      pm2.setServletLink("HelloJavaBean");
      wsdm.addPortComponent(pm2);
      
      //create 3 of 4 PortComponents
      PortComponentMetaData pm3 = new PortComponentMetaData(wsdm);
      pm3.setPortComponentName("ValidSecureURL"); 
      pm3.setWsdlPort(new QName("http://test.jboss.org/ws4eesimple",
            "ValidSecureURLPort","impl") );
      pm3.setServiceEndpointInterface("org.jboss.test.webservice.ws4eesimple.HelloWs");
      pm3.setServletLink("HelloJavaBean");
      wsdm.addPortComponent(pm3);
      
      //    create 4 of 4 PortComponents
      PortComponentMetaData pm4 = new PortComponentMetaData(wsdm);
      pm4.setPortComponentName("InvalidSecureURL"); 
      pm4.setWsdlPort(new QName("http://test.jboss.org/ws4eesimple",
            "InvalidSecureURLPort","impl") );
      pm4.setServiceEndpointInterface("org.jboss.test.webservice.ws4eesimple.HelloWs");
      pm4.setServletLink("HelloJavaBean");
      wsdm.addPortComponent(pm4);
   }

}
