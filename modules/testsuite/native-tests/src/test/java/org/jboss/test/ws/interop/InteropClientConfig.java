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

import org.jboss.wsf.common.DOMUtils;
import org.jboss.test.ws.interop.ClientScenario;
import org.w3c.dom.Element;

import java.net.URL;
import java.util.Iterator;

/**
 * Represents a interop test client configuration model.
 *
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Aug 22, 2006
 */
public class InteropClientConfig {
   private Element configRoot;

   public InteropClientConfig(Element configRoot) {
      this.configRoot = configRoot;
   }

   public ClientScenario getScenario(String name)
   {
      ClientScenario cs = null;

      try
      {
         cs = null;
         Iterator it = DOMUtils.getChildElements(configRoot);
         while(it.hasNext())
         {
            Element child = (Element)it.next();
            String scenarioName = DOMUtils.getAttributeValue(child, "name");
            if(name.equals(scenarioName))
            {
               // mandatory elements
               Element targetEndpoint = DOMUtils.getFirstChildElement(child, "target-endpoint");
               URL endpointURL = new URL(targetEndpoint.getTextContent());
               cs = new ClientScenario(scenarioName, endpointURL);

               // todo: add parameter element parsing here
               Iterator parameter = DOMUtils.getChildElements(child, "param");
               while(parameter.hasNext())
               {
                  Element param = (Element)parameter.next();
                  String key = DOMUtils.getAttributeValue(param, "name");
                  String value = DOMUtils.getAttributeValue(param, "value");
                  cs.getParameterMap().put(key, value);
               }
               break;
            }
         }
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException(e);
      }

      return cs;
   }

}
