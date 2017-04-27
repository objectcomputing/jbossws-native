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
package javax.xml.ws;

import java.security.BasicPermission;

/**
 * This class defines web service permissions.
 * <p>
 * Web service Permissions are identified by name (also referred to as
 * a "target name") alone. There are no actions associated
 * with them.
 * <p>
 * The following permission target name is defined:
 * <p>
 * <dl>
 *   <dt>publishEndpoint
 * </dl>
 * <p>
 * The <code>publishEndpoint</code> permission allows publishing a
 * web service endpoint using the <code>publish</code> methods
 * defined by the <code>javax.xml.ws.Endpoint</code> class.
 *
 * @see javax.xml.ws.Endpoint
 * @see java.security.BasicPermission
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.lang.SecurityManager
 */
public final class WebServicePermission extends BasicPermission
{

   private static final long serialVersionUID = -146474640053770988L;

   /**
    * Creates a new permission with the specified name.
    *
    * @param name the name of the <code>WebServicePermission</code>
    */
   public WebServicePermission(String name)
   {
      super(name);
   }

   /**
    * Creates a new permission with the specified name and actions.
    *
    * The <code>actions</code> parameter is currently unused and
    * it should be <code>null</code>.
    *
    * @param name the name of the <code>WebServicePermission</code>
    * @param actions should be <code>null</code>
    */
   public WebServicePermission(String name, String actions)
   {
      super(name, actions);
   }

}
