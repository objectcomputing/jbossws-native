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
package org.jboss.ws.extensions.security.exception;

import javax.xml.namespace.QName;

import org.jboss.ws.extensions.security.Constants;

/**
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class WSSecurityException extends Exception
{
   private boolean internal = false;

   private QName faultCode = new QName(Constants.JBOSS_WSSE_NS, "InternalError", Constants.JBOSS_WSSE_PREFIX);

   private String faultString = "An internal WS-Security error occurred. See log for details";

   public WSSecurityException(String message)
   {
      super(message);
      this.internal = true;
   }

   public WSSecurityException(String message, Throwable cause)
   {
      super(message, cause);
      this.internal = true;
   }

   protected void setFaultCode(QName faultCode)
   {
      this.faultCode = faultCode;
   }

   protected void setFaultString(String faultMessage)
   {
      this.faultString = faultMessage;
   }

   public boolean isInternalError()
   {
      return internal;
   }

   public QName getFaultCode()
   {
      return faultCode;
   }

   public String getFaultString()
   {
      return faultString;
   }
}
