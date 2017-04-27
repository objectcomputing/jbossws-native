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
 * <p>Java class for natuerlichePerson complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="natuerlichePerson">
 *   &lt;complexContent>
 *     &lt;extension base="{http://example.com}person">
 *       &lt;sequence>
 *         &lt;element name="aliasName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="allianzName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="amtlicherName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="anredeCode" type="{http://example.com}anredeCode" minOccurs="0"/>
 *         &lt;element name="geburtsDatum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gesetzlicherVertreter" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="heimatOrt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="kanton" type="{http://example.com}kantonType" minOccurs="0"/>
 *         &lt;element name="landKurzname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ledigenName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nationalitaet" type="{http://example.com}nationalitaet" minOccurs="0"/>
 *         &lt;element name="nationalitaetStatus" type="{http://example.com}nationalitaetStatus" minOccurs="0"/>
 *         &lt;element name="rufname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sexType" type="{http://example.com}statusType" minOccurs="0"/>
 *         &lt;element name="titel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="todesDatum" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="vorname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "natuerlichePerson", propOrder = {
    "aliasName",
    "allianzName",
    "amtlicherName",
    "anredeCode",
    "geburtsDatum",
    "gesetzlicherVertreter",
    "heimatOrt",
    "kanton",
    "landKurzname",
    "ledigenName",
    "nationalitaet",
    "nationalitaetStatus",
    "rufname",
    "sexType",
    "titel",
    "todesDatum",
    "vorname"
})
public class NatuerlichePerson
    extends Person
{

    protected String aliasName;
    protected String allianzName;
    protected String amtlicherName;
    protected AnredeCode anredeCode;
    protected String geburtsDatum;
    protected Boolean gesetzlicherVertreter;
    protected String heimatOrt;
    protected KantonType kanton;
    protected String landKurzname;
    protected String ledigenName;
    protected Nationalitaet nationalitaet;
    protected NationalitaetStatus nationalitaetStatus;
    protected String rufname;
    protected StatusType sexType;
    protected String titel;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar todesDatum;
    protected String vorname;

    /**
     * Gets the value of the aliasName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAliasName() {
        return aliasName;
    }

    /**
     * Sets the value of the aliasName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAliasName(String value) {
        this.aliasName = value;
    }

    /**
     * Gets the value of the allianzName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllianzName() {
        return allianzName;
    }

    /**
     * Sets the value of the allianzName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllianzName(String value) {
        this.allianzName = value;
    }

    /**
     * Gets the value of the amtlicherName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmtlicherName() {
        return amtlicherName;
    }

    /**
     * Sets the value of the amtlicherName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmtlicherName(String value) {
        this.amtlicherName = value;
    }

    /**
     * Gets the value of the anredeCode property.
     * 
     * @return
     *     possible object is
     *     {@link AnredeCode }
     *     
     */
    public AnredeCode getAnredeCode() {
        return anredeCode;
    }

    /**
     * Sets the value of the anredeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link AnredeCode }
     *     
     */
    public void setAnredeCode(AnredeCode value) {
        this.anredeCode = value;
    }

    /**
     * Gets the value of the geburtsDatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeburtsDatum() {
        return geburtsDatum;
    }

    /**
     * Sets the value of the geburtsDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeburtsDatum(String value) {
        this.geburtsDatum = value;
    }

    /**
     * Gets the value of the gesetzlicherVertreter property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGesetzlicherVertreter() {
        return gesetzlicherVertreter;
    }

    /**
     * Sets the value of the gesetzlicherVertreter property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGesetzlicherVertreter(Boolean value) {
        this.gesetzlicherVertreter = value;
    }

    /**
     * Gets the value of the heimatOrt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeimatOrt() {
        return heimatOrt;
    }

    /**
     * Sets the value of the heimatOrt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeimatOrt(String value) {
        this.heimatOrt = value;
    }

    /**
     * Gets the value of the kanton property.
     * 
     * @return
     *     possible object is
     *     {@link KantonType }
     *     
     */
    public KantonType getKanton() {
        return kanton;
    }

    /**
     * Sets the value of the kanton property.
     * 
     * @param value
     *     allowed object is
     *     {@link KantonType }
     *     
     */
    public void setKanton(KantonType value) {
        this.kanton = value;
    }

    /**
     * Gets the value of the landKurzname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLandKurzname() {
        return landKurzname;
    }

    /**
     * Sets the value of the landKurzname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLandKurzname(String value) {
        this.landKurzname = value;
    }

    /**
     * Gets the value of the ledigenName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLedigenName() {
        return ledigenName;
    }

    /**
     * Sets the value of the ledigenName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLedigenName(String value) {
        this.ledigenName = value;
    }

    /**
     * Gets the value of the nationalitaet property.
     * 
     * @return
     *     possible object is
     *     {@link Nationalitaet }
     *     
     */
    public Nationalitaet getNationalitaet() {
        return nationalitaet;
    }

    /**
     * Sets the value of the nationalitaet property.
     * 
     * @param value
     *     allowed object is
     *     {@link Nationalitaet }
     *     
     */
    public void setNationalitaet(Nationalitaet value) {
        this.nationalitaet = value;
    }

    /**
     * Gets the value of the nationalitaetStatus property.
     * 
     * @return
     *     possible object is
     *     {@link NationalitaetStatus }
     *     
     */
    public NationalitaetStatus getNationalitaetStatus() {
        return nationalitaetStatus;
    }

    /**
     * Sets the value of the nationalitaetStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link NationalitaetStatus }
     *     
     */
    public void setNationalitaetStatus(NationalitaetStatus value) {
        this.nationalitaetStatus = value;
    }

    /**
     * Gets the value of the rufname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRufname() {
        return rufname;
    }

    /**
     * Sets the value of the rufname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRufname(String value) {
        this.rufname = value;
    }

    /**
     * Gets the value of the sexType property.
     * 
     * @return
     *     possible object is
     *     {@link StatusType }
     *     
     */
    public StatusType getSexType() {
        return sexType;
    }

    /**
     * Sets the value of the sexType property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusType }
     *     
     */
    public void setSexType(StatusType value) {
        this.sexType = value;
    }

    /**
     * Gets the value of the titel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitel() {
        return titel;
    }

    /**
     * Sets the value of the titel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitel(String value) {
        this.titel = value;
    }

    /**
     * Gets the value of the todesDatum property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTodesDatum() {
        return todesDatum;
    }

    /**
     * Sets the value of the todesDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTodesDatum(XMLGregorianCalendar value) {
        this.todesDatum = value;
    }

    /**
     * Gets the value of the vorname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVorname() {
        return vorname;
    }

    /**
     * Sets the value of the vorname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVorname(String value) {
        this.vorname = value;
    }

}
