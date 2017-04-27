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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for person complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="person">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dateModified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="mandant" type="{http://example.com}mandant" minOccurs="0"/>
 *         &lt;element name="personAdressen" type="{http://example.com}personAdresse" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="personId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="personInterface" type="{http://example.com}personInterface" minOccurs="0"/>
 *         &lt;element name="personenArt" type="{http://example.com}personenArt" minOccurs="0"/>
 *         &lt;element name="sprache" type="{http://example.com}languageType" minOccurs="0"/>
 *         &lt;element name="umzugDatum" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="userIdCreated" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userIdModified" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wegzugDatum" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="zuzugDatum" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "person", propOrder = {
    "dateCreated",
    "dateModified",
    "mandant",
    "personAdressen",
    "personId",
    "personInterface",
    "personenArt",
    "sprache",
    "umzugDatum",
    "userIdCreated",
    "userIdModified",
    "wegzugDatum",
    "zuzugDatum"
})
public abstract class Person {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateCreated;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateModified;
    protected Mandant mandant;
    @XmlElement(nillable = true)
    protected List<PersonAdresse> personAdressen;
    protected Long personId;
    protected PersonInterface personInterface;
    protected PersonenArt personenArt;
    protected LanguageType sprache;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar umzugDatum;
    protected String userIdCreated;
    protected String userIdModified;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar wegzugDatum;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar zuzugDatum;

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
     * Gets the value of the personAdressen property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the personAdressen property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPersonAdressen().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonAdresse }
     * 
     * 
     */
    public List<PersonAdresse> getPersonAdressen() {
        if (personAdressen == null) {
            personAdressen = new ArrayList<PersonAdresse>();
        }
        return this.personAdressen;
    }

    /**
     * Gets the value of the personId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPersonId() {
        return personId;
    }

    /**
     * Sets the value of the personId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPersonId(Long value) {
        this.personId = value;
    }

    /**
     * Gets the value of the personInterface property.
     * 
     * @return
     *     possible object is
     *     {@link PersonInterface }
     *     
     */
    public PersonInterface getPersonInterface() {
        return personInterface;
    }

    /**
     * Sets the value of the personInterface property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonInterface }
     *     
     */
    public void setPersonInterface(PersonInterface value) {
        this.personInterface = value;
    }

    /**
     * Gets the value of the personenArt property.
     * 
     * @return
     *     possible object is
     *     {@link PersonenArt }
     *     
     */
    public PersonenArt getPersonenArt() {
        return personenArt;
    }

    /**
     * Sets the value of the personenArt property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonenArt }
     *     
     */
    public void setPersonenArt(PersonenArt value) {
        this.personenArt = value;
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
     * Gets the value of the umzugDatum property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUmzugDatum() {
        return umzugDatum;
    }

    /**
     * Sets the value of the umzugDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUmzugDatum(XMLGregorianCalendar value) {
        this.umzugDatum = value;
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
     * Gets the value of the wegzugDatum property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getWegzugDatum() {
        return wegzugDatum;
    }

    /**
     * Sets the value of the wegzugDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setWegzugDatum(XMLGregorianCalendar value) {
        this.wegzugDatum = value;
    }

    /**
     * Gets the value of the zuzugDatum property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getZuzugDatum() {
        return zuzugDatum;
    }

    /**
     * Sets the value of the zuzugDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setZuzugDatum(XMLGregorianCalendar value) {
        this.zuzugDatum = value;
    }

}
