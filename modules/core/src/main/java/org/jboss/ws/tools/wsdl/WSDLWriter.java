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
package org.jboss.ws.tools.wsdl;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.wsdl.DOMTypes;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.metadata.wsdl.WSDLTypes;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.XSModelTypes;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Element;

/**
 * A helper that writes out a WSDL definition
 *
 * @author Thomas.Diesler@jboss.org
 * @author Anil.Saldhana@jboss.org
 * @since 10-Oct-2004
 */
public class WSDLWriter
{
   // provide logging
   protected static final Logger log = Logger.getLogger(WSDLWriter.class);

   protected WSDLDefinitions wsdl;
   protected WSDLUtils utils = WSDLUtils.getInstance();

   // The soap prefix
   protected String soapPrefix = "soap";

   /**
    * Include or import WSDL Types
    */
   protected boolean includeSchemaInWSDL = true;

   public WSDLWriter(WSDLDefinitions wsdl)
   {
      if (wsdl == null)
         throw new IllegalArgumentException("WSDL definitions is NULL");

      this.wsdl = wsdl;
   }

   /** Write the wsdl definition to the given writer, clients should not care about the wsdl version. */
   public void write(Writer writer, String charset) throws IOException
   {
      write(writer, charset, null);
   }

   public void write(Writer writer, String charset, WSDLWriterResolver resolver) throws IOException
   {
      String wsdlNamespace = wsdl.getWsdlNamespace();
      if (Constants.NS_WSDL11.equals(wsdlNamespace))
      {
         WSDL11Writer wsdl11Writer = new WSDL11Writer(wsdl);
         wsdl11Writer.write(writer, charset, resolver);
      }
      else
      {
         throw new WSException("Unsupported wsdl version: " + wsdlNamespace);
      }
   }

   public void write(Writer writer) throws IOException
   {
      StringBuilder buffer = new StringBuilder();
      buffer.append(Constants.XML_HEADER);

      appendDefinitions(buffer, wsdl.getTargetNamespace());
      appendTypes(buffer, wsdl.getTargetNamespace());
      appendInterfaces(buffer, wsdl.getTargetNamespace());
      appendBindings(buffer, wsdl.getTargetNamespace());
      appendServices(buffer, wsdl.getTargetNamespace());

      buffer.append("</definitions>");

      Element element = DOMUtils.parse(buffer.toString());
      new DOMWriter(writer).setPrettyprint(true).print(element);
   }

   protected void appendDefinitions(StringBuilder buffer, String namespace)
   {
      buffer.append("<definitions");
      //Append service name as done by wscompile, if there is just one
      WSDLService[] services = wsdl.getServices();
      if (services != null && services.length == 1)
      {
         WSDLService ser = services[0];
         buffer.append(" name='" + ser.getName().getLocalPart() + "'");
      }
      buffer.append(" targetNamespace='" + namespace + "'");
      buffer.append(" xmlns='" + wsdl.getWsdlNamespace() + "'");

      Iterator it = wsdl.getRegisteredNamespaceURIs();
      while (it.hasNext())
      {
         String namespaceURI = (String)it.next();
         String prefix = wsdl.getPrefix(namespaceURI);
         if (prefix.length() > 0)
         {
            buffer.append(" xmlns:" + prefix + "='" + namespaceURI + "'");
            if (prefix.startsWith("soap"))
               soapPrefix = prefix;
         }
      }
      buffer.append(">");
   }

   protected void appendTypes(StringBuilder buffer, String namespace)
   {
      WSDLTypes wsdlTypes = wsdl.getWsdlTypes();
      // If the type section is bound to a particular namespace, verify it mataches, otherwise skip
      if (wsdlTypes.getNamespace() != null && !wsdlTypes.getNamespace().equals(namespace))
         return;

      if (wsdlTypes instanceof XSModelTypes)
      {
         buffer.append("<types>");
         JBossXSModel xsM = WSDLUtils.getSchemaModel(wsdlTypes);
         String schema = xsM.serialize();
         buffer.append(schema);
         buffer.append("</types>");
      }
      else if (wsdlTypes instanceof DOMTypes)
      {
         synchronized (wsdlTypes)
         {
            buffer.append(DOMWriter.printNode(((DOMTypes)wsdlTypes).getElement(), true));
         }
      }
   }

   protected void appendInterfaces(StringBuilder buffer, String namespace)
   {
   }

   protected void appendBindings(StringBuilder buffer, String namespace)
   {
   }

   protected void appendServices(StringBuilder buffer, String namespace)
   {
   }

   /** Get a prefixed name of form prefix:localPart */
   protected String getQNameRef(QName qname)
   {
      String retStr = qname.getLocalPart();

      String prefix = qname.getPrefix();
      String nsURI = qname.getNamespaceURI();
      if (prefix.length() == 0 && nsURI.length() > 0)
      {
         qname = wsdl.registerQName(qname);
         prefix = qname.getPrefix();
      }

      if (prefix.length() > 0)
         retStr = prefix + ":" + retStr;

      return retStr;
   }

   public WSDLDefinitions getWsdl()
   {
      return wsdl;
   }

   public void setWsdl(WSDLDefinitions wsdl)
   {
      this.wsdl = wsdl;
   }

   public boolean isIncludeTypesInWSDL()
   {
      return includeSchemaInWSDL;
   }

   public void logException(Exception e)
   {
      if (log.isTraceEnabled())
      {
         log.trace(e);
      }
   }

   public void logMessage(String msg)
   {
      if (log.isTraceEnabled())
      {
         log.trace(msg);
      }
   }
}
