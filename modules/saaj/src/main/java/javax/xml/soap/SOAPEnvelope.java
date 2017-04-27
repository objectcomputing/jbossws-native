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

/** The container for the SOAPHeader and SOAPBody portions of a SOAPPart object.
 * By default, a SOAPMessage object is created with a SOAPPart object that has a SOAPEnvelope object.
 * The SOAPEnvelope object by default has an empty SOAPBody object and an empty SOAPHeader object.<p>
 * The SOAPBody object is required, and the SOAPHeader object, though optional,
 * is used in the majority of cases. If the SOAPHeader object is not needed, it can be deleted, which is shown later.<p>
 * A client can access the SOAPHeader and SOAPBody objects by calling the methods SOAPEnvelope.getHeader and SOAPEnvelope.getBody.
 * The following lines of code use these two methods after starting with the SOAPMessage object message to get the SOAPPart object
 * sp, which is then used to get the SOAPEnvelope object se.<p>
 *
 * <code>
 * SOAPPart sp = message.getSOAPPart();<br>
 * SOAPEnvelope se = sp.getEnvelope();<br>
 * SOAPHeader sh = se.getHeader();<br>
 * SOAPBody sb = se.getBody(); <br>
 * </code>
 * <p>
 * It is possible to change the body or header of a SOAPEnvelope object by retrieving the current one,
 * deleting it, and then adding a new body or header.
 * The javax.xml.soap.Node method deleteNode deletes the XML element (node) on which it is called.
 * For example, the following line of code deletes the SOAPBody object that is retrieved by the method getBody.
 * <p>
 * <code>se.getBody().detachNode();</code>
 * <p>
 * To create a SOAPHeader object to replace the one that was removed,
 * a client uses the method SOAPEnvelope.addHeader, which creates a new header and adds it to the SOAPEnvelope object.
 * Similarly, the method addBody creates a new SOAPBody object and adds it to the SOAPEnvelope object.
 * The following code fragment retrieves the current header, removes it, and adds a new one.
 * Then it retrieves the current body, removes it, and adds a new one.
 * <p>
 * <code>
 * SOAPPart sp = message.getSOAPPart();<br>
 * SOAPEnvelope se = sp.getEnvelope();<br>
 * se.getHeader().detachNode(); <br>
 * SOAPHeader sh = se.addHeader();<br>
 * se.getBody().detachNode(); <br>
 * SOAPBody sb = se.addBody();<br>
 * </code>
 * <p>
 * <b>It is an error to add a SOAPBody or SOAPHeader object if one already exists.</b>
 * The SOAPEnvelope interface provides three methods for creating Name objects.
 * One method creates Name objects with a local name, a namespace prefix, and a namesapce URI.
 * The second method creates Name objects with a local name and a namespace prefix,
 * and the third creates Name objects with just a local name.
 * <p>
 * The following line of code, in which se is a SOAPEnvelope object, creates a new Name object with all three.
 * <code>
 * Name name = se.createName("GetLastTradePrice", "WOMBAT", "http://www.wombat.org/trader");
 * </code>
 *
 * @author Scott.Stark@jboss.org
 */
public interface SOAPEnvelope extends SOAPElement
{
   public abstract SOAPBody addBody() throws SOAPException;

   public abstract SOAPHeader addHeader() throws SOAPException;

   public abstract Name createName(String localName) throws SOAPException;

   public abstract Name createName(String localName, String prefix, String uri) throws SOAPException;

   public abstract SOAPBody getBody() throws SOAPException;

   public abstract SOAPHeader getHeader() throws SOAPException;

}
