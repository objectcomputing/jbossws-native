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
 * This assertion deployer actually does nothing when asked to
 * start a policy assertion. It is used as a placeholder by
 * PolicyDeployer in case no modification to umdm or anything
 * else is actually required (for example when running the
 * WSProvideTask tool).
 * 
 * @author Stefano Maestri <mailto:stefano.maestri@javalinux.it> 
 * @author Alessio Soldano <mailto:alessio.soldano@javalinux.it>
 * 
 * @since 16-May-2007
 *
 */
public class NopAssertionDeployer implements AssertionDeployer
{

   public void deployClientSide(PrimitiveAssertion assertion, ExtensibleMetaData extMetaData) throws UnsupportedAssertion
   {
      //nop
   }

   public void deployServerSide(PrimitiveAssertion assertion, ExtensibleMetaData extMetaData) throws UnsupportedAssertion
   {
      //nop
   }

}
