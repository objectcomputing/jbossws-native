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

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.extensions.security.Util;
import org.jboss.ws.metadata.wsdl.DOMTypes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.xml.bind.api.JAXBRIContext;

/**
 * JAXBWSDLGenerator provides a JAXB based WSDLGenerator.
 * 
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class JAXBWSDLGenerator extends WSDLGenerator
{
   private JAXBRIContext ctx;

   public JAXBWSDLGenerator(JAXBRIContext ctx)
   {
      super();
      this.ctx = ctx;
   }

   /**
    * Delegate schema generation to JAXB RI
    */
   protected void processTypes()
   {
      // Register namespaces
      for (String ns : ctx.getKnownNamespaceURIs())
         if (ns.length() > 0)
            wsdl.registerNamespaceURI(ns, null);

      try
      {
         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         Document doc = builder.newDocument();
         DOMTypes types = new DOMTypes(doc);
         final Element element = types.getElement();
         final Element throwAway = doc.createElement("throw-away");

         ctx.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespace, String file) throws IOException
            {
               // JBWS-1295, getKnownNamespaceURIs is not accurate
               if (namespace != null && namespace.length() > 0 && wsdl.getPrefix(namespace) == null)
                  wsdl.registerNamespaceURI(namespace, null);

               // JAXB creates an empty namespace due to type references, ignore it
               DOMResult result = null;
               if (namespace == null || namespace.length() == 0)
               {
                  result = new DOMResult(throwAway);
                  result.setSystemId("remove-me");
               }
               else
               {
                  result = new DOMResult(element);
                  result.setSystemId("replace-me");
               }

               return result;
            }
         });

         // Until we stop inlining schema, we will need to filter schemaLocations since JAXB requires them
         removeSchemaLocations(element);

         wsdl.setWsdlTypes(types);
      }
      catch (Exception exception)
      {
         throw new WSException("Could not generate schema: " + exception.getMessage(), exception);
      }
   }

   private void removeSchemaLocations(Element element)
   {
      for (Element child = Util.getFirstChildElement(element); child != null; child = Util.getNextSiblingElement(child))
      {
         if ("import".equals(child.getLocalName()) && Constants.NS_SCHEMA_XSD.equals(child.getNamespaceURI())
               && "replace-me".equals(child.getAttribute("schemaLocation")))
         {
            child.removeAttribute("schemaLocation");
         }
         else if ("import".equals(child.getLocalName()) && Constants.NS_SCHEMA_XSD.equals(child.getNamespaceURI())
               && "remove-me".equals(child.getAttribute("schemaLocation")))
         {
            element.removeChild(child);
         }
         else
         {
            removeSchemaLocations(child);
         }
      }
   }
}
