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
package org.jboss.ws.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.rpc.holders.Holder;

import org.jboss.ws.WSException;
import org.jboss.ws.metadata.wsdl.WSDLUtils;

/**
 * Utility class to write JAX-RPC holders.
 * 
 * @author darran.lofthouse@jboss.com
 * @since 11 Apr 2007
 */
public class HolderWriter
{

   private static final WSDLUtils utils = WSDLUtils.getInstance();

   private static final String newline = "\n";

   /** 
    * HashMap of the holders already generated so they do not need to be 
    * generated again.
    */
   private HashMap<String, String> createdHolders = new HashMap<String, String>();

   String getOrCreateHolder(final String type, final File pkgLoc)
   {

      String fullHolderName = createdHolders.get(type);
      if (fullHolderName == null)
      {
         StringBuilder buf = new StringBuilder();
         fullHolderName = writeHolder(type, buf);

         createdHolders.put(type, fullHolderName);

         String holderName = fullHolderName.substring(fullHolderName.lastIndexOf('.') + 1);

         try
         {
            File sei = utils.createPhysicalFile(pkgLoc, holderName);
            FileWriter writer = new FileWriter(sei);
            writer.write(buf.toString());
            writer.flush();
            writer.close();
         }
         catch (IOException e)
         {
            throw new WSException("Unable to create JAX-RPC holder.", e);
         }
      }

      return fullHolderName;
   }

   private String writeHolder(final String type, final StringBuilder buf)
   {
      String pkg = type.substring(0, type.lastIndexOf('.'));
      String typeName = type.substring(type.lastIndexOf('.') + 1);
      String holderName = typeName + "Holder";

      utils.writeJbossHeader(buf);
      buf.append(newline);
      buf.append("package ").append(pkg).append(";").append(newline);

      buf.append(newline);
      buf.append("public class ").append(holderName).append(" implements ").append(Holder.class.getName());
      buf.append(newline).append("{").append(newline);

      // Add the public member variable.
      buf.append(newline);
      buf.append("  public ").append(type).append(" value;");
      buf.append(newline);

      // Add the default constructor.
      buf.append(newline);
      buf.append("  ").append("public ").append(holderName).append("()").append(newline);
      buf.append("  {").append(newline);
      buf.append("    this.value = new ").append(type).append("();").append(newline);
      buf.append("  }").append(newline);

      // Add the second constructor.                  
      buf.append(newline);
      buf.append("  ").append("public ").append(holderName).append("(final ").append(type).append(" value)").append(newline);
      buf.append("  {").append(newline);
      buf.append("    this.value = value;").append(newline);
      buf.append("  }").append(newline);
      buf.append(newline);

      buf.append("}").append(newline);
      return pkg + "." + holderName;
   }

}
