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
package org.jboss.ws.extensions.wsrm.transport;

import static org.jboss.ws.extensions.wsrm.transport.RMChannelConstants.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.jboss.remoting.marshal.Marshaller;
import org.jboss.remoting.marshal.UnMarshaller;
import org.jboss.ws.core.MessageAbstraction;

/**
 * Translates JBoss messages to RM sources and vice-versa.
 * It's necessary to translate JBoss messages to/from RM
 * sources before submiting them to the RMSender.
 * 
 * <p>
 * This is because of the following reasons:
 * <ul>
 *   <li>contextClassloader not serializable issue</li>
 *   <li>DOMUtil threadlocal issue (if message is de/serialized in separate thread)</li>
 * </ul>
 * </p>  
 *
 * @author richard.opalka@jboss.com
 */
public final class RMMessageAssembler
{

   /**
    * Transforms JBoss request message to the RM request message
    * This transformation is done before submiting request to the RMSender.
    */
   public static RMMessage convertMessageToRMSource(MessageAbstraction request, RMMetadata rmMetadata) throws Throwable
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      Marshaller marshaller = (Marshaller)rmMetadata.getContext(SERIALIZATION_CONTEXT).get(MARSHALLER);
      marshaller.write(request, baos);
      RMMessage rmMessage = RMMessageFactory.newMessage(baos.toByteArray(), rmMetadata);
      return rmMessage;
   }
   
   /**
    * Transforms RM response message to the JBoss response message.
    * This transformation is done after RMSender have finished his job.
    */
   public static MessageAbstraction convertRMSourceToMessage(RMMessage rmRequest, RMMessage rmResponse, RMMetadata rmMetadata) throws Throwable
   {
      boolean oneWay = RMTransportHelper.isOneWayOperation(rmRequest);
      MessageAbstraction response = null;
      if (false == oneWay)
      {
         byte[] payload = rmResponse.getPayload();
         InputStream in = (payload == null) ? null : new ByteArrayInputStream(rmResponse.getPayload()); 
         UnMarshaller unmarshaller = (UnMarshaller)rmMetadata.getContext(SERIALIZATION_CONTEXT).get(UNMARSHALLER);
         response = (MessageAbstraction)unmarshaller.read(in, rmResponse.getMetadata().getContext(REMOTING_INVOCATION_CONTEXT));
      }
      Map<String, Object> invocationContext = rmMetadata.getContext(INVOCATION_CONTEXT);
      invocationContext.clear();
      invocationContext.putAll(rmMetadata.getContext(REMOTING_INVOCATION_CONTEXT));
      return response;
   }
   
}
