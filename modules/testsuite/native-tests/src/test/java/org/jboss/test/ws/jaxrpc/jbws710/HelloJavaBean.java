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
package org.jboss.test.ws.jaxrpc.jbws710;

import java.rmi.RemoteException;

import org.jboss.logging.Logger;

public class HelloJavaBean implements Hello {
	
	private static Logger log = Logger.getLogger(HelloJavaBean.class);
   
   private static String retStr;

   public void onewayRequest(String str1, String str2, String str3) throws RemoteException
   {
      log.info("onewayRequest: " + str1 + str2 + str3);
      retStr = str1 + str2 + str3;
   }

   public String onewayResponse() throws RemoteException
   {
      log.info("onewayResponse: " + retStr);
      return retStr;
   }
}
