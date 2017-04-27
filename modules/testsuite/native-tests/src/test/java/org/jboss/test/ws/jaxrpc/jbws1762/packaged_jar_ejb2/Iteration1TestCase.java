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
package org.jboss.test.ws.jaxrpc.jbws1762.packaged_jar_ejb2;

import junit.framework.Test;

import org.jboss.test.ws.jaxrpc.jbws1762.AbstractEJB2Test;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * [JBWS-1762] web.xml modified to web.xml.org - subsequent runs fail
 *
 * @author richard.opalka@jboss.com
 *
 * @since Oct 20, 2007
 */
public class Iteration1TestCase extends AbstractEJB2Test
{
   public static Test suite() throws Exception
   {
      return new JBossWSTestSetup(Iteration1TestCase.class, "jaxrpc-jbws1762-packaged.jar");
   }
   
   public String getWSDLLocation()
   {
      return "jaxrpc-jbws1762-packaged-jar/EJB2Bean?wsdl";
   }
}
