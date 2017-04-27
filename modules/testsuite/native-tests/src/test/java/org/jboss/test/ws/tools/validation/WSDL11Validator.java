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
package org.jboss.test.ws.tools.validation;

import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceFault;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperation;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationInput;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutput;
import org.jboss.ws.metadata.wsdl.WSDLProperty;
import org.jboss.ws.metadata.wsdl.WSDLTypes;
import org.jboss.ws.metadata.wsdl.WSDLUtils;
import org.jboss.ws.metadata.wsdl.xmlschema.JBossXSModel;
import org.jboss.ws.tools.exceptions.JBossWSToolsException;

/**
 *  WSDL 11 Validator
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since   Jul 22, 2005
 */

public class WSDL11Validator extends WSDLValidator
{
   private static Logger log = Logger.getLogger(WSDL11Validator.class);
   /**
    *
    */
   public WSDL11Validator()
   {
      super();
   }
   /**
    * Validates that two wsdl files represented by their WSDLDefinition objects
    * are semantically equivalent
    * @param wsdlExp
    * @param wsdlAct
    * @return true - if wsdl files are sematically equivalent, false - otherwise
    */
   public boolean validate(WSDLDefinitions wsdlExp, WSDLDefinitions wsdlAct)
   throws JBossWSToolsException
   {
      boolean bool = super.validate(wsdlExp,wsdlAct);
      if( bool)
         bool =  validateMessages(wsdlExp,wsdlAct);
      if(!bool) log.error("Message Validation Failed");
      return bool;
   }

   //**************************************************************************
   //
   //                   PRIVATE METHODS
   //
   //**************************************************************************
   public boolean validateMessages(WSDLDefinitions w1, WSDLDefinitions w2)
   throws JBossWSToolsException
   {
      WSDLInterface[] intf1 = w1.getInterfaces();
      WSDLInterface[] intf2 = w2.getInterfaces();

      if(intf1.length != intf2.length)
         throw new JBossWSToolsException("Number of portType operations in wsdl mismatch");

      int len = intf1.length;
      for (int i = 0; i < len; i++)
      {
         WSDLInterface i1 = intf1[i];
         WSDLInterface i2 = intf2[i];
         WSDLInterfaceOperation[] ops1 = i1.getSortedOperations();
         WSDLInterfaceOperation[] ops2 = i2.getSortedOperations();

         int lenOps = ops1.length;
         for (int j = 0; j < lenOps; j++)
         {
            if(validateMessage(ops1[j],ops2[j]) == false)
            {
               log.error("Validation of Messages failed");
               throw new JBossWSToolsException("Validation of messages failed");
            }
         }//end for

         // Append the Faults
         WSDLInterfaceFault[] faults1 = i1.getFaults();
         WSDLInterfaceFault[] faults2 = i2.getFaults();
         int lenf = faults1 != null ? faults1.length : 0;
         for (int k = 0; k < lenf; k++)
         {
            WSDLInterfaceFault flt1 = faults1[k];
            WSDLInterfaceFault flt2 = faults2[k];
            QName elt = flt1.getElement();
            QName elt2 = flt2.getElement();
            if(!(elt.getLocalPart().equals(elt2.getLocalPart()) &&
                  elt.getNamespaceURI().equals(elt2.getNamespaceURI())))
            {
               log.error("Faults do not match");
               throw new JBossWSToolsException("Validation of faults failed:"+elt.getLocalPart());
            }

         }
      }//end for
      return true;
   }

   //*********************************************************************************
   //
   //                                PRIVATE METHODS
   //
   //*********************************************************************************
   private void checkNullParametersInconsistency(Object o1, Object o2, Class c)
   {
      WSDLUtils utils = WSDLUtils.getInstance();
      if((o1 == null && o2 != null) || (o1 != null && o2 == null) )
         throw new IllegalStateException(utils.getJustClassName(c) + " does not match");
   }

   private boolean validateMessage(WSDLInterfaceOperation op1,
                           WSDLInterfaceOperation op2 ) throws JBossWSToolsException
   {
      String op1name = op1.getName().getLocalPart();
      String intf1name = op1.getWsdlInterface().getName().toString();
      String op2name = op2.getName().getLocalPart();
      String intf2name = op2.getWsdlInterface().getName().toString();
      if(op1name.equals(op2name) == false)
         throw new JBossWSToolsException(op1name + " does not match with " + op2name);
      if( intf1name.equals(intf2name) == false)
         throw new JBossWSToolsException(intf1name + " does not match with " + intf2name);

      WSDLInterfaceOperationInput[] inputs1 = op1.getInputs();
      WSDLInterfaceOperationInput[] inputs2 = op2.getInputs();
      int lenin1 = inputs1.length;
      for (int i = 0; i < lenin1; i++)
      {
         WSDLInterfaceOperationInput input1 = inputs1[i];
         WSDLInterfaceOperationInput input2 = inputs2[i];
         if(validateInputParts(input1, input2) == false)
            throw new JBossWSToolsException("Validation of input parts failed:" + input1.getElement());
      }

      //Now the return type
      WSDLInterfaceOperationOutput[] outputs1 = op1.getOutputs();
      WSDLInterfaceOperationOutput[] outputs2 = op2.getOutputs();
      int lenout = outputs1.length;

      if(lenout != outputs2.length)
         throw new JBossWSToolsException("Length of operation outputs do not match");

      for (int i = 0; i < lenout; i++)
      {
            WSDLInterfaceOperationOutput out1 = outputs1[i];
            WSDLInterfaceOperationOutput out2 = outputs2[i];
            if(validateOutputParts(out1, out2) == false)
               throw new JBossWSToolsException("Validation of output parts failed:" + out1);
      }
      return true;
   }

   private boolean validateInputParts(WSDLInterfaceOperationInput in1,
         WSDLInterfaceOperationInput in2) throws JBossWSToolsException
   {
      //Check if there are any custom properties
      WSDLInterfaceOperation op1 = in1.getWsdlOperation();
      WSDLInterfaceOperation op2 = in2.getWsdlOperation();
      String zeroarg1 = null;
      String zeroarg2 = null;
      WSDLProperty prop1 = op1.getProperty(Constants.WSDL_PROPERTY_ZERO_ARGS);
      if (prop1 != null)
         zeroarg1 = prop1.getValue();
      WSDLProperty prop2 = op2.getProperty(Constants.WSDL_PROPERTY_ZERO_ARGS);
      if (prop2 != null)
         zeroarg2 = prop2.getValue();
      if(zeroarg1 != null && zeroarg2 != null && zeroarg1.equals(zeroarg2) == false)
         return false;
      if (zeroarg1 != null && "true".equals(zeroarg1))
         return true;

      //Check if there is a property
      WSDLProperty wprop1 = in1.getProperty(Constants.WSDL_PROPERTY_PART_XMLTYPE);
      WSDLProperty wprop2 = in2.getProperty(Constants.WSDL_PROPERTY_PART_XMLTYPE);

      QName el1 = in1.getElement();
      QName el2 = in2.getElement();
      WSDLDefinitions w1 = in1.getWsdlOperation().getWsdlInterface().getWsdlDefinitions();
      WSDLDefinitions w2 = in2.getWsdlOperation().getWsdlInterface().getWsdlDefinitions();
      if(wprop1 != null) el1 = parseQName(wprop1.getValue(),w1);
      if(wprop2 != null) el2 = parseQName(wprop2.getValue(),w2);
      //Validate the QNames by using types

      XSTypeDefinition x1 = getTypeDefinition(el1,w1);
      XSTypeDefinition x2 = getTypeDefinition(el2,w2);
      boolean bool = validateType(x1,x2);

      return bool;
   }

   private boolean validateOutputParts(WSDLInterfaceOperationOutput out1,
         WSDLInterfaceOperationOutput out2) throws JBossWSToolsException
   {
      //Check if there are any custom properties
      WSDLInterfaceOperation op1 = out1.getWsdlOperation();
      WSDLInterfaceOperation op2 = out2.getWsdlOperation();
      String voidreturn1 = null;
      String voidreturn2 = null;
      WSDLProperty prop1 = op1.getProperty(Constants.WSDL_PROPERTY_VOID_RETURN);
      WSDLProperty prop2 = op2.getProperty(Constants.WSDL_PROPERTY_VOID_RETURN);

      if(prop1 != null ) voidreturn1 = prop1.getValue();
      if(prop2 != null ) voidreturn2 = prop2.getValue();
      if(voidreturn1 != null &&
            voidreturn2 != null &&
            voidreturn1.equals(voidreturn2) == false) return false;
      if(voidreturn1 != null && "true".equals(voidreturn1)) return true;

      QName el1 = out1.getElement();
      QName el2 = out2.getElement();
      WSDLDefinitions w1 = out1.getWsdlOperation().getWsdlInterface().getWsdlDefinitions();
      WSDLDefinitions w2 = out2.getWsdlOperation().getWsdlInterface().getWsdlDefinitions();
      if( prop1 != null) el1 = parseQName( prop1.getValue(),w1);
      if( prop2 != null) el2 = parseQName( prop2.getValue(),w2);
      //Validate the QNames by using types
      XSTypeDefinition x1 = getTypeDefinition(el1,w1);
      XSTypeDefinition x2 = getTypeDefinition(el2,w2);
      boolean bool = validateType(x1,x2);

      return bool;
   }

   private XSTypeDefinition getTypeDefinition(QName xmlType, WSDLDefinitions wsdl)
   {
      WSDLTypes types = wsdl.getWsdlTypes();
      JBossXSModel xsmodel = WSDLUtils.getSchemaModel(types);
      return xsmodel.getTypeDefinition(xmlType.getLocalPart(),xmlType.getNamespaceURI());
   }

   private boolean validateType(XSTypeDefinition x1, XSTypeDefinition x2) throws JBossWSToolsException
   {
      boolean bool = false;
      if(x1==null && x2 == null) return true;
      this.checkNullParametersInconsistency(x1,x2,XSTypeDefinition.class);

      if(x1.getName().equals(x2.getName()) == false)
         throw new JBossWSToolsException("Validation of XSType failed:"
                     + x1.getName() + ":" + x2.getName());
      //TODO: Expand comparison of types to include attributes/elements
      if(x1 instanceof XSComplexTypeDefinition &&
            x2 instanceof XSComplexTypeDefinition)
      {
         XSComplexTypeDefinition xc1 = (XSComplexTypeDefinition) x1;
         XSComplexTypeDefinition xc2 = (XSComplexTypeDefinition) x2;
         bool = validateXSComplexTypeDefinitions(xc1, xc2);
      }
      return bool;
   }

   private QName parseQName( String qnamestr, WSDLDefinitions wsdl)
   {
      QName qn = null;
      StringTokenizer st = new StringTokenizer(qnamestr,":");
      while(st.hasMoreTokens())
      {
         String prefix = st.nextToken();
         String ns = wsdl.getNamespaceURI(prefix);
         String localpart = st.nextToken();
         qn = new QName(ns,localpart,prefix);
      }
      return qn;
   }

   private boolean checkXSAttributesEquality(XSAttributeDeclaration x1,
                        XSAttributeDeclaration x2)
   {
      boolean bool = true;
      if(x1.getName().equals(x2.getName())) return false;
      XSTypeDefinition xt1 = x1.getTypeDefinition();
      XSTypeDefinition xt2 = x2.getTypeDefinition();
      this.checkNullParametersInconsistency(xt1,xt2,XSTypeDefinition.class);
      if( xt1 != null && xt2 != null)
      {
         if(xt1.getName().equals(xt2.getName()) == false)
            return false;
      }
      return bool;
   }

   private boolean validateXSComplexTypeDefinitions(XSComplexTypeDefinition xc1,
         XSComplexTypeDefinition xc2 )
   {
      //First lets validate the attributes
      boolean bool = true;
      XSObjectList xobj1 = xc1.getAttributeUses();
      XSObjectList xobj2 = xc2.getAttributeUses();
      this.checkNullParametersInconsistency(xobj1,xobj2,XSObjectList.class);
      if(xobj1 == null && xobj2 == null)
         return true;
      if(xobj1.getLength() != xobj2.getLength())
         return false;
      int len = xobj1.getLength();
      for(int i=0; i<len ; i++)
      {
         XSAttributeDeclaration xat1 = (XSAttributeDeclaration)xobj1.item(i);
         XSAttributeDeclaration xat2 = (XSAttributeDeclaration)xobj1.item(i);
         bool =  checkXSAttributesEquality(xat1,xat2);
      }

      //Validate the particles
      XSParticle xspart1 = xc1.getParticle();
      XSParticle xspart2 = xc2.getParticle();
      XSTerm xt1 = xspart1.getTerm();
      XSTerm xt2 = xspart2.getTerm();
      if(xt1 instanceof XSModelGroup && xt2 instanceof XSModelGroup)
         bool = validateXSModelGroups((XSModelGroup)xt1, (XSModelGroup)xt2);
      return bool;
   }

   private boolean validateXSModelGroups( XSModelGroup xm1,  XSModelGroup xm2)
   {
      boolean bool = true;
      if(xm1.getCompositor() != xm2.getCompositor())
         return false;
      short c = xm1.getCompositor();
      if( c == XSModelGroup.COMPOSITOR_ALL || c == XSModelGroup.COMPOSITOR_SEQUENCE)
      {
         XSObjectList xo1 = xm1.getParticles();
         XSObjectList xo2 = xm2.getParticles();
         if( xo1.getLength() != xo2.getLength())
            throw new IllegalStateException("Length of particles do not match:"+xm1.getName());
         int len = xo1.getLength();
         for(int i=0; i< len; i++)
         {
            XSTerm xterm1 = ((XSParticle)xo1.item(i)).getTerm();
            XSTerm xterm2 = ((XSParticle)xo2.item(i)).getTerm();
            short termType = xterm1.getType();
            if(xterm1.getType() != xterm2.getType())
               throw new IllegalStateException(xm1.getName() + " does not match with "+ xm2.getName());
            if(XSConstants.MODEL_GROUP == termType)
               bool = validateXSModelGroups((XSModelGroup)xterm1, (XSModelGroup)xterm2);
            else
               if(XSConstants.ELEMENT_DECLARATION == termType)
                  bool = validateXSElementDeclaration((XSElementDeclaration)xterm1, (XSElementDeclaration)xterm2);
         }
      }
      return bool;
   }

   private boolean validateXSElementDeclaration(XSElementDeclaration xe1,
         XSElementDeclaration xe2)
   {
      boolean bool = true;
      bool = (xe1.getName().equals(xe2.getName()));
      if(bool)
      {
         //Check if there are types
         XSTypeDefinition xt1 = xe1.getTypeDefinition();
         XSTypeDefinition xt2 = xe2.getTypeDefinition();
         bool = xt1.getName().equals(xt2.getName());
         String ns1 = xt1.getNamespace();
         String ns2 = xt2.getNamespace();

         /**
          * Ignore the namespace if it is wscompile generated arrays namespace
          */
         if(bool && ns1 != null && ns2 != null && (ns1.lastIndexOf("arrays") < 0))
            bool = xt1.getNamespace().equals(xt2.getNamespace());
         //TODO:take care of enclosing CT Definition
      }

      return bool;
   }
}

