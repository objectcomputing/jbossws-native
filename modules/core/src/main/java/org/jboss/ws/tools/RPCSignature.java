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
import java.util.Collection;

import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperation;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationInput;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutput;
import org.jboss.ws.metadata.wsdl.WSDLRPCPart;
import org.jboss.ws.metadata.wsdl.WSDLRPCSignatureItem;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.WSDLRPCSignatureItem.Direction;

public class RPCSignature
{
   private Collection<WSDLRPCPart> parameters = new ArrayList<WSDLRPCPart>();
   private WSDLRPCPart returnParameter;

   public Collection<WSDLRPCPart> parameters()
   {
      return parameters;
   }

   public WSDLRPCPart returnParameter()
   {
      return returnParameter;
   }

   public RPCSignature(WSDLInterfaceOperation operation)
   {
      WSDLInterfaceOperationInput input = WSDLUtils.getWsdl11Input(operation);
      WSDLInterfaceOperationOutput output = WSDLUtils.getWsdl11Output(operation);
      for (WSDLRPCSignatureItem item : operation.getRpcSignatureItems())
      {
         if (item.getDirection() == Direction.RETURN)
         {
            if (output != null)
               returnParameter = output.getChildPart(item.getName());
            continue;
         }

         WSDLRPCPart part = null;
         if (input != null)
            part = input.getChildPart(item.getName());
         if (output != null && part == null)
            part = output.getChildPart(item.getName());

         if (part != null)
            parameters.add(part);
      }

      for (WSDLRPCPart part : input.getChildParts())
      {
         if (operation.getRpcSignatureitem(part.getName()) == null)
            parameters.add(part);
      }

      if (output != null)
      {
         for (WSDLRPCPart part : output.getChildParts())
         {
            if (operation.getRpcSignatureitem(part.getName()) == null)
            {
               // Filter in-outs
               if (input.getChildPart(part.getName()) != null)
                  continue;

               if (returnParameter == null)
               {
                  returnParameter = part;
               }
               else
               {
                  parameters.add(part);
               }
            }
         }
      }
   }
}
