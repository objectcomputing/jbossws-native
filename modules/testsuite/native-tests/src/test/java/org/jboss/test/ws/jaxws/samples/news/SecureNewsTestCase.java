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
package org.jboss.test.ws.jaxws.samples.news;

import java.net.URL;
import junit.framework.Test;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

/**
 * Tests secure press agency
 * (secure printer cannot be tested since it requires 
 * keystore & trustore in jboss-web tomcat configuration)
 * 
 * @author alessio.soldano@jboss.com
 * @since 01-May-2008
 */
public class SecureNewsTestCase extends JBossWSTest
{
   public static Test suite()
   {
      return new JBossWSTestSetup
      (
         SecureNewsTestCase.class,
         "jaxws-samples-news-step2-newspaper.jar, jaxws-samples-news-step2-agency-client.jar"
      );
   }
   
   public void testAgency() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/news/pressRelease?wsdl");
      SecureAgency agency = new SecureAgency(wsdlURL);
      agency.run("Press release title", "Press release body");
   }
}
