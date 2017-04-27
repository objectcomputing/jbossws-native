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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ws.Constants;
import org.jboss.ws.metadata.wsdl.Extendable;
import org.jboss.ws.metadata.wsdl.WSDLBinding;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperation;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperationInput;
import org.jboss.ws.metadata.wsdl.WSDLBindingOperationOutput;
import org.jboss.ws.metadata.wsdl.WSDLDefinitions;
import org.jboss.ws.metadata.wsdl.WSDLEndpoint;
import org.jboss.ws.metadata.wsdl.WSDLExtensibilityElement;
import org.jboss.ws.metadata.wsdl.WSDLInterface;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperation;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationInfault;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationInput;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutfault;
import org.jboss.ws.metadata.wsdl.WSDLInterfaceOperationOutput;
import org.jboss.ws.metadata.wsdl.WSDLProperty;
import org.jboss.ws.metadata.wsdl.WSDLService;
import org.jboss.ws.tools.exceptions.JBossWSToolsException;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/**
 *  WSDL Validator Helper class
 *  @author <mailto:Anil.Saldhana@jboss.org>Anil Saldhana
 *  @since  August 1, 2005 
 */ 
public class WSDLValidationHelper
{
   
   private final static Logger log = Logger.getLogger(WSDLValidationHelper.class);
   
   /**
    * Validate the WSDLBinding objects
    * @param w1 WSDLDefinitions object for the first wsdl
    * @param w2 WSDLDefinition object for the second wsdl
    * @return true - if match, false - otherwise
    * @throws JBossWSToolsException
    */
   public static boolean validateBindings(WSDLDefinitions w1, WSDLDefinitions w2) 
   throws JBossWSToolsException
   {  boolean bool = true;
      WSDLBinding[] bindings1 = w1.getBindings();
      WSDLBinding[] bindings2 = w2.getBindings();
      if (bindings1 == null || bindings1.length == 0)
      {
         throw new JBossWSToolsException("bindings in first wsdl cannot be null"); 
      }
      if(bindings2 == null || bindings2.length == 0)
      {
         throw new JBossWSToolsException("bindings in second wsdl cannot be null"); 
      }
      if(bindings1.length != bindings2.length)
         throw new JBossWSToolsException("Mismatch in the number of bindings"); 
      
      for (int i = 0; i < bindings1.length; i++)
      {
         WSDLBinding binding1 = bindings1[i];
         WSDLBinding binding2 = bindings2[i];
         bool = bool && validateExtensibilityElements(binding1, binding2);
         bool = bool && validateBindingOperations(binding1.getOperations(),binding2.getOperations());
      }
      return bool;
   }
   
   
   /**
    * Validates the namespace definitions in the wsdl
    * @param w1 WSDL Definitions for the first wsdl
    * @param w2 WSDL Definitions for the second wsdl
    * @return true if the namespace definitions match
    * @throws JBossWSToolsException any inconsistencies
    */
   public static boolean validateInterfaces(WSDLDefinitions w1, WSDLDefinitions w2) 
   throws JBossWSToolsException
   {
      boolean bool = false;
      
      WSDLInterface[] wiarr1 = w1.getInterfaces(); 
      WSDLInterface[] wiarr2 = w2.getInterfaces();
      
      if(wiarr1.length != wiarr2.length)
         throw new JBossWSToolsException("Number of interfaces mismatch");
      
      int len = wiarr1.length;
      
      for(int i = 0; i < len; i++)
      {
         WSDLInterface wi1 = wiarr1[i];
         WSDLInterface wi2 = wiarr2[i];
         if(checkQNameEquality(wi1.getName(),wi2.getName()) == false) 
            throw new JBossWSToolsException("Interface mismatch");
         WSDLInterfaceOperation[] wioparr1 = wi1.getOperations();
         WSDLInterfaceOperation[] wioparr2 = wi2.getOperations();
         
         if(wioparr1.length != wioparr2.length)
            throw new JBossWSToolsException("Number of Interface Operations mismatch");
         int innerlen = wioparr1.length;
         for(int j = 0 ; j< innerlen; j++)
         {
            bool = validateInterfaceOperation(wioparr1[j],wioparr2[j]);
            if(bool == false) 
               throw new JBossWSToolsException("validation Interface Operations failed");
         }
      }
      return bool;
   }
   
   /**
    * Validates the extensibility elements contained into the specified Extendable
    * objects.
    * @param w1
    * @param w2
    * @return
    * @throws JBossWSToolsException
    */
   public static boolean validateExtensibilityElements(Extendable ext1, Extendable ext2)
   throws JBossWSToolsException
   {
      boolean bool = true;
      //add validation of further extensibility element types below
      if (bool) bool = validatePolicyElements(ext1,ext2);
      
      return bool;
   }
   
   
   private static boolean validatePolicyElements(Extendable ext1, Extendable ext2)
   throws JBossWSToolsException
   {
      //policies
      List<WSDLExtensibilityElement> pol1 = new ArrayList<WSDLExtensibilityElement>(
            ext1.getExtensibilityElements(Constants.WSDL_ELEMENT_POLICY));
      List<WSDLExtensibilityElement> pol2 = new ArrayList<WSDLExtensibilityElement>(
            ext2.getExtensibilityElements(Constants.WSDL_ELEMENT_POLICY));
      //check whether lists are the same size
      if (pol1.size() != pol2.size())
         throw new JBossWSToolsException("Policy WSDLExtensibilityElement mismatch!");
      //check equality
      for (WSDLExtensibilityElement el1 : pol1)
      {
         boolean done = false;
         Iterator it = pol2.iterator();
         WSDLExtensibilityElement el2 = null;
         while (it.hasNext() && !done)
         {
            el2 = (WSDLExtensibilityElement)it.next();
            done = (el1.isRequired() == el2.isRequired()) &&
               checkElementEquality(el1.getElement(), el2.getElement());
         }
         if (!done)
         {
            log.error("Failing policy validation on policy on: "+ext1+" and "+ext2);
            return false;
         }
         pol2.remove(el2);
      }
      //policy references
      List<WSDLExtensibilityElement> polRef1 = new ArrayList<WSDLExtensibilityElement>(
            ext1.getExtensibilityElements(Constants.WSDL_ELEMENT_POLICYREFERENCE));
      List<WSDLExtensibilityElement> polRef2 = new ArrayList<WSDLExtensibilityElement>(
            ext2.getExtensibilityElements(Constants.WSDL_ELEMENT_POLICYREFERENCE));
      //check whether lists are the same size
      if (polRef1.size() != polRef2.size())
         throw new JBossWSToolsException("Policy ref WSDLExtensibilityElement mismatch!");
      //check equality
      for (WSDLExtensibilityElement el1 : polRef1)
      {
         boolean done = false;
         Iterator it = polRef2.iterator();
         WSDLExtensibilityElement el2 = null;
         while (it.hasNext() && !done)
         {
            el2 = (WSDLExtensibilityElement)it.next();
            done = (el1.isRequired() == el2.isRequired()) &&
               checkElementEquality(el1.getElement(), el2.getElement());
         }
         if (!done)
         {
            log.error("Failing policy validation on policy ref on: "+ext1+" and "+ext2);
            return false;
         }
         polRef2.remove(el2);
      }
      //check properties
      WSDLProperty prop1 = ext1.getProperty(Constants.WSDL_PROPERTY_POLICYURIS);
      WSDLProperty prop2 = ext2.getProperty(Constants.WSDL_PROPERTY_POLICYURIS);
      if (prop1 != null || prop2 != null)
      {
         if (prop1 == null || prop2 == null || prop1.isRequired() != prop2.isRequired())
            throw new JBossWSToolsException("Policy prop WSDLExtensibilityElement mismatch!");
         String value1 = prop1.getValue();
         String value2 = prop2.getValue();
         if (value1 != null || value2 != null)
         {
            if (value1 == null || value2 == null || !value1.equalsIgnoreCase(value2))
            {
               log.error("Failing policy validation on policy uri prop on: "+ext1+" and "+ext2);
               return false;
            }
         }
      }
      return true;
   }
   
   /**
    * Validates the namespace definitions in the wsdl
    * @param w1 WSDL Definitions for the first wsdl
    * @param w2 WSDL Definitions for the second wsdl
    * @return true if the namespace definitions match
    * @throws JBossWSToolsException any inconsistencies
    */
   public static boolean validateNSDefinitions(WSDLDefinitions w1, WSDLDefinitions w2) 
   throws JBossWSToolsException
   {
      String ts1 = w1.getTargetNamespace();
      String ts2 = w2.getTargetNamespace();
      if(ts1 == null || ts1.equals(""))
         throw new JBossWSToolsException("Target Namespaces 1 is null");
      if(ts2 == null || ts2.equals(""))
         throw new JBossWSToolsException("Target Namespaces 2 is null");
      if(ts1.equals(ts2) == false)
         throw new JBossWSToolsException("Target Namespaces do not match");
      
      Iterator iter1 = w1.getRegisteredPrefix();
      
      /**
       * IDEA: We get a namespace from the first wsdl and check that it is defined
       * in the other wsdl, irrespective of the prefix match
       */
      while(iter1.hasNext())
      {
         String prefix1 = (String)iter1.next(); 
         
         String ns = w1.getNamespaceURI(prefix1); 
         
         //Ignore the namespaces that are generated by wscompile for arrays
         if(ns.indexOf("arrays") > -1 )
            continue;
         
         String prefix2 = w2.getPrefix(ns);
         
         if(prefix2 == null)
            throw new JBossWSToolsException("Namespace " + ns + " not defined in the second wsdl");    
      }
      
      //Lets check the prefixes and namespaces
      /*while(iter1.hasNext() && iter2.hasNext())
      {
         String prefix1 = iter1.next();
         String prefix2 = iter2.next();
         
         String ns1 = w1.getNamespaceURI(prefix1);
         String ns2 = w2.getNamespaceURI(prefix2);
         
         if(prefix1.equals(prefix2)==false)
            throw new JBossWSToolsException("Prefixes mismatch:"+prefix1 +" and " +prefix2 );
         if(ns1.equals(ns2) == false)
         {
            StringBuffer sb = new StringBuffer("Prefixes:"+prefix1+" and "+prefix2+" have");
            sb.append("Namespaces mismatch:"+ns1 +" and"+ns2);
            throw new JBossWSToolsException(sb.toString());
         }   
      }*/
      return true;
   }
   
   /**
    * Validate the WSDLServices for the two wsdl
    * @param w1 WSDLDefinitions object of the first wsdl
    * @param w2 WSDLDefinitions object of the second wsdl
    * @return true-if match, false - otherwise
    * @throws JBossWSToolsException
    */
   public static boolean validateServices(WSDLDefinitions w1, WSDLDefinitions w2) 
   throws JBossWSToolsException
   {  
      boolean bool = false;
      WSDLService[] services1 = w1.getServices();
      WSDLService[] services2 = w2.getServices();
      if (services1 == null || services1.length == 0)
      {
         throw new JBossWSToolsException("services in first wsdl cannot be null"); 
      }
      if(services2 == null || services2.length == 0)
      {
         throw new JBossWSToolsException("services in second wsdl cannot be null"); 
      }
      if(services1.length != services2.length)
         throw new JBossWSToolsException("Mismatch in the number of services"); 
      
      for (int i = 0; i < services1.length; i++)
      {
         WSDLService s1 = services1[i];
         WSDLService s2 = services2[i];
         bool = checkQNameEquality(s1.getName(),s2.getName());
         if(bool)
         {
            bool = validateExtensibilityElements(s1, s2);
            WSDLEndpoint[] we1 = s1.getEndpoints();
            WSDLEndpoint[] we2 = s2.getEndpoints();
            bool = bool && validateWSDLEndpoints(we1, we2);
         }
      }
      return bool;
   }
   
   /**
    * Compare two QName (s)  for equality
    * @param name1
    * @param name2
    * @return
    */
   private static boolean checkQNameEquality(QName name1, QName name2)
   {
     String str1 = name1.getLocalPart();
     String str2 = name2.getLocalPart();
     if(str1.equals(str2) == false) return false;
     if(name1.getNamespaceURI().equals(name2.getNamespaceURI()) == false) return false;
     return true;
   }
   
   /**
    * Compare two Element (s) for equality (this cares about child elements and attributes only)
    * @param el1
    * @param el2
    * @return
    */
   private static boolean checkElementEquality(Element el1, Element el2)
   {
      QName qName1 = DOMUtils.getElementQName(el1);
      QName qName2 = DOMUtils.getElementQName(el2);
      if (!checkQNameEquality(qName1,qName2)) return false;
      Map attributes1 = DOMUtils.getAttributes(el1); //map <QName, String>
      Map attributes2 = DOMUtils.getAttributes(el2);
      if (attributes1.size()!=attributes2.size()) return false;
      for (Iterator it = attributes1.keySet().iterator(); it.hasNext(); )
      {
         QName key = (QName)it.next();
         if (key.getPrefix().startsWith("xmlns")) continue;
         if (!attributes2.containsKey(key)) return false;
         String value1 = (String)attributes1.get(key);
         String value2 = (String)attributes2.get(key);
         if (!value1.equals(value2)) return false;
      }
      for (Iterator it = DOMUtils.getChildElements(el1); it.hasNext(); )
      {
         Element child1 = (Element)it.next();
         Iterator it2 = DOMUtils.getChildElements(el2, DOMUtils.getElementQName(child1));
         if (!it2.hasNext()) return false;
         Element child2 = (Element)it2.next();
         if (it2.hasNext() || !checkElementEquality(child1, child2)) return false;
      }
      return true;
   }
   
   private static boolean validateInterfaceOperation(WSDLInterfaceOperation w1,
                                 WSDLInterfaceOperation w2) throws JBossWSToolsException
   
   {
      boolean bool = checkQNameEquality(w1.getName(),w2.getName());
      bool = bool && validateExtensibilityElements(w1, w2);
      if(bool)
      {
            //validate the inputs
            WSDLInterfaceOperationInput wiarr1[] = w1.getInputs();
            WSDLInterfaceOperationInput wiarr2[] = w2.getInputs();
            if(wiarr1.length != wiarr2.length)
               throw new JBossWSToolsException("Number of WSDLInterfaceOperationInput mismatch");
            int len = wiarr1.length;
            for(int i = 0 ; i < len; i ++)
            {
               bool = validateInterfaceOperationInput(wiarr1[i],wiarr2[i]);
               if(!bool) return bool;
            }
            //validate the outputs
            WSDLInterfaceOperationOutput woarr1[] = w1.getOutputs();
            WSDLInterfaceOperationOutput woarr2[] = w2.getOutputs();
            if(woarr1.length != woarr2.length)
               throw new JBossWSToolsException("Number of WSDLInterfaceOperationInput mismatch");
            len = woarr1.length;
            for(int i = 0 ; i < len; i ++)
            {
               bool = validateInterfaceOperationOutput(woarr1[i],woarr2[i]);
               if(!bool) return bool;
            }
            //validate the faults
            WSDLInterfaceOperationInfault[] inf1 = w1.getInfaults();
            WSDLInterfaceOperationInfault[] inf2 = w2.getInfaults();
            if( (inf1 != null && inf2 == null) ||
                 (inf1 == null && inf2 != null) )
                 throw new JBossWSToolsException("Infaults mismatch for operation:"+w1.getName());
            if(inf1.length != inf2.length)
                 throw new JBossWSToolsException("Number of Infaults mismatch for operation:"+w1.getName());
            
            len = inf1.length;
            for(int i=0; i< len; i++)
            {
               bool = checkQNameEquality(inf1[i].getRef(),inf2[i].getRef());
               if(bool)
                  bool = checkStringEquality(inf1[i].getMessageLabel(),inf2[i].getMessageLabel());
               if(bool == false) return bool;
            }
              
            WSDLInterfaceOperationOutfault[] outf1 = w1.getOutfaults();
            WSDLInterfaceOperationOutfault[] outf2 = w2.getOutfaults();
            if( (outf1 != null && outf2 == null) ||
                 (outf1 == null && outf2 != null) )
                 throw new JBossWSToolsException("Outfaults mismatch for operation:"+w1.getName());
            if(outf1.length != outf2.length)
               throw new JBossWSToolsException("Number of Infaults mismatch for operation:"+w1.getName());
            
            len = outf1.length;
            for(int i=0; i< len; i++)
            {
               bool = checkQNameEquality(outf1[i].getRef(),outf2[i].getRef());
               if(bool)
                  bool = checkStringEquality(outf1[i].getMessageLabel(),outf2[i].getMessageLabel());
               if(bool == false) return bool;
            }
      }
         
      return bool;
   }
   
   private static boolean checkStringEquality(String one, String two)
   {
      if (one == null && two == null)
         return true;
      
      return one != null && one.equals(two);
   }

   private static boolean validateInterfaceOperationInput(WSDLInterfaceOperationInput i1,
                                   WSDLInterfaceOperationInput i2) throws JBossWSToolsException
   {
      boolean bool = false;
      QName xmlName1 = i1.getElement();
      QName xmlName2 = i2.getElement();
      bool = checkQNameEquality(xmlName1,xmlName2);
      if(bool == false)
         throw new JBossWSToolsException(xmlName1 + " & " + xmlName2 + " mismatch");
      bool = validateExtensibilityElements(i1, i2);
      if(bool == false)
         throw new JBossWSToolsException("WSDLExtensibilityElement mismatch");
      return bool;
   }
   
   private static boolean validateInterfaceOperationOutput(WSDLInterfaceOperationOutput i1,
         WSDLInterfaceOperationOutput i2)
   {
      boolean bool = false;
      bool = checkQNameEquality(i1.getElement(),i2.getElement());
      if(bool == false) return false;
      return bool;
   }
   
   private static boolean validateBindingOperations(WSDLBindingOperation[] barr1, WSDLBindingOperation[] barr2)
   throws JBossWSToolsException
   {
      boolean bool = false;
      if((barr1 == null && barr2 != null) ||
            (barr1 != null && barr2 == null))
         throw new JBossWSToolsException("Mismatch in the Binding Operations");
      if(barr1.length != barr2.length)
         throw new JBossWSToolsException("Mismatch in number of Binding Operations");
      
      int len = barr1.length;
      
      for(int i=0; i< len; i++)
      {
         bool = validateBindingOperation(barr1[i], barr2[i]);
         if(!bool) return bool;
      }
      
      return bool;
   }
   
   private static boolean validateBindingOperation(WSDLBindingOperation b1, WSDLBindingOperation b2)
   throws JBossWSToolsException
   {
      String bname = b1.getRef().getLocalPart();
      boolean bool = false;
      bool = checkQNameEquality(b1.getRef(),b2.getRef());
      WSDLBindingOperationInput[] wb1 = b1.getInputs();
      WSDLBindingOperationInput[] wb2 = b2.getInputs();
      
      if(wb1.length != wb2.length)
         throw new JBossWSToolsException("Mismatch in the number of inputs for binding op:"+bname);
      
      int len = wb1.length;
      for(int i=0; i< len; i++)
      {
         WSDLBindingOperationInput bindin1 = wb1[i];
         WSDLBindingOperationInput bindin2 = wb2[i]; 
         bool = checkStringEquality(bindin1.getMessageLabel(),bindin2.getMessageLabel());
         bool = bool & validateExtensibilityElements(bindin1, bindin2);
         if(!bool) return bool;
      }
      
      WSDLBindingOperationOutput[] wboutarr1 = b1.getOutputs();
      WSDLBindingOperationOutput[] wboutarr2 = b2.getOutputs();
      
      if(wboutarr1.length != wboutarr2.length)
         throw new JBossWSToolsException("Mismatch in the number of outputs for binding op:"+bname);
      
      len = wboutarr1.length;
      for(int i=0; i< len; i++)
      {
         WSDLBindingOperationOutput bindout1 = wboutarr1[i];
         WSDLBindingOperationOutput bindout2 = wboutarr2[i]; 
         bool = checkStringEquality(bindout1.getMessageLabel(),bindout2.getMessageLabel());
      }
      bool = bool & validateExtensibilityElements(b1, b2);
      return bool;
   }
   
   private static boolean validateWSDLEndpoints(WSDLEndpoint[] warr1, WSDLEndpoint[] warr2)
   throws JBossWSToolsException
   {
      boolean bool = false;
      if(warr1 == null || warr1.length == 0)
         throw new JBossWSToolsException("Number of endpoints in first wsdl is null");
      if(warr2 == null || warr2.length == 0)
         throw new JBossWSToolsException("Number of endpoints in second wsdl is null");
      int len = warr1.length;
      for(int i=0; i< len;i++)
      {
         WSDLEndpoint e1 = warr1[i];
         WSDLEndpoint e2 = warr2[i];
         String add1 = e1.getAddress();
         String add2 = e2.getAddress();
         if(add1.equals(add2) == false)
            throw new JBossWSToolsException("Endpoint addresses do not match");
         bool = checkQNameEquality(e1.getBinding(),e2.getBinding());
         if(bool == false) 
            throw new JBossWSToolsException("Endpoint binding do not match");
         bool = checkQNameEquality(e1.getName(),e2.getName()); 
         if(bool == false) 
            throw new JBossWSToolsException("Endpoint Names do not match"); 
         bool = bool && validateExtensibilityElements(e1, e2);
      }
      return bool;
   }
}
