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
package org.jboss.test.ws.jaxrpc.jbws163;

import java.math.BigInteger;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;
import javax.xml.rpc.holders.BigIntegerHolder;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.LongHolder;
import javax.xml.rpc.holders.ShortHolder;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/** Test IN, INOUT, OUT of unsignedLong, etc.
 *
 * http://jira.jboss.com/jira/browse/JBWS-163
 *
 * @author Thomas.Diesler@jboss.org
 * @since 09-Jun-2005
 */
public class JBWS163TestCase extends JBossWSTest
{
   private static Hello hello;

   /** Deploy the test */
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS163TestCase.class, "jaxrpc-jbws163.war, jaxrpc-jbws163-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();

      if (hello == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/HelloService");
         hello = (Hello)service.getPort(Hello.class);
      }
   }

   public void testNonPositiveInteger() throws Exception
   {
      BigInteger argIN = new BigInteger("-100");
      BigIntegerHolder argINOUT = new BigIntegerHolder(new BigInteger("-200"));
      BigIntegerHolder argOUT = new BigIntegerHolder();
      hello.nonPositiveInteger(argIN, argINOUT, argOUT);
      assertEquals(argINOUT.value, argIN);
      assertEquals(argOUT.value, argIN);
   }

   public void testNegativeInteger() throws Exception
   {
      BigInteger argIN = new BigInteger("-100");
      BigIntegerHolder argINOUT = new BigIntegerHolder(new BigInteger("-200"));
      BigIntegerHolder argOUT = new BigIntegerHolder();
      hello.negativeInteger(argIN, argINOUT, argOUT);
      assertEquals(argINOUT.value, argIN);
      assertEquals(argOUT.value, argIN);
   }

   public void testNonNegativeInteger() throws Exception
   {
      BigInteger argIN = new BigInteger("100");
      BigIntegerHolder argINOUT = new BigIntegerHolder(new BigInteger("200"));
      BigIntegerHolder argOUT = new BigIntegerHolder();
      hello.nonNegativeInteger(argIN, argINOUT, argOUT);
      assertEquals(argINOUT.value, argIN);
      assertEquals(argOUT.value, argIN);
   }

   public void testUnsignedLong() throws Exception
   {
      BigInteger argIN = new BigInteger("100");
      BigIntegerHolder argINOUT = new BigIntegerHolder(new BigInteger("200"));
      BigIntegerHolder argOUT = new BigIntegerHolder();
      hello.unsignedLong(argIN, argINOUT, argOUT);
      assertEquals(argINOUT.value, argIN);
      assertEquals(argOUT.value, argIN);
   }

   public void testPositiveInteger() throws Exception
   {
      BigInteger argIN = new BigInteger("100");
      BigIntegerHolder argINOUT = new BigIntegerHolder(new BigInteger("200"));
      BigIntegerHolder argOUT = new BigIntegerHolder();
      hello.positiveInteger(argIN, argINOUT, argOUT);
      assertEquals(argINOUT.value, argIN);
      assertEquals(argOUT.value, argIN);
   }

   public void testUnsignedInt() throws Exception
   {
      long argIN = 100;
      LongHolder argINOUT = new LongHolder(200);
      LongHolder argOUT = new LongHolder();
      hello.unsignedInt(argIN, argINOUT, argOUT);
      assertEquals(argINOUT.value, argIN);
      assertEquals(argOUT.value, argIN);
   }

   public void testUnsignedShort() throws Exception
   {
      int argIN = 100;
      IntHolder argINOUT = new IntHolder(200);
      IntHolder argOUT = new IntHolder();
      hello.unsignedShort(argIN, argINOUT, argOUT);
      assertEquals(argINOUT.value, argIN);
      assertEquals(argOUT.value, argIN);
   }

   public void testUnsignedByte() throws Exception
   {
      short argIN = 100;
      ShortHolder argINOUT = new ShortHolder((short)200);
      ShortHolder argOUT = new ShortHolder();
      hello.unsignedByte(argIN, argINOUT, argOUT);
      assertEquals(argINOUT.value, argIN);
      assertEquals(argOUT.value, argIN);
   }
}
