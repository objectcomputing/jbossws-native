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

/** SOAPElementFactory is a factory for XML fragments that will eventually end
 * up in the SOAP part. These fragments can be inserted as children of the
 * SOAPHeader or SOAPBody or SOAPEnvelope.
 * 
 * Elements created using this factory do not have the properties of an element
 * that lives inside a SOAP header document. These elements are copied into the
 * XML document tree when they are inserted. 

 * @author Scott.Stark@jboss.org
 */
public class SOAPElementFactory
{
   private SOAPFactory soapFactory;

   /**
    * 
    * @return
    * @throws SOAPException
    */
   public static SOAPElementFactory newInstance() throws SOAPException
   {
      SOAPFactory factory = SOAPFactory.newInstance();
      return new SOAPElementFactory(factory);
   }

   /**
    * @deprecated Use javax.xml.soap.SOAPFactory.createElement(javax.xml.soap.Name) 
    * @return
    * @throws SOAPException
    */
   public SOAPElement create(String localName) throws SOAPException
   {
      return soapFactory.createElement(localName);
   }

   /**
    * @deprecated Use javax.xml.soap.SOAPFactory.createElement(String localName, String prefix, String uri) instead
    * @param localName
    * @param prefix
    * @param uri
    * @return
    * @throws SOAPException
    */
   public SOAPElement create(String localName, String prefix, String uri) throws SOAPException
   {
      return soapFactory.createElement(localName, prefix, uri);
   }

   /**
    * @deprecated Use javax.xml.soap.SOAPFactory.createElement(javax.xml.soap.Name) 
    * @param name
    * @return
    * @throws SOAPException
    */
   public SOAPElement create(Name name) throws SOAPException
   {
      return soapFactory.createElement(name);
   }

   private SOAPElementFactory(SOAPFactory soapFactory)
   {
      this.soapFactory = soapFactory;
   }
}
