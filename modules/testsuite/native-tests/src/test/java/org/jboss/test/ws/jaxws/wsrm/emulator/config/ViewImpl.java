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
package org.jboss.test.ws.jaxws.wsrm.emulator.config;

import static org.jboss.test.ws.jaxws.wsrm.emulator.Constant.*;

import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Immutable object implementation representing <b>/views/view</b> configuration element content
 *
 * @author richard.opalka@jboss.com
 *
 * @since Nov 7, 2007
 */
final class ViewImpl implements View
{

   private final String id;
   private final Request req;
   private final Response res;
   
   ViewImpl(Element e, Map<String, String> namespaces)
   {
      this.id = e.getAttribute(ID_ATTRIBUTE);
      NodeList response = e.getElementsByTagName(RESPONSE_ELEMENT);
      this.res = ObjectFactory.getResponse((Element)response.item(0), namespaces); 
      NodeList request = e.getElementsByTagName(REQUEST_ELEMENT);
      this.req = ObjectFactory.getRequest((Element)request.item(0), namespaces); 
   }
   
   public final String getId()
   {
      return this.id;
   }

   public final Request getRequest()
   {
      return this.req;
   }

   public final Response getResponse()
   {
      return this.res;
   }
   
   public final String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(VIEW_ELEMENT).append(EQUAL).append(LEFT_BRACKET);
      sb.append(ID_ATTRIBUTE).append(EQUAL).append(id).append(COMMA).append(SPACE);
      sb.append(REQUEST_ELEMENT).append(EQUAL).append(req).append(COMMA).append(SPACE);
      sb.append(RESPONSE_ELEMENT).append(EQUAL).append(res).append(RIGHT_BRACKET);
      return sb.toString();
   }

}
