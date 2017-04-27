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
package org.jboss.ws.extensions.addressing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingConstants;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.AddressingType;
import javax.xml.ws.addressing.AttributedURI;
import javax.xml.ws.addressing.EndpointReference;
import javax.xml.ws.addressing.ReferenceParameters;
import javax.xml.ws.addressing.Relationship;

/** 
 * Each instance is associated with a particular WS-Addressing schema whose 
 * namespace URI is returned by its <code>getAddressingVersion</code> method.
 *   
 * The namespace of each  key in the underlying map must match this URI and the 
 * local names of all the keys must be exactly the names of the Message Addressing Properties 
 * defined in that version of the WS-Addressing specification.
 * 
 * Each value in the underlying type must be an instance of <code>AddressingType</code> whose 
 * WS-Addressing version (determined by its <code>getAddressingVersion</code>) method must
 * match the WS-Addressing version associated with the <code>AddressingProperties</code>.
 *
 * TODO: verify that AddressingPropertiesImpl can extend ElementExtensibleImpl. 
 * See http://jira.jboss.com/jira/browse/JBWS-728
 * 
 * @author Thomas.Diesler@jboss.org
 * @since 14-Nov-2005
 */
public class AddressingPropertiesImpl extends ElementExtensibleImpl implements AddressingProperties
{
   private static AddressingConstants ADDR = new AddressingConstantsImpl();

   // A REQUIRED absolute URI representing the address of the intended receiver of this message.
   private AttributedURI to;
   // A REQUIRED absolute URI that uniquely identifies the semantics implied by this message.
   private AttributedURI action;
   // An OPTIONAL absolute URI that uniquely identifies the message.
   private AttributedURI messageId;
   // An OPTIONAL pair of values that indicate how this message relates to another message.
   private Relationship[] relatesTo;
   // An OPTIONAL endpoint reference for the intended receiver for replies to this message.
   private EndpointReference replyTo;
   // An OPTIONAL endpoint reference for the intended receiver for faults related to this message.
   private EndpointReference faultTo;
   // An OPTIONAL reference to the endpoint from which the message originated.
   private EndpointReference from;
   // Corresponds to the value of the [reference parameters] property of the endpoint reference to which the message is addressed.
   private ReferenceParameters refParams = new ReferenceParametersImpl();

   private Map<QName, AddressingType> addrTypes = new HashMap<QName, AddressingType>();

	private boolean initialized = false;
	
	public AttributedURI getTo()
   {
      return to;
   }

   public void setTo(AttributedURI to)
   {
      this.to = to;
   }

   public AttributedURI getAction()
   {
      return action;
   }

   public void setAction(AttributedURI action)
   {
      this.action = action;
   }

   public AttributedURI getMessageID()
   {
      return messageId;
   }

   public void setMessageID(AttributedURI iri)
   {
      this.messageId = iri;
   }

   public Relationship[] getRelatesTo()
   {
      return relatesTo;
   }

   public void setRelatesTo(Relationship[] relatesTo)
   {
      this.relatesTo = relatesTo;
   }

   public EndpointReference getReplyTo()
   {
      return replyTo;
   }

   public void setReplyTo(EndpointReference replyTo)
   {
      this.replyTo = replyTo;
   }

   public EndpointReference getFaultTo()
   {
      return faultTo;
   }

   public void setFaultTo(EndpointReference faultTo)
   {
      this.faultTo = faultTo;
   }

   public EndpointReference getFrom()
   {
      return from;
   }

   public void setFrom(EndpointReference from)
   {
      this.from = from;
   }

   public ReferenceParameters getReferenceParameters()
   {
      return refParams;
   }

   /**
    * Initializes the properties as a destination using the given
    * <code>EndpointReference</code>.  The <bold>To</bold> property is initialized 
    * using the <bold>Address</bold> property of the <code>EndpointReference</code> 
    * and the <bold>ReferenceParameters</bold> property is initialized using the
    * <bold>ReferenceParameters</bold> property of the <code>EndpointReference</code>.
    *
    * @param epr The <code>EndpointReference</code> representing the destination.
    */
   public void initializeAsDestination(EndpointReference epr)
   {
		if(initialized) return;
		
		if (epr == null)
         throw new IllegalArgumentException("Invalid null endpoint reference");

		this.to = epr.getAddress();
      
      ReferenceParameters srcParams = epr.getReferenceParameters();
      for (Object obj : srcParams.getElements())
      {
         SOAPElement soapElement = (SOAPElement)obj;
         soapElement.setAttributeNS(getNamespaceURI(), "wsa:IsReferenceParameter", "true");
         addElement(soapElement.cloneNode(true));
      }

		this.initialized = true;
	}

   /**
    * Initialize this <code>AddressingProperties</code> as a reply to the 
    * given message.  As described in the WS-Addressing Core specification.  
    * The <bold>ReplyTo</bold> property is using the <bold>Address</bold> 
    * property of the source <code>AddressingProperties</code> and the 
    * <bold>ReferenceParameters</bold> property is initialized using the
    * <bold>ReferenceParameters</bold> property of the source message.
    *
    * @param  props The source <code>AddressingProperties</code>
    * @param isFault <code>true</code> if the reply is a Fault message.
    */
   public void initializeAsReply(AddressingProperties props, boolean isFault)
   {
		if(initialized) return;
		
		EndpointReference epr = (isFault ? props.getFaultTo() : null);
      if (epr == null)
      {
         epr = props.getReplyTo();
      }
      this.to = (epr != null ? epr.getAddress() : new AttributedURIImpl(ADDR.getAnonymousURI()));

      if (epr != null)
      {
         ReferenceParameters srcParams = epr.getReferenceParameters();
         for (Object obj : srcParams.getElements())
         {
            SOAPElement soapElement = (SOAPElement)obj;
            soapElement.setAttributeNS(getNamespaceURI(), "wsa:IsReferenceParameter", "true");
            addElement(soapElement.cloneNode(true));
         }
      }      
      if (props.getMessageID() != null)
      {
         AddressingBuilder builder = AddressingBuilder.getAddressingBuilder();
         Relationship rel = builder.newRelationship(props.getMessageID().getURI());
         this.relatesTo = new Relationship[] { rel };
      }

		this.initialized = true;
	}

   // Map interface ****************************************************************

   public int size()
   {
      return addrTypes.size();
   }

   public boolean isEmpty()
   {
      return addrTypes.isEmpty();
   }

   public boolean containsKey(Object arg0)
   {
      return addrTypes.containsKey(arg0);
   }

   public boolean containsValue(Object arg0)
   {
      return addrTypes.containsValue(arg0);
   }

   public AddressingType get(Object arg0)
   {
      return addrTypes.get(arg0);
   }

   public AddressingType put(QName arg0, AddressingType arg1)
   {
      return addrTypes.put(arg0, arg1);
   }

   public AddressingType remove(Object arg0)
   {
      return addrTypes.remove(arg0);
   }

   public void putAll(Map<? extends QName, ? extends AddressingType> arg0)
   {
      addrTypes.putAll(arg0);
   }

   public void clear()
   {
      addrTypes.clear();
   }

   public Set<QName> keySet()
   {
      return addrTypes.keySet();
   }

   public Collection<AddressingType> values()
   {
      return addrTypes.values();
   }

   public Set<Entry<QName, AddressingType>> entrySet()
   {
      return addrTypes.entrySet();
   }
}
