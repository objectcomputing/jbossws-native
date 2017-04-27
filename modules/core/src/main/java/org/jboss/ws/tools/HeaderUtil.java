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
package org.jboss.ws.tools;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLBindingMessageReference;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperation;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperation;
import org.jboss.ws.metadata.wsdl.WSDLSOAPHeader;

/**
 * Collection of static methods required for processing
 * bound headers in WSDL to Java so they can be re-used for
 * the JAX-RPC generation.
 * 
 * @author darran.lofthouse@jboss.com
 * @since 29 May 2007
 */
public class HeaderUtil
{

   public static WSDLBindingOperation getWSDLBindingOperation(final WSDLDefinitions wsdl, final WSDLInterfaceOperation operation)
   {
      WSDLBindingOperation bindingOperation = null;

      WSDLInterface wsdlInterface = operation.getWsdlInterface();
      QName operationName = operation.getName();

      WSDLBinding binding = wsdl.getBindingByInterfaceName(wsdlInterface.getName());
      bindingOperation = binding.getOperationByRef(operationName);

      return bindingOperation;
   }

   public static WSDLSOAPHeader[] getSignatureHeaders(final WSDLBindingMessageReference[] refs)
   {
      ArrayList<WSDLSOAPHeader> headers = new ArrayList<WSDLSOAPHeader>();

      if (refs != null)
      {
         for (WSDLBindingMessageReference current : refs)
         {
            for (WSDLSOAPHeader header : current.getSoapHeaders())
            {
               headers.add(header);
            }
         }
      }

      return headers.toArray(new WSDLSOAPHeader[headers.size()]);
   }

   public static boolean containsMatchingPart(WSDLSOAPHeader[] headers, WSDLSOAPHeader header)
   {
      for (WSDLSOAPHeader current : headers)
      {
         if (current.getPartName().equals(header.getPartName()))
         {
            if (current.getElement().equals(header.getElement()))
            {
               return true;
            }
         }
      }

      return false;
   }

}
