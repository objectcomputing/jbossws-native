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
package javax.xml.ws.handler;

import javax.xml.ws.ProtocolException;

/** The <code>Handler</code> interface
 *  is the base interface for JAX-WS handlers.
 * 
 *  @since JAX-WS 2.0
**/
public interface Handler<C extends MessageContext> {

  /** The <code>handleMessage</code> method is invoked for normal processing
   *  of inbound and outbound messages. Refer to the description of the handler
   *  framework in the JAX-WS specification for full details.
   *
   *  @param context the message context.
   *  @return An indication of whether handler processing should continue for
   *  the current message
   *                 <ul>
   *                 <li>Return <code>true</code> to continue 
   *                     processing.</li>
   *                 <li>Return <code>false</code> to block 
   *                     processing.</li>
   *                  </ul>
   *  @throws RuntimeException Causes the JAX-WS runtime to cease
   *    handler processing and generate a fault.
   *  @throws ProtocolException Causes the JAX-WS runtime to switch to
   *    fault message processing.
  **/
  public boolean handleMessage(C context);

  /** The <code>handleFault</code> method is invoked for fault message 
   *  processing.  Refer to the description of the handler
   *  framework in the JAX-WS specification for full details.
   *
   *  @param context the message context
   *  @return An indication of whether handler fault processing should continue 
   *  for the current message
   *                 <ul>
   *                 <li>Return <code>true</code> to continue 
   *                     processing.</li>
   *                 <li>Return <code>false</code> to block 
   *                     processing.</li>
   *                  </ul>
   *  @throws RuntimeException Causes the JAX-WS runtime to cease
   *    handler fault processing and dispatch the fault.
   *  @throws ProtocolException Causes the JAX-WS runtime to cease
   *    handler fault processing and dispatch the fault.
  **/
  public boolean handleFault(C context);

  /**
   * Called at the conclusion of a message exchange pattern just prior to
   * the JAX-WS runtime disptaching a message, fault or exception.  Refer to
   * the description of the handler
   * framework in the JAX-WS specification for full details.
   *
   * @param context the message context
  **/
  public void close(MessageContext context);
}
