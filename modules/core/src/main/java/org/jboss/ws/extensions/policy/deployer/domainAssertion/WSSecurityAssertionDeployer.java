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
package org.jboss.ws.extensions.policy.deployer.domainAssertion;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import org.apache.ws.policy.PrimitiveAssertion;
import org.jboss.logging.Logger;
import org.jboss.ws.extensions.policy.deployer.PolicyDeployer;
import org.jboss.ws.extensions.policy.deployer.exceptions.UnsupportedAssertion;
import org.jboss.ws.extensions.policy.deployer.util.PrimitiveAssertionWriter;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.ExtensibleMetaData;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.wsse.WSSecurityConfigFactory;
import org.jboss.ws.metadata.wsse.WSSecurityConfiguration;
import org.jboss.ws.metadata.wsse.WSSecurityOMFactory;

/**
 * 
 * @author Stefano Maestri, <mailto:stefano.maestri@javalinux.it>
 * @author Alessio Soldano, <mailto:alessio.soldano@javalinux.it>
 *
 */
public class WSSecurityAssertionDeployer implements AssertionDeployer
{
   private final static Logger log = Logger.getLogger(PolicyDeployer.class);
   
   public void deployServerSide(PrimitiveAssertion assertion, ExtensibleMetaData extMetaData) throws UnsupportedAssertion
   {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      if (extMetaData instanceof EndpointMetaData)
      {
         EndpointMetaData ep = (EndpointMetaData) extMetaData;
        
         WSSecurityConfiguration securityConfiguration;
         try
         {
            //GET XML of security assertion
            PrimitiveAssertionWriter.newInstance().writePrimitiveAssertion(assertion, stream);
            StringReader reader = new StringReader(stream.toString());
            
            //Set security configuration 
            securityConfiguration = WSSecurityOMFactory.newInstance().parse(reader);
            WSSecurityConfigFactory.newInstance().initKeystorePath(ep.getRootFile(), securityConfiguration);
            ep.getServiceMetaData().setSecurityConfiguration(securityConfiguration);
            
            //set up handler chain as defined in standard file
            ep.setConfigName("Standard WSSecurity Endpoint");
      
         }
         catch (Exception e)
         {
            e.printStackTrace();
            throw new UnsupportedAssertion();
         }
      }
   }
   
   public void deployClientSide(PrimitiveAssertion assertion, ExtensibleMetaData extMetaData) throws UnsupportedAssertion
   {
      if (extMetaData instanceof EndpointMetaData)
      {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();
         EndpointMetaData epMetaData = (EndpointMetaData) extMetaData;
         ServiceMetaData serviceMetaData = epMetaData.getServiceMetaData();
         if (serviceMetaData.getSecurityConfiguration() == null)
         {
            try
            {
               PrimitiveAssertionWriter.newInstance().writePrimitiveAssertion(assertion, stream);
               StringReader reader = new StringReader(stream.toString());
               
               WSSecurityConfiguration securityConfiguration = WSSecurityOMFactory.newInstance().parse(reader);
               serviceMetaData.setSecurityConfiguration(securityConfiguration);
               
               epMetaData.setConfigName("Standard WSSecurity Client");
            }
            catch (Exception e)
            {
               e.printStackTrace();
               throw new UnsupportedAssertion();
            }
         }
      }
      
   }

}
