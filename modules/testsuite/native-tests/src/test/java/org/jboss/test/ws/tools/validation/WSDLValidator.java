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
package org.jboss.test.ws.tools.validation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperation;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperation;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.metadata.wsdl.WSDLTypes;
import org.jboss.ws.tools.exceptions.JBossWSToolsException;

/**
 * WSDL Validator
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jul 22, 2005
 */
public  class WSDLValidator
{
   private static Logger log = Logger.getLogger(WSDLValidator.class);
   private Class seiClass;
   private WSDLDefinitions wsdl;

   private ArrayList errorList = new ArrayList();

   /**
    * Obtain a list of error messages in String format
    * @return
    */
   public List getErrorList()
   {
      return errorList;
   }

   /**
    * Validate a Java SEI against a WSDL
    * @param seiClass
    * @param wsdl
    * @return Valid wsdl??
    */
   public boolean validate(Class seiClass, WSDLDefinitions wsdl)
   {
      this.seiClass = seiClass;
      this.wsdl = wsdl;

      errorList.clear();

      validateDefinitions();
      try
      {
         validateTypes();
      }
      catch (IOException e)
      {
         log.error("Error validating types:",e);
      }
      validateInterfaces();
      validateBindings();
      validateServices();

      return errorList.size() == 0;
   }

   /**
    * Validates that two wsdl files represented by their WSDLDefinition objects
    * are semantically equivalent
    * @param wsdlExp
    * @param wsdlAct
    * @return true - if wsdl files are sematically equivalent, false - otherwise
    */
   public boolean validate(WSDLDefinitions wsdlExp, WSDLDefinitions wsdlAct)
   throws JBossWSToolsException
   {
      boolean bool = false;
      bool =  WSDLValidationHelper.validateNSDefinitions(wsdlExp,wsdlAct);
      if ( bool )
         bool = WSDLValidationHelper.validateExtensibilityElements(wsdlExp,wsdlAct);
      if( bool)
         bool = WSDLValidationHelper.validateBindings(wsdlExp,wsdlAct);
      if( bool)
        bool = WSDLValidationHelper.validateInterfaces(wsdlExp,wsdlAct);
      if( bool)
        bool = WSDLValidationHelper.validateServices(wsdlExp,wsdlAct);

      return bool;
   }

   //**********************************************************************
   //                       PRIVATE METHODS
   //**********************************************************************
   private void validateDefinitions()
   {
      String targetNS = wsdl.getTargetNamespace();
      if (targetNS == null)
         errorList.add("targetNamespace cannot be null");
   }

   private void validateTypes() throws IOException
   {
      WSDLTypes types = wsdl.getWsdlTypes();

      String targetNS = wsdl.getTargetNamespace();
      if (types == null)
         errorList.add("types cannot be null");
   }

   private void validateInterfaces()
   {
      WSDLInterface[] interfaces = wsdl.getInterfaces();
      if (interfaces == null || interfaces.length == 0)
         errorList.add("interfaces cannot be null");

      for (int i = 0; i < interfaces.length; i++)
      {
         WSDLInterface inf = interfaces[i];
         if (inf.getName() == null)
            errorList.add("interface name cannot be null");

         validateInterfaceOperations(inf);
      }
   }

   private void validateInterfaceOperations(WSDLInterface inf)
   {
      if (inf == null)
      {
         errorList.add("Interface is Null");
         return;
      }
      WSDLInterfaceOperation[] operations = inf.getOperations();
      if (operations == null || operations.length == 0)
      {
         errorList.add("bindings cannot be null");
         return;
      }
      int len = operations.length;
      for (int i = 0; i < len; i++)
      {
         WSDLInterfaceOperation op = operations[i];
         if (op == null || op.getName() == null)
            errorList.add("operation name cannot be null");

         boolean found = false;
         Method[] methods = seiClass.getMethods();

         int lenm = methods.length;

         for (int j = 0; j < lenm; j++)
         {
            Method method = methods[j];

            if (op != null && op.getName() != null &&
               method.getName().equals(op.getName().getLocalPart()))
            {
               if (found)
               {
                  errorList.add("duplicate method: " + method);
               }
               found = true;
            }
         }

         if (found == false)
            errorList.add("method not found for: " + op.getName());

      }
   }

   private void validateBindings()
   {
      WSDLBinding[] bindings = wsdl.getBindings();
      if (bindings == null || bindings.length == 0)
      {
         errorList.add("bindings cannot be null");
         return;
      }

      for (int i = 0; i < bindings.length; i++)
      {
         WSDLBinding binding = bindings[i];
         validateBindingOperations(binding);
      }
   }

   private void validateBindingOperations(WSDLBinding binding)
   {
      WSDLBindingOperation[] operations = binding.getOperations();
      for (int i = 0; i < operations.length; i++)
      {
         WSDLBindingOperation op = operations[i];

         boolean found = false;
         Method[] methods = seiClass.getMethods();
         for (int j = 0; j < methods.length; j++)
         {
            Method method = methods[j];
            if (method.getName().equals(op.getRef().getLocalPart()))
            {
               if (found)
               {
                  errorList.add("duplicate method: " + method);
               }
               found = true;
            }
         }

         if (found == false)
            errorList.add("method not found for: " + op.getRef());
      }
   }

   private void validateServices()
   {
      WSDLService[] services = wsdl.getServices();
      if (services == null || services.length == 0)
         errorList.add("services cannot be null");

      for (int i = 0; i < services.length; i++)
      {
         WSDLService service = services[i];

         WSDLEndpoint[] endpoints = service.getEndpoints();
         if (endpoints == null || endpoints.length == 0)
            errorList.add("endpoints cannot be null");

         for (int j = 0; j < endpoints.length; j++)
         {
            WSDLEndpoint endpoint = endpoints[j];
            if (endpoint.getName() == null)
               errorList.add("endpoint name cannot be null");
            if (endpoint.getBinding() == null)
               errorList.add("endpoint binding cannot be null");
         }
      }
   }

}

