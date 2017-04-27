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
package org.jboss.test.ws.jaxws.wsrm.deserialization;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.xml.soap.SOAPMessage;

import org.jboss.ws.extensions.wsrm.common.RMHelper;
import org.jboss.ws.extensions.wsrm.protocol.RMMessageFactory;
import org.jboss.ws.extensions.wsrm.protocol.RMProvider;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMAckRequested;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCloseSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCloseSequenceResponse;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCreateSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMCreateSequenceResponse;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMIncompleteSequenceBehavior;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSequenceAcknowledgement;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMSerializable;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMTerminateSequence;
import org.jboss.ws.extensions.wsrm.protocol.spi.RMTerminateSequenceResponse;
import org.jboss.wsf.test.JBossWSTest;

/**
 * WS-RM messages de/serialization test case
 * @author richard.opalka@jboss.com
 */
public final class RMDeSerializationTestCase extends JBossWSTest
{
   private static final RMMessageFactory WSRM_200702_FACTORY = RMProvider.get().getMessageFactory();
   
   private static final String CREATE_SEQUENCE_MESSAGE
      = "<soap:Envelope "
      + "   xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\""
      + "   xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702\""
      + "   xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
      + "   <soap:Header>"
      + "      <wsa:MessageID>http://Business456.com/guid/0baaf88d-483b-4ecf-a6d8</wsa:MessageID>"
      + "      <wsa:To>http://example.com/serviceB/123</wsa:To>"
      + "      <wsa:Action>http://docs.oasis-open.org/ws-rx/wsrm/200702/CreateSequence</wsa:Action>"
      + "      <wsa:ReplyTo>"
      + "         <wsa:Address>http://Business456.com/serviceA/789</wsa:Address>"
      + "      </wsa:ReplyTo>"
      + "   </soap:Header>"
      + "   <soap:Body>"
      + "      <wsrm:CreateSequence>"
      + "         <wsrm:AcksTo>"
      + "            <wsa:Address>http://Business456.com/serviceA/789</wsa:Address>"
      + "         </wsrm:AcksTo>"
      + "         <wsrm:Expires>PT0S</wsrm:Expires>" 
      + "         <wsrm:Offer>"
      + "            <wsrm:Identifier>http://Business456.com/RM/ABC</wsrm:Identifier>"
      + "            <wsrm:Endpoint>"
      + "               <wsa:Address>http://Business456.com/serviceA/ASDF</wsa:Address>"
      + "            </wsrm:Endpoint>"
      + "            <wsrm:Expires>PT1S</wsrm:Expires>"
      + "            <wsrm:IncompleteSequenceBehavior>DiscardEntireSequence</wsrm:IncompleteSequenceBehavior>"
      + "         </wsrm:Offer>"
      + "      </wsrm:CreateSequence>"
      + "   </soap:Body>"
      + "</soap:Envelope>";
   
   private static final String CREATE_SEQUENCE_RESPONSE_MESSAGE
      = "<soap:Envelope"
      + "   xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\""
      + "   xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702\""
      + "   xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
      + "   <soap:Header>"
      + "      <wsa:To>http://Business456.com/serviceA/789</wsa:To>"
      + "      <wsa:RelatesTo>http://Business456.com/guid/0baaf88d-483b-4ecf-a6d8</wsa:RelatesTo>"
      + "      <wsa:Action>http://docs.oasis-open.org/ws-rx/wsrm/200702/CreateSequenceResponse</wsa:Action>"
      + "   </soap:Header>"
      + "   <soap:Body>"
      + "      <wsrm:CreateSequenceResponse>"
      + "         <wsrm:Identifier>http://Business456.com/RM/ABC</wsrm:Identifier>"
      + "         <wsrm:Expires>PT0S</wsrm:Expires>"
      + "         <wsrm:IncompleteSequenceBehavior>DiscardFollowingFirstGap</wsrm:IncompleteSequenceBehavior>"
      + "         <wsrm:Accept>"
      + "            <wsrm:AcksTo>"
      + "               <wsa:Address>http://Business456.com/serviceA/ASDF</wsa:Address>"
      + "            </wsrm:AcksTo>"
      + "         </wsrm:Accept>"
      + "      </wsrm:CreateSequenceResponse>"
      + "   </soap:Body>"
      + "</soap:Envelope>";
   
   private static final String SEQUENCE_PLUS_ACKREQUESTED_MESSAGE
      = "<soap:Envelope"
      + "   xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\""
      + "   xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702\""
      + "   xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
      + "   <soap:Header>"
      + "      <wsa:MessageID>http://Business456.com/guid/71e0654e-5ce8-477b-bb9d</wsa:MessageID>"
      + "      <wsa:To>http://example.com/serviceB/123</wsa:To>"
      + "      <wsa:From>"
      + "         <wsa:Address>http://Business456.com/serviceA/789</wsa:Address>"
      + "      </wsa:From>"
      + "      <wsa:Action>http://example.com/serviceB/123/request</wsa:Action>"
      + "      <wsrm:Sequence>"
      + "         <wsrm:Identifier>http://Business456.com/RM/ABC</wsrm:Identifier>"
      + "         <wsrm:MessageNumber>1</wsrm:MessageNumber>"
      + "      </wsrm:Sequence>"
      + "      <wsrm:AckRequested>"
      + "         <wsrm:Identifier>http://Business456.com/RM/ABC</wsrm:Identifier>"
      + "      </wsrm:AckRequested>"
      + "   </soap:Header>"
      + "   <soap:Body><!-- Some Application Data --></soap:Body>"
      + "</soap:Envelope>";

   private static final String SEQUENCE_ACKNOWLEDGEMENT_MESSAGE_1
      = "<soap:Envelope"
      + "   xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\""
      + "   xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702\""
      + "   xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
      + "   <soap:Header>"
      + "      <wsa:MessageID>http://example.com/guid/0baaf88d-483b-4ecf-a6d8</wsa:MessageID>"
      + "      <wsa:To>http://Business456.com/serviceA/789</wsa:To>"
      + "      <wsa:From>"
      + "         <wsa:Address>http://example.com/serviceB/123</wsa:Address>"
      + "      </wsa:From>"
      + "      <wsa:Action>http://docs.oasis-open.org/ws-rx/wsrm/200702/SequenceAcknowledgement</wsa:Action>"
      + "      <wsrm:SequenceAcknowledgement>"
      + "         <wsrm:Identifier>http://Business456.com/RM/ABC</wsrm:Identifier>"
      + "         <wsrm:AcknowledgementRange Upper='1' Lower='1'/>"
      + "         <wsrm:AcknowledgementRange Upper='3' Lower='3'/>"
      + "         <wsrm:Final/>"
      + "      </wsrm:SequenceAcknowledgement>"
      + "   </soap:Header>"
      + "   <soap:Body/>"
      + "</soap:Envelope>";
   
   private static final String SEQUENCE_ACKNOWLEDGEMENT_MESSAGE_2
   = "<soap:Envelope"
   + "   xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\""
   + "   xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702\""
   + "   xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
   + "   <soap:Header>"
   + "      <wsa:MessageID>http://example.com/guid/0baaf88d-483b-4ecf-a6d8</wsa:MessageID>"
   + "      <wsa:To>http://Business456.com/serviceA/789</wsa:To>"
   + "      <wsa:From>"
   + "         <wsa:Address>http://example.com/serviceB/123</wsa:Address>"
   + "      </wsa:From>"
   + "      <wsa:Action>http://docs.oasis-open.org/ws-rx/wsrm/200702/SequenceAcknowledgement</wsa:Action>"
   + "      <wsrm:SequenceAcknowledgement>"
   + "         <wsrm:Identifier>http://Business456.com/RM/ABC</wsrm:Identifier>"
   + "         <wsrm:Nack>2</wsrm:Nack>"
   + "      </wsrm:SequenceAcknowledgement>"
   + "   </soap:Header>"
   + "   <soap:Body/>"
   + "</soap:Envelope>";

   private static final String SEQUENCE_ACKNOWLEDGEMENT_MESSAGE_3
   = "<soap:Envelope"
   + "   xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\""
   + "   xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702\""
   + "   xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
   + "   <soap:Header>"
   + "      <wsa:MessageID>http://example.com/guid/0baaf88d-483b-4ecf-a6d8</wsa:MessageID>"
   + "      <wsa:To>http://Business456.com/serviceA/789</wsa:To>"
   + "      <wsa:From>"
   + "         <wsa:Address>http://example.com/serviceB/123</wsa:Address>"
   + "      </wsa:From>"
   + "      <wsa:Action>http://docs.oasis-open.org/ws-rx/wsrm/200702/SequenceAcknowledgement</wsa:Action>"
   + "      <wsrm:SequenceAcknowledgement>"
   + "         <wsrm:Identifier>http://Business456.com/RM/ABC</wsrm:Identifier>"
   + "         <wsrm:None/>"
   + "      </wsrm:SequenceAcknowledgement>"
   + "   </soap:Header>"
   + "   <soap:Body/>"
   + "</soap:Envelope>";

   private static final String CLOSE_SEQUENCE_MESSAGE
   = "<soap:Envelope"
   + "   xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\""
   + "   xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702\""
   + "   xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
   + "   <soap:Header>"
   + "      <wsa:MessageID>http://Business456.com/guid/0baaf88d-483b-4ecf-a6d8</wsa:MessageID>"
   + "      <wsa:To>http://example.com/serviceB/123</wsa:To>"
   + "      <wsa:Action>http://docs.oasis-open.org/ws-rx/wsrm/200702/TerminateSequence</wsa:Action>"
   + "      <wsa:From>"
   + "         <wsa:Address>http://Business456.com/serviceA/789</wsa:Address>"
   + "      </wsa:From>"
   + "   </soap:Header>"
   + "   <soap:Body>"
   + "      <wsrm:CloseSequence>"
   + "         <wsrm:Identifier>http://Business456.com/RM/ABC</wsrm:Identifier>"
   + "         <wsrm:LastMsgNumber>3</wsrm:LastMsgNumber>"
   + "      </wsrm:CloseSequence>"
   + "   </soap:Body>"
   + "</soap:Envelope>";

   private static final String TERMINATE_SEQUENCE_MESSAGE
      = "<soap:Envelope"
      + "   xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\""
      + "   xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702\""
      + "   xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
      + "   <soap:Header>"
      + "      <wsa:MessageID>http://Business456.com/guid/0baaf88d-483b-4ecf-a6d8</wsa:MessageID>"
      + "      <wsa:To>http://example.com/serviceB/123</wsa:To>"
      + "      <wsa:Action>http://docs.oasis-open.org/ws-rx/wsrm/200702/TerminateSequence</wsa:Action>"
      + "      <wsa:From>"
      + "         <wsa:Address>http://Business456.com/serviceA/789</wsa:Address>"
      + "      </wsa:From>"
      + "   </soap:Header>"
      + "   <soap:Body>"
      + "      <wsrm:TerminateSequence>"
      + "         <wsrm:Identifier>http://Business456.com/RM/ABC</wsrm:Identifier>"
      + "         <wsrm:LastMsgNumber>3</wsrm:LastMsgNumber>"
      + "      </wsrm:TerminateSequence>"
      + "   </soap:Body>"
      + "</soap:Envelope>";
   
   private static final String CLOSE_SEQUENCE_RESPONSE_MESSAGE
      = "<soap:Envelope"
      + "   xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\""
      + "   xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702\""
      + "   xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
      + "   <soap:Header>"
      + "      <wsa:MessageID>http://Business456.com/guid/0baaf88d-483b-4ecf-a6d8</wsa:MessageID>"
      + "      <wsa:To>http://example.com/serviceA/789</wsa:To>"
      + "      <wsa:Action>http://docs.oasis-open.org/ws-rx/wsrm/200702/TerminateSequenceResponse</wsa:Action>"
      + "      <wsa:RelatesTo>http://Business456.com/guid/0baaf88d-483b-4ecf-a6d8</wsa:RelatesTo>"
      + "      <wsa:From>"
      + "         <wsa:Address>http://Business456.com/serviceA/789</wsa:Address>"
      + "      </wsa:From>"
      + "   </soap:Header>"
      + "   <soap:Body>"
      + "      <wsrm:CloseSequenceResponse>"
      + "         <wsrm:Identifier>http://Business456.com/RM/ABC</wsrm:Identifier>"
      + "      </wsrm:CloseSequenceResponse>"
      + "   </soap:Body>"
      + "</soap:Envelope>";
   
   private static final String TERMINATE_SEQUENCE_RESPONSE_MESSAGE
   = "<soap:Envelope"
   + "   xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\""
   + "   xmlns:wsrm=\"http://docs.oasis-open.org/ws-rx/wsrm/200702\""
   + "   xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
   + "   <soap:Header>"
   + "      <wsa:MessageID>http://Business456.com/guid/0baaf88d-483b-4ecf-a6d8</wsa:MessageID>"
   + "      <wsa:To>http://example.com/serviceA/789</wsa:To>"
   + "      <wsa:Action>http://docs.oasis-open.org/ws-rx/wsrm/200702/TerminateSequenceResponse</wsa:Action>"
   + "      <wsa:RelatesTo>http://Business456.com/guid/0baaf88d-483b-4ecf-a6d8</wsa:RelatesTo>"
   + "      <wsa:From>"
   + "         <wsa:Address>http://Business456.com/serviceA/789</wsa:Address>"
   + "      </wsa:From>"
   + "   </soap:Header>"
   + "   <soap:Body>"
   + "      <wsrm:TerminateSequenceResponse>"
   + "         <wsrm:Identifier>http://Business456.com/RM/ABC</wsrm:Identifier>"
   + "      </wsrm:TerminateSequenceResponse>"
   + "   </soap:Body>"
   + "</soap:Envelope>";
   
   public void testSequenceAcknowledgementDeserialization1() throws Exception
   {
      RMSequenceAcknowledgement sequenceAcknowledgement = WSRM_200702_FACTORY.newSequenceAcknowledgement();
      sequenceAcknowledgement.deserializeFrom(toSOAPMessage(SEQUENCE_ACKNOWLEDGEMENT_MESSAGE_1));
      // perform assertion
      assertEquals(sequenceAcknowledgement.getIdentifier(), "http://Business456.com/RM/ABC");
      assertTrue(sequenceAcknowledgement.isFinal());
      assertFalse(sequenceAcknowledgement.isNone());
      assertEquals(sequenceAcknowledgement.getNacks().size(), 0);
      List<RMSequenceAcknowledgement.RMAcknowledgementRange> ranges = sequenceAcknowledgement.getAcknowledgementRanges();
      assertEquals(ranges.size(), 2);
      RMSequenceAcknowledgement.RMAcknowledgementRange firstRange = ranges.get(0);
      assertEquals(firstRange.getLower(), 1);
      assertEquals(firstRange.getLower(), 1);
      RMSequenceAcknowledgement.RMAcknowledgementRange secondRange = ranges.get(1);
      assertEquals(secondRange.getLower(), 3);
      assertEquals(secondRange.getLower(), 3);
   }
   
   public void testSequenceAcknowledgementSerialization1() throws Exception
   {
      RMSequenceAcknowledgement sequenceAcknowledgementMessage = WSRM_200702_FACTORY.newSequenceAcknowledgement();
      // construct message
      sequenceAcknowledgementMessage.setIdentifier("http://Business456.com/RM/ABC");
      sequenceAcknowledgementMessage.setFinal();
      RMSequenceAcknowledgement.RMAcknowledgementRange firstRange = sequenceAcknowledgementMessage.newAcknowledgementRange();
      firstRange.setLower(1);
      firstRange.setUpper(1);
      sequenceAcknowledgementMessage.addAcknowledgementRange(firstRange);
      RMSequenceAcknowledgement.RMAcknowledgementRange secondRange = sequenceAcknowledgementMessage.newAcknowledgementRange();
      secondRange.setLower(3);
      secondRange.setUpper(3);
      sequenceAcknowledgementMessage.addAcknowledgementRange(secondRange);
      // perform assertion
      assertEquals(sequenceAcknowledgementMessage, SEQUENCE_ACKNOWLEDGEMENT_MESSAGE_1, WSRM_200702_FACTORY);
   }
   
   public void testSequenceAcknowledgementDeserialization2() throws Exception
   {
      RMSequenceAcknowledgement sequenceAcknowledgement = WSRM_200702_FACTORY.newSequenceAcknowledgement();
      sequenceAcknowledgement.deserializeFrom(toSOAPMessage(SEQUENCE_ACKNOWLEDGEMENT_MESSAGE_2));
      // perform assertion
      assertEquals(sequenceAcknowledgement.getIdentifier(), "http://Business456.com/RM/ABC");
      assertFalse(sequenceAcknowledgement.isFinal());
      assertFalse(sequenceAcknowledgement.isNone());
      assertEquals(sequenceAcknowledgement.getAcknowledgementRanges().size(), 0);
      List<Long> nacks = sequenceAcknowledgement.getNacks();
      assertEquals(nacks.size(), 1);
      assertEquals(nacks.get(0).longValue(), 2);
   }

   public void testSequenceAcknowledgementSerialization2() throws Exception
   {
      RMSequenceAcknowledgement sequenceAcknowledgementMessage = WSRM_200702_FACTORY.newSequenceAcknowledgement();
      // construct message
      sequenceAcknowledgementMessage.setIdentifier("http://Business456.com/RM/ABC");
      sequenceAcknowledgementMessage.addNack(2);
      // perform assertion
      assertEquals(sequenceAcknowledgementMessage, SEQUENCE_ACKNOWLEDGEMENT_MESSAGE_2, WSRM_200702_FACTORY);
   }
   
   public void testSequenceAcknowledgementDeserialization3() throws Exception
   {
      RMSequenceAcknowledgement sequenceAcknowledgement = WSRM_200702_FACTORY.newSequenceAcknowledgement();
      sequenceAcknowledgement.deserializeFrom(toSOAPMessage(SEQUENCE_ACKNOWLEDGEMENT_MESSAGE_3));
      // perform assertion
      assertEquals(sequenceAcknowledgement.getIdentifier(), "http://Business456.com/RM/ABC");
      assertFalse(sequenceAcknowledgement.isFinal());
      assertTrue(sequenceAcknowledgement.isNone());
      assertEquals(sequenceAcknowledgement.getAcknowledgementRanges().size(), 0);
      assertEquals(sequenceAcknowledgement.getNacks().size(), 0);
   }
   
   public void testSequenceAcknowledgementSerialization3() throws Exception
   {
      RMSequenceAcknowledgement sequenceAcknowledgementMessage = WSRM_200702_FACTORY.newSequenceAcknowledgement();
      // construct message
      sequenceAcknowledgementMessage.setIdentifier("http://Business456.com/RM/ABC");
      sequenceAcknowledgementMessage.setNone();
      // perform assertion
      assertEquals(sequenceAcknowledgementMessage, SEQUENCE_ACKNOWLEDGEMENT_MESSAGE_3, WSRM_200702_FACTORY);
   }
   
   public void testCreateSequenceMessageDeserialization() throws Exception
   {
      RMCreateSequence createSequenceMessage = WSRM_200702_FACTORY.newCreateSequence();
      createSequenceMessage.deserializeFrom(toSOAPMessage(CREATE_SEQUENCE_MESSAGE));
      // perform assertion
      assertEquals(createSequenceMessage.getAcksTo(), "http://Business456.com/serviceA/789");
      assertEquals(createSequenceMessage.getExpires(), RMHelper.stringToDuration("PT0S"));
      RMCreateSequence.RMOffer offer = createSequenceMessage.getOffer(); 
      assertEquals(offer.getIdentifier(), "http://Business456.com/RM/ABC");
      assertEquals(offer.getEndpoint(), "http://Business456.com/serviceA/ASDF");
      assertEquals(offer.getExpires(), "PT1S");
      assertEquals(offer.getIncompleteSequenceBehavior(), RMIncompleteSequenceBehavior.DISCARD_ENTIRE_SEQUENCE);
   }
   
   public void testCreateSequenceMessageSerialization() throws Exception
   {
      RMCreateSequence createSequenceMessage = WSRM_200702_FACTORY.newCreateSequence();
      // construct message
      createSequenceMessage.setAcksTo("http://Business456.com/serviceA/789");
      createSequenceMessage.setExpires(RMHelper.stringToDuration("PT0S"));
      RMCreateSequence.RMOffer offer = createSequenceMessage.newOffer();
      offer.setIdentifier("http://Business456.com/RM/ABC");
      offer.setEndpoint("http://Business456.com/serviceA/ASDF");
      offer.setExpires("PT1S");
      offer.setIncompleteSequenceBehavior(RMIncompleteSequenceBehavior.DISCARD_ENTIRE_SEQUENCE);
      createSequenceMessage.setOffer(offer);
      // perform assertion
      assertEquals(createSequenceMessage, CREATE_SEQUENCE_MESSAGE, WSRM_200702_FACTORY);
   }
   
   public void testCreateSequenceResponseMessageDeserialization() throws Exception
   {
      RMCreateSequenceResponse createSequenceResponseMessage = WSRM_200702_FACTORY.newCreateSequenceResponse();
      createSequenceResponseMessage.deserializeFrom(toSOAPMessage(CREATE_SEQUENCE_RESPONSE_MESSAGE));
      // perform assertion
      assertEquals(createSequenceResponseMessage.getIdentifier(), "http://Business456.com/RM/ABC");
      assertEquals(createSequenceResponseMessage.getExpires(), RMHelper.stringToDuration("PT0S"));
      assertEquals(createSequenceResponseMessage.getIncompleteSequenceBehavior(), RMIncompleteSequenceBehavior.DISCARD_FOLLOWING_FIRST_GAP);
      RMCreateSequenceResponse.RMAccept accept = createSequenceResponseMessage.getAccept();
      assertEquals(accept.getAcksTo(), "http://Business456.com/serviceA/ASDF");
   }
   
   public void testCreateSequenceResponseMessageSerialization() throws Exception
   {
      RMCreateSequenceResponse createSequenceResponse = WSRM_200702_FACTORY.newCreateSequenceResponse();
      // construct message
      createSequenceResponse.setIdentifier("http://Business456.com/RM/ABC");
      createSequenceResponse.setExpires(RMHelper.stringToDuration("PT0S"));
      createSequenceResponse.setIncompleteSequenceBehavior(RMIncompleteSequenceBehavior.DISCARD_FOLLOWING_FIRST_GAP);
      RMCreateSequenceResponse.RMAccept accept = createSequenceResponse.newAccept();
      accept.setAcksTo("http://Business456.com/serviceA/ASDF");
      createSequenceResponse.setAccept(accept);
      // perform assertion
      assertEquals(createSequenceResponse, CREATE_SEQUENCE_RESPONSE_MESSAGE, WSRM_200702_FACTORY);
   }
   
   public void testCloseSequenceMessageDeserialization() throws Exception
   {
      RMCloseSequence closeSequence = WSRM_200702_FACTORY.newCloseSequence();
      closeSequence.deserializeFrom(toSOAPMessage(CLOSE_SEQUENCE_MESSAGE));
      // perform assertion
      assertEquals(closeSequence.getIdentifier(), "http://Business456.com/RM/ABC");
      assertEquals(closeSequence.getLastMsgNumber(), 3);
   }
   
   public void testCloseSequenceMessageSerialization() throws Exception
   {
      RMCloseSequence closeSequence = WSRM_200702_FACTORY.newCloseSequence();
      // construct message
      closeSequence.setIdentifier("http://Business456.com/RM/ABC");
      closeSequence.setLastMsgNumber(3);
      // perform assertion
      assertEquals(closeSequence, CLOSE_SEQUENCE_MESSAGE, WSRM_200702_FACTORY);
   }

   public void testCloseSequenceResponseMessageDeserialization() throws Exception
   {
      RMCloseSequenceResponse closeSequenceResponse = WSRM_200702_FACTORY.newCloseSequenceResponse();
      closeSequenceResponse.deserializeFrom(toSOAPMessage(CLOSE_SEQUENCE_RESPONSE_MESSAGE));
      // perform assertion
      assertEquals(closeSequenceResponse.getIdentifier(), "http://Business456.com/RM/ABC");
   }
   
   public void testCloseSequenceResponseMessageSerialization() throws Exception
   {
      RMCloseSequenceResponse closeSequenceResponse = WSRM_200702_FACTORY.newCloseSequenceResponse();
      // construct message
      closeSequenceResponse.setIdentifier("http://Business456.com/RM/ABC");
      // perform assertion
      assertEquals(closeSequenceResponse, CLOSE_SEQUENCE_RESPONSE_MESSAGE, WSRM_200702_FACTORY);
   }
   
   public void testTerminateSequenceMessageDeserialization() throws Exception
   {
      RMTerminateSequence terminateSequence = WSRM_200702_FACTORY.newTerminateSequence();
      terminateSequence.deserializeFrom(toSOAPMessage(TERMINATE_SEQUENCE_MESSAGE));
      // perform assertion
      assertEquals(terminateSequence.getIdentifier(), "http://Business456.com/RM/ABC");
      assertEquals(terminateSequence.getLastMsgNumber(), 3);
   }
   
   public void testTerminateSequenceMessageSerialization() throws Exception
   {
      RMTerminateSequence terminateSequence = WSRM_200702_FACTORY.newTerminateSequence();
      // construct message
      terminateSequence.setIdentifier("http://Business456.com/RM/ABC");
      terminateSequence.setLastMsgNumber(3);
      // perform assertion
      assertEquals(terminateSequence, TERMINATE_SEQUENCE_MESSAGE, WSRM_200702_FACTORY);
   }
   
   public void testTerminateSequenceResponseMessageDeserialization() throws Exception
   {
      RMTerminateSequenceResponse terminateSequenceResponse = WSRM_200702_FACTORY.newTerminateSequenceResponse();
      terminateSequenceResponse.deserializeFrom(toSOAPMessage(TERMINATE_SEQUENCE_RESPONSE_MESSAGE));
      // perform assertion
      assertEquals(terminateSequenceResponse.getIdentifier(), "http://Business456.com/RM/ABC");
   }
   
   public void testTerminateSequenceResponseMessageSerialization() throws Exception
   {
      RMTerminateSequenceResponse terminateSequenceResponse = WSRM_200702_FACTORY.newTerminateSequenceResponse();
      // construct message
      terminateSequenceResponse.setIdentifier("http://Business456.com/RM/ABC");
      // perform assertion
      assertEquals(terminateSequenceResponse, TERMINATE_SEQUENCE_RESPONSE_MESSAGE, WSRM_200702_FACTORY);
   }
   
   public void testSequenceMessageSerialization() throws Exception
   {
      RMSequence sequence = WSRM_200702_FACTORY.newSequence();
      sequence.deserializeFrom(toSOAPMessage(SEQUENCE_PLUS_ACKREQUESTED_MESSAGE));
      // perform assertion
      assertEquals(sequence.getIdentifier(), "http://Business456.com/RM/ABC");
      assertEquals(sequence.getMessageNumber(), 1);
   }
   
   public void testSequenceMessageDeserialization() throws Exception
   {
      RMSequence sequence = WSRM_200702_FACTORY.newSequence();
      // construct message
      sequence.setIdentifier("http://Business456.com/RM/ABC");
      sequence.setMessageNumber(1);
      // perform assertion
      assertEquals(sequence, SEQUENCE_PLUS_ACKREQUESTED_MESSAGE, WSRM_200702_FACTORY);
   }
   
   public void testAckRequestedMessageSerialization() throws Exception
   {
      RMAckRequested ackRequested = WSRM_200702_FACTORY.newAckRequested();
      ackRequested.deserializeFrom(toSOAPMessage(SEQUENCE_PLUS_ACKREQUESTED_MESSAGE));
      // perform assertion
      assertEquals(ackRequested.getIdentifier(), "http://Business456.com/RM/ABC");
   }
   
   public void testAckRequestedMessageDeserialization() throws Exception
   {
      RMAckRequested ackRequested = WSRM_200702_FACTORY.newAckRequested();
      // construct message
      ackRequested.setIdentifier("http://Business456.com/RM/ABC");
      // perform assertion
      assertEquals(ackRequested, SEQUENCE_PLUS_ACKREQUESTED_MESSAGE, WSRM_200702_FACTORY);
   }
   
   // TODO: implement other de/serializations
   
   private static void assertEquals(RMSerializable serializable, String exemplar, RMMessageFactory factory) throws Exception
   {
      // serialize constructed message
      SOAPMessage createdSOAPMessage = newEmptySOAPMessage();
      serializable.serializeTo(createdSOAPMessage);
      // deserialize from constructed message
      RMSerializable serializable1 = newEmptySerializable(factory, serializable);
      serializable1.deserializeFrom(createdSOAPMessage);
      // deserialize from reference message
      RMSerializable serializable2 = newEmptySerializable(factory, serializable);
      serializable2.deserializeFrom(toSOAPMessage(exemplar));
      // perform assertion
      assertEquals(serializable1, serializable2);
   }
   
   private static RMSerializable newEmptySerializable(RMMessageFactory factory, RMSerializable helper)
   {
      if (helper instanceof RMCreateSequence)
         return factory.newCreateSequence();
      if (helper instanceof RMCreateSequenceResponse)
         return factory.newCreateSequenceResponse();
      if (helper instanceof RMCloseSequence)
         return factory.newCloseSequence();
      if (helper instanceof RMCloseSequenceResponse)
         return factory.newCloseSequenceResponse();
      if (helper instanceof RMTerminateSequence)
         return factory.newTerminateSequence();
      if (helper instanceof RMTerminateSequenceResponse)
         return factory.newTerminateSequenceResponse();
      if (helper instanceof RMSequence)
         return factory.newSequence();
      if (helper instanceof RMAckRequested)
         return factory.newAckRequested();
      if (helper instanceof RMSequenceAcknowledgement)
         return factory.newSequenceAcknowledgement();
      
      throw new IllegalArgumentException();
   }
   
   private static SOAPMessage toSOAPMessage(String data) throws Exception
   {
      javax.xml.soap.MessageFactory factory = javax.xml.soap.MessageFactory.newInstance();
      return factory.createMessage(null, new ByteArrayInputStream(data.getBytes()));
   }
   
   private static SOAPMessage newEmptySOAPMessage() throws Exception
   {
      javax.xml.soap.MessageFactory factory = javax.xml.soap.MessageFactory.newInstance();
      return factory.createMessage();
   }

}
