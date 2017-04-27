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
package org.jboss.ws.metadata.wsdl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.xb.binding.NamespaceRegistry;
import org.w3c.dom.Document;

/**
 * The top level Definitions component is just a container for two categories of components;
 * WSDL components and type system components. WSDL components are interfaces, bindings and services.
 *
 * Type system components describe the constraints on a message�s content.
 *
 * @author Thomas.Diesler@jboss.org
 * @since 10-Oct-2004
 */
public class WSDLDefinitions extends Extendable implements Serializable
{
   private static final long serialVersionUID = 1643422922694990226L;

   // provide logging
   private final Logger log = Logger.getLogger(WSDLDefinitions.class);

   /** The REQUIRED targetNamespace attribute information item defines the namespace affiliation of top-level
    * components defined in this definitions element information item. Interfaces, Bindings and Services
    * are top-level components. */
   private String targetNamespace;
   /** The REQUIRED wsdl namespace. For WSDL-2.0 it is http://www.w3.org/2003/11/wsdl,
    * for WSDL-1.1 it is http://schemas.xmlsoap.org/wsdl/ */
   private String wsdlNamespace;
   /** Zero or more import element information items */
   private List<WSDLImport> imports = new ArrayList<WSDLImport>();
   /** Zero or more include element information items */
   private List<WSDLInclude> includes = new ArrayList<WSDLInclude>();
   /** Types element information item */
   private WSDLTypes types;
   /** Zero or more interface element information items */
   private Map<QName, WSDLInterface> interfaces = new LinkedHashMap<QName, WSDLInterface>();
   /** Zero or more binding element information items */
   private Map<QName, WSDLBinding> bindings = new LinkedHashMap<QName, WSDLBinding>();
   /** Zero or more service element information items */
   private Map<QName, WSDLService> services = new LinkedHashMap<QName, WSDLService>();

   // Zero or more namespace definitions
   // [TODO] What is this doing here?
   private NamespaceRegistry namespaces = new NamespaceRegistry();

   // The original wsdl4j definition if we have wsdl-1.1
   private Definition wsdlOneOneDefinition;
   // The WSDL document
   private Document wsdlDocument;

   public WSDLDefinitions() {}

   /** Set the wsdl4j definition if we have wsdl-1.1 */
   public void setWsdlOneOneDefinition(Definition wsdlDefinition)
   {
      this.wsdlOneOneDefinition = wsdlDefinition;
   }

   /**
    * Get the wsdl4j definition if we have wsdl-1.1.
    *
    * Note: This object is NOT THREAD-SAFE
    */
   public Definition getWsdlOneOneDefinition()
   {
      return wsdlOneOneDefinition;
   }

   public Document getWsdlDocument()
   {
      return wsdlDocument;
   }

   public void setWsdlDocument(Document wsdlDocument)
   {
      this.wsdlDocument = wsdlDocument;
   }

   /** Register the given namespaceURI/prefix combination */
   public String registerNamespaceURI(String nsURI, String prefix)
   {
      if (Constants.NS_XML.equalsIgnoreCase(nsURI))
      {
         //"http://www.w3.org/XML/1998/namespace" is always bound to "xml" prefix
         //and does not need to be registered.
         return Constants.PREFIX_XML;
      }
      else if (Constants.PREFIX_XML.equalsIgnoreCase(prefix))
      {
         throw new IllegalArgumentException("The prefix " + Constants.PREFIX_XML +
               " cannot be bound to any namespace other than its usual namespace (trying to bind to "
               + nsURI + " )");
      }
      
      String pre = namespaces.getPrefix(nsURI);
      if (pre == null || 0 == pre.length())
      {
         pre = namespaces.registerURI(nsURI, prefix);
         log.trace("registerNamespaceURI: " + pre + '=' + nsURI);
      }
      return pre;
   }

   /** Register a QName and return a QName that is guarantied to have a prefix */
   public QName registerQName(QName qname)
   {
      return namespaces.registerQName(qname);
   }

   /** Get the prefix for a given namespaceURI */
   public String getPrefix(String namespaceURI)
   {
      return namespaces.getPrefix(namespaceURI);
   }

   /** Get the namespaceURI for a given prefix */
   public String getNamespaceURI(String prefix)
   {
      return namespaces.getNamespaceURI(prefix);
   }

   /** Get the prefix for the target namespace */
   public String getTargetPrefix()
   {
      return namespaces.getPrefix(targetNamespace);
   }

   /** Get an iterator for registered namespace URIs */
   public Iterator getRegisteredNamespaceURIs()
   {
      return namespaces.getRegisteredURIs();
   }

   /** Get an iterator for registered prefixs */
   public Iterator getRegisteredPrefix()
   {
      return namespaces.getRegisteredPrefixes();
   }

   public String getTargetNamespace()
   {
      return targetNamespace;
   }

   public void setTargetNamespace(String namespaceURI)
   {
      if(namespaceURI == null)
         throw new IllegalArgumentException("Illegal Null Argument:namespaceURI");

      log.trace("setTargetNamespace: " + namespaceURI);
      this.targetNamespace = namespaceURI;
   }

   public String getWsdlNamespace()
   {
      return wsdlNamespace;
   }

   public void setWsdlNamespace(String namespaceURI)
   {
      if (Constants.NS_WSDL11.equals(namespaceURI) == false)
         throw new IllegalArgumentException("Invalid default namespace: " + namespaceURI);

      this.wsdlNamespace = namespaceURI;
   }

   public WSDLImport[] getImports()
   {
      WSDLImport[] arr = new WSDLImport[imports.size()];
      imports.toArray(arr);
      return arr;
   }

   public void addImport(WSDLImport anImport)
   {
      imports.add(anImport);
   }

   public WSDLInclude[] getIncludes()
   {
      WSDLInclude[] arr = new WSDLInclude[includes.size()];
      includes.toArray(arr);
      return arr;
   }

   public void addInclude(WSDLInclude include)
   {
      includes.add(include);
   }

   public WSDLTypes getWsdlTypes()
   {
      return types;
   }

   public void setWsdlTypes(WSDLTypes types)
   {
      this.types = types;
      this.types.setWSDLDefintiions(this);
   }

   public WSDLInterface getInterface(QName name)
   {
      return interfaces.get(name);
   }

   public WSDLInterface[] getInterfaces()
   {
      WSDLInterface[] arr = new WSDLInterface[interfaces.size()];
      new ArrayList<WSDLInterface>(interfaces.values()).toArray(arr);
      return arr;
   }

   public void addInterface(WSDLInterface wsdlInterface)
   {
      interfaces.put(wsdlInterface.getName(), wsdlInterface);
   }

   public WSDLBinding[] getBindings()
   {
      WSDLBinding[] arr = new WSDLBinding[bindings.size()];
      new ArrayList<WSDLBinding>(bindings.values()).toArray(arr);
      return arr;
   }

   public WSDLBinding getBinding(QName name)
   {
      return bindings.get(name);
   }

   public WSDLBinding getBindingByInterfaceName(QName qname)
   {
      WSDLBinding wsdlBinding = null;
      for (WSDLBinding aux : bindings.values())
      {
         if (aux.getInterfaceName().equals(qname))
         {
            if (wsdlBinding != null)
               log.warn("Multiple WSDL bindings referrence the same interface: " + qname);

            wsdlBinding = aux;
         }
      }
      return wsdlBinding;
   }

   public void addBinding(WSDLBinding binding)
   {
      bindings.put(binding.getName(), binding);
   }

   public WSDLService[] getServices()
   {
      WSDLService[] arr = new WSDLService[services.size()];
      new ArrayList<WSDLService>(services.values()).toArray(arr);
      return arr;
   }

   public void addService(WSDLService service)
   {
      services.put(service.getName(), service);
   }

   public WSDLService getService(QName name)
   {
      return services.get(name);
   }
   
   public WSDLService getService(String localName)
   {
      return services.get(new QName(targetNamespace, localName));
   }

   /** Unregister the given namespaceURI/prefix combination */
   public void unRegisterNamespaceURI(String namespaceURI, String prefix)
   {
      String pre = namespaces.getPrefix(namespaceURI);
      if(pre != null && pre.equals(prefix))
         namespaces.removePrefixMapping(prefix);
   }
}
