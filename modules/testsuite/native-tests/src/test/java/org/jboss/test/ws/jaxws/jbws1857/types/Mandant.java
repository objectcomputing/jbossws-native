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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for mandant complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mandant">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="adminEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adresse" type="{http://example.com}adresse" minOccurs="0"/>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dateModified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ekFachApplikation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ekKundenNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ekTeilnehmer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="esrAnschrift1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="esrAnschrift2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="esrAnschrift3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="esrAnschrift4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="esrBankAnschrift1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="esrBankAnschrift2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="esrBankAnschrift3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="esrKonto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ibanNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="level_0" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="level_1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="level_2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="level_3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="level_4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="level_5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="logo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mandantId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="mandantName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mandantName1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mandantName2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sprache" type="{http://example.com}languageType" minOccurs="0"/>
 *         &lt;element name="status" type="{http://example.com}statusType" minOccurs="0"/>
 *         &lt;element name="statusDatum" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="topMandant" type="{http://example.com}mandant" minOccurs="0"/>
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
@XmlType(name = "mandant", propOrder = {
    "adminEmail",
    "adresse",
    "dateCreated",
    "dateModified",
    "ekFachApplikation",
    "ekKundenNr",
    "ekTeilnehmer",
    "esrAnschrift1",
    "esrAnschrift2",
    "esrAnschrift3",
    "esrAnschrift4",
    "esrBankAnschrift1",
    "esrBankAnschrift2",
    "esrBankAnschrift3",
    "esrKonto",
    "ibanNr",
    "level0",
    "level1",
    "level2",
    "level3",
    "level4",
    "level5",
    "logo",
    "mandantId",
    "mandantName",
    "mandantName1",
    "mandantName2",
    "sprache",
    "status",
    "statusDatum",
    "topMandant",
    "userIdCreated",
    "userIdModified"
})
public class Mandant {

    protected String adminEmail;
    protected Adresse adresse;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateCreated;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateModified;
    protected String ekFachApplikation;
    protected String ekKundenNr;
    protected String ekTeilnehmer;
    protected String esrAnschrift1;
    protected String esrAnschrift2;
    protected String esrAnschrift3;
    protected String esrAnschrift4;
    protected String esrBankAnschrift1;
    protected String esrBankAnschrift2;
    protected String esrBankAnschrift3;
    protected String esrKonto;
    protected String ibanNr;
    @XmlElement(name = "level_0")
    protected String level0;
    @XmlElement(name = "level_1")
    protected String level1;
    @XmlElement(name = "level_2")
    protected String level2;
    @XmlElement(name = "level_3")
    protected String level3;
    @XmlElement(name = "level_4")
    protected String level4;
    @XmlElement(name = "level_5")
    protected String level5;
    protected String logo;
    protected Long mandantId;
    protected String mandantName;
    protected String mandantName1;
    protected String mandantName2;
    protected LanguageType sprache;
    protected StatusType status;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar statusDatum;
    protected Mandant topMandant;
    protected String userIdCreated;
    protected String userIdModified;

    /**
     * Gets the value of the adminEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdminEmail() {
        return adminEmail;
    }

    /**
     * Sets the value of the adminEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdminEmail(String value) {
        this.adminEmail = value;
    }

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
     * Gets the value of the ekFachApplikation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEkFachApplikation() {
        return ekFachApplikation;
    }

    /**
     * Sets the value of the ekFachApplikation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEkFachApplikation(String value) {
        this.ekFachApplikation = value;
    }

    /**
     * Gets the value of the ekKundenNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEkKundenNr() {
        return ekKundenNr;
    }

    /**
     * Sets the value of the ekKundenNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEkKundenNr(String value) {
        this.ekKundenNr = value;
    }

    /**
     * Gets the value of the ekTeilnehmer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEkTeilnehmer() {
        return ekTeilnehmer;
    }

    /**
     * Sets the value of the ekTeilnehmer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEkTeilnehmer(String value) {
        this.ekTeilnehmer = value;
    }

    /**
     * Gets the value of the esrAnschrift1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEsrAnschrift1() {
        return esrAnschrift1;
    }

    /**
     * Sets the value of the esrAnschrift1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsrAnschrift1(String value) {
        this.esrAnschrift1 = value;
    }

    /**
     * Gets the value of the esrAnschrift2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEsrAnschrift2() {
        return esrAnschrift2;
    }

    /**
     * Sets the value of the esrAnschrift2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsrAnschrift2(String value) {
        this.esrAnschrift2 = value;
    }

    /**
     * Gets the value of the esrAnschrift3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEsrAnschrift3() {
        return esrAnschrift3;
    }

    /**
     * Sets the value of the esrAnschrift3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsrAnschrift3(String value) {
        this.esrAnschrift3 = value;
    }

    /**
     * Gets the value of the esrAnschrift4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEsrAnschrift4() {
        return esrAnschrift4;
    }

    /**
     * Sets the value of the esrAnschrift4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsrAnschrift4(String value) {
        this.esrAnschrift4 = value;
    }

    /**
     * Gets the value of the esrBankAnschrift1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEsrBankAnschrift1() {
        return esrBankAnschrift1;
    }

    /**
     * Sets the value of the esrBankAnschrift1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsrBankAnschrift1(String value) {
        this.esrBankAnschrift1 = value;
    }

    /**
     * Gets the value of the esrBankAnschrift2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEsrBankAnschrift2() {
        return esrBankAnschrift2;
    }

    /**
     * Sets the value of the esrBankAnschrift2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsrBankAnschrift2(String value) {
        this.esrBankAnschrift2 = value;
    }

    /**
     * Gets the value of the esrBankAnschrift3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEsrBankAnschrift3() {
        return esrBankAnschrift3;
    }

    /**
     * Sets the value of the esrBankAnschrift3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsrBankAnschrift3(String value) {
        this.esrBankAnschrift3 = value;
    }

    /**
     * Gets the value of the esrKonto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEsrKonto() {
        return esrKonto;
    }

    /**
     * Sets the value of the esrKonto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsrKonto(String value) {
        this.esrKonto = value;
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
     * Gets the value of the level0 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLevel0() {
        return level0;
    }

    /**
     * Sets the value of the level0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLevel0(String value) {
        this.level0 = value;
    }

    /**
     * Gets the value of the level1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLevel1() {
        return level1;
    }

    /**
     * Sets the value of the level1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLevel1(String value) {
        this.level1 = value;
    }

    /**
     * Gets the value of the level2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLevel2() {
        return level2;
    }

    /**
     * Sets the value of the level2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLevel2(String value) {
        this.level2 = value;
    }

    /**
     * Gets the value of the level3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLevel3() {
        return level3;
    }

    /**
     * Sets the value of the level3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLevel3(String value) {
        this.level3 = value;
    }

    /**
     * Gets the value of the level4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLevel4() {
        return level4;
    }

    /**
     * Sets the value of the level4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLevel4(String value) {
        this.level4 = value;
    }

    /**
     * Gets the value of the level5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLevel5() {
        return level5;
    }

    /**
     * Sets the value of the level5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLevel5(String value) {
        this.level5 = value;
    }

    /**
     * Gets the value of the logo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogo() {
        return logo;
    }

    /**
     * Sets the value of the logo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogo(String value) {
        this.logo = value;
    }

    /**
     * Gets the value of the mandantId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMandantId() {
        return mandantId;
    }

    /**
     * Sets the value of the mandantId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMandantId(Long value) {
        this.mandantId = value;
    }

    /**
     * Gets the value of the mandantName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMandantName() {
        return mandantName;
    }

    /**
     * Sets the value of the mandantName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMandantName(String value) {
        this.mandantName = value;
    }

    /**
     * Gets the value of the mandantName1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMandantName1() {
        return mandantName1;
    }

    /**
     * Sets the value of the mandantName1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMandantName1(String value) {
        this.mandantName1 = value;
    }

    /**
     * Gets the value of the mandantName2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMandantName2() {
        return mandantName2;
    }

    /**
     * Sets the value of the mandantName2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMandantName2(String value) {
        this.mandantName2 = value;
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
     * Gets the value of the topMandant property.
     * 
     * @return
     *     possible object is
     *     {@link Mandant }
     *     
     */
    public Mandant getTopMandant() {
        return topMandant;
    }

    /**
     * Sets the value of the topMandant property.
     * 
     * @param value
     *     allowed object is
     *     {@link Mandant }
     *     
     */
    public void setTopMandant(Mandant value) {
        this.topMandant = value;
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
