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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.jboss.util.NotImplementedException;
import org.jboss.ws.Constants;
import org.jboss.ws.core.CommonSOAPFaultException;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.xml.fastinfoset.dom.DOMDocumentParser;

/**
 * A SOAPEnvelope builder for FastInfoset 
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 12-Mar-2008
 */
public class FastInfosetEnvelopeBuilder extends EnvelopeBuilderDOM
{
   @Override
   public SOAPEnvelope build(SOAPMessage soapMessage, InputStream ins, boolean ignoreParseError) throws IOException, SOAPException
   {
      // Parse the XML input stream
      Element domEnv = null;
      try
      {
         DOMDocumentParser parser = new DOMDocumentParser();
         Document resDoc = DOMUtils.getDocumentBuilder().newDocument();
         parser.parse(resDoc, ins);
         domEnv = resDoc.getDocumentElement();
      }
      catch (Exception ex)
      {
         if (ignoreParseError)
         {
            return null;
         }
         QName faultCode = Constants.SOAP11_FAULT_CODE_CLIENT;
         throw new CommonSOAPFaultException(faultCode, ex.getMessage());
      }

      return build(soapMessage, domEnv);
   }

   @Override
   public SOAPEnvelope build(SOAPMessage soapMessage, Reader reader, boolean ignoreParseError) throws IOException, SOAPException
   {
      throw new NotImplementedException();
   }
}
