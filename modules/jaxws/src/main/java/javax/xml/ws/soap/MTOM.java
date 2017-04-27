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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.xml.ws.spi.WebServiceFeatureAnnotation;

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
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebServiceFeatureAnnotation(id=MTOMFeature.ID,bean=MTOMFeature.class)
public @interface MTOM {
    /**
     * Specifies if this feature is enabled or disabled.
     */
    boolean enabled() default true; 
     
    /**
     * Property for MTOM threshold value. When MTOM is enabled, binary data above this 
     * size in bytes will be XOP encoded or sent as attachment. The value of this property 
     * MUST always be >= 0. Default value is 0.      
     */         
    int threshold() default 0;
}
