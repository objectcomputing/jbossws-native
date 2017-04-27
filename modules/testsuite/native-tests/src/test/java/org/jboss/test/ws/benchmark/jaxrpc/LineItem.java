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

/**
 * A LineItem.
 * 
 * @author <a href="anders.hedstrom@home.se">Anders Hedstrom</a>
 */
public class LineItem {
    protected int orderId;
    protected int itemId;
    protected int productId;
    protected String productDescription;
    protected int orderQuantity;
    protected float unitPrice;
    
    public LineItem() {
    }
    
    public LineItem(int orderId, int itemId, int productId, String productDescription, int orderQuantity, float unitPrice) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.productId = productId;
        this.productDescription = productDescription;
        this.orderQuantity = orderQuantity;
        this.unitPrice = unitPrice;
    }
    
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public int getItemId() {
        return itemId;
    }
    
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public String getProductDescription() {
        return productDescription;
    }
    
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    
    public int getOrderQuantity() {
        return orderQuantity;
    }
    
    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }
    
    public float getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }
}
