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
package org.jboss.test.ws.jaxrpc.jbws1647;

import java.io.ByteArrayInputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.jboss.wsf.test.JBossWSTest;

/**
 * 
 * @author darran.lofthouse@jboss.com
 * @since 15 May 2007
 */
public abstract class JBWS1647TestBase extends JBossWSTest
{
   public abstract String getMessage();

   public abstract String getEndpointAddress();

   public void testCall() throws Exception
   {
      MessageFactory msgFactory = MessageFactory.newInstance();
      SOAPMessage soapMessage = msgFactory.createMessage(null, new ByteArrayInputStream(getMessage().getBytes()));

      SOAPConnectionFactory conFactory = SOAPConnectionFactory.newInstance();
      SOAPConnection con = conFactory.createConnection();
      SOAPMessage resMessage = con.call(soapMessage, getEndpointAddress());

      SOAPElement soapElement = (SOAPElement)resMessage.getSOAPBody().getChildElements().next();
      assertEquals("echoMessageResponse", soapElement.getElementName().getLocalName());
   }

}
