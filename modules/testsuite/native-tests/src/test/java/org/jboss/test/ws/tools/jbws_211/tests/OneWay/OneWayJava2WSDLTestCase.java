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
package org.jboss.test.ws.tools.jbws_211.tests.OneWay;

import org.jboss.test.ws.tools.jbws_211.tests.JBWS211Test;

/**
 *  JBWS-211: Java To WSDL 1.1 Comprehensive Test Collection
 *  Tests generation of Java -> WSDL 1.1 for OneWay web services
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Sep 24, 2005
 */
public class OneWayJava2WSDLTestCase extends JBWS211Test
{
   private String base = "OneWay";

   public String getBase()
   {
      return base;
   }      
   
   public String getWSDLName()
   {
      return base + "Service.wsdl";
   }
}
