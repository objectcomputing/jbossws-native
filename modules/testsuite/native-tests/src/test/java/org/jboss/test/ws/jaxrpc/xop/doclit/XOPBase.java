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
package org.jboss.test.ws.jaxrpc.xop.doclit;

import java.awt.Image;
import java.io.File;

import javax.activation.DataHandler;
import javax.xml.rpc.Stub;
import javax.xml.transform.Source;

import org.jboss.wsf.test.XOPTestSupport;
import org.jboss.test.ws.jaxrpc.xop.shared.PingDataHandler;
import org.jboss.test.ws.jaxrpc.xop.shared.PingDataHandlerResponse;
import org.jboss.test.ws.jaxrpc.xop.shared.PingImage;
import org.jboss.test.ws.jaxrpc.xop.shared.PingImageResponse;
import org.jboss.test.ws.jaxrpc.xop.shared.PingMsg;
import org.jboss.test.ws.jaxrpc.xop.shared.PingMsgResponse;
import org.jboss.test.ws.jaxrpc.xop.shared.PingSource;
import org.jboss.test.ws.jaxrpc.xop.shared.PingSourceResponse;
import org.jboss.ws.core.StubExt;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.common.IOUtils;

/**
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Sep 25, 2006
 */
public abstract class XOPBase extends JBossWSTest {

   private File imgFile = getResourceFile("jaxrpc/xop/shared/attach.jpeg");

   protected abstract XOPPing getPort();

   // ---------------------------------------------------------------------------------
   // Test raw binary data

   public void testRequestResponseOptimized() throws Exception {

      DataHandler dh = new DataHandler("Another plain text attachment", "text/plain");
      byte[] bytesIn = IOUtils.convertToBytes(dh);
      PingMsgResponse value = getPort().ping(new PingMsg("1|1", bytesIn));
      assertNotNull("Return value was null", value);
      byte[] bytesOut = value.getXopContent();
      assertNotNull("Returned xopContent was null", bytesOut);
      assertEquals("Content length doesn't match", bytesIn.length, bytesOut.length);
   }

   public void testResponseOptimized() throws Exception {

      byte[] bytesIn = XOPTestSupport.getBytesFromFile(imgFile);

      // disable MTOM
      ((Stub)getPort())._setProperty(StubExt.PROPERTY_MTOM_ENABLED, Boolean.FALSE);

      PingMsgResponse value = getPort().ping(new PingMsg("0|1", bytesIn));
      assertNotNull("Return value was null",value);
      byte[] bytesOut = value.getXopContent();
      assertNotNull("Return xopContent was null", bytesOut);
      assertEquals("Content length doesn't match", bytesIn.length, bytesOut.length);
   }

   public void testRequestOptimized() throws Exception {

      byte[] bytesIn = XOPTestSupport.getBytesFromFile(imgFile);

      // reusing the stub means cleaning the previous state
      ((Stub)getPort())._setProperty(StubExt.PROPERTY_MTOM_ENABLED, Boolean.TRUE);

      PingMsgResponse value = getPort().ping(new PingMsg("1|0", bytesIn));
      assertNotNull("Return value was null",value);
      byte[] bytesOut = value.getXopContent();
      assertNotNull("Return xopContent was null", bytesOut);
      assertEquals("Content length doesn't match", bytesIn.length, bytesOut.length);
   }

   // ---------------------------------------------------------------------------------
   // Test concrete java types

   public void testImageResponseOptimized() throws Exception {

      Image image = XOPTestSupport.createTestImage(imgFile);

      if(image!=null)
      {
         // disable MTOM
         ((Stub)getPort())._setProperty(StubExt.PROPERTY_MTOM_ENABLED, Boolean.FALSE);

         PingImage pingImage = new PingImage("0|1", image);
         PingImageResponse response = getPort().pingImage(pingImage);
         assertNotNull("Return xopContent was null", response);
         assertNotNull("Return xopContent was null", response.getXopContent());
      }
   }

   public void testImageRequestOptimized() throws Exception {

      Image image = XOPTestSupport.createTestImage(imgFile);

      if(image!=null)
      {
         // enable MTOM
         ((Stub)getPort())._setProperty(StubExt.PROPERTY_MTOM_ENABLED, Boolean.TRUE);

         PingImage pingImage = new PingImage("1|0", image);
         PingImageResponse response = getPort().pingImage(pingImage);
         assertNotNull("Response was null", response);
         assertNotNull("Return xopContent was null", response.getXopContent());
      }
   }

   public void testSourceResponseOptimized() throws Exception {

      Source source = XOPTestSupport.createTestSource();

      // disable MTOM
      ((Stub)getPort())._setProperty(StubExt.PROPERTY_MTOM_ENABLED, Boolean.FALSE);

      PingSource pingSource = new PingSource();
      pingSource.setMessage("0|1");
      pingSource.setXopContent(source);

      PingSourceResponse response = getPort().pingSource(pingSource);
      assertNotNull("Response was null", response);
      assertNotNull("Return xopContent was null", response.getXopContent());

   }

   public void testSourceRequestOptimized() throws Exception {

      Source source = XOPTestSupport.createTestSource();

      // enable MTOM
      ((Stub)getPort())._setProperty(StubExt.PROPERTY_MTOM_ENABLED, Boolean.TRUE);

      PingSource pingSource = new PingSource();
      pingSource.setMessage("1|0");
      pingSource.setXopContent(source);

      PingSourceResponse response = getPort().pingSource(pingSource);
      assertNotNull("Response was null", response);
      assertNotNull("Return xopContent was null", response.getXopContent());
   }

   public void testDHResponseOptimized() throws Exception {

      DataHandler dh = XOPTestSupport.createDataHandler(imgFile);

      // disable MTOM
      ((Stub)getPort())._setProperty(StubExt.PROPERTY_MTOM_ENABLED, Boolean.FALSE);

      PingDataHandler reqest = new PingDataHandler(dh);
      reqest.setMessage("0|1");

      PingDataHandlerResponse response = getPort().pingDataHandler(reqest);
      assertNotNull("Response was null", response);
      assertNotNull("Return xopContent was null", response.getXopContent());

   }

   public void testDHRequestOptimized() throws Exception {

      DataHandler dh = XOPTestSupport.createDataHandler(imgFile);

      // enable MTOM
      ((Stub)getPort())._setProperty(StubExt.PROPERTY_MTOM_ENABLED, Boolean.TRUE);

      PingDataHandler reqest = new PingDataHandler(dh);
      reqest.setMessage("1|0");

      PingDataHandlerResponse response = getPort().pingDataHandler(reqest);
      assertNotNull("Response was null", response);
      assertNotNull("Return xopContent was null", response.getXopContent());
   }
}
