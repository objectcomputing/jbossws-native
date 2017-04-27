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
package org.jboss.test.ws.benchmark.jaxws;

import org.jboss.test.ws.benchmark.jaxws.doclit.BenchmarkService;
import junit.framework.Test;

import org.jboss.test.ws.benchmark.jaxws.doclit.*;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

import javax.xml.ws.Service;
import javax.xml.ws.BindingProvider;
import javax.xml.namespace.QName;
import java.net.URL;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Test Benchmark EJB Service
 *
 * @author anders.hedstrom@home.se
 * @since 9-Nov-2005
 */
public class BenchmarkDocJSETestCase extends JBossWSTest
{
   private static BenchmarkService endpoint;

   public static Test suite()
   {
      return new JBossWSTestSetup(BenchmarkDocJSETestCase.class, "jaxws-benchmark-doclit.war");
   }

   protected void setUp() throws Exception
   {
      super.setUp();

      if (endpoint == null)
      {
         URL wsdlLocation = getResourceURL("benchmark/jaxws/doclit/WEB-INF/wsdl/BenchmarkWebService.wsdl");
         Service service = Service.create(wsdlLocation, new QName("http://org.jboss.ws/benchmark", "BenchmarkWebService"));
         endpoint = service.getPort(BenchmarkService.class);
         ((BindingProvider)endpoint).getRequestContext().put(
            BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
               "http://"+getServerHost()+":8080/jaxws-benchmark-doclit/jse"
            );                                  
      }
   }

   public void testEchoSimpleType() throws Exception
   {
      SimpleUserType userType = createSimpleUserType();
      SimpleUserType retObj = endpoint.echoSimpleType(userType);
      assertEquals(userType.getS()+userType.getF()+userType.getI(), retObj.getS()+retObj.getF()+retObj.getI());
   }

   private SimpleUserType createSimpleUserType() {
      SimpleUserType userType = new SimpleUserType();
      userType.setF(0.99f);
      userType.setI(99);
      userType.setS("Hello World");
      return userType;
   }

   public void testEchoArrayOfSimpleUserType() throws Exception
   {
      List array = new ArrayList();
      SimpleUserType in = createSimpleUserType();
      array.add(in);
      List retObj = endpoint.echoArrayOfSimpleUserType(array);
      SimpleUserType out = (SimpleUserType)retObj.get(0);
      assertNotNull(out);
      assertEquals(out.getS(), in.getS());
   }

   public void testEchoSynthetic() throws Exception
   {
      //"test", createSimpleUserType(), "test".getBytes()
      Synthetic synthetic = new Synthetic();
      synthetic.setSut(createSimpleUserType());
      synthetic.setS("Hello World");
      synthetic.setB("Hello World".getBytes());
      Synthetic retObj = endpoint.echoSynthetic(synthetic);
      assertEquals(synthetic.getS()+synthetic.getSut().getS()+synthetic.getSut().getF()+synthetic.getSut().getI(), retObj.getS()+retObj.getSut().getS()+retObj.getSut().getF()+retObj.getSut().getI());
   }

   public void testGetOrder() throws Exception
   {
      Order order = endpoint.getOrder(50,1);
      assertEquals(50, order.getLineItems().size());
   }
}
