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
package org.jboss.test.ws.tools.sei;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import javax.xml.namespace.QName;

/**
 * An SEI for standard java types
 *
 * See jaxrpc-1.1 (5.3.2)
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-Oct-2004
 */
public interface StandardJavaArrayTypes extends Remote
{
   public String[] echoString(String[] v) throws RemoteException;

   public Date[] echoDate(Date[] v) throws RemoteException;

   public Calendar[] echoCalendar(Calendar[] v) throws RemoteException;

   public BigInteger[] echoInteger(BigInteger[] v) throws RemoteException;

   public BigDecimal[] echoDecimal(BigDecimal[] v) throws RemoteException;

   public QName[] echoQName(QName[] v) throws RemoteException;

   public URI[] echoURI(URI[] v) throws RemoteException;
}
