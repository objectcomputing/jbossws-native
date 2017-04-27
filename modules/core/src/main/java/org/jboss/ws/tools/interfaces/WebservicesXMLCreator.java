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
package org.jboss.ws.tools.interfaces;

import java.io.File;
import java.io.IOException;

/**
 * Defines the contract for webservices.xml creating agents
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jul 23, 2005 
 */

public interface WebservicesXMLCreator
{

   /**
    * Generate the webservices.xml file
    * @throws IOException
    */
   public void generateWSXMLDescriptor(File file) throws IOException;

   /**
    * @param targetNamespace The targetNamespace to set.
    */
   public void setTargetNamespace(String targetNamespace);

   /**
    * @param seiName The seiName to set.
    */
   public void setSeiName(String seiName);

   /**
    * @param portName The portName to set.
    */
   public void setPortName(String portName);

   /**
    * @param serviceName The serviceName to set.
    */
   public void setServiceName(String serviceName);

   /**
    * @param ejbLink The ejbLink to set.
    */
   public void setEjbLink(String ejbLink);

   /**
    * @param servletLink The servletLink to set.
    */
   public void setServletLink(String servletLink);

   /**
    * @param mappingFileEntry The mapping file entry
    */
   public void setMappingFile(String mappingFileEntry);

   /**
    * 
    * @param wsdlFileEntry The wsdl-file entry
    */
   public void setWsdlFile(String wsdlFileEntry);

   /**
    * 
    * @param append add ws descriptions to existing webservices.xml file, if any 
    */
   public void setAppend(boolean append);
}
