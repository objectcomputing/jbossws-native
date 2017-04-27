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

/** The <code>ProtocolException</code> class is a
 *  base class for exceptions related to a specific protocol binding. Subclasses
 *  are used to communicate protocol level fault information to clients and may
 *  be used on the server to control the protocol specific fault representation.
 *
 *  @since JAX-WS 2.0
 **/
public class ProtocolException extends WebServiceException
{
   private static final long serialVersionUID = 6688436881502883481L;

   /**
    * Constructs a new protocol exception with null as its detail message. The
    * cause is not initialized, and may subsequently be initialized by a call
    * to Throwable.initCause(java.lang.Throwable).
    */
   public ProtocolException()
   {
      super();
   }

   /**
    * Constructs a new protocol exception with the specified detail message.
    * The cause is not initialized, and may subsequently be initialized by a
    * call to Throwable.initCause(java.lang.Throwable).
    *
    * @param message the detail message. The detail message is saved for later
    *   retrieval by the Throwable.getMessage() method.
    */
   public ProtocolException(String message)
   {
      super(message);
   }

   /**
    * Constructs a new runtime exception with the specified detail message and
    * cause.
    *
    * Note that the detail message associated with  cause is not automatically
    * incorporated in  this runtime exception's detail message.
    *
    * @param message the detail message (which is saved for later retrieval  by
    *   the Throwable.getMessage() method).
    * @param cause the cause (which is saved for later retrieval by the
    * Throwable.getCause() method). (A null value is  permitted, and indicates
    * that the cause is nonexistent or  unknown.)
    */
   public ProtocolException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * Constructs a new runtime exception with the specified cause and a  detail
    * message of (cause==null ? null : cause.toString())  (which typically
    * contains the class and detail message of  cause). This constructor is
    * useful for runtime exceptions  that are little more than wrappers for
    * other throwables.
    *
    * @param cause the cause (which is saved for later retrieval by the
    * Throwable.getCause() method). (A null value is  permitted, and indicates
    * that the cause is nonexistent or  unknown.)
    */
   public ProtocolException(Throwable cause)
   {
      super(cause);
   }
}
