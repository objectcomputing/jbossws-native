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

public class KeywordDetailType extends KeywordType
{

   protected java.lang.Long searches;

   protected java.lang.Integer tier;

   protected java.lang.Long advancedImpressions;

   protected java.math.BigDecimal topBid;

   protected java.math.BigDecimal minimumBid;

   protected java.lang.Boolean duplicate;

   protected java.lang.Long contentMatchImpressions;

   protected java.lang.Integer contentMatchPos1Clicks;

   protected java.lang.Integer contentMatchPos2Clicks;

   protected java.lang.Integer contentMatchPos3Clicks;

   public KeywordDetailType()
   {
   }

   public KeywordDetailType(java.lang.Long searches, java.lang.Integer tier, java.lang.Long advancedImpressions,
         java.math.BigDecimal topBid, java.math.BigDecimal minimumBid, java.lang.Boolean duplicate,
         java.lang.Long contentMatchImpressions, java.lang.Integer contentMatchPos1Clicks,
         java.lang.Integer contentMatchPos2Clicks, java.lang.Integer contentMatchPos3Clicks)
   {
      this.searches = searches;
      this.tier = tier;
      this.advancedImpressions = advancedImpressions;
      this.topBid = topBid;
      this.minimumBid = minimumBid;
      this.duplicate = duplicate;
      this.contentMatchImpressions = contentMatchImpressions;
      this.contentMatchPos1Clicks = contentMatchPos1Clicks;
      this.contentMatchPos2Clicks = contentMatchPos2Clicks;
      this.contentMatchPos3Clicks = contentMatchPos3Clicks;
   }

   public java.lang.Long getSearches()
   {
      return searches;
   }

   public void setSearches(java.lang.Long searches)
   {
      this.searches = searches;
   }

   public java.lang.Integer getTier()
   {
      return tier;
   }

   public void setTier(java.lang.Integer tier)
   {
      this.tier = tier;
   }

   public java.lang.Long getAdvancedImpressions()
   {
      return advancedImpressions;
   }

   public void setAdvancedImpressions(java.lang.Long advancedImpressions)
   {
      this.advancedImpressions = advancedImpressions;
   }

   public java.math.BigDecimal getTopBid()
   {
      return topBid;
   }

   public void setTopBid(java.math.BigDecimal topBid)
   {
      this.topBid = topBid;
   }

   public java.math.BigDecimal getMinimumBid()
   {
      return minimumBid;
   }

   public void setMinimumBid(java.math.BigDecimal minimumBid)
   {
      this.minimumBid = minimumBid;
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

   public java.lang.Integer getContentMatchPos1Clicks()
   {
      return contentMatchPos1Clicks;
   }

   public void setContentMatchPos1Clicks(java.lang.Integer contentMatchPos1Clicks)
   {
      this.contentMatchPos1Clicks = contentMatchPos1Clicks;
   }

   public java.lang.Integer getContentMatchPos2Clicks()
   {
      return contentMatchPos2Clicks;
   }

   public void setContentMatchPos2Clicks(java.lang.Integer contentMatchPos2Clicks)
   {
      this.contentMatchPos2Clicks = contentMatchPos2Clicks;
   }

   public java.lang.Integer getContentMatchPos3Clicks()
   {
      return contentMatchPos3Clicks;
   }

   public void setContentMatchPos3Clicks(java.lang.Integer contentMatchPos3Clicks)
   {
      this.contentMatchPos3Clicks = contentMatchPos3Clicks;
   }

}
