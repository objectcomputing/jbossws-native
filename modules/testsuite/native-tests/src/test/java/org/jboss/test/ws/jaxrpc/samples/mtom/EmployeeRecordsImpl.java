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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.activation.DataHandler;
import javax.xml.transform.stream.StreamSource;

import org.jboss.wsf.common.IOUtils;

/**
 * EmployeeRecords service endpoint
 *
 * @author Heiko.Braun@jboss.org
 */
public class EmployeeRecordsImpl implements EmployeeRecords
{
   public Status updateEmployee(Employee employee) throws RemoteException
   {
      try
      {
         DataHandler dataHandler = employee.getLegacyData();
         String contentType = dataHandler.getContentType();

         // Note: An MTOM request is ambiguous.
         // Some vendors do XOP encoding dpending on a threshold.
         // In that case you might receive an inlined request for
         // an MTOM capable endpoint. The example below shows how deal with it.

         if ("text/xml".equals(contentType))
         {
            StreamSource xmlStream = (StreamSource)dataHandler.getContent();
            IOUtils.copyStream(System.out, xmlStream.getInputStream());
         }
         else if ("application/octet-stream".equals(contentType))
         {
            IOUtils.copyStream(System.out, dataHandler.getInputStream());
         }

         return new Status("OK");
      }
      catch (IOException e)
      {
         throw new RemoteException(e.getMessage());
      }
   }

   public Employee queryEmployee(Query query) throws RemoteException
   {
      Employee employee = new Employee();
      employee.setFirstname("Peter");
      employee.setLastname("Pan");
      employee.setLegacyData(getLegacyData());

      return employee;
   }

   private DataHandler getLegacyData()
   {
      try
      {
         return new DataHandler(new StreamSource(new ByteArrayInputStream("<Payroll><Data/></Payroll>".getBytes())), "application/xml");
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
