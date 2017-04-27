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
package org.jboss.ws.tools.wsdl;

import com.ibm.wsdl.DefinitionImpl;
import com.ibm.wsdl.extensions.PopulatedExtensionRegistry;
import com.ibm.wsdl.xml.WSDLWriterImpl;

import javax.wsdl.Definition;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;

/**
 * A fork of the original wsdl4j 1.6.2 package
 * that delegates to the {@link JBossWSDLReaderImpl}.
 * <p>
 * Original author: Matthew J. Duftler (duftler@us.ibm.com)
 */
public class JBossWSDLFactoryImpl extends WSDLFactory
{
	/**
	 * Create a new instance of a Definition, with an instance
	 * of a PopulatedExtensionRegistry as its ExtensionRegistry.
	 *
	 * @see com.ibm.wsdl.extensions.PopulatedExtensionRegistry
	 */
	public Definition newDefinition()
	{
		Definition def = new DefinitionImpl();
		ExtensionRegistry extReg = newPopulatedExtensionRegistry();

		def.setExtensionRegistry(extReg);

		return def;
	}

	/**
	 * Create a new instance of a WSDLReader.
	 */
	public WSDLReader newWSDLReader()
	{
		return new JBossWSDLReaderImpl();
	}

	public WSDLWriter newWSDLWriter()
	{
		return new WSDLWriterImpl();
	}

	/**
	 * Create a new instance of an ExtensionRegistry with pre-registered
	 * serializers/deserializers for the SOAP, HTTP and MIME
	 * extensions. Java extensionTypes are also mapped for all
	 * the SOAP, HTTP and MIME extensions.
	 */
	public ExtensionRegistry newPopulatedExtensionRegistry()
	{
		return new PopulatedExtensionRegistry();
	}
}
