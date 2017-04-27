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
package org.jboss.ws.extensions.security;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;

import org.jboss.logging.Logger;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.CommonSOAPFaultException;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jboss.ws.core.soap.SOAPMessageImpl;
import org.jboss.ws.extensions.security.exception.InvalidSecurityHeaderException;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.jboss.ws.extensions.security.nonce.DefaultNonceFactory;
import org.jboss.ws.extensions.security.nonce.NonceFactory;
import org.jboss.ws.extensions.security.operation.AuthorizeOperation;
import org.jboss.ws.extensions.security.operation.EncodingOperation;
import org.jboss.ws.extensions.security.operation.EncryptionOperation;
import org.jboss.ws.extensions.security.operation.RequireEncryptionOperation;
import org.jboss.ws.extensions.security.operation.RequireOperation;
import org.jboss.ws.extensions.security.operation.RequireSignatureOperation;
import org.jboss.ws.extensions.security.operation.RequireTimestampOperation;
import org.jboss.ws.extensions.security.operation.SendUsernameOperation;
import org.jboss.ws.extensions.security.operation.SignatureOperation;
import org.jboss.ws.extensions.security.operation.TimestampOperation;
import org.jboss.ws.metadata.umdm.EndpointMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.wsse.Authenticate;
import org.jboss.ws.metadata.wsse.Authorize;
import org.jboss.ws.metadata.wsse.Config;
import org.jboss.ws.metadata.wsse.Encrypt;
import org.jboss.ws.metadata.wsse.Operation;
import org.jboss.ws.metadata.wsse.Port;
import org.jboss.ws.metadata.wsse.RequireEncryption;
import org.jboss.ws.metadata.wsse.RequireSignature;
import org.jboss.ws.metadata.wsse.RequireTimestamp;
import org.jboss.ws.metadata.wsse.Requires;
import org.jboss.ws.metadata.wsse.Sign;
import org.jboss.ws.metadata.wsse.Timestamp;
import org.jboss.ws.metadata.wsse.Username;
import org.jboss.ws.metadata.wsse.WSSecurityConfiguration;
import org.jboss.wsf.common.DOMWriter;
import org.jboss.wsf.spi.SPIProvider;
import org.jboss.wsf.spi.SPIProviderResolver;
import org.jboss.wsf.spi.invocation.SecurityAdaptor;
import org.jboss.wsf.spi.invocation.SecurityAdaptorFactory;
import org.w3c.dom.Element;

public class WSSecurityDispatcher implements WSSecurityAPI
{
   // provide logging
   private static Logger log = Logger.getLogger(WSSecurityDispatcher.class);

   public void decodeMessage(WSSecurityConfiguration configuration, SOAPMessage message, Config operationConfig) throws SOAPException
   {
      Config config = getActualConfig(configuration, operationConfig);
      SOAPHeader soapHeader = message.getSOAPHeader();
      QName secQName = new QName(Constants.WSSE_NS, "Security");
      Element secHeaderElement = (soapHeader != null) ? Util.findElement(soapHeader, secQName) : null;

      if (secHeaderElement == null)
      {
         // This is ok, we always allow faults to be received because WS-Security does not encrypt faults
         if (message.getSOAPBody().getFault() != null)
            return;

         if (hasRequirements(config))
            throw convertToFault(new InvalidSecurityHeaderException("This service requires <wsse:Security>, which is missing."));
      }

      try
      {
         if (secHeaderElement != null)
         {
            decodeHeader(configuration, config, message, secHeaderElement);
         }

         authorize(config);
      }
      catch (WSSecurityException e)
      {
         if (e.isInternalError())
            log.error("Internal error occured handling inbound message:", e);
         else if (log.isDebugEnabled())
            log.debug("Returning error to sender: " + e.getMessage());

         throw convertToFault(e);
      }

   }

   private void decodeHeader(WSSecurityConfiguration configuration, Config config, SOAPMessage message, Element secHeaderElement) throws WSSecurityException
   {
      SecurityStore securityStore = new SecurityStore(configuration.getKeyStoreURL(), configuration.getKeyStoreType(), configuration.getKeyStorePassword(),
            configuration.getKeyPasswords(), configuration.getTrustStoreURL(), configuration.getTrustStoreType(), configuration.getTrustStorePassword());
      NonceFactory factory = Util.loadFactory(NonceFactory.class, configuration.getNonceFactory(), DefaultNonceFactory.class);

      Authenticate authenticate = null;

      if (config != null)
      {
         authenticate = config.getAuthenticate();
      }

      SecurityDecoder decoder = new SecurityDecoder(securityStore, factory, configuration.getTimestampVerification(), authenticate);

      decoder.decode(message.getSOAPPart(), secHeaderElement);

      if (log.isTraceEnabled())
         log.trace("Decoded Message:\n" + DOMWriter.printNode(message.getSOAPPart(), true));

      List<RequireOperation> operations = buildRequireOperations(config);

      decoder.verify(operations);
      if (log.isDebugEnabled())
         log.debug("Verification is successful");

      decoder.complete();
   }

   private void authorize(Config config) throws WSSecurityException
   {
      if (config != null)
      {
         Authorize authorize = config.getAuthorize();
         if (authorize != null)
         {
            AuthorizeOperation authorizeOp = new AuthorizeOperation(authorize);
            authorizeOp.process();
         }
      }
   }

   public void encodeMessage(WSSecurityConfiguration configuration, SOAPMessage message, Config operationConfig, String user, String password) throws SOAPException
   {
      Config config = getActualConfig(configuration, operationConfig);
      log.debug("WS-Security config: " + config);

      // Nothing to process
      if (config == null)
         return;

      ArrayList<EncodingOperation> operations = new ArrayList<EncodingOperation>();
      Timestamp timestamp = config.getTimestamp();
      if (timestamp != null)
      {
         operations.add(new TimestampOperation(timestamp.getTtl()));
      }

      Username username = config.getUsername();
      if (username != null && user != null && password != null)
      {
         NonceFactory factory = Util.loadFactory(NonceFactory.class, configuration.getNonceFactory(), DefaultNonceFactory.class);
         operations.add(new SendUsernameOperation(user, password, username.isDigestPassword(), username.isUseNonce(), username.isUseCreated(), factory.getGenerator()));
      }

      Sign sign = config.getSign();
      if (sign != null)
      {
         List<Target> targets = convertTargets(sign.getTargets());
         if (sign.isIncludeTimestamp())
         {
            if (timestamp == null)
               operations.add(new TimestampOperation(null));

            if (targets != null && targets.size() > 0)
               targets.add(new WsuIdTarget("timestamp"));
         }

         operations.add(new SignatureOperation(targets, sign.getAlias(), sign.getTokenRefType()));
      }

      Encrypt encrypt = config.getEncrypt();
      if (encrypt != null)
      {
         List<Target> targets = convertTargets(encrypt.getTargets());
         operations.add(new EncryptionOperation(targets, encrypt.getAlias(), encrypt.getAlgorithm(), encrypt.getWrap(), encrypt.getTokenRefType()));
      }

      if (operations.size() == 0)
         return;

      if (log.isDebugEnabled())
         log.debug("Encoding Message:\n" + DOMWriter.printNode(message.getSOAPPart(), true));

      try
      {
         SecurityStore securityStore = new SecurityStore(configuration.getKeyStoreURL(), configuration.getKeyStoreType(), configuration.getKeyStorePassword(),
               configuration.getKeyPasswords(), configuration.getTrustStoreURL(), configuration.getTrustStoreType(), configuration.getTrustStorePassword());
         SecurityEncoder encoder = new SecurityEncoder(operations, securityStore);
         encoder.encode(message.getSOAPPart());
      }
      catch (WSSecurityException e)
      {
         if (e.isInternalError())
            log.error("Internal error occured handling outbound message:", e);
         else if (log.isDebugEnabled())
            log.debug("Returning error to sender: " + e.getMessage());

         throw convertToFault(e);
      }
   }

   public void cleanup()
   {
      //Reset username/password since they're stored using a ThreadLocal
      SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
      SecurityAdaptor securityAdaptor = spiProvider.getSPI(SecurityAdaptorFactory.class).newSecurityAdapter();
      securityAdaptor.setPrincipal(null);
      securityAdaptor.setCredential(null);
   }

   private List<Target> convertTargets(List<org.jboss.ws.metadata.wsse.Target> targets)
   {
      if (targets == null)
         return null;

      ArrayList<Target> newList = new ArrayList<Target>(targets.size());

      for (org.jboss.ws.metadata.wsse.Target target : targets)
      {
         if ("qname".equals(target.getType()))
         {
            QNameTarget qnameTarget = new QNameTarget(QName.valueOf(target.getValue()), target.isContentOnly());
            newList.add(qnameTarget);
         }
         else if ("wsuid".equals(target.getType()))
         {
            newList.add(new WsuIdTarget(target.getValue()));
         }
      }

      return newList;
   }

   private CommonSOAPFaultException convertToFault(WSSecurityException e)
   {
      return new CommonSOAPFaultException(e.getFaultCode(), e.getFaultString());
   }

   private List<RequireOperation> buildRequireOperations(Config operationConfig)
   {
      if (operationConfig == null)
         return null;

      Requires requires = operationConfig.getRequires();
      if (requires == null)
         return null;

      ArrayList<RequireOperation> operations = new ArrayList<RequireOperation>();
      RequireTimestamp requireTimestamp = requires.getRequireTimestamp();
      if (requireTimestamp != null)
         operations.add(new RequireTimestampOperation(requireTimestamp.getMaxAge()));

      RequireSignature requireSignature = requires.getRequireSignature();
      if (requireSignature != null)
      {
         List<Target> targets = convertTargets(requireSignature.getTargets());
         operations.add(new RequireSignatureOperation(targets));
      }

      RequireEncryption requireEncryption = requires.getRequireEncryption();
      if (requireEncryption != null)
      {
         List<Target> targets = convertTargets(requireEncryption.getTargets());
         operations.add(new RequireEncryptionOperation(targets));
      }

      return operations;
   }

   private Config getActualConfig(WSSecurityConfiguration configuration, Config operationConfig)
   {
      if (operationConfig == null)
      {
         //if no configuration override, we try getting the right operation config
         //according to the invoked operation that can be found using the context
         CommonMessageContext ctx = MessageContextAssociation.peekMessageContext();
         if (ctx != null)
         {
            EndpointMetaData epMetaData = ctx.getEndpointMetaData();
            QName port = epMetaData.getPortName();

            OperationMetaData opMetaData = ctx.getOperationMetaData();
            if (opMetaData == null)
            {
               // Get the operation meta data from the soap message
               // for the server side inbound message.
               SOAPMessageImpl soapMessage = (SOAPMessageImpl)ctx.getSOAPMessage();
               try
               {
                  opMetaData = soapMessage.getOperationMetaData(epMetaData);
               }
               catch (SOAPException e)
               {
                  throw new WebServiceException("Error while looking for the operation meta data: " + e);
               }
            }
            if (opMetaData != null)
               operationConfig = selectOperationConfig(configuration, port, opMetaData.getQName());
         }
      }
      //null operationConfig means default behavior
      return operationConfig != null ? operationConfig : configuration.getDefaultConfig();
   }

   private Config selectOperationConfig(WSSecurityConfiguration configuration, QName portName, QName opName)
   {
      Port port = configuration.getPorts().get(portName != null ? portName.getLocalPart() : null);
      if (port == null)
         return configuration.getDefaultConfig();

      Operation operation = port.getOperations().get(opName != null ? opName.toString() : null);
      if (operation == null)
      {
         //if the operation name was not available or didn't match any wsse configured operation,
         //we fall back to the port wsse config (if available) or the default config.
         Config portConfig = port.getDefaultConfig();
         return (portConfig == null) ? configuration.getDefaultConfig() : portConfig;

      }
      return operation.getConfig();
   }

   private boolean hasRequirements(Config config)
   {
      return config != null && config.getRequires() != null;
   }
}
