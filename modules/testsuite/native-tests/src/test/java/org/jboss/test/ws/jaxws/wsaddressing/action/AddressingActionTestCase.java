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
package org.jboss.test.ws.jaxws.wsaddressing.action;

import junit.framework.Test;
import org.jboss.ws.extensions.addressing.jaxws.WSAddressingClientHandler;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Test endpoint using ws-addressing.
 *
 * The wsa:Action should override any other dispatch method
 *
 * @author Thomas.Diesler@jboss.org
 * @since 29-Nov-2005
 */
public class AddressingActionTestCase extends JBossWSTest
{
	private static ActionEndpoint endpoint;

	public static Test suite()
	{
		return new JBossWSTestSetup(AddressingActionTestCase.class, "jaxws-wsaddressing-action-rpc.war");
	}

	protected void setUp() throws Exception
	{
		super.setUp();

		if (endpoint == null)
		{
			String wsdlLocation = "http://"+getServerHost()+":8080/jaxws-wsaddressing-action-rpc?wsdl";
			URL wsdlURL = new URL(wsdlLocation);
			QName serviceName = new QName("http://org.jboss.ws/addressing/action", "ActionRpcEndpointImplService");

			Service service = Service.create(wsdlURL, serviceName);
			endpoint = service.getPort(ActionEndpoint.class);
		}
	}

	public void testRpcEndpoint() throws Exception
	{

		// Setup a custom chain
		List<Handler> customHandlerChain = new ArrayList<Handler>();
		customHandlerChain.add(new CustomAddressingHandler());
		customHandlerChain.add(new WSAddressingClientHandler());
		((BindingProvider)endpoint).getBinding().setHandlerChain(customHandlerChain);

		assertEquals("foo:HelloFoo", endpoint.foo("HelloFoo"));
		assertEquals("bar:HelloBar", endpoint.bar("HelloBar"));
	}

	/**
	 * Verify that our addresing impl. correctly processes
	 * wsa headers when mustUnderstand=1 is sent
	 * 
	 * @throws Exception
	 */
	public void testRpcEndpointEnabledMU() throws Exception
	{

		// Setup a custom chain
		List<Handler> customHandlerChain = new ArrayList<Handler>();
		customHandlerChain.add(new AddressingHandlerEnableMU());
		customHandlerChain.add(new WSAddressingClientHandler());
		((BindingProvider)endpoint).getBinding().setHandlerChain(customHandlerChain);

		assertEquals("foo:HelloFoo", endpoint.foo("HelloFoo"));
		assertEquals("bar:HelloBar", endpoint.bar("HelloBar"));
	}
}
