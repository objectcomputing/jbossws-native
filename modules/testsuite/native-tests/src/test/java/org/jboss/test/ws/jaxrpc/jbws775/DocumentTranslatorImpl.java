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
package org.jboss.test.ws.jaxrpc.jbws775;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jboss.logging.Logger;

public class DocumentTranslatorImpl implements DocumentTranslator, Remote
{
   private Logger log = Logger.getLogger(DocumentTranslatorImpl.class);

   public TDocument translate(TTranslationRequest tRequest) throws TTextNotTranslatable, TDictionaryNotAvailable, RemoteException
   {
      TDocument tDocument = tRequest.getDocument();
      
      TDocumentHead tHead = tDocument.getHead();
      String lang = tHead.getLanguage();
      String title = tHead.getTitle();
      
      log.info("[lang=" + lang + ",title=" + title + "]");
      if ("en".equals(lang) == false || "title".equals(title) == false)
         throw new IllegalStateException("Invalid TDocumentHead");

      return tDocument;
   }

   public void quoteTranslation(TQuotationRequest quotationRequest) throws RemoteException
   {
   }

   public TStatusResponse getQuotationStatus(TStatusRequest statusRequest) throws RemoteException
   {
      TStatusResponse _retVal = null;
      return _retVal;
   }
}
