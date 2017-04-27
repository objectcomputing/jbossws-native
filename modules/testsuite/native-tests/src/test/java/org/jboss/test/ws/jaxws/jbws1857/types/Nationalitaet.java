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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for nationalitaet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="nationalitaet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="iso2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="iso3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="landName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nationalitaetId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tcsCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nationalitaet", propOrder = {
    "iso2",
    "iso3",
    "landName",
    "nationalitaetId",
    "tcsCode"
})
public class Nationalitaet {

    protected String iso2;
    protected String iso3;
    protected String landName;
    protected String nationalitaetId;
    protected String tcsCode;

    /**
     * Gets the value of the iso2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIso2() {
        return iso2;
    }

    /**
     * Sets the value of the iso2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIso2(String value) {
        this.iso2 = value;
    }

    /**
     * Gets the value of the iso3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIso3() {
        return iso3;
    }

    /**
     * Sets the value of the iso3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIso3(String value) {
        this.iso3 = value;
    }

    /**
     * Gets the value of the landName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLandName() {
        return landName;
    }

    /**
     * Sets the value of the landName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLandName(String value) {
        this.landName = value;
    }

    /**
     * Gets the value of the nationalitaetId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNationalitaetId() {
        return nationalitaetId;
    }

    /**
     * Sets the value of the nationalitaetId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNationalitaetId(String value) {
        this.nationalitaetId = value;
    }

    /**
     * Gets the value of the tcsCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTcsCode() {
        return tcsCode;
    }

    /**
     * Sets the value of the tcsCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTcsCode(String value) {
        this.tcsCode = value;
    }

}
