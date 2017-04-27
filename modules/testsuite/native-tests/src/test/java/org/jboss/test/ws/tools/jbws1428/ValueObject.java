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
package org.jboss.test.ws.tools.jbws1428;

/**
 * [JBWS-1428] - Java to WSDL - Arrays defined with indexed properties within 
 * value type are skipped when generating the WSDL
 * 
 * @author darran.lofthouse@jboss.com
 * @since 14 Dec 2006
 */
public class ValueObject
{

   private String myMessage;

   private String[] myValues;

   public String getMyMessage()
   {
      return myMessage;
   }

   public void setMyMessage(String myMessage)
   {
      this.myMessage = myMessage;
   }

   public String[] getMyValues()
   {
      return myValues;
   }

   public void setMyValues(String[] myValues)
   {
      this.myValues = myValues;
   }

   public String getMyValues(int i)
   {
      return myValues[i];
   }

   public void setMyValues(int i, String myValues)
   {
      this.myValues[i] = myValues;
   }

}
