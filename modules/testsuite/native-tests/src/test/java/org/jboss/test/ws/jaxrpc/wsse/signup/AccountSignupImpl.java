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
package org.jboss.test.ws.jaxrpc.wsse.signup;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import org.jboss.logging.Logger;

public class AccountSignupImpl implements AccountSignup
{
   private Logger log = Logger.getLogger(AccountSignup.class);

   public int signup(AccountInfo accountInfo, float discountAmount, Date signupTime) throws RemoteException
   {
      String creditCardNumber = accountInfo.getCreditCardInfo().getCreditCardNumber();
      log.info("Credit card number = " + creditCardNumber);

      Date expiration = accountInfo.getCreditCardInfo().getExpiration();
      log.info("Credit card expiration = " + expiration);

      String securityCode = accountInfo.getCreditCardInfo().getSecurityCode();
      log.info("Credit card security code = " + securityCode);

      if (! "1234-1234-1234-1234".equals(creditCardNumber))
         throw new RemoteException("Invalid credit card number");

      Calendar cal = Calendar.getInstance();
      cal.clear();
      cal.set(2005, 11, 1, 0, 0);

      if (! expiration.equals(cal.getTime()))
         throw new RemoteException("Invalid expiration date");

      if (! securityCode.equals("123"))
         throw new RemoteException("Invalid security code");

      // We pretend that we signed up the account
      return 345;
   }
}
