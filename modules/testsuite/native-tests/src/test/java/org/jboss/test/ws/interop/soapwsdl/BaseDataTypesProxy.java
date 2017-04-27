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
package org.jboss.test.ws.interop.soapwsdl;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Adapts a standard SEI to several test services
 * that share the same signature but got different SEI's.
 *
 * @author Heiko Braun <heiko@openj.net>
 * @since 19-02-2006
 */
public class BaseDataTypesProxy implements InvocationHandler {

   private Object obj;

   public static Object newInstance(Object obj) {
      return java.lang.reflect.Proxy.newProxyInstance(
            obj.getClass().getClassLoader(),
            new Class[] {BaseDataTypesSEI.class},
            new BaseDataTypesProxy(obj)
      );
   }

   private BaseDataTypesProxy(Object obj) {
      this.obj = obj;
   }

   public Object invoke(Object proxy, Method m, Object[] args)
         throws Throwable
   {
      Object result = null;
      try {
         for(Method target : obj.getClass().getMethods())
         {
            if(target.getName().equals(m.getName()))
            {
               // it's dirty, but hey...
               result = target.invoke(obj, args);
            }
         }
                  
      } catch (InvocationTargetException e) {
         throw e.getTargetException();
      } catch (Exception e) {
         e.printStackTrace();
         throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
      } finally {
         //
      }
      return result;
   }
}
