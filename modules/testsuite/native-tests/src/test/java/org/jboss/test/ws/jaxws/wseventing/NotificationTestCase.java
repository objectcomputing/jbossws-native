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
package org.jboss.test.ws.jaxws.wseventing;

import javax.naming.InitialContext;

import junit.framework.Test;

import org.jboss.ws.extensions.eventing.EventingConstants;
import org.jboss.ws.extensions.eventing.jaxws.SubscribeResponse;
import org.jboss.ws.extensions.eventing.mgmt.EventDispatcher;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Test the notification delivery.
 *
 * @author heiko@openj.net
 * @since 29-Apr-2005
 */
public class NotificationTestCase extends EventingSupport
{
   public static Test suite()
   {
      return new JBossWSTestSetup(NotificationTestCase.class, "jaxws-wseventing.war");
   }

   public void testNotification() throws Exception {

      SubscribeResponse response = doSubscribe();

      Element payload = DOMUtils.parse(eventString);
      try
      {
         InitialContext iniCtx = getInitialContext();
         EventDispatcher delegate = (EventDispatcher)
               iniCtx.lookup(EventingConstants.DISPATCHER_JNDI_NAME);
         delegate.dispatch(eventSourceURI, payload);
         Thread.sleep(3000);         
      }
      catch (Exception e)
      {         
         throw e;
      }
   }

   public void testInVMNotification() throws Exception
   {
      URL u = new URL ( "http://"+getServerHost()+":8080/jaxws-wseventing/inVM" );
      HttpURLConnection huc = (HttpURLConnection) u.openConnection();
      huc.setRequestMethod("GET");
      huc.connect();

      StringBuffer sb = new StringBuffer();

      int code = huc.getResponseCode();
      if  (code>=200 &&  code<300 )
      {
         InputStream in = huc.getInputStream();
         BufferedReader input = new BufferedReader(new InputStreamReader(in));
         String line = "";
         while ((line = input.readLine()) != null)
            sb.append(line);
      }

      assertEquals(sb.toString(), "Notification successful");
      huc.disconnect();
   }

}
