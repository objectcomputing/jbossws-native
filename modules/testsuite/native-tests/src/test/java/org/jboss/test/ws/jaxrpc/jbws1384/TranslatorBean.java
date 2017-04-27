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
package org.jboss.test.ws.jaxrpc.jbws1384;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.server.ServletEndpointContext;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;

import org.jboss.logging.Logger;

public class TranslatorBean implements TransmulatorInterface, ServiceLifecycle
{
   private Logger log = Logger.getLogger(TranslatorBean.class);

   private ServletEndpointContext sepCtx;

   public String invokeAttach(String username, String password, String operationName, String inputXML, String attachmentContents)
   {
      String reqMessage = "[user=" + username + ",pass=" + password + ",op=" + operationName + ",xml=" + inputXML + "]";

      // Verify parameter was actually an attachment
      String attachedStr = null;
      try
      {
         SOAPMessageContext msgContext = (SOAPMessageContext)sepCtx.getMessageContext();
         AttachmentPart part = (AttachmentPart)msgContext.getMessage().getAttachments().next();
         attachedStr = (String)part.getContent();
      }
      catch (SOAPException ex)
      {
         throw new RuntimeException(ex);
      }

      if (attachedStr == null || !attachedStr.equals(attachmentContents))
         throw new IllegalStateException("Attachment strings do not match");

      log.info(reqMessage + " " + attachedStr);
      return reqMessage + " " + attachedStr;
   }

   public void init(Object context) throws ServiceException
   {
      this.sepCtx = (ServletEndpointContext)context;
   }

   public void destroy()
   {
   }
}
