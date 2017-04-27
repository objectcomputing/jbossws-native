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
package org.jboss.test.ws.jaxws.jbws2565;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * [JBWS-2565] Wrong WebContext authMethod or transportGuarantee annotatoin field values are not detected at deploy time.
 *  
 * @author richard.opalka@jboss.com
 */
public final class JBWS2565TestCase extends JBossWSTest
{
   
   public static Test suite()
   {
      return new JBossWSTestSetup(JBWS2565TestCase.class, "jaxws-jbws2565.ear");
   }

   public void test() throws Exception
   {
      final ServiceFactory factory = ServiceFactory.newInstance();
      final Service service = factory.createService(new QName("http://my.services.web", "MyWebServiceName"));
      final Call call = service.createCall(new QName("http://my.services.web", "MyWebServicePort"));
      call.setTargetEndpointAddress("http://" + getServerHost() + ":8080/jaxws-jbws2565/MyWebServiceBean?wsdl");
      call.setOperationName(new QName("http://my.services.web", "doStuff"));
      final QName QNAME_TYPE_STRING = new QName("http://www.w3.org/2001/XMLSchema", "string");
      call.setReturnType(QNAME_TYPE_STRING);
      final String[] serviceArgs = {};
      final String result = (String) call.invoke(serviceArgs);
      assertEquals("i've done stuff", result);
   }
   
   public void testWrongArchiveDeployment() throws Exception
   {
      try
      {
         this.deploy("jaxws-jbws2565-wrong.ear");
         fail("Deployment of this archive had to fail.");
      }
      catch (Exception ignore)
      {
         log.error("Expected exception caught:" + ignore.getMessage(), ignore);
      }
   }
   
}
