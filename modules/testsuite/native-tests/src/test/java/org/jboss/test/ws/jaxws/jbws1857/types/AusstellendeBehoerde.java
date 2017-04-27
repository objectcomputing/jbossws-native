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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ausstellendeBehoerde complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ausstellendeBehoerde">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="adresse" type="{http://example.com}adresse" minOccurs="0"/>
 *         &lt;element name="amtArt" type="{http://example.com}amtArt" minOccurs="0"/>
 *         &lt;element name="amtsId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="ausstellendeBehoerdeId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dateModified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="mandant" type="{http://example.com}mandant" minOccurs="0"/>
 *         &lt;element name="userIdCreated" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userIdModified" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ausstellendeBehoerde", propOrder = {
    "adresse",
    "amtArt",
    "amtsId",
    "ausstellendeBehoerdeId",
    "dateCreated",
    "dateModified",
    "mandant",
    "userIdCreated",
    "userIdModified"
})
public class AusstellendeBehoerde {

    protected Adresse adresse;
    protected AmtArt amtArt;
    protected Long amtsId;
    protected Long ausstellendeBehoerdeId;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateCreated;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateModified;
    protected Mandant mandant;
    protected String userIdCreated;
    protected String userIdModified;

    /**
     * Gets the value of the adresse property.
     * 
     * @return
     *     possible object is
     *     {@link Adresse }
     *     
     */
    public Adresse getAdresse() {
        return adresse;
    }

    /**
     * Sets the value of the adresse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Adresse }
     *     
     */
    public void setAdresse(Adresse value) {
        this.adresse = value;
    }

    /**
     * Gets the value of the amtArt property.
     * 
     * @return
     *     possible object is
     *     {@link AmtArt }
     *     
     */
    public AmtArt getAmtArt() {
        return amtArt;
    }

    /**
     * Sets the value of the amtArt property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmtArt }
     *     
     */
    public void setAmtArt(AmtArt value) {
        this.amtArt = value;
    }

    /**
     * Gets the value of the amtsId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAmtsId() {
        return amtsId;
    }

    /**
     * Sets the value of the amtsId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAmtsId(Long value) {
        this.amtsId = value;
    }

    /**
     * Gets the value of the ausstellendeBehoerdeId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAusstellendeBehoerdeId() {
        return ausstellendeBehoerdeId;
    }

    /**
     * Sets the value of the ausstellendeBehoerdeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAusstellendeBehoerdeId(Long value) {
        this.ausstellendeBehoerdeId = value;
    }

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
     * Gets the value of the mandant property.
     * 
     * @return
     *     possible object is
     *     {@link Mandant }
     *     
     */
    public Mandant getMandant() {
        return mandant;
    }

    /**
     * Sets the value of the mandant property.
     * 
     * @param value
     *     allowed object is
     *     {@link Mandant }
     *     
     */
    public void setMandant(Mandant value) {
        this.mandant = value;
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

}
