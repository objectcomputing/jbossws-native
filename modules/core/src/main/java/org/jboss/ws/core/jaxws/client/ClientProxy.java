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
package org.jboss.ws.core.jaxws.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.WSException;
import org.jboss.ws.core.StubExt;
import org.jboss.ws.extensions.wsrm.api.RMProvider;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.wsf.common.JavaUtils;

/**
 * The dynamic proxy that delegates to the underlying client implementation
 *
 * @author Thomas.Diesler@jboss.org
 * @since 04-Jul-2006
 */
public class ClientProxy implements InvocationHandler
{
   // provide logging
   private static final Logger log = Logger.getLogger(ClientProxy.class);

   // The underlying Call
   private ClientImpl client;
   // List<Method> of the Stub methods
   private List stubMethods;
   // List<Method> of the Object methods
   private List objectMethods;

   // The service configured executor
   private ExecutorService executor;

   // The set of standard properties
   private static final Set<String> standardProperties = new HashSet<String>();
   static
   {
      standardProperties.add(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
      standardProperties.add(BindingProvider.SESSION_MAINTAIN_PROPERTY);
      standardProperties.add(BindingProvider.USERNAME_PROPERTY);
      standardProperties.add(BindingProvider.PASSWORD_PROPERTY);
      standardProperties.add(BindingProvider.SOAPACTION_USE_PROPERTY);
      standardProperties.add(BindingProvider.SOAPACTION_URI_PROPERTY);
   }

   public ClientProxy(ExecutorService executor, ClientImpl client)
   {
      this.client = client;
      this.executor = executor;
      this.stubMethods = new ArrayList(Arrays.asList(BindingProvider.class.getMethods()));
      this.stubMethods.addAll(Arrays.asList(StubExt.class.getMethods()));
      this.stubMethods.addAll(Arrays.asList(RMProvider.class.getMethods()));
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
         Method stubMethod = ClientImpl.class.getMethod(methodName, method.getParameterTypes());
         return stubMethod.invoke(client, args);
      }

      // An invocation on proxy's Object class
      else if (objectMethods.contains(method))
      {
         Method objMethod = ClientImpl.class.getMethod(methodName, method.getParameterTypes());
         return objMethod.invoke(client, args);
      }

      // An invocation on the service endpoint interface
      else
      {
         EndpointMetaData epMetaData = client.getEndpointMetaData();
         OperationMetaData opMetaData = epMetaData.getOperation(method);
         if (opMetaData == null)
            throw new WSException("Cannot obtain operation meta data for: " + methodName);

         QName opName = opMetaData.getQName();

         if (log.isTraceEnabled())
            log.trace("Invoke method: " + method + opMetaData);

         try
         {
            Object retObj;
            Class retType = method.getReturnType();
            boolean isAsync = methodName.endsWith(Constants.ASYNC_METHOD_SUFFIX);

            // Invoke asynchronously
            if (isAsync && JavaUtils.isAssignableFrom(Response.class, retType))
            {
               retObj = invokeAsync(opName, args, retType);
            }
            // Invoke asynchronously with handler
            else if (isAsync && JavaUtils.isAssignableFrom(Future.class, retType) && args.length > 1)
            {
               Object handler = args[args.length - 1];
               retObj = invokeAsync(opName, args, retType, (AsyncHandler)handler);
            }
            // Invoke synchronously
            else
            {
               Map<String, Object> resContext = client.getBindingProvider().getResponseContext();
               retObj = invoke(opName, args, retType, resContext);
            }
            return retObj;
         }
         catch (Exception ex)
         {
            handleException(ex);
            return null;
         }
      }
   }

   private Object invoke(QName opName, Object[] args, Class retType, Map<String, Object> resContext) throws RemoteException
   {      
      boolean rmDetected = this.client.getEndpointConfigMetaData().getConfig().getRMMetaData() != null;
      boolean rmActivated = client.getWSRMSequence() != null;
      if (rmDetected && !rmActivated)
      {
         client.createSequence();
      }
      Object retObj = client.invoke(opName, args, resContext);
      if (retObj != null)
      {
         if (retType == null)
            throw new WSException("Return value not supported by: " + opName);

         if (JavaUtils.isPrimitive(retType))
            retObj = JavaUtils.getPrimitiveValueArray(retObj);
      }
      return retObj;
   }

   private Response invokeAsync(QName opName, Object[] args, Class retType)
   {
      ResponseImpl response = new ResponseImpl();
      Runnable task = new AsyncRunnable(response, null, opName, args, retType);

      if(log.isDebugEnabled()) log.debug("Schedule task " + ((AsyncRunnable)task).getTaskID().toString());

      Future future = executor.submit(task);
      response.setFuture(future);
      return response;
   }

   private Future invokeAsync(QName opName, Object[] args, Class retType, AsyncHandler handler)
   {
      ResponseImpl response = new ResponseImpl();
      Runnable task = new AsyncRunnable(response, handler, opName, args, retType);
      Future future = executor.submit(task);
      response.setFuture(future);
      return response;
   }

   /**
    * 4.2.4  Conformance (Remote Exceptions): If an error occurs during a remote operation invocation, an implemention
    * MUST throw a service specific exception if possible. If the error cannot be mapped to a service
    * specific exception, an implementation MUST throw a ProtocolException or one of its subclasses, as
    * appropriate for the binding in use. See section 6.4.1 for more details.
    */
   private void handleException(Exception ex) throws Throwable
   {
      if (ex instanceof SOAPFaultException)
      {
         // Unwrap the cause if it is an Application Exception, otherwise use a protocol exception
         Throwable cause = ex.getCause();
         if (cause instanceof Exception)
         {
            // Throw unwrapped WebServiceException
            if (cause instanceof WebServiceException)
               throw (WebServiceException)cause;

            // Throw wrapped SOAPException
            if (cause instanceof SOAPException)
               throw (SOAPFaultException)ex;

            // Throw wrapped RuntimeException
            if (cause instanceof RuntimeException)
               throw (SOAPFaultException)ex;

            // Throw all other causes
            throw (Exception)cause;
         }
      }
      throw ex;
   }

   class AsyncRunnable implements Runnable
   {
      private ResponseImpl response;
      private AsyncHandler handler;
      private QName opName;
      private Object[] args;
      private Class retType;
      private UUID uuid;

      public AsyncRunnable(ResponseImpl response, AsyncHandler handler, QName opName, Object[] args, Class retType)
      {
         this.response = response;
         this.handler = handler;
         this.opName = opName;
         this.args = args;
         this.retType = retType;
         this.uuid = UUID.randomUUID();
      }

      public void run()
      {
         try
         {
            Map<String, Object> resContext = response.getContext();
            Object result = invoke(opName, args, retType, resContext);

            if(log.isDebugEnabled()) log.debug("Finished task " + getTaskID().toString()+": " + result);

            response.set(result);

            // Call the handler if available
            if (handler != null)
               handler.handleResponse(response);
         }
         catch (Exception ex)
         {
            handleAsynInvokeException(ex);
         }
      }

      // 4.18 Conformance (Failed Dispatch.invokeAsync): When an operation is invoked using an invokeAsync
      // method, an implementation MUST throw a WebServiceException if there is any error in the configuration 
      // of the Dispatch instance. Errors that occur during the invocation are reported when the client
      // attempts to retrieve the results of the operation.
      private void handleAsynInvokeException(Exception ex)
      {
         String msg = "Cannot dispatch message";
         log.error(msg, ex);

         WebServiceException wsex;
         if (ex instanceof WebServiceException)
         {
            wsex = (WebServiceException)ex;
         }
         else
         {
            wsex = new WebServiceException(msg, ex);
         }
         response.setException(wsex);
      }

      public UUID getTaskID() {
         return uuid;
      }
   }
}
