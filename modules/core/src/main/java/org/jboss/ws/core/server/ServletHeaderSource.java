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
package org.jboss.ws.core.server;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;

/**
 * Implementation of <code>HeaderSource</code> that pulls header
 * information from an HttpServlet.
 *
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 * @author Thomas.Diesler@jboss.org
 */
public class ServletHeaderSource implements MimeHeaderSource
{
   private HttpServletRequest req;
   private HttpServletResponse res;

   public ServletHeaderSource(HttpServletRequest req, HttpServletResponse res)
   {
      this.req = req;
      this.res = res;
   }

   public MimeHeaders getMimeHeaders()
   {
      Enumeration e = req.getHeaderNames();
      if (e == null)
         return null;

      MimeHeaders headers = new MimeHeaders();

      while (e.hasMoreElements())
      {
         String name = (String)e.nextElement();
         headers.addHeader(name, req.getHeader(name));
      }

      return headers;
   }

   public Map<String, List<String>> getHeaderMap()
   {
      Map<String, List<String>> headerMap = new HashMap<String, List<String>>();

      Enumeration e = req.getHeaderNames();
      if (e != null)
      {
         while (e.hasMoreElements())
         {
            String name = (String)e.nextElement();
            List<String> values = new ArrayList<String>();
            values.add(req.getHeader(name));
            headerMap.put(name, values);
         }
      }

      return headerMap;
   }

   public void setMimeHeaders(MimeHeaders headers)
   {
      Iterator i = headers.getAllHeaders();
      while (i.hasNext())
      {
         MimeHeader header = (MimeHeader)i.next();
         res.addHeader(header.getName(), header.getValue());
      }
   }

   public void setHeaderMap(Map<String, List<String>> headers)
   {
      Iterator<String> it = headers.keySet().iterator();
      while (it.hasNext())
      {
         String name = it.next();
         List<String> values = headers.get(name);
         for (String value : values)
         {
            res.addHeader(name, value);
         }
      }
   }
}
