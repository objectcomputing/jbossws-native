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
package javax.xml.ws;

/** The <code>WebServiceException</code> class is the base 
 *  exception class for all JAX-WS API runtime exceptions.
 *
 *  @since JAX-WS 2.0
 **/

public class WebServiceException extends java.lang.RuntimeException
{
   private static final long serialVersionUID = 9050257594613372011L;

   /** Constructs a new exception with <code>null</code> as its 
    *  detail message. The cause is not initialized.
    **/
   public WebServiceException()
   {
      super();
   }

   /** Constructs a new exception with the specified detail 
    *  message.  The cause is not initialized.
    *  @param message The detail message which is later 
    *                 retrieved using the getMessage method
    **/
   public WebServiceException(String message)
   {
      super(message);
   }

   /** Constructs a new exception with the specified detail 
    *  message and cause.
    *
    *  @param message The detail message which is later retrieved
    *                 using the getMessage method
    *  @param cause   The cause which is saved for the later
    *                 retrieval throw by the getCause method 
    **/
   public WebServiceException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /** Constructs a new WebServiceException with the specified cause
    *  and a detail message of <tt>(cause==null ? null : 
    *  cause.toString())</tt> (which typically contains the 
    *  class and detail message of <tt>cause</tt>).
    *
    *  @param cause   The cause which is saved for the later
    *                 retrieval throw by the getCause method.
    *                 (A <tt>null</tt> value is permitted, and
    *                 indicates that the cause is nonexistent or
    *               unknown.)
    **/
   public WebServiceException(Throwable cause)
   {
      super(cause);
   }

}
