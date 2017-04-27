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
package org.jboss.test.ws.jaxrpc.jbws732;

import java.util.Arrays;

import org.jboss.logging.Logger;

public class WrappedEndpointImpl implements WrappedEndpoint
{
   // Provide logging
   private static Logger log = Logger.getLogger(WrappedEndpointImpl.class);
   
   private void dump(String[] arr)
   {
      if (arr == null)
      {
         log.info("Null");
      }
      else
      {
         String message = Arrays.asList(arr).toString();
         log.info(message);
      }
   }

   public String[] echoStringArray(String[] arr)
   {
      dump(arr);

      return arr;
   }

   public String[] echoStringWrappedArray(String[] arr)
   {
      dump(arr);

      return arr;
   }
}
