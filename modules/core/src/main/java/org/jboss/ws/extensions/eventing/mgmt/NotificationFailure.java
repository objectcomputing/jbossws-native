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
package org.jboss.ws.extensions.eventing.mgmt;

import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.ws.WSException;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Element;

/**
 * Represent an error during notification.
 *
 * @author Stefano Maestri <stefano.maestri@javalinux.it>, Alessio Soldano <alessio.soldano@javalinux.it>
 * @since 26/11/2006
 */

public class NotificationFailure
{
   private URI endTo;
   private Element event;
   private Exception exception;

   public NotificationFailure(String endTo, Element event, Exception exception)
   {
      super();
      try {
         this.endTo = new URI(endTo);
      } catch (URISyntaxException e) {
         throw new WSException(e);
      }
      this.event = event;
      this.exception = exception;
   }
   public URI getEndTo()
   {
      return endTo;
   }
   public void setEndTo(URI endTo)
   {
      this.endTo = endTo;
   }
   public Element getEvent()
   {
      return event;
   }
   public void setEvent(Element event)
   {
      this.event = event;
   }
   public Exception getException()
   {
      return exception;
   }
   public void setException(Exception exception)
   {
      this.exception = exception;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("endTo: ");
      sb.append(endTo);
      sb.append("\n\nevent: ");
      sb.append(DOMWriter.printNode(event, false));
      sb.append("\n\nexception: ");
      sb.append(exception);
      sb.append("\n*******************\n");
      return sb.toString();
   }

}
