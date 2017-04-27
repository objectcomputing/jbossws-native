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
package org.jboss.test.ws.jaxws.samples.dar;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.AddressingProperties;
import javax.xml.ws.addressing.AttributedURI;
import javax.xml.ws.addressing.JAXWSAConstants;

import org.jboss.test.ws.jaxws.samples.dar.generated.DarEndpoint;
import org.jboss.test.ws.jaxws.samples.dar.generated.DarRequest;
import org.jboss.test.ws.jaxws.samples.dar.generated.DarResponse;
import org.jboss.test.ws.jaxws.samples.dar.generated.DarService;
import org.jboss.ws.core.StubExt;
import org.jboss.ws.extensions.addressing.AddressingClientUtil;

/**
 * DAR addressing client; invokes the DAR addressing endpoint (sync, asynch and oneway) 
 *
 * @author alessio.soldano@jboss.org
 * @since 31-Jan-2008
 */
public class AddressingClient
{
   protected DarEndpoint endpoint;
   protected String replyToHost;
   
   private static AddressingBuilder BUILDER;
   private static final String WSA_TO = "http://org.jboss.test.ws.jaxws.samples.dar/server";
   private static final String WSA_ACTION = "http://org.jboss.test.ws.jaxws.samples.dar/action/processIn";
   private static final String WSA_ACTION_ONEWAY = "http://org.jboss.test.ws.jaxws.samples.dar/action/onewayProcessIn";

   static
   {
      BUILDER = AddressingBuilder.getAddressingBuilder();
   }
   
   public AddressingClient(URL url, String replyToHost)
   {
      DarService service = new DarService(url, new QName("http://org.jboss.ws/samples/dar", "DarService"));
      endpoint = service.getDarEndpointPort();
      ((StubExt)endpoint).setConfigName("Standard WSAddressing Client");
      ClientHelper.setUsernamePassword((BindingProvider)endpoint, "kermit", "thefrog");
      this.replyToHost = replyToHost;
   }
   
   public void run(boolean asynch) throws Exception
   {
      configureAddressingProperties((BindingProvider)endpoint, WSA_ACTION, WSA_TO, "http://" + replyToHost + ":8080/dar-client/replyTo");
      DarRequest request = ClientHelper.getRequest();
      System.out.println(new Date() + " Sending request...");
      if (asynch)
      {
         Response<DarResponse> resp = endpoint.processAsync(request);
         System.out.println("Doing something interesting in the mean time... ;-) ");
         resp.get();
      }
      else
      {
         endpoint.process(request);
      }
      //don't care about the response: it is null since the real one went to the replyTo address
      System.out.println(new Date() + " Done.");
   }
   
   public void runOneway() throws Exception
   {
      configureAddressingProperties((BindingProvider)endpoint, WSA_ACTION_ONEWAY, WSA_TO, "http://" + replyToHost + ":8080/dar-client/replyService");
      DarRequest request = ClientHelper.getRequest();
      System.out.println(new Date() + " Sending request...");
      endpoint.onewayProcess(request);
      System.out.println(new Date() + " Done.");
   }
   
   private static void configureAddressingProperties(BindingProvider bp, String wsaAction, String wsaTo, String replyTo) throws URISyntaxException
   {
      AddressingProperties requestProps = AddressingClientUtil.createDefaultProps(wsaAction, wsaTo);
      AttributedURI messageId = AddressingClientUtil.createMessageID();
      System.out.println("Sent message ID: " + messageId.getURI());
      requestProps.setMessageID(messageId);
      requestProps.setReplyTo(BUILDER.newEndpointReference(new URI(replyTo)));
      bp.getRequestContext().put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES_OUTBOUND, requestProps);
   }
   
   public static void main(String[] args)
   {
      try
      {
         if (args.length == 1)
         {
            AddressingClient client = new AddressingClient(new URL(args[0]), "localhost");
            System.out.println("* Synchronous invocation: ");
            client.run(false);
            System.out.println("\n* Asynchronous invocation: ");
            client.run(true);
            System.out.println("\n* Oneway invocation: ");
            client.runOneway();
         }
         else
         {
            System.out.println("AddressingClient usage:");
            System.out.println("wsrunclient.sh -classpath AddressingClient.jar org.jboss.test.ws.jaxws.samples.dar.AddressingClient http://host:port/dar?wsdl");
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
