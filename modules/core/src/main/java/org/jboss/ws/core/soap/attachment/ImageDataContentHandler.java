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
package org.jboss.ws.core.soap.attachment;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.jboss.logging.Logger;

/**
 * <code>ImageDataContentHandler</code> is a JAF content handler that handles
 * marshalling/unmarshalling between <code>Image</code> objects and a stream.
 *
 * This handler provides support for all mime types handled by the ImageIO
 * implementation on the virtual machine that this class is ran under. These
 * are dynamically registered, so any custom ImageIO plugins are discovered and
 * used.
 *
 * It's important to note that some mime types (for example image/gif) may not
 * have encoding support provided by the ImageIO implementation. If this happens
 * an exception will be thrown indicating the lack of encoding support.
 *
 * @author <a href="mailto:jason@stacksmash.com">Jason T. Greene</a>
 * @author <a href="mailto:mageshbk@jboss.com">Magesh Kumar B</a>
 */
public class ImageDataContentHandler extends Component implements DataContentHandler
{
   // provide logging
   private static Logger log = Logger.getLogger(ImageDataContentHandler.class);

   private static DataFlavor[] flavors;

   static
   {
      buildFlavors();

      // Don't write back to disk since the images are in memory anyways
      ImageIO.setUseCache(false);
   }

   private static void buildFlavors()
   {
      String[] mimeTypes = ImageIO.getReaderMIMETypes();
      if (mimeTypes == null)
         return;
      ArrayList flavs = new ArrayList ();
      DataFlavor flavor;
      //flavors = new DataFlavor[mimeTypes.length];
      for (int i = 0; i < mimeTypes.length; i++)
      {
         try
         {
            flavor = new ActivationDataFlavor(Image.class, mimeTypes[i], "Image");
            flavs.add(flavor);
         }
         catch (IllegalArgumentException iae)
         {
            //This mime type is not supported
            log.warn("Unsupported MIME Type '" + mimeTypes[i] +"'");
         }
      }
      int size = flavs.size();
      flavors = new DataFlavor[size];
      for (int i = 0; i < size; i++)
      {
         flavors[i] = (ActivationDataFlavor)flavs.get(i);
      }
   }

   private static ImageWriter getImageWriter(String mimeType) {
      Iterator i = ImageIO.getImageWritersByMIMEType(mimeType);
      if (! i.hasNext())
         return null;

      return (ImageWriter) i.next();
   }

   private BufferedImage getBufferedImage(Image image) throws IOException
   {
      if (image instanceof BufferedImage)
         return (BufferedImage) image;

      try
      {
         BufferedImage buffered;

         MediaTracker tracker = new MediaTracker(this);
         tracker.addImage(image, 0);
         tracker.waitForAll();
         buffered = new BufferedImage(image.getHeight(null), image.getWidth(null), BufferedImage.TYPE_INT_RGB);
         Graphics2D gfx = buffered.createGraphics();
         gfx.drawImage(image, 0, 0, null);
         return buffered;
      }
      catch (InterruptedException e)
      {
         throw new IOException("Could not convert image " + e.getMessage());
      }
   }

   /**
    * Returns a {@link Image} from the specified
    * data source.
    *
    * @param ds the activation datasource
    * @return an AWT image object
    */
   public Object getContent(DataSource ds) throws IOException
   {
      return ImageIO.read(ds.getInputStream());
   }

   /**
    * Returns a {@link Image}from the specified data source. This method is
    * useless for this content handler because the image format is dynamically
    * discovered.
    *
    * @param df the flavor specifiying the mime type of ds
    * @param ds the activation data source
    * @return an AWT image object
    */
   public Object getTransferData(DataFlavor df, DataSource ds) throws UnsupportedFlavorException, IOException
   {
      return getContent(ds);
   }

   /**
    * Returns the acceptable data flavors that this content handler supports.
    *
    * @return array of <code>ActivationDataHandlers</code>
    */
   public DataFlavor[] getTransferDataFlavors()
   {
      return flavors;
   }

   /**
    * Writes the passed in {@link Image} object using the specified
    * mime type to the specified output stream.
    *
    * @param obj an AWT image object
    * @param mimeType the mime type of the image format to use
    * @param os the output stream to write this image to
    */
   public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException
   {
      if (obj == null)
         throw new IOException("Cannot write null source object");
      
      if (!(obj instanceof Image))
         throw new IOException("Requires the source object to be a java.awt.Image but is: " + obj.getClass().getName());

      ImageWriter writer = getImageWriter(mimeType);
      if (writer == null)
         throw new IOException("Image encoding not available for mime type " + mimeType + " on this vm");

      BufferedImage buffered = getBufferedImage((Image) obj);
      ImageOutputStream stream = ImageIO.createImageOutputStream(os);

      writer.setOutput(stream);
      writer.write(buffered);

      // We must close the stream now because if we are wrapping a ServletOutputStream,
      // a future gc can commit a stream that used in another thread (very very bad)
      stream.flush();
      stream.close();
   }
}
