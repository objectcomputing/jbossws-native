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
package org.jboss.ws.core.soap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The NodeList interface provides the abstraction of an ordered collection of nodes, without defining or constraining
 * how this collection is implemented. NodeList objects in the DOM are live.
 *
 * The items in the NodeList are accessible via an integral index, starting from 0.
 *
 * @author Thomas.Diesler@jboss.org
 */
class NodeListImpl implements NodeList
{
   private List<NodeImpl> nodeList = new ArrayList<NodeImpl>();

   NodeListImpl(List<NodeImpl> nodes)
   {
      nodeList = new ArrayList<NodeImpl>(nodes);
   }

   NodeListImpl(Iterator<NodeImpl> nodes)
   {
      nodeList = new ArrayList<NodeImpl>();
      while (nodes.hasNext())
         nodeList.add(nodes.next());
   }

   public Node item(int index)
   {
      if (nodeList.size() > index)
         return nodeList.get(index);
      else
         return null;
   }

   public int getLength()
   {
      return nodeList.size();
   }
}
