/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.test.ws.tools.jbws1428;

import org.jboss.test.ws.tools.WSToolsBase;
import org.jboss.test.ws.tools.validation.JaxrpcMappingValidator;
import org.jboss.ws.tools.WSTools;

/**
 * [JBWS-1428] - Java to WSDL - Arrays defined with indexed properties within 
 * value type are skipped when generating the WSDL
 * 
 * @author darran.lofthouse@jboss.com
 * @since 14 Dec 2006
 */
public class JBWS1428TestCase extends WSToolsBase
{

   public void testGenerate() throws Exception
   {
      String resourceDir = createResourceFile("tools/jbws1428").getAbsolutePath();
      String toolsDir = "target/wstools/jbws1428/output";
      String[] args = new String[] { "-dest", toolsDir, "-config", resourceDir + "/wstools-config.xml" };
      new WSTools().generate(args);

      semanticallyValidateWSDL(resourceDir + "/ValueObjectService.wsdl", toolsDir + "/wsdl/ValueObjectService.wsdl");

      JaxrpcMappingValidator mappingValidator = new JaxrpcMappingValidator();
      mappingValidator.validate(resourceDir + "/jaxrpc-mapping.xml", toolsDir + "/jaxrpc-mapping.xml");
   }

}
