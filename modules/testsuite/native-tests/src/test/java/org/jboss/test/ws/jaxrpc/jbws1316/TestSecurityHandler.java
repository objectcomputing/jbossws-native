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
package org.jboss.test.ws.jaxrpc.jbws1316;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.MessageContext;

import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.soap.SOAPMessageImpl;
import org.jboss.ws.extensions.security.Constants;
import org.jboss.ws.extensions.security.Util;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Simple handler to add wsse:Security and Timestamp to header in controlled
 * way to test JBWS-1316 implementation.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 * @since Aril 14 2008
 */
public class TestSecurityHandler extends GenericHandler
{

   public static final String JBWS1316_CREATED = "jbws1316.created";

   public static final String JBWS1316_EXPIRES = "jbws1316.expires";

   @Override
   public boolean handleRequest(MessageContext msgContext)
   {
      Integer createdValue = (Integer)msgContext.getProperty(JBWS1316_CREATED);
      Integer expiresValue = (Integer)msgContext.getProperty(JBWS1316_EXPIRES);

      // Created is bare minimum to add the header.
      if (createdValue == null)
      {
         return super.handleRequest(msgContext);
      }

      Calendar baseCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

      Calendar createdCalendar = (Calendar)baseCalendar.clone();
      createdCalendar.add(Calendar.SECOND, createdValue);

      Calendar expiresCalendar = null;

      if (expiresValue != null)
      {
         expiresCalendar = (Calendar)baseCalendar.clone();
         expiresCalendar.add(Calendar.SECOND, expiresValue);
      }

      CommonMessageContext ctx = (CommonMessageContext)msgContext;
      SOAPMessageImpl soapMessage = (SOAPMessageImpl)ctx.getSOAPMessage();
      Document message = soapMessage.getSOAPPart();

      Element soapHeader = Util.findOrCreateSoapHeader(message.getDocumentElement());

      Element wsseSecurityElement = message.createElementNS(Constants.WSSE_NS, Constants.WSSE_HEADER);
      Util.addNamespace(wsseSecurityElement, Constants.WSSE_PREFIX, Constants.WSSE_NS);
      Util.addNamespace(wsseSecurityElement, Constants.WSU_PREFIX, Constants.WSU_NS);

      Element wsseTimestampElement = message.createElementNS(Constants.WSU_NS, Constants.WSU_PREFIX + ":" + "Timestamp");
      wsseTimestampElement.setAttributeNS(Constants.WSU_NS, Constants.WSU_ID, "timestamp");
      Element child = message.createElementNS(Constants.WSU_NS, Constants.WSU_PREFIX + ":" + "Created");
      child.appendChild(message.createTextNode(SimpleTypeBindings.marshalDateTime(createdCalendar)));
      wsseTimestampElement.appendChild(child);
      if (expiresCalendar != null)
      {
         child = message.createElementNS(Constants.WSU_NS, Constants.WSU_PREFIX + ":" + "Expires");
         child.appendChild(message.createTextNode(SimpleTypeBindings.marshalDateTime(expiresCalendar)));
         wsseTimestampElement.appendChild(child);
      }
      wsseSecurityElement.appendChild(wsseTimestampElement);

      soapHeader.insertBefore(wsseSecurityElement, soapHeader.getFirstChild());

      return true;
   }

   @Override
   public QName[] getHeaders()
   {
      return null;
   }

}
