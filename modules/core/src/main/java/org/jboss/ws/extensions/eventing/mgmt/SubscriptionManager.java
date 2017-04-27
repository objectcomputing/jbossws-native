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

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jboss.logging.Logger;
import org.jboss.util.naming.Util;
import org.jboss.ws.WSException;
import org.jboss.ws.extensions.eventing.EventingConstants;
import org.jboss.ws.extensions.eventing.deployment.EventingEndpointDeployment;
import org.jboss.ws.extensions.eventing.jaxws.AttributedURIType;
import org.jboss.ws.extensions.eventing.jaxws.EndpointReferenceType;
import org.jboss.ws.extensions.eventing.jaxws.ReferenceParametersType;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.jboss.wsf.common.utils.UUIDGenerator;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The SubscriptionManagerEndpoint maintains event sources and subscriptions.<br>
 * It is interfaced through the EventSourceEndpoint and SubscriptionManagerEndpoint SOAP endpoints.
 * <p>
 * Applications can use the EventDispatcher interface to dispatch
 * event messages to subscribers. The current implementation is backed by a ThreadPoolExecutor,
 * that asynchronously delivers messages to event sink endpoints.
 * It can be configurd through the following attributes:
 * <ul>
 *  <li>corePoolSize - average number of idle threads
 *  <li>maximumPoolSize - maximum number of threads
 *  <li>eventKeepAlive - keep alive before an undelivered event message is discarded.
 * </ul>
 * Event filtering is supported on subscription level based on XPath expressions.
 * For further information see <code>http://www.w3.org/TR/xpath#predicates</code>.
 * <p>
 * Currently only event push is supported.
 *
 * @see org.jboss.ws.extensions.eventing.jaxws.EventSourceEndpoint
 * @see org.jboss.ws.extensions.eventing.jaxws.SubscriptionManagerEndpoint
 * @see org.jboss.ws.extensions.eventing.jaxws.FilterType
 *
 * @author Heiko Braun, <heiko@openj.net>
 * @since 02-Dec-2005
 */
public class SubscriptionManager implements SubscriptionManagerMBean, EventDispatcher
{
   private static final Logger log = Logger.getLogger(SubscriptionManager.class);

   // Maps event source namespaces to event source instances.
   private ConcurrentMap<URI, EventSource> eventSourceMapping = new ConcurrentHashMap<URI, EventSource>();
   // Maps subscriptions to event sources
   private ConcurrentMap<URI, List<Subscription>> subscriptionMapping = new ConcurrentHashMap<URI, List<Subscription>>();
   // Buffers notifications. FIFO ordering.
   private BlockingQueue<Runnable> eventQueue = new LinkedBlockingQueue<Runnable>();
   // Event dispatcher thread pool.
   private ThreadPoolExecutor threadPool;
   // True force validation of every notification message against its schema
   private boolean validateNotifications = false;
   // subscription watchdog that maintains expirations
   private WatchDog watchDog;
   // True if dispatcher is bound to JNDI
   private boolean isDispatcherBound = false;
   // List containing all errors occured during notification since service startup
   // TODO: save this list and made possible to resend failed notification using jms instead of list
   private List<NotificationFailure> notificationFailures = new ArrayList<NotificationFailure>();
   // The host that the dispatcher is bound to
   private String bindAddress;

   private static EventingBuilder builder = EventingBuilder.createEventingBuilder();

   public String getBindAddress()
   {
      if (bindAddress == null)
      {
         try
         {
            InetAddress localHost = InetAddress.getLocalHost();
            log.debug("BindAddress not set, using host: " + localHost.getHostName());
            bindAddress = localHost.getHostName();
         }
         catch (UnknownHostException e)
         {
            log.debug("BindAddress not set, using: 'localhost'");
            bindAddress = "localhost";
         }
      }
      return bindAddress;
   }

   public void setBindAddress(String bindAddress)
   {
      this.bindAddress = bindAddress;
   }

   public void create() throws Exception
   {
      MBeanServer server = getJMXServer();
      if (server != null)
      {
         log.debug("Create subscription manager");
         server.registerMBean(this, OBJECT_NAME);
      }
   }

   public void destroy() throws Exception
   {
      MBeanServer server = getJMXServer();
      if (server != null)
      {
         log.debug("Destroy subscription manager");
         server.unregisterMBean(OBJECT_NAME);
      }
   }

   public void start() throws Exception
   {
      log.debug("Start subscription manager");

      // setup thread pool
      threadPool = new ThreadPoolExecutor(5, 15, // core/max num threads
            5000, TimeUnit.MILLISECONDS, // 5 seconds keepalive
            eventQueue);

      // start the subscription watchdog
      watchDog = new WatchDog(subscriptionMapping);
      watchDog.startup();
   }

   public void stop()
   {
      log.debug("Stop subscription manager");
      try
      {
         // remove event dispatcher
         Util.unbind(new InitialContext(), EventingConstants.DISPATCHER_JNDI_NAME);

         // stop thread pool
         threadPool.shutdown();

         // stop the watchdog
         watchDog.shutdown();

         for (URI eventSourceNS : eventSourceMapping.keySet())
         {
            removeEventSource(eventSourceNS);
         }
      }
      catch (NamingException e)
      {
         // ignore
      }
   }

   private static URI generateSubscriptionID()
   {
      try
      {
         return new URI("urn:jbwse:" + UUIDGenerator.generateRandomUUIDString());
      }
      catch (URISyntaxException e)
      {
         throw new WSException(e.getMessage());
      }
   }

   /**
    * A two phase deployment process.
    */
   public void registerEventSource(EventingEndpointDeployment deploymentInfo)
   {
      // workaround for JBWS-1006
      lazyBindEventDispatcher();

      EventSource eventSource = builder.newEventSource(deploymentInfo);
      if (eventSourceMapping.containsKey(eventSource.getNameSpace()) == false)
      {
         eventSourceMapping.put(eventSource.getNameSpace(), eventSource);
         updateManagerAddress(deploymentInfo, eventSource);

         eventSource.setState(EventSource.State.CREATED);
         log.debug("Created: " + eventSource);
      }
      else
      {
         eventSource = eventSourceMapping.get(eventSource.getNameSpace());
         updateManagerAddress(deploymentInfo, eventSource);
         subscriptionMapping.put(eventSource.getNameSpace(), new CopyOnWriteArrayList<Subscription>());

         eventSource.setState(EventSource.State.STARTED);
         log.debug("Started: " + eventSource);
      }
   }

   private void lazyBindEventDispatcher()
   {
      if (!isDispatcherBound)
      {
         try
         {
            // bind dispatcher to JNDI
            Util.rebind(new InitialContext(), EventingConstants.DISPATCHER_JNDI_NAME, new DispatcherDelegate(getBindAddress()));
            log.info("Bound event dispatcher to java:/" + EventingConstants.DISPATCHER_JNDI_NAME);
            isDispatcherBound = true;
         }
         catch (NamingException e)
         {
            throw new WSException("Unable to bind EventDispatcher ", e);
         }
      }
   }

   /**
    * When both the EventSourcePort and the SubscriptionManagerPort are registered
    * we finally know the real SubscriptionManager EPR and update the endpoint adress.
    * @param deploymentInfo
    * @param eventSource
    */
   private static void updateManagerAddress(EventingEndpointDeployment deploymentInfo, EventSource eventSource)
   {
      String addr = null;
      if (deploymentInfo.getPortName().getLocalPart().equals("SubscriptionManagerPort"))
         addr = deploymentInfo.getEndpointAddress();

      if (addr != null)
         eventSource.setManagerAddress(addr);
   }

   public void removeEventSource(URI eventSourceNS)
   {
      if (eventSourceMapping.containsKey(eventSourceNS))
      {
         List<Subscription> subscriptions = subscriptionMapping.get(eventSourceNS);
         for (Subscription s : subscriptions) // iterator is a snapshot
         {
            s.end(EventingConstants.SOURCE_SHUTTING_DOWN);
         }

         subscriptions.clear();
         eventSourceMapping.remove(eventSourceNS);
         log.debug("Event source " + eventSourceNS + " removed");
      }
   }

   /**
    * Subscribe to an event source.
    */
   public SubscriptionTicket subscribe(URI eventSourceNS, EndpointReferenceType notifyTo, EndpointReferenceType endTo, Date expires, Filter filter)
         throws SubscriptionError
   {
      log.debug("Subscription request for " + eventSourceNS);

      EventSource eventSource = eventSourceMapping.get(eventSourceNS);
      if (null == eventSource)
         throw new SubscriptionError(EventingConstants.CODE_UNABLE_TO_PROCESS, "EventSource '" + eventSourceNS + "' not registered");

      if (eventSource.getState() != EventSource.State.STARTED)
         throw new SubscriptionError(EventingConstants.CODE_UNABLE_TO_PROCESS, "EventSource '" + eventSourceNS + "' not started");

      // expiry constraints
      if (expires != null)
      {
         assertLeaseConstraints(expires);
      }
      else
      {
         expires = new Date((System.currentTimeMillis() + EventingConstants.DEFAULT_LEASE));
      }

      // filter constraints
      if (filter != null)
      {
         if (eventSource.getSupportedFilterDialects().isEmpty())
            throw new SubscriptionError(EventingConstants.CODE_FILTER_NOT_SUPPORTED, "Filtering is not supported.");
         else
         {
            boolean filterAvailable = false;
            for (URI supportedDialect : eventSource.getSupportedFilterDialects())
            {
               if (filter.getDialect().equals(supportedDialect))
               {
                  filterAvailable = true;
                  break;
               }
            }

            if (!filterAvailable)
               throw new SubscriptionError(EventingConstants.CODE_REQUESTED_FILTER_UNAVAILABLE, "The requested filter dialect is not supported.");
         }
      }

      // create subscription
      EndpointReferenceType epr = new EndpointReferenceType();
      AttributedURIType attrURI = new AttributedURIType();
      attrURI.setValue(eventSource.getManagerAddress().toString());
      epr.setAddress(attrURI);
      ReferenceParametersType refParam = new ReferenceParametersType();
      JAXBElement idqn = new JAXBElement(new QName("http://schemas.xmlsoap.org/ws/2004/08/eventing", "Identifier"), String.class, generateSubscriptionID().toString());
      refParam.getAny().add(idqn);
      epr.setReferenceParameters(refParam);

      Subscription subscription = new Subscription(eventSource.getNameSpace(), epr, notifyTo, endTo, expires, filter);

      subscriptionMapping.get(eventSourceNS).add(subscription);

      log.debug("Registered subscription " + subscription.getIdentifier());

      return new SubscriptionTicket(epr, subscription.getExpires());
   }

   private void assertLeaseConstraints(Date expireDate) throws SubscriptionError
   {
      long expires = expireDate.getTime() - System.currentTimeMillis();
      if (expires < 0 || EventingConstants.MAX_LEASE_TIME < expires)
         throw new SubscriptionError(EventingConstants.CODE_INVALID_EXPIRATION_TIME, "The expiration time requested is invalid: " + expires + "ms");
   }

   /**
    * Renew a subscription.
    *
    * @param identifier
    * @param lease
    * @return the new lease date
    * @throws SubscriptionError
    */
   public Date renew(URI identifier, Date lease) throws SubscriptionError
   {
      Subscription subscription = subscriberForID(identifier);
      if (null == subscription)
         throw new SubscriptionError(EventingConstants.CODE_UNABLE_TO_RENEW, "Subscription " + identifier + " does not exist");

      if (lease != null)
         assertLeaseConstraints(lease);
      else lease = new Date((System.currentTimeMillis() + EventingConstants.DEFAULT_LEASE));

      subscription.setExpires(lease);
      return lease;
   }

   /**
    * Get status for subscription.
    *
    * @param identifier
    * @return the actual lease date.
    * @throws SubscriptionError when the subscriber does not exist
    */
   public final Date getStatus(URI identifier) throws SubscriptionError
   {
      Subscription subscription = subscriberForID(identifier);
      if (null == subscription)
         throw new SubscriptionError(EventingConstants.CODE_UNABLE_TO_PROCESS, "Subscription " + identifier + " does not exist");

      return subscription.getExpires();
   }

   /**
    * Release a subscription.
    *
    * @param identifier
    * @throws SubscriptionError when the subscriber does not exist
    */
   public void unsubscribe(URI identifier) throws SubscriptionError
   {
      for (List<Subscription> subscriptions : subscriptionMapping.values())
      {
         for (Subscription s : subscriptions)
         {
            if (identifier.equals(s.getIdentifier()))
            {
               subscriptions.remove(s);
               log.debug("Removed subscription " + s);
               break;
            }
         }
      }
   }

   public String showEventsourceTable()
   {

      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      pw.println("<h3>Deployed Eventsources</h3>");

      pw.println("<table>");
      pw.println("<tr><td>Name</td><td>NS</td></tr>");

      for (EventSource source : eventSourceMapping.values())
      {
         pw.println("<tr><td>" + source.getName() + "</td><td>" + source.getNameSpace() + "</td></tr>");
      }

      pw.println("</table>");
      pw.close();

      return sw.toString();
   }

   public String showSubscriptionTable()
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      pw.println("<h3>Registered Subscriptions</h3>");

      pw.println("<table>");
      pw.println("<tr><td>Identifier</td><td>Expires</td><td>Filter</td></tr>");

      for (List<Subscription> subscriptions : subscriptionMapping.values())
      {
         for (Subscription s : subscriptions)
         {
            pw.println("<tr><td>" + s.getIdentifier() + "</td><td>" + s.getExpires() + "</td><td>" + (s.getFilter()!=null ? s.getFilter().getExpression() : "") + "</td></tr>");
         }
      }
      pw.println("</table>");
      pw.close();

      return sw.toString();
   }

   private Subscription subscriberForID(URI id)
   {
      Subscription subscription = null;
      for (List<Subscription> subscriptions : subscriptionMapping.values())
      {
         for (Subscription s : subscriptions)
         {
            if (id.equals(s.getIdentifier()))
            {
               subscription = s;
               break;
            }
         }
      }
      return subscription;
   }

   public void dispatch(URI eventSourceNS, Element payload)
   {
      DispatchJob dispatchJob = new DispatchJob(eventSourceNS, payload, subscriptionMapping);
      if (validateNotifications && !this.validateMessage(DOMWriter.printNode(payload, false), eventSourceNS))
      {
         throw new DispatchException("Notification message validation failed!");
      }
      threadPool.execute(dispatchJob);
   }

   public void addNotificationFailure(NotificationFailure failure)
   {
      notificationFailures.add(failure);
   }

   public List<NotificationFailure> showNotificationFailures()
   {
      return notificationFailures;
   }

   private boolean validateMessage(String msg, URI eventSourceNS)
   {
      try
      {
         EventSource es = eventSourceMapping.get(eventSourceNS);
         log.info(new StringBuffer("Validating message: \n\n").append(msg).append("\n\nagainst the following schema(s): \n").toString());
         for (int i = 0; i < es.getNotificationSchema().length; i++)
         {
            log.info(es.getNotificationSchema()[i]);
         }
         Element rootElement = DOMUtils.parse(msg);
         if (!es.getNotificationRootElementNS().equalsIgnoreCase(rootElement.getNamespaceURI()))
         {
            log.error("Root element expected namespace: " + es.getNotificationRootElementNS());
            return false;
         }
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware(true);
         factory.setValidating(true);
         factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");

         String[] notificationSchemas = es.getNotificationSchema();
         InputSource[] is = new InputSource[notificationSchemas.length];
         for (int i = 0; i < notificationSchemas.length; i++)
         {
            is[i] = new InputSource(new StringReader(notificationSchemas[notificationSchemas.length - 1 - i]));
            //is[i] = new InputSource(new StringReader(notificationSchemas[i]));
         }

         factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", is);
         factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
         DocumentBuilder docBuilder = factory.newDocumentBuilder();
         ErrorHandler errorHandler = new Validator();
         docBuilder.setErrorHandler(errorHandler);
         docBuilder.parse(new InputSource(new StringReader(msg)));
         log.info("Document validated!");
         return true;
      }
      catch (Exception e)
      {
         log.error(e);
         log.info("Cannot validate and/or parse the document!");
         return false;
      }

   }

   // ----------------------------------------------------------------------
   // MBean support

   public int getCorePoolSize()
   {
      return threadPool.getCorePoolSize();
   }

   public int getMaximumPoolSize()
   {
      return threadPool.getMaximumPoolSize();
   }

   public int getLargestPoolSize()
   {
      return threadPool.getLargestPoolSize();
   }

   public int getActiveCount()
   {
      return threadPool.getActiveCount();
   }

   public long getCompletedTaskCount()
   {
      return threadPool.getCompletedTaskCount();
   }

   public void setCorePoolSize(int corePoolSize)
   {
      threadPool.setCorePoolSize(corePoolSize);
   }

   public void setMaxPoolSize(int maxPoolSize)
   {
      threadPool.setMaximumPoolSize(maxPoolSize);
   }

   public void setEventKeepAlive(long millies)
   {
      threadPool.setKeepAliveTime(millies, TimeUnit.MILLISECONDS);
   }

   public boolean isValidateNotifications()
   {
      return this.validateNotifications;
   }

   public void setValidateNotifications(boolean validateNotifications)
   {
      this.validateNotifications = validateNotifications;
   }

   private MBeanServer getJMXServer()
   {
      MBeanServer server = null;
      ArrayList servers = MBeanServerFactory.findMBeanServer(null);
      if (servers.size() > 0)
      {
         server = (MBeanServer)servers.get(0);
      }
      return server;
   }

   /**
    * The watchdog maintains subscription expirations.
    */
   private class WatchDog implements Runnable
   {

      private ConcurrentMap<URI, List<Subscription>> subscriptions;
      private boolean active = true;
      private Thread worker;

      public WatchDog(ConcurrentMap<URI, List<Subscription>> subscriptions)
      {
         this.subscriptions = subscriptions;
      }

      public void run()
      {
         while (active)
         {

            for (List<Subscription> subscriptions : subscriptionMapping.values())
            {
               for (Subscription s : subscriptions)
               {
                  if (s.isExpired())
                  {
                     s.end(EventingConstants.SOURCE_CANCELING);
                     subscriptions.remove(s);
                  }
               }
            }

            try
            {
               Thread.sleep(1000 * 60);
            }
            catch (InterruptedException e)
            {
               log.error(e);
            }
         }
      }

      public void startup()
      {
         worker = new Thread(this, "SubscriptionWatchDog");
         worker.start();
      }

      public void shutdown()
      {
         this.active = false;
      }

   }

   private class Validator implements ErrorHandler
   {
      public void error(SAXParseException exception) throws SAXException
      {
         throw new SAXException(exception);
      }

      public void fatalError(SAXParseException exception) throws SAXException
      {
         throw new SAXException(exception);
      }

      public void warning(SAXParseException exception) throws SAXException
      {
      }
   }

}
