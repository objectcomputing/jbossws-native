/*
* JBoss, Home of Professional Open Source.
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
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

import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;

import org.jboss.logging.Logger;
import org.jboss.security.AuthenticationManager;
import org.jboss.security.RealmMapping;
import org.jboss.security.SecurityAssociation;
import org.jboss.security.SecurityContext;
import org.jboss.security.SecurityContextAssociation;
import org.jboss.security.SimplePrincipal;
import org.jboss.ws.WSException;
import org.jboss.ws.extensions.security.exception.FailedAuthenticationException;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.jboss.ws.metadata.wsse.Authorize;
import org.jboss.ws.metadata.wsse.Role;
import org.jboss.wsf.spi.SPIProvider;
import org.jboss.wsf.spi.SPIProviderResolver;
import org.jboss.wsf.spi.invocation.SecurityAdaptor;
import org.jboss.wsf.spi.invocation.SecurityAdaptorFactory;

/**
 * Operation to authenticate and check the authorisation of the
 * current user.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 * @since December 23rd 2008
 */
public class AuthorizeOperation
{

   private static final Logger log = Logger.getLogger(AuthorizeOperation.class);

   private Authorize authorize;

   private AuthenticationManager am;

   private RealmMapping rm;

   private SecurityAdaptorFactory secAdapterfactory;

   public AuthorizeOperation(Authorize authorize)
   {
      this.authorize = authorize;

      try
      {
         Context ctx = new InitialContext();
         Object obj = ctx.lookup("java:comp/env/security/securityMgr");
         am = (AuthenticationManager)obj;
         rm = (RealmMapping)am;
      }
      catch (NamingException ne)
      {
         throw new WSException("Unable to lookup AuthenticationManager", ne);
      }

      SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
      secAdapterfactory = spiProvider.getSPI(SecurityAdaptorFactory.class);
   }

   public void process() throws WSSecurityException
   {
      boolean TRACE = log.isTraceEnabled();

      if (TRACE)
         log.trace("About to check authorization, using security domain '" + am.getSecurityDomain() + "'");

      // Step 1 - Authenticate using currently associated principals.
      SecurityAdaptor securityAdaptor = secAdapterfactory.newSecurityAdapter();
      Principal principal = securityAdaptor.getPrincipal();
      Object credential = securityAdaptor.getCredential();

      Subject subject = new Subject();

      if (am.isValid(principal, credential, subject) == false)
      {
         String msg = "Authentication failed, principal=" + principal;
         log.error(msg);
         SecurityException e = new SecurityException(msg);
         throw new FailedAuthenticationException(e);
      }
      securityAdaptor.pushSubjectContext(subject, principal, credential);

      if (TRACE)
         log.trace("Authenticated, principal=" + principal);

      // Step 2 - If unchecked all ok so return.
      if (authorize.isUnchecked())
      {
         if (TRACE)
            log.trace("authorize.isUnchecked()==true skipping roles check.");

         return;
      }

      // Step 3 - If roles specified check user in role. 
      Set<Principal> expectedRoles = expectedRoles();
      if (TRACE)
         log.trace("expectedRoles=" + expectedRoles);

      if (rm.doesUserHaveRole(principal, expectedRoles) == false)
      {
         Set<Principal> userRoles = rm.getUserRoles(principal);
         String msg = "Insufficient method permissions, principal=" + principal + ", requiredRoles=" + expectedRoles + ", principalRoles=" + userRoles;
         log.error(msg);
         SecurityException e = new SecurityException(msg);
         throw new FailedAuthenticationException(e);
      }

      if (TRACE)
         log.trace("Roles check complete, principal=" + principal + ", requiredRoles=" + expectedRoles);
   }

   private Set<Principal> expectedRoles()
   {
      List<Role> roles = authorize.getRoles();
      int rolesCount = (roles != null) ? roles.size() : 0;
      Set<Principal> expectedRoles = new HashSet<Principal>(rolesCount);

      if (roles != null)
      {
         for (Role current : roles)
         {
            expectedRoles.add(new SimplePrincipal(current.getName()));
         }
      }

      return expectedRoles;
   }

}
