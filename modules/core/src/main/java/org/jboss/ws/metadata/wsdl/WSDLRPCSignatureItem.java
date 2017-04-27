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
package org.jboss.ws.metadata.wsdl;


/**
 * Represents an individual item of a wrpc:signature
 *
 * @author <a href="mailto:jason.greene@jboss.com">Jason T. Greene</a>
 */
public class WSDLRPCSignatureItem
{
   public enum Direction {IN, OUT, INOUT, RETURN};

   private Direction direction;
   private final String name;
   private int position;

   public WSDLRPCSignatureItem(String name)
   {
      this.name = name;
      this.direction = Direction.IN;
   }

   public WSDLRPCSignatureItem(String name, Direction direction)
   {
      this.direction = direction;
      this.name = name;
   }

   public String getName()
   {
      return name;
   }

   public Direction getDirection()
   {
      return direction;
   }

   public void setDirection(Direction direction)
   {
      this.direction = direction;
   }

   public void setPosition(int position)
   {
      this.position = position;
   }

   public int getPosition()
   {
      return position;
   }
}
