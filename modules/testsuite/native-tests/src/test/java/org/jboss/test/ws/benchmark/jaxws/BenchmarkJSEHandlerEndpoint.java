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
package org.jboss.test.ws.benchmark.jaxws;

import org.jboss.test.ws.benchmark.jaxws.doclit.*;

import javax.jws.*;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko.Braun@jboss.org
 * @since 31.01.2007
 */
@WebService(
   name = "BenchmarkService",
   targetNamespace = "http://org.jboss.ws/benchmark",
   endpointInterface = "org.jboss.test.ws.benchmark.jaxws.doclit.BenchmarkService"
)
@HandlerChain(file = "handlers.xml")
public class BenchmarkJSEHandlerEndpoint implements BenchmarkService {

   @WebMethod
   @WebResult(name = "result", targetNamespace = "")
   @RequestWrapper(localName = "echoArrayOfSimpleUserType", targetNamespace = "http://org.jboss.ws/benchmark/types", className = "org.jboss.test.ws.benchmark.jaxws.doclit.EchoArrayOfSimpleUserType")
   @ResponseWrapper(localName = "echoArrayOfSimpleUserTypeResponse", targetNamespace = "http://org.jboss.ws/benchmark/types", className = "org.jboss.test.ws.benchmark.jaxws.doclit.EchoArrayOfSimpleUserTypeResponse")
   public List<SimpleUserType> echoArrayOfSimpleUserType(@WebParam(name = "arrayOfSimpleUserType_1", targetNamespace = "") List<SimpleUserType> arrayOfSimpleUserType1) {
      return arrayOfSimpleUserType1;
   }

   @WebMethod
   @WebResult(name = "result", targetNamespace = "")
   @RequestWrapper(localName = "echoSimpleType", targetNamespace = "http://org.jboss.ws/benchmark/types", className = "org.jboss.test.ws.benchmark.jaxws.doclit.EchoSimpleType")
   @ResponseWrapper(localName = "echoSimpleTypeResponse", targetNamespace = "http://org.jboss.ws/benchmark/types", className = "org.jboss.test.ws.benchmark.jaxws.doclit.EchoSimpleTypeResponse")
   public SimpleUserType echoSimpleType(@WebParam(name = "SimpleUserType_1", targetNamespace = "") SimpleUserType simpleUserType1) {
      return simpleUserType1;
   }

   @WebMethod
   @WebResult(name = "result", targetNamespace = "")
   @RequestWrapper(localName = "echoSynthetic", targetNamespace = "http://org.jboss.ws/benchmark/types", className = "org.jboss.test.ws.benchmark.jaxws.doclit.EchoSynthetic")
   @ResponseWrapper(localName = "echoSyntheticResponse", targetNamespace = "http://org.jboss.ws/benchmark/types", className = "org.jboss.test.ws.benchmark.jaxws.doclit.EchoSyntheticResponse")
   public Synthetic echoSynthetic(@WebParam(name = "Synthetic_1", targetNamespace = "") Synthetic synthetic1) {
      return synthetic1;
   }

   @WebMethod
   @WebResult(name = "result", targetNamespace = "")
   @RequestWrapper(localName = "getOrder", targetNamespace = "http://org.jboss.ws/benchmark/types", className = "org.jboss.test.ws.benchmark.jaxws.doclit.GetOrder")
   @ResponseWrapper(localName = "getOrderResponse", targetNamespace = "http://org.jboss.ws/benchmark/types", className = "org.jboss.test.ws.benchmark.jaxws.doclit.GetOrderResponse")
   public Order getOrder(@WebParam(name = "int_1", targetNamespace = "") int int1, @WebParam(name = "int_2", targetNamespace = "") int int2) {
      return createOrderResponse(int1, int2);
   }

   public Order createOrderResponse(int orderId, int customerId)
   {
      int id = customerId;

      Address ship = new Address();
      ship.setAddress1("Ship StreetAddres " + id);
      ship.setAddress2("Street Address Line 2 " + id);
      ship.setCity("City " + id);
      ship.setFirstName("Ship FirstName " + id);
      ship.setLastName("Ship LastName " + id);
      ship.setState("State " + id);
      ship.setZip("12345");
      Address bill = ship;


      Customer customer = new Customer();
      customer.setBillingAddress(bill);
      customer.setContactFirstName("FirstName " + id);
      customer.setContactLastName("LastName " + id);
      customer.setContactPhone("089452132355");
      customer.setCreditCardExpirationDate("27-12-04");
      customer.setCreditCardNumber("90879876876876");
      customer.setCustomerId(customerId);
      customer.setLastActivityDate(null);
      customer.setShippingAddress(ship);

      int numberLineItems = orderId;

      ArrayList lines = new ArrayList();

      for(int i = 0; i < numberLineItems; i++)
      {
         //  orderId, i+1, i, "Test Product " +i, 1, (float) 1.00
         LineItem line = new LineItem();
         line.setOrderId(orderId);
         line.setOrderQuantity(10+i);
         line.setProductDescription("Test Product " +i);
         line.setProductId(2*i);
         line.setUnitPrice((float) 1.00);
         lines.add(line);
      }


      // orderId, 1, new GregorianCalendar(), (float) 50, customer, (LineItem[])lines.toArray(new LineItem[0])
      Order order = new Order();
      order.setOrderId(orderId);
      order.setOrderDate(null);
      order.setOrderTotalAmount((float) 50);
      order.setCustomer(customer);
      order.getLineItems().addAll(lines);

      return order;
   }

}
