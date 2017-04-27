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
package org.jboss.ws.metadata.jaxrpcmapping;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.core.utils.ResourceURL;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.ObjectModelFactory;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.UnmarshallingContext;
import org.xml.sax.Attributes;

/**
 * A JBossXB factory for {@link org.jboss.ws.metadata.jaxrpcmapping.JavaWsdlMapping}
 *
 * @author Thomas.Diesler@jboss.org
 * @since 14-May-2004
 */
public class JavaWsdlMappingFactory implements ObjectModelFactory
{
   // provide logging
   private static Logger log = Logger.getLogger(JavaWsdlMappingFactory.class);

   // Hide constructor
   private JavaWsdlMappingFactory()
   {
   }

   /**
    * Create a new instance of a jaxrpc-mapping factory
    */
   public static JavaWsdlMappingFactory newInstance()
   {
      return new JavaWsdlMappingFactory();
   }

   /**
    * Factory method for JavaWsdlMapping
    */
   public JavaWsdlMapping parse(URL mappingURL) throws IOException
   {
      if (mappingURL == null)
      {
         throw new IllegalArgumentException("JAXRPC mapping URL cannot be null");
      }

      // setup the XML binding Unmarshaller
      InputStream is = new ResourceURL(mappingURL).openStream();
      try
      {
         Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
         JavaWsdlMapping javaWsdlMapping = (JavaWsdlMapping)unmarshaller.unmarshal(is, this, null);
         return javaWsdlMapping;
      }
      catch (JBossXBException e)
      {
         IOException ioex = new IOException("Cannot parse: " + mappingURL);
         Throwable cause = e.getCause();
         if (cause != null)
            ioex.initCause(cause);
         throw ioex;
      }
      finally
      {
         is.close();
      }
   }


   /**
    * This method is called on the factory by the object model builder when the parsing starts.
    */
   public Object newRoot(Object root, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      return new JavaWsdlMapping();
   }

   public Object completeRoot(Object root, UnmarshallingContext ctx, String uri, String name)
   {
      return root;
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(JavaWsdlMapping javaWsdlMapping, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("newChild: " + localName);
      if ("package-mapping".equals(localName))
      {
         return new PackageMapping(javaWsdlMapping);
      }
      if ("java-xml-type-mapping".equals(localName))
      {
         return new JavaXmlTypeMapping(javaWsdlMapping);
      }
      if ("exception-mapping".equals(localName))
      {
         return new ExceptionMapping(javaWsdlMapping);
      }
      if ("service-interface-mapping".equals(localName))
      {
         return new ServiceInterfaceMapping(javaWsdlMapping);
      }
      if ("service-endpoint-interface-mapping".equals(localName))
      {
         return new ServiceEndpointInterfaceMapping(javaWsdlMapping);
      }
      return null;
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(JavaWsdlMapping javaWsdlMapping, PackageMapping packageMapping, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + javaWsdlMapping + ",child=" + packageMapping + "]");
      javaWsdlMapping.addPackageMapping(packageMapping);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(JavaWsdlMapping javaWsdlMapping, JavaXmlTypeMapping typeMapping, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + javaWsdlMapping + ",child=" + typeMapping + "]");
      javaWsdlMapping.addJavaXmlTypeMappings(typeMapping);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(JavaWsdlMapping javaWsdlMapping, ExceptionMapping exceptionMapping, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + javaWsdlMapping + ",child=" + exceptionMapping + "]");
      javaWsdlMapping.addExceptionMappings(exceptionMapping);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(JavaWsdlMapping javaWsdlMapping, ServiceInterfaceMapping siMapping, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + javaWsdlMapping + ",child=" + siMapping + "]");
      javaWsdlMapping.addServiceInterfaceMappings(siMapping);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(JavaWsdlMapping javaWsdlMapping, ServiceEndpointInterfaceMapping seiMapping, UnmarshallingContext navigator, String namespaceURI,
         String localName)
   {
      log.trace("addChild: [obj=" + javaWsdlMapping + ",child=" + seiMapping + "]");
      javaWsdlMapping.addServiceEndpointInterfaceMappings(seiMapping);
   }

   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(PackageMapping packageMapping, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      log.trace("setValue: [obj=" + packageMapping + ",localName=" + localName + ",value=" + value + "]");
      if ("package-type".equals(localName))
      {
         packageMapping.setPackageType(value);
      }
      else if ("namespaceURI".equals(localName))
      {
         packageMapping.setNamespaceURI(value);
      }
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(JavaXmlTypeMapping typeMapping, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("newChild: " + localName);
      if ("variable-mapping".equals(localName))
      {
         return new VariableMapping(typeMapping);
      }
      return null;
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(JavaXmlTypeMapping typeMapping, VariableMapping variableMapping, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + typeMapping + ",child=" + variableMapping + "]");
      typeMapping.addVariableMapping(variableMapping);
   }

   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(JavaXmlTypeMapping typeMapping, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      log.trace("setValue: [obj=" + typeMapping + ",localName=" + localName + ",value=" + value + "]");
      if ("java-type".equals(localName))
      {
         typeMapping.setJavaType(value);
      }
      else if ("root-type-qname".equals(localName))
      {
         QName qname = navigator.resolveQName(value);
         typeMapping.setRootTypeQName(qname);
      }
      else if ("anonymous-type-qname".equals(localName))
      {
         QName qname = null;
         try
         {
            // <anonymous-type-qname xmlns:typeNS="http://org.jboss.ws/anonymous/types">typeNS:&gt;root</anonymous-type-qname>
            qname = navigator.resolveQName(value);
         }
         catch (Exception e)
         {
            // ignore unresolved qname
         }

         if (qname == null)
         {
            // <anonymous-type-qname>http://example.com/stockquote/schemas:&gt;GetLastTradePrice</anonymous-type-qname>
            int index = value.lastIndexOf(':');
            if (index > 0)
            {
               String nsURI = value.substring(0, index);
               String localPart = value.substring(index + 1);
               qname = new QName(nsURI, localPart);
            }
         }
         
         if (qname == null)
            throw new IllegalArgumentException("Invalid anonymous qname: " + value);
         
         typeMapping.setAnonymousTypeQName(qname);
      }
      else if ("qname-scope".equals(localName))
      {
         typeMapping.setQNameScope(value);
      }
   }

   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(ExceptionMapping exceptionMapping, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      log.trace("setValue: [obj=" + exceptionMapping + ",localName=" + localName + ",value=" + value + "]");
      if ("exception-type".equals(localName))
      {
         exceptionMapping.setExceptionType(value);
      }
      else if ("wsdl-message".equals(localName))
      {
         exceptionMapping.setWsdlMessage(navigator.resolveQName(value));
      }
      else if ("constructor-parameter-order".equals(localName))
      {
         exceptionMapping.addConstructorParameter(value);
      }
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(ServiceInterfaceMapping siMapping, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("newChild: " + localName);
      if ("port-mapping".equals(localName))
      {
         return new PortMapping(siMapping);
      }
      return null;
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(ServiceInterfaceMapping siMapping, PortMapping portMapping, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + siMapping + ",child=" + portMapping + "]");
      siMapping.addPortMapping(portMapping);
   }

   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(ServiceInterfaceMapping siMapping, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      log.trace("setValue: [obj=" + siMapping + ",localName=" + localName + ",value=" + value + "]");
      if ("service-interface".equals(localName))
      {
         siMapping.setServiceInterface(value);
      }
      else if ("wsdl-service-name".equals(localName))
      {
         siMapping.setWsdlServiceName(navigator.resolveQName(value));
      }
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(ServiceEndpointInterfaceMapping seiMapping, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("newChild: " + localName);
      if ("service-endpoint-method-mapping".equals(localName))
      {
         return new ServiceEndpointMethodMapping(seiMapping);
      }
      return null;
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(ServiceEndpointInterfaceMapping seiMapping, ServiceEndpointMethodMapping seiMethodMapping, UnmarshallingContext navigator, String namespaceURI,
         String localName)
   {
      log.trace("addChild: [obj=" + seiMapping + ",child=" + seiMapping + "]");
      seiMapping.addServiceEndpointMethodMapping(seiMethodMapping);
   }

   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(ServiceEndpointInterfaceMapping seiMapping, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      log.trace("setValue: [obj=" + seiMapping + ",localName=" + localName + ",value=" + value + "]");
      if ("service-endpoint-interface".equals(localName))
      {
         seiMapping.setServiceEndpointInterface(value);
      }
      else if ("wsdl-port-type".equals(localName))
      {
         seiMapping.setWsdlPortType(navigator.resolveQName(value));
      }
      else if ("wsdl-binding".equals(localName))
      {
         seiMapping.setWsdlBinding(navigator.resolveQName(value));
      }
   }

   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(VariableMapping variableMapping, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      log.trace("setValue: [obj=" + variableMapping + ",localName=" + localName + ",value=" + value + "]");
      if ("java-variable-name".equals(localName))
      {
         variableMapping.setJavaVariableName(value);
      }
      else if ("data-member".equals(localName))
      {
         variableMapping.setDataMember(true);
      }
      else if ("xml-attribute-name".equals(localName))
      {
         variableMapping.setXmlAttributeName(value);
      }
      else if ("xml-element-name".equals(localName))
      {
         variableMapping.setXmlElementName(value);
      }
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(VariableMapping variableMapping, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("newChild: " + localName);
      if ("data-member".equals(localName))
      {
         variableMapping.setDataMember(true);
      }
      else if ("xml-wildcard".equals(localName))
      {
         variableMapping.setXmlWildcard(true);
      }
      return null;
   }

   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(PortMapping portMapping, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      log.trace("setValue: [obj=" + portMapping + ",localName=" + localName + ",value=" + value + "]");
      if ("port-name".equals(localName))
      {
         portMapping.setPortName(value);
      }
      else if ("java-port-name".equals(localName))
      {
         portMapping.setJavaPortName(value);
      }
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(ServiceEndpointMethodMapping methodMapping, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("newChild: " + localName);
      if ("method-param-parts-mapping".equals(localName))
      {
         return new MethodParamPartsMapping(methodMapping);
      }
      if ("wsdl-return-value-mapping".equals(localName))
      {
         return new WsdlReturnValueMapping(methodMapping);
      }
      if ("wrapped-element".equals(localName))
      {
         methodMapping.setWrappedElement(true);
      }
      return null;
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(ServiceEndpointMethodMapping methodMapping, MethodParamPartsMapping partsMapping, UnmarshallingContext navigator, String namespaceURI,
         String localName)
   {
      log.trace("addChild: [obj=" + methodMapping + ",child=" + partsMapping + "]");
      methodMapping.addMethodParamPartsMapping(partsMapping);
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(ServiceEndpointMethodMapping methodMapping, WsdlReturnValueMapping returnValueMapping, UnmarshallingContext navigator, String namespaceURI,
         String localName)
   {
      log.trace("addChild: [obj=" + methodMapping + ",child=" + returnValueMapping + "]");
      methodMapping.setWsdlReturnValueMapping(returnValueMapping);
   }

   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(ServiceEndpointMethodMapping methodMapping, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      log.trace("setValue: [obj=" + methodMapping + ",localName=" + localName + ",value=" + value + "]");
      if ("java-method-name".equals(localName))
      {
         methodMapping.setJavaMethodName(value);
      }
      else if ("wsdl-operation".equals(localName))
      {
         methodMapping.setWsdlOperation(value);
      }
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(MethodParamPartsMapping partsMapping, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("newChild: " + localName);
      if ("wsdl-message-mapping".equals(localName))
      {
         return new WsdlMessageMapping(partsMapping);
      }
      return null;
   }

   /**
    * Called when parsing character is complete.
    */
   public void addChild(MethodParamPartsMapping partsMapping, WsdlMessageMapping msgMapping, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      log.trace("addChild: [obj=" + partsMapping + ",child=" + msgMapping + "]");
      partsMapping.setWsdlMessageMapping(msgMapping);
   }

   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(MethodParamPartsMapping partsMapping, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      log.trace("setValue: [obj=" + partsMapping + ",localName=" + localName + ",value=" + value + "]");
      if ("param-position".equals(localName))
      {
         partsMapping.setParamPosition(new Integer(value).intValue());
      }
      else if ("param-type".equals(localName))
      {
         partsMapping.setParamType(value);
      }
   }

   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(WsdlReturnValueMapping retValueMapping, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      log.trace("setValue: [obj=" + retValueMapping + ",localName=" + localName + ",value=" + value + "]");
      if ("method-return-value".equals(localName))
      {
         retValueMapping.setMethodReturnValue(value);
      }
      else if ("wsdl-message".equals(localName))
      {
         retValueMapping.setWsdlMessage(navigator.resolveQName(value));
      }
      else if ("wsdl-message-part-name".equals(localName))
      {
         retValueMapping.setWsdlMessagePartName(value);
      }
   }

   /**
    * Called when a new simple child element with text value was read from the XML content.
    */
   public void setValue(WsdlMessageMapping msgMapping, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      log.trace("setValue: [obj=" + msgMapping + ",localName=" + localName + ",value=" + value + "]");
      if ("wsdl-message".equals(localName))
      {
         msgMapping.setWsdlMessage(navigator.resolveQName(value));
      }
      else if ("wsdl-message-part-name".equals(localName))
      {
         msgMapping.setWsdlMessagePartName(value);
      }
      else if ("parameter-mode".equals(localName))
      {
         msgMapping.setParameterMode(value);
      }
   }

   /**
    * Called when parsing of a new element started.
    */
   public Object newChild(WsdlMessageMapping msgMapping, UnmarshallingContext navigator, String namespaceURI, String localName, Attributes attrs)
   {
      log.trace("newChild: " + localName);
      if ("soap-header".equals(localName))
      {
         msgMapping.setSoapHeader(true);
      }      
      return null;
   }
}
