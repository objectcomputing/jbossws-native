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
package org.jboss.test.ws.jaxrpc.samples.wsbpel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.management.ObjectName;

import junit.framework.Test;

import org.jboss.logging.Logger;
import org.jboss.wsf.test.JBossWSTestHelper;
import org.jboss.wsf.test.JBossWSTestSetup;
import org.jboss.wsf.common.ObjectNameFactory;

/**
 * A test setup that deploys process archives.
 * 
 * @author <a href="mailto:alex.guizar@jboss.com">Alejandro Guizar</a>
 * @author Thomas.Diesler@jboss.com
 */
public class JbpmBpelTestSetup extends JBossWSTestSetup
{
   private final ObjectName oname = ObjectNameFactory.create("jboss.jbpm:name=JbpmBpel,service=JbpmService");
   private final String[] processFiles;
   private boolean undeployOnTearDown;

   private static final Logger log = Logger.getLogger(JbpmBpelTestSetup.class);

   public JbpmBpelTestSetup(Test test, String[] processFiles)
   {
      super(test, null);
      this.processFiles = processFiles;
   }

   protected void setUp() throws Exception
   {
      // Deploy jbpm-bpel.sar if it is not deployed already
      JBossWSTestHelper helper = new JBossWSTestHelper();
      if (JBossWSTestHelper.getServer().isRegistered(oname) == false)
      {
         helper.deploy("jbpm-bpel.sar");
         undeployOnTearDown = true;
      }
      
      for (int i = 0; i < processFiles.length; i++)
      {
         String processFileName = processFiles[i];

         // check file exists before dispatching to server 
         File processFile = getArchiveFile(processFileName);
         if (!processFile.exists())
            throw new FileNotFoundException(processFileName);

         deployProcess(processFile);
      }
   }
   
   protected void tearDown() throws Exception
   {
      if (undeployOnTearDown)
         new JBossWSTestHelper().undeploy("jbpm-bpel.sar");
   }

   public static void deployProcess(File processFile) throws Exception
   {
      // format file component
      String file = getJbpmBpelDeployContext() + "?processfile=" + URLEncoder.encode(processFile.toURI().toString(), "UTF-8");

      // create target URL
      URL targetUrl = new URL("http", JBossWSTestHelper.getServerHost(), getServerHttpPort(), file);

      // submit process start request
      int responseCode = submitRequest(targetUrl);

      if (responseCode != HttpURLConnection.HTTP_OK)
         throw new IOException("could not deploy process: " + processFile);
   }

   private static int submitRequest(URL targetUrl) throws IOException
   {
      HttpURLConnection httpConnection = (HttpURLConnection)targetUrl.openConnection();

      try
      {
         log.debug("submitting request: " + targetUrl);
         httpConnection.connect();

         int responseCode = httpConnection.getResponseCode();
         log.debug("got response code: " + responseCode);

         return responseCode;
      }
      finally
      {
         httpConnection.disconnect();
      }
   }

   public static int getServerHttpPort()
   {
      try
      {
         return Integer.parseInt(System.getProperty("jbpm.bpel.http.port"));
      }
      catch (NumberFormatException e)
      {
         return 8080;
      }
   }

   public static String getJbpmBpelDeployContext()
   {
      return System.getProperty("jbpm.bpel.deploy.context", "/jbpm-bpel/deploy");
   }
}
