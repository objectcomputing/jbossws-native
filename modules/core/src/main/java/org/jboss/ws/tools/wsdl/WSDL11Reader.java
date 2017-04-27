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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ElementExtensible;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.http.HTTPBinding;
import javax.wsdl.extensions.mime.MIMEContent;
import javax.wsdl.extensions.mime.MIMEMultipartRelated;
import javax.wsdl.extensions.mime.MIMEPart;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import javax.wsdl.extensions.soap12.SOAP12Body;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.core.utils.JBossWSEntityResolver;
import org.jboss.ws.core.utils.ResourceURL;
import org.jboss.ws.metadata.wsdl.Extendable;
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLBindingMessageReference;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperation;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperationInput;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperationOutput;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLDocumentation;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLExtensibilityElement;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceFault;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperation;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationInput;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutfault;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutput;
import org.jboss.ws.metadata.wsdl.WSDLMIMEPart;
import org.jboss.ws.metadata.wsdl.WSDLProperty;
import org.jboss.ws.metadata.wsdl.WSDLRPCPart;
import org.jboss.ws.metadata.wsdl.WSDLRPCSignatureItem;
import org.jboss.ws.metadata.wsdl.WSDLSOAPHeader;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.metadata.wsdl.WSDLTypes;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.XSModelTypes;
import org.jboss.ws.metadata.wsdl.WSDLRPCSignatureItem.Direction;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.metadata.wsdl.xsd.SchemaUtils;
import org.jboss.ws.tools.JavaToXSD;
import org.jboss.wsf.common.DOMUtils;
import org.jboss.wsf.common.DOMWriter;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A helper that translates a WSDL-1.1 object graph into a WSDL-2.0 object graph.
 *
 * @author Thomas.Diesler@jboss.org
 * @author Anil.Saldhana@jboss.org
 * @author <a href="jason.greene@jboss.com">Jason T. Greene</a>
 * @since 10-Oct-2004
 */
public class WSDL11Reader
{
   // provide logging
   private static final Logger log = Logger.getLogger(WSDL11Reader.class);

   private WSDLDefinitions destWsdl;
   private JBossWSEntityResolver entityResolver = new JBossWSEntityResolver();

   // Maps wsdl message parts to their corresponding element names
   private Map<String, QName> messagePartToElementMap = new HashMap<String, QName>();

   // Map of <ns,URL> for schemalocation keyed by namespace
   private Map<String, URL> schemaLocationsMap = new HashMap<String, URL>();

   private LinkedHashMap<QName, Binding> allBindings;

   private LinkedHashMap<QName, Binding> portTypeBindings;

   // Temporary files used by this reader.
   private List<File> tempFiles = new ArrayList<File>();

   // SWA handling
   private Map<QName, List<String>> skippedSWAParts = new HashMap<QName, List<String>>();

   // It is generally unsafe to use the getter for a top level element on another top level element.
   // For examples Binding.getPortType() returns a PortType which might might be undefined
   // The lists below only contain "defined" top level elements
   private Map<String, List<Service>> servicesByNamespace = new HashMap<String, List<Service>>();
   private Map<String, List<Binding>> bindingsByNamespace = new HashMap<String, List<Binding>>();
   private Map<String, List<PortType>> portTypesByNamespace = new HashMap<String, List<PortType>>();
   private Map<String, List<Message>> messagesByNamespace = new HashMap<String, List<Message>>();
   
   private Set<Definition> importedDefinitions = new HashSet<Definition>();

   /**
    * Takes a WSDL11 Definition element and converts into
    * our object graph that has been developed for WSDL20
    *
    * @param srcWsdl The src WSDL11 definition
    * @param wsdlLoc The source location, if null we cannot process imports or includes
    */
   public WSDLDefinitions processDefinition(Definition srcWsdl, URL wsdlLoc) throws IOException, WSDLException
   {
      log.trace("processDefinition: " + wsdlLoc);

      destWsdl = new WSDLDefinitions();
      destWsdl.setWsdlTypes(new XSModelTypes());
      destWsdl.setWsdlOneOneDefinition(srcWsdl);
      destWsdl.setWsdlNamespace(Constants.NS_WSDL11);

      processNamespaces(srcWsdl);
      processTopLevelElements(srcWsdl);
      processTypes(srcWsdl, wsdlLoc);
      processUnknownExtensibilityElements(srcWsdl, destWsdl);
      processServices(srcWsdl);

      if (getAllDefinedBindings(srcWsdl).size() != destWsdl.getBindings().length)
         processUnreachableBindings(srcWsdl);

      cleanupTemporaryFiles();

      return destWsdl;
   }

   private void processTopLevelElements(Definition srcWsdl)
   {
      String targetNS = srcWsdl.getTargetNamespace();
      importedDefinitions.add(srcWsdl);

      // Messages
      Collection<Message> messages = srcWsdl.getMessages().values();
      for (Message message : messages)
      {
         List<Message> list = messagesByNamespace.get(targetNS);
         if (list == null)
         {
            list = new ArrayList<Message>();
            messagesByNamespace.put(targetNS, list);
         }
         if (message.isUndefined() == false)
            list.add(message);
      }

      // PortTypes
      Collection<PortType> portTypes = srcWsdl.getPortTypes().values();
      for (PortType portType : portTypes)
      {
         List<PortType> list = portTypesByNamespace.get(targetNS);
         if (list == null)
         {
            list = new ArrayList<PortType>();
            portTypesByNamespace.put(targetNS, list);
         }
         if (portType.isUndefined() == false)
            list.add(portType);
      }

      // Bindings
      Collection<Binding> bindings = srcWsdl.getBindings().values();
      for (Binding binding : bindings)
      {
         List<Binding> list = bindingsByNamespace.get(targetNS);
         if (list == null)
         {
            list = new ArrayList<Binding>();
            bindingsByNamespace.put(targetNS, list);
         }
         if (binding.isUndefined() == false)
            list.add(binding);
      }

      // Services
      Collection<Service> services = srcWsdl.getServices().values();
      for (Service service : services)
      {
         List<Service> list = servicesByNamespace.get(targetNS);
         if (list == null)
         {
            list = new ArrayList<Service>();
            servicesByNamespace.put(targetNS, list);
         }
         list.add(service);
      }

      // Imports
      Collection<List<Import>> importLists = srcWsdl.getImports().values();
      for (List<Import> imports : importLists)
      {
         for (Import imp : imports)
         {
            Definition impWsdl = imp.getDefinition();
            if (!importedDefinitions.contains(impWsdl))
            {
               processTopLevelElements(impWsdl);
            }
         }
      }
   }

   private void cleanupTemporaryFiles()
   {
      for (File current : tempFiles)
      {
         current.delete();
      }
   }

   // process all bindings not within service separetly
   private void processUnreachableBindings(Definition srcWsdl) throws WSDLException
   {
      log.trace("processUnreachableBindings");

      Iterator it = getAllDefinedBindings(srcWsdl).values().iterator();
      while (it.hasNext())
      {
         Binding srcBinding = (Binding)it.next();
         QName srcQName = srcBinding.getQName();

         WSDLBinding destBinding = destWsdl.getBinding(srcQName);
         if (destBinding == null)
         {
            processBinding(srcWsdl, srcBinding);
         }
      }
   }

   private void processNamespaces(Definition srcWsdl)
   {
      String targetNS = srcWsdl.getTargetNamespace();
      destWsdl.setTargetNamespace(targetNS);

      // Copy wsdl namespaces
      Map nsMap = srcWsdl.getNamespaces();
      Iterator iter = nsMap.entrySet().iterator();
      while (iter.hasNext())
      {
         Map.Entry entry = (Map.Entry)iter.next();
         String prefix = (String)entry.getKey();
         String nsURI = (String)entry.getValue();
         destWsdl.registerNamespaceURI(nsURI, prefix);
      }
   }

   private void processUnknownExtensibilityElements(ElementExtensible src, Extendable dest) throws WSDLException
   {
      List extElements = src.getExtensibilityElements();
      for (int i = 0; i < extElements.size(); i++)
      {
         ExtensibilityElement extElement = (ExtensibilityElement)extElements.get(i);
         if (extElement instanceof UnknownExtensibilityElement)
         {
            UnknownExtensibilityElement uee = (UnknownExtensibilityElement)extElement;
            boolean understood = false;
            understood = understood || processPolicyElements(uee, dest);
            understood = understood || processUseAddressing(uee, dest);
            //add processing of further extensibility element types below
            
            if (!understood)
            {
               processNotUnderstoodExtesibilityElement(uee, dest);
            }
         }
      }
   }

   /**
    * Process the provided extensibility element looking for policies or policy references.
    * Returns true if the provided element is policy related, false otherwise.
    * 
    * @param extElement
    * @param dest
    * @return
    */
   private boolean processPolicyElements(UnknownExtensibilityElement extElement, Extendable dest)
   {
      boolean result = false;
      Element srcElement = extElement.getElement();
      if (Constants.URI_WS_POLICY.equals(srcElement.getNamespaceURI()))
      {
         //copy missing namespaces from the source element to our element
         Element element = (Element)srcElement.cloneNode(true);
         copyMissingNamespaceDeclarations(element, srcElement);
         if (element.getLocalName().equals("Policy"))
         {
            WSDLExtensibilityElement el = new WSDLExtensibilityElement(Constants.WSDL_ELEMENT_POLICY, element);
            el.setRequired("true".equalsIgnoreCase(element.getAttribute("required")));
            dest.addExtensibilityElement(el);
            result = true;
         }
         else if (element.getLocalName().equals("PolicyReference"))
         {
            WSDLExtensibilityElement el = new WSDLExtensibilityElement(Constants.WSDL_ELEMENT_POLICYREFERENCE, element);
            el.setRequired("true".equalsIgnoreCase(element.getAttribute("required")));
            dest.addExtensibilityElement(el);
            result = true;
         }
      }
      return result;
   }
   
   /**
    * Process the provided extensibility element looking for UsingAddressing.
    * Returns true if the provided element is UsingAddressing, false otherwise.
    * 
    * @param extElement
    * @param dest
    * @return
    */
   private boolean processUseAddressing(UnknownExtensibilityElement extElement, Extendable dest)
   {
      log.warn("UsingAddressing extensibility element not supported yet.");
      return false;
   }
   
   private void processNotUnderstoodExtesibilityElement(UnknownExtensibilityElement extElement, Extendable dest)
   {
      Element element = (Element)extElement.getElement().cloneNode(true);
      WSDLExtensibilityElement notUnderstoodElement = new WSDLExtensibilityElement("notUnderstoodExtensibilityElement", element);
      notUnderstoodElement.setRequired("true".equalsIgnoreCase(element.getAttributeNS(Constants.NS_WSDL11, "required")));
      dest.addNotUnderstoodExtElement(notUnderstoodElement);
   }

   private void processTypes(Definition srcWsdl, URL wsdlLoc) throws IOException, WSDLException
   {
      log.trace("BEGIN processTypes: " + wsdlLoc);

      WSDLTypes destTypes = destWsdl.getWsdlTypes();

      Types srcTypes = srcWsdl.getTypes();
      if (srcTypes != null && srcTypes.getExtensibilityElements().size() > 0)
      {
         List extElements = srcTypes.getExtensibilityElements();
         int len = extElements.size();

         for (int i = 0; i < len; i++)
         {
            ExtensibilityElement extElement = (ExtensibilityElement)extElements.get(i);

            Element domElement;
            if (extElement instanceof Schema)
            {
               domElement = ((Schema)extElement).getElement();
            }
            else if (extElement instanceof UnknownExtensibilityElement)
            {
               domElement = ((UnknownExtensibilityElement)extElement).getElement();
            }
            else
            {
               throw new WSDLException(WSDLException.OTHER_ERROR, "Unsupported extensibility element: " + extElement);
            }

            Element domElementClone = (Element)domElement.cloneNode(true);
            copyParentNamespaceDeclarations(domElementClone, domElement);

            String localname = domElementClone.getLocalName();
            try
            {
               List<URL> published = new LinkedList<URL>();
               if ("import".equals(localname))
               {
                  processSchemaImport(destTypes, wsdlLoc, domElementClone, published);
               }
               else if ("schema".equals(localname))
               {
                  processSchemaInclude(destTypes, wsdlLoc, domElementClone, published);
               }
               else
               {
                  throw new IllegalArgumentException("Unsuported schema element: " + localname);
               }
               published.clear();
               published = null;
            }
            catch (IOException e)
            {
               throw new WSDLException(WSDLException.OTHER_ERROR, "Cannot extract schema definition", e);
            }
         }

         if (len > 0)
         {
            JavaToXSD jxsd = new JavaToXSD();
            JBossXSModel xsmodel = jxsd.parseSchema(schemaLocationsMap);
            WSDLUtils.addSchemaModel(destTypes, destWsdl.getTargetNamespace(), xsmodel);
         }
      }
      else
      {
         log.trace("Empty wsdl types element, processing imports");
         Iterator it = srcWsdl.getImports().values().iterator();
         while (it.hasNext())
         {
            List<Import> srcImports = (List<Import>)it.next();
            for (Import srcImport : srcImports)
            {
               Definition impDefinition = srcImport.getDefinition();
               String impLoc = impDefinition.getDocumentBaseURI();
               processTypes(impDefinition, new URL(impLoc));
            }
         }
      }

      log.trace("END processTypes: " + wsdlLoc + "\n" + destTypes);
   }

   private void copyParentNamespaceDeclarations(Element destElement, Element srcElement)
   {
      Node parent = srcElement.getParentNode();
      while (parent != null)
      {
         if (parent.hasAttributes())
         {
            NamedNodeMap attributes = parent.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++)
            {
               Attr attr = (Attr)attributes.item(i);
               String name = attr.getName();
               String value = attr.getValue();
               if (name.startsWith("xmlns:") && destElement.hasAttribute(name) == false)
                  destElement.setAttribute(name, value);
            }
         }
         parent = parent.getParentNode();
      }
   }

   private void copyMissingNamespaceDeclarations(Element destElement, Element srcElement)
   {
      String prefix = destElement.getPrefix();
      String nsUri;
      try
      {
         nsUri = DOMUtils.getElementQName(destElement).getNamespaceURI();
      }
      catch (IllegalArgumentException e)
      {
         nsUri = null;
      }
      if (prefix != null && nsUri == null)
      {
         destElement.setAttributeNS(Constants.NS_XMLNS, "xmlns:" + prefix, srcElement.lookupNamespaceURI(prefix));

      }

      NamedNodeMap attributes = destElement.getAttributes();
      for (int i = 0; i < attributes.getLength(); i++)
      {
         Attr attr = (Attr)attributes.item(i);
         String attrPrefix = attr.getPrefix();
         if (attrPrefix != null && !attr.getName().startsWith("xmlns") && destElement.lookupNamespaceURI(attrPrefix) == null)
         {
            destElement.setAttributeNS(Constants.NS_XMLNS, "xmlns:" + attrPrefix, srcElement.lookupNamespaceURI(attrPrefix));
         }
      }
      NodeList childrenList = destElement.getChildNodes();
      for (int i = 0; i < childrenList.getLength(); i++)
      {
         Node node = childrenList.item(i);
         if (node instanceof Element)
            copyMissingNamespaceDeclarations((Element)node, srcElement);
      }
   }

   private void processSchemaImport(WSDLTypes types, URL wsdlLoc, Element importEl, List<URL> published) throws IOException, WSDLException
   {
      if (wsdlLoc == null)
         throw new IllegalArgumentException("Cannot process import, parent location not set");

      log.trace("processSchemaImport: " + wsdlLoc);

      String location = getOptionalAttribute(importEl, "schemaLocation");
      if (location == null)
         throw new IllegalArgumentException("schemaLocation is null for xsd:import");

      URL locationURL = getLocationURL(wsdlLoc, location);
      Element rootElement = DOMUtils.parse(new ResourceURL(locationURL).openStream());
      if (!published.contains(locationURL))
      {
         published.add(locationURL);
         URL newloc = processSchemaInclude(types, locationURL, rootElement,  published);
         if (newloc != null)
            importEl.setAttribute("schemaLocation", newloc.toExternalForm());
      }
   }

   private URL processSchemaInclude(WSDLTypes types, URL wsdlLoc, Element schemaEl, List<URL> published) throws IOException, WSDLException
   {
      if (wsdlLoc == null)
         throw new IllegalArgumentException("Cannot process iclude, parent location not set");

      File tmpFile = null;
      if (wsdlLoc == null)
         throw new IllegalArgumentException("Cannot process include, parent location not set");

      log.trace("processSchemaInclude: " + wsdlLoc);

      String schemaPrefix = schemaEl.getPrefix();

      String importTag = (schemaPrefix == null) ? "import" : schemaPrefix + ":import";
      Element importElement = schemaEl.getOwnerDocument().createElementNS(Constants.NS_SCHEMA_XSD, importTag);
      importElement.setAttribute("namespace", Constants.URI_SOAP11_ENC);
      schemaEl.insertBefore(importElement, DOMUtils.getFirstChildElement(schemaEl));

      // Handle schema includes
      Iterator it = DOMUtils.getChildElements(schemaEl, new QName(Constants.NS_SCHEMA_XSD, "include"));
      while (it.hasNext())
      {
         Element includeEl = (Element)it.next();
         String location = getOptionalAttribute(includeEl, "schemaLocation");
         if (location == null)
            throw new IllegalArgumentException("schemaLocation is null for xsd:include");

         URL locationURL = getLocationURL(wsdlLoc, location);
         Element rootElement = DOMUtils.parse(new ResourceURL(locationURL).openStream());
         if (!published.contains(locationURL))
         {
            published.add(locationURL);
            URL newloc = processSchemaInclude(types, locationURL, rootElement, published);
            if (newloc != null)
               includeEl.setAttribute("schemaLocation", newloc.toExternalForm());
         }
      }

      String targetNS = getOptionalAttribute(schemaEl, "targetNamespace");
      if (targetNS != null)
      {
         log.trace("processSchemaInclude: [targetNS=" + targetNS + ",parentURL=" + wsdlLoc + "]");

         tmpFile = SchemaUtils.getSchemaTempFile(targetNS);
         tempFiles.add(tmpFile);

         FileWriter fwrite = new FileWriter(tmpFile);
         new DOMWriter(fwrite).setPrettyprint(true).print(schemaEl);
         fwrite.close();

         schemaLocationsMap.put(targetNS, tmpFile.toURL());
      }

      // schema elements that have no target namespace are skipped
      //
      //  <xsd:schema>
      //    <xsd:import namespace="http://org.jboss.webservice/example/types" schemaLocation="Hello.xsd"/>
      //    <xsd:import namespace="http://org.jboss.webservice/example/types/arrays/org/jboss/test/webservice/admindevel" schemaLocation="subdir/HelloArr.xsd"/>
      //  </xsd:schema>
      if (targetNS == null)
      {
         log.trace("Schema element without target namespace in: " + wsdlLoc);
      }

      handleSchemaImports(schemaEl, wsdlLoc);

      return tmpFile != null ? tmpFile.toURL() : null;
   }

   private void handleSchemaImports(Element schemaEl, URL parentURL) throws WSDLException, IOException
   {
      if (parentURL == null)
         throw new IllegalArgumentException("Cannot process import, parent location not set");

      Iterator it = DOMUtils.getChildElements(schemaEl, new QName(Constants.NS_SCHEMA_XSD, "import"));
      while (it.hasNext())
      {
         Element includeEl = (Element)it.next();
         String schemaLocation = getOptionalAttribute(includeEl, "schemaLocation");
         String namespace = getOptionalAttribute(includeEl, "namespace");

         log.trace("handleSchemaImport: [namespace=" + namespace + ",schemaLocation=" + schemaLocation + "]");

         // Skip, let the entity resolver resolve these
         if (namespace != null && schemaLocation != null)
         {
            URL currLoc = getLocationURL(parentURL, schemaLocation);
            if (schemaLocationsMap.get(namespace) == null)
            {
               schemaLocationsMap.put(namespace, currLoc);
               
               // Recursively handle schema imports
               Element importedSchema = null;
               if (entityResolver.getEntityMap().containsKey(namespace))
               {
                  try
                  {
                     importedSchema = DOMUtils.parse(entityResolver.resolveEntity(namespace, namespace).getByteStream());
                  }
                  catch (SAXException se)
                  {
                     log.error(se.getMessage(), se);
                  }
               }
               if (importedSchema == null)
               {
                  importedSchema = DOMUtils.parse(currLoc.openStream());
               }
               handleSchemaImports(importedSchema, currLoc);
            }
         }
         else
         {
            log.trace("Skip schema import: [namespace=" + namespace + ",schemaLocation=" + schemaLocation + "]");
         }
      }
   }

   private URL getLocationURL(URL parentURL, String location) throws MalformedURLException, WSDLException
   {
      log.trace("getLocationURL: [location=" + location + ",parent=" + parentURL + "]");

      URL locationURL = null;
      try
      {
         locationURL = new URL(location);
      }
      catch (MalformedURLException e)
      {
         // ignore malformed URL
      }

      if (locationURL == null)
      {
         if (location.startsWith("/"))
            location = location.substring(1);

         String path = parentURL.toExternalForm();
         path = path.substring(0, path.lastIndexOf("/"));

         while (location.startsWith("../"))
         {
            path = path.substring(0, path.lastIndexOf("/"));
            location = location.substring(3);
         }

         locationURL = new URL(path + "/" + location);
      }

      log.trace("Modified schemaLocation: " + locationURL);
      return locationURL;
   }

   private void processPortType(Definition srcWsdl, PortType srcPortType, WSDLBinding destBinding) throws WSDLException
   {
      log.trace("processPortType: " + srcPortType.getQName());

      QName qname = srcPortType.getQName();
      if (destWsdl.getInterface(qname) == null)
      {
         WSDLInterface destInterface = new WSDLInterface(destWsdl, qname);

         //policy extensions
         QName policyURIsProp = (QName)srcPortType.getExtensionAttribute(Constants.WSDL_ATTRIBUTE_WSP_POLICYURIS);
         if (policyURIsProp != null && !"".equalsIgnoreCase(policyURIsProp.getLocalPart()))
         {
            destInterface.addProperty(new WSDLProperty(Constants.WSDL_PROPERTY_POLICYURIS, policyURIsProp.getLocalPart()));
         }

         // eventing extensions
         QName eventSourceProp = (QName)srcPortType.getExtensionAttribute(Constants.WSDL_ATTRIBUTE_WSE_EVENTSOURCE);
         if (eventSourceProp != null && eventSourceProp.getLocalPart().equals(Boolean.TRUE.toString()))
         {
            destInterface.addProperty(new WSDLProperty(Constants.WSDL_PROPERTY_EVENTSOURCE, eventSourceProp.getLocalPart()));
         }
         
         // documentation
         Element documentationElement = srcPortType.getDocumentationElement();
         if (documentationElement != null && documentationElement.getTextContent() != null)
         {
            destInterface.setDocumentationElement(new WSDLDocumentation(documentationElement.getTextContent()));
         }

         destWsdl.addInterface(destInterface);

         processPortTypeOperations(srcWsdl, destInterface, srcPortType, destBinding);
      }
   }

   private void processPortTypeOperations(Definition srcWsdl, WSDLInterface destInterface, PortType srcPortType, WSDLBinding destBinding) throws WSDLException
   {
      Iterator itOperations = srcPortType.getOperations().iterator();
      while (itOperations.hasNext())
      {
         Operation srcOperation = (Operation)itOperations.next();

         WSDLInterfaceOperation destOperation = new WSDLInterfaceOperation(destInterface, srcOperation.getName());
         processUnknownExtensibilityElements(srcOperation, destOperation);
         destOperation.setStyle(getOperationStyle(srcWsdl, srcPortType, srcOperation));

         if (srcOperation.getStyle() != null && false == OperationType.NOTIFICATION.equals(srcOperation.getStyle()))
         {
            processPortTypeOperationInput(srcWsdl, srcOperation, destOperation, srcPortType, destBinding);
         }

         processPortTypeOperationOutput(srcWsdl, srcOperation, destOperation, srcPortType, destBinding);
         processPortTypeOperationFaults(srcOperation, destOperation, destInterface, destBinding);
         
         // documentation
         Element documentationElement = srcOperation.getDocumentationElement();
         if (documentationElement != null && documentationElement.getTextContent() != null)
         {
            destOperation.setDocumentationElement(new WSDLDocumentation(documentationElement.getTextContent()));
         }

         destInterface.addOperation(destOperation);
      }
   }

   private void processPortTypeOperationInput(Definition srcWsdl, Operation srcOperation, WSDLInterfaceOperation destOperation, PortType srcPortType,
         WSDLBinding destBinding) throws WSDLException
   {
      Input srcInput = srcOperation.getInput();
      if (srcInput != null)
      {
         Message srcMessage = srcInput.getMessage();
         if (srcMessage == null)
            throw new WSDLException(WSDLException.INVALID_WSDL, "Cannot find input message on operation " + srcOperation.getName() + " on port type: "
                  + srcPortType.getQName());

         log.trace("processOperationInput: " + srcMessage.getQName());

         QName wsaAction = (QName)srcInput.getExtensionAttribute(Constants.WSDL_ATTRIBUTE_WSA_ACTION);
         if (wsaAction != null)
            destOperation.addProperty(new WSDLProperty(Constants.WSDL_PROPERTY_ACTION_IN, wsaAction.getLocalPart()));

         List<String> paramOrder = (List<String>)srcOperation.getParameterOrdering();
         if (paramOrder != null)
         {
            for (String name : paramOrder)
            {
               if (srcMessage.getPart(name) != null)
                  destOperation.addRpcSignatureItem(new WSDLRPCSignatureItem(name));
            }
         }

         WSDLInterfaceOperationInput rpcInput = new WSDLInterfaceOperationInput(destOperation);
         for (Part srcPart : (List<Part>)srcMessage.getOrderedParts(paramOrder))
         {
            // Skip SWA attachment parts
            if (ignorePart(srcPortType, srcPart))
               continue;

            if (Constants.URI_STYLE_DOCUMENT == destOperation.getStyle())
            {
               WSDLInterfaceOperationInput destInput = new WSDLInterfaceOperationInput(destOperation);
               QName elementName = messagePartToElementName(srcMessage, srcPart, destOperation, destBinding);
               destInput.setElement(elementName);

               //Lets remember the Message name
               destInput.setMessageName(srcMessage.getQName());
               destOperation.addProperty(new WSDLProperty(Constants.WSDL_PROPERTY_MESSAGE_NAME_IN, srcMessage.getQName().getLocalPart()));

               destInput.setPartName(srcPart.getName());
               processUnknownExtensibilityElements(srcMessage, destInput);

               destOperation.addInput(destInput);
            }
            else
            {
               // If we don't have a type then we aren't a valid RPC parameter
               // This could happen on a header element, in which case the
               // binding will pick it up
               QName xmlType = srcPart.getTypeName();
               if (xmlType != null)
               {
                  rpcInput.addChildPart(new WSDLRPCPart(srcPart.getName(), destWsdl.registerQName(xmlType)));
               }
               else
               {
                  messagePartToElementName(srcMessage, srcPart, destOperation, destBinding);
               }
            }
         }
         if (Constants.URI_STYLE_RPC == destOperation.getStyle())
         {
            // This is really a place holder, but also the actual value used in
            // WSDL 2.0 RPC bindings
            rpcInput.setElement(destOperation.getName());
            rpcInput.setMessageName(srcMessage.getQName());
            processUnknownExtensibilityElements(srcMessage, rpcInput);
            destOperation.addInput(rpcInput);
         }
      }
   }

   private boolean ignorePart(PortType srcPortType, Part srcPart)
   {

      boolean canBeSkipped = false;
      QName parentName = srcPortType.getQName();
      if (skippedSWAParts.containsKey(parentName))
      {
         if (skippedSWAParts.get(parentName).contains(srcPart.getName()))
         {
            log.trace("Skip attachment part: " + parentName + "->" + srcPart.getName());
            canBeSkipped = true;
         }
      }

      return canBeSkipped;
   }

   private void processPortTypeOperationOutput(Definition srcWsdl, Operation srcOperation, WSDLInterfaceOperation destOperation, PortType srcPortType,
         WSDLBinding destBinding) throws WSDLException
   {
      Output srcOutput = srcOperation.getOutput();
      if (srcOutput == null)
      {
         destOperation.setPattern(Constants.WSDL20_PATTERN_IN_ONLY);
         return;
      }

      Message srcMessage = srcOutput.getMessage();
      if (srcMessage == null)
         throw new WSDLException(WSDLException.INVALID_WSDL, "Cannot find output message on operation " + srcOperation.getName() + " on port type: "
               + srcPortType.getQName());

      log.trace("processOperationOutput: " + srcMessage.getQName());

      destOperation.setPattern(Constants.WSDL20_PATTERN_IN_OUT);
      QName wsaAction = (QName)srcOutput.getExtensionAttribute(Constants.WSDL_ATTRIBUTE_WSA_ACTION);
      if (wsaAction != null)
         destOperation.addProperty(new WSDLProperty(Constants.WSDL_PROPERTY_ACTION_OUT, wsaAction.getLocalPart()));

      List<String> paramOrder = (List<String>)srcOperation.getParameterOrdering();
      if (paramOrder != null)
      {
         for (String name : paramOrder)
         {
            if (srcMessage.getPart(name) != null)
            {
               WSDLRPCSignatureItem item = destOperation.getRpcSignatureitem(name);
               if (item != null)
                  item.setDirection(Direction.INOUT);
               else destOperation.addRpcSignatureItem(new WSDLRPCSignatureItem(name, Direction.OUT));
            }
         }
      }

      WSDLInterfaceOperationOutput rpcOutput = new WSDLInterfaceOperationOutput(destOperation);
      for (Part srcPart : (List<Part>)srcMessage.getOrderedParts(null))
      {
         // Skip SWA attachment parts
         if (ignorePart(srcPortType, srcPart))
            continue;

         if (Constants.URI_STYLE_DOCUMENT == destOperation.getStyle())
         {
            WSDLInterfaceOperationOutput destOutput = new WSDLInterfaceOperationOutput(destOperation);

            QName elementName = messagePartToElementName(srcMessage, srcPart, destOperation, destBinding);
            destOutput.setElement(elementName);

            // Lets remember the Message name
            destOutput.setMessageName(srcMessage.getQName());
            destOperation.addProperty(new WSDLProperty(Constants.WSDL_PROPERTY_MESSAGE_NAME_OUT, srcMessage.getQName().getLocalPart()));

            // Remember the original part name
            destOutput.setPartName(srcPart.getName());

            destOperation.addOutput(destOutput);
         }
         else
         {
            // If we don't have a type then we aren't a valid RPC parameter
            // This could happen on a header element, in which case the
            // binding will pick it up
            QName xmlType = srcPart.getTypeName();
            if (xmlType != null)
               rpcOutput.addChildPart(new WSDLRPCPart(srcPart.getName(), destWsdl.registerQName(xmlType)));
            else messagePartToElementName(srcMessage, srcPart, destOperation, destBinding);
         }
      }

      if (Constants.URI_STYLE_RPC == destOperation.getStyle())
      {
         // This is really a place holder, but also the actual value used in
         // WSDL 2.0 RPC bindings
         QName name = destOperation.getName();
         rpcOutput.setElement(new QName(name.getNamespaceURI(), name.getLocalPart() + "Response"));
         rpcOutput.setMessageName(srcMessage.getQName());
         destOperation.addOutput(rpcOutput);
      }
   }

   private void processPortTypeOperationFaults(Operation srcOperation, WSDLInterfaceOperation destOperation, WSDLInterface destInterface, WSDLBinding destBinding)
         throws WSDLException
   {

      Map faults = srcOperation.getFaults();
      Iterator itFaults = faults.values().iterator();
      while (itFaults.hasNext())
      {
         Fault srcFault = (Fault)itFaults.next();
         processOperationFault(destOperation, destInterface, srcFault);
      }
   }

   private void processOperationFault(WSDLInterfaceOperation destOperation, WSDLInterface destInterface, Fault srcFault) throws WSDLException
   {
      String faultName = srcFault.getName();
      log.trace("processOperationFault: " + faultName);

      WSDLInterfaceFault destFault = new WSDLInterfaceFault(destInterface, faultName);
      Message message = srcFault.getMessage();
      QName messageName = message.getQName();

      Map partsMap = message.getParts();
      if (partsMap.size() != 1)
         throw new WSDLException(WSDLException.INVALID_WSDL, "Unsupported number of fault parts in message " + messageName);

      Part part = (Part)partsMap.values().iterator().next();
      QName xmlName = part.getElementName();

      if (xmlName != null)
      {
         destFault.setElement(xmlName);
      }
      else
      {
         destFault.setElement(messageName);
         log.warn("Unsupported fault message part in message: " + messageName);
      }

      // Add the fault to the interface
      destInterface.addFault(destFault);

      // Add the fault refererence to the operation
      WSDLInterfaceOperationOutfault opOutFault = new WSDLInterfaceOperationOutfault(destOperation);
      opOutFault.setRef(destFault.getName());
      destOperation.addOutfault(opOutFault);
   }

   /** Translate the message part name into an XML element name.
    */
   private QName messagePartToElementName(Message srcMessage, Part srcPart, WSDLInterfaceOperation destOperation, WSDLBinding destBinding) throws WSDLException
   {
      QName xmlName = null;

      // R2306 A wsdl:message in a DESCRIPTION MUST NOT specify both type and element attributes on the same wsdl:part
      if (srcPart.getTypeName() != null && srcPart.getElementName() != null)
         throw new WSDLException(WSDLException.INVALID_WSDL, "Message parts must not define an element name and type name: " + srcMessage.getQName());

      String bindingType = destBinding.getType();
      if (Constants.NS_HTTP.equals(bindingType))
      {
         xmlName = new QName(srcPart.getName());
      }
         
      String style = destOperation.getStyle();
      if (xmlName == null && Constants.URI_STYLE_RPC.equals(style))
      {
         // R2203 An rpc-literal binding in a DESCRIPTION MUST refer, in its soapbind:body element(s),
         // only to wsdl:part element(s) that have been defined using the type attribute.
         if (srcPart.getName() == null)
            throw new WSDLException(WSDLException.INVALID_WSDL, "RPC style message parts must define a typy name: " + srcMessage.getQName());

         // <part name="param" element="tns:SomeType" />
         // Headers do have an element name even in rpc
         xmlName = srcPart.getElementName();

         // <part name="param" type="xsd:string" />
         if (xmlName == null)
            xmlName = new QName(srcPart.getName());
      }

      if (xmlName == null && Constants.URI_STYLE_DOCUMENT.equals(style))
      {
         // R2204 A document-literal binding in a DESCRIPTION MUST refer, in each of its soapbind:body element(s),
         // only to wsdl:part element(s) that have been defined using the element attribute
         // [hb] do this only for non swa porttypes
         if (srcPart.getElementName() == null)
            throw new WSDLException(WSDLException.INVALID_WSDL, "Document style message parts must define an element name: " + srcMessage.getQName());

         // <part name="param" element="tns:SomeType" />
         xmlName = srcPart.getElementName();
      }

      if (xmlName == null)
         throw new IllegalStateException("Cannot name for wsdl part: " + srcPart);

      xmlName = destWsdl.registerQName(xmlName);
      String key = srcMessage.getQName() + "->" + srcPart.getName();
      messagePartToElementMap.put(key, xmlName);

      return xmlName;
   }

   private BindingOperation getBindingOperation(Definition srcWsdl, PortType srcPortType, Operation srcOperation) throws WSDLException
   {
      Binding srcBinding = getPortTypeBindings(srcWsdl).get(srcPortType.getQName());

      if (srcBinding == null)
         throw new WSDLException(WSDLException.INVALID_WSDL, "Cannot find binding for: " + srcPortType.getQName());

      String srcOperationName = srcOperation.getName();
      BindingOperation srcBindingOperation = srcBinding.getBindingOperation(srcOperationName, null, null);
      if (srcBindingOperation == null)
         throw new WSDLException(WSDLException.INVALID_WSDL, "Cannot find binding operation for: " + srcOperationName);
      return srcBindingOperation;
   }

   private String getOperationStyle(Definition srcWsdl, PortType srcPortType, Operation srcOperation) throws WSDLException
   {
      Binding srcBinding = getPortTypeBindings(srcWsdl).get(srcPortType.getQName());
      BindingOperation srcBindingOperation = getBindingOperation(srcWsdl, srcPortType, srcOperation);

      String operationStyle = null;
      List<ExtensibilityElement> extList = srcBindingOperation.getExtensibilityElements();
      for (ExtensibilityElement extElement : extList)
      {
         QName elementType = extElement.getElementType();
         if (extElement instanceof SOAPOperation)
         {
            SOAPOperation soapOp = (SOAPOperation)extElement;
            operationStyle = soapOp.getStyle();
         }
         else if (extElement instanceof SOAP12Operation)
         {
            SOAP12Operation soapOp = (SOAP12Operation)extElement;
            operationStyle = soapOp.getStyle();
         }
      }

      if (operationStyle == null)
      {
         for (ExtensibilityElement extElement : (List<ExtensibilityElement>)srcBinding.getExtensibilityElements())
         {
            QName elementType = extElement.getElementType();
            if (extElement instanceof SOAPBinding)
            {
               SOAPBinding soapBinding = (SOAPBinding)extElement;
               operationStyle = soapBinding.getStyle();
            }
            else if (extElement instanceof SOAP12Binding)
            {
               SOAP12Binding soapBinding = (SOAP12Binding)extElement;
               operationStyle = soapBinding.getStyle();
            }
         }
      }

      return ("rpc".equals(operationStyle)) ? Constants.URI_STYLE_RPC : Constants.URI_STYLE_DOCUMENT;
   }

   private WSDLBinding processBinding(Definition srcWsdl, Binding srcBinding) throws WSDLException
   {
      QName srcBindingQName = srcBinding.getQName();
      log.trace("processBinding: " + srcBindingQName);

      WSDLBinding destBinding = destWsdl.getBinding(srcBindingQName);
      if (destBinding == null)
      {
         PortType srcPortType = getDefinedPortType(srcBinding);

         String bindingType = null;
         List<ExtensibilityElement> extList = srcBinding.getExtensibilityElements();
         for (ExtensibilityElement extElement : extList)
         {
            QName elementType = extElement.getElementType();
            if (extElement instanceof SOAPBinding)
            {
               bindingType = Constants.NS_SOAP11;
            }
            else if (extElement instanceof SOAP12Binding)
            {
               bindingType = Constants.NS_SOAP12;
            }
            else if (extElement instanceof HTTPBinding)
            {
               bindingType = Constants.NS_HTTP;
            }
            else if ("binding".equals(elementType.getLocalPart()))
            {
               log.warn("Unsupported binding: " + elementType);
               bindingType = elementType.getNamespaceURI();
            }
         }

         if (bindingType == null)
            throw new WSDLException(WSDLException.INVALID_WSDL, "Cannot obtain binding type for: " + srcBindingQName);

         if (Constants.NS_SOAP11.equals(bindingType) || Constants.NS_SOAP12.equals(bindingType) || Constants.NS_HTTP.equals(bindingType))
         {
            destBinding = new WSDLBinding(destWsdl, srcBindingQName);
            destBinding.setInterfaceName(srcPortType.getQName());
            destBinding.setType(bindingType);
            processUnknownExtensibilityElements(srcBinding, destBinding);
            destWsdl.addBinding(destBinding);

            preProcessSWAParts(srcBinding, srcWsdl);
            processPortType(srcWsdl, srcPortType, destBinding);

            String bindingStyle = Style.getDefaultStyle().toString();
            for (ExtensibilityElement extElement : extList)
            {
               if (extElement instanceof SOAPBinding)
               {
                  SOAPBinding soapBinding = (SOAPBinding)extElement;
                  bindingStyle = soapBinding.getStyle();
               }
               else if (extElement instanceof SOAP12Binding)
               {
                  SOAP12Binding soapBinding = (SOAP12Binding)extElement;
                  bindingStyle = soapBinding.getStyle();
               }
            }

            processBindingOperations(srcWsdl, destBinding, srcBinding, bindingStyle);
         }
      }

      return destBinding;
   }

   /** The port might reference a binding which is defined in another wsdl
    */
   private Binding getDefinedBinding(Port srcPort) throws WSDLException
   {
      Binding srcBinding = srcPort.getBinding();
      if (srcBinding == null)
         throw new WSDLException(WSDLException.INVALID_WSDL, "Cannot find binding for port: " + srcPort.getName());

      QName srcBindingName = srcBinding.getQName();
      if (srcBinding.isUndefined())
      {
         String nsURI = srcBindingName.getNamespaceURI();
         List<Binding> bindings = bindingsByNamespace.get(nsURI);
         if (bindings == null)
            throw new WSDLException(WSDLException.INVALID_WSDL, "Cannot find bindings for namespace: " + nsURI);

         for (Binding auxBinding : bindings)
         {
            if (srcBindingName.equals(auxBinding.getQName()))
            {
               srcBinding = auxBinding;
               break;
            }
         }
      }

      return srcBinding;
   }

   /** The binding might reference a port type which is defined in another wsdl
    */
   private PortType getDefinedPortType(Binding srcBinding) throws WSDLException
   {
      QName srcBindingQName = srcBinding.getQName();

      PortType srcPortType = srcBinding.getPortType();
      if (srcPortType == null)
         throw new WSDLException(WSDLException.INVALID_WSDL, "Cannot find port type for binding: " + srcBindingQName);

      QName srcPortTypeName = srcPortType.getQName();
      if (srcPortType.isUndefined())
      {
         String nsURI = srcPortTypeName.getNamespaceURI();
         List<PortType> portTypes = portTypesByNamespace.get(nsURI);
         if (portTypes == null)
            throw new WSDLException(WSDLException.INVALID_WSDL, "Cannot find port types for namespace: " + nsURI);

         for (PortType auxPortType : portTypes)
         {
            if (srcPortTypeName.equals(auxPortType.getQName()))
            {
               srcPortType = auxPortType;
               break;
            }
         }
      }

      return srcPortType;
   }

   /**
    * Identify and mark message parts that belong to
    * an SWA binding and can be skipped when processing this WSDL
    */
   private void preProcessSWAParts(Binding srcBinding, Definition srcWsdl) throws WSDLException
   {

      Iterator opIt = srcBinding.getBindingOperations().iterator();
      while (opIt.hasNext())
      {
         BindingOperation bindingOperation = (BindingOperation)opIt.next();

         // Input
         if (bindingOperation.getBindingInput() != null)
            markSWAParts(bindingOperation.getBindingInput().getExtensibilityElements(), srcBinding, srcWsdl);

         // Output
         if (bindingOperation.getBindingOutput() != null)
            markSWAParts(bindingOperation.getBindingOutput().getExtensibilityElements(), srcBinding, srcWsdl);
      }
   }

   private void markSWAParts(List extensions, Binding srcBinding, Definition srcWsdl) throws WSDLException
   {
      Iterator extIt = extensions.iterator();
      while (extIt.hasNext())
      {
         Object o = extIt.next();
         if (o instanceof MIMEMultipartRelated)
         {

            QName portTypeName = getDefinedPortType(srcBinding).getQName();

            if (log.isTraceEnabled())
               log.trace("SWA found on portType" + portTypeName);

            MIMEMultipartRelated mrel = (MIMEMultipartRelated)o;
            Iterator mimePartIt = mrel.getMIMEParts().iterator();
            while (mimePartIt.hasNext())
            {
               MIMEPart mimePartDesc = (MIMEPart)mimePartIt.next();
               List mimePartExt = mimePartDesc.getExtensibilityElements();
               if (!mimePartExt.isEmpty() && (mimePartExt.get(0) instanceof MIMEContent))
               {
                  MIMEContent mimeContent = (MIMEContent)mimePartExt.get(0);

                  if (skippedSWAParts.get(portTypeName) == null)
                     skippedSWAParts.put(portTypeName, new ArrayList<String>());
                  skippedSWAParts.get(portTypeName).add(mimeContent.getPart());
               }
            }

            break;
         }
      }
   }

   private Map<QName, Binding> getPortTypeBindings(Definition srcWsdl) throws WSDLException
   {
      getAllDefinedBindings(srcWsdl);
      return portTypeBindings;
   }

   private Map<QName, Binding> getAllDefinedBindings(Definition srcWsdl) throws WSDLException
   {
      if (allBindings != null)
         return allBindings;

      allBindings = new LinkedHashMap<QName, Binding>();
      portTypeBindings = new LinkedHashMap<QName, Binding>();
      Map srcBindings = srcWsdl.getBindings();
      Iterator itBinding = srcBindings.values().iterator();
      while (itBinding.hasNext())
      {
         Binding srcBinding = (Binding)itBinding.next();
         allBindings.put(srcBinding.getQName(), srcBinding);
         portTypeBindings.put(getDefinedPortType(srcBinding).getQName(), srcBinding);
      }

      // Bindings not available when pulled in through <wsdl:import>
      // http://sourceforge.net/tracker/index.php?func=detail&aid=1240323&group_id=128811&atid=712792
      Iterator itService = srcWsdl.getServices().values().iterator();
      while (itService.hasNext())
      {
         Service srcService = (Service)itService.next();
         Iterator itPort = srcService.getPorts().values().iterator();
         while (itPort.hasNext())
         {
            Port srcPort = (Port)itPort.next();
            Binding srcBinding = srcPort.getBinding();
            allBindings.put(srcBinding.getQName(), srcBinding);
            portTypeBindings.put(getDefinedPortType(srcBinding).getQName(), srcBinding);
         }
      }

      return allBindings;
   }

   private void processBindingOperations(Definition srcWsdl, WSDLBinding destBinding, Binding srcBinding, String bindingStyle) throws WSDLException
   {
      Iterator it = srcBinding.getBindingOperations().iterator();
      while (it.hasNext())
      {
         BindingOperation srcBindingOperation = (BindingOperation)it.next();
         processBindingOperation(srcWsdl, destBinding, bindingStyle, srcBindingOperation);
      }
   }

   private void processBindingOperation(Definition srcWsdl, WSDLBinding destBinding, String bindingStyle, BindingOperation srcBindingOperation) throws WSDLException
   {
      String srcOperationName = srcBindingOperation.getName();
      log.trace("processBindingOperation: " + srcOperationName);

      WSDLInterface destInterface = destBinding.getInterface();
      String namespaceURI = destInterface.getName().getNamespaceURI();

      WSDLBindingOperation destBindingOperation = new WSDLBindingOperation(destBinding);
      QName refQName = new QName(namespaceURI, srcOperationName);
      destBindingOperation.setRef(refQName);
      processUnknownExtensibilityElements(srcBindingOperation, destBindingOperation);
      destBinding.addOperation(destBindingOperation);

      String opName = srcOperationName;
      WSDLInterfaceOperation destIntfOperation = destInterface.getOperation(opName);

      // Process soap:operation@soapAction, soap:operation@style
      List<ExtensibilityElement> extList = srcBindingOperation.getExtensibilityElements();
      for (ExtensibilityElement extElement : extList)
      {
         if (extElement instanceof SOAPOperation)
         {
            SOAPOperation soapOp = (SOAPOperation)extElement;
            destBindingOperation.setSOAPAction(soapOp.getSoapActionURI());
         }
         else if (extElement instanceof SOAP12Operation)
         {
            SOAP12Operation soapOp = (SOAP12Operation)extElement;
            destBindingOperation.setSOAPAction(soapOp.getSoapActionURI());
         }
      }

      BindingInput srcBindingInput = srcBindingOperation.getBindingInput();
      if (srcBindingInput != null)
      {
         processBindingInput(srcWsdl, destBindingOperation, destIntfOperation, srcBindingOperation, srcBindingInput);
      }

      BindingOutput srcBindingOutput = srcBindingOperation.getBindingOutput();
      if (srcBindingOutput != null)
      {
         processBindingOutput(srcWsdl, destBindingOperation, destIntfOperation, srcBindingOperation, srcBindingOutput);
      }
   }

   interface ReferenceCallback
   {
      void removeReference(QName element);

      void removeRPCPart(String partName);

      QName getXmlType(String partName);
   }

   private void processBindingInput(Definition srcWsdl, WSDLBindingOperation destBindingOperation, final WSDLInterfaceOperation destIntfOperation,
         final BindingOperation srcBindingOperation, BindingInput srcBindingInput) throws WSDLException
   {
      log.trace("processBindingInput");

      List<ExtensibilityElement> extList = srcBindingInput.getExtensibilityElements();
      WSDLBindingOperationInput input = new WSDLBindingOperationInput(destBindingOperation);
      processUnknownExtensibilityElements(srcBindingInput, input);
      destBindingOperation.addInput(input);

      ReferenceCallback cb = new ReferenceCallback() {
         public QName getXmlType(String partName)
         {
            return srcBindingOperation.getOperation().getInput().getMessage().getPart(partName).getTypeName();
         }

         public void removeReference(QName element)
         {
            WSDLInterfaceOperationInput destIntfInput = destIntfOperation.getInput(element);
            if (destIntfInput != null)
               destIntfOperation.removeInput(element);
         }

         public void removeRPCPart(String partName)
         {
            WSDLInterfaceOperationInput operationInput = destIntfOperation.getInput(destIntfOperation.getName());
            operationInput.removeChildPart(partName);
         }
      };

      processBindingReference(srcWsdl, destBindingOperation, destIntfOperation, extList, input, srcBindingOperation, cb);
   }

   private void processBindingOutput(Definition srcWsdl, WSDLBindingOperation destBindingOperation, final WSDLInterfaceOperation destIntfOperation,
         final BindingOperation srcBindingOperation, BindingOutput srcBindingOutput) throws WSDLException
   {
      log.trace("processBindingInput");

      List<ExtensibilityElement> extList = srcBindingOutput.getExtensibilityElements();
      WSDLBindingOperationOutput output = new WSDLBindingOperationOutput(destBindingOperation);
      destBindingOperation.addOutput(output);

      ReferenceCallback cb = new ReferenceCallback() {
         public QName getXmlType(String partName)
         {
            return srcBindingOperation.getOperation().getOutput().getMessage().getPart(partName).getTypeName();
         }

         public void removeReference(QName element)
         {
            WSDLInterfaceOperationOutput destIntfOutput = destIntfOperation.getOutput(element);
            if (destIntfOutput != null)
               destIntfOperation.removeOutput(element);
         }

         public void removeRPCPart(String partName)
         {
            QName name = destIntfOperation.getName();
            WSDLInterfaceOperationOutput operationOutput = destIntfOperation.getOutput(new QName(name.getNamespaceURI(), name.getLocalPart() + "Response"));
            operationOutput.removeChildPart(partName);
         }
      };

      processBindingReference(srcWsdl, destBindingOperation, destIntfOperation, extList, output, srcBindingOperation, cb);
   }

   private void processBindingReference(Definition srcWsdl, WSDLBindingOperation destBindingOperation, WSDLInterfaceOperation destIntfOperation,
         List<ExtensibilityElement> extList, WSDLBindingMessageReference reference, BindingOperation srcBindingOperation, ReferenceCallback callback)
         throws WSDLException
   {
      for (ExtensibilityElement extElement : extList)
      {
         if (extElement instanceof SOAPBody)
         {
            SOAPBody body = (SOAPBody)extElement;
            processEncodingStyle(body, destBindingOperation);

            // <soap:body use="encoded" namespace="http://MarshallTestW2J.org/" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"/>
            String namespaceURI = body.getNamespaceURI();
            destBindingOperation.setNamespaceURI(namespaceURI);
         }
         else if (extElement instanceof SOAP12Body)
         {
            SOAP12Body body = (SOAP12Body)extElement;
            processEncodingStyle(body, destBindingOperation);

            String namespaceURI = body.getNamespaceURI();
            destBindingOperation.setNamespaceURI(namespaceURI);
         }
         else if (extElement instanceof SOAPHeader)
         {
            SOAPHeader header = (SOAPHeader)extElement;
            QName headerMessageName = header.getMessage();
            String headerPartName = header.getPart();

            String key = headerMessageName + "->" + headerPartName;
            QName elementName = (QName)messagePartToElementMap.get(key);

            // The message may not have been reachable from a port type operation
            boolean isImplicitHeader = false;
            Message srcMessage = srcWsdl.getMessage(headerMessageName);
            if (elementName == null && srcMessage != null)
            {
               Iterator itParts = srcMessage.getParts().values().iterator();
               while (itParts.hasNext())
               {
                  Part srcPart = (Part)itParts.next();
                  String partName = srcPart.getName();
                  if (partName.equals(headerPartName))
                  {
                     isImplicitHeader = true;
                     elementName = srcPart.getElementName();
                  }
               }
            }

            if (elementName == null)
               throw new WSDLException(WSDLException.INVALID_WSDL, "Could not determine element name from header: " + key);

            WSDLSOAPHeader soapHeader = new WSDLSOAPHeader(elementName, headerPartName);
            soapHeader.setIncludeInSignature(!isImplicitHeader);
            reference.addSoapHeader(soapHeader);
            if (Constants.URI_STYLE_DOCUMENT == destIntfOperation.getStyle())
            {
               callback.removeReference(elementName);
            }
            else
            {
               // Just in case
               callback.removeRPCPart(headerPartName);
            }
         }
         else if (extElement instanceof MIMEMultipartRelated)
         {
            MIMEMultipartRelated related = (MIMEMultipartRelated)extElement;
            Iterator i = related.getMIMEParts().iterator();
            while (i.hasNext())
            {
               MIMEPart part = (MIMEPart)i.next();
               Iterator j = part.getExtensibilityElements().iterator();
               String name = null;
               String types = null;

               while (j.hasNext())
               {
                  ExtensibilityElement inner = (ExtensibilityElement)j.next();
                  if (inner instanceof MIMEContent)
                  {
                     MIMEContent content = (MIMEContent)inner;
                     name = content.getPart();
                     if (types == null)
                     {
                        types = content.getType();
                     }
                     else
                     {
                        types += "," + content.getType();
                     }
                  }
               }

               if (name != null)
               {
                  QName xmlType = callback.getXmlType(name);
                  reference.addMimePart(new WSDLMIMEPart(name, xmlType, types));
                  if (Constants.URI_STYLE_DOCUMENT == destIntfOperation.getStyle())
                  {
                     // A mime part must be defined as <part type="">
                     callback.removeReference(new QName(name));
                  }
                  else
                  {
                     callback.removeRPCPart(name);
                  }
               }
            }
         }
      }
   }

   private void processEncodingStyle(ExtensibilityElement extElement, WSDLBindingOperation destBindingOperation)
   {
      log.trace("processEncodingStyle");

      String encStyle = null;
      if (extElement instanceof SOAPBody)
      {
         SOAPBody body = (SOAPBody)extElement;
         List encStyleList = body.getEncodingStyles();
         if (encStyleList != null)
         {
            if (encStyleList.size() > 1)
               log.warn("Multiple encoding styles not supported: " + encStyleList);

            if (encStyleList.size() > 0)
            {
               encStyle = (String)encStyleList.get(0);
            }
         }
      }
      else if (extElement instanceof SOAP12Body)
      {
         SOAP12Body body = (SOAP12Body)extElement;
         encStyle = body.getEncodingStyle();
      }

      if (encStyle != null)
      {
         String setStyle = destBindingOperation.getEncodingStyle();
         if (encStyle.equals(setStyle) == false)
            log.warn("Encoding style '" + encStyle + "' not supported for: " + destBindingOperation.getRef());

         destBindingOperation.setEncodingStyle(encStyle);
      }
   }

   private void processServices(Definition srcWsdl) throws WSDLException
   {
      log.trace("BEGIN processServices: " + srcWsdl.getDocumentBaseURI());

      // Each definition needs a clear binding cache
      allBindings = null;

      if (srcWsdl.getServices().size() > 0)
      {
         Iterator it = srcWsdl.getServices().values().iterator();
         while (it.hasNext())
         {
            Service srcService = (Service)it.next();
            QName qname = srcService.getQName();
            WSDLService destService = new WSDLService(destWsdl, qname);
            processUnknownExtensibilityElements(srcService, destService);
            destWsdl.addService(destService);
            processPorts(srcWsdl, destService, srcService);
         }
      }
      else
      {
         log.trace("Empty wsdl services, processing imports");
         Iterator it = srcWsdl.getImports().values().iterator();
         while (it.hasNext())
         {
            List<Import> srcImports = (List<Import>)it.next();
            for (Import srcImport : srcImports)
            {
               Definition importDefinition = srcImport.getDefinition();
               processServices(importDefinition);
            }
         }

         // The binding cache must be clear after imports, so that undefined bindings can be located
         allBindings = null;
      }

      log.trace("END processServices: " + srcWsdl.getDocumentBaseURI());
   }

   private void processPorts(Definition srcWsdl, WSDLService destService, Service srcService) throws WSDLException
   {
      Iterator it = srcService.getPorts().values().iterator();
      while (it.hasNext())
      {
         Port srcPort = (Port)it.next();
         processPort(srcWsdl, destService, srcPort);
      }
   }

   private void processPort(Definition srcWsdl, WSDLService destService, Port srcPort) throws WSDLException
   {
      log.trace("processPort: " + srcPort.getName());

      Binding srcBinding = getDefinedBinding(srcPort);
      QName endpointName = new QName(srcWsdl.getTargetNamespace(), srcPort.getName());
      WSDLEndpoint destEndpoint = new WSDLEndpoint(destService, endpointName);
      destEndpoint.setBinding(srcBinding.getQName());
      destEndpoint.setAddress(getSOAPAddress(srcPort));
      processUnknownExtensibilityElements(srcPort, destEndpoint);

      WSDLBinding destBinding = processBinding(srcWsdl, srcBinding);
      if (destBinding != null)
         destService.addEndpoint(destEndpoint);
   }

   /** Get the endpoint address from the ports extensible element
    */
   private String getSOAPAddress(Port srcPort) throws WSDLException
   {
      String soapAddress = "dummy";

      Iterator it = srcPort.getExtensibilityElements().iterator();
      while (it.hasNext())
      {
         ExtensibilityElement extElement = (ExtensibilityElement)it.next();
         QName elementType = extElement.getElementType();

         if (extElement instanceof SOAPAddress)
         {
            SOAPAddress addr = (SOAPAddress)extElement;
            soapAddress = addr.getLocationURI();
            break;
         }
         else if (extElement instanceof SOAP12Address)
         {
            SOAP12Address addr = (SOAP12Address)extElement;
            soapAddress = addr.getLocationURI();
            break;
         }
         else if ("address".equals(elementType.getLocalPart()))
         {
            log.warn("Unprocessed extension element: " + elementType);
         }
      }

      if (soapAddress == null)
         throw new WSDLException(WSDLException.INVALID_WSDL, "Cannot obtain SOAP address");

      return soapAddress;
   }

   private String getOptionalAttribute(Element domElement, String attrName)
   {
      String attrValue = domElement.getAttribute(attrName);
      return (attrValue.length() > 0 ? attrValue : null);
   }
}
