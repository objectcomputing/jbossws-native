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

import java.rmi.RemoteException;

import org.jboss.test.ws.jaxrpc.xop.shared.MTOMServiceBase;
import org.jboss.test.ws.jaxrpc.xop.shared.PingDataHandler;
import org.jboss.test.ws.jaxrpc.xop.shared.PingDataHandlerResponse;
import org.jboss.test.ws.jaxrpc.xop.shared.PingImage;
import org.jboss.test.ws.jaxrpc.xop.shared.PingImageResponse;
import org.jboss.test.ws.jaxrpc.xop.shared.PingMsg;
import org.jboss.test.ws.jaxrpc.xop.shared.PingMsgResponse;
import org.jboss.test.ws.jaxrpc.xop.shared.PingSource;
import org.jboss.test.ws.jaxrpc.xop.shared.PingSourceResponse;

/**
 * MTOM test service impl.
 * The 'message' param value determines wether or not the response should be XOP encoded.
 *
 * @author Heiko Braun, <heiko@openj.net>
 * @since 11-Apr-2006
 */
public class XOPPingImpl extends MTOMServiceBase implements XOPPing {

   public PingMsgResponse ping(PingMsg pingMsg) throws RemoteException {
      toggleXOP(pingMsg.getMessage());
      return new PingMsgResponse(pingMsg.getXopContent());
   }

   public PingImageResponse pingImage(PingImage pingImage) throws RemoteException {
      toggleXOP(pingImage.getMessage());
      return new PingImageResponse(pingImage.getXopContent());
   }

   public PingSourceResponse pingSource(PingSource pingSource) throws RemoteException {
      toggleXOP(pingSource.getMessage());
      return new PingSourceResponse(pingSource.getXopContent());
   }

   public PingDataHandlerResponse pingDataHandler(PingDataHandler pingDataHandler) throws RemoteException {
      toggleXOP(pingDataHandler.getMessage());
      return new PingDataHandlerResponse(pingDataHandler.getXopContent());
   }
}
