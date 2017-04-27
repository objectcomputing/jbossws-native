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
package org.jboss.ws.extensions.eventing;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jboss.ws.Constants;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSNamespaceItem;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSStringList;

/**
 * @author Heiko.Braun@jboss.org
 * @since 15.01.2007
 */
public class EventingUtils {

   public static String[] extractNotificationSchema(JBossXSModel schemaModel) {

      List<String> list = ((JBossXSStringList)schemaModel.getNamespaces()).toList();
      List<String> schemas = new LinkedList<String>();

      for (Iterator it = list.iterator(); it.hasNext(); )
      {

         String ns = (String)it.next();

         if (!Constants.URI_SOAP11_ENC.equalsIgnoreCase(ns) &&
            !Constants.NS_SCHEMA_XSI.equalsIgnoreCase(ns) &&
            !Constants.NS_SCHEMA_XSD.equalsIgnoreCase(ns) &&
            !Constants.URI_WS_EVENTING.equalsIgnoreCase(ns) &&
            !Constants.URI_WS_ADDRESSING.equalsIgnoreCase(ns))
         {
            JBossXSNamespaceItem item = schemaModel.getNamespaceItem(ns);
            boolean qElem = item.isQualifiedElements();
            item.setQualifiedElements(true);
            schemas.add(item.toString());
            item.setQualifiedElements(qElem);
         }
      }

      return schemas.toArray(new String[schemas.size()]);
   }
}
