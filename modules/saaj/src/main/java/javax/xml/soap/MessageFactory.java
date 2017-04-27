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

import java.io.IOException;
import java.io.InputStream;

/**
 A factory for creating SOAPMessage objects.
 
 A SAAJ client can create a MessageFactory object using the method newInstance, as shown in the following lines of code.
 
 MessageFactory mf = MessageFactory.newInstance();
 MessageFactory mf12 = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
 
 
 All MessageFactory objects, regardless of how they are created, will produce SOAPMessage objects that have the following elements by default:
 
 * A SOAPPart object
 * A SOAPEnvelope object
 * A SOAPBody object
 * A SOAPHeader object 
 
 In some cases, specialized MessageFactory objects may be obtained that produce messages prepopulated with additional entries in the SOAPHeader object and the SOAPBody object. The content of a new SOAPMessage object depends on which of the two MessageFactory methods is used to create it.
 
 * createMessage()
 This is the method clients would normally use to create a request message.
 * createMessage(MimeHeaders, java.io.InputStream) -- message has content from the InputStream object and headers from the MimeHeaders object
 This method can be used internally by a service implementation to create a message that is a response to a request.
 
 *
 * @author Scott.Stark@jboss.org
 */
public abstract class MessageFactory
{
  
   /**
    * Creates a new MessageFactory object that is an instance of the default implementation (SOAP 1.1), 
    * This method uses the following ordered lookup procedure to determine the MessageFactory implementation class to load:
    * 
    * Use the javax.xml.soap.MessageFactory system property.
    * Use the properties file "lib/jaxm.properties" in the JRE directory. This configuration file is in standard java.util.Properties format and contains the fully qualified name of the implementation class with the key being the system property defined above.
    * Use the Services API (as detailed in the JAR specification), if available, to determine the classname. The Services API will look for a classname in the file META-INF/services/javax.xml.soap.MessageFactory in jars available to the runtime.
    * Use the SAAJMetaFactory instance to locate the MessageFactory implementation class.
    * @throws SOAPException if there was an error in creating the default implementation of the MessageFactory. 
    */
   public static MessageFactory newInstance() throws SOAPException
   {
      MessageFactory factory = null;
      try
      {
         String propertyName = "javax.xml.soap.MessageFactory";
         factory = (MessageFactory)SAAJFactoryLoader.loadFactory(propertyName, null);
      }
      catch (RuntimeException rte)
      {
         throw new SOAPException(rte);
      }
      
      // Use the SAAJMetaFactory instance to locate the MessageFactory implementation class.
      if (factory == null)
      {
         SAAJMetaFactory saajFactory = SAAJMetaFactory.getInstance();
         factory = saajFactory.newMessageFactory(SOAPConstants.DEFAULT_SOAP_PROTOCOL);
      }
      
      if (factory == null)
         throw new SOAPException("Failed to to determine the MessageFactory implementation class");
      
      return factory;
   }

   /**
    * Creates a new MessageFactory object that is an instance of the specified implementation. 
    * May be a dynamic message factory, a SOAP 1.1 message factory, or a SOAP 1.2 message factory. 
    * A dynamic message factory creates messages based on the MIME headers specified as arguments to the createMessage method. 
    * This method uses the SAAJMetaFactory to locate the implementation class and create the MessageFactory instance.
    * 
    * @param protocol a string constant representing the class of the specified message factory implementation. 
    *    May be either DYNAMIC_SOAP_PROTOCOL, DEFAULT_SOAP_PROTOCOL (which is the same as) SOAP_1_1_PROTOCOL, or SOAP_1_2_PROTOCOL.
    * @throws SOAPException if there was an error in creating the specified implementation of MessageFactory.
    * @since SAAJ 1.3
    */
   public static MessageFactory newInstance(String protocol) throws SOAPException
   {
      SAAJMetaFactory saajFactory = SAAJMetaFactory.getInstance();
      MessageFactory factory = saajFactory.newMessageFactory(protocol);
      
      if (factory == null)
         throw new SOAPException("Failed to to determine the MessageFactory implementation class");
      
      return factory;
   }
   
   /**
    * Creates a new SOAPMessage object with the default SOAPPart, SOAPEnvelope, SOAPBody, and SOAPHeader objects.
    * Profile-specific message factories can choose to prepopulate the SOAPMessage object with profile-specific headers.
    *
    * Content can be added to this message's SOAPPart object, and the message can be sent "as is" when a message
    * containing only a SOAP part is sufficient. Otherwise, the SOAPMessage object needs to create one or more
    * AttachmentPart objects and add them to itself. Any content that is not in XML format must be in an AttachmentPart object.
    *
    * @return a new SOAPMessage object
    * @throws SOAPException if a SOAP error occurs
    */
   public abstract SOAPMessage createMessage() throws SOAPException;

   /**
    * Internalizes the contents of the given InputStream object into a new SOAPMessage object and returns the SOAPMessage object.
    *
    * @param headers the transport-specific headers passed to the message in a transport-independent fashion for creation of the message
    * @param in the InputStream object that contains the data for a message
    * @return a new SOAPMessage object containing the data from the given InputStream object
    * @throws IOException if there is a problem in reading data from the input stream
    * @throws SOAPException  if the message is invalid
    */
   public abstract SOAPMessage createMessage(MimeHeaders headers, InputStream in) throws IOException, SOAPException;
}
