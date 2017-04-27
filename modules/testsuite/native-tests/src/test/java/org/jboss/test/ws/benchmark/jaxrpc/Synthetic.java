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

/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.test.ws.benchmark.jaxrpc;

import java.io.Serializable;

/**
 * A Synthetic.
 * 
 * @author <a href="anders.hedstrom@home.se">Anders Hedstrom</a>
 */
public class Synthetic implements Serializable
{
   private String s;
   private SimpleUserType sut;
   private byte[] barray;

   public Synthetic()
   {
   }

   
   /**
    * @param s
    * @param sut
    * @param b
    */
   public Synthetic(String s, SimpleUserType sut, byte[] b)
   {
      super();
      this.s = s;
      this.sut = sut;
      this.barray = b;
   }
   
   public byte[] getB()
   {
      return barray;
   }
   public void setB(byte[] b)
   {
      this.barray = b;
   }
   public String getS()
   {
      return s;
   }
   public void setS(String s)
   {
      this.s = s;
   }
   public SimpleUserType getSut()
   {
      return sut;
   }
   public void setSut(SimpleUserType sut)
   {
      this.sut = sut;
   }
}
