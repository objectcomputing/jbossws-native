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
package org.jboss.test.ws.jaxws.jbws1172;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.jboss.test.ws.jaxws.jbws1172.types.MyTest;
import org.jboss.ws.extensions.validation.SchemaExtractor;
import org.jboss.ws.extensions.validation.SchemaValidationHelper;
import org.jboss.ws.feature.SchemaValidationFeature;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * [JBWS-1172] Support schema validation for incoming messages
 * 
 * http://jira.jboss.org/jira/browse/JBWS-1172
 *
 * @author Thomas.Diesler@jboss.com
 * @since 28-Feb-2008
 */
public class JBWS1172TestCase extends JBossWSTest
{
   private static final QName SERVICE_NAME = new QName("http://www.my-company.it/ws/my-test", "MyTestService");

   public static Test suite()
   {
      return new JBossWSTestSetup(JBWS1172TestCase.class, "jaxws-jbws1172.war");
   }

   public void testSchemaValidationPositive() throws Exception
   {
      URL wsdlURL = getResourceURL("jaxws/jbws1172/WEB-INF/wsdl/TestService.wsdl");
      URL xsdURL = new SchemaExtractor().getSchemaUrl(wsdlURL);
      String inxml = "<performTest xmlns='http://www.my-company.it/ws/my-test'><Code>1000</Code></performTest>";
      new SchemaValidationHelper(xsdURL).validateDocument(inxml);
   }

   public void testSchemaValidationNegative() throws Exception
   {
      URL wsdlURL = getResourceURL("jaxws/jbws1172/WEB-INF/wsdl/TestService.wsdl");
      URL xsdURL = new SchemaExtractor().getSchemaUrl(wsdlURL);
      String inxml = "<performTest xmlns='http://www.my-company.it/ws/my-test'><Code>2000</Code></performTest>";
      try
      {
         new SchemaValidationHelper(xsdURL).validateDocument(inxml);
      }
      catch (SAXException ex)
      {
         String msg = ex.getMessage();
         assertTrue("Unexpectd message: " + msg, msg.indexOf("Value '2000' is not facet-valid with respect to maxInclusive '1000'") > 0);
      }
   }

   public void testEndpointWsdlValidation() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jaxws-jbws1172/noval?wsdl");
      URL xsdURL = new SchemaExtractor().getSchemaUrl(wsdlURL);
      String inxml = "<performTest xmlns='http://www.my-company.it/ws/my-test'><Code>1000</Code></performTest>";
      new SchemaValidationHelper(xsdURL).validateDocument(inxml);
   }
   
   public void testValidatingClientWithExplicitSchema() throws Exception
   {
      URL wsdlURL = getResourceURL("jaxws/jbws1172/WEB-INF/wsdl/TestService.wsdl");
      URL xsdURL = new SchemaExtractor().getSchemaUrl(wsdlURL);
      
      Service service = Service.create(wsdlURL, SERVICE_NAME);
      SchemaValidationFeature feature = new SchemaValidationFeature(xsdURL.toString());
      MyTest port = service.getPort(MyTest.class, feature);
      try
      {
         port.performTest(new Long(2000));
      }
      catch (Exception ex)
      {
         StringWriter stwr = new StringWriter(); 
         ex.printStackTrace(new PrintWriter(stwr));
         String msg = stwr.toString();
         assertTrue("Unexpectd message: " + ex.getMessage(), msg.indexOf("Value '2000' is not facet-valid with respect to maxInclusive '1000'") > 0);
      }
   }
   
   public void testValidatingClientWithErrorHandler() throws Exception
   {
      URL wsdlURL = getResourceURL("jaxws/jbws1172/WEB-INF/wsdl/TestService.wsdl");
      URL xsdURL = new SchemaExtractor().getSchemaUrl(wsdlURL);
      
      Service service = Service.create(wsdlURL, SERVICE_NAME);
      SchemaValidationFeature feature = new SchemaValidationFeature(xsdURL.toString());
      
      TestErrorHandler errorHandler = new TestErrorHandler();
      feature.setErrorHandler(errorHandler);
      
      MyTest port = service.getPort(MyTest.class, feature);
      port.performTest(new Long(2000));
      
      String msg = errorHandler.getErrors();
      assertTrue("Unexpectd message: " + msg, msg.indexOf("Value '2000' is not facet-valid with respect to maxInclusive '1000'") > 0);
   }
   
   public void testNonValidatingEndpoint() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jaxws-jbws1172/noval?wsdl");
      
      Service service = Service.create(wsdlURL, SERVICE_NAME);
      MyTest port = service.getPort(MyTest.class);
      port.performTest(new Long(1000));
      port.performTest(new Long(2000));
   }
   
   public void testValidatingEndpoint() throws Exception
   {
      URL wsdlURL = new URL("http://" + getServerHost() + ":8080/jaxws-jbws1172/doval?wsdl");
      
      Service service = Service.create(wsdlURL, SERVICE_NAME);
      MyTest port = service.getPort(MyTest.class);
      port.performTest(new Long(1000));
      try
      {
         port.performTest(new Long(2000));
      }
      catch (Exception ex)
      {
         String msg = ex.getMessage();
         assertTrue("Unexpectd message: " + ex.getMessage(), msg.indexOf("Value '2000' is not facet-valid with respect to maxInclusive '1000'") > 0);
      }
}
   
   private static  class TestErrorHandler implements ErrorHandler
   {
      private StringBuilder errors = new StringBuilder();
      public String getErrors()
      {
         return errors.toString();
      }
      public void error(SAXParseException ex) throws SAXException
      {
         errors.append(ex.getMessage());
      }
      public void fatalError(SAXParseException ex) throws SAXException
      {
      }
      public void warning(SAXParseException ex) throws SAXException
      {
      }
   }
}
