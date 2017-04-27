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
package javax.xml.rpc.handler.soap;

import javax.xml.rpc.handler.MessageContext;
import javax.xml.soap.SOAPMessage;

/** This interface provides access to the SOAP message for either RPC request
 * or response. The javax.xml.soap.SOAPMessage specifies the standard Java API
 * for the representation of a SOAP 1.1 message with attachments.
 * 
 * @author Scott.Stark@jboss.org
 * @author Rahul Sharma (javadoc)
 */
public interface SOAPMessageContext extends MessageContext
{
   public SOAPMessage getMessage();

   public void setMessage(SOAPMessage message);

   public String[] getRoles();
}
