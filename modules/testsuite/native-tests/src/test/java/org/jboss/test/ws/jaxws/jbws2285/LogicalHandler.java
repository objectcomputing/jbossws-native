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
package org.jboss.test.ws.jaxws.jbws2285;

import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.wsf.common.DOMWriter;
import org.jboss.wsf.common.handler.GenericLogicalHandler;

/**
 * Logical handler implementation.
 * 
 * @author darran.lofthouse@jboss.com
 * @since 19th August 2008
 */
public class LogicalHandler extends GenericLogicalHandler
{

   private static final Logger log = Logger.getLogger(LogicalHandler.class);

   @Override
   protected boolean handleInbound(final MessageContext msgContext)
   {
      log.info("handleInbound()");

      LogicalMessageContext lmc = (LogicalMessageContext)msgContext;
      LogicalMessage message = lmc.getMessage();

      Object payload = message.getPayload();

      if (payload instanceof DOMSource == false)
      {
         throw new WSException("Test requires DOMSource payload");
      }

      DOMSource domPayload = (DOMSource)payload;
      Node node = domPayload.getNode();

      NodeList nodes = node.getChildNodes();
      for (int i = 0; i < nodes.getLength(); i++)
      {
         Node current = nodes.item(i);
         if ("arg0".equals(current.getLocalName()))
         {
            current.setTextContent("XXX");
         }
      }

      log.info(DOMWriter.printNode(node, false));

      return true;
   }

}
