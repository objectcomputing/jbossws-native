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
package org.jboss.test.ws.jaxrpc.jbws1647;

import org.jboss.wsf.test.JBossWSTestSetup;

import junit.framework.Test;

/**
 * Text Node Preservation For Messages Sent Across The Wire
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1647
 * 
 * @author darran.lofthouse@jboss.com
 * @since 15 May 2007
 */
public class JBWS1647DocumentTestCase extends JBWS1647TestBase
{

   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(JBWS1647DocumentTestCase.class, "jaxrpc-jbws1647-doclit.war");
   }

   public String getMessage()
   {
      return "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>" + DocumentHandler.MESSAGE_BODY + "</env:Envelope>";
   }

   public String getEndpointAddress()
   {
      return "http://" + getServerHost() + ":8080/jaxrpc-jbws1647-doclit/TestEndpoint";
   }

}
