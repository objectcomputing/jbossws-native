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
package org.jboss.test.ws.tools.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * A JAX-RPC 1.1 Enum Type using Doubles.
 *
 * NOTE: This is just a test, in general, it's a really bad idea to use doubles
 * for an enum index
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class PlanetRadius
{
   /*
    MERCURY (3.303e+23, 2.4397e6),
    VENUS   (4.869e+24, 6.0518e6),
    EARTH   (5.976e+24, 6.37814e6),
    MARS    (6.421e+23, 3.3972e6),
    JUPITER (1.9e+27,   7.1492e7),
    SATURN  (5.688e+26, 6.0268e7),
    URANUS  (8.686e+25, 2.5559e7),
    NEPTUNE (1.024e+26, 2.4746e7),
    PLUTO   (1.27e+22,  1.137e6);
    */

   private java.math.BigDecimal value;

   protected PlanetRadius(java.math.BigDecimal value)
   {
      this.value = value;
   }

   private static Map map = new HashMap();

   public static final PlanetRadius MERCURY = new PlanetRadius(new java.math.BigDecimal("2.4397e6"));
   public static final PlanetRadius VENUS = new PlanetRadius(new java.math.BigDecimal("6.0518e6"));
   public static final PlanetRadius EARTH = new PlanetRadius(new java.math.BigDecimal("6.37814e6"));
   public static final PlanetRadius MARS = new PlanetRadius(new java.math.BigDecimal("3.3972e6"));
   public static final PlanetRadius JUPITER = new PlanetRadius(new java.math.BigDecimal("7.1492e7"));
   public static final PlanetRadius SATURN = new PlanetRadius(new java.math.BigDecimal("6.0268e7"));
   public static final PlanetRadius URANUS = new PlanetRadius(new java.math.BigDecimal("2.5559e7"));
   public static final PlanetRadius NEPTUNE = new PlanetRadius(new java.math.BigDecimal("2.4746e7"));
   public static final PlanetRadius PLUTO = new PlanetRadius(new java.math.BigDecimal("1.137e6"));

   static
   {
      map.put(MERCURY.getValue(), MERCURY);
      map.put(VENUS.getValue(), VENUS);
      map.put(EARTH.getValue(), EARTH);
      map.put(MARS.getValue(), MARS);
      map.put(JUPITER.getValue(), JUPITER);
      map.put(SATURN.getValue(), SATURN);
      map.put(URANUS.getValue(), URANUS);
      map.put(NEPTUNE.getValue(), NEPTUNE);
      map.put(PLUTO.getValue(), PLUTO);
   }

   public java.math.BigDecimal getValue()
   {
      return value;
   }

   public static PlanetRadius fromValue(java.math.BigDecimal value)
   {
      PlanetRadius ret = (PlanetRadius) map.get(value);
      if (ret == null)
         throw new IllegalArgumentException("Unknown planet");

      return ret;
   }

   public boolean equals(Object obj)
   {
      if (!(obj instanceof PlanetRadius))
         return false;

      return ((PlanetRadius) obj).value.equals(value);
   }

   public int hashCode()
   {
      return value.hashCode();
   }
}
