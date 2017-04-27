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


public class JavaBean
{
   protected java.math.BigDecimal myBigDecimal;
   protected double myDouble;
   protected long myLong;
   protected JavaBean2 myJavaBean;
   protected short myShort;
   protected int myInt;
   protected java.util.Calendar myCalendar;
   protected byte myByte;
   protected boolean myBoolean;
   protected java.lang.String myString;
   protected java.math.BigInteger myBigInteger;
   protected float myFloat;

   public JavaBean()
   {
   }

   public JavaBean(java.math.BigDecimal myBigDecimal, double myDouble, long myLong, JavaBean2 myJavaBean, short myShort, int myInt, java.util.Calendar myCalendar, byte myByte, boolean myBoolean, java.lang.String myString, java.math.BigInteger myBigInteger, float myFloat)
   {
      this.myBigDecimal = myBigDecimal;
      this.myDouble = myDouble;
      this.myLong = myLong;
      this.myJavaBean = myJavaBean;
      this.myShort = myShort;
      this.myInt = myInt;
      this.myCalendar = myCalendar;
      this.myByte = myByte;
      this.myBoolean = myBoolean;
      this.myString = myString;
      this.myBigInteger = myBigInteger;
      this.myFloat = myFloat;
   }

   public java.math.BigDecimal getMyBigDecimal()
   {
      return myBigDecimal;
   }

   public void setMyBigDecimal(java.math.BigDecimal myBigDecimal)
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

   public JavaBean2 getMyJavaBean()
   {
      return myJavaBean;
   }

   public void setMyJavaBean(JavaBean2 myJavaBean)
   {
      this.myJavaBean = myJavaBean;
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

   public java.util.Calendar getMyCalendar()
   {
      return myCalendar;
   }

   public void setMyCalendar(java.util.Calendar myCalendar)
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

   public java.lang.String getMyString()
   {
      return myString;
   }

   public void setMyString(java.lang.String myString)
   {
      this.myString = myString;
   }

   public java.math.BigInteger getMyBigInteger()
   {
      return myBigInteger;
   }

   public void setMyBigInteger(java.math.BigInteger myBigInteger)
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
      if ((obj instanceof JavaBean) == false) return false;
      JavaBean other = (JavaBean)obj;
      return other.toString().equals(toString());
   }

   public int hashCode()
   {
      return toString().hashCode();
   }

   public String toString()
   {
      String ret =
              "[myBigDecimal=" + myBigDecimal +
              ",myDouble=" + myDouble +
              ",myLong=" + myLong +
              ",myJavaBean=" + myJavaBean +
              ",myShort=" + myShort +
              ",myInt=" + myInt +
              ",myCalendar=" + (myCalendar != null ? myCalendar.getTime() : null) +
              ",myByte=" + myByte +
              ",myBoolean=" + myBoolean +
              ",myString=" + myString +
              ",myBigInteger=" + myBigInteger +
              ",myFloat=" + myFloat +
              "]";
      return ret;
   }
}
