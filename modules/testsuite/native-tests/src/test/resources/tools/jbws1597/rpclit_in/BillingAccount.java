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
 * JBossWS WS-Tools Generated Source
 *
 * Generation Date: Fri Jun 01 17:17:30 CEST 2007
 *
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 */

package org.jboss.test.ws.jbws1597;


public class  BillingAccount
{

protected java.lang.String sortCode;

protected java.lang.String accountNumber;
public BillingAccount(){}

public BillingAccount(java.lang.String sortCode, java.lang.String accountNumber){
this.sortCode=sortCode;
this.accountNumber=accountNumber;
}
public java.lang.String getSortCode() { return sortCode ;}

public void setSortCode(java.lang.String sortCode){ this.sortCode=sortCode; }

public java.lang.String getAccountNumber() { return accountNumber ;}

public void setAccountNumber(java.lang.String accountNumber){ this.accountNumber=accountNumber; }

}
