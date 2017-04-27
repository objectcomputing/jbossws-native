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
package org.jboss.ws.metadata.wsse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <code>Authorize</code> specifies that the users credentials should be 
 * checked to ensure the user is authorized to call the endpoint.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 * @since December 18th 2008
 */
public class Authorize implements Serializable
{

   private Unchecked unchecked;

   private List<Role> roles = new ArrayList<Role>();

   public List<Role> getRoles()
   {
      return Collections.unmodifiableList(roles);
   }

   public void addRole(final Role role)
   {
      if (isUnchecked())
      {
         throw new IllegalStateException("Can not add role after setting 'Unchecked'");
      }
      roles.add(role);
   }

   public boolean isUnchecked()
   {
      return unchecked != null;
   }

   void setUnchecked(Unchecked unchecked)
   {
      if (roles.isEmpty() == false)
      {
         throw new IllegalStateException("Can not set 'Unchecked' with role(s) defined.");
      }
      this.unchecked = unchecked;
   }

}
