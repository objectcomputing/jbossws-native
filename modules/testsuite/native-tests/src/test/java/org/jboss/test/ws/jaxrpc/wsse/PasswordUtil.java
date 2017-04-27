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
package org.jboss.test.ws.jaxrpc.wsse;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * This is a simple decode utility for using along with the jboss-ws-security keystore and truststore use cases
 *
 * @author <a href="mailto:magesh.bojan@jboss.com">Magesh Kumar B</a>
 */
public class PasswordUtil
{
   public static void main(String args[])
   {
     if( args.length != 1 )
      {
         System.err.println(
            "Read a password in plain text form from a password file"
           +"Usage: PasswordUtil password-file"
           +"  password-file : the path to the file to write the password to"
         );
      }
      try
      {
         System.out.println(decode(args[0]));
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private static char[] decode(String passwordFilePath) throws Exception
   {
      RandomAccessFile passwordFile = new RandomAccessFile(passwordFilePath, "rws");
      byte[] salt = new byte[8];
      passwordFile.readFully(salt);
      int count = passwordFile.readInt();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int b;
      while( (b = passwordFile.read()) >= 0 )
         baos.write(b);
      passwordFile.close();
      byte[] secret = baos.toByteArray();

      PBEParameterSpec cipherSpec = new PBEParameterSpec(salt, count);
      PBEKeySpec keySpec = new PBEKeySpec("78aac249a60a13d5e882927928043ebb".toCharArray());
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEwithMD5andDES");
      SecretKey cipherKey = factory.generateSecret(keySpec);
      Cipher cipher = Cipher.getInstance("PBEwithMD5andDES");
      cipher.init(Cipher.DECRYPT_MODE, cipherKey, cipherSpec);
      byte[] decode = cipher.doFinal(secret);
      return new String(decode, "UTF-8").toCharArray();
   }
}
