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
package org.jboss.test.ws.jaxws.wsrm;

import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingConstants;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.JAXWSAConstants;
import org.jboss.wsf.common.utils.UUIDGenerator;

/**
 * WS-RM Tests helper
 *
 * @author richard.opalka@jboss.com
 */
public final class Helper
{
   
   private static AddressingBuilder BUILDER;
   private static AddressingConstants CONSTANTS;

   static
   {
      BUILDER = AddressingBuilder.getAddressingBuilder();
      CONSTANTS = BUILDER.newAddressingConstants();
   }
   
   private Helper()
   {
      // no instances
   }

   /**
    * Setup addressing SOAP headers for specified proxy
    * @param proxy
    * @param wsaAction
    * @param serviceURL
    */
   public static void setAddrProps(Object proxy, String wsaAction, String serviceURL)
   {
      BindingProvider bp = (BindingProvider)proxy;
      AddressingProperties props = createAnonymousProps(wsaAction, serviceURL);

      bp.getRequestContext().put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_OUTBOUND, props);
   }
   
   public static AddressingProperties createAnonymousProps(String wsaAction, String wsaTo)
   {
      try
      {
         AddressingProperties addrProps = BUILDER.newAddressingProperties();
         addrProps.setMessageID(BUILDER.newURI(new URI("urn:uuid:" + UUIDGenerator.generateRandomUUIDString())));
         addrProps.setAction(BUILDER.newURI(wsaAction));
         addrProps.setTo(BUILDER.newURI(wsaTo));
         addrProps.setReplyTo(BUILDER.newEndpointReference(new URI(CONSTANTS.getAnonymousURI())));
         return addrProps;
      }
      catch (URISyntaxException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
   }
   
}
