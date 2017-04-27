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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.GregorianCalendar;

import javax.activation.DataHandler;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;

import org.jboss.test.ws.jaxws.samples.news.generated.printer.mtom.EditionMTOM;
import org.jboss.test.ws.jaxws.samples.news.generated.printer.mtom.NewspaperMTOMService;
import org.jboss.test.ws.jaxws.samples.news.generated.printer.mtom.NewspaperMTOMEndpoint;
import org.jboss.test.ws.jaxws.samples.news.generated.printer.swa.EditionSWA;
import org.jboss.test.ws.jaxws.samples.news.generated.printer.swa.NewspaperSWAService;
import org.jboss.test.ws.jaxws.samples.news.generated.printer.swa.NewspaperSWAEndpoint;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

/**
 * The printer client
 *
 * @author alessio.soldano@jboss.org
 * @since 31-Jan-2008
 */
public class Printer
{
   protected NewspaperMTOMEndpoint mtomEndpoint;
   protected NewspaperSWAEndpoint swaEndpoint;
   protected boolean mtom;
   
   public Printer(URL url, boolean mtom)
   {
      this.mtom = mtom;
      if (mtom)
      {
         NewspaperMTOMService mtomService = new NewspaperMTOMService(url, new QName("http://org.jboss.ws/samples/news", "NewspaperMTOMService"));
         mtomEndpoint = mtomService.getNewspaperMTOMEndpointPort();
      }
      else
      {
         NewspaperSWAService swaService = new NewspaperSWAService(url, new QName("http://org.jboss.ws/samples/news", "NewspaperSWAService"));
         swaEndpoint = swaService.getNewspaperSWAEndpointPort();
      }
   }
   
   public void run() throws IOException
   {
      XMLGregorianCalendar from = new XMLGregorianCalendarImpl(new GregorianCalendar(2008,1,10));
      XMLGregorianCalendar to = new XMLGregorianCalendarImpl(new GregorianCalendar(2008,1,14));
      if (mtom)
      {
         ((SOAPBinding)(((BindingProvider)mtomEndpoint).getBinding())).setMTOMEnabled(true);
         for (String id : mtomEndpoint.getNewspaperEditionIdList(from, to).getItem())
         {
            System.out.println("Downloading newspaper document: " + id);
            EditionMTOM edition = mtomEndpoint.getNewspaperEdition(id);
            System.out.println("Content: " + edition.getContent());
         }
      }
      else
      {
         for (String id : swaEndpoint.getNewspaperEditionIdList(from, to).getItem())
         {
            System.out.println("Downloading newspaper document: " + id);
            EditionSWA edition = swaEndpoint.getNewspaperEdition(id);
            DataHandler dh = edition.getContent();
            System.out.println("Content type: " + dh.getContentType());
            Object dataContent = dh.getContent();
            System.out.println("Content: " + dataContent);
            if (dataContent instanceof InputStream)
            {
               ((InputStream)dataContent).close();
            }
         }
      }
   }
   
   public static void main(String[] args)
   {
      try
      {
         if (args.length == 1)
         {
            Printer printer = new Printer(new URL(args[0]), args[0].endsWith("mtom?wsdl"));
            printer.run();
         }
         else
         {
            System.out.println("Printer client usage:");
            System.out.println("wsrunclient.sh -classpath agency.jar org.jboss.test.ws.jaxws.samples.news.Printer http://host:port/news/newspaper/mtom?wsdl");
            System.out.println("or");
            System.out.println("wsrunclient.sh -classpath agency.jar org.jboss.test.ws.jaxws.samples.news.Printer http://host:port/news/newspaper/swa?wsdl");
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
