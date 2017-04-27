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
 * <p>Java class for adresse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="adresse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="adressArt" type="{http://example.com}adressArt" minOccurs="0"/>
 *         &lt;element name="adressTyp" type="{http://example.com}adressTyp" minOccurs="0"/>
 *         &lt;element name="adressZeile1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adressZeile2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adresseId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="bezeichnung1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bezeichnung2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dateModified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gebiet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hausnummer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mandantId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="ort" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="plz" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="plzAusland" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="plzZusatz" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="postfachNr" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="postfachText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="strasse" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="telefonGeschaeft" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="telefonHandy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="telefonPrivat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "adresse", propOrder = {
    "adressArt",
    "adressTyp",
    "adressZeile1",
    "adressZeile2",
    "adresseId",
    "bezeichnung1",
    "bezeichnung2",
    "dateCreated",
    "dateModified",
    "email",
    "fax",
    "gebiet",
    "hausnummer",
    "mandantId",
    "ort",
    "plz",
    "plzAusland",
    "plzZusatz",
    "postfachNr",
    "postfachText",
    "strasse",
    "telefonGeschaeft",
    "telefonHandy",
    "telefonPrivat",
    "userIdCreated",
    "userIdModified"
})
public class Adresse {

    protected AdressArt adressArt;
    protected AdressTyp adressTyp;
    protected String adressZeile1;
    protected String adressZeile2;
    protected Long adresseId;
    protected String bezeichnung1;
    protected String bezeichnung2;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateCreated;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateModified;
    protected String email;
    protected String fax;
    protected String gebiet;
    protected String hausnummer;
    protected Long mandantId;
    protected String ort;
    protected String plz;
    protected String plzAusland;
    protected String plzZusatz;
    protected int postfachNr;
    protected String postfachText;
    protected String strasse;
    protected String telefonGeschaeft;
    protected String telefonHandy;
    protected String telefonPrivat;
    protected String userIdCreated;
    protected String userIdModified;

    /**
     * Gets the value of the adressArt property.
     * 
     * @return
     *     possible object is
     *     {@link AdressArt }
     *     
     */
    public AdressArt getAdressArt() {
        return adressArt;
    }

    /**
     * Sets the value of the adressArt property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdressArt }
     *     
     */
    public void setAdressArt(AdressArt value) {
        this.adressArt = value;
    }

    /**
     * Gets the value of the adressTyp property.
     * 
     * @return
     *     possible object is
     *     {@link AdressTyp }
     *     
     */
    public AdressTyp getAdressTyp() {
        return adressTyp;
    }

    /**
     * Sets the value of the adressTyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdressTyp }
     *     
     */
    public void setAdressTyp(AdressTyp value) {
        this.adressTyp = value;
    }

    /**
     * Gets the value of the adressZeile1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdressZeile1() {
        return adressZeile1;
    }

    /**
     * Sets the value of the adressZeile1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdressZeile1(String value) {
        this.adressZeile1 = value;
    }

    /**
     * Gets the value of the adressZeile2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdressZeile2() {
        return adressZeile2;
    }

    /**
     * Sets the value of the adressZeile2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdressZeile2(String value) {
        this.adressZeile2 = value;
    }

    /**
     * Gets the value of the adresseId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAdresseId() {
        return adresseId;
    }

    /**
     * Sets the value of the adresseId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAdresseId(Long value) {
        this.adresseId = value;
    }

    /**
     * Gets the value of the bezeichnung1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBezeichnung1() {
        return bezeichnung1;
    }

    /**
     * Sets the value of the bezeichnung1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBezeichnung1(String value) {
        this.bezeichnung1 = value;
    }

    /**
     * Gets the value of the bezeichnung2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBezeichnung2() {
        return bezeichnung2;
    }

    /**
     * Sets the value of the bezeichnung2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBezeichnung2(String value) {
        this.bezeichnung2 = value;
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
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the fax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFax() {
        return fax;
    }

    /**
     * Sets the value of the fax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFax(String value) {
        this.fax = value;
    }

    /**
     * Gets the value of the gebiet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGebiet() {
        return gebiet;
    }

    /**
     * Sets the value of the gebiet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGebiet(String value) {
        this.gebiet = value;
    }

    /**
     * Gets the value of the hausnummer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHausnummer() {
        return hausnummer;
    }

    /**
     * Sets the value of the hausnummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHausnummer(String value) {
        this.hausnummer = value;
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
     * Gets the value of the ort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrt() {
        return ort;
    }

    /**
     * Sets the value of the ort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrt(String value) {
        this.ort = value;
    }

    /**
     * Gets the value of the plz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlz() {
        return plz;
    }

    /**
     * Sets the value of the plz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlz(String value) {
        this.plz = value;
    }

    /**
     * Gets the value of the plzAusland property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlzAusland() {
        return plzAusland;
    }

    /**
     * Sets the value of the plzAusland property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlzAusland(String value) {
        this.plzAusland = value;
    }

    /**
     * Gets the value of the plzZusatz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlzZusatz() {
        return plzZusatz;
    }

    /**
     * Sets the value of the plzZusatz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlzZusatz(String value) {
        this.plzZusatz = value;
    }

    /**
     * Gets the value of the postfachNr property.
     * 
     */
    public int getPostfachNr() {
        return postfachNr;
    }

    /**
     * Sets the value of the postfachNr property.
     * 
     */
    public void setPostfachNr(int value) {
        this.postfachNr = value;
    }

    /**
     * Gets the value of the postfachText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostfachText() {
        return postfachText;
    }

    /**
     * Sets the value of the postfachText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostfachText(String value) {
        this.postfachText = value;
    }

    /**
     * Gets the value of the strasse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrasse() {
        return strasse;
    }

    /**
     * Sets the value of the strasse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrasse(String value) {
        this.strasse = value;
    }

    /**
     * Gets the value of the telefonGeschaeft property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefonGeschaeft() {
        return telefonGeschaeft;
    }

    /**
     * Sets the value of the telefonGeschaeft property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefonGeschaeft(String value) {
        this.telefonGeschaeft = value;
    }

    /**
     * Gets the value of the telefonHandy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefonHandy() {
        return telefonHandy;
    }

    /**
     * Sets the value of the telefonHandy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefonHandy(String value) {
        this.telefonHandy = value;
    }

    /**
     * Gets the value of the telefonPrivat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefonPrivat() {
        return telefonPrivat;
    }

    /**
     * Sets the value of the telefonPrivat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefonPrivat(String value) {
        this.telefonPrivat = value;
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
