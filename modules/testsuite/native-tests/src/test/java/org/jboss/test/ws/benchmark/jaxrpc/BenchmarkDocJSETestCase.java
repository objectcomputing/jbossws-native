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
package org.jboss.test.ws.benchmark.jaxrpc;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;

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
        return new JBossWSTestSetup(BenchmarkDocJSETestCase.class, "jaxrpc-benchmark-doclit.war, jaxrpc-benchmark-doclit-client.jar");
    }

    protected void setUp() throws Exception
    {
        super.setUp();

       try
       {
          if (endpoint == null)
          {
              InitialContext iniCtx = getInitialContext("benchmark-client");
              Service service = (Service)iniCtx.lookup("java:comp/env/service/BenchmarkJSE");
              endpoint = (BenchmarkService)service.getPort(BenchmarkService.class);

             ((Stub)endpoint)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, "http://" + getServerHost() + ":8080/jaxrpc-benchmark-doc/jse");
          }
       }
       catch (NamingException e)
       {
          e.printStackTrace();
       }
       catch (ServiceException e)
       {
          e.printStackTrace();
       }
    }

    public void testEchoSimpleType() throws Exception
    {
        SimpleUserType userType = new SimpleUserType(1, 1.0f, "test");
        SimpleUserType retObj = endpoint.echoSimpleType(userType);
        assertEquals(userType.getS()+userType.getF()+userType.getI(), retObj.getS()+retObj.getF()+retObj.getI());
    }

    public void testEchoArrayOfSimpleUserType() throws Exception
    {
        SimpleUserType[] array = new SimpleUserType[1];
        array[0] = new SimpleUserType(1, 1.0f, "test");
        SimpleUserType[] retObj = endpoint.echoArrayOfSimpleUserType(array);
        assertEquals(array[0].getS()+array[0].getF()+array[0].getI(), retObj[0].getS()+retObj[0].getF()+retObj[0].getI());
    }

    public void testEchoSynthetic() throws Exception
    {
        Synthetic synthetic = new Synthetic("test", new SimpleUserType(1, 1.0f, "test"), "test".getBytes());
        Synthetic retObj = endpoint.echoSynthetic(synthetic);
        assertEquals(synthetic.getS()+synthetic.getSut().getS()+synthetic.getSut().getF()+synthetic.getSut().getI(), retObj.getS()+retObj.getSut().getS()+retObj.getSut().getF()+retObj.getSut().getI());
    }

    public void testGetOrder() throws Exception
    {
        Order order = endpoint.getOrder(50,1);
        assertEquals(50,order.getLineItems().length);
    }
}
