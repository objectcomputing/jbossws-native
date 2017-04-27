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
package org.jboss.ws.metadata.wsdl.xmlschema;

import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLParseException;
import org.jboss.logging.Logger;

/**
 *  Error Handler for the Xerces schema parser default implementation
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Aug 11, 2005
 */
public class JBossXSErrorHandler implements XMLErrorHandler
{
   private static final Logger log = Logger.getLogger(JBossXSErrorHandler.class);

   /**
    * Reports a warning. Warnings are non-fatal and can be safely ignored by most applications.
    * @param domain - The domain of the warning. The domain can be any string but is suggested to be a valid URI. The domain can be used to conveniently specify a web site location of the relevent specification or document pertaining to this warning.
    * @param key - The warning key. This key can be any string and is implementation dependent.
    * @param exception - Exception.
    * @throws XNIException Thrown to signal that the parser should stop parsing the document.
    */
   public void warning(String domain, String key, XMLParseException xexp) throws XNIException
   {
      log.trace(getFormattedString(domain, key, xexp));
   }

   /**
    * Reports an error. Errors are non-fatal and usually signify that the document is invalid with respect to its grammar(s).
    * @param domain - The domain of the warning. The domain can be any string but is suggested to be a valid URI. The domain can be used to conveniently specify a web site location of the relevent specification or document pertaining to this warning.
    * @param key - The warning key. This key can be any string and is implementation dependent.
    * @param exception - Exception.
    * @throws XNIException Thrown to signal that the parser should stop parsing the document.
    */
   public void error(String domain, String key, XMLParseException xexp) throws XNIException
   {
      if ("src-include.2.1".equals(key))
         throw new XNIException("Parser should stop:", xexp);

      String errorMsg = getFormattedString(domain, key, xexp);
      log.error(errorMsg);
   }

   /**
    * Report a fatal error. Fatal errors usually occur when the document is not well-formed
    * and signifies that the parser cannot continue normal operation.
    * @param domain - The domain of the warning. The domain can be any string but is suggested to be a valid URI. The domain can be used to conveniently specify a web site location of the relevent specification or document pertaining to this warning.
    * @param key - The warning key. This key can be any string and is implementation dependent.
    * @param exception - Exception.
    * @throws XNIException Thrown to signal that the parser should stop parsing the document.
    */
   public void fatalError(String domain, String key, XMLParseException xexp) throws XNIException
   {
      String errorMsg = getFormattedString(domain, key, xexp);
      log.error(errorMsg);
      throw new XNIException("Parser should stop: " + errorMsg, xexp);
   }

   /**
    * Get the name of the schema file in question
    */
   private String getFileName(XMLParseException xexp)
   {
      String fname = xexp.getExpandedSystemId();
      if (fname != null)
      {
         int index = fname.lastIndexOf('/');
         if (index != -1)
            fname = fname.substring(index + 1);
      }
      else
      {
         fname = "";
      }

      return fname;
   }

   /**
    * Return a formatted string that gives as much information
    * as possible to the user
    */
   private String getFormattedString(String domain, String key, XMLParseException xexp)
   {
      StringBuilder buf = new StringBuilder(getFileName(xexp));
      buf.append("[domain:");
      buf.append(domain).append("]::[key=").append(key).append("]::");
      buf.append("Message=");
      buf.append(xexp.getLocalizedMessage());
      return buf.toString();
   }
}
