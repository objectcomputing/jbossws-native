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

import java.io.Serializable;

public class ServiceRequest implements Serializable
{
   private String id;
   private Stop from;
   private Stop to;
   private int people;
   
   public Stop getFrom()
   {
      return from;
   }
   public void setFrom(Stop from)
   {
      this.from = from;
   }
   public Stop getTo()
   {
      return to;
   }
   public void setTo(Stop to)
   {
      this.to = to;
   }
   public int getPeople()
   {
      return people;
   }
   public void setPeople(int people)
   {
      this.people = people;
   }
   public String getId()
   {
      return id;
   }
   public void setId(String id)
   {
      this.id = id;
   }
}
