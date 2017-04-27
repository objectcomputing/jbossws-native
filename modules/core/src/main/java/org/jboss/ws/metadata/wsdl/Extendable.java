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
package org.jboss.ws.metadata.wsdl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

/**
 * An abstract base class of a WSDL extendable element.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Oct-2004
 */
public abstract class Extendable implements Serializable
{
   private static final long serialVersionUID = -3164210175211099488L;

   // provide logging
   private Logger log = Logger.getLogger(getClass());
   
   private Map features = new LinkedHashMap();
   private Map properties = new LinkedHashMap();
   private Map<String,List<WSDLExtensibilityElement>> extElements = new LinkedHashMap<String,List<WSDLExtensibilityElement>>();
   private List<WSDLExtensibilityElement> notUnderstoodExtElements = new LinkedList<WSDLExtensibilityElement>();

   public WSDLFeature[] getFeatures()
   {
      WSDLFeature[] arr = new WSDLFeature[features.size()];
      new ArrayList(features.values()).toArray(arr);
      return arr;
   }

   public void addFeature(WSDLFeature feature)
   {
      log.trace("addFeature: " + feature);
      String uri = feature.getURI();
      features.put(uri, feature);
   }

   public WSDLFeature getFeature(String uri)
   {
      WSDLFeature feature = (WSDLFeature)features.get(uri);
      return feature;
   }

   public WSDLProperty[] getProperties()
   {
      WSDLProperty[] arr = new WSDLProperty[properties.size()];
      new ArrayList(properties.values()).toArray(arr);
      return arr;
   }

   public void addProperty(WSDLProperty property)
   {
      log.trace("addProperty: " + property);
      String uri = property.getURI();
      properties.put(uri, property);
   }

   public WSDLProperty getProperty(String uri)
   {
      WSDLProperty property = (WSDLProperty)properties.get(uri);
      return property;
   }
   
   public void addExtensibilityElement(WSDLExtensibilityElement extElement)
   {
      log.trace("addExtensibilityElement: " + extElement);
      String uri = extElement.getUri();
      List<WSDLExtensibilityElement> list = extElements.get(uri);
      if (list == null)
      {
         list = new LinkedList<WSDLExtensibilityElement>();
         extElements.put(uri,list);
      }
      list.add(extElement);
   }

   public List<WSDLExtensibilityElement> getExtensibilityElements(String uri)
   {
      List<WSDLExtensibilityElement> list = extElements.get(uri);
      return list == null ? new ArrayList<WSDLExtensibilityElement>() : list;
   }
   
   public List<WSDLExtensibilityElement> getAllExtensibilityElements()
   {
      List<WSDLExtensibilityElement> list = new LinkedList<WSDLExtensibilityElement>();
      for (String k : extElements.keySet())
      {
         list.addAll(extElements.get(k));
      }
      return list;
   }

   public List<WSDLExtensibilityElement> getNotUnderstoodExtElements()
   {
      if (notUnderstoodExtElements == null)
      {
         notUnderstoodExtElements = new LinkedList<WSDLExtensibilityElement>();
      }
      return notUnderstoodExtElements;
   }

   public void setNotUnderstoodExtElements(List<WSDLExtensibilityElement> notUnderstoodExtElement)
   {
      this.notUnderstoodExtElements = notUnderstoodExtElement;
   }
   
   public void addNotUnderstoodExtElement(WSDLExtensibilityElement element)
   {
      if (notUnderstoodExtElements == null)
      {
         notUnderstoodExtElements = new LinkedList<WSDLExtensibilityElement>();
      }
      notUnderstoodExtElements.add(element);
   }
}
