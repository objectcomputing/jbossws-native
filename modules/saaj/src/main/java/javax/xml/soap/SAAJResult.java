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

import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Element;

/**
 * Acts as a holder for the results of a JAXP transformation or a JAXB marshalling, in the form of a SAAJ tree. 
 * These results should be accessed by using the getResult() method. The DOMResult.getNode()  method should be avoided in almost all cases.
 * 
 * @author Thomas.Diesler@jboss.org
 * @since SAAJ 1.3
 */
public class SAAJResult extends DOMResult
{
   private SOAPElement rootElement;
   
   /**
    * Creates a SAAJResult that will present results in the form of a SAAJ tree that supports the default (SOAP 1.1) protocol.
    * 
    * This kind of SAAJResult is meant for use in situations where the results will be used as a parameter to a method that 
    * takes a parameter whose type, such as SOAPElement, is drawn from the SAAJ API. When used in a transformation, 
    * the results are populated into the SOAPPart of a SOAPMessage that is created internally. 
    * The SOAPPart returned by DOMResult.getNode() is not guaranteed to be well-formed. 
    * 
    * @throws SOAPException if there is a problem creating a SOAPMessage
    * @since SAAJ 1.3
    */
   public SAAJResult() throws SOAPException
   {
   }

   /**
    * Creates a SAAJResult that will present results in the form of a SAAJ tree that supports the specified protocol. 
    * The DYNAMIC_SOAP_PROTOCOL is ambiguous in this context and will cause this constructor to throw an UnsupportedOperationException.
    * 
    * This kind of SAAJResult is meant for use in situations where the results will be used as a parameter to a method that takes a parameter whose type, such as SOAPElement, is drawn from the SAAJ API. When used in a transformation the results are populated into the SOAPPart of a SOAPMessage that is created internally. The SOAPPart returned by DOMResult.getNode() is not guaranteed to be well-formed.
    *  
    * @param protocol the name of the SOAP protocol that the resulting SAAJ tree should support
    * @throws SOAPException if a SOAPMessage supporting the specified protocol cannot be created
    * @since SAAJ 1.3
    */
   public SAAJResult(String protocol) throws SOAPException
   {
   }

   /**
    * Creates a SAAJResult that will write the results into the SOAPPart of the supplied SOAPMessage. 
    * In the normal case these results will be written using DOM APIs and, as a result, the finished SOAPPart 
    * will not be guaranteed to be well-formed unless the data used to create it is also well formed. When used in a 
    * transformation the validity of the SOAPMessage after the transformation can be guaranteed only by means outside SAAJ specification.
    * 
    * @param message the message whose SOAPPart will be populated as a result of some transformation or marshalling operation
    * @since SAAJ 1.3
    */
   public SAAJResult(SOAPMessage message)
   {
      try
      {
         rootElement = message.getSOAPPart().getEnvelope();
      }
      catch (SOAPException ex)
      {
         throw new IllegalArgumentException("Cannot create SAAJ result", ex);
      }
   }

   /**
    * Creates a SAAJResult that will write the results as a child node of the SOAPElement specified. 
    * In the normal case these results will be written using DOM APIs and as a result may invalidate the structure of the SAAJ tree. 
    * This kind of SAAJResult should only be used when the validity of the incoming data can be guaranteed by means outside of the SAAJ specification.
    * 
    * @param rootNode the root to which the results will be appended
    * @since SAAJ 1.3
    */
   public SAAJResult(SOAPElement rootNode)
   {
      rootElement = rootNode;
   }

   /**
    * @return the resulting Tree that was created under the specified root Node.
    * @since SAAJ 1.3
    */
   public Node getResult()
   {
      return rootElement;
   }

   public void setNode(org.w3c.dom.Node node)
   {
      rootElement = null;
      if (node instanceof Element)
      {
         try
         {
            SOAPFactory factory = SOAPFactory.newInstance();
            rootElement = factory.createElement((Element)node);
         }
         catch (SOAPException ex)
         {
            throw new IllegalArgumentException("Cannot set node: " + node, ex);
         }
      }
   }
}

