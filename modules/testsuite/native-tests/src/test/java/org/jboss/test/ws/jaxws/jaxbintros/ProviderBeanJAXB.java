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
package org.jboss.test.ws.jaxws.jaxbintros;

import org.jboss.logging.Logger;
import org.jboss.ws.core.jaxws.JAXBContextFactory;
import org.jboss.wsf.common.DOMWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;

/**
 * Test the JAXBBindingCustomization that rely on JAXBIntrodcutions
 *
 * @author heiko.braun@jboss.com 
 */
@WebServiceProvider(serviceName = "ProviderService", portName = "ProviderPort", targetNamespace = "http://org.jboss.ws/provider", wsdlLocation = "WEB-INF/wsdl/Provider.wsdl")
@ServiceMode(value = Service.Mode.PAYLOAD)
public class ProviderBeanJAXB implements Provider<Source>
{
   // provide logging
   private static Logger log = Logger.getLogger(ProviderBeanJAXB.class);

   public Source invoke(Source request)
   {
      try
      {
         DOMSource dsource = (DOMSource)request;
         String payload = DOMWriter.printNode(dsource.getNode(), true);

         System.out.println("Payload: \n" + payload);
         
         // defaults to CustomizableJAXBContextFactory, which takes BindingCustomizations into consideration
         JAXBContext jc = JAXBContextFactory.newInstance().createContext(new Class[] { UserType.class });
         Unmarshaller unmarshaller = jc.createUnmarshaller();
         JAXBElement jbe = unmarshaller.unmarshal(request, UserType.class);
         UserType user = (UserType)jbe.getValue();

         log.info("[string=" + user.getString() + ",qname=" + user.getQname() + "]");

         return new JAXBSource(jc, user);
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception e)
      {
         throw new WebServiceException(e);
      }
   }
}
