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
package org.jboss.test.ws.jaxrpc.jbws1384;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.Stub;

import junit.framework.Test;

import org.jboss.ws.core.jaxrpc.client.ServiceFactoryImpl;
import org.jboss.ws.tools.WSTools;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Attachment parts with doclit message
 *
 * http://jira.jboss.org/jira/browse/JBWS-1384
 *
 * @author Thomas.Diesler@jboss.org
 * @since 11-Jan-2007
 */
public class JBWS1384TestCase extends JBossWSTest
{
   private static TransmulatorInterface port;

   public static Test suite()
   {
      return new JBossWSTestSetup(JBWS1384TestCase.class, "jaxrpc-jbws1384.war");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (port == null)
      {
         ServiceFactoryImpl factory = (ServiceFactoryImpl)ServiceFactory.newInstance();
         URL wsdlURL = getResourceURL("jaxrpc/jbws1384/WEB-INF/wsdl/ExampleService.wsdl");
         URL mappingURL = getResourceURL("jaxrpc/jbws1384/WEB-INF/jaxrpc-mapping.xml");
         QName serviceName = new QName("http://org.jboss.test.webservice/samples2", "Gasherbrum");
         Service service = factory.createService(wsdlURL, serviceName , mappingURL);
         port = (TransmulatorInterface)service.getPort(TransmulatorInterface.class);
         ((Stub)port)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, "http://" + getServerHost() + ":8080/jaxrpc-jbws1384");
      }
   }

   public void testWsdlToJava() throws Exception
   {
      WSTools wstools = new WSTools();
      String configPath = getResourceFile("jaxrpc/jbws1384/wstools-config.xml").getPath();
      boolean ret = wstools.generate(configPath, "./wstools/jbws1384");
      assertTrue("wstools success", ret);
   }

   public void testEndpoint() throws Exception
   {
      /*
      StubExt stub = (StubExt)port;
      AttachmentPart part = stub.createAttachmentPart();
      part.setContent("attached-string", "text/plain");
      stub.addAttachmentPart(part);
      */
      String retStr = port.invokeAttach("user", "pass", "op", "<root/>", "attached-string");
      assertEquals("[user=user,pass=pass,op=op,xml=<root/>] attached-string", retStr);
   }
}
