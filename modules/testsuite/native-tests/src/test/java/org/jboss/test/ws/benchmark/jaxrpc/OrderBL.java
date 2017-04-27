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

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * A OrderBL.
 * 
 * @author <a href="anders.hedstrom@home.se">Anders Hedstrom</a>
 */
public class OrderBL {
   
    public OrderBL() {
	}

	public Order getOrder(int orderId, int customerId)
	{
		int id = 1;
		Address ship = new Address("Ship FirstName " + id, "Ship LastName " + id, "Ship StreetAddres " + id, "Street Address Line 2 " + id, "City " + id, "State " + id, "12345");
		Address bill = new Address("Bill FirstName " + id, "Bil1 LastName " + id, "Bill StreetAddres " + id, "Street Address Line 2 " + id, "City " + id, "State " + id, "12345");

		Customer customer = new Customer(customerId, "FirstName " + id, "LastName " + id, Integer.toString(id), new GregorianCalendar(), Integer.toString(id), Integer.toString(id), bill, ship);

		int numberLineItems = orderId;

		ArrayList lines = new ArrayList();

		for(int i = 0; i < numberLineItems; i++)
		{
			LineItem line = new LineItem(orderId, i+1, i, "Test Product " +i, 1, (float) 1.00);

			lines.add(line);
		}

		
		Order order = new Order(orderId, 1, new GregorianCalendar(), (float) 50, customer, (LineItem[])lines.toArray(new LineItem[0]) );

		return order;
	}
}
