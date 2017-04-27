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
package org.jboss.test.ws.jaxws.jbws2011;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.jboss.ws.Constants;
import org.jboss.ws.core.StubExt;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * http://jira.jboss.org/jira/browse/JBWS-2011
 *
 * @author alessio.soldano@jboss.com
 * @since 03-10-2008
 */
public class CRLFTestCase extends JBossWSTest
{
   private String TARGET_ENDPOINT_ADDRESS = "http://" + getServerHost() + ":8080/jaxws-jbws2011";

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(CRLFTestCase.class, "jaxws-jbws2011-client.jar,jaxws-jbws2011.jar");
   }

   public void testCRLFOne() throws Exception
   {
      Hello port = getPort();
      Dto dto = new Dto();
      dto.setPar1("first\rparameter");
      dto.setPar2("second\nparameter");
      ((BindingProvider)port).getRequestContext().put(Constants.DOM_CONTENT_CANONICAL_NORMALIZATION, true);
      Dto result = port.echo(dto);
      assertEquals(dto.getPar1(), result.getPar1());
      assertEquals(dto.getPar2(), result.getPar2());
      try
      {
         ((BindingProvider)port).getRequestContext().put(Constants.DOM_CONTENT_CANONICAL_NORMALIZATION, false);
         result = port.echo(dto);
         fail();
      }
      catch (Exception e)
      {
         //OK
      }
   }

   public void testCRLFTwo() throws Exception
   {
      Hello port = getPort();
      Dto dto = new Dto();
      dto.setPar1("first\r\nparameter");
      dto.setPar2("second\n\rparameter");
      Dto result;
      try
      {
         result = port.echo(dto);
         fail();
      }
      catch (Exception e)
      {
         //OK
      }
      try
      {
         ((BindingProvider)port).getRequestContext().put(Constants.DOM_CONTENT_CANONICAL_NORMALIZATION, true);
         result = port.echo(dto);
         assertEquals(dto.getPar1(), result.getPar1());
         assertEquals(dto.getPar2(), result.getPar2());
      }
      catch (Exception e)
      {
         fail();
      }
   }
   
   public void testCRs() throws Exception
   {
      Hello port = getPort();
      Dto dto = new Dto();
      dto.setPar1("first\rparameter");
      dto.setPar2("second\r\rparameter");
      Dto result;
      try
      {
         result = port.echo(dto);
         fail();
      }
      catch (Exception e)
      {
         //OK
      }
      try
      {
         ((BindingProvider)port).getRequestContext().put(Constants.DOM_CONTENT_CANONICAL_NORMALIZATION, true);
         result = port.echo(dto);
         assertEquals(dto.getPar1(), result.getPar1());
         assertEquals(dto.getPar2(), result.getPar2());
      }
      catch (Exception e)
      {
         fail();
      }
   }
   
   public void testLFs() throws Exception
   {
      Hello port = getPort();
      Dto dto = new Dto();
      dto.setPar1("first\nparameter");
      dto.setPar2("second\n\nparameter");
      Dto result = port.echo(dto);
      assertEquals(dto.getPar1(), result.getPar1());
      assertEquals(dto.getPar2(), result.getPar2());
   }

   private Hello getPort() throws Exception
   {
      URL wsdlURL = new URL(TARGET_ENDPOINT_ADDRESS + "?wsdl");
      QName serviceName = new QName("http://org.jboss.ws/jbws2011", "HelloService");
      Hello port = Service.create(wsdlURL, serviceName).getPort(Hello.class);
      URL securityURL = getResourceURL("jaxws/jbws2011/META-INF/jboss-wsse-client.xml");
      ((StubExt)port).setSecurityConfig(securityURL.toExternalForm());
      ((StubExt)port).setConfigName("Standard WSSecurity Client");
      return port;
   }
}
