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
package org.jboss.ws.tools.metadata;

import java.rmi.Remote;
import java.util.List;
import java.util.Map;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.tools.Configuration.OperationConfig;
import org.jboss.wsf.common.ResourceLoaderAdapter;

/**
 *  Builder class that builds the Tools Meta Data
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Oct 6, 2005
 */
public class ToolsUnifiedMetaDataBuilder
{
   private static Logger log = Logger.getLogger(ToolsUnifiedMetaDataBuilder.class);
   private Class seiClass;
   private UnifiedMetaData um;

   private String targetNamespace = null;
   private String typeNamespace = null;
   private String serviceName = null;
   private Map<String, List<OperationConfig>> operationMap = null;

   private Style style;
   private ParameterStyle parameterStyle;

   public ToolsUnifiedMetaDataBuilder(Class epClass, String targetNamespace, String typeNamespace, String serviceName, Style style, ParameterStyle parameterStyle,
         Map<String, List<OperationConfig>> operationMap)
   {
      this.seiClass = epClass;
      this.targetNamespace = targetNamespace;
      this.typeNamespace = typeNamespace != null ? typeNamespace : targetNamespace;
      this.serviceName = serviceName;
      this.style = style;
      this.parameterStyle = parameterStyle;
      this.operationMap = operationMap;

      buildMetaData();
   }

   public UnifiedMetaData getUnifiedMetaData()
   {
      return um;
   }

   //PRIVATE METHODS
   private void buildMetaData()
   {
      //Check if it extends Remote Interface
      if (!Remote.class.isAssignableFrom(seiClass))
         throw new WSException("A service endpoint interface should extend Remote");

      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
      ResourceLoaderAdapter vfsRoot = new ResourceLoaderAdapter();
      um = new UnifiedMetaData(vfsRoot);
      um.setClassLoader(contextClassLoader);

      String seiName = WSDLUtils.getInstance().getJustClassName(seiClass.getName());
      if (serviceName == null)
         serviceName = seiName + "Service";

      um.addService(getServiceMetaData(um, targetNamespace, serviceName, seiName, seiClass));

      generateOperationMetaData(seiClass);
   }

   private void generateOperationMetaData(Class seiClass)
   {
      ServiceMetaData sm = um.getServices().get(0);
      ToolsEndpointMetaData em = (ToolsEndpointMetaData)sm.getEndpointByServiceEndpointInterface(seiClass.getName());
      if (em == null)
         throw new WSException("EndpointMetadata is null");
      ReflectiveMetaDataBuilder rmb = new ReflectiveMetaDataBuilder(em);
      rmb.setOperationMap(operationMap);
      em = rmb.generate();
   }

   private ServiceMetaData getServiceMetaData(UnifiedMetaData um, String targetNamespace, String serviceName, String portTypeName, Class seiClass)
   {
      ServiceMetaData sm = new ServiceMetaData(um, new QName(targetNamespace,serviceName));
      QName name = new QName(targetNamespace, portTypeName + "Port");
      QName interfaceName = new QName(targetNamespace, portTypeName);
      ToolsEndpointMetaData tm = new ToolsEndpointMetaData(sm, name, interfaceName);
      tm.typeNamespace = typeNamespace;
      tm.setServiceEndpointInterfaceName(seiClass.getName());
      tm.setStyle(style);
      tm.setParameterStyle(parameterStyle);

      sm.addEndpoint(tm);
      return sm;
   }
}
