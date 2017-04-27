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
package javax.xml.rpc.soap;

import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;

/** The SOAPFaultException exception represents a SOAP fault.
 * 
 * The message part in the SOAP fault maps to the contents of faultdetail
 * element accessible through the getDetail method on the SOAPFaultException.
 * The method createDetail on the javax.xml.soap.SOAPFactory creates an instance
 * of the javax.xml.soap.Detail.
 * 
 * The faultstring provides a human-readable description of the SOAP fault. The
 * faultcode element provides an algorithmic mapping of the SOAP fault.
 *  
 * Refer to SOAP 1.1 and WSDL 1.1 specifications for more details of the SOAP
 * faults. 
 * 
 * @author Scott.Stark@jboss.org
 * @author Thomas.Diesler@jboss.org
 * @author Rahul Sharma (javadoc)
 */
public class SOAPFaultException extends RuntimeException
{
   private static final long serialVersionUID = -7224636940495025621L;

   // provide logging
   private static Logger log = Logger.getLogger(SOAPFaultException.class.getName());

   private QName faultCode;
   private String faultString;
   private String faultActor;
   private Detail faultDetail;

   public SOAPFaultException(QName faultCode, String faultString, String faultActor, Detail faultDetail)
   {
      super(faultString);

      Name detailName = faultDetail != null ? faultDetail.getElementName() : null;
      log.fine("new SOAPFaultException [code=" + faultCode + ",string=" + faultString + ",actor=" + faultActor + ",detail=" + detailName + "]");

      this.faultCode = faultCode;
      this.faultString = faultString;
      this.faultActor = faultActor;
      this.faultDetail = faultDetail;
   }

   public QName getFaultCode()
   {
      return faultCode;
   }

   public String getFaultString()
   {
      return faultString;
   }

   public String getFaultActor()
   {
      return faultActor;
   }

   public Detail getDetail()
   {
      return faultDetail;
   }
}
