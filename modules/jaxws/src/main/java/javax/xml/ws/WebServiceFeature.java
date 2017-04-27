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

/**
 * A WebServiceFeature is used to represent a feature that can be 
 * enabled or disabled for a web service.  
 * <p>
 * The JAX-WS specification will define some standard features and
 * JAX-WS implementors are free to define additional features if
 * necessary.  Vendor specific features may not be portable so 
 * caution should be used when using them. Each Feature definition 
 * MUST define a <code>public static final String ID</code> 
 * that can be used in the Feature annotation to refer 
 * to the feature. This ID MUST be unique across all features
 * of all vendors.  When defining a vendor specific feature ID,
 * use a vendor specific namespace in the ID string. 
 *
 * @see javax.xml.ws.RespectBindingFeature
 * @see javax.xml.ws.soap.AddressingFeature
 * @see javax.xml.ws.soap.MTOMFeature
 * 
 * @since 2.1
 */
public abstract class WebServiceFeature
{
   /**
    * Each Feature definition MUST define a public static final 
    * String ID that can be used in the Feature annotation to refer 
    * to the feature.
    */
   // public static final String ID = "some unique feature Identifier";
   /**
    * Get the unique identifier for this WebServiceFeature.
    * 
    * @return the unique identifier for this feature.
    */
   public abstract String getID();

   /**
    * Specifies if the feature is enabled or disabled
    */
   protected boolean enabled = false;

   protected WebServiceFeature()
   {
   }

   /**
    * Returns <code>true</code> if this feature is enabled.
    *
    * @return <code>true</code> if and only if the feature is enabled .
    */
   public boolean isEnabled()
   {
      return enabled;
   }
}
