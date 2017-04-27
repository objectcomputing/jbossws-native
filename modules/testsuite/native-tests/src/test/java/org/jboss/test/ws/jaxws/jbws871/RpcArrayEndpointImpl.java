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
package org.jboss.test.ws.jaxws.jbws871;

import java.util.Arrays;

import javax.jws.WebService;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;

@WebService(endpointInterface="org.jboss.test.ws.jaxws.jbws871.RpcArrayEndpoint")
public class RpcArrayEndpointImpl
{
   // Provide logging
   private static Logger log = Logger.getLogger(RpcArrayEndpointImpl.class);

   public Integer[] intArr(String type, Integer arr[])
   {
      log.info("intArr: " + type + "," + ((arr == null) ? "null" : Arrays.asList(arr)));

      if (type.equals("null"))
      {
         if (arr != null)
         {
            throw new WSException("null array expected");
         }
      }
      else if (type.equals("empty"))
      {
         if (arr.length != 0)
         {
            throw new WSException("empty array expected");
         }
      }
      else if (type.equals("single"))
      {
         if (arr.length != 1)
         {
            throw new WSException("single value array expected");
         }
      }
      else if (type.equals("multi"))
      {
         if (arr.length != 3)
         {
            throw new WSException("multi value array expected");
         }
      }
      else
      {
         throw new WSException("invalid type: " + type);
      }

      return arr;
   }

   public Integer[][] intMultiArr(Integer arr[][])
   {
      log.info("intMultiArr: " + arr);
      return arr;
   }
}
