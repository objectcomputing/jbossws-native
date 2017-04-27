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
package org.jboss.ws.core.jaxrpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.StubExt;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.wsf.common.JavaUtils;

/**
 * The dynamic proxy that delegates to the underlying Call implementation
 *
 * @author Thomas.Diesler@jboss.org
 * @since 07-Jan-2005
 */
public class PortProxy implements InvocationHandler
{
   // provide logging
   private static final Logger log = Logger.getLogger(PortProxy.class);
   
   // The underlying Call
   private CallImpl call;
   // List<Method> of the Stub methods
   private List stubMethods;
   // List<Method> of the Object methods
   private List objectMethods;

   // The set of standard properties
   private static final Set<String> standardProperties = new HashSet<String>();
   static
   {
      standardProperties.add(Stub.ENDPOINT_ADDRESS_PROPERTY);
      standardProperties.add(Stub.SESSION_MAINTAIN_PROPERTY);
      standardProperties.add(Stub.USERNAME_PROPERTY);
      standardProperties.add(Stub.PASSWORD_PROPERTY);
   }
   
   // The map of jboss-ws4ee supported properties
   private static final Map<String,String> legacyPropertyMap = new HashMap<String,String>();
   static
   {
      legacyPropertyMap.put("org.jboss.webservice.client.timeout", StubExt.PROPERTY_CLIENT_TIMEOUT);
      legacyPropertyMap.put("org.jboss.webservice.keyAlias", StubExt.PROPERTY_KEY_ALIAS);
      legacyPropertyMap.put("org.jboss.webservice.keyStore", StubExt.PROPERTY_KEY_STORE);
      legacyPropertyMap.put("org.jboss.webservice.keyStoreAlgorithm", StubExt.PROPERTY_KEY_STORE_ALGORITHM);
      legacyPropertyMap.put("org.jboss.webservice.keyStorePassword", StubExt.PROPERTY_KEY_STORE_PASSWORD);
      legacyPropertyMap.put("org.jboss.webservice.keyStoreType", StubExt.PROPERTY_KEY_STORE_TYPE);
      legacyPropertyMap.put("org.jboss.webservice.sslProtocol", StubExt.PROPERTY_SSL_PROTOCOL);
      legacyPropertyMap.put("org.jboss.webservice.sslProviderName", StubExt.PROPERTY_SSL_PROVIDER_NAME);
      legacyPropertyMap.put("org.jboss.webservice.trustStore", StubExt.PROPERTY_TRUST_STORE);
      legacyPropertyMap.put("org.jboss.webservice.trustStoreAlgorithm", StubExt.PROPERTY_TRUST_STORE_ALGORITHM);
      legacyPropertyMap.put("org.jboss.webservice.trustStorePassword", StubExt.PROPERTY_TRUST_STORE_PASSWORD);
      legacyPropertyMap.put("org.jboss.webservice.trustStoreType", StubExt.PROPERTY_TRUST_STORE_TYPE);
   }
   
   public PortProxy(CallImpl call)
   {
      this.call = call;
      this.stubMethods = new ArrayList(Arrays.asList(StubExt.class.getMethods()));
      this.stubMethods.addAll(Arrays.asList(Stub.class.getMethods()));
      this.objectMethods = Arrays.asList(Object.class.getMethods());
   }

   /** Processes a method invocation on a proxy instance and returns the result.
    */
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
   {
      // An invocation on the Stub interface
      String methodName = method.getName();
      if (stubMethods.contains(method))
      {
         if (methodName.equals("_getPropertyNames"))
         {
            return call.getPropertyNames();
         }
         else if (methodName.equals("_getProperty"))
         {
            return getProperty((String)args[0]);
         }
         else if (methodName.equals("_setProperty"))
         {
            setProperty((String)args[0], args[1]);
            return null;
         }
         else
         {
            Method callMethod = CallImpl.class.getMethod(methodName, method.getParameterTypes());
            return callMethod.invoke(call, args);
         }
      }

      // An invocation on proxy's Object class
      else if (objectMethods.contains(method))
      {
         Method callMethod = CallImpl.class.getMethod(methodName, method.getParameterTypes());
         return callMethod.invoke(call, args);
      }

      // An invocation on the service endpoint interface
      else
      {
         EndpointMetaData epMetaData = call.getEndpointMetaData();
         OperationMetaData opMetaData = epMetaData.getOperation(method);
         if (opMetaData == null)
            throw new WSException("Cannot obtain operation meta data for: " + methodName);

         call.setOperationName(opMetaData.getQName());

         try
         {
            if (opMetaData.isOneWay())
            {
               call.invokeOneWay(args);
               return null;
            }
            else
            {
               Object retObj = call.invoke(args);
               if (retObj != null)
               {
                  Class retType = method.getReturnType();
                  if (retType == null)
                     throw new WSException("Return value not supported by: " + opMetaData);

                  if (JavaUtils.isPrimitive(retType))
                     retObj = JavaUtils.getPrimitiveValueArray(retObj);
               }
               return retObj;
            }
         }
         catch (Exception ex)
         {
            handleException(ex);
            return null;
         }
      }
   }

   private Object getProperty(String name)
   {
      name = assertPropertyName(name);
      return call.getProperty(name);
   }

   private void setProperty(String name, Object value)
   {
      name = assertPropertyName(name);
      call.setProperty(name, value);
   }

   private String assertPropertyName(String name)
   {
      if (name != null && name.startsWith("javax.xml.rpc") && standardProperties.contains(name) == false)
         throw new JAXRPCException("Unsupported property: " + name);
      
      if (legacyPropertyMap.keySet().contains(name))
      {
         String jbosswsName = legacyPropertyMap.get(name);
         log.warn("Legacy propery '" + name + "' mapped to '" + jbosswsName + "'");
         name = jbosswsName;
      }

      return name;
   }

   private void handleException(Exception ex) throws Throwable
   {
      Throwable th = ex;
      if (ex instanceof RemoteException && ex.getCause() instanceof SOAPFaultException)
      {
         SOAPFaultException faultEx = (SOAPFaultException)ex.getCause();
         if (faultEx.getCause() != null)
            th = faultEx.getCause();
      }
      throw th;
   }
}
