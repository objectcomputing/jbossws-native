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
package org.jboss.test.ws.jaxrpc.wspolicy;

import java.io.ByteArrayInputStream;

import org.apache.ws.policy.Policy;
import org.apache.ws.policy.util.PolicyFactory;
import org.apache.ws.policy.util.PolicyReader;
import org.apache.ws.policy.util.PolicyWriter;
import org.jboss.wsf.test.JBossWSTest;

/**
 * Test the WS-Policy parser
 *
 * @author Thomas.Diesler@jboss.org
 * @since 13-Nov-2005
 */
public class PolicyParserTestCase extends JBossWSTest
{
   public void testPolicyParser() throws Exception
   {
      String inStr =
         "<wsp:Policy xmlns:wsp='http://schemas.xmlsoap.org/ws/2004/09/policy' xmlns:wsse='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wsswssecurity-secext-1.0.xsd'>" +
         "  <wsp:ExactlyOne>" +
         "    <wsp:All>" +
         "      <wsse:SecurityToken>" +
         "        <wsse:TokenType>wsse:Kerberosv5TGT</wsse:TokenType>" +
         "      </wsse:SecurityToken>" +
         "      <wsse:SecurityToken>" +
         "        <wsse:TokenType>wsse:X509v3</wsse:TokenType>" +
         "      </wsse:SecurityToken>" +
         "    </wsp:All>" +
         "  </wsp:ExactlyOne>" +
         "</wsp:Policy>";

      PolicyReader reader = PolicyFactory.getPolicyReader(PolicyFactory.DOM_POLICY_READER);
      PolicyWriter writer = PolicyFactory.getPolicyWriter(PolicyFactory.StAX_POLICY_WRITER);
      Policy p = reader.readPolicy(new ByteArrayInputStream(inStr.getBytes()));

      //writer.writePolicy(p, System.out);      
   }
}
