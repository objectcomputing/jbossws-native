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

public class KeywordMonthlyReportType extends KeywordType
{

   protected java.lang.Long searches;

   protected java.lang.Long advancedImpressions;

   protected java.lang.Boolean duplicate;

   protected java.lang.Long contentMatchImpressions;

   protected java.math.BigDecimal maxBid;

   protected java.math.BigDecimal maxCost;

   public KeywordMonthlyReportType()
   {
   }

   public KeywordMonthlyReportType(java.lang.Long searches, java.lang.Long advancedImpressions, java.lang.Boolean duplicate,
         java.lang.Long contentMatchImpressions, java.math.BigDecimal maxBid, java.math.BigDecimal maxCost)
   {
      this.searches = searches;
      this.advancedImpressions = advancedImpressions;
      this.duplicate = duplicate;
      this.contentMatchImpressions = contentMatchImpressions;
      this.maxBid = maxBid;
      this.maxCost = maxCost;
   }

   public java.lang.Long getSearches()
   {
      return searches;
   }

   public void setSearches(java.lang.Long searches)
   {
      this.searches = searches;
   }

   public java.lang.Long getAdvancedImpressions()
   {
      return advancedImpressions;
   }

   public void setAdvancedImpressions(java.lang.Long advancedImpressions)
   {
      this.advancedImpressions = advancedImpressions;
   }

   public java.lang.Boolean getDuplicate()
   {
      return duplicate;
   }

   public void setDuplicate(java.lang.Boolean duplicate)
   {
      this.duplicate = duplicate;
   }

   public java.lang.Long getContentMatchImpressions()
   {
      return contentMatchImpressions;
   }

   public void setContentMatchImpressions(java.lang.Long contentMatchImpressions)
   {
      this.contentMatchImpressions = contentMatchImpressions;
   }

   public java.math.BigDecimal getMaxBid()
   {
      return maxBid;
   }

   public void setMaxBid(java.math.BigDecimal maxBid)
   {
      this.maxBid = maxBid;
   }

   public java.math.BigDecimal getMaxCost()
   {
      return maxCost;
   }

   public void setMaxCost(java.math.BigDecimal maxCost)
   {
      this.maxCost = maxCost;
   }

}
