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
package javax.xml.ws.http;

/** The <code>HTTPException</code> exception represents a 
 *  XML/HTTP fault.
 *
 *  <p>Since there is no standard format for faults or exceptions
 *  in XML/HTTP messaging, only the HTTP status code is captured. 
 * 
 *  @since JAX-WS 2.0
 **/
public class HTTPException extends javax.xml.ws.ProtocolException
{
   private static final long serialVersionUID = -7126704204037460302L;
   
   private int statusCode;

   /** Constructor for the HTTPException
    *  @param statusCode   <code>int</code> for the HTTP status code
    **/
   public HTTPException(int statusCode)
   {
      super();
      this.statusCode = statusCode;
   }

   /** Gets the HTTP status code.
    *
    *  @return HTTP status code
    **/
   public int getStatusCode()
   {
      return statusCode;
   }
}
