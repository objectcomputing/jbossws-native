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
package org.jboss.test.ws.jaxrpc.jbws643;

public class GetKeywordStatisticsRequest
{

   protected java.lang.String[] ns1Keyword;

   protected java.lang.String ns1Market;

   protected boolean ns1GetTopBid;

   public GetKeywordStatisticsRequest()
   {
   }

   public GetKeywordStatisticsRequest(java.lang.String[] ns1Keyword, java.lang.String ns1Market, boolean ns1GetTopBid)
   {
      this.ns1Keyword = ns1Keyword;
      this.ns1Market = ns1Market;
      this.ns1GetTopBid = ns1GetTopBid;
   }

   public java.lang.String[] getNs1Keyword()
   {
      return ns1Keyword;
   }

   public void setNs1Keyword(java.lang.String[] ns1Keyword)
   {
      this.ns1Keyword = ns1Keyword;
   }

   public java.lang.String getNs1Market()
   {
      return ns1Market;
   }

   public void setNs1Market(java.lang.String ns1Market)
   {
      this.ns1Market = ns1Market;
   }

   public boolean isNs1GetTopBid()
   {
      return ns1GetTopBid;
   }

   public void setNs1GetTopBid(boolean ns1GetTopBid)
   {
      this.ns1GetTopBid = ns1GetTopBid;
   }

}
