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
package javax.xml.rpc.handler;

import javax.xml.namespace.QName;

/** The GenericHandler class implements the Handler interface. SOAP Message
 * Handler developers should typically subclass GenericHandler class unless
 * the Handler class needs another class as a superclass.
 * 
 * The GenericHandler class is a convenience abstract class that makes writing
 * Handlers easy. This class provides default implementations of the lifecycle
 * methods init and destroy and also different handle methods. A Handler
 * developer should only override methods that it needs to specialize as part
 * of the derived Handler implementation class.
 * 
 * @author Scott.Stark@jboss.org
 * @author Rahul Sharma (javadoc)
 */
public abstract class GenericHandler implements Handler
{
   /** Default constructor. */
   protected GenericHandler()
   {
   }

   /**
    * Gets the header blocks processed by this Handler instance.
    *
    * @return Array of QNames of header blocks processed by this handler instance.
    * QName is the qualified name of the outermost element of the Header block.
    */
   public abstract QName[] getHeaders();

   /**
    * The init method to enable the Handler instance to initialize itself. This method should be overridden if the
    * derived Handler class needs to specialize implementation of this method.
    * @param config handler configuration
    */
   public void init(HandlerInfo config)
   {
   }

   /**
    * The destroy method indicates the end of lifecycle for a Handler instance. This method should be overridden if
    * the derived Handler class needs to specialize implementation of this method.
    */
   public void destroy()
   {
   }

   /**
    * The handleRequest method processes the request SOAP message. The default implementation of this method returns true.
    * This indicates that the handler chain should continue processing of the request SOAP message.
    * This method should be overridden if the derived Handler class needs to specialize implementation of this method.
    * @param msgContext the message msgContext
    * @return true/false
    */
   public boolean handleRequest(MessageContext msgContext)
   {
      return true;
   }

   /**
    * The handleResponse method processes the response message. The default implementation of this method returns true.
    * This indicates that the handler chain should continue processing of the response SOAP message.
    * This method should be overridden if the derived Handler class needs to specialize implementation of this method.
    * @param msgContext the message msgContext
    * @return true/false
    */
   public boolean handleResponse(MessageContext msgContext)
   {
      return true;
   }

   /**
    * The handleFault method processes the SOAP faults based on the SOAP message processing model.
    * The default implementation of this method returns true. This indicates that the handler chain should continue
    * processing of the SOAP fault. This method should be overridden if the derived Handler class needs to specialize
    * implementation of this method.
    * @param msgContext the message msgContext
    * @return the message msgContext
    */
   public boolean handleFault(MessageContext msgContext)
   {
      return true;
   }
}
