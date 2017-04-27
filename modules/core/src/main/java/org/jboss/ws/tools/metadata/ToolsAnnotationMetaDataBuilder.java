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
package org.jboss.ws.tools.metadata;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.holders.Holder;

import org.jboss.ws.WSException;
import org.jboss.ws.core.soap.Style;
import org.jboss.ws.metadata.umdm.FaultMetaData;
import org.jboss.ws.metadata.umdm.OperationMetaData;
import org.jboss.ws.metadata.umdm.ParameterMetaData;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.tools.ToolsUtils;

/**
 *  Builds the ToolsEndpointMetaData using JSR-181 annotations
 *  on the endpoint
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  Oct 20, 2005
 */
public class ToolsAnnotationMetaDataBuilder
{
   private ToolsEndpointMetaData tmd = null;
   private String targetNamespace = null;
   private String typeNamespace = null;

   private Class endpoint = null;

   public ToolsAnnotationMetaDataBuilder(ToolsEndpointMetaData tmd, String targetNamespace, String typeNamespace)
   {
      this.tmd = tmd;
      this.targetNamespace = targetNamespace;
      this.typeNamespace = typeNamespace;
      endpoint = tmd.getServiceEndpointInterface();
   }

   public ToolsEndpointMetaData generate()
   {
      generateOperationMetaData();
      return tmd;
   }

   //PRIVATE METHODS
   private void generateOperationMetaData()
   {
      //    Generate the Operation Metadata
      Method[] marr = endpoint.getDeclaredMethods();
      if( marr != null)
      {
         int len = Array.getLength(marr);
         for(int i = 0; i < len ; i++)
         {
            Method m = marr[i];
            if (WSDLUtils.getInstance().checkIgnoreMethod(m))
               continue;
            tmd.addOperation(getOperationMetaData(m,tmd));
         }
      }
   }

   private OperationMetaData getOperationMetaData(Method m, ToolsEndpointMetaData em)
   {
      String opname = null;
      String soapAction = null;
      //Check if there are annotations
      WebMethod an = m.getAnnotation(WebMethod.class);
      if(an != null)
      {
         opname = an.operationName();
         soapAction = an.action();
      }
      if(opname == null || opname.length() == 0)
         opname = m.getName();

      OperationMetaData om = new OperationMetaData(em, new QName(targetNamespace,opname),
            m.getName());
      om.setSOAPAction(soapAction);

      Style style = Style.RPC;

      SOAPBinding sb = (SOAPBinding)endpoint.getAnnotation(SOAPBinding.class);
      if(sb != null)
      {
         String wsdlStyle = sb.style().name();
         if(wsdlStyle != null && wsdlStyle.equalsIgnoreCase("DOCUMENT"))
            style = Style.DOCUMENT;
      }
      em.setStyle(style);

      Class[] paramTypes = m.getParameterTypes();
      int lenparam = paramTypes != null ? paramTypes.length : 0;

      for (int j = 0; j < lenparam; j++)
      {
         Class paramType = paramTypes[j];
         if(Remote.class.isAssignableFrom(paramType))
            throw new WSException("OpName:" + opname + " param:" + paramType.getName() +
                  " should not extend Remote" );
         //Get the ParameterMetaData for the individual parameters
         om.addParameter(getParameterMetaData(paramType, om, j + 1));
      }


      //Oneway annotation
      Oneway ow = m.getAnnotation(Oneway.class);
      if(ow != null)
         om.setOneWay(true);

      Class ret = m.getReturnType();
      ParameterMetaData retPmd = getParameterMetaDataForReturnType(ret, om, 1);
      if(retPmd != null )
         om.setReturnParameter(retPmd);

      //Take care of exceptions also
      Class[] exarr = m.getExceptionTypes();
      if(exarr != null)
      {
         int len = Array.getLength(exarr);
         int i = 0;
         for(  i = 0 ; i < len ; i++)
         {
            Class exClass = exarr[i];
            if(!RemoteException.class.isAssignableFrom(exClass))
               om.addFault(getFaultMetaData(exClass,om));
         }
      }
      return om;
   }

   private ParameterMetaData getParameterMetaData(Class type, OperationMetaData om,
         int index)
   {
      WebParam wp = (WebParam)type.getAnnotation(WebParam.class);
      String tns = targetNamespace;
      String name = "";
      ParameterMode mode = ParameterMode.IN;

      if( wp != null)
      {
         tns = wp.targetNamespace();
         if(tns == null || tns == "")
            tns = targetNamespace;
         name = wp.name() == "" ? type.getName() + "_" + index : type.getName();
         if( wp.mode() == WebParam.Mode.INOUT )
            mode = ParameterMode.INOUT;
         else
            if(wp.mode() == WebParam.Mode.OUT)
               mode = ParameterMode.OUT;
      }

      if(name == null || name.length() == 0)
         name = this.getXMLName(type) + "_" +index;

      if(typeNamespace != null && typeNamespace.equals(tns) == false)
         tns = typeNamespace; //Types always deal with typeNamespace

      boolean header = wp != null ? wp.header() : false;
      QName xmlType = ToolsUtils.getXMLType(type, tns);
      ParameterMetaData pm = new ParameterMetaData(om, new QName(tns,name),
            xmlType, type.getName());

      return pm;
   }

   private ParameterMetaData getParameterMetaDataForReturnType(Class type,
         OperationMetaData om,  int index)
   {
      if(type == void.class)
         return null;

      if(Remote.class.isAssignableFrom( type ))
         throw new WSException(om.getJavaName() + " has return type which " +
               "should not extend java.rmi.Remote" );
      WebResult wr = (WebResult)type.getAnnotation(WebResult.class);
      String tns = targetNamespace;
      String name = "result";

      if( wr != null)
      {
         tns = wr.targetNamespace();
         if(tns == null || tns == "")
            tns = targetNamespace;
         name = wr.name() == "" ? type.getName() + "_" + index : type.getName();
      }
      else
      {
         //  Check if it is a Holder
         if (Holder.class.isAssignableFrom( type ))
         {
            type = WSDLUtils.getInstance().getJavaTypeForHolder( type );
         }
      }

      if(typeNamespace != null && tns.equals(typeNamespace) == false)
         tns = typeNamespace;
      QName xmlType = ToolsUtils.getXMLType(type, tns);
      ParameterMetaData pm = new ParameterMetaData(om, new QName(tns,name),
            xmlType, type.getName());
      return pm;
   }

   private FaultMetaData getFaultMetaData(Class exType, OperationMetaData om)
   {
      String exname = WSDLUtils.getInstance().getJustClassName(exType);
      QName xmlName = new QName( typeNamespace, exname);

      FaultMetaData fm =  new FaultMetaData(om, xmlName, xmlName, exType.getName());
      return fm;
   }

   private String getXMLName(Class javaClass)
   {
      String name = "";
      WSDLUtils utils = WSDLUtils.getInstance();
      if(Holder.class.isAssignableFrom(javaClass))
         javaClass = utils.getJavaTypeForHolder(javaClass);
      if(javaClass.isArray())
      {
         int len = utils.getArrayDimension(javaClass);
         for(int i = 0; i < len; i++)
            javaClass = javaClass.getComponentType();

         name = utils.getMessagePartForArray(javaClass);
      }
      else
         name = utils.getJustClassName(javaClass);
      return name;
   }
}

