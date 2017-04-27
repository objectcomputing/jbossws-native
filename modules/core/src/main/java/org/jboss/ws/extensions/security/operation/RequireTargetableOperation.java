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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.ws.extensions.security.QNameTarget;
import org.jboss.ws.extensions.security.Target;
import org.jboss.ws.extensions.security.Util;
import org.jboss.ws.extensions.security.WsuIdTarget;
import org.jboss.ws.extensions.security.element.SecurityHeader;
import org.jboss.ws.extensions.security.exception.FailedCheckException;
import org.jboss.ws.extensions.security.exception.WSSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RequireTargetableOperation implements RequireOperation
{
   private List<Target> targets;
   
   public RequireTargetableOperation(List<Target> targets)
   {
      this.targets = targets;
   }

   private Collection<String> resolveTarget(Document message, Target target) throws WSSecurityException
   {
      if (target instanceof QNameTarget)
         return resolveQNameTarget(message, (QNameTarget) target);
      else if (target instanceof WsuIdTarget)
      {
         Collection<String> result = new ArrayList<String>(1);
         result.add(((WsuIdTarget)target).getId());
         return result;
      }

      throw new WSSecurityException("Unknown target");
   }

   private Collection<String> resolveQNameTarget(Document message, QNameTarget target) throws WSSecurityException
   {
      QName name = target.getName();

      Element element = Util.findElement(message.getDocumentElement(), name);
      if (element == null)
         throw new FailedCheckException("Required QName was not present: " + name);

      String id = Util.getWsuId(element);

      if (id == null)
         throw new FailedCheckException("Required element did not contain a wsu:id.");

      Collection<String> result = new ArrayList<String>(1);
      result.add(id);

      return result;
   }

   public void process(Document message, SecurityHeader header, Collection<String> processedIds) throws WSSecurityException
   {
      if (targets == null || targets.size() == 0)
      {
         // By default we require just the body element
         String namespace = message.getDocumentElement().getNamespaceURI();
         targets = new ArrayList<Target>(1);
         targets.add(new QNameTarget(new QName(namespace, "Body"), true));
      }

      for (Target target : targets)
      {
          Collection<String> ids = resolveTarget(message, target);
          if (! processedIds.containsAll(ids))
             throw new FailedCheckException("Required elements for encryption and or signing are not all present.");
      }
   }
}
