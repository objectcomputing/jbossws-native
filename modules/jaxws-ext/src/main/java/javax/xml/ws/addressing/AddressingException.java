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
package javax.xml.ws.addressing;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class AddressingException extends WebServiceException
{
   private static final long serialVersionUID = -4470655951999027171L;

   protected QName code;

   protected String reason;

   protected Object detail;

   protected static AddressingConstants ac = null;
   protected static String fMessage = null;
   static
   {
      ac = AddressingBuilder.getAddressingBuilder().newAddressingConstants();
   }

   public AddressingException()
   {
   }

   public AddressingException(String message)
   {
      super(message);
   }

   public AddressingException(Throwable cause)
   {
      super(cause);
   }

   public AddressingException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Returns the fault code.
    *
    * @return the fault code
    */
   public QName getCode()
   {
      return code;
   }

   public QName getSubcode()
   {
      return null;
   }

   public String getReason()
   {
      return reason;
   }

   public Object getDetail()
   {
      return detail;
   }

}
