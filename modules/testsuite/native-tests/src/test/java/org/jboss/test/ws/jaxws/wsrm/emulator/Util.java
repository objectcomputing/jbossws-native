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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.test.ws.jaxws.wsrm.emulator.config.ObjectFactory;
import org.jboss.test.ws.jaxws.wsrm.emulator.config.View;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility class
 *
 * @author richard.opalka@jboss.com
 *
 * @since Nov 7, 2007
 */
public final class Util
{
   
   private Util()
   {
      // forbidden inheritance
   }
   
   private static final String CRLF = "\r\n";
   
   public static byte[] createHTTPHeaders(URL url, int payloadLength, String contentType)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("POST " + url.getPath() + " HTTP/1.1" + CRLF);
      sb.append("Content-Type: " + contentType + CRLF);
      sb.append("Host: " + url.getHost() + ":" + url.getPort() + CRLF);
      sb.append("Content-Length: " + payloadLength + CRLF);
      sb.append(CRLF);
      return sb.toString().getBytes();
   }

   public static String replace(String oldString, String newString, String data)
   {
      int fromIndex = 0;
      int index = 0;
      StringBuilder result = new StringBuilder();
      
      while ((index = data.indexOf(oldString, fromIndex)) >= 0)
      {
         result.append(data.substring(fromIndex, index));
         result.append(newString);
         fromIndex = index + oldString.length();
      }
      result.append(data.substring(fromIndex));
      return result.toString();
   }
   
   public static String getResourceAsString(ServletContext ctx, String resource)
   throws IOException
   {
      return toString(ctx.getResourceAsStream(resource));
   }

   public static Document getDocument(InputStream is, boolean nsAware)
   throws ServletException
   {
      try 
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware(nsAware);
         return factory.newDocumentBuilder().parse(is);
      }
      catch (SAXException se)
      {
         throw new ServletException(se);
      }
      catch (ParserConfigurationException pce)
      {
         throw new ServletException(pce);
      }
      catch (IOException ie)
      {
         throw new ServletException(ie);
      }
   }

   public static Map<String, String> getNamespaces(Element root)
   {
      NodeList namespaces = ((Element)root.getElementsByTagName(NAMESPACES_ELEMENT).item(0)).getElementsByTagName(NAMESPACE_ELEMENT);
      Map<String, String> retVal = new HashMap<String, String>();
      for (int i = 0; i < namespaces.getLength(); i++)
      {
         Element namespace = (Element)namespaces.item(i);
         String key = PARAGRAPH  + LEFT_BRACKET + namespace.getAttribute(NAME_ATTRIBUTE) + RIGHT_BRACKET;
         retVal.put(key, namespace.getAttribute(VALUE_ATTRIBUTE));
      }
      return retVal;
   }
   
   public static List<View> getViews(Element root, Map<String, String> namespaces)
   {
      List<View> retVal = new LinkedList<View>();
      NodeList views = root.getElementsByTagName(VIEW_ELEMENT);
      for (int i = 0; i < views.getLength(); i++)
      {
         retVal.add(ObjectFactory.getView((Element)views.item(i), namespaces));
      }
      return retVal;
   }
   
   public static Map<String, String> initializeProperties(String reqMsg, Map<String, String> reqMap)
   throws ServletException, IOException
   {
      Document msgDoc = getDocument(new ByteArrayInputStream(reqMsg.getBytes()), true);
      
      Map<String, String> retVal = new HashMap<String, String>();
      for (Iterator<String> i = reqMap.keySet().iterator(); i.hasNext(); )
      {
         String key = i.next();
         String val = reqMap.get(key);
         Element e = getElement(msgDoc, val);
         retVal.put(key, e == null ? "" : e.getTextContent());
      }
      
      return retVal;
   }
   
   public static Map<String, String> prepareReplaceMap(Map<String, String> reqM, Map<String, String> resM)
   {
      Map<String, String> retVal = new HashMap<String, String>();
      
      for (Iterator<String> i = resM.keySet().iterator(); i.hasNext(); )
      {
         String iKey = i.next();
         String iVal = resM.get(iKey);
         for (Iterator<String> j = reqM.keySet().iterator(); j.hasNext(); )
         {
            String jKey = j.next();
            String jVal = reqM.get(jKey);
            String jRef = PARAGRAPH + LEFT_BRACKET + jKey + RIGHT_BRACKET;
            if (iVal.indexOf(jRef) != -1)
            {
               iVal = Util.replace(jRef, jVal, iVal);
            }
         }
         retVal.put(PARAGRAPH + LEFT_BRACKET + iKey + RIGHT_BRACKET, iVal);
      }
      
      return retVal;
   }
   
   public static View getView(String httpMethod, String req, List<View> views) 
   throws ServletException, IOException
   {
      boolean isPost = HTTP_POST.equalsIgnoreCase(httpMethod);
      Document requestMessage = isPost ? getDocument(new ByteArrayInputStream(req.getBytes()), true) : null;
      for (View view : views)
      {
         if (httpMethod.equalsIgnoreCase(view.getRequest().getHttpMethod()))
         {
            if (matches(requestMessage, view))
            {
               return view;
            }
         }
      }
      
      return null;
   }
   
   public static boolean matches(Document req, View view)
   {
      Map<String, String> matches = view.getRequest().getMatches();
      if ((matches == null) || (matches.size() == 0))
         return true;
      
      boolean match = true;
      for (String matchString : matches.keySet())
      {
         match = match && elementExists(req, matchString);
         String equalsValue = matches.get(matchString);
         if (match && (null != equalsValue) && (!"".equals(equalsValue)))
         {
            match = match && equalsValue.equals(getElement(req, matchString).getTextContent());
         }
      }
      
      return match;
   }
   
   public static boolean elementExists(Document doc, String match)
   {
      return getElement(doc, match) != null;
   }
   
   public static Element getElement(Document req, String match)
   {
      Element e = null;
      
      StringTokenizer st = new StringTokenizer(match, SEPARATOR);
      while (st.hasMoreTokens())
      {
         String toConvert = st.nextToken();
         QName nodeName = QName.valueOf(toConvert);
         e = getChildElement(e != null ? e : req, nodeName);
         if (e == null) return null;
      }
      
      return e;
   }
   
   public static Element getChildElement(Node e, QName nodeQName)
   {
      NodeList childNodes = e.getChildNodes();
      if (childNodes == null)
         return null;
      
      for (int i = 0; i < childNodes.getLength(); i++)
      {
         Node node = childNodes.item(i);
         if (node.getNodeType() == Node.ELEMENT_NODE)
         {
            Element element = (Element)node;
            boolean namespaceMatches = nodeQName.getNamespaceURI().equals(element.getNamespaceURI());
            boolean localNameMatches = nodeQName.getLocalPart().equals(element.getLocalName());
            if (namespaceMatches && localNameMatches)
               return element;
         }
      }
      
      return null;
   }
   
   public static String replaceAll(String response, Map<String, String> replaceMap)
   {
      if ((replaceMap != null) && (replaceMap.size() > 0))
      {
         for (Iterator<String> iterator = replaceMap.keySet().iterator(); iterator.hasNext(); )
         {
            String oldString = iterator.next();
            String newString = replaceMap.get(oldString);
            response = Util.replace(oldString, newString, response);
         }
      }
      return response;
   }
   
   public static String getRequestMessage(HttpServletRequest req)
   throws IOException
   {
      BufferedReader reader = req.getReader();
      String line = null;
      StringBuilder sb = new StringBuilder();
      while ((line = reader.readLine()) != null)
      {
         sb.append(line);
      }
      return sb.toString();
   }

   public static String toString(InputStream is)
   throws IOException
   {
      if (is == null)
         return "";
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int offset = -1;
      while ((offset = is.read(buffer, 0, buffer.length)) != -1)
      {
         baos.write(buffer, 0, offset);
      }
      return baos.toString();
   }
   
}
