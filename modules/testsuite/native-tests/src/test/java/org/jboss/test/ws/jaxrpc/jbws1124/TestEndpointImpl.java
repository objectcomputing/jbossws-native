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
package org.jboss.test.ws.jaxrpc.jbws1124;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;

/**
 * @author Heiko.Braun@jboss.org
 */
public class TestEndpointImpl implements TestEndpoint
{
   private Logger log = Logger.getLogger(TestEndpointImpl.class);
   private static final String WEB_INF_TEST_RESOURCE_TXT = "org/jboss/test/ws/jaxrpc/jbws1124/test-resource.txt";

   public String getResourceString() throws RemoteException
   {

         ClassLoader loader = getClass().getClassLoader();
         InputStream ins = loader.getResourceAsStream(WEB_INF_TEST_RESOURCE_TXT);

         if(ins!=null)
         {
            try
            {
               String line = new BufferedReader(new InputStreamReader(ins)).readLine();
               log.info(line);
               return line;
            }
            catch (IOException ex)
            {
               throw new WSException(ex);
            }
         }
         else
         {
            throw new WSException("Failed to load '"+WEB_INF_TEST_RESOURCE_TXT+"' with loader " + loader);  
         }

   }

}
