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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

/**
 * Mock implementation of a DAR problem optimizer
 *
 * @author alessio.soldano@jboss.org
 * @since 31-Jan-2008
 */
public class DarProcessor
{
   private Logger log = Logger.getLogger(this.getClass());
   
   public DarResponse process(DarRequest request)
   {
      //FAKE implementation...
      log.info("Processing DAR request... " + request);
      DarResponse response = new DarResponse();
      if (request != null)
      {
         List<Stop> stopList = new LinkedList<Stop>();
         List<ServiceRequest> rejectedRequests = new LinkedList<ServiceRequest>();
         if (!request.getRequests().isEmpty())
         {
            rejectedRequests.add(request.getRequests().remove(0));
            for (ServiceRequest sRequest : request.getRequests())
            {
               Stop from = sRequest.getFrom();
               Stop to = sRequest.getTo();
               if (from != null && to != null)
               {
                  log.info(sRequest.getPeople() + " person(s) from " + from.getNode() + " to " + to.getNode());
                  stopList.add(from);
                  stopList.add(to);
               }
            }
         }
         //computing... ;-)
         try
         {
            Thread.sleep(5000);
         }
         catch (Exception e)
         {
         }
         Map<String,Route> routes = new HashMap<String,Route>();
         if (!request.getBuses().isEmpty())
         {
            for (Bus bus : request.getBuses())
            {
               Route route = new Route();
               route.setBusId(bus.getId());
               route.setStops(new LinkedList<Stop>());
               routes.put(bus.getId(), route);
            }
            Iterator<Stop> itStop = stopList.iterator();
            while (itStop.hasNext())
            {
               int random = new Double(Math.random()*request.getBuses().size()).intValue();
               Bus bus = request.getBuses().get(random);
               Route route = routes.get(bus.getId());
               route.getStops().add(itStop.next());
               route.getStops().add(itStop.next());
            }
         }
         response.setUnservedRequests(rejectedRequests);
         response.setRoutes(new LinkedList<Route>(routes.values()));
         log.info("Done " + request);
      }
      return response;
   }
}
