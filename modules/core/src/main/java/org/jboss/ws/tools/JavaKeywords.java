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
 * Singleton to check if a specified work is a restricted keyword.
 * 
 * This list also includes the boolean literals and the null literal.
 * 
 * @author darran.lofthouse@jboss.com
 * @since 5 Mar 2007
 */
public class JavaKeywords
{

   private static final JavaKeywords keywords = new JavaKeywords();

   private final String[] keywordsArray = { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do",
         "double", "else", "enum", "extends", "final", "finally", "float", "for", "if", "goto", "implements", "import", "instanceof", "int", "interface", "long",
         "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw",
         "throws", "transient", "try", "void", "volatile", "while" };

   private final String[] restrictedLiteralsArray = { "true", "false", "null" };

   private final Set<String> restrictedKeywords;

   private JavaKeywords()
   {
      int keywordsSize = keywordsArray.length + restrictedLiteralsArray.length;
      keywordsSize = (int)((double)keywordsSize / 0.75) + 1;

      restrictedKeywords = new HashSet<String>(keywordsSize);
      addAll(restrictedKeywords, keywordsArray);
      addAll(restrictedKeywords, restrictedLiteralsArray);
   }

   private void addAll(final Set<String> set, final String[] data)
   {
      for (String current : data)
      {
         set.add(current);
      }
   }

   /**
    * The internal method to check if the keyword is a restricted
    * keyword.
    * 
    * @param name
    * @return
    */
   private boolean internalIsRestrictedKeyword(final String name)
   {
      return restrictedKeywords.contains(name);
   }

   /**
    * Check is the passed in name is a reserved Java keyword.
    * 
    * @param name
    * @return
    */
   public static boolean isJavaKeyword(final String name)
   {
      return keywords.internalIsRestrictedKeyword(name);
   }

}
