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
package javax.xml.ws.soap;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

/**
 * This feature represents the use of MTOM with a 
 * web service.
 *
 * <p>
 * The following describes the affects of this feature with respect
 * to being enabled or disabled:
 * <ul>
 *  <li> ENABLED: In this Mode, MTOM will be enabled.
 *  <li> DISABLED: In this Mode, MTOM will be disabled
 * </ul>
 * <p>
 * The {@link #threshold} property can be used to set the threshold 
 * value used to determine when binary data should be XOP encoded.
 *
 * @since JAX-WS 2.1
 */       
public final class MTOMFeature extends WebServiceFeature {
    /** 
     * Constant value identifying the MTOMFeature
     */
    public static final String ID = "http://www.w3.org/2004/08/soap/features/http-optimization";
  
   
    /**
     * Property for MTOM threshold value. This property serves as a hint when 
     * MTOM is enabled, binary data above this size in bytes SHOULD be sent 
     * as attachment.
     * The value of this property MUST always be >= 0. Default value is 0.      
     */
    protected int threshold = 0;
    

    /**
     * Create an <code>MTOMFeature</code>.
     * The instance created will be enabled.
     */
    public MTOMFeature() {
        this.enabled = true;
    }    
    
    /**
     * Creates an <code>MTOMFeature</code>.
     * 
     * @param enabled specifies if this feature should be enabled or not
     */
    public MTOMFeature(boolean enabled) {
        this.enabled = enabled;
    }


    /**
     * Creates an <code>MTOMFeature</code>.
     * The instance created will be enabled.
     *
     * @param threshold the size in bytes that binary data SHOULD be before
     * being sent as an attachment.
     *
     * @throws WebServiceException if threshold is < 0
     */
    public MTOMFeature(int threshold) {
        if (threshold < 0)
            throw new WebServiceException("MTOMFeature.threshold must be >= 0, actual value: "+threshold);
        this.enabled = true;
        this.threshold = threshold;
    }    
    
    /**
     * Creates an <code>MTOMFeature</code>.
     * 
     * @param enabled specifies if this feature should be enabled or not
     * @param threshold the size in bytes that binary data SHOULD be before
     * being sent as an attachment.
     *
     * @throws WebServiceException if threshold is < 0
     */
    public MTOMFeature(boolean enabled, int threshold) {
        if (threshold < 0)
            throw new WebServiceException("MTOMFeature.threshold must be >= 0, actual value: "+threshold);
        this.enabled = enabled;
        this.threshold = threshold;
    }    
    
    /**
     * {@inheritDoc}
     */
    public String getID() {
        return ID;
    }
    
    /**
     * Gets the threshold value used to determine when binary data 
     * should be sent as an attachment.
     *
     * @return the current threshold size in bytes
     */
    public int getThreshold() {
        return threshold;
    }
}
