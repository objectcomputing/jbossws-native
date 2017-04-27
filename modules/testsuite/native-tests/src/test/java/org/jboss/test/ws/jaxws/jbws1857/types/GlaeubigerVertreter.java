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
 * <p>Java class for glaeubigerVertreter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="glaeubigerVertreter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="adresse" type="{http://example.com}adresse" minOccurs="0"/>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dateModified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="glaeubigerVertreterId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="glaeubigerVertreterName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="glaeubigerVertreterName1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="glaeubigerVertreterName2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gueltigBis" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="gueltigVon" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ibanNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mandant" type="{http://example.com}mandant" minOccurs="0"/>
 *         &lt;element name="pcKonto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sprache" type="{http://example.com}languageType" minOccurs="0"/>
 *         &lt;element name="status" type="{http://example.com}statusType" minOccurs="0"/>
 *         &lt;element name="statusDatum" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
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
@XmlType(name = "glaeubigerVertreter", propOrder = {
    "adresse",
    "dateCreated",
    "dateModified",
    "glaeubigerVertreterId",
    "glaeubigerVertreterName",
    "glaeubigerVertreterName1",
    "glaeubigerVertreterName2",
    "gueltigBis",
    "gueltigVon",
    "ibanNr",
    "mandant",
    "pcKonto",
    "sprache",
    "status",
    "statusDatum",
    "userIdCreated",
    "userIdModified"
})
public class GlaeubigerVertreter {

    protected Adresse adresse;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateCreated;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateModified;
    protected Long glaeubigerVertreterId;
    protected String glaeubigerVertreterName;
    protected String glaeubigerVertreterName1;
    protected String glaeubigerVertreterName2;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar gueltigBis;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar gueltigVon;
    protected String ibanNr;
    protected Mandant mandant;
    protected String pcKonto;
    protected LanguageType sprache;
    protected StatusType status;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar statusDatum;
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
     * Gets the value of the glaeubigerVertreterId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getGlaeubigerVertreterId() {
        return glaeubigerVertreterId;
    }

    /**
     * Sets the value of the glaeubigerVertreterId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setGlaeubigerVertreterId(Long value) {
        this.glaeubigerVertreterId = value;
    }

    /**
     * Gets the value of the glaeubigerVertreterName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGlaeubigerVertreterName() {
        return glaeubigerVertreterName;
    }

    /**
     * Sets the value of the glaeubigerVertreterName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGlaeubigerVertreterName(String value) {
        this.glaeubigerVertreterName = value;
    }

    /**
     * Gets the value of the glaeubigerVertreterName1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGlaeubigerVertreterName1() {
        return glaeubigerVertreterName1;
    }

    /**
     * Sets the value of the glaeubigerVertreterName1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGlaeubigerVertreterName1(String value) {
        this.glaeubigerVertreterName1 = value;
    }

    /**
     * Gets the value of the glaeubigerVertreterName2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGlaeubigerVertreterName2() {
        return glaeubigerVertreterName2;
    }

    /**
     * Sets the value of the glaeubigerVertreterName2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGlaeubigerVertreterName2(String value) {
        this.glaeubigerVertreterName2 = value;
    }

    /**
     * Gets the value of the gueltigBis property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getGueltigBis() {
        return gueltigBis;
    }

    /**
     * Sets the value of the gueltigBis property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setGueltigBis(XMLGregorianCalendar value) {
        this.gueltigBis = value;
    }

    /**
     * Gets the value of the gueltigVon property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getGueltigVon() {
        return gueltigVon;
    }

    /**
     * Sets the value of the gueltigVon property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setGueltigVon(XMLGregorianCalendar value) {
        this.gueltigVon = value;
    }

    /**
     * Gets the value of the ibanNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIbanNr() {
        return ibanNr;
    }

    /**
     * Sets the value of the ibanNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIbanNr(String value) {
        this.ibanNr = value;
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
     * Gets the value of the pcKonto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPcKonto() {
        return pcKonto;
    }

    /**
     * Sets the value of the pcKonto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPcKonto(String value) {
        this.pcKonto = value;
    }

    /**
     * Gets the value of the sprache property.
     * 
     * @return
     *     possible object is
     *     {@link LanguageType }
     *     
     */
    public LanguageType getSprache() {
        return sprache;
    }

    /**
     * Sets the value of the sprache property.
     * 
     * @param value
     *     allowed object is
     *     {@link LanguageType }
     *     
     */
    public void setSprache(LanguageType value) {
        this.sprache = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link StatusType }
     *     
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusType }
     *     
     */
    public void setStatus(StatusType value) {
        this.status = value;
    }

    /**
     * Gets the value of the statusDatum property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStatusDatum() {
        return statusDatum;
    }

    /**
     * Sets the value of the statusDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStatusDatum(XMLGregorianCalendar value) {
        this.statusDatum = value;
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
