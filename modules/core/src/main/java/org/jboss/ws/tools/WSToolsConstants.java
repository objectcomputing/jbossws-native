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
package org.jboss.ws.tools;

import org.jboss.ws.Constants;
 

/**
 * A collection of constants relevant to JBossWS Tools
 *
 * @author Thomas.Diesler@jboss.org
 * @author Anil.Saldhana@jboss.org
 * @since 01-Aug-2005
 */
public interface WSToolsConstants extends Constants
{
   String BASE = "http://www.jboss.org/wstools/";
   
   /** Tools Constants */  
   /**
    * Option used to inform the tools subsytems that all the xml types that are generated
    * should be generated under the xml target namespace, irrespective of the java packages
    * from where the Java types come
    */
   static final String WSTOOLS_FEATURE_RESTRICT_TO_TARGET_NS = BASE + "restrict_types_to_TargetNamespace";
   
   /**
    * Should the schema definitions be included in the wsdl file or they should be generated 
    * in different files.
    */
   static final String WSTOOLS_FEATURE_INCLUDE_SCHEMA_IN_WSDL = BASE + "include_schema_in_WSDL";
   
   /**
    * Flag to tools that the generated Java Artifacts can contain Java Annotations
    */
   static final String WSTOOLS_FEATURE_USE_ANNOTATIONS = BASE + "Use_Annotations";
   
   /**
    * Constant - Not a feature
    */
   static final String WSTOOLS_CONSTANT_MAPPING_SERVICE_PREFIX = "serviceNS";
   
   /**
    * Constant - Not a feature
    */
   static final String WSTOOLS_CONSTANT_MAPPING_WSDL_MESSAGE_NS = "wsdlMsgNS";
   
   /**
    * Constant - Not a feature
    */
   static final String WSTOOLS_CONSTANT_MAPPING_IN_OUT_HOLDER_PARAM_MODE = "INOUT";
   
   /**
    * Constant - Not a feature
    */
   static final String WSTOOLS_CONSTANT_MAPPING_OUT_HOLDER_PARAM_MODE = "OUT";
   
   /**
    * Constant - Not a feature
    */
   static final String WSTOOLS_CONSTANT_MAPPING_IN_PARAM_MODE = "IN";
}
