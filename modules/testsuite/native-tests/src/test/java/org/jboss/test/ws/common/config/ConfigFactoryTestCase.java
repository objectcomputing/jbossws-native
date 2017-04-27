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
package org.jboss.test.ws.common.config;

import java.io.File;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.ws.extensions.wsrm.config.RMBackPortsServerConfig;
import org.jboss.ws.extensions.wsrm.config.RMConfig;
import org.jboss.ws.extensions.wsrm.config.RMDeliveryAssuranceConfig;
import org.jboss.ws.extensions.wsrm.config.RMMessageRetransmissionConfig;
import org.jboss.ws.extensions.wsrm.config.RMPortConfig;
import org.jboss.ws.metadata.config.EndpointProperty;
import org.jboss.ws.metadata.config.JBossWSConfigFactory;
import org.jboss.ws.metadata.config.jaxrpc.CommonConfigJAXRPC;
import org.jboss.ws.metadata.config.jaxrpc.ConfigRootJAXRPC;
import org.jboss.ws.metadata.config.jaxrpc.EndpointConfigJAXRPC;
import org.jboss.ws.metadata.config.jaxws.ConfigRootJAXWS;
import org.jboss.ws.metadata.config.jaxws.EndpointConfigJAXWS;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerChainMetaData;
import org.jboss.wsf.spi.metadata.j2ee.serviceref.UnifiedHandlerMetaData;
import org.jboss.wsf.test.JBossWSTest;

/**
 * Test parsing of the JBossWS config
 *
 * @author Thomas.Diesler@jboss.org
 * @since 21-Dec-2004
 */
public class ConfigFactoryTestCase extends JBossWSTest
{

   public void testJAXRPCObjectModelFactory() throws Exception
   {
      File confFile = getResourceFile("common/config/jaxrpc-endpoint-config.xml");
      assertTrue(confFile.exists());

      JBossWSConfigFactory factory = JBossWSConfigFactory.newInstance();
      ConfigRootJAXRPC config = (ConfigRootJAXRPC)factory.parse(confFile.toURL());

      assertNotNull("Null config", config);

      assertEquals(3, config.getEndpointConfig().size());
      EndpointConfigJAXRPC epc1 = (EndpointConfigJAXRPC)config.getEndpointConfig().get(0);
      EndpointConfigJAXRPC epc2 = (EndpointConfigJAXRPC)config.getEndpointConfig().get(2);

      assertEquals("Standard Endpoint", epc1.getConfigName());
      assertNull(epc1.getPreHandlerChain());
      assertNull(epc1.getPostHandlerChain());
      assertFalse("MTOM should not be enabled", epc1.hasFeature("http://org.jboss.ws/mtom"));

      assertEquals("Standard WSSecurity Endpoint", epc2.getConfigName());
      UnifiedHandlerChainMetaData preChain = epc2.getPreHandlerChain();
      assertEquals(1, preChain.getHandlers().size());
      UnifiedHandlerMetaData h1 = (UnifiedHandlerMetaData)preChain.getHandlers().get(0);
      assertEquals("WSSecurityHandlerInbound", h1.getHandlerName());
      assertEquals("org.jboss.ws.extensions.security.jaxrpc.WSSecurityHandlerInbound", h1.getHandlerClass());
   }

   public void testJAXWSFeatures() throws Exception
   {
      File confFile = getResourceFile("common/config/jaxws-endpoint-config.xml");
      assertTrue(confFile.exists());
      JBossWSConfigFactory factory = JBossWSConfigFactory.newInstance();

      ConfigRootJAXWS config = (ConfigRootJAXWS)factory.parse(confFile.toURL());
      assertNotNull("Null config", config);

      EndpointConfigJAXWS epConfig = (EndpointConfigJAXWS)config.getConfigByName("Standard MTOM Endpoint");
      assertTrue("Feature not set", epConfig.hasFeature("http://org.jboss.ws/mtom"));

      // disable feature
      epConfig.setFeature("http://org.jboss.ws/mtom", false);
      assertFalse("Feature still set", epConfig.hasFeature("http://org.jboss.ws/mtom"));
   }

   public void testJAXRPCFeatures() throws Exception
   {
      File confFile = getResourceFile("common/config/jaxrpc-endpoint-config.xml");
      assertTrue(confFile.exists());
      JBossWSConfigFactory factory = JBossWSConfigFactory.newInstance();
      ConfigRootJAXRPC config = (ConfigRootJAXRPC)factory.parse(confFile.toURL());
      assertNotNull("Null config", config);

      CommonConfigJAXRPC epConfig = (CommonConfigJAXRPC)config.getConfigByName("Standard MTOM Endpoint");
      assertTrue("Feature not set", epConfig.hasFeature("http://org.jboss.ws/mtom"));

      // disable feature
      epConfig.setFeature("http://org.jboss.ws/mtom", false);
      assertFalse("Feature still set", epConfig.hasFeature("http://org.jboss.ws/mtom"));
   }

   public void testProperties() throws Exception
   {
      File confFile = getResourceFile("common/config/jaxws-endpoint-config.xml");
      assertTrue(confFile.exists());

      JBossWSConfigFactory factory = JBossWSConfigFactory.newInstance();
      ConfigRootJAXWS config = (ConfigRootJAXWS)factory.parse(confFile.toURL());
      assertNotNull("Null config", config);

      EndpointConfigJAXWS epConfig = (EndpointConfigJAXWS)config.getConfigByName("Standard WSSecurity Endpoint");
      String value = epConfig.getProperty(EndpointProperty.MTOM_THRESHOLD);
      assertNotNull("Property does not exist", value);
      assertEquals("Wrong property valule", value, "5000");
   }

   public void testWSRMConfiguration() throws Exception
   {
      File confFile = getResourceFile("common/config/jaxws-endpoint-config.xml");
      assertTrue(confFile.exists());

      JBossWSConfigFactory factory = JBossWSConfigFactory.newInstance();
      ConfigRootJAXWS config = (ConfigRootJAXWS)factory.parse(confFile.toURL());
      EndpointConfigJAXWS epConfig = (EndpointConfigJAXWS)config.getConfigByName("Standard WSRM Endpoint");
      RMConfig wsrmConfig = epConfig.getRMMetaData();
      assertNotNull(wsrmConfig);
      RMDeliveryAssuranceConfig deliveryAssurance = wsrmConfig.getDeliveryAssurance();
      assertEquals(deliveryAssurance.getInOrder(), "true");
      assertEquals(deliveryAssurance.getQuality(), "AtLeastOnce");
      List<RMPortConfig> ports = wsrmConfig.getPorts();
      RMPortConfig port1 = ports.get(0);
      assertEquals(port1.getPortName(), new QName("http://custom/namespace/", "Port1"));
      assertEquals(port1.getDeliveryAssurance().getInOrder(), "false");
      assertEquals(port1.getDeliveryAssurance().getQuality(), "AtMostOnce");
      RMPortConfig port2 = ports.get(1);
      assertEquals(port2.getPortName(), new QName("http://custom/namespace/", "Port2"));
      assertEquals(port2.getDeliveryAssurance().getInOrder(), "true");
      assertEquals(port2.getDeliveryAssurance().getQuality(), "ExactlyOnce");
      RMBackPortsServerConfig backportsServer = wsrmConfig.getBackPortsServer();
      assertEquals(backportsServer.getHost(), "realhostname.realdomain");
      assertEquals(backportsServer.getPort(), "9999");
      RMMessageRetransmissionConfig messageRetransmission = wsrmConfig.getMessageRetransmission();
      assertEquals(messageRetransmission.getCountOfAttempts(), 50);
      assertEquals(messageRetransmission.getRetransmissionInterval(), 10);
      assertEquals(messageRetransmission.getMessageTimeout(), 3);
   }
}
