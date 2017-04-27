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
package org.jboss.ws.tools.helpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Element;


/**
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Sep 27, 2006
 */
public class ReformatXML {

   public static void main(String[] args) throws Exception
   {
      if(args.length == 0)
         throw new IllegalArgumentException("Please specify a filename");

      ReformatXML formatter = new ReformatXML();
      try
      {
         FileInputStream in = new FileInputStream(args[0]);
         System.out.println( formatter.reformat(in));
      }
      catch (FileNotFoundException e)
      {
         System.err.println("Failed to read from file " + args[0] +": "+e.getMessage());
      }
   }

   public String reformat(InputStream in) throws Exception
   {
      Element root = DOMUtils.parse(in);
      return DOMWriter.printNode(root, true);
   }
}
