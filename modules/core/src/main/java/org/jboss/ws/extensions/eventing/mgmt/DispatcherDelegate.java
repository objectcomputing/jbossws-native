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
package org.jboss.ws.extensions.eventing.mgmt;

import java.net.URI;
import java.util.Iterator;

import javax.management.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;

import org.jboss.ws.WSException;
import org.jboss.logging.Logger;
import org.w3c.dom.Element;

/**
 * Event dispatching delegate that will be bound to JNDI.
 *
 * @see DispatcherFactory
 * 
 * @author Heiko Braun, <heiko@openj.net>
 * @since 11-Jan-2006
 */
public class DispatcherDelegate implements EventDispatcher, Referenceable
{
   private static final Logger log = Logger.getLogger(DispatcherDelegate.class);
   
   private String hostname;
   public final static String MANAGER_HOSTNAME = "manager.hostname";
   private SubscriptionManagerMBean subscriptionManager = null;

   public DispatcherDelegate()
   {
   }

   public DispatcherDelegate(String hostname)
   {
      setHostname(hostname);
   }

   public void dispatch(URI eventSourceNS, Element payload)
   {
      getSubscriptionManager().dispatch(eventSourceNS, payload);
   }

   public Reference getReference() throws NamingException
   {

      Reference myRef = new Reference(DispatcherDelegate.class.getName(), DispatcherFactory.class.getName(), null);

      // let the delegate now where to find the subscription manager
      myRef.add(new StringRefAddr(MANAGER_HOSTNAME, hostname));

      return myRef;
   }

   private SubscriptionManagerMBean getSubscriptionManager()
   {
      if (null == subscriptionManager)
      {
         try
         {
            ObjectName objectName = SubscriptionManager.OBJECT_NAME;
            subscriptionManager = (SubscriptionManagerMBean)
              MBeanServerInvocationHandler.newProxyInstance(
                getServer(),
                objectName,
                SubscriptionManagerMBean.class, false
              );
         }
         catch (Exception e)
         {
            throw new WSException("Failed to access subscription manager: " + e.getMessage(), e);
         }
      }

      return subscriptionManager;
   }

   /**
    * http://wiki.jboss.org/wiki/Wiki.jsp?page=FindMBeanServer
    * 
    * @return
    * @throws NamingException
    */
   private MBeanServerConnection getServer() throws NamingException
   {
      // Local
      MBeanServerConnection server = locateJBoss();
      
      if(null==server)
      {
         // Remote
         InitialContext iniCtx = new InitialContext();
         server = (MBeanServerConnection)iniCtx.lookup("jmx/invoker/RMIAdaptor");
         log.debug("Using RMI invocation");
      }
      else
      {
         log.debug("Using in-VM invocation");
      }
      
      return server;
   }

   // avoid dependency on jboss-jmx.jar
   public MBeanServerConnection locateJBoss()
   {
      MBeanServerConnection jboss = null;

      for (Iterator i = MBeanServerFactory.findMBeanServer(null).iterator(); i.hasNext(); )
      {
         MBeanServer server = (MBeanServer) i.next();
         if ("jboss".equals(server.getDefaultDomain()))
         {
            jboss = server;
         }
      }

      return jboss;
   }

   void setHostname(String hostname)
   {
      if (null == hostname)
         throw new IllegalArgumentException("Hostname may not be null");
      this.hostname = hostname;
   }
}
