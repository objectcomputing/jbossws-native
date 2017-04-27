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
package org.jboss.test.ws.jaxrpc.jbws732;

import javax.naming.InitialContext;
import javax.xml.rpc.Service;

import junit.framework.Test;

import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Empty unwrapped arrays incorrectly unmarshalled as a null value
 * 
 * http://jira.jboss.org/jira/browse/JBWS-732
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 * @since 08-Mar-2006
 */
public class JBWS732TestCase extends JBossWSTest
{
   private static WrappedEndpoint wrapped;
   private static BareEndpoint bare;

   public static Test suite()
   {
      return new JBossWSTestSetup(JBWS732TestCase.class, "jaxrpc-jbws732.war, jaxrpc-jbws732-client.jar");
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      if (wrapped == null || bare == null)
      {
         InitialContext iniCtx = getInitialContext();
         Service service = (Service)iniCtx.lookup("java:comp/env/service/jbws732/wrapped");
         wrapped = (WrappedEndpoint)service.getPort(WrappedEndpoint.class);

         service = (Service)iniCtx.lookup("java:comp/env/service/jbws732/bare");
         bare = (BareEndpoint)service.getPort(BareEndpoint.class);
      }
   }


   /* Wrapped Style, Unwrapped Arrays */
   public void testEchoUnwrappedArrayWrappedStyleElements() throws Exception
   {
      String[] inArr = {"one", "two"};
      String[] outArr = wrapped.echoStringArray(inArr);
      assertEquals(inArr, outArr);
   }

   public void testEchoUnwrappedArrayWrappedStyleEmpty() throws Exception
   {
      String[] inArr = {};
      String[] outArr = wrapped.echoStringArray(inArr);
      assertEquals(inArr, outArr);

   }
   public void testEchoUnwrappedArrayWrappedStyleNullElement() throws Exception
   {
      String[] outArr = wrapped.echoStringArray(new String[] {null});

      assertEquals(new String[] {null}, outArr);
   }

   public void testEchoUnwrappedArrayWrappedStyleNull() throws Exception
   {
      String[] outArr = wrapped.echoStringArray(null);

      // Since, tt is impossible to send an unwrapped array as null,
      // JAXB says this is semantically equivalent to inserting a null element in the collection
      assertEquals(new String[] {null}, outArr);
   }

   /* Wrapped Style, Wrapped Arrays */
   public void testEchoWrappedArrayWrappedStyleElements() throws Exception
   {
      String[] inArr = {"one", "two"};
      String[] outArr = wrapped.echoStringWrappedArray(inArr);
      assertEquals(inArr, outArr);
   }

   public void testEchoWrappedArrayWrappedStyleEmpty() throws Exception
   {
      String[] inArr = {};
      String[] outArr = wrapped.echoStringWrappedArray(inArr);
      assertEquals(inArr, outArr);
   }

   public void testEchoWrappedArrayWrappedStyleNullElement() throws Exception
   {
      String[] outArr = wrapped.echoStringWrappedArray(new String[] {null});

      assertEquals(new String[] {null}, outArr);
   }

   public void testEchoWrappedArrayWrappedStyleNull() throws Exception
   {
      String[] outArr = wrapped.echoStringWrappedArray(null);

      assertEquals(null, outArr);
   }


   /* Bare Style, Unwrapped Arrays */
   public void testEchoUnwrappedArrayBareStyleElements() throws Exception
   {
      String[] inArr = {"one", "two"};
      BareRequest request = new BareRequest();
      request.setStringArray(inArr);

      BareResponse response = bare.echoStringArray(request);
      assertEquals(inArr, response.getStringArray());
   }

   public void testEchoUnwrappedArrayBareStyleEmpty() throws Exception
   {
      BareRequest in = new BareRequest(new String[]{});
      BareResponse out = bare.echoStringArray(in);
      assertEquals(in.getStringArray(), out.getStringArray());
   }

   public void testEchoUnwrappedArrayBareStyleNullElement() throws Exception
   {
      BareRequest in = new BareRequest();
      in.setStringArray(new String[]{null});
      BareResponse out = bare.echoStringArray(in);

      assertEquals(new String[]{null}, out.getStringArray());
   }

   public void testEchoUnwrappedArrayBareStyleNull() throws Exception
   {
      BareRequest in = new BareRequest();
      in.setStringArray(null);
      BareResponse out = bare.echoStringArray(in);

      // Since, tt is impossible to send an unwrapped array as null,
      // JAXB says this is semantically equivalent to inserting a null element in the collection
      assertEquals(new String[]{null}, out.getStringArray());
   }


   /* Bare Style, Wrapped Arrays */
   public void testEchoWrappedArrayBareStyleElements() throws Exception
   {
      String[] inArr = {"one", "two"};
      BareWrappedArrayRequest request = new BareWrappedArrayRequest();
      request.setStringArray(inArr);

      BareWrappedArrayResponse response = bare.echoStringWrappedArray(request);
      assertEquals(inArr, response.getStringArray());
   }

   public void testEchoWrappedArrayBareStyleEmpty() throws Exception
   {
      BareWrappedArrayRequest in = new BareWrappedArrayRequest(new String[]{});
      BareWrappedArrayResponse out = bare.echoStringWrappedArray(in);
      assertEquals(in.getStringArray(), out.getStringArray());
   }

   public void testEchoWwrappedArrayBareStyleNullElement() throws Exception
   {
      BareWrappedArrayRequest in = new BareWrappedArrayRequest();
      in.setStringArray(new String[]{null});
      BareWrappedArrayResponse out = bare.echoStringWrappedArray(in);

      assertEquals(new String[]{null}, out.getStringArray());
   }

   public void testEchoWrappedArrayBareStyleNull() throws Exception
   {
      BareWrappedArrayRequest in = new BareWrappedArrayRequest();
      in.setStringArray(null);
      BareWrappedArrayResponse out = bare.echoStringWrappedArray(in);

      assertEquals(null, out.getStringArray());
   }
}
