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

import java.io.PrintStream;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.ws.BindingProvider;

import org.jboss.test.ws.jaxws.samples.dar.generated.Bus;
import org.jboss.test.ws.jaxws.samples.dar.generated.DarRequest;
import org.jboss.test.ws.jaxws.samples.dar.generated.DarResponse;
import org.jboss.test.ws.jaxws.samples.dar.generated.Route;
import org.jboss.test.ws.jaxws.samples.dar.generated.ServiceRequest;
import org.jboss.test.ws.jaxws.samples.dar.generated.Stop;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class ClientHelper
{
   public static void setUsernamePassword(BindingProvider bp, String username, String password)
   {
      bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
      bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
   }
   
   public static DarRequest getRequest()
   {
      DarRequest request = new DarRequest();
      request.setMapId("map1234");
      for (int s=0; s<5; s++)
      {
         ServiceRequest serviceRequest = new ServiceRequest();
         Stop up = new Stop();
         up.setNode(new Double(Math.random()*1000).intValue());
         up.setTime(new XMLGregorianCalendarImpl(new GregorianCalendar()));
         Stop down = new Stop();
         down.setNode(new Double(Math.random()*1000).intValue());
         down.setTime(new XMLGregorianCalendarImpl(new GregorianCalendar()));
         serviceRequest.setFrom(up);
         serviceRequest.setTo(down);
         serviceRequest.setPeople(new Double(Math.random()*3).intValue()+1);
         serviceRequest.setId("Req" + s);
         request.getRequests().add(serviceRequest);
      }
      for (int b=0; b<2; b++)
      {
         Bus bus = new Bus();
         bus.setCapacity(10);
         bus.setId("Bus" + b);
         request.getBuses().add(bus);
      }
      return request;
   }
   
   public static void printResponse(DarResponse response)
   {
      printResponse(response, System.out);
   }
   
   public static void printResponse(DarResponse response, PrintStream output)
   {
      List<Route> routes = response.getRoutes();
      for (Route route : routes)
      {
         output.print(route.getBusId() + ": ");
         for (Stop stop : route.getStops())
         {
            output.print(stop.getNode() + " ");
         }
         output.print("\n");
      }
   }
}
