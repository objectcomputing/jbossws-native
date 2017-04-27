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

import java.util.Iterator;

import javax.xml.transform.Source;

import org.w3c.dom.Document;

/** 
 * The container for the SOAP-specific portion of a SOAPMessage object.
 *  
 * All messages are required to have a SOAP part, so when a SOAPMessage object is created, 
 * it will automatically have a SOAPPart object. 
 * 
 * A SOAPPart object is a MIME part and has the MIME headers Content-Id, Content-Location, and Content-Type. 
 * Because the value of Content-Type must be "text/xml", a SOAPPart object automatically has a MIME header of 
 * Content-Type with its value set to "text/xml".
 *  
 * The value must be "text/xml" because content in the SOAP part of a message must be in XML format. 
 * Content that is not of type "text/xml" must be in an AttachmentPart object rather than in the SOAPPart object.
 * 
 * When a message is sent, its SOAP part must have the MIME header Content-Type set to "text/xml". 
 * Or, from the other perspective, the SOAP part of any message that is received must have the MIME header 
 * Content-Type with a value of "text/xml".
 * 
 * A client can access the SOAPPart object of a SOAPMessage object by calling the method SOAPMessage.getSOAPPart. 
 * The following line of code, in which message is a SOAPMessage object, retrieves the SOAP part of a message.
 * 
 * SOAPPart soapPart = message.getSOAPPart();
 * 
 * A SOAPPart object contains a SOAPEnvelope object, which in turn contains a SOAPBody object and a SOAPHeader object. 
 * The SOAPPart method getEnvelope can be used to retrieve the SOAPEnvelope object.
 *  
 * @author Scott.Stark@jboss.org
 */
public abstract class SOAPPart implements Document, Node
{
   public SOAPPart()
   {
   }

   public abstract SOAPEnvelope getEnvelope() throws SOAPException;

   public String getContentId()
   {
      String id = null;
      String[] header = getMimeHeader("Content-Id");
      if (header != null && header.length > 0)
      {
         id = header[0];
      }
      return id;
   }

   public void setContentId(String contentId)
   {
      setMimeHeader("Content-Id", contentId);
   }

   public String getContentLocation()
   {
      String location = null;
      String[] header = getMimeHeader("Content-Location");
      if (header != null && header.length > 0)
      {
         location = header[0];
      }
      return location;
   }

   public void setContentLocation(String contentLocation)
   {
      setMimeHeader("Content-Location", contentLocation);
   }

   public abstract void removeMimeHeader(String s);

   public abstract void removeAllMimeHeaders();

   public abstract String[] getMimeHeader(String s);

   public abstract void setMimeHeader(String s, String s1);

   public abstract void addMimeHeader(String s, String s1);

   public abstract Iterator getAllMimeHeaders();

   public abstract Iterator getMatchingMimeHeaders(String as[]);

   public abstract Iterator getNonMatchingMimeHeaders(String as[]);

   /** Sets the content of the SOAPEnvelope object with the data from the given Source object.
    * This Source must contain a valid SOAP document.
    * @param source the {@link javax.xml.transform.Source} object with the data to be set
    * @throws SOAPException if the implementation cannot convert the specified Source object
    */
   public abstract void setContent(Source source) throws SOAPException;

   /** Returns the content of the SOAPEnvelope as a JAXP <CODE>Source</CODE> object.
    * @return the content as a <CODE> javax.xml.transform.Source</CODE> object
    * @throws  javax.xml.soap.SOAPException  if the implementation cannot convert the specified <CODE>Source</CODE> object
    * @see #setContent(javax.xml.transform.Source) setContent(javax.xml.transform.Source)
    */
   public abstract Source getContent() throws SOAPException;
}
