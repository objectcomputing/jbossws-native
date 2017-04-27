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

import javax.xml.ws.BindingProvider;

/**
 * The printer client using https transport
 *
 * @author alessio.soldano@jboss.org
 * @since 31-Jan-2008
 */
public class SecurePrinter extends Printer
{
   public SecurePrinter(URL url, boolean mtom)
   {
      super(url,mtom);
      BindingProvider bp = mtom ? (BindingProvider)mtomEndpoint : (BindingProvider)swaEndpoint;
      bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "kermit");
      bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "thefrog");
      System.setProperty("org.jboss.security.ignoreHttpsHost", "true");
   }
   
   public static void main(String[] args)
   {
      try
      {
         if (args.length == 1)
         {
            SecurePrinter printer = new SecurePrinter(new URL(args[0]), args[0].endsWith("mtom?wsdl"));
            printer.run();
         }
         else
         {
            System.out.println("SecurePrinter client usage:");
            System.out.println("wsrunclient.sh -classpath agency.jar -Djavax.net.ssl.trustStore=truststorePath -Djavax.net.ssl.trustStorePassword=truststorePwd " +
            		"org.jboss.test.ws.jaxws.samples.news.SecurePrinter http://host:port/news/newspaper/mtom?wsdl");
            System.out.println("or");
            System.out.println("wsrunclient.sh -classpath agency.jar -Djavax.net.ssl.trustStore=truststorePath -Djavax.net.ssl.trustStorePassword=truststorePwd " +
            		"org.jboss.test.ws.jaxws.samples.news.SecurePrinter http://host:port/news/newspaper/swa?wsdl");
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
