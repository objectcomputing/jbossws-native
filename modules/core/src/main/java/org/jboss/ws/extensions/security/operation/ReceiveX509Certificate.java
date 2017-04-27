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
package org.jboss.ws.extensions.security.operation;

import java.security.Principal;
import java.security.cert.X509Certificate;

import org.jboss.logging.Logger;
import org.jboss.security.CertificatePrincipal;
import org.jboss.security.auth.certs.SubjectDNMapping;
import org.jboss.ws.extensions.security.element.Token;
import org.jboss.ws.extensions.security.element.X509Token;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.jboss.wsf.spi.SPIProvider;
import org.jboss.wsf.spi.SPIProviderResolver;
import org.jboss.wsf.spi.invocation.SecurityAdaptor;
import org.jboss.wsf.spi.invocation.SecurityAdaptorFactory;
import org.w3c.dom.Document;

/**
 * This is used for X509Certificate JAAS authentication
 * 
 * @author alessio.soldano@jboss.com
 * @since 24-May-2008
 */
public class ReceiveX509Certificate implements TokenOperation
{
   private static Logger log = Logger.getLogger(ReceiveX509Certificate.class);
   private SecurityAdaptorFactory secAdapterfactory;
   private CertificatePrincipal certMapping;

   public ReceiveX509Certificate(String certificatePrincipal)
   {
      SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
      secAdapterfactory = spiProvider.getSPI(SecurityAdaptorFactory.class);
      if (certificatePrincipal != null && !certificatePrincipal.equals(""))
      {
         try
         {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> cpClass = loader.loadClass(certificatePrincipal);
            certMapping = (CertificatePrincipal) cpClass.newInstance();
         }
         catch (Exception e)
         {
            log.error("Failed to load CertificatePrincipal '" + certificatePrincipal + "', using default SubjectDNMapping.", e);
         }
      }
      if (certMapping == null)
         certMapping = new SubjectDNMapping();
   }

   public void process(Document message, Token token) throws WSSecurityException
   {
      if (token == null || !(token instanceof X509Token))
      {
         throw new IllegalArgumentException("Token " + token + " is not a X509Token!");
      }
      X509Certificate cert = ((X509Token)token).getCert();
      Principal principal = certMapping.toPrinicipal(new X509Certificate[] { cert });
      SecurityAdaptor securityAdaptor = secAdapterfactory.newSecurityAdapter();
      securityAdaptor.setPrincipal(principal);
      securityAdaptor.setCredential(cert);

   }

}
