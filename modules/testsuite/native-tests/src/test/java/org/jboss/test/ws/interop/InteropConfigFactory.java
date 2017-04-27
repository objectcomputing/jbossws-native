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

import org.jboss.logging.Logger;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.test.ws.interop.ClientScenario;
import org.jboss.test.ws.interop.InteropClientConfig;
import org.w3c.dom.Element;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Create an object model frpom test scenario descriptors.
 *
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Aug 22, 2006
 */
public class InteropConfigFactory {

   private static Logger log = Logger.getLogger(InteropConfigFactory.class);

   public static InteropConfigFactory newInstance()
   {
      return new InteropConfigFactory();
   }

   public InteropClientConfig createClientConfig()
   {
      URLClassLoader ctxLoader = (URLClassLoader)Thread.currentThread().getContextClassLoader();
      URL scenariosDescriptor = ctxLoader.findResource("META-INF/scenarios.xml");
      if(scenariosDescriptor!=null)
      {
         try
         {
            Element configRoot = DOMUtils.parse( scenariosDescriptor.openStream() );
            return new InteropClientConfig(configRoot);
         }
         catch (IOException e)
         {
            throw new IllegalStateException("Error parsing META-INF/scenarios.xml", e);
         }
      }
      else
      {
         throw new IllegalStateException("Failed to load META-INF/scenarios.xml");
      }

   }

   public ClientScenario createClientScenario(String scenarioName)
   {
      InteropClientConfig clientConfig = createClientConfig();
      ClientScenario sc = null;
      if (scenarioName != null)
      {
         sc = clientConfig.getScenario(scenarioName);
      }
      if(null==sc)
         sc = clientConfig.getScenario("default");

      return sc;
   }
}
