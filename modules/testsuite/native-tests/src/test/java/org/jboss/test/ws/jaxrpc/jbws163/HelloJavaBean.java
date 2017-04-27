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

import javax.xml.rpc.holders.BigIntegerHolder;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.LongHolder;
import javax.xml.rpc.holders.ShortHolder;

import org.jboss.logging.Logger;

public class HelloJavaBean implements Hello
{
   private Logger log = Logger.getLogger(HelloJavaBean.class);

   public void nonPositiveInteger(BigInteger argIN, BigIntegerHolder argINOUT, BigIntegerHolder argOUT)
   {
      log.info("nonPositiveInteger: [argIn=" + argIN + ",argINOUT=" + argINOUT.value + ",argOUT=" + argOUT.value + "]");
      argINOUT.value = argIN;
      argOUT.value = argIN;
   }

   public void negativeInteger(BigInteger argIN, BigIntegerHolder argINOUT, BigIntegerHolder argOUT)
   {
      log.info("negativeInteger: [argIn=" + argIN + ",argINOUT=" + argINOUT.value + ",argOUT=" + argOUT.value + "]");
      argINOUT.value = argIN;
      argOUT.value = argIN;
   }

   public void nonNegativeInteger(BigInteger argIN, BigIntegerHolder argINOUT, BigIntegerHolder argOUT)
   {
      log.info("nonNegativeInteger: [argIn=" + argIN + ",argINOUT=" + argINOUT.value + ",argOUT=" + argOUT.value + "]");
      argINOUT.value = argIN;
      argOUT.value = argIN;
   }

   public void unsignedLong(BigInteger argIN, BigIntegerHolder argINOUT, BigIntegerHolder argOUT)
   {
      log.info("unsignedLong: [argIn=" + argIN + ",argINOUT=" + argINOUT.value + ",argOUT=" + argOUT.value + "]");
      argINOUT.value = argIN;
      argOUT.value = argIN;
   }

   public void positiveInteger(BigInteger argIN, BigIntegerHolder argINOUT, BigIntegerHolder argOUT)
   {
      log.info("positiveInteger: [argIn=" + argIN + ",argINOUT=" + argINOUT.value + ",argOUT=" + argOUT.value + "]");
      argINOUT.value = argIN;
      argOUT.value = argIN;
   }

   public void unsignedInt(long argIN, LongHolder argINOUT, LongHolder argOUT)
   {
      log.info("unsignedInt: [argIn=" + argIN + ",argINOUT=" + argINOUT.value + ",argOUT=" + argOUT.value + "]");
      argINOUT.value = argIN;
      argOUT.value = argIN;
   }

   public void unsignedShort(int argIN, IntHolder argINOUT, IntHolder argOUT)
   {
      log.info("unsignedShort: [argIn=" + argIN + ",argINOUT=" + argINOUT.value + ",argOUT=" + argOUT.value + "]");
      argINOUT.value = argIN;
      argOUT.value = argIN;
   }

   public void unsignedByte(short argIN, ShortHolder argINOUT, ShortHolder argOUT)
   {
      log.info("unsignedByte: [argIn=" + argIN + ",argINOUT=" + argINOUT.value + ",argOUT=" + argOUT.value + "]");
      argINOUT.value = argIN;
      argOUT.value = argIN;
   }
}
