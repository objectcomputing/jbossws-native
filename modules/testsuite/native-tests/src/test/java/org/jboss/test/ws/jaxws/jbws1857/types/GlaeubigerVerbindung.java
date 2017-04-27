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
package org.jboss.test.ws.jaxws.jbws1857.types;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for glaeubigerVerbindung complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="glaeubigerVerbindung">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dateModified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="glaeubiger" type="{http://example.com}glaeubiger" minOccurs="0"/>
 *         &lt;element name="quote" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="userIdCreated" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userIdModified" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="verlustschein" type="{http://example.com}verlustschein" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "glaeubigerVerbindung", propOrder = {
    "dateCreated",
    "dateModified",
    "glaeubiger",
    "quote",
    "userIdCreated",
    "userIdModified",
    "verlustschein"
})
public class GlaeubigerVerbindung {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateCreated;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateModified;
    protected Glaeubiger glaeubiger;
    protected BigDecimal quote;
    protected String userIdCreated;
    protected String userIdModified;
    protected Verlustschein verlustschein;

    /**
     * Gets the value of the dateCreated property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateCreated() {
        return dateCreated;
    }

    /**
     * Sets the value of the dateCreated property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateCreated(XMLGregorianCalendar value) {
        this.dateCreated = value;
    }

    /**
     * Gets the value of the dateModified property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateModified() {
        return dateModified;
    }

    /**
     * Sets the value of the dateModified property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateModified(XMLGregorianCalendar value) {
        this.dateModified = value;
    }

    /**
     * Gets the value of the glaeubiger property.
     * 
     * @return
     *     possible object is
     *     {@link Glaeubiger }
     *     
     */
    public Glaeubiger getGlaeubiger() {
        return glaeubiger;
    }

    /**
     * Sets the value of the glaeubiger property.
     * 
     * @param value
     *     allowed object is
     *     {@link Glaeubiger }
     *     
     */
    public void setGlaeubiger(Glaeubiger value) {
        this.glaeubiger = value;
    }

    /**
     * Gets the value of the quote property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getQuote() {
        return quote;
    }

    /**
     * Sets the value of the quote property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setQuote(BigDecimal value) {
        this.quote = value;
    }

    /**
     * Gets the value of the userIdCreated property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserIdCreated() {
        return userIdCreated;
    }

    /**
     * Sets the value of the userIdCreated property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserIdCreated(String value) {
        this.userIdCreated = value;
    }

    /**
     * Gets the value of the userIdModified property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserIdModified() {
        return userIdModified;
    }

    /**
     * Sets the value of the userIdModified property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserIdModified(String value) {
        this.userIdModified = value;
    }

    /**
     * Gets the value of the verlustschein property.
     * 
     * @return
     *     possible object is
     *     {@link Verlustschein }
     *     
     */
    public Verlustschein getVerlustschein() {
        return verlustschein;
    }

    /**
     * Sets the value of the verlustschein property.
     * 
     * @param value
     *     allowed object is
     *     {@link Verlustschein }
     *     
     */
    public void setVerlustschein(Verlustschein value) {
        this.verlustschein = value;
    }

}
