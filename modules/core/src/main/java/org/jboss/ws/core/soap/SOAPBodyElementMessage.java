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
package org.jboss.ws.core.soap;

import java.io.Writer;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.soap.SOAPBodyElement;

import org.jboss.wsf.common.DOMWriter;

/**
 * An abstract implemenation of the SOAPBodyElement
 * <p/>
 * This class should not expose functionality that is not part of
 * {@link javax.xml.soap.SOAPBodyElement}. Client code should use <code>SOAPBodyElement</code>.
 *
 * @author Thomas.Diesler@jboss.org
 */
public class SOAPBodyElementMessage extends SOAPElementImpl implements SOAPBodyElement
{
   public SOAPBodyElementMessage(QName name)
   {
      super(name);
   }

   public SOAPBodyElementMessage(SOAPElementImpl element)
   {
      super(element);
   }

   public void writeElement(Writer writer) {
      try
      {
         DOMWriter domWriter = new DOMWriter(writer);
         domWriter.print(this);
      }
      catch (Exception e)
      {
         throw new JAXRPCException(e);
      }
   }
}
