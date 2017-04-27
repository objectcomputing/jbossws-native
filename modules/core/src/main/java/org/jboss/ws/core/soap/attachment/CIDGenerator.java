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
package org.jboss.ws.core.soap.attachment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jboss.logging.Logger;
import org.jboss.wsf.common.utils.UUIDGenerator;

/**
 * A common CID generator
 *
 * @author Thomas.Diesler@jboss.org
 * @since 17-Jan-2006
 */
public class CIDGenerator
{
   // provide logging
   private static Logger log = Logger.getLogger(CIDGenerator.class);
   
   private int count = 0;                                      

   public String generateFromCount()
   {
      StringBuilder cid = new StringBuilder();
      long time = System.currentTimeMillis();

      cid.append(count++).append("-").append(time).append("-")
         .append(cid.hashCode()).append("@").append(MimeConstants.CID_DOMAIN);

      if(log.isDebugEnabled()) log.debug("generateFromCount: " + cid);
      return cid.toString();
   }

   public String generateFromName(String name)
   {

      // See http://www.ietf.org/rfc/rfc2392.txt on rules howto create cid's
      // TODO: URL decode when cid's are received
      try
      {
         name = URLEncoder.encode(name, "UTF-8");
      }
      catch (UnsupportedEncodingException ex)
      {
         log.error("Cannot encode name for cid: " + ex);
      }

      String cid = name + "-" + UUIDGenerator.generateRandomUUIDString() + "@" + MimeConstants.CID_DOMAIN;
      if(log.isDebugEnabled()) log.debug("generateFromName: " + cid);
      return cid;
   }
}
