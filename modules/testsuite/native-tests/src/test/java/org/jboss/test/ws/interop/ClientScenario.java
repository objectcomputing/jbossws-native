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
package org.jboss.test.ws.interop;

import java.net.URL;
import java.util.Map;
import java.util.HashMap;

/**
 * A particular interop test scenario.
 *
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Aug 22, 2006
 */
public class ClientScenario {

   private String name;
   private String description;
   private URL targetEndpoint;
   private Map<String, String> parameterMap;

   public ClientScenario(String name, URL targetEndpoint) {
      this.name = name;
      this.targetEndpoint = targetEndpoint;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public URL getTargetEndpoint() {
      return targetEndpoint;
   }

   public String getName() {
      return name;
   }

   Map<String,String> getParameterMap() {
      if(this.parameterMap == null)
         parameterMap = new HashMap<String, String>();
      return parameterMap;
   }

   public String getParameter(String key)
   {
      return getParameterMap().get(key);
   }

   public String toString() {
      return "ClientScenario {name="+name+", endpointURL="+targetEndpoint+", params="+getParameterMap()+"}";
   }

}
