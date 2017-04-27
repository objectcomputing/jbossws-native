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

import java.io.InputStream;
import java.util.Iterator;

import javax.activation.DataHandler;

/**
    A single attachment to a SOAPMessage object. A SOAPMessage object may contain zero, one, or
    many AttachmentPart objects. Each AttachmentPart object consists of two parts, applicationspecific
    content and associated MIME headers. The MIME headers consists of name/value pairs that can
    be used to identify and describe the content.
   
    An AttachmentPart object must conform to certain standards.
   
    1. It must conform to MIME [RFC2045] standards (http://www.ietf.org/rfc/rfc2045.txt)
    2. It MUST contain content
    3. The header portion MUST include the following header:
   
    Content-Type
    This header identifies the type of data in the content of an AttachmentPart object and MUST
    conform to [RFC2045]. The following is an example of a Content-Type header:
   
    Content-Type: application/xml
    
    The following line of code, in which ap is an AttachmentPart object, sets the header shown in the
    previous example.
   
    ap.setMimeHeader(“Content-Type”, “application/xml”);
    
    There are no restrictions on the content portion of an AttachmentPart object. The content may be
    anything from a simple plain text object to a complex XML document or image file.
   
    An AttachmentPart object is created with the method
    SOAPMessage.createAttachmentPart. After setting its MIME headers, the
    AttachmentPart object is added to the message that created it with the method
    SOAPMessage.addAttachmentPart.
   
    The following code fragment, in which m is a SOAPMessage object and contentStringl is a
    String, creates an instance of AttachmentPart, sets the AttachmentPart object with some
    content and header information, and adds the AttachmentPart object to the SOAPMessage object.
   
    AttachmentPart ap1 = m.createAttachmentPart();
    ap1.setContent(contentString1, “text/plain”);
    m.addAttachmentPart(ap1);
    
    The following code fragment creates and adds a second AttachmentPart instance to the same
    message. jpegData is a binary byte buffer representing the jpeg file.
   
    AttachmentPart ap2 = m.createAttachmentPart();
    byte[] jpegData = ...;
    ap2.setContent(new ByteArrayInputStream(jpegData), “image/jpeg”);
    m.addAttachmentPart(ap2);
    
    The getContent method retrieves the contents and header from an AttachmentPart object.
    Depending on the DataContentHandler objects present, the returned Object can either be a
    typed Java object corresponding to the MIME type or an InputStream object that contains the
    content as bytes.
   
    String content1 = ap1.getContent();
    java.io.InputStream content2 = ap2.getContent();
    
    The method clearContent removes all the content from an AttachmentPart object but does not
    affect its header information.
   
    ap1.clearContent();
 
 * @author Scott.Stark@jboss.org
 */
public abstract class AttachmentPart
{
   private String contentId;
   private String contentLocation;
   private String contentType;

   /**
    * Adds a MIME header with the specified name and value to this AttachmentPart object.
    *
    * Note that RFC822 headers can contain only US-ASCII characters.
    * @param name a String giving the name of the header to be added
    * @param value a String giving the value of the header to be added
    */
   public abstract void addMimeHeader(String name, String value);

   /**
    * Clears out the content of this AttachmentPart object. The MIME header portion is left untouched.
    */
   public abstract void clearContent();

   /**
    * Retrieves all the headers for this AttachmentPart object as an iterator over the MimeHeader objects.
    * @return an Iterator object with all of the Mime headers for this AttachmentPart object
    */
   public abstract Iterator getAllMimeHeaders();

   /**
    * Gets the content of this AttachmentPart object as a Java object.
    * The type of the returned Java object depends on
    * (1) the DataContentHandler object that is used to interpret the bytes and
    * (2) the Content-Type given in the header.
    *
    * For the MIME content types "text/plain", "text/html" and "text/xml", the DataContentHandler object does the
    * conversions to and from the Java types corresponding to the MIME types.
    *
    * For other MIME types,the DataContentHandler object can return an InputStream object that contains the content
    * data as raw bytes.
    *
    * A SAAJ-compliant implementation must, as a minimum, return a java.lang.String object corresponding to any
    * content stream with a Content-Type value of text/plain, a javax.xml.transform.stream.StreamSource object
    * corresponding to a content stream with a Content-Type value of text/xml,
    * a java.awt.Image object corresponding to a content stream with a Content-Type value of image/gif or image/jpeg.
    *
    * For those content types that an installed DataContentHandler object does not understand, the DataContentHandler
    * object is required to return a java.io.InputStream object with the raw bytes.
    *
    * @return a Java object with the content of this AttachmentPart object
    * @throws SOAPException if there is no content set into this AttachmentPart object or if there was a data transformation error
    */
   public abstract Object getContent() throws SOAPException;

   /**
    * Returns an InputStream which can be used to obtain the content of AttachmentPart as Base64 encoded character data, 
    * this method would base64 encode the raw bytes of the attachment and return.
    * 
    * @return an InputStream from which the Base64 encoded AttachmentPart can be read.
    * @throws SOAPException if there is no content set into this AttachmentPart object or if there was a data transformation error.
    * @since SAAJ 1.3
    */
   public abstract InputStream getBase64Content() throws SOAPException;

   /**
    * Gets the content of this AttachmentPart object as an InputStream as if a call had been made to getContent and no DataContentHandler 
    * had been registered for the content-type of this AttachmentPart.
    * 
    * Note that reading from the returned InputStream would result in consuming the data in the stream. 
    * It is the responsibility of the caller to reset the InputStream appropriately before calling a Subsequent API. 
    * If a copy of the raw attachment content is required then the getRawContentBytes() API should be used instead.
    *  
    * @return an InputStream from which the raw data contained by the AttachmentPart can be accessed.
    * @throws SOAPException if there is no content set into this AttachmentPart object or if there was a data transformation error.
    * @since SAAJ 1.3
    */
   public abstract java.io.InputStream getRawContent() throws SOAPException;

   /**
    * Gets the content of this AttachmentPart object as a byte[] array as if a call had been 
    * made to getContent and no DataContentHandler had been registered for the content-type of this AttachmentPart.
    * 
    * @return a byte[] array containing the raw data of the AttachmentPart.
    * @throws SOAPException if there is no content set into this AttachmentPart object or if there was a data transformation error.
    * @since SAAJ 1.3
    */
   public abstract byte[] getRawContentBytes() throws SOAPException;

   /**
    * Gets the DataHandler object for this AttachmentPart object.
    * @return object associated with this AttachmentPart object
    * @throws SOAPException if there is no data in this AttachmentPart object
    */
   public abstract DataHandler getDataHandler() throws SOAPException;

   /**
    * Retrieves all MimeHeader objects that match a name in the given array.
    * @param names a String array with the name(s) of the MIME headers to be returned
    * @return all of the MIME headers that match one of the names in the given array as an Iterator object
    */
   public abstract Iterator getMatchingMimeHeaders(String[] names);

   /**
    * Gets all the values of the header identified by the given String.
    * @param name the name of the header; example: "Content-Type"
    * @return a String array giving the value for the specified header
    */
   public abstract String[] getMimeHeader(String name);

   /**
    * Retrieves all MimeHeader objects whose name does not match a name in the given array.
    * @param names a String array with the name(s) of the MIME headers not to be returned
    * @return all of the MIME headers in this AttachmentPart object except those that match one of the names
    * in the given array. The nonmatching MIME headers are returned as an Iterator object.
    */
   public abstract Iterator getNonMatchingMimeHeaders(String[] names);

   /**
    * Returns the number of bytes in this AttachmentPart object.
    * @return the size of this AttachmentPart object in bytes or -1 if the size cannot be determined
    * @throws SOAPException if the content of this attachment is corrupted of if there was an exception while trying to determine the size.
    */
   public abstract int getSize() throws SOAPException;

   /**
    * Removes all the MIME header entries.
    */
   public abstract void removeAllMimeHeaders();

   /**
    * Removes all MIME headers that match the given name.
    * @param name the string name of the MIME header/s to be removed
    */
   public abstract void removeMimeHeader(String name);

   /**
    * Sets the content of this attachment part to that of the given Object and sets the value of the Content-Type header
    * to the given type. The type of the Object should correspond to the value given for the Content-Type.
    * This depends on the particular set of DataContentHandler objects in use.
    *
    * @param object the Java object that makes up the content for this attachment part
    * @param contentType the MIME string that specifies the type of the content
    * @throws IllegalArgumentException if the contentType does not match the type of the content object,
    * or if there was no DataContentHandler object for this content object
    */
   public abstract void setContent(Object object, String contentType);

   /**
    * Sets the content of this attachment part to that contained by the InputStream content and sets the value of the Content-Type header to the value contained in contentType.
    * 
    * A subsequent call to getSize() may not be an exact measure of the content size.
    *  
    * @param content the raw data to add to the attachment part
    * @param contentType the value to set into the Content-Type header
    * @throws SOAPException if an there is an error in setting the content 
    * @throws NullPointerException if content is null
    * @since SAAJ 1.3
    */
   public abstract void setRawContent(InputStream content, String contentType) throws SOAPException;

   /**
    * Sets the content of this attachment part to that contained by the byte[] array content and sets the value of the Content-Type 
    * header to the value contained in contentType.
    * 
    * @param content the raw data to add to the attachment part
    * @param contentType the value to set into the Content-Type header
    * @param offset the offset in the byte array of the content
    * @param len the number of bytes that form the content
    * @throws SOAPException if an there is an error in setting the content or content is null
    * @since SAAJ 1.3
    */
   public abstract void setRawContentBytes(byte[] content, int offset, int len, String contentType) throws SOAPException;

   /**
    * Sets the content of this attachment part from the Base64 source InputStream and sets the value of the Content-Type header 
    * to the value contained in contentType, This method would first decode the base64 input and write the resulting raw bytes to the attachment.
    * 
    * A subsequent call to getSize() may not be an exact measure of the content size.
    *  
    * @param content the base64 encoded data to add to the attachment part
    * @param contentType the value to set into the Content-Type header
    * @throws SOAPException if an there is an error in setting the content
    * @throws NullPointerException if content is null
    * @since SAAJ 1.3
    */
   public abstract void setBase64Content(InputStream content, String contentType) throws SOAPException;

   /**
    * Sets the given DataHandler object as the data handler for this AttachmentPart object.
    * Typically, on an incoming message, the data handler is automatically set.
    * When a message is being created and populated with content, the setDataHandler method can be used to get data
    * from various data sources into the message.
    * @param dataHandler the DataHandler object to be set
    * @throws IllegalArgumentException if there was a problem with the specified DataHandler object
    */
   public abstract void setDataHandler(DataHandler dataHandler);

   /**
    * Changes the first header entry that matches the given name to the given value, adding a new header if no existing
    * header matches. This method also removes all matching headers but the first.
    *
    * Note that RFC822 headers can only contain US-ASCII characters.
    * @param name a String giving the name of the header for which to search
    * @param value a String giving the value to be set for the header whose name matches the given name
    * @throws IllegalArgumentException if there was a problem with the specified mime header name or value
    */
   public abstract void setMimeHeader(String name, String value);

   /**
    * Gets the value of the MIME header whose name is "Content-Id".
    * @return a String giving the value of the "Content-Id" header or null if there is none
    */
   public String getContentId()
   {
      return contentId;
   }

   /**
    * Sets the MIME header whose name is "Content-Id" with the given value.
    * @param contentId a String giving the value of the "Content-Id" header
    * @throws IllegalArgumentException if there was a problem with the specified contentId value
    */
   public void setContentId(String contentId)
   {
      this.contentId = contentId;
   }

   /**
    * Gets the value of the MIME header whose name is "Content-Location".
    * @return a String giving the value of the "Content-Location" header or null if there is none
    */
   public String getContentLocation()
   {
      return contentLocation;
   }

   /**
    * Sets the MIME header whose name is "Content-Location" with the given value.
    * @throws IllegalArgumentException if there was a problem with the specified content location
    */
   public void setContentLocation(String contentLocation)
   {
      this.contentLocation = contentLocation;
   }

   /**
    * Gets the value of the MIME header whose name is "Content-Type".
    * @return a String giving the value of the "Content-Type" header or null if there is none
    */
   public String getContentType()
   {
      return contentType;
   }

   /**
    * Sets the MIME header whose name is "Content-Type" with the given value.
    * @param contentType a String giving the value of the "Content-Type" header
    * @throws IllegalArgumentException if there was a problem with the specified content type
    */
   public void setContentType(String contentType)
   {
      this.contentType = contentType;
   }
}
