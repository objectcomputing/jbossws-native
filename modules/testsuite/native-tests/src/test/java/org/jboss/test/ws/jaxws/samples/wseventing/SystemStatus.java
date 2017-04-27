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
package org.jboss.test.ws.jaxws.samples.wseventing;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * @author Heiko.Braun@jboss.com
 */
@XmlRootElement(name="SystemStatus", namespace = "http://www.jboss.org/sysmon")
public class SystemStatus
{
   private Date time;
   private String hostname;
   private String hostAddress;
   private int activeThreadCount;
   private String freeMemory;
   private String maxMemory;


   public Date getTime()
   {
      return time;
   }

   public void setTime(Date time)
   {
      this.time = time;
   }

   public String getHostname()
   {
      return hostname;
   }

   public void setHostname(String hostname)
   {
      this.hostname = hostname;
   }

   public String getHostAddress()
   {
      return hostAddress;
   }

   public void setHostAddress(String hostAddress)
   {
      this.hostAddress = hostAddress;
   }

   public int getActiveThreadCount()
   {
      return activeThreadCount;
   }

   public void setActiveThreadCount(int activeThreadCount)
   {
      this.activeThreadCount = activeThreadCount;
   }

   public String getFreeMemory()
   {
      return freeMemory;
   }

   public void setFreeMemory(String freeMemory)
   {
      this.freeMemory = freeMemory;
   }

   public String getMaxMemory()
   {
      return maxMemory;
   }

   public void setMaxMemory(String maxMemory)
   {
      this.maxMemory = maxMemory;
   }


   public String toString()
   {
      return "SystemStatus {hostname="+hostname+", freeMemory="+freeMemory+"}";
   }
}
