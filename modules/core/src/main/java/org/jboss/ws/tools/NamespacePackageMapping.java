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

import java.util.HashSet;
import java.util.Set;

/**
 *  Utility class for mapping an XML namespace to a Java package.
 *  
 *  This mapping is performed according to the algorith defined in section D.5.1 
 *  of the JAXB 2.0 specification.
 *  
 * @author darran.lofthouse@jboss.com
 * @since 18-Jul-2006
 */
public class NamespacePackageMapping
{

   private static final NamespacePackageMapping npm = new NamespacePackageMapping();

   /*
    * The JAXB specification says to use the list of country codes as defined in ISO-3166 1981 to identify the top
    * level domains.
    * 
    * The recommendations in the Java Language Specification for selecting unique package names is based around
    * choosing package names using the domain name of your organisation.
    * 
    * Not all countries use the country code listed in ISO-3166 as their top level domain.  As an example 'The United
    * Kingdom of Great Britain and Northern Ireland' uses .uk as the top level domain, however ISO-3166 lists the 
    * country code as gb.
    * 
    * For this reason I have used the list of country codes available from here: -
    * 
    *   http://www.iana.org/cctld/cctld-whois.htm
    */
   private final String[] countryCodesArray = { "ac", "ad", "ae", "af", "ag", "ai", "al", "am", "an", "ao", "aq", "ar", "as", "at", "au", "aw", "az", "ax", "ba", "bb",
         "bd", "be", "bf", "bg", "bh", "bi", "bj", "bm", "bn", "bo", "br", "bs", "bt", "bv", "bw", "by", "bz", "ca", "cc", "cd", "cf", "cg", "ch", "ci", "ck", "cl",
         "cm", "cn", "co", "cr", "cs", "cu", "cv", "cx", "cy", "cz", "de", "dj", "dk", "dm", "do", "dz", "ec", "ee", "eg", "eh", "er", "es", "et", "eu", "fi", "fj",
         "fk", "fm", "fo", "fr", "ga", "gb", "gd", "ge", "gf", "gg", "gh", "gi", "gl", "gm", "gn", "gp", "gq", "gr", "gs", "gt", "gu", "gw", "gy", "hk", "hm", "hn",
         "hr", "ht", "hu", "id", "ie", "il", "im", "in", "io", "iq", "ir", "is", "it", "je", "jm", "jo", "jp", "ke", "kg", "kh", "ki", "km", "kn", "kp", "kr", "kw",
         "ky", "kz", "la", "lb", "lc", "li", "lk", "lr", "ls", "lt", "lu", "lv", "ly", "ma", "mc", "md", "mg", "mh", "mk", "ml", "mm", "mn", "mo", "mp", "mq", "mr",
         "ms", "mt", "mu", "mv", "mw", "mx", "my", "mz", "na", "nc", "ne", "nf", "ng", "ni", "nl", "no", "np", "nr", "nu", "nz", "om", "pa", "pe", "pf", "pg", "ph",
         "pk", "pl", "pm", "pn", "pr", "ps", "pt", "pw", "py", "qa", "re", "ro", "ru", "rw", "sa", "sb", "sc", "sd", "se", "sg", "sh", "si", "sj", "sk", "sl", "sm",
         "sn", "so", "sr", "st", "sv", "sy", "sz", "tc", "td", "tf", "tg", "th", "tj", "tk", "tl", "tm", "tn", "to", "tp", "tr", "tt", "tv", "tw", "tz", "ua", "ug",
         "uk", "um", "us", "uy", "uz", "va", "vc", "ve", "vg", "vi", "vn", "vu", "wf", "ws", "ye", "yt", "yu", "za", "zm", "zw" };

   private final String[] topLevelDomainsArray = { "com", "gov", "net", "org", "edu" };

   private final Set<String> topLevelDomains;

   /**
    * Convert an XML namespace to a Java package using the algorithm defined in section
    * D.5.1 of the JAXB specification.
    * 
    * @param targetNS
    * @return
    */
   public static String getJavaPackageName(final String targetNS)
   {
      return npm.convertNamespaceToPackage(targetNS);
   }

   private NamespacePackageMapping()
   {
      int topLevelSize = countryCodesArray.length + topLevelDomainsArray.length;
      topLevelSize = (int)((double)topLevelSize / 0.75) + 1;

      topLevelDomains = new HashSet<String>(topLevelSize);
      addAll(topLevelDomains, countryCodesArray);
      addAll(topLevelDomains, topLevelDomainsArray);
   }

   private void addAll(final Set<String> set, final String[] data)
   {
      for (String current : data)
      {
         set.add(current);
      }
   }

   private String convertNamespaceToPackage(final String targetNS)
   {
      String workingNS = targetNS;

      boolean schemeIsURN = workingNS.startsWith("urn:");

      if (workingNS.startsWith("http://"))
      {
         workingNS = workingNS.substring(7);
      }
      else if (workingNS.startsWith("https://"))
      {
         workingNS = workingNS.substring(8);
      }
      else if (workingNS.startsWith("urn:"))
      {
         workingNS = workingNS.substring(4);
      }
      else
      {
         throw new IllegalArgumentException("TargetNS should start with http:// / https:// / urn:");
      }

      // Test for an ending .?? .??? or .html
      boolean hasExtension = workingNS.matches("^.*\\/.*\\.((.{2,3})|(html))$");
      if (hasExtension)
      {
         workingNS = workingNS.substring(0, workingNS.lastIndexOf("."));
      }

      String[] tokens = workingNS.split("[\\/\\:]+");

      if (schemeIsURN)
      {
         tokens[0] = tokens[0].replace('-', '.');
      }

      String[] packagePrefix = convertDomainToPackage(tokens[0]);
      String[] temp = tokens;

      tokens = new String[packagePrefix.length + temp.length - 1];
      System.arraycopy(packagePrefix, 0, tokens, 0, packagePrefix.length);
      System.arraycopy(temp, 1, tokens, packagePrefix.length, temp.length - 1);

      StringBuilder packageName = new StringBuilder();

      for (int i = 0; i < tokens.length; i++)
      {
         String current = tokens[i];
         current = current.toLowerCase();
         current = convertReserved(current);
         current = convertStart(current);
         current = ToolsUtils.convertInvalidCharacters(current);

         packageName.append(current);
         if (i < tokens.length - 1)
         {
            packageName.append(".");
         }
      }

      return packageName.toString();
   }

   /**
    * Check if the passed in component is a reserved keyword, 
    * if it is append and underscore and return.  
    */
   private String convertReserved(final String component)
   {
      String result = component;
      if (JavaKeywords.isJavaKeyword(result))
      {
         result = result + "_";
      }

      return result;
   }

   /**
    * Check if the first character of the component is a valid character to 
    * start an identifier, if it is not prefix it with an underscore and return.
    */
   private String convertStart(final String component)
   {
      String result = component;

      if (Character.isJavaIdentifierStart(result.charAt(0)) == false)
      {
         result = "_" + result;
      }

      return result;
   }

   /**
    * Given a domain name split it into the corresponding parts, and convert
    * into the order suitable for creating a package name.
    * 
    * The transposition is only performed if the last element is a top level domain 
    * ("com", "gov", "net", "org", "edu" or a country code used as a top level domain).
    */
   private String[] convertDomainToPackage(final String domain)
   {
      String[] parts = domain.split("\\.");

      String[] packge;

      if (topLevelDomains.contains(parts[parts.length - 1]))
      {
         if (parts[0].equals("www"))
         {
            packge = new String[parts.length - 1];
         }
         else
         {
            packge = new String[parts.length];
         }

         for (int i = 0; i < packge.length; i++)
         {
            packge[i] = parts[parts.length - i - 1];
         }
      }
      else
      {
         packge = parts;
      }

      return packge;
   }

}
