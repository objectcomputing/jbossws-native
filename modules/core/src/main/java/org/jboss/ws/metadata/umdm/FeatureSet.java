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
package org.jboss.ws.metadata.umdm;

import java.util.HashSet;
import java.util.Set;

import javax.xml.ws.WebServiceFeature;

/**
 * A component that maintains a set of web service features
 *
 * @author Thomas.Diesler@jboss.org
 * @since 12-May-2005
 */
public class FeatureSet
{
   // The features defined for this endpoint
   private Set<WebServiceFeature> features = new HashSet<WebServiceFeature>();

   public <T extends WebServiceFeature> boolean hasFeature(Class<T> key)
   {
      return getFeature(key) != null;
   }

   public <T extends WebServiceFeature> boolean isFeatureEnabled(Class<T> key)
   {
      T feature = getFeature(key);
      return (feature != null ? feature.isEnabled() : false);
   }

   public <T extends WebServiceFeature> T getFeature(Class<T> key)
   {
      for (WebServiceFeature feature : features)
      {
         if (key == feature.getClass())
            return (T)feature;
      }
      return null;
   }

   public void addFeature(WebServiceFeature feature)
   {
      this.features.add(feature);
   }

}
