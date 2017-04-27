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
package org.jboss.ws.extensions.xop;

import java.util.Iterator;

import org.jboss.ws.core.soap.SAAJVisitor;
import org.jboss.ws.core.soap.SOAPContentElement;
import org.jboss.ws.core.soap.SOAPElementImpl;

/**
 * Visit soap object model and restore XOP contents.
 * This visitor is invoked when:
 * <ul>
 *    <li>Client side request handler chain has been executed
 *    <li>Server side response or fault handler chain been executed
 * </ul>
 *
 * It basically takes care that when jaxrpc handlers have been in place,
 * the XOP contents are being restored upon request and response.
 * 
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Sep 26, 2006
 */
public class RestoreXOPElementVisitor implements SAAJVisitor {

   public void visitXOPElements(SOAPElementImpl root)
   {
      boolean isSCE = (root instanceof SOAPContentElement);

      // don't expand SOAPContentElements
      if(isSCE)
      {
         root.accept(this);
      }
      else
      {
         Iterator it = root.getChildElements();
         while(it.hasNext())
         {
            final Object o = it.next();
            if(o instanceof SOAPElementImpl)
               visitXOPElements((SOAPElementImpl)o);
         }
      }
   }

   public void visitSOAPElement(SOAPElementImpl soapElement) {
      // nada
   }

   public void visitSOAPContentElement(SOAPContentElement scElement) {
      scElement.handleMTOMTransitions();
   }
}
