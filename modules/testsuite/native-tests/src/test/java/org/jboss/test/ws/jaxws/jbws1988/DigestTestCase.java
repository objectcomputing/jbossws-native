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
package org.jboss.test.ws.jaxws.jbws1988;

import org.jboss.ws.extensions.security.operation.SendUsernameOperation;
import org.jboss.wsf.test.JBossWSTest;

/**
 * Black box tests of the username token profile digest algorithm
 *
 * @author alessio.soldano@jboss.com
 * @since 12-Mar-2008
 */
public class DigestTestCase extends JBossWSTest
{
   public void testWithNonceAndCreated() throws Exception
   {
      String password = "taadtaadpstcsm";
      String nonce = "d36e316282959a9ed4c89851497a717f";
      String created = "2003-12-15T14:43:07Z";
      String expectedDigest = "quR/EWLAV4xLf9Zqyw4pDmfV9OY=";
      String digest = SendUsernameOperation.createPasswordDigest(nonce, created, password);
      assertEquals(expectedDigest, digest);
      
      password = "therealfrog";
      nonce = "gHGIdDEWjX1Ay/LiVd3qJ1ua8VbjXis8CJwNDQh1ySA=";
      created = "2008-03-12T17:12:31.310Z";
      expectedDigest = "IEeuDaP/NTozwiyJHzTgBoCCDjg=";
      digest = SendUsernameOperation.createPasswordDigest(nonce, created, password);
      assertEquals(expectedDigest, digest);
   }
   
   public void testWithNonce() throws Exception
   {
      String password = "therealfrog";
      String nonce = "gHGIdDEWjX1Ay/LiVd3qJ1ua8VbjXis8CJwNDQh1ySA=";
      String expectedDigest = "sdA2umjMZQEY2ejbt5L6WbJOrB0=";
      String digest = SendUsernameOperation.createPasswordDigest(nonce, null, password);
      assertEquals(expectedDigest, digest);
   }
   
   public void testWithCreated() throws Exception
   {
      String password = "therealfrog";
      String created = "2008-03-12T17:12:31.310Z";
      String expectedDigest = "fwt4eF/AjmE0mvY1gI4hkAiSIbk=";
      String digest = SendUsernameOperation.createPasswordDigest(null, created, password);
      assertEquals(expectedDigest, digest);
   }
}
