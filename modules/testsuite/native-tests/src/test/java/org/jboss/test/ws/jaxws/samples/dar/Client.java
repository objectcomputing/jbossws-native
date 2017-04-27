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
package org.jboss.test.ws.jaxws.samples.dar;

import java.net.URL;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;

import org.jboss.test.ws.jaxws.samples.dar.generated.DarEndpoint;
import org.jboss.test.ws.jaxws.samples.dar.generated.DarRequest;
import org.jboss.test.ws.jaxws.samples.dar.generated.DarResponse;
import org.jboss.test.ws.jaxws.samples.dar.generated.DarService;

/**
 * DAR client; invokes the DAR endpoint (sync, asynch) 
 *
 * @author alessio.soldano@jboss.org
 * @since 31-Jan-2008
 */
public class Client
{
   protected DarEndpoint endpoint;
   
   public Client(URL url)
   {
      DarService service = new DarService(url, new QName("http://org.jboss.ws/samples/dar", "DarService"));
      endpoint = service.getDarEndpointPort();
      ClientHelper.setUsernamePassword((BindingProvider)endpoint, "kermit", "thefrog");
   }
   
   public DarResponse run(boolean asynch) throws Exception
   {
      DarRequest request = ClientHelper.getRequest();
      System.out.println(new Date() + " Sending request...");
      DarResponse darResponse;
      if (asynch)
      {
         Response<DarResponse> response = endpoint.processAsync(request);
         System.out.println("Doing something interesting in the mean time... ;-) ");
         darResponse = response.get();
      }
      else
      {
         darResponse = endpoint.process(request);
      }
      System.out.println(new Date() + " Response received: "+darResponse);
      ClientHelper.printResponse(darResponse);
      return darResponse;
   }
   
   public static void main(String[] args)
   {
      try
      {
         if (args.length == 1)
         {
            Client client = new Client(new URL(args[0]));
            System.out.println("* Synchronous invocation: ");
            client.run(false);
            System.out.println("\n* Asynchronous invocation: ");
            client.run(true);
         }
         else
         {
            System.out.println("Client usage:");
            System.out.println("wsrunclient.sh -classpath client.jar org.jboss.test.ws.jaxws.samples.dar.Client http://host:port/dar?wsdl");
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
