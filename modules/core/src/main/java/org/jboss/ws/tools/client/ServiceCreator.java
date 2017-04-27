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
package org.jboss.ws.tools.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.jboss.ws.WSException;
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.tools.ToolsUtils;
import org.jboss.ws.tools.interfaces.ServiceCreatorIntf;
import org.jboss.wsf.common.JavaUtils;

/**
 *  Creates the  Service Interface<br>
 *  JBWS-160: Client Side Artifacts Generation<br>
 *  
 *  <br>Note: Web Services Layer<br>
 *  Method to create Service interface is as follows<br>
 *  Constructor to use is:<br>
 *  <br>{@link #ServiceCreator( WSDLDefinitions wsdl, String packageName, File location )  ServiceCreator} 
 * <br>{@link #createServiceDescriptor()  createServiceDescriptor} 
 * 
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jun 22, 2005 
 */

public class ServiceCreator implements ServiceCreatorIntf
{
   /**
    * The Package Name
    */
   protected String packageName = null;

   /**
    * Directory Location where the service file has to be created
    */
   protected File dirLocation = null;

   /**
    * Root object of the WSDL Object Graph
    */
   protected WSDLDefinitions wsdl = null;

   /** Singleton class that handles many utility functions */
   private WSDLUtils utils = WSDLUtils.getInstance();

   /**
    * Constructor
    */
   public ServiceCreator()
   {
   }

   /**
    * Constructor
    * @param serviceName Service Name
    * @param portName Port Name of the endpoint
    * @param packageName Package Name of the Service interface
    */
   public ServiceCreator(WSDLDefinitions wsdl, String packageName)
   {
      this.wsdl = wsdl;
      this.packageName = packageName;
   }

   /**
    * Constructor
    * WebServices Layer uses this
    * @param serviceName Service Name
    * @param portName Port Name of the endpoint
    * @param packageName Package Name of the Service interface
    * @param location Directory Location where the Service File has to be created
    */
   public ServiceCreator(WSDLDefinitions wsdl, String packageName, File location)
   {
      this(wsdl, packageName);
      this.dirLocation = location;
   }

   /**
    * @return Returns the dirLocation.
    */
   public File getDirLocation()
   {
      return dirLocation;
   }

   /**
    * @param dirLocation The dirLocation to set.
    */
   public void setDirLocation(File dirLocation)
   {
      this.dirLocation = dirLocation;
   }

   /**
    * @return Returns the packageName.
    */
   public String getPackageName()
   {
      return packageName;
   }

   /**
    * @param packageName The packageName to set.
    */
   public void setPackageName(String packageName)
   {
      this.packageName = packageName;
   }

   /**
    * @see #wsdl
    * @return
    */
   public WSDLDefinitions getWsdl()
   {
      return wsdl;
   }

   /**
    * @see #wsdl 
    * @param wsdl
    */
   public void setWsdl(WSDLDefinitions wsdl)
   {
      this.wsdl = wsdl;
   }

   /**
    * Create the Service Interface for the endpoint
    * @throws IOException 
    *
    */
   public void createServiceDescriptor() throws IOException
   {
      if (packageName == null)
         throw new WSException("package name is null");
      if (dirLocation == null)
         throw new WSException("dir location  is null");
      if (wsdl == null)
         throw new WSException("wsdl definitions is null");

      WSDLService[] services = wsdl.getServices();

      int len = services != null ? services.length : 0;
      for (int i = 0; i < len; i++)
      {
         generateServiceFile(services[i]);
      }
   }

   //PRIVATE METHODS 
   private void generateHeader(StringBuilder buf)
   {
      buf.append("/*  " + newLine(1));
      buf.append("* JBoss, the OpenSource EJB server" + newLine(1));
      buf.append("* Distributable under LGPL license. See terms of license at gnu.org." + newLine(1));
      buf.append("*/" + newLine(2));
      buf.append("//Auto Generated by jbossws - Please do not edit!!!");
      buf.append(newLine(2));
   }

   private void generatePackageNameAndImport(StringBuilder buf)
   {
      buf.append("package " + packageName + ";" + newLine(3));
      buf.append("import javax.xml.rpc.*; " + newLine(3));
   }

   private String getReturnType(WSDLBinding wbind)
   {
      String portType = wbind.getInterface().getName().getLocalPart();
      portType = utils.chopPortType(portType);

      //Check if it conflicts with a service name
      if (wsdl.getService(portType) != null)
         portType += "_PortType";
      return packageName + "." + JavaUtils.capitalize(portType);
   }

   private void generateServiceFile(WSDLService wsdlService) throws IOException
   {
      String serviceName = wsdlService.getName().getLocalPart();
      if (serviceName.endsWith("Service") == false)
         serviceName = serviceName + "Service";

      //Check if the serviceName conflicts with a portType or interface name
      if (wsdl.getInterface(new QName(wsdl.getTargetNamespace(), serviceName)) != null)
         serviceName = new StringBuilder(serviceName).insert(serviceName.lastIndexOf("Service"), '_').toString();

      serviceName = ToolsUtils.convertInvalidCharacters(serviceName);
      serviceName = JavaUtils.capitalize(serviceName);

      StringBuilder buf = new StringBuilder();
      generateHeader(buf);
      generatePackageNameAndImport(buf);

      buf.append("public interface  " + serviceName + " extends  javax.xml.rpc.Service" + newLine(1));
      buf.append("{" + newLine(2));

      WSDLEndpoint[] endpts = wsdlService.getEndpoints();
      int len = endpts != null ? endpts.length : 0;

      for (int i = 0; i < len; i++)
         buf.append(generateServiceMethodForWSDLEndpoint(endpts[i])).append(newLine(1));

      buf.append("}" + newLine(1));

      File loc = utils.createPackage(dirLocation.getAbsolutePath(), packageName);
      File sei = utils.createPhysicalFile(loc, serviceName);
      FileWriter writer = new FileWriter(sei);
      writer.write(buf.toString());
      writer.flush();
      writer.close();
   }

   public static String removeHyphens(final String component)
   {
      String result = component;
      for (int i = 0; i < result.length(); i++)
      {
         if (result.charAt(i) == '-')
         {
            result = result.replace(result.charAt(i), '_');
         }
      }

      return result;
   }

   private String generateServiceMethodForWSDLEndpoint(WSDLEndpoint endpt)
   {
      StringBuilder buf = new StringBuilder("     public ");
      QName bindName = endpt.getBinding();
      WSDLBinding wbind = wsdl.getBinding(bindName);

      buf.append(removeHyphens(getReturnType(wbind))).append(" get"); 
      buf.append(endpt.getName().getLocalPart()).append("()").append(" throws ServiceException;").append(newLine(1));
      return buf.toString();
   }

   private String newLine(int times)
   {
      String newline = "\n";
      StringBuilder buf = new StringBuilder(newline);
      for (int i = 0; i < times - 1; i++)
         buf.append(newline);
      return buf.toString();
   }
}
