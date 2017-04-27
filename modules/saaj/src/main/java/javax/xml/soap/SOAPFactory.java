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

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

/** SOAPFactory is a factory for creating various objects that exist in the SOAP XML tree.
 *
 * SOAPFactory can be used to create XML fragments that will eventually end up in the SOAP part.
 * These fragments can be inserted as children of the SOAPHeaderElement or SOAPBodyElement or
 * SOAPEnvelope or other SOAPElement objects.
 *
 * SOAPFactory also has methods to create javax.xml.soap.Detail objects as well as java.xml.soap.Name objects.
 *
 * @author Scott.Stark@jboss.org
 */
public abstract class SOAPFactory
{
   private static SOAPFactory soapFactory;

   /**
    * Creates a new SOAPFactory object that is an instance of the default implementation (SOAP 1.1),
    * This method uses the following ordered lookup procedure to determine the SOAPFactory implementation class to load:
    *
    *    Use the javax.xml.soap.SOAPFactory system property.
    *    Use the properties file "lib/jaxm.properties" in the JRE directory. This configuration file is in standard java.util.Properties format and contains the fully qualified name of the implementation class with the key being the system property defined above.
    *    Use the Services API (as detailed in the JAR specification), if available, to determine the classname. The Services API will look for a classname in the file META-INF/services/javax.xml.soap.SOAPFactory in jars available to the runtime.
    *    Use the SAAJMetaFactory instance to locate the SOAPFactory implementation class.
    *
    * @return a new instance of a SOAPFactory
    * @throws SOAPException if there was an error creating the default SOAPFactory
    */
   public static SOAPFactory newInstance() throws SOAPException
   {
      if (soapFactory == null)
      {
         try
         {
            String propertyName = "javax.xml.soap.SOAPFactory";
            soapFactory = (SOAPFactory)SAAJFactoryLoader.loadFactory(propertyName, null);
         }
         catch (RuntimeException rte)
         {
            throw new SOAPException(rte);
         }

         // Use the SAAJMetaFactory instance to locate the MessageFactory implementation class.
         if (soapFactory == null)
         {
            SAAJMetaFactory saajFactory = SAAJMetaFactory.getInstance();
            soapFactory = saajFactory.newSOAPFactory(SOAPConstants.DEFAULT_SOAP_PROTOCOL);
         }

         if (soapFactory == null)
            throw new SOAPException("Failed to to determine the SOAPFactory implementation class");
      }
      return soapFactory;
   }


   /**
    * Creates a new SOAPFactory object that is an instance of the specified implementation, this method uses the SAAJMetaFactory
    * to locate the implementation class and create the SOAPFactory instance.
    *
    * @param protocol a string constant representing the class of the specified message factory implementation.
    *    May be either DYNAMIC_SOAP_PROTOCOL, DEFAULT_SOAP_PROTOCOL (which is the same as) SOAP_1_1_PROTOCOL, or SOAP_1_2_PROTOCOL.
    * @throws SOAPException if there was an error creating the specified SOAPFactory
    * @since SAAJ 1.3
    */
   public static SOAPFactory newInstance(String protocol) throws SOAPException
   {
      SAAJMetaFactory saajFactory = SAAJMetaFactory.getInstance();
      SOAPFactory factory = saajFactory.newSOAPFactory(protocol);

      if (factory == null)
         throw new SOAPException("Failed to to determine the SOAPFactory implementation class");

      return factory;
   }

   /** Creates a new Detail object which serves as a container for DetailEntry objects.
    *
    *  This factory method creates Detail objects for use in situations where it is not practical to use the SOAPFault abstraction.
    *
    * @return a Detail object
    * @throws SOAPException if there is a SOAP error
    */
   public abstract Detail createDetail() throws SOAPException;

   /**
    * Creates a SOAPElement object from an existing DOM Element. If the DOM Element that is passed in as an argument is already a
    * SOAPElement then this method must return it unmodified without any further work. Otherwise, a new SOAPElement is created and
    * a deep copy is made of the domElement argument. The concrete type of the return value will depend on the name of the domElement
    * argument. If any part of the tree rooted in domElement violates SOAP rules, a SOAPException will be thrown.
    * @param domElement the Element to be copied.
    * @return a new SOAPElement that is a copy of domElement.
    * @throws SOAPException if there is an error in creating the SOAPElement object
    * @since SAAJ 1.3
    */
   public SOAPElement createElement(Element domElement) throws SOAPException
   {
      throw new IllegalStateException("Should be implemented by concrete implementation of this class");
   }

   /** Create a SOAPElement object initialized with the given local name.
    *
    * @param localName a String giving the local name for the new element
    * @return the new SOAPElement object that was created
    * @throws SOAPException if there is an error in creating the SOAPElement object
    */
   public abstract SOAPElement createElement(String localName) throws SOAPException;

   /** Create a new SOAPElement object with the given local name, prefix and uri.
    *
    * @param localName a String giving the local name for the new element
    * @param prefix the prefix for this SOAPElement
    * @param uri a String giving the URI of the namespace to which the new element belongs
    * @return the new SOAPElement object that was created
    * @throws SOAPException if there is an error in creating the SOAPElement object
    */
   public abstract SOAPElement createElement(String localName, String prefix, String uri) throws SOAPException;

   /** Create a SOAPElement object initialized with the given Name object.
    *
    * @param name a Name object with the XML name for the new element
    * @return the new SOAPElement object that was created
    * @throws SOAPException if there is an error in creating the SOAPElement object
    */
   public abstract SOAPElement createElement(Name name) throws SOAPException;

   /**
    * Creates a SOAPElement object initialized with the given QName object.
    * The concrete type of the return value will depend on the name given to the new SOAPElement.
    * For instance, a new SOAPElement with the name "{http://www.w3.org/2003/05/soap-envelope}Envelope" would
    * cause a SOAPEnvelope that supports SOAP 1.2 behavior to be created.
    * @param qname a QName object with the XML name for the new element
    * @return the new SOAPElement object that was created
    * @throws SOAPException if there is an error in creating the SOAPElement object
    * @since SAAJ 1.3
    */
   public SOAPElement createElement(QName qname) throws SOAPException
   {
      throw new IllegalStateException("Should be implemented by concrete implementation of this class");
   }

   /**
    * Creates a new SOAPFault object initialized with the given reasonText  and faultCode
    * @param reasonText the ReasonText/FaultString for the fault
    * @param faultCode the FaultCode for the fault
    * @return a SOAPFault object
    * @throws SOAPException if there is a SOAP error
    * @since SAAJ 1.3
    */
   public abstract SOAPFault createFault(String reasonText, QName faultCode) throws SOAPException;

   /**
    * Creates a new default SOAPFault object
    * @return a SOAPFault object
    * @throws SOAPException if there is a SOAP error
    * @since SAAJ 1.3
    */
   public abstract SOAPFault createFault() throws SOAPException;

   /** Creates a new Name object initialized with the given local name.
    *
    * This factory method creates Name objects for use in situations where it is not practical to use the
    * SOAPEnvelope abstraction.
    * @return
    * @throws SOAPException
    */
   public abstract Name createName(String localName) throws SOAPException;

   /** Creates a new Name object initialized with the given local name, namespace prefix, and namespace URI.
    *
    *  This factory method creates Name objects for use in situations where it is not practical to use the SOAPEnvelope abstraction.
    * 
    * @param localName a String giving the local name
    * @param prefix a String giving the prefix of the namespace
    * @param uri a String giving the URI of the namespace
    * @return a Name object initialized with the given local name, namespace prefix, and namespace URI
    * @throws SOAPException  if there is a SOAP error
    */
   public abstract Name createName(String localName, String prefix, String uri) throws SOAPException;
}
