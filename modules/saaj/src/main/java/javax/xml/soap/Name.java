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

/** A representation of an XML name. This interface provides methods for
 getting the local and namespace-qualified names and also for getting the
 prefix associated with the namespace for the name. It is also possible to get
 the URI of the namespace.
 
 The following is an example of a namespace declaration in an element.
 
 <wombat:GetLastTradePrice xmlns:wombat="http://www.wombat.org/trader">
 
 ("xmlns" stands for "XML namespace".) The following shows what the methods in
 the Name interface will return. 
 
 getQualifiedName will return "prefix:LocalName" = "WOMBAT:GetLastTradePrice" 
 getURI will return "http://www.wombat.org/trader" 
 getLocalName will return "GetLastTracePrice" 
 getPrefix will return "WOMBAT" 
 XML namespaces are used to disambiguate SOAP identifiers from
 application-specific identifiers. 
 Name objects are created using the method SOAPEnvelope.createName, which has
 two versions. One method creates Name objects with a local name, a namespace
 prefix, and a namespace URI. and the second creates Name objects with just a
 local name. The following line of code, in which se is a SOAPEnvelope object,
 creates a new Name object with all three.
 
 Name name = se.createName("GetLastTradePrice", "WOMBAT",
 "http://www.wombat.org/trader");
 
 The following line of code gives an example of how a Name object can be used.
 The variable element is a SOAPElement object. This code creates a new
 SOAPElement object with the given name and adds it to element. 
 
 element.addChildElement(name);

 @author Scott.Stark@jboss.org
 */
public interface Name
{
   /** Gets the local name part of the XML name that this Name object represents.
    *
    * @return a string giving the local name
    */
   public String getLocalName();

   /** Returns the prefix that was specified when this Name object was initialized.
    * This prefix is associated with the namespace for the XML name that this Name object represents.
    *
    * @return the prefix as a string
    */
   public String getPrefix();

   /** Gets the namespace-qualified name of the XML name that this Name object represents.
    *
    * @return the namespace-qualified name as a string
    */
   public String getQualifiedName();

   /** Returns the URI of the namespace for the XML name that this Name object represents.
    *
    * @return the URI as a string
    */
   public String getURI();
}
