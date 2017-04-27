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

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.jboss.ws.core.soap.SAAJVisitor;
import org.jboss.ws.core.soap.SOAPContentElement;
import org.jboss.ws.core.soap.SOAPElementImpl;

/**
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Nov 7, 2006
 */
public class CreateAttachmentVisitor implements SAAJVisitor {
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
      // Calling writeElement will enforce marshalling of this object
      // Any attachment will be created while doing this.
      try
      {
         scElement.writeElement( new NoopWriter() );
      }
      catch (IOException e)
      {
         //
      }
   }

   class NoopWriter extends Writer {
      public void write(char cbuf[], int off, int len) throws IOException {

      }

      public void flush() throws IOException {

      }

      public void close() throws IOException {

      }
   }
}
