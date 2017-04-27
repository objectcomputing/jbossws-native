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

import java.util.Locale;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

/** An object that represents the contents of the SOAP body element in a SOAP
 * message. A SOAP body element consists of XML data that affects the way the
 * application-specific content is processed.
 * 
 * A SOAPBody object contains SOAPBodyElement objects, which have the content
 * for the SOAP body. A SOAPFault object, which carries status and/or error
 * information, is an example of a SOAPBodyElement object.

 * @author Scott.Stark@jboss.org
 */
public interface SOAPBody extends SOAPElement
{
   /** Creates a new SOAPBodyElement object with the specified name and adds it to this SOAPBody object.
    *
    * @param name a Name object with the name for the new SOAPBodyElement object
    * @return the new SOAPBodyElement object
    * @throws SOAPException if a SOAP error occurs
    */
   public abstract SOAPBodyElement addBodyElement(Name name) throws SOAPException;

   /**
    * Creates a new SOAPBodyElement object with the specified QName and adds it to this SOAPBody object.
    * @param qname a QName object with the qname for the new SOAPBodyElement object
    * @return the new SOAPBodyElement object
    * @throws SOAPException if a SOAP error occurs
    * @since SAAJ 1.3
    */
   public abstract SOAPBodyElement addBodyElement(QName qname) throws SOAPException;

   /** Adds the root node of the DOM Document to this SOAPBody object.
    *
    * Calling this method invalidates the document parameter.
    * The client application should discard all references to this Document and its contents upon calling addDocument.
    * The behavior of an application that continues to use such references is undefined.
    *
    * @param doc  the Document object whose root node will be added to this SOAPBody.
    * @return the SOAPBodyElement that represents the root node that was added.
    * @throws SOAPException  if the Document cannot be added
    */
   public abstract SOAPBodyElement addDocument(Document doc) throws SOAPException;

   /** Creates a new SOAPFault object and adds it to this SOAPBody object.
    *
    * The new SOAPFault will have default values set for the mandatory child elements faultcode and faultstring.
    * A SOAPBody may contain at most one SOAPFault child element
    *
    * @return the new SOAPFault object
    * @throws SOAPException if there is a SOAP error
    */
   public abstract SOAPFault addFault() throws SOAPException;

   /** Creates a new SOAPFault object and adds it to this SOAPBody object.
    *
    * The new SOAPFault will have a faultcode element that is set to the faultCode parameter and a faultstring
    * set to faultString.
    *
    * A SOAPBody may contain at most one SOAPFault child element
    *
    * @param faultCode a Name object giving the fault code to be set; must be one of the fault codes defined in the SOAP 1.1 specification and of type QName
    * @param faultString a String giving an explanation of the fault
    * @return the new SOAPFault object
    * @throws SOAPException if there is a SOAP error
    */
   public abstract SOAPFault addFault(Name faultCode, String faultString) throws SOAPException;
   
   /**
    * Creates a new SOAPFault object and adds it to this SOAPBody  object. The type of the SOAPFault  will be a SOAP 1.1 or a SOAP 1.2 SOAPFault 
    * depending on the protocol specified while creating the MessageFactory  instance.
    * 
    * For SOAP 1.2 the faultCode parameter is the value of the Fault/Code/Value element and the faultString parameter is the value 
    * of the Fault/Reason/Text element. For SOAP 1.1 the faultCode parameter is the value of the faultcode element and the 
    * faultString parameter is the value of the faultstring element.
    * 
    * In case of a SOAP 1.2 fault, the default value for the mandatory xml:lang attribute on the Fault/Reason/Text element will be 
    * set to java.util.Locale.getDefault()
    * 
    * A SOAPBody may contain at most one SOAPFault child element
    *  
    * @param faultCode a QName object giving the fault code to be set; must be one of the fault codes defined in the version of SOAP specification in use
    * @param faultString a String giving an explanation of the fault
    * @throws SOAPException if there is a SOAP error
    * @since SAAJ 1.3
    */
   public abstract SOAPFault addFault(QName faultCode, String faultString) throws SOAPException;

   /** Creates a new SOAPFault object and adds it to this SOAPBody object.
    *
    * The new SOAPFault will have a faultcode element that is set to the faultCode parameter and a faultstring
    * set to faultString and localized to locale.
    *
    * A SOAPBody may contain at most one SOAPFault child element
    *
    * @param faultCode a Name object giving the fault code to be set; must be one of the fault codes defined in the SOAP 1.1 specification and of type QName
    * @param faultString a String giving an explanation of the fault
    * @param locale a Locale object indicating the native language of the faultString
    * @return the new SOAPFault object
    * @throws SOAPException if there is a SOAP error
    */
   public abstract SOAPFault addFault(Name faultCode, String faultString, Locale locale) throws SOAPException;

   /**
    * Creates a new SOAPFault object and adds it to this SOAPBody object. The type of the SOAPFault will be a SOAP 1.1 or a SOAP 1.2 
    * SOAPFault depending on the protocol specified while creating the MessageFactory instance.
    * 
    * For SOAP 1.2 the faultCode parameter is the value of the Fault/Code/Value element and the faultString parameter is 
    * the value of the Fault/Reason/Text element. For SOAP 1.1 the faultCode parameter is the value of the faultcode element 
    * and the faultString parameter is the value of the faultstring element.
    * 
    * A SOAPBody may contain at most one SOAPFault child element.
    *  
    * @param faultCode a QName object giving the fault code to be set; must be one of the fault codes defined in the version of SOAP specification in use
    * @param faultString a String giving an explanation of the fault
    * @param locale a Locale object indicating the native language of the faultString
    * @return the new SOAPFault object
    * @throws SOAPException if there is a SOAP error
    * @since SAAJ 1.3
    */
   public abstract SOAPFault addFault(QName faultCode, String faultString, Locale locale) throws SOAPException;
   
   /** Returns the SOAPFault object in this SOAPBody  object.
    *
    * @return the SOAPFault object in this SOAPBody  object
    */
   public abstract SOAPFault getFault();

   /** Indicates whether a SOAPFault object exists in this SOAPBody object.
    *
    * @return true if a SOAPFault object exists in this SOAPBody object; false  otherwise
    */
   public abstract boolean hasFault();
   
   /**
    * Creates a new DOM Document and sets the first child of this SOAPBody as it's document element. 
    * The child SOAPElement is removed as part of the process.
    * @return the Document representation of the SOAPBody content.
    * @throws SOAPException if there is not exactly one child SOAPElement of the  SOAPBody.
    * @since SAAJ 1.3
    */
   public Document extractContentAsDocument() throws SOAPException;   
}
