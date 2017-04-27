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

/** An object representing the contents in the SOAP header part of the SOAP
 * envelope. The immediate children of a SOAPHeader object can be represented
 * only as SOAPHeaderElement objects.
 * 
 * A SOAPHeaderElement object can have other SOAPElement objects as its children. 

 * @author Scott.Stark@jboss.org
 */
public interface SOAPHeaderElement extends SOAPElement
{
   /** Returns the uri of the actor associated with this SOAPHeaderElement object.
    *
    * @return  a String giving the URI of the actor
    */
   public String getActor();

   /** Returns whether the mustUnderstand attribute for this SOAPHeaderElement object is turned on.
    *
    * @return true if the mustUnderstand attribute of this SOAPHeaderElement object is turned on; false otherwise
    */
   public boolean getMustUnderstand();

   /**
    * Returns the boolean value of the relay attribute for this SOAPHeaderElement
    * @return true if the relay attribute is turned on; false otherwise
    * @throws UnsupportedOperationException - if this message does not support the SOAP 1.2 concept of Relay attribute.
    * @since SAAJ 1.3
    */
   public boolean getRelay();

   /**
    * Sets the relay attribute for this SOAPHeaderElement to be either true or false.
    *
    * The SOAP relay attribute is set to true to indicate that the SOAP header block must be relayed by any node that is
    * targeted by the header block but not actually process it. This attribute is ignored on header blocks whose mustUnderstand
    * attribute is set to true or that are targeted at the ultimate reciever (which is the default).
    * The default value of this attribute is false.
    * @param relay the new value of the relay attribute
    * @throws SOAPException if there is a problem in setting the relay attribute.
    * @throws UnsupportedOperationException if this message does not support the SOAP 1.2 concept of Relay attribute.
    * @since SAAJ 1.3
    */
   public void setRelay(boolean relay) throws SOAPException;

   /**
    * Returns the value of the Role attribute of this SOAPHeaderElement.
    * @return a String giving the URI of the Role
    * @throws UnsupportedOperationException if this message does not support the SOAP 1.2 concept of Fault Role.
    * @since SAAJ 1.3
    */
   public String getRole();

   /**
    * Sets the Role associated with this SOAPHeaderElement  object to the specified Role.
    *
    * @param roleURI the URI of the Role
    * @throws SOAPException if there is an error in setting the role
    * @throws UnsupportedOperationException if this message does not support the SOAP 1.2 concept of Fault Role
    * @since SAAJ 1.3
    */
   public void setRole(String roleURI) throws SOAPException;

   /** Sets the actor associated with this SOAPHeaderElement object to the specified actor.
    * The default value of an actor is: SOAPConstants.URI_SOAP_ACTOR_NEXT
    *
    * @param actorURI  a String giving the URI of the actor to set
    */
   public void setActor(String actorURI);

   /** Sets the mustUnderstand attribute for this SOAPHeaderElement object to be on or off.
    *
    * If the mustUnderstand attribute is on, the actor who receives the SOAPHeaderElement must process it correctly.
    * This ensures, for example, that if the SOAPHeaderElement object modifies the message,
    * that the message is being modified correctly.
    *
    * @param mustUnderstand  true to set the mustUnderstand attribute on; false to turn if off
    */
   public void setMustUnderstand(boolean mustUnderstand);
}
