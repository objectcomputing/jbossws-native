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
package org.jboss.test.ws.jaxrpc.jbws84;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.xml.soap.SOAPElement;

/**
 * @author Thomas.Diesler@jboss.org
 * @since 26-Nov-2004
 */
public interface Message extends Remote
{
   static final String NAMESPACE_URI = "http://org.jboss.test.webservice/jbws84";
   static final String PREFIX = "ns1";

   String request =
           "<ns1:Order xmlns:ns1='http://org.jboss.test.webservice/jbws84'>" +
           "  <Customer>Kermit</Customer>" +
           "  <Item>Ferrari</Item>" +
           "</ns1:Order>";

   String response =
           "<ns1:Response xmlns:ns1='http://org.jboss.test.webservice/jbws84'>" +
           "  <POID>12345</POID>" +
           "  <Status>ok</Status>" +
           "</ns1:Response>";

   public SOAPElement processSOAPElement(SOAPElement msg) throws RemoteException;
}
