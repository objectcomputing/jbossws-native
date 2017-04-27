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
import java.util.Locale;

import javax.xml.namespace.QName;

/** An element in the SOAPBody object that contains error and/or status
 * information. This information may relate to errors in the SOAPMessage
 * object or to problems that are not related to the content in the message
 * itself. Problems not related to the message itself are generally errors in
 * processing, such as the inability to communicate with an upstream server. 
 * 
 * Depending on the protocol specified while creating the MessageFactory instance, a
 * SOAPFault has sub-elements as defined in the SOAP 1.1/SOAP 1.2 specification.
 * 
 * @author Scott.Stark@jboss.org
 * @author Thomas.Diesler@jboss.org
 */
public interface SOAPFault extends SOAPBodyElement
{
   /**
    * Creates an optional Detail object and sets it as the Detail object for this SOAPFault  object.
    *
    * It is illegal to add a detail when the fault already contains a detail.
    * Therefore, this method should be called only after the existing detail has been removed.
    * @return the new Detail object
    * @throws SOAPException  if this SOAPFault object already contains a valid Detail object
    */
   Detail addDetail() throws SOAPException;

   /**
    * Appends or replaces a Reason Text item containing the specified text message and an xml:lang derived from locale. 
    * If a Reason Text item with this xml:lang already exists its text value will be replaced with text. 
    * The locale parameter should not be null
    * 
    * Code sample:
    * 
    *    SOAPFault fault = ...;
    *    fault.addFaultReasonText("Version Mismatch", Locale.ENGLISH);
    *     
    * @param text reason message string
    * @param locale Locale object representing the locale of the message
    * @throws SOAPException if there was an error in adding the Reason text or the locale passed was null.
    * @throws UnsupportedOperationException if this message does not support the SOAP 1.2 concept of Fault Reason.
    * @since SAAJ 1.3
    */
   void addFaultReasonText(String text, Locale locale) throws SOAPException;

   /**
    * Adds a Subcode to the end of the sequence of Subcodes contained by this SOAPFault. 
    * Subcodes, which were introduced in SOAP 1.2, are represented by a recursive sequence of 
    * subelements rooted in the mandatory Code subelement of a SOAP Fault.
    * 
    * @param subcode a QName containing the Value of the Subcode
    * @throws SOAPException if there was an error in setting the Subcode
    * @throws UnsupportedOperationException if this message does not support the SOAP 1.2 concept of Subcode.
    * @since SAAJ 1.3
    */
   void appendFaultSubcode(javax.xml.namespace.QName subcode) throws SOAPException;

   /**
    * Returns the optional detail element for this SOAPFault  object.
    *
    * A Detail object carries application-specific error information related to SOAPBodyElement objects.
    *
    * @return a Detail object with application-specific error information
    */
   Detail getDetail();

   /**
    * Gets the fault actor for this SOAPFault object.
    * @return a String giving the actor in the message path that caused this SOAPFault object
    */
   String getFaultActor();

   /**
    * Gets the fault code for this SOAPFault object.
    * @return a String with the fault code
    */
   String getFaultCode();

   /**
    * Gets the mandatory SOAP 1.1 fault code for this SOAPFault object as a SAAJ Name object.
    * The SOAP 1.1 specification requires the value of the "faultcode" element to be of type QName.
    * This method returns the content of the element as a QName in the form of a SAAJ Name object.
    * This method should be used instead of the getFaultCode method since it allows applications to
    * easily access the namespace name without additional parsing.
    *
    * In the future, a QName object version of this method may also be added.
    *
    * @return a Name representing the faultcode
    */
   Name getFaultCodeAsName();

   /**
    * Gets the fault code for this SOAPFault object as a QName object.
    * @return a QName representing the faultcode
    * @since SAAJ 1.3
    */
   QName getFaultCodeAsQName();
   
   /**
    * Returns the optional Node element value for this SOAPFault object. The Node element is optional in SOAP 1.2.
    * @return Content of the env:Fault/env:Node element as a String or null if none
    * @throws UnsupportedOperationException if this message does not support the SOAP 1.2 concept of Fault Node.
    * @since SAAJ 1.3
    */
   String getFaultNode();

   /**
    * Returns an Iterator over a distinct sequence of Locales for which there are associated Reason Text items. 
    * Any of these Locales can be used in a call to getFaultReasonText in order to obtain a localized version of the Reason Text string.
    * @return an Iterator over a sequence of Locale  objects for which there are associated Reason Text items.
    * @throws SOAPException if there was an error in retrieving the fault Reason locales.
    * @throws UnsupportedOperationException if this message does not support the SOAP 1.2 concept of Fault Reason.
    * @since SAAJ 1.3
    */
   Iterator getFaultReasonLocales() throws SOAPException;
   
   /**
    * Returns an Iterator over a sequence of String objects containing all of the Reason Text items for this SOAPFault.
    * @return an Iterator over env:Fault/env:Reason/env:Text items.
    * @throws SOAPException if there was an error in retrieving the fault Reason texts.
    * @throws UnsupportedOperationException if this message does not support the SOAP 1.2 concept of Fault Reason.
    * @since SAAJ 1.3
    */
   Iterator getFaultReasonTexts() throws SOAPException;
   
   /**
    * Returns the Reason Text associated with the given Locale. If more than one such Reason Text exists the first matching Text is returned
    * @param locale the Locale for which a localized Reason Text is desired
    * @return the Reason Text associated with locale
    * @throws SOAPException if there was an error in retrieving the fault Reason text for the specified locale .
    * @throws UnsupportedOperationException if this message does not support the SOAP 1.2 concept of Fault Reason.
    * @since SAAJ 1.3
    */
   String getFaultReasonText(Locale locale) throws SOAPException;
   
   /**
    * Creates or replaces any existing Node element value for this SOAPFault object. The Node element is optional in SOAP 1.2.
    * @throws SOAPException f there was an error in setting the Node for this SOAPFault object.
    * @throws UnsupportedOperationException if this message does not support the SOAP 1.2 concept of Fault Node.
    * @since SAAJ 1.3
    */
   void setFaultNode(String uri) throws SOAPException;

   /**
    * Returns the optional Role element value for this SOAPFault object. The Role element is optional in SOAP 1.2.
    * @return Content of the env:Fault/env:Role element as a String or null if none
    * @throws UnsupportedOperationException if this message does not support the SOAP 1.2 concept of Fault Role.
    * @since SAAJ 1.3
    */
   String getFaultRole();

   /**
    * Creates or replaces any existing Role element value for this SOAPFault object. The Role element is optional in SOAP 1.2.
    * @throws SOAPException f there was an error in setting the Role for this SOAPFault object.
    * @throws UnsupportedOperationException if this message does not support the SOAP 1.2 concept of Fault Role.
    * @since SAAJ 1.3
    */
   void setFaultRole(String uri) throws SOAPException;
   /**
    * Gets the fault string for this SOAPFault object.
    * @return a String giving an explanation of the fault
    */
   String getFaultString();

   /**
    * Gets the locale of the fault string for this SOAPFault object.
    * @return a Locale object indicating the native language of the fault string or null if no locale was specified
    */
   Locale getFaultStringLocale();

   /**
    * Gets the Subcodes for this SOAPFault as an iterator over QNames.
    * @return an Iterator that accesses a sequence of QNames. This Iterator should not support the optional remove method. 
    *    The order in which the Subcodes are returned reflects the hierarchy of Subcodes present in the fault from top to bottom.
    * @throws UnsupportedOperationException if this message does not support the SOAP 1.2 concept of Subcode.  
    * @since SAAJ 1.3
    */
   Iterator getFaultSubcodes();
   
   /**
    * Removes any Subcodes that may be contained by this SOAPFault. Subsequent calls to getFaultSubcodes will return an empty 
    * iterator until a call to appendFaultSubcode is made.
    * @throws UnsupportedOperationException if this message does not support the SOAP 1.2 concept of Subcode.  
    * @since SAAJ 1.3
    */
   void removeAllFaultSubcodes();
   
   /**
    * Returns true if this SOAPFault has a Detail  subelement and false otherwise. Equivalent to (getDetail()!=null).
    * @return true if this SOAPFault has a Detail  subelement and false otherwise.
    * @since SAAJ 1.3
    */
   boolean hasDetail();
   
   /**
    * Sets this SOAPFault object with the given fault actor.
    *
    * The fault actor is the recipient in the message path who caused the fault to happen.
    *
    * @param faultActor  a String identifying the actor that caused this SOAPFault object
    * @throws SOAPException  if there was an error in adding the faultActor to the underlying XML tree.
    */
   void setFaultActor(String faultActor) throws SOAPException;

   /**
    * Sets this SOAPFault object with the give fault code.
    *
    * Fault codes, which given information about the fault, are defined in the SOAP 1.1 specification.
    * This element is mandatory in SOAP 1.1. Because the fault code is required to be a QName
    * it is preferable to use the setFaultCode(Name) form of this method.
    *
    * @param faultCode a String giving the fault code to be set.
    * It must be of the form "prefix:localName" where the prefix has been defined in a namespace declaration.
    * @throws SOAPException if there was an error in adding the faultCode to the underlying XML tree.
    */
   void setFaultCode(String faultCode) throws SOAPException;

   /**
    * Sets this SOAPFault object with the given fault code.
    *
    * Fault codes, which give information about the fault, are defined in the SOAP 1.1 specification.
    * A fault code is mandatory and must be of type QName. This method provides a convenient way to set a fault code.
    * For example,
    *
    *    SOAPEnvelope se = ...;
    *    //Create a qualified name in the SOAP namespace with a localName
    *    // of "Client".  Note that prefix parameter is optional and is null
    *    // here which causes the implementation to use an appropriate prefix.
    *    Name qname = se.createName("Client", null, SOAPConstants.URI_NS_SOAP_ENVELOPE);
    *    SOAPFault fault = ...;
    *    fault.setFaultCode(qname);
    *
    * It is preferable to use this method over setFaultCode(String).
    *
    * @param faultCode  a Name object giving the fault code to be set. It must be namespace qualified.
    * @throws SOAPException if there was an error in adding the faultcode element to the underlying XML tree.
    */
   void setFaultCode(Name faultCode) throws SOAPException;

   /**
    * Sets this SOAPFault object with the given fault code. It is preferable to use this method over setFaultCode(Name).
    * @param faultCode a QName object giving the fault code to be set. It must be namespace qualified.
    * @throws SOAPException if there was an error in adding the faultcode element to the underlying XML tree.
    * @since SAAJ 1.3
    */
   void setFaultCode(QName faultCode) throws SOAPException;

   /**
    * Sets the fault string for this SOAPFault object to the given string.
    *
    * @param faultString  a String giving an explanation of the fault
    * @throws SOAPException  if there was an error in adding the faultString to the underlying XML tree.
    */
   void setFaultString(String faultString) throws SOAPException;

   /**
    * Sets the fault string for this SOAPFault object to the given string and localized to the given locale.
    *
    * @param faultString  a String giving an explanation of the fault
    * @param locale a Locale object indicating the native language of the faultString
    * @throws SOAPException
    */
   void setFaultString(String faultString, Locale locale) throws SOAPException;
}
