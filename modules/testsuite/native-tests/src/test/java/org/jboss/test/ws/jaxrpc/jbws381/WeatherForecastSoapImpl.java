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
package org.jboss.test.ws.jaxrpc.jbws381;

import org.jboss.logging.Logger;


public class WeatherForecastSoapImpl implements WeatherForecastSoap
{
   private static Logger log = Logger.getLogger(WeatherForecastSoapImpl.class);

   public GetWeatherByZipCodeResponse getWeatherByZipCode(GetWeatherByZipCode request)
   {
      String zipCode = request.getZipCode();
      log.info("getWeatherByZipCode: " + zipCode);

      WeatherForecasts wf = new WeatherForecasts();
      wf.setLatitude(1);
      wf.setLongitude(2);
      wf.setAllocationFactor(3);
      wf.setFipsCode("fipsCode");
      wf.setPlaceName(zipCode);
      wf.setStateCode("statusCode");

      WeatherData wd1 = new WeatherData("day1", null, null, null, null, null);
      WeatherData wd2 = new WeatherData("day2", null, null, null, null, null);
      WeatherData wd3 = new WeatherData("day3", null, null, null, null, null);
      WeatherData[] wdArr = new WeatherData[]{wd1, wd2, wd3};

      wf.setDetails(wdArr);
      return new GetWeatherByZipCodeResponse(wf);
   }

   public GetWeatherByPlaceNameResponse getWeatherByPlaceName(GetWeatherByPlaceName request)
   {
      String placeName = request.getPlaceName();
      log.info("getWeatherByPlaceName: " + placeName);

      WeatherForecasts wf = new WeatherForecasts();
      wf.setLatitude(1);
      wf.setLongitude(2);
      wf.setAllocationFactor(3);
      wf.setFipsCode("fipsCode");
      wf.setPlaceName(placeName);
      wf.setStateCode("statusCode");

      WeatherData wd1 = new WeatherData("day1", null, null, null, null, null);
      WeatherData wd2 = new WeatherData("day2", null, null, null, null, null);
      WeatherData wd3 = new WeatherData("day3", null, null, null, null, null);
      WeatherData[] wdArr = new WeatherData[]{wd1, wd2, wd3};
      wf.setDetails(wdArr);
      return new GetWeatherByPlaceNameResponse(wf);
   }
}
