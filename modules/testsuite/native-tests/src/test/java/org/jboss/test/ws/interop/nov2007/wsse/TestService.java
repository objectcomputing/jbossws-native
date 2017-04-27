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
package org.jboss.test.ws.interop.nov2007.wsse;


import javax.xml.bind.JAXBElement;

import org.jboss.test.ws.interop.nov2007.wsse.EchoDataSet.Request;
import org.jboss.test.ws.interop.nov2007.wsse.EchoDataSetResponse.EchoDataSetResult;
import org.jboss.test.ws.interop.nov2007.wsse.EchoXmlResponse.EchoXmlResult;

/**
 * WCF Interoperability Plug-fest - November 2007
 * 
 * IPingService test implementation
 * 
 * @author Alessio Soldano <alessio.soldano@jboss.com>
 * 
 * @since 26-Oct-2007
 */
public class TestService
{
   
   public String ping(String ping)
   {
      System.out.println("ping: " + ping);
      return ping;
   }

   public String echo(String request)
   {
      System.out.println("echo: " + request);
      return request;
   }

   @SuppressWarnings("unchecked")
   public EchoDataSetResult echoDataSet(Request request)
   {
      System.out.println("echoDataSet, request=" + request);
      EchoDataSetResult result = new EchoDataSetResult();
      if (request != null)
      {
         Object any = request.getAny();
         System.out.println("echoDataSet, request.getAny()="+any);
         if (any != null)
         {
            try
            {
               JAXBElement<DataSet> jaxbEl = (JAXBElement<DataSet>)any;
               JAXBElement inner = (JAXBElement)(jaxbEl.getValue().getAny());
               System.out.println("echoDataSet, DataSet inner value=" + inner.getValue());
            } catch (Exception e) {}
         }
         result.setAny(request.getAny());
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   public EchoXmlResult echoXml(org.jboss.test.ws.interop.nov2007.wsse.EchoXml.Request request)
   {
      System.out.println("echoXml, request=" + request);
      EchoXmlResult result = new EchoXmlResult();
      if (request != null)
      {
         Object any = request.getAny();
         System.out.println("echoXml, request.getAny()=" + any);
         if (any != null)
         {
            try
            {
               System.out.println("echoXml, inner value=" + ((JAXBElement)any).getValue());
            } catch (Exception e) {}
         }
         result.setAny(request.getAny());         
      }
      return result;
   }

   public String fault(String request)
   {
      System.out.println("fault: " + request);
      return request;
   }

   public String header(String request)
   {
      System.out.println("header: "+request);
      return request;
   }

   public PingResponse ping(PingRequest parameters)
   {
      System.out.println("ping: " + parameters);
      PingResponse result = new PingResponse();
      PingResponseBody responseBody = new PingResponseBody();
      if (parameters.getPing() != null)
      {
         responseBody.setOrigin(parameters.getPing().getOrigin());
         responseBody.setScenario(parameters.getPing().getScenario());
         responseBody.setText(parameters.getPing().getOrigin() + " : " + parameters.getPing().getText());
      }
      result.setPingResponse(responseBody);
      return result;
   }
   
}
