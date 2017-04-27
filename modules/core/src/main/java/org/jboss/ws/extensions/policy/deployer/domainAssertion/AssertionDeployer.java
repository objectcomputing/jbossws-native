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

import org.apache.ws.policy.PrimitiveAssertion;
import org.jboss.ws.extensions.policy.deployer.exceptions.UnsupportedAssertion;
import org.jboss.ws.metadata.umdm.ExtensibleMetaData;

/**
 * Interface each policy domain should implement for policy deployment.
 * 
 * @author Stefano Maestri <mailto:stefano.maestri@javalinux.it> 
 * @author Alessio Soldano <mailto:alessio.soldano@javalinux.it>
 *
 */
public interface AssertionDeployer
{
   /**
    * Server side deployment method; ExtensibleMetaData provided so
    * that the implementor to let it plugs its own handlers. 
    * 
    * @param assertion
    * @param extMetaData
    * @throws UnsupportedAssertion
    */
   public void deployServerSide(PrimitiveAssertion assertion, ExtensibleMetaData extMetaData) throws UnsupportedAssertion;
   
   /**
    * Client side deployment method; ExtensibleMetaData provided so
    * that the implementor to let it plugs its own handlers.
    * 
    * @param assertion
    * @param extMetaData
    * @throws UnsupportedAssertion
    */
   public void deployClientSide(PrimitiveAssertion assertion, ExtensibleMetaData extMetaData) throws UnsupportedAssertion;
}
