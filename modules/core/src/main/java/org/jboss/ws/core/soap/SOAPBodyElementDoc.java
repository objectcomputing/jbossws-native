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
package org.jboss.ws.core.soap;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.transform.Source;

import org.jboss.logging.Logger;
import org.jboss.ws.WSException;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.soap.SOAPContent.State;
import org.jboss.ws.extensions.validation.SchemaExtractor;
import org.jboss.ws.extensions.validation.SchemaValidationHelper;
import org.jboss.ws.feature.SchemaValidationFeature;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;

/**
 * An abstract implemenation of the SOAPBodyElement
 * <p/>
 * This class should not expose functionality that is not part of
 * {@link javax.xml.soap.SOAPBodyElement}. Client code should use <code>SOAPBodyElement</code>.
 *
 * @author Thomas.Diesler@jboss.org
 */
public class SOAPBodyElementDoc extends SOAPContentElement implements SOAPBodyElement
{
   // provide logging
   private static Logger log = Logger.getLogger(SOAPBodyElementDoc.class);
   
   private SchemaValidationFeature feature;
   
   public SOAPBodyElementDoc(Name name)
   {
      super(name);
   }

   public SOAPBodyElementDoc(QName qname)
   {
      super(qname);
   }
   
   public SOAPBodyElementDoc(SOAPElementImpl element)
   {
      super(element);
   }

   @Override
   protected State transitionTo(State nextState)
   {
      State prevState = soapContent.getState();
      if (nextState != prevState)
      {
         if (isValidationEnabled() && nextState == State.OBJECT_VALID)
         {
            log.info("Validating: " + prevState);
            validatePayload(soapContent.getPayload());
         }
         
         prevState = super.transitionTo(nextState);
         
         if (isValidationEnabled() && prevState == State.OBJECT_VALID)
         {
            log.info("Validating: " + nextState);
            validatePayload(soapContent.getPayload());
         }
      }
      return prevState;
   }

   private void validatePayload(Source source) 
   {
      SchemaExtractor schemaExtractor = new SchemaExtractor();
      try
      {
         CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
         EndpointMetaData epMetaData = msgContext.getEndpointMetaData();
         feature = epMetaData.getFeature(SchemaValidationFeature.class);
         URL xsdURL = feature.getSchemaLocation() != null ? new URL(feature.getSchemaLocation()) : null;
         if (xsdURL == null)
         {
            URL wsdlURL = epMetaData.getServiceMetaData().getWsdlFileOrLocation();
            if (wsdlURL == null)
            {
               log.warn("Validation error: Cannot obtain wsdl URL");
            }
            else
            {
               xsdURL = schemaExtractor.getSchemaUrl(wsdlURL);
            }
         }
         if (xsdURL != null)
         {
            ErrorHandler errorHandler = feature.getErrorHandler();
            Element xmlDOM = DOMUtils.sourceToElement(source);
            new SchemaValidationHelper(xsdURL).setErrorHandler(errorHandler).validateDocument(xmlDOM);
         }
      }
      catch (RuntimeException rte)
      {
         throw rte;
      }
      catch (Exception ex)
      {
         WSException.rethrow(ex);
      }
      finally
      {
         schemaExtractor.close();
      }
   }

   private boolean isValidationEnabled()
   {
      CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();
      if (msgContext != null)
         feature = msgContext.getEndpointMetaData().getFeature(SchemaValidationFeature.class);
      
      return feature != null ? feature.isEnabled() : false;
   }
}
