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
package javax.xml.ws.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.xml.ws.WebServiceFeature;

/**
 * Annotation used to identify other annotations
 * as a <code>WebServiceFeature</code>.
 *
 * Each <code>WebServiceFeature</code> annotation annotated with
 * this annotation MUST contain an 
 * <code>enabled</code> property of type
 * <code>boolean</code> with a default value of <code>true</code>. 
 * JAX-WS defines the following
 * <code>WebServiceFeature</code> annotations, however, an implementation
 * may define vendors specific annotations for other features.
 * If a JAX-WS implementation encounters an annotation annotated
 * with the <code>WebServiceFeatureAnnotation</code> that is does not
 * recognize/support an error MUST be given.
 *
 * @see javax.xml.ws.soap.WSAddressing
 * @see javax.xml.ws.soap.MTOM
 * @see javax.xml.ws.RespectBinding
 *
 * @since JAX-WS 2.1
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebServiceFeatureAnnotation {
    /**
     * Unique identifier for the WebServiceFeature.  This 
     * identifier MUST be unique across all implementations
     * of JAX-WS.
     */
    String id();

    /**
     * The <code>WebServiceFeature</code> bean that is associated
     * with the <code>WebServiceFeature</code> annotation
     */
    Class<? extends WebServiceFeature> bean();
}
