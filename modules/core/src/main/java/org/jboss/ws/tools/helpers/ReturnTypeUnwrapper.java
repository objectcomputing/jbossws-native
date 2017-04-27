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
package org.jboss.ws.tools.helpers;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.ws.WSException;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;

/**
 * A helper class to unwrap a return type is possible.
 * 
 * @author darran.lofthouse@jboss.com
 * @since 10 Dec 2006
 */
public class ReturnTypeUnwrapper
{

   public JBossXSModel xsmodel;
   public QName xmlType;
   public XSElementDeclaration unwrappedElement;
   public boolean array = false;
   public boolean primitive = false;
   private boolean wrapped;

   public ReturnTypeUnwrapper(QName xmlType, JBossXSModel xsmodel, boolean wrapped)
   {
      this.xmlType = xmlType;
      this.xsmodel = xsmodel;
      this.wrapped = wrapped;
   }

   public boolean unwrap()
   {
      if (wrapped == false)
         return false;

      XSTypeDefinition xt = xsmodel.getTypeDefinition(xmlType.getLocalPart(), xmlType.getNamespaceURI());

      if (xt instanceof XSComplexTypeDefinition == false)
         throw new WSException("[JAX-RPC 2.3.1.2] Tried to unwrap a non-complex type.");

      XSComplexTypeDefinition wrapper = (XSComplexTypeDefinition)xt;

      boolean hasAttributes = wrapper.getAttributeUses().getLength() > 0;
      if (hasAttributes)
         throw new WSException("[JAX-RPC 2.3.1.2] Can not unwrap, complex type contains attributes.");

      boolean unwrapped = false;

      XSParticle particle = wrapper.getParticle();
      if (particle != null)
      {
         XSTerm term = particle.getTerm();

         if (term instanceof XSModelGroup == false)
            throw new WSException("[JAX-RPC 2.3.1.2] Expected model group, could not unwrap");

         XSModelGroup group = (XSModelGroup)term;

         unwrapped = unwrapModelGroup(group);
      }

      return unwrapped;
   }

   private boolean unwrapModelGroup(XSModelGroup group)
   {
      XSObjectList particles = group.getParticles();
      if (particles.getLength() == 1)
      {
         XSParticle particle = (XSParticle)particles.item(0);
         boolean array = particle.getMaxOccursUnbounded() || particle.getMaxOccurs() > 1;
         XSTerm term = particle.getTerm();

         if (term instanceof XSModelGroup)
         {
            return unwrapModelGroup((XSModelGroup)term);
         }
         else if (term instanceof XSElementDeclaration)
         {
            unwrappedElement = (XSElementDeclaration)term;
            XSTypeDefinition type = unwrappedElement.getTypeDefinition();
            if (type.getAnonymous() == false)
               xmlType = new QName(unwrappedElement.getTypeDefinition().getNamespace(), unwrappedElement.getTypeDefinition().getName());

            this.array = array;
            primitive = !(unwrappedElement.getNillable() || (particle.getMinOccurs() == 0 && particle.getMaxOccurs() == 1));
         }

      }
      else
      {
         throw new WSException("[JAX-RPC 2.3.1.2] Unable to unwrap model group with multiple particles.");
      }

      return unwrappedElement != null;
   }

}
