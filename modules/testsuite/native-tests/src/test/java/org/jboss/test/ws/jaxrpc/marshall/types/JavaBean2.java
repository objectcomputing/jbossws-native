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
package org.jboss.test.ws.jaxrpc.marshall.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

public class JavaBean2
{
   protected java.math.BigDecimal myBigDecimal;
   protected double myDouble;
   protected long myLong;
   protected short myShort;
   protected int myInt;
   protected Calendar myCalendar;
   protected byte myByte;
   protected boolean myBoolean;
   protected String myString;
   protected java.math.BigInteger myBigInteger;
   protected float myFloat;

   public JavaBean2()
   {
   }

   public JavaBean2(BigDecimal myBigDecimal, double myDouble, long myLong, short myShort, int myInt, Calendar myCalendar, byte myByte, boolean myBoolean, String myString, BigInteger myBigInteger, float myFloat)
   {
      this.myBigDecimal = myBigDecimal;
      this.myDouble = myDouble;
      this.myLong = myLong;
      this.myShort = myShort;
      this.myInt = myInt;
      this.myCalendar = myCalendar;
      this.myByte = myByte;
      this.myBoolean = myBoolean;
      this.myString = myString;
      this.myBigInteger = myBigInteger;
      this.myFloat = myFloat;
   }

   public BigDecimal getMyBigDecimal()
   {
      return myBigDecimal;
   }

   public void setMyBigDecimal(BigDecimal myBigDecimal)
   {
      this.myBigDecimal = myBigDecimal;
   }

   public double getMyDouble()
   {
      return myDouble;
   }

   public void setMyDouble(double myDouble)
   {
      this.myDouble = myDouble;
   }

   public long getMyLong()
   {
      return myLong;
   }

   public void setMyLong(long myLong)
   {
      this.myLong = myLong;
   }

   public short getMyShort()
   {
      return myShort;
   }

   public void setMyShort(short myShort)
   {
      this.myShort = myShort;
   }

   public int getMyInt()
   {
      return myInt;
   }

   public void setMyInt(int myInt)
   {
      this.myInt = myInt;
   }

   public Calendar getMyCalendar()
   {
      return myCalendar;
   }

   public void setMyCalendar(Calendar myCalendar)
   {
      this.myCalendar = myCalendar;
   }

   public byte getMyByte()
   {
      return myByte;
   }

   public void setMyByte(byte myByte)
   {
      this.myByte = myByte;
   }

   public boolean isMyBoolean()
   {
      return myBoolean;
   }

   public void setMyBoolean(boolean myBoolean)
   {
      this.myBoolean = myBoolean;
   }

   public String getMyString()
   {
      return myString;
   }

   public void setMyString(String myString)
   {
      this.myString = myString;
   }

   public BigInteger getMyBigInteger()
   {
      return myBigInteger;
   }

   public void setMyBigInteger(BigInteger myBigInteger)
   {
      this.myBigInteger = myBigInteger;
   }

   public float getMyFloat()
   {
      return myFloat;
   }

   public void setMyFloat(float myFloat)
   {
      this.myFloat = myFloat;
   }

   public boolean equals(Object obj)
   {
      if (obj == this) return true;
      if ((obj instanceof JavaBean2) == false) return false;
      JavaBean2 other = (JavaBean2)obj;
      return other.toString().equals(toString());
   }

   public int hashCode()
   {
      return toString().hashCode();
   }

   public String toString()
   {
      Date date = (myCalendar != null ? myCalendar.getTime() : null);
      String ret =
              "[myBigDecimal=" + myBigDecimal +
              ",myDouble=" + myDouble +
              ",myLong=" + myLong +
              ",myShort=" + myShort +
              ",myInt=" + myInt +
              ",myCalendar=" + date +
              ",myByte=" + myByte +
              ",myBoolean=" + myBoolean +
              ",myString=" + myString +
              ",myBigInteger=" + myBigInteger +
              ",myFloat=" + myFloat +
              "]";
      return ret;
   }
}
