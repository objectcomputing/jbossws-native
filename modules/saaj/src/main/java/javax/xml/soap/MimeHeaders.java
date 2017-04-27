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
package javax.xml.soap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/** A container for MimeHeader objects, which represent the MIME headers present
 * in a MIME part of a message. 
 * 
 * This class is used primarily when an application wants to retrieve specific
 * attachments based on certain MIME headers and values. This class will most
 * likely be used by implementations of AttachmentPart and other MIME dependent
 * parts of the SAAJ API.
 *  
 * @author Scott.Stark@jboss.org
 * @author Thomas.Diesler@jboss.org
 */
public class MimeHeaders
{
   private LinkedList headers = new LinkedList();

   /**
    * Adds a MimeHeader object with the specified name and value to this MimeHeaders object's list of headers.
    * 
    * Note that RFC822 headers can contain only US-ASCII characters.
    *  
    * @param name a String with the name of the header to be added
    * @param value a String with the value of the header to be added
    * @throws IllegalArgumentException - if there was a problem in the mime header name or value being added
    */
   public void addHeader(String name, String value) throws IllegalArgumentException
   {
      if (name == null || name.length() == 0)
         throw new IllegalArgumentException("Invalid null or empty header name");
      
      MimeHeader header = new MimeHeader(name, value);
      headers.add(header);
   }

   /**
    * Returns all the MimeHeaders in this MimeHeaders object.
    * @return an Iterator object over this MimeHeaders  object's list of MimeHeader objects
    */
   public Iterator getAllHeaders()
   {
      return headers.iterator();
   }

   /**
    * Returns all of the values for the specified header as an array of String objects.
    * 
    * @param name the name of the header for which values will be returned
    * @return a String array with all of the values for the specified header
    */
   public String[] getHeader(String name)
   {
      ArrayList tmp = new ArrayList();
      for (int n = 0; n < headers.size(); n++)
      {
         MimeHeader mh = (MimeHeader)headers.get(n);
         if (mh.getName().equalsIgnoreCase(name))
            tmp.add(mh.getValue());
      }
      String[] values = null;
      if (tmp.size() > 0)
      {
         values = new String[tmp.size()];
         tmp.toArray(values);
      }
      return values;
   }

   /**
    * Returns all the MimeHeader objects whose name matches a name in the given array of names.
    * @param names an array of String objects with the names for which to search
    * @return an Iterator object over the MimeHeader  objects whose name matches one of the names in the given list
    */
   public Iterator getMatchingHeaders(String[] names)
   {
      MatchingIterator iter = new MatchingIterator(headers, names, true);
      return iter;
   }

   /**
    * Returns all of the MimeHeader objects whose name does not match a name in the given array of names.
    * @param names an array of String objects with the names for which to search
    * @return an Iterator object over the MimeHeader  objects whose name does not match one of the names in the given list
    */
   public Iterator getNonMatchingHeaders(String[] names)
   {
      MatchingIterator iter = new MatchingIterator(headers, names, false);
      return iter;
   }

   /**
    * Removes all the header entries from this MimeHeaders object.
    */
   public void removeAllHeaders()
   {
      headers.clear();
   }

   /**
    * Remove all MimeHeader objects whose name matches the given name.
    * @param name a String with the name of the header for which to search
    */
   public void removeHeader(String name)
   {
      Iterator iter = headers.iterator();
      while (iter.hasNext())
      {
         MimeHeader mh = (MimeHeader)iter.next();
         if (mh.getName().equalsIgnoreCase(name))
            iter.remove();
      }
   }

   /** 
    * Replaces the current value of the first header entry whose name matches
    * the given name with the given value, adding a new header if no existing
    * header name matches. This method also removes all matching headers after
    * the first one.
    * 
    * Note that RFC822 headers can contain only US-ASCII characters.
    * 
    * @param name a String with the name of the header for which to search
    * @param value a String with the value that will replace the current value of the specified header
    * @throws IllegalArgumentException if there was a problem in the mime header name or the value being set
    */
   public void setHeader(String name, String value)
   {
      boolean didSet = false;
      for (int n = 0; n < headers.size(); n++)
      {
         MimeHeader mh = (MimeHeader)headers.get(n);
         if (mh.getName().equalsIgnoreCase(name))
         {
            if (didSet == true)
            {
               headers.remove(n);
               n--;
            }
            else
            {
               mh = new MimeHeader(name, value);
               headers.set(n, mh);
               didSet = true;
            }
         }
      }

      if (didSet == false)
      {
         this.addHeader(name, value);
      }
   }

   public String toString()
   {
      return "[MimeHeaders=" + headers + "]";
   }
   
   private static class MatchingIterator implements Iterator
   {
      private LinkedList headers;
      private HashSet names;
      private boolean match;
      private int index;
      private MimeHeader mh;

      MatchingIterator(LinkedList headers, String[] names, boolean match)
      {
         this.headers = headers;
         this.index = 0;
         this.names = new HashSet();
         for (int n = 0; n < names.length; n++)
            this.names.add(names[n].toLowerCase());
         this.match = match;
      }

      public boolean hasNext()
      {
         boolean hasNext = index < headers.size();
         while (hasNext == true)
         {
            mh = (MimeHeader)headers.get(index);
            index++;
            String name = mh.getName().toLowerCase();
            if (names.contains(name) == match)
               break;
            hasNext = index < headers.size();
         }
         return hasNext;
      }

      public Object next()
      {
         return mh;
      }

      public void remove()
      {
         headers.remove(index - 1);
      }
   }
}
