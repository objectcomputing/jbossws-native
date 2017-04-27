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
package org.jboss.test.ws.jaxws.wsrm.emulator;

import static org.jboss.test.ws.jaxws.wsrm.emulator.Constant.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.test.ws.jaxws.wsrm.emulator.config.View;
import org.w3c.dom.Element;

/**
 * This class represents Controller part of the MVC architectural pattern
 *
 * @author richard.opalka@jboss.com
 *
 * @since Oct 24, 2007
 */
public class EndpointEmulator extends HttpServlet
{
   
   private static final Random generator = new Random();
   private String configFile;
   private ServletContext ctx;
   private List<View> views;
   
   @Override
   public void init(ServletConfig config) throws ServletException
   {
      super.init(config);
      
      // initialize context variable
      ctx = getServletContext();
      
      // ensure there's a config.file servlet init parameter specified in web.xml
      configFile = config.getInitParameter(CONFIG_FILE);
      if (configFile == null)
         throw new RuntimeException(CONFIG_FILE + " init parameter is missing");
      
      // ensure this config.file points to correct resource inside war archive
      InputStream is = ctx.getResourceAsStream(configFile);
      if (is == null)
         throw new RuntimeException("Resource " + configFile + " not found");
      
      Element root = Util.getDocument(is, false).getDocumentElement();
      views = Util.getViews(root, Util.getNamespaces(root)); 
   }
   
   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse res)
   throws ServletException, IOException
   {
      handleRequest(HTTP_POST, req, res);
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse res)
   throws ServletException, IOException
   {
      handleRequest(HTTP_GET, req, res);
   }
   
   private void handleRequest(String httpMethod, HttpServletRequest req, HttpServletResponse resp)
   throws ServletException, IOException
   {
      String requestMessage = Util.getRequestMessage(req); 
      View view = Util.getView(httpMethod, requestMessage, views);
      ctx.log(configFile + SEPARATOR + view.getId());
      Map<String, String> resProperties = view.getResponse().getProperties();
      Map<String, String> reqProperties = view.getRequest().getProperties();
      String responseMessage = null;
      if (view.getResponse().getResource() != null)
      {
         responseMessage = Util.getResourceAsString(ctx, view.getResponse().getResource());
      }
      String responseTo = null;
      
      if (resProperties.size() > 0)
      {
         Map<String, String> initializedReqProperties = null;
         if (reqProperties.size() > 0)
         {
            initializedReqProperties = Util.initializeProperties(requestMessage, reqProperties);
         }
         
         Map<String, String> replaceMap = Util.prepareReplaceMap(initializedReqProperties, resProperties);
         if (responseMessage != null)
         {
            responseMessage = Util.replaceAll(responseMessage, replaceMap);
         }
         responseTo = replaceMap.get(RESPONSE_TO);
         if (ADDRESSING_ANONYMOUS_URI.equals(responseTo))
         {
            responseTo = null;
         }
      }
      
      boolean returnServerError = HTTP_POST.equalsIgnoreCase(httpMethod) ? (generator.nextInt(3) % 3 == 1) : false;
      if (returnServerError)
      {
         resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         ctx.log("returning HTTP code 500");
         PrintWriter out = resp.getWriter();
         out.write("<html><body><h1>Internal Server Error</h1><p>Generated by endpoint emulator for testing purposes.</p></body></html>");
         out.flush();
         out.close();
         return;
      }
      
      boolean delayResponse = HTTP_POST.equalsIgnoreCase(httpMethod) ? (generator.nextInt(3) % 3 == 1) : false;
      if (delayResponse)
      {
         try
         {
            ctx.log("delaying response, will be send in 5 seconds");
            Thread.sleep(5 * 1000);
         } 
         catch (InterruptedException ie)
         {
            ctx.log(ie.getMessage(), ie);
         }
      }
      
      if (responseTo == null)
      {
         ctx.log("Sending response through ServletResponse");
         if (view.getResponse().getContentType() != null)
         {
            resp.setContentType(view.getResponse().getContentType());
         }
         resp.setStatus(Integer.valueOf(view.getResponse().getStatusCode()));
         if (responseMessage != null)
         {
            PrintWriter writer = resp.getWriter();
            writer.print(responseMessage);
            writer.flush();
            writer.close();
         }
      }
      else
      {
         if (responseMessage != null)
         {
            ctx.log("Sending response through new socket connection");
            URL url = new URL(responseTo);
            Socket socket = new Socket(url.getHost(), url.getPort());
            OutputStream out = socket.getOutputStream();
            out.write(Util.createHTTPHeaders(url, responseMessage.length(), view.getResponse().getContentType()));
            out.write(responseMessage.getBytes());
            out.flush();
            out.close();
         }
      }
   }
   
}
