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

/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.test.ws.benchmark.jaxrpc;

import java.io.Serializable;
import java.util.Calendar;

/**
 * A Order.
 * 
 * @author <a href="anders.hedstrom@home.se">Anders Hedstrom</a>
 */
public class Order implements Serializable
{

   protected int orderId;
   protected int orderStatus;
   protected Calendar orderDate;
   protected float orderTotalAmount;
   protected Customer customer;
   protected LineItem[] lineItems;
   
   public Order()
   {
   }

   
   /**
    * @param orderId
    * @param orderStatus
    * @param orderDate
    * @param orderTotalAmount
    * @param customer
    * @param lineItems
    */
   public Order(int orderId, int orderStatus, Calendar orderDate,
         float orderTotalAmount, Customer customer, LineItem[] lineItems)
   {
      super();
      this.orderId = orderId;
      this.orderStatus = orderStatus;
      this.orderDate = orderDate;
      this.orderTotalAmount = orderTotalAmount;
      this.customer = customer;
      this.lineItems = lineItems;
   }
   
   
   public Customer getCustomer()
   {
      return customer;
   }
   public void setCustomer(Customer customer)
   {
      this.customer = customer;
   }
   public LineItem[] getLineItems()
   {
      return lineItems;
   }
   public void setLineItems(LineItem[] lineItems)
   {
      this.lineItems = lineItems;
   }
   public Calendar getOrderDate()
   {
      return orderDate;
   }
   public void setOrderDate(Calendar orderDate)
   {
      this.orderDate = orderDate;
   }
   public int getOrderId()
   {
      return orderId;
   }
   public void setOrderId(int orderId)
   {
      this.orderId = orderId;
   }
   public int getOrderStatus()
   {
      return orderStatus;
   }
   public void setOrderStatus(int orderStatus)
   {
      this.orderStatus = orderStatus;
   }
   public float getOrderTotalAmount()
   {
      return orderTotalAmount;
   }
   public void setOrderTotalAmount(float orderTotalAmount)
   {
      this.orderTotalAmount = orderTotalAmount;
   }
}
