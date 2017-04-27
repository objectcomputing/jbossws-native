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

import java.util.GregorianCalendar;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

public class ReplyConverter
{
   public static org.jboss.test.ws.jaxws.samples.dar.generated.reply.DarResponse convertResponse(DarResponse response)
   {
      if (response == null)
         return null;
      org.jboss.test.ws.jaxws.samples.dar.generated.reply.DarResponse r = new org.jboss.test.ws.jaxws.samples.dar.generated.reply.DarResponse();
      for (Route route : response.getRoutes())
      {
         r.getRoutes().add(convertRoute(route));
      }
      for (ServiceRequest request : response.getUnservedRequests())
      {
         r.getUnservedRequests().add(convertServiceRequest(request));
      }
      return r;
   }
   
   public static org.jboss.test.ws.jaxws.samples.dar.generated.reply.ServiceRequest convertServiceRequest(ServiceRequest request)
   {
      if (request == null)
         return null;
      org.jboss.test.ws.jaxws.samples.dar.generated.reply.ServiceRequest s = new org.jboss.test.ws.jaxws.samples.dar.generated.reply.ServiceRequest();
      s.setFrom(convertStop(request.getFrom()));
      s.setTo(convertStop(request.getTo()));
      s.setId(request.getId());
      s.setPeople(request.getPeople());
      return s;
   }

   public static org.jboss.test.ws.jaxws.samples.dar.generated.reply.Route convertRoute(Route route)
   {
      if (route == null)
         return null;
      org.jboss.test.ws.jaxws.samples.dar.generated.reply.Route r = new org.jboss.test.ws.jaxws.samples.dar.generated.reply.Route();
      r.setBusId(route.getBusId());
      for (Stop stop : route.getStops())
      {
         r.getStops().add(convertStop(stop));
      }
      return r;
   }
   
   public static org.jboss.test.ws.jaxws.samples.dar.generated.reply.Stop convertStop(Stop stop)
   {
      if (stop == null)
         return null;
      org.jboss.test.ws.jaxws.samples.dar.generated.reply.Stop s = new org.jboss.test.ws.jaxws.samples.dar.generated.reply.Stop();
      s.setNode(stop.getNode());
      GregorianCalendar cal = new GregorianCalendar();
      cal.setTime(stop.getTime());
      s.setTime(new XMLGregorianCalendarImpl(cal));
      return s;
   }
}
