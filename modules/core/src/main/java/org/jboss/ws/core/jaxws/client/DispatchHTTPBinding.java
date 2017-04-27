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
package org.jboss.ws.core.jaxws.client;

import org.jboss.logging.Logger;
import org.jboss.ws.core.HTTPMessageImpl;
import org.jboss.ws.core.MessageAbstraction;
import org.jboss.ws.util.xml.BufferedStreamResult;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceException;

/**
 * The Dispatch interface provides support for the dynamic invocation of a service endpoint operations. 
 * The javax.xml.ws.Service interface acts as a factory for the creation of Dispatch  instances.
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 04-Jul-2006
 */
public class DispatchHTTPBinding extends DispatchBinding
{
   // provide logging
   private final Logger log = Logger.getLogger(DispatchHTTPBinding.class);

   private JAXBContext jaxbContext;
   private Class type;
   private Mode mode;

   public DispatchHTTPBinding(Mode mode, Class type, JAXBContext jaxbContext)
   {
      this.mode = mode;
      this.type = type;
      this.jaxbContext = jaxbContext;
   }

   public MessageAbstraction getRequestMessage(Object obj)
   {
      HTTPMessageImpl reqMsg = null;
      try
      {
         if (Source.class.isAssignableFrom(type))
         {
            Source source = (Source)obj;
            reqMsg = new HTTPMessageImpl(source);
            if(validateDispatch)
               reqMsg.doValidate();
         }
         else if (jaxbContext != null)
         {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            BufferedStreamResult result = new BufferedStreamResult();
            marshaller.marshal(obj, result);

            reqMsg = new HTTPMessageImpl(result);
         }
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception ex)
      {
         throw new WebServiceException("Cannot create request message", ex);
      }

      if (reqMsg == null)
         throw new WebServiceException("Cannot create request message for: " + obj);

      return reqMsg;
   }

   public Object getReturnObject(MessageAbstraction message)
   {
      HTTPMessageImpl resMsg = (HTTPMessageImpl)message;

      Object retObj = null;
      try
      {
         if (Source.class.isAssignableFrom(type))
         {
            retObj = resMsg.getXmlFragment().getSource();
         }
         else if (jaxbContext != null)
         {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Source source = resMsg.getXmlFragment().getSource();
            retObj = unmarshaller.unmarshal(source);
         }
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception ex)
      {
         throw new WebServiceException("Cannot process response message", ex);
      }
      return retObj;
   }
}
