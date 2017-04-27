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
package org.jboss.ws.core.soap;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPFaultElement;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.core.utils.SAAJUtils;
import org.jboss.wsf.common.DOMWriter;
import org.jboss.xb.QNameBuilder;
import org.w3c.dom.Attr;

/**
 * An element in the SOAPBody object that contains error and/or status information.
 * This information may relate to errors in the SOAPMessage object or to problems
 * that are not related to the content in the message itself. Problems not related
 * to the message itself are generally errors in processing, such as the inability
 * to communicate with an upstream server.
 *
 * The SOAPFault interface provides methods for retrieving the information contained
 * in a SOAPFault object and for setting the fault code, the fault actor, and a string
 * describing the fault. A fault code is one of the codes defined in the SOAP 1.1 specification
 * that describe the fault. An actor is an intermediate recipient to whom a message was routed.
 * The message path may include one or more actors, or, if no actors are specified, the message
 * goes only to the default actor, which is the final intended recipient.
 *
 * @author Thomas.Diesler@jboss.org
 */
public class SOAPFaultImpl extends SOAPBodyElementDoc implements SOAPFault
{
   // provide logging
   private static Logger log = Logger.getLogger(SOAPFaultImpl.class);

   private SOAPElement faultcode;
   // also represents Reason
   private SOAPElement faultstring;
   // also represents Role
   private SOAPElement faultactor;
   private SOAPElement faultnode;
   private Detail detail;

   public final static Set<QName> soap11FaultCodes = new HashSet<QName>();
   static
   {
      soap11FaultCodes.add(Constants.SOAP11_FAULT_CODE_CLIENT);
      soap11FaultCodes.add(Constants.SOAP11_FAULT_CODE_SERVER);
      soap11FaultCodes.add(Constants.SOAP11_FAULT_CODE_VERSION_MISMATCH);
      soap11FaultCodes.add(Constants.SOAP11_FAULT_CODE_MUST_UNDERSTAND);
   }
   public final static Set<QName> soap12FaultCodes = new HashSet<QName>();
   static
   {
      soap12FaultCodes.add(SOAPConstants.SOAP_VERSIONMISMATCH_FAULT);
      soap12FaultCodes.add(SOAPConstants.SOAP_MUSTUNDERSTAND_FAULT);
      soap12FaultCodes.add(SOAPConstants.SOAP_DATAENCODINGUNKNOWN_FAULT);
      soap12FaultCodes.add(SOAPConstants.SOAP_SENDER_FAULT);
      soap12FaultCodes.add(SOAPConstants.SOAP_RECEIVER_FAULT);
   }

   public SOAPFaultImpl() throws SOAPException
   {
      this(Constants.PREFIX_ENV, Constants.NS_SOAP11_ENV);
   }

   public SOAPFaultImpl(String prefix, String namespace) throws SOAPException
   {
      super(new NameImpl("Fault", prefix, namespace));
   }

   /** Gets the fault code for this SOAPFault object.
    */
   public String getFaultCode()
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
      {
         if (faultcode == null)
            findFaultCodeElement();

         return faultcode.getValue();
      }
      else
      {
         if (faultcode == null)
            findCodeElement();

         return getChildValueElement(faultcode).getValue();
      }
   }

   /**
    * Gets the mandatory SOAP 1.1 fault code for this SOAPFault object as a SAAJ Name object.
    */
   public Name getFaultCodeAsName()
   {
      return new NameImpl(getFaultCodeAsQName());
   }

   public QName getFaultCodeAsQName()
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
      {
         if (faultcode == null)
            findFaultCodeElement();

         return QNameBuilder.buildQName(faultcode, faultcode.getValue());
      }
      else
      {
         if (faultcode == null)
            findCodeElement();

         SOAPElement valueElement = getChildValueElement(faultcode);
         return QNameBuilder.buildQName(valueElement, valueElement.getValue());
      }
   }

   private void findFaultCodeElement()
   {
      faultcode = getChildElement(this, Constants.SOAP11_FAULTCODE);
      log.trace("findFaultCodeElement : " + faultcode);
   }

   private void findCodeElement()
   {
      faultcode = getChildElement(this, Constants.SOAP12_CODE);
      log.trace("findCodeElement : " + faultcode);
   }

   private static SOAPElement getChildValueElement(SOAPElement codeElement)
   {
      return getChildElement(codeElement, Constants.SOAP12_VALUE);
   }

   /** Sets this SOAPFault object with the give fault code.
    */
   public void setFaultCode(String faultCode) throws SOAPException
   {
      // Must be of the form "prefix:localName" where the prefix has been defined in a namespace declaration.
      QName qname = QNameBuilder.buildQName(this, faultCode);
      setFaultCode(qname);
   }

   /** Sets this SOAPFault object with the given fault code.
    */
   public void setFaultCode(Name faultCode) throws SOAPException
   {
      setFaultCode(((NameImpl)faultCode).toQName());
   }

   public void setFaultCode(QName faultCode) throws SOAPException
   {
      boolean isSOAP11 = Constants.NS_SOAP11_ENV.equals(getNamespaceURI());
      
      String faultCodeNS = faultCode.getNamespaceURI();
      if (faultCodeNS.length() == 0)
         throw new SOAPException("Fault code '" + faultCode + "' must be namespace qualified");
      
      /* JUDDI uses unqualified fault codes
      // Fix the namespace for SOAP1.1, if it is not a standard fault code
      if (isSOAP11 && faultCodeNS.length() == 0)
      {
         String localPart = faultCode.getLocalPart();
         for (QName soap11Fault : soap11FaultCodes)
         {
            if (soap11Fault.equals(localPart))
               throw new SOAPException("Fault code '" + faultCode + "' must be namespace qualified");
         }
         QName newFaultCode = new QName("http://unknown-namespace-uri", localPart);
         log.warn("Fault code '" + faultCode + "' must be namespace qualified, assuming: " + newFaultCode);
         faultCode = newFaultCode;
      }
      */

      if (isSOAP11)
      {
         if (faultcode == null)
         {
            findFaultCodeElement();
            if (faultcode == null)
               faultcode = addUnqualifiedFaultElement("faultcode");
         }
         setCode(faultcode, faultCode);
      }
      else
      {
         if (soap12FaultCodes.contains(faultCode) == false)
            throw new SOAPException(faultCode + " is not a standard SOAP 1.2 Code value");

         if (faultcode == null)
         {
            findCodeElement();
            if (faultcode == null)
            {
               faultcode = addQualifiedFaultElement("Code");
               addChildValueElement(faultcode);
            }
         }
         setCode(getChildValueElement(faultcode), faultCode);
      }
   }

   private static void setCode(SOAPElement codeElement, QName code) throws SOAPException
   {
      SAAJUtils.setQualifiedElementValue(codeElement, code);
   }

   private static SOAPElement addChildValueElement(SOAPElement codeElement) throws SOAPException
   {
      return codeElement.addChildElement("Value", codeElement.getPrefix(), codeElement.getNamespaceURI());
   }

   public Iterator getFaultSubcodes()
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
         throw new UnsupportedOperationException("SOAP 1.1 Fault does not support the concept of Subcode");

      ArrayList<QName> subcodes = new ArrayList<QName>();

      SOAPElement baseCodeElement = faultcode;
      SOAPElement subcodeElement;
      for (subcodeElement = getChildSubcodeElement(baseCodeElement); subcodeElement != null; subcodeElement = getChildSubcodeElement(baseCodeElement))
      {
         SOAPElement valueElement = getChildValueElement(subcodeElement);
         QName subcode = QNameBuilder.buildQName(valueElement, valueElement.getValue());
         subcodes.add(subcode);

         baseCodeElement = subcodeElement;
      }

      // this iterator should not support the remove method
      return Collections.unmodifiableList(subcodes).iterator();
   }

   private static SOAPElement getChildSubcodeElement(SOAPElement element)
   {
      return getChildElement(element, Constants.SOAP12_SUBCODE);
   }

   public void appendFaultSubcode(QName subcode) throws SOAPException
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
         throw new UnsupportedOperationException("SOAP 1.1 Fault does not support the concept of Subcode");

      String nsURI = subcode.getNamespaceURI();
      if (nsURI.length() == 0)
         throw new SOAPException("subcode must be namespace qualified: " + subcode);

      if (faultcode == null)
         findCodeElement();

      // find innermost subcode element
      SOAPElement baseCodeElement = faultcode;
      for (SOAPElement subcodeElement = getChildSubcodeElement(baseCodeElement); subcodeElement != null; subcodeElement = getChildSubcodeElement(baseCodeElement))
         baseCodeElement = subcodeElement;

      SOAPElement subcodeElement = baseCodeElement.addChildElement("Subcode", baseCodeElement.getPrefix(), baseCodeElement.getNamespaceURI());
      SOAPElement valueElement = addChildValueElement(subcodeElement);

      setCode(valueElement, subcode);
   }

   public void removeAllFaultSubcodes()
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
         throw new UnsupportedOperationException("SOAP 1.1 Fault does not support the concept of Subcode");

      if (faultcode == null)
         findFaultCodeElement();

      SOAPElement subcodeElement = getChildSubcodeElement(faultcode);
      if (subcodeElement != null)
         subcodeElement.detachNode();
   }

   /** Gets the fault string for this SOAPFault object.
    */
   public String getFaultString()
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
      {
         if (faultstring == null)
         {
            findFaultStringElement();
            if (faultstring == null)
               return null;
         }
         return faultstring.getValue();
      }
      else
      {
         try
         {
            return (String)getFaultReasonTexts().next();
         }
         catch (SOAPException e)
         {
            return null;
         }
      }
   }

   /** Gets the locale of the fault string for this SOAPFault object.
    */
   public Locale getFaultStringLocale()
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
      {
         if (faultstring == null)
         {
            findFaultStringElement();
            if (faultstring == null)
               return null;
         }
         return getLocale(faultstring);
      }
      else
      {
         try
         {
            return (Locale)getFaultReasonLocales().next();
         }
         catch (SOAPException e)
         {
            return null;
         }
      }
   }

   private void findFaultStringElement()
   {
      faultstring = getChildElement(this, Constants.SOAP11_FAULTSTRING);
      log.trace("findFaultStringElement : " + faultstring);
   }

   private static Locale getLocale(SOAPElement element)
   {
      Attr xmlLang = element.getAttributeNodeNS(Constants.NS_XML, "lang");
      return xmlLang != null ? toLocale(xmlLang.getValue()) : null;
   }

   /** Converts a language tag as defined in <a 
    * href="http://www.ietf.org/rfc/rfc3066.txt">IETF RFC 3066</a> to a 
    * {@link Locale}.
    */
   private static Locale toLocale(String languageTag)
   {
      String[] subtags = languageTag.split("-");
      // ignore subtags beyond the second
      return subtags.length == 1 ? new Locale(subtags[0]) : new Locale(subtags[0], subtags[1]);
   }

   /** Sets the fault string for this SOAPFault object to the given string.
    */
   public void setFaultString(String faultString) throws SOAPException
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
      {
         setFaultStringInternal(faultString);
         faultstring.removeAttributeNS(Constants.NS_XML, "lang");
      }
      else addFaultReasonText(faultString, Locale.getDefault());
   }

   /** Sets the fault string for this SOAPFault object to the given string and localized to the given locale.
    */
   public void setFaultString(String faultString, Locale locale) throws SOAPException
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
      {
         setFaultStringInternal(faultString);
         setLocale(faultstring, locale);
      }
      else addFaultReasonText(faultString, locale);
   }

   private void setFaultStringInternal(String faultString) throws SOAPException
   {
      if (faultstring == null)
      {
         findFaultStringElement();
         if (faultstring == null)
            faultstring = addUnqualifiedFaultElement("faultstring");
      }
      faultstring.setValue(faultString);
   }

   private static void setLocale(SOAPElement element, Locale locale)
   {
      element.setAttributeNS(Constants.NS_XML, "xml:lang", toLanguageTag(locale));
   }

   /** Converts a {@link Locale} to a language tag as defined in <a 
    * href="http://www.ietf.org/rfc/rfc3066.txt">IETF RFC 3066</a>.
    */
   private static String toLanguageTag(Locale locale)
   {
      String languageTag = locale.getLanguage();

      String country = locale.getCountry();
      if (country.length() != 0)
         languageTag += "-" + country;

      return languageTag;
   }

   public Iterator getFaultReasonTexts() throws SOAPException
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
         throw new UnsupportedOperationException("SOAP 1.1 Fault does not support the concept of Reason");

      if (faultstring == null)
      {
         findReasonElement();
         if (faultstring == null)
            return Collections.EMPTY_LIST.iterator();
      }

      ArrayList<String> texts = new ArrayList<String>();
      Iterator it = faultstring.getChildElements(Constants.SOAP12_TEXT);
      while (it.hasNext())
      {
         SOAPElement textElement = (SOAPElement)it.next();
         texts.add(textElement.getValue());
      }

      if (texts.isEmpty())
         throw new SOAPException("no Text elements found inside Reason");

      return texts.iterator();
   }

   public Iterator getFaultReasonLocales() throws SOAPException
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
         throw new UnsupportedOperationException("SOAP 1.1 Fault does not support the concept of Reason");

      if (faultstring == null)
      {
         findReasonElement();
         if (faultstring == null)
            return Collections.EMPTY_LIST.iterator();
      }

      ArrayList<Locale> locales = new ArrayList<Locale>();
      Iterator it = faultstring.getChildElements(Constants.SOAP12_TEXT);
      while (it.hasNext())
      {
         SOAPElement textElement = (SOAPElement)it.next();
         Locale locale = getLocale(textElement);
         if (locale == null)
            throw new SOAPException("lang attribute not present on Text element");
         locales.add(locale);
      }

      if (locales.isEmpty())
         throw new SOAPException("no Text elements found inside Reason");

      return locales.iterator();
   }

   public String getFaultReasonText(Locale locale) throws SOAPException
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
         throw new UnsupportedOperationException("SOAP 1.1 Fault does not support the concept of Reason");

      if (locale == null)
         return null;

      if (faultstring == null)
      {
         findReasonElement();
         if (faultstring == null)
            return null;
      }

      SOAPElement textElement = getTextElement(locale);
      return textElement != null ? textElement.getValue() : null;
   }

   private void findReasonElement()
   {
      faultstring = getChildElement(this, Constants.SOAP12_REASON);
      log.trace("findReasonElement: " + faultstring);
   }

   private SOAPElement getTextElement(Locale locale)
   {
      log.trace("getTextElement(" + locale + ")");
      SOAPElement textElement = null;

      Iterator it = faultstring.getChildElements(Constants.SOAP12_TEXT);
      while (it.hasNext())
      {
         SOAPElement element = (SOAPElement)it.next();
         if (locale.equals(getLocale(element)))
         {
            textElement = element;
            break;
         }
      }

      log.trace("getTextElement : " + textElement);
      return textElement;
   }

   public void addFaultReasonText(String text, Locale locale) throws SOAPException
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
         throw new UnsupportedOperationException("SOAP 1.1 Fault does not support the concept of Reason");

      if (locale == null)
         throw new SOAPException("locale passed is null");

      if (faultstring == null)
      {
         findReasonElement();
         if (faultstring == null)
            faultstring = addQualifiedFaultElement("Reason");
      }

      SOAPElement textElement = getTextElement(locale);
      if (textElement == null)
      {
         textElement = faultstring.addChildElement("Text", getPrefix(), getNamespaceURI());
         setLocale(textElement, locale);
      }
      textElement.setValue(text);
   }

   /** Gets the fault actor for this SOAPFault object.
    */
   public String getFaultActor()
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
      {
         if (faultactor == null)
         {
            findFaultActorElement();
            if (faultactor == null)
               return null;
         }
         return faultactor.getValue();
      }
      else return getFaultRole();
   }

   private void findFaultActorElement()
   {
      faultactor = getChildElement(this, Constants.SOAP11_FAULTACTOR);
      log.trace("findFaultActorElement : " + faultactor);
   }

   /** Sets this SOAPFault object with the given fault actor.
    */
   public void setFaultActor(String faultActor) throws SOAPException
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
      {
         if (faultactor == null)
         {
            findFaultActorElement();
            if (faultactor == null)
               faultactor = addUnqualifiedFaultElement("faultactor");
         }
         faultactor.setValue(faultActor);
      }
      else setFaultRole(faultActor);
   }

   public String getFaultRole()
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
         throw new UnsupportedOperationException("SOAP 1.1 Fault does not support the concept of Role");

      if (faultactor == null)
      {
         findRoleElement();
         if (faultactor == null)
            return null;
      }
      return faultactor.getValue();
   }

   private void findRoleElement()
   {
      faultactor = getChildElement(this, Constants.SOAP12_ROLE);
      log.trace("findRoleElement : " + faultactor);
   }

   public void setFaultRole(String uri) throws SOAPException
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
         throw new UnsupportedOperationException("SOAP 1.1 Fault does not support the concept of Role");

      if (faultactor == null)
      {
         findRoleElement();
         if (faultactor == null)
            faultactor = addQualifiedFaultElement("Role");
      }
      faultactor.setValue(uri);
   }

   public String getFaultNode()
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
         throw new UnsupportedOperationException("SOAP 1.1 Fault does not support the concept of Node");

      if (faultnode == null)
      {
         findNodeElement();
         if (faultnode == null)
            return null;
      }
      return faultnode.getValue();
   }

   private void findNodeElement()
   {
      faultnode = getChildElement(this, Constants.SOAP12_NODE);
      log.trace("findNodeElement : " + faultnode);
   }

   public void setFaultNode(String uri) throws SOAPException
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
         throw new UnsupportedOperationException("SOAP 1.1 Fault does not support the concept of Node");

      if (faultnode == null)
      {
         findNodeElement();
         if (faultnode == null)
            faultnode = addQualifiedFaultElement("Node");
      }
      faultnode.setValue(uri);
   }

   public boolean hasDetail()
   {
      return getDetail() != null;
   }

   /** Returns the optional detail element for this SOAPFault  object.
    */
   public Detail getDetail()
   {
      if (detail == null)
      {
         if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
            findSoap11DetailElement();
         else findSoap12DetailElement();
      }
      return detail;
   }

   /** Creates an optional Detail object and sets it as the Detail object for this SOAPFault  object.
    */
   public Detail addDetail() throws SOAPException
   {
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()))
      {
         if (detail == null)
            findSoap11DetailElement();

         if (detail != null)
            throw new SOAPException("this fault already contains a detail element");

         detail = new DetailImpl();
      }
      else
      {
         if (detail == null)
            findSoap12DetailElement();

         if (detail != null)
            throw new SOAPException("this fault already contains a detail element");

         detail = new DetailImpl(getPrefix(), getNamespaceURI());
      }

      detail = (Detail)addChildElement(detail);
      return detail;
   }

   private void findSoap11DetailElement()
   {
      detail = (Detail)getChildElement(this, Constants.SOAP11_DETAIL);
      log.trace("findSoap11DetailElement : " + detail);
   }

   private void findSoap12DetailElement()
   {
      detail = (Detail)getChildElement(this, Constants.SOAP12_DETAIL);
      log.trace("findSoap12DetailElement : " + detail);
   }

   @Override
   public SOAPElement addChildElement(SOAPElement child) throws SOAPException
   {
      if (!(child instanceof SOAPFaultElement))
         child = convertToFaultElement((SOAPElementImpl)child);

      return super.addChildElement(child);
   }

   private SOAPFaultElement convertToFaultElement(SOAPElementImpl element)
   {
      element.detachNode();

      QName elementName = element.getElementQName();
      SOAPFaultElement faultElement;
      if (Constants.NS_SOAP11_ENV.equals(getNamespaceURI()) ? Constants.SOAP11_DETAIL.equals(elementName) : Constants.SOAP12_DETAIL.equals(elementName))
         faultElement = new DetailImpl(element);
      else faultElement = new SOAPFaultElementImpl(element);

      log.trace("convertToFaultElement : " + faultElement);
      return faultElement;
   }

   QName getDefaultFaultCode()
   {
      return Constants.NS_SOAP11_ENV.equals(getNamespaceURI()) ? Constants.SOAP11_FAULT_CODE_SERVER : SOAPConstants.SOAP_RECEIVER_FAULT;
   }

   public void writeElement(Writer writer)
   {
      new DOMWriter(writer).print(this);
   }

   private static SOAPElement getChildElement(SOAPElement element, QName name)
   {
      Iterator it = element.getChildElements(name);
      return it.hasNext() ? (SOAPElement)it.next() : null;
   }

   private SOAPElement addUnqualifiedFaultElement(String localName) throws SOAPException
   {
      log.trace("addUnqualifiedFaultElement(" + localName + ")");
      return addChildElement(new SOAPFaultElementImpl(localName));
   }

   private SOAPElement addQualifiedFaultElement(String localName) throws SOAPException
   {
      log.trace("addQualifiedFaultElement(" + localName + ")");
      return addChildElement(new SOAPFaultElementImpl(localName, getPrefix(), getNamespaceURI()));
   }
}
