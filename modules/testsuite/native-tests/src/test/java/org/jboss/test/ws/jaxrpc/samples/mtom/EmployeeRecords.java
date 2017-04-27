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
package org.jboss.test.ws.jaxrpc.samples.mtom;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Service Endpoint Interface for XOP
 *
 * image/gif         java.awt.Image
 * image/jpeg        java.awt.Image
 * text/plain        java.lang.String
 * multipart/*       javax.mail.internet.MimeMultipart
 * text/xml          javax.xml.transform.Source
 * application/xml   javax.xml.transform.Source
 *
 * @author Thomas.Diesler@jboss.org
 * @since 18-Jan-2006
 */
public interface EmployeeRecords extends Remote
{
   public Status updateEmployee(Employee employee) throws RemoteException;
   public Employee queryEmployee(Query query) throws RemoteException;
}
