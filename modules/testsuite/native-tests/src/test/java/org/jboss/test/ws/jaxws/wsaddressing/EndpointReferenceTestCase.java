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
package org.jboss.test.ws.jaxws.wsaddressing;

import org.jboss.ws.extensions.addressing.EndpointReferenceImpl;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.DOMUtils;
import org.w3c.dom.Element;

/** 
 * Test the EndpointReferenceImpl
 *
 * @author Thomas.Diesler@jboss.org
 * @since 13-Nov-2005
 */
public class EndpointReferenceTestCase extends JBossWSTest
{
   public void testEndpointReferenceParser() throws Exception
   {
      String inStr = 
         "<wsa:EndpointReference fabrikam:eprAttr='eprAttrValue' xmlns:wsa='http://www.w3.org/2005/08/addressing' xmlns:fabrikam='http://www.fabrikam.com/ns'>" +
         "  <wsa:Address fabrikam:addrAttr='addrAttrValue'>http://www.fabrikam123.example/acct</wsa:Address>" +
         "  <wsa:ReferenceParameters fabrikam:paramAttr='paramAttrValue'>" +
         "    <fabrikam:CustomerKey>123456789</fabrikam:CustomerKey>" +
         "    <fabrikam:ShoppingCart>ABCDEFG</fabrikam:ShoppingCart>" +
         "  </wsa:ReferenceParameters>" +
         "  <wsa:Metadata fabrikam:metaAttr='metaAttrValue'>" +
         "    <wsp:Policy xmlns:wsp='http://schemas.xmlsoap.org/ws/2004/09/policy' xmlns:wsse='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wsswssecurity-secext-1.0.xsd'>" + 
         "      <wsp:ExactlyOne>" + 
         "        <wsp:All>" + 
         "          <wsse:SecurityToken>" +
         "            <wsse:TokenType>wsse:Kerberosv5TGT</wsse:TokenType>" +
         "          </wsse:SecurityToken>" + 
         "        </wsp:All>" +
         "      </wsp:ExactlyOne>" +
         "    </wsp:Policy>" +
         "  </wsa:Metadata>" +
         "  <fabrikam:eprElement>123456789</fabrikam:eprElement>" +
         "</wsa:EndpointReference>";
      
      Element inElement = DOMUtils.parse(inStr);
      EndpointReferenceImpl epr = new EndpointReferenceImpl(inElement);
      Element outElement = epr.toElement();
      
      assertEquals(inElement, outElement);
   }
}
