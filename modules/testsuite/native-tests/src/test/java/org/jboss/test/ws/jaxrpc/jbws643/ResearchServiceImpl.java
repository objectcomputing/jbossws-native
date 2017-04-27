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

import org.jboss.logging.Logger;

public class ResearchServiceImpl implements ResearchService
{
   private Logger log = Logger.getLogger(ResearchServiceImpl.class);

   public GetKeywordsBySubphraseResponse getKeywordsBySubphrase(GetKeywordsBySubphraseRequest getKeywordsBySubphraseRequest)
   {
      GetKeywordsBySubphraseResponse retObj = null;
      return retObj;
   }

   public GetKeywordStatisticsResponse getKeywordStatistics(GetKeywordStatisticsRequest getKeywordStatisticsRequest)
   {
      log.debug("getKeywordStatistics: " + getKeywordStatisticsRequest);
      
      KeywordDetailType kdt1 = new KeywordDetailType();
      kdt1.setSearches(new Long(1));
      KeywordDetailType kdt2 = new KeywordDetailType();
      kdt2.setSearches(new Long(2));
      GetKeywordStatisticsResponse retObj = new GetKeywordStatisticsResponse(new KeywordDetailType[] { kdt1, kdt2 });
      return retObj;
   }

   public GetKeywordMonthlyReportResponse getKeywordMonthlyReport(GetKeywordMonthlyReportRequest getKeywordMonthlyReportRequest)
   {
      GetKeywordMonthlyReportResponse retObj = null;
      return retObj;
   }

   public GetCanonicalizationResponse getCanonicalization(GetCanonicalizationRequest getCanonicalizationRequest)
   {
      GetCanonicalizationResponse retObj = null;
      return retObj;
   }
}
