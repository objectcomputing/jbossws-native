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
package org.jboss.ws.extensions.xop.jaxrpc;

import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;

/**
 * Scans complex type definitions for nested XOP type declarations.
 * A XOP type declaration is identified as a complex type
 * that derives from xsd:base64Binary, i.e:
 *
 * <code> <pre>
 * &lt;xs:complexType name="MyXOPElement" >
 *   &lt;xs:simpleContent>
 *       &lt;xs:extension base="xs:base64Binary" >
 *           &lt;xs:attribute ref="xmime:contentType" />
 *       &lt;/xs:extension>
 *   &lt;/xs:simpleContent>
 * &lt;/xs:complexType>
 * </pre></code>
 *
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Jun 9, 2006
 */
public class XOPScanner {

   // avoid circular scans
   private List<String> scannedItems = new ArrayList<String>();
   private static final String BASE64_BINARY = "base64Binary";

   /**
    * Query a complex type for nested XOP type definitions.
    */
   public XSTypeDefinition findXOPTypeDef(XSTypeDefinition typeDef)
   {      
      if(typeDef==null)
         return typeDef;
      XSTypeDefinition result = null;
      String name = typeDef.getName();
      String namespace = typeDef.getNamespace()!=null ? typeDef.getNamespace():"";

      if(typeDef instanceof XSSimpleTypeDefinition && BASE64_BINARY.equals(name))
      {
         return typeDef;
      }
      else if(typeDef instanceof XSComplexTypeDefinition)
      {
         XSComplexTypeDefinition complexTypeDef = (XSComplexTypeDefinition)typeDef;
         if(name!=null)
         {
            String typeKey = namespace+":"+name;

            if(scannedItems.contains(typeKey))
            {
               return null;
            }
            else
            {
               scannedItems.add(typeKey);
            }
         }

         // An XOP parameter is detected if it is a complex type
         // that derives from xsd:base64Binary
         if (complexTypeDef.getSimpleType() != null)
         {
            String typeName = complexTypeDef.getSimpleType().getName();
            if (BASE64_BINARY.equals(typeName))
               return complexTypeDef;
         }
         else
         {

            XSModelGroup xm = null;
            if(complexTypeDef.getContentType() != XSComplexTypeDefinition.CONTENTTYPE_EMPTY)
            {
               XSParticle xp = complexTypeDef.getParticle();
               if (xp != null)
               {
                  XSTerm xterm = xp.getTerm();
                  if(xterm instanceof XSModelGroup)
                  {
                     xm = (XSModelGroup)xterm;
                     //System.out.println("xm -> " + xm);

                     XSObjectList xo = xm.getParticles();

                     // interate over nested particles
                     for(int i=0; i<xm.getParticles().getLength(); i++ )
                     {
                        XSTerm xsterm = ((XSParticle)xo.item(i)).getTerm();

                        // Can be either XSModelGroup, XSWildcard, XSElementDeclaration
                        // We only proceed with XSElementDeclaration
                        if(xsterm instanceof XSElementDeclaration)
                        {
                           XSElementDeclaration xe = (XSElementDeclaration)xsterm;
                           XSTypeDefinition nestedTypeDef = xe.getTypeDefinition();

                           //System.out.println("Query nested -> " + xe.getName());
                           result = findXOPTypeDef(nestedTypeDef);
                        }
                     }
                  }
               }
            }

         }

         //System.out.println("result -> " + result);

      }

      return result;

   }

   public void reset()
   {
      scannedItems.clear();
   }

}
