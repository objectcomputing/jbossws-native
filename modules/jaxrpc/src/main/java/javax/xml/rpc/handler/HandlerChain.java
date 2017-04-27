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

import java.util.List;
import java.util.Map;

import javax.xml.rpc.JAXRPCException;

/** This interface represents a list of handlers. All elements in the
 * HandlerChain are of the type javax.xml.rpc.handler.Handler.
 * 
 * An implementation class for the HandlerChain interface abstracts the policy
 * and mechanism for the invocation of the registered handlers. 
 * 
 * @author Scott.Stark@jboss.org
 * @author Rahul Sharma (javadoc)
 */
public interface HandlerChain extends List
{
   /**
    * Initializes the configuration for a HandlerChain.
    * @param config Configuration for the initialization of this handler chain
    * @throws JAXRPCException If any error during initialization
    */
   public void init(Map config);

   /**
    * Indicates the end of lifecycle for a HandlerChain.
    * @throws JAXRPCException If any error during destroy
    */
   public void destroy();

   /**
    * Gets SOAP actor roles registered for this HandlerChain at this SOAP node. The returned array includes the
    * special SOAP actor next.
    * @return SOAP Actor roles as URIs
    */
   public String[] getRoles();

   /**
    * Sets SOAP Actor roles for this HandlerChain. This specifies the set of roles in which this HandlerChain is to act
    * for the SOAP message processing at this SOAP node. These roles assumed by a HandlerChain must be invariant during
    * the processing of an individual SOAP message through the HandlerChain.
    * <p/>
    * A HandlerChain always acts in the role of the special SOAP actor next. Refer to the SOAP specification for the
    * URI name for this special SOAP actor. There is no need to set this special role using this method.
    * @param soapActorNames URIs for SOAP actor name
    */
   public void setRoles(String[] soapActorNames);

   /**
    * The handleRequest method initiates the request processing for this handler chain.
    * @param msgContext MessageContext parameter provides access to the request SOAP message.
    * @return Returns true if all handlers in chain have been processed. Returns false if a handler in the chain returned false from its handleRequest method.
    * @throws JAXRPCException if any processing error happens
    */
   public boolean handleRequest(MessageContext msgContext);

   /**
    * The handleResponse method initiates the response processing for this handler chain.
    * @return Returns true if all handlers in chain have been processed. Returns false if a handler in the chain returned false from its handleRequest method.
    * @throws JAXRPCException if any processing error happens
    */
   public boolean handleResponse(MessageContext msgContext);

   /**
    * The handleFault method initiates the SOAP fault processing for this handler chain.
    * @return Returns true if all handlers in chain have been processed. Returns false if a handler in the chain returned false from its handleRequest method.
    * @throws JAXRPCException if any processing error happens
    */
   public boolean handleFault(MessageContext msgContext);
}
