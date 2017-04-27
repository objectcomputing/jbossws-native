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
package org.jboss.test.ws.interop.soapwsdl.basedoclitb;

import org.jboss.test.ws.interop.soapwsdl.BaseDataTypesSupport;
import org.jboss.test.ws.interop.soapwsdl.BaseDataTypesSEI;
import org.jboss.test.ws.interop.soapwsdl.BaseDataTypesProxy;
import org.jboss.wsf.test.JBossWSTestSetup;

import junit.framework.Test;

import javax.xml.ws.Service;
import javax.xml.ws.BindingProvider;
import javax.xml.namespace.QName;
import java.net.URL;
import java.io.File;


/**
 * @author Heiko Braun, <heiko@openj.net>
 * @since 17-Feb-2006
 */
public class BaseDataTypesDocLitBTestCase extends BaseDataTypesSupport {

   static IBaseDataTypesDocLitB targetPort;
   static BaseDataTypesSEI proxy;

   public static Test suite()
   {
      return new JBossWSTestSetup(BaseDataTypesDocLitBTestCase.class, "jbossws-interop-BaseDataTypesDocLitB.war");
   }

    protected void setUp() throws Exception
   {
      super.setUp();

      if (targetPort == null)
      {
         URL wsdlLocation = getResourceURL("interop/soapwsdl/BaseDataTypesDocLitB/WEB-INF/wsdl/service.wsdl");
         Service service = Service.create(wsdlLocation, new QName("http://tempuri.org/", "BaseDataTypesDocLitBService") );
         targetPort = service.getPort(IBaseDataTypesDocLitB.class);
         ((BindingProvider)targetPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
            "http://"+getServerHost()+":8080/basedoclitb/endpoint");
         proxy = (BaseDataTypesSEI)BaseDataTypesProxy.newInstance(targetPort);
      }
   }

   protected BaseDataTypesSEI getTargetPort() throws Exception {
      return this.proxy;
   }
}
