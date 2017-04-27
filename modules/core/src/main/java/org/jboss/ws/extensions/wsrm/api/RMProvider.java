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
package org.jboss.ws.extensions.wsrm.api;

/**
 * WS-RM provider used for sequence creation. Typical usecase is:
 * 
 * <p><blockquote><pre>
 * boolean addressableClient = true;
 * SEI sei = (SEI)service.getPort(SEI.class)
 * sei.callMethod1();
 * sei.callMethod2();
 * ...
 * ((RMProvider)sei).closeSequence();
 * ((RMProvider)sei).createSequence();
 * sei.callMethod1();
 * sei.callMethod2();
 * ...
 * ((RMProvider)sei).closeSequence();
 * </pre></blockquote></p>
 *
 * @author richard.opalka@jboss.com
 *
 * @since Oct 22, 2007
 */
public interface RMProvider
{
   /**
    * Creates new WS-RM sequence and associates it with service proxy
    * @throws RMException if something went wrong
    */
   void createSequence();
   
   /**
    * Close the sequence associated with service proxy
    * @throws RMException if something went wrong
    */
   void closeSequence();
}
