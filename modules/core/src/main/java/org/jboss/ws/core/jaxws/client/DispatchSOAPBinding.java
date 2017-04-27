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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.soap.SOAPFaultException;

import org.jboss.logging.Logger;
import org.jboss.ws.core.MessageAbstraction;
import org.jboss.ws.core.soap.SOAPBodyElementDoc;
import org.jboss.ws.core.soap.SOAPBodyImpl;
import org.jboss.ws.core.soap.SOAPContentElement;
import org.jboss.ws.core.soap.SOAPMessageImpl;
import org.jboss.ws.core.soap.XMLFragment;
import org.jboss.wsf.common.DOMWriter;

/**
 * A helper that 
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 02-Apr-2007
 */
public class DispatchSOAPBinding extends DispatchBinding
{
   // provide logging
   private final Logger log = Logger.getLogger(DispatchSOAPBinding.class);

   private JAXBContext jaxbContext;
   private Class type;
   private Mode mode;

   public DispatchSOAPBinding(Mode mode, Class type, JAXBContext jaxbContext)
   {
      this.mode = mode;
      this.type = type;
      this.jaxbContext = jaxbContext;
   }

   public MessageAbstraction getRequestMessage(Object obj)
   {
      SOAPMessageImpl reqMsg = null;
      try
      {
         MessageFactory factory = MessageFactory.newInstance();
         if (SOAPMessage.class.isAssignableFrom(type))
         {
            reqMsg = (SOAPMessageImpl)obj;
         }
         else if (Source.class.isAssignableFrom(type))
         {
            Source source = (Source)obj;
            if (mode == Mode.PAYLOAD)
            {
               reqMsg = (SOAPMessageImpl)factory.createMessage();
               SOAPBodyImpl soapBody = (SOAPBodyImpl)reqMsg.getSOAPBody();
               SOAPContentElement bodyElement = new SOAPBodyElementDoc(SOAPBodyElementDoc.GENERIC_PARAM_NAME);
               bodyElement = (SOAPContentElement)soapBody.addChildElement(bodyElement);
               XMLFragment xmlFragment = new XMLFragment(source);
               bodyElement.setXMLFragment(xmlFragment);

               // validate payload if necessary
               if (validateDispatch)
               {
                  // expand to DOM will validate the contents
                  xmlFragment.toElement();
               }

            }
            if (mode == Mode.MESSAGE)
            {
               TransformerFactory tf = TransformerFactory.newInstance();
               ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
               tf.newTransformer().transform(source, new StreamResult(baos));
               reqMsg = (SOAPMessageImpl)factory.createMessage(null, new ByteArrayInputStream(baos.toByteArray()));
            }
         }
         else if (jaxbContext != null)
         {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            marshaller.marshal(obj, baos);

            reqMsg = (SOAPMessageImpl)factory.createMessage();
            SOAPBodyImpl soapBody = (SOAPBodyImpl)reqMsg.getSOAPBody();
            SOAPContentElement bodyElement = new SOAPBodyElementDoc(SOAPBodyElementDoc.GENERIC_PARAM_NAME);
            bodyElement = (SOAPContentElement)soapBody.addChildElement(bodyElement);
            StreamSource source = new StreamSource(new ByteArrayInputStream(baos.toByteArray()));
            bodyElement.setXMLFragment(new XMLFragment(source));
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
      SOAPMessage resMsg = (SOAPMessage)message;

      Object retObj = null;
      try
      {
         if (SOAPMessage.class.isAssignableFrom(type))
         {
            retObj = resMsg;
         }
         else if (Source.class.isAssignableFrom(type))
         {
            if (mode == Mode.PAYLOAD)
            {
               SOAPBodyImpl soapBody = (SOAPBodyImpl)resMsg.getSOAPBody();

               SOAPFault soapFault = soapBody.getFault();
               if (soapFault != null)
                  throw new SOAPFaultException(soapFault);

               SOAPElement soapElement = soapBody.getBodyElement();
               retObj = new DOMSource(soapElement);
            }
            if (mode == Mode.MESSAGE)
            {
               SOAPEnvelope soapEnvelope = resMsg.getSOAPPart().getEnvelope();
               String xmlMessage = DOMWriter.printNode(soapEnvelope, false);
               retObj = new StreamSource(new StringReader(xmlMessage));
            }
         }
         else if (jaxbContext != null)
         {
            SOAPBodyImpl soapBody = (SOAPBodyImpl)resMsg.getSOAPBody();
            SOAPElement soapElement = soapBody.getBodyElement();

            log.debug("JAXB unmarshal: " + DOMWriter.printNode(soapElement, false));
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            retObj = unmarshaller.unmarshal(soapElement);
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
