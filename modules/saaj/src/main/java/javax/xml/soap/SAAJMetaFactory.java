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

/**
 * The access point for the implementation classes of the factories defined in the SAAJ API.
 * All of the newInstance methods defined on factories in SAAJ 1.3 defer to instances of this class to do the actual object creation.
 * The implementations of newInstance() methods (in SOAPFactory and MessageFactory) that existed in SAAJ 1.2
 * have been updated to also delegate to the SAAJMetaFactory when the SAAJ 1.2 defined lookup fails to locate the Factory implementation class name.
 * 
 * SAAJMetaFactory is a service provider interface. There are no public methods on this class.
 * 
 * @since SAAJ 1.3
 */
public abstract class SAAJMetaFactory
{
   protected SAAJMetaFactory()
   {
   }
   
   /**
    * Creates a new instance of a concrete SAAJMetaFactory object. The SAAJMetaFactory is an SPI, 
    * it pulls the creation of the other factories together into a single place. Changing out the SAAJMetaFactory 
    * has the effect of changing out the entire SAAJ implementation. Service providers provide the name of their SAAJMetaFactory 
    * implementation. This method uses the following ordered lookup procedure to determine the SAAJMetaFactory implementation class to load:
    * 
    *   - Use the javax.xml.soap.MetaFactory system property.
    *   - Use the properties file "lib/jaxm.properties" in the JRE directory. This configuration file is in standard java.util.Properties format and contains the fully qualified name of the implementation class with the key being the system property defined above.
    *   - Use the Services API (as detailed in the JAR specification), if available, to determine the classname. The Services API will look for a classname in the file META-INF/services/javax.xml.soap.MetaFactory in jars available to the runtime.
    *   - Default to com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl.
    *    
    * @return a concrete SAAJMetaFactory object
    * @throws SOAPException if there is an error in creating the SAAJMetaFactory
    */
   static SAAJMetaFactory getInstance() throws SOAPException
   {
      String propertyName = "javax.xml.soap.MetaFactory";
      String defaultImpl = "org.jboss.ws.core.soap.SAAJMetaFactoryImpl";
      SAAJMetaFactory factory = (SAAJMetaFactory)SAAJFactoryLoader.loadFactory(propertyName, defaultImpl);
      
      if (factory == null)
         throw new SOAPException("Failed to to determine the implementation class for: " + propertyName);

      return factory;
   }
   
   /**
    * Creates a MessageFactory object for the given String protocol.
    * @param protocol a String indicating the protocol (SOAPConstants.SOAP_1_1_PROTOCOL, SOAPConstants.SOAP_1_2_PROTOCOL, SOAPConstants.DYNAMIC_SOAP_PROTOCOL)
    * @throws SOAPException if there is an error in creating the MessageFactory
    */
   protected abstract MessageFactory newMessageFactory(String protocol) throws SOAPException;
   
   /**
    * Creates a SOAPFactory object for the given String protocol.
    * @param protocol a String indicating the protocol (SOAPConstants.SOAP_1_1_PROTOCOL, SOAPConstants.SOAP_1_2_PROTOCOL, SOAPConstants.DYNAMIC_SOAP_PROTOCOL)
    * @throws SOAPException if there is an error in creating the SOAPFactory
    */
   protected abstract SOAPFactory newSOAPFactory(String protocol) throws SOAPException;
}
