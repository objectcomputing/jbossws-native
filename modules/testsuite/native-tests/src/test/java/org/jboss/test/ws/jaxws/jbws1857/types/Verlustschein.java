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
 * <p>Java class for verlustschein complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="verlustschein">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ausstellDatum" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ausstellendeBehoerde" type="{http://example.com}ausstellendeBehoerde" minOccurs="0"/>
 *         &lt;element name="bemerkung1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bemerkung2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="betreibungsNr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateCreated" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dateModified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="eresetztVerlustschein" type="{http://example.com}verlustschein" minOccurs="0"/>
 *         &lt;element name="forderungsgrund" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="glaeubigerVerbindung" type="{http://example.com}glaeubigerVerbindung" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="glaeubigerVertreter" type="{http://example.com}glaeubigerVertreter" minOccurs="0"/>
 *         &lt;element name="haftung" type="{http://example.com}haftungArt" minOccurs="0"/>
 *         &lt;element name="herkunftReferenz" type="{http://example.com}herkunftReferenz" minOccurs="0"/>
 *         &lt;element name="mandant" type="{http://example.com}mandant" minOccurs="0"/>
 *         &lt;element name="schuldner" type="{http://example.com}personInterface" minOccurs="0"/>
 *         &lt;element name="status" type="{http://example.com}statusType" minOccurs="0"/>
 *         &lt;element name="userIdCreated" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userIdModified" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="verjaehrungsfrist" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="verlustscheinArt" type="{http://example.com}verlustscheinArt" minOccurs="0"/>
 *         &lt;element name="verlustscheinId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="vsNummer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verlustschein", propOrder = {
    "ausstellDatum",
    "ausstellendeBehoerde",
    "bemerkung1",
    "bemerkung2",
    "betreibungsNr",
    "dateCreated",
    "dateModified",
    "eresetztVerlustschein",
    "forderungsgrund",
    "glaeubigerVerbindung",
    "glaeubigerVertreter",
    "haftung",
    "herkunftReferenz",
    "mandant",
    "schuldner",
    "status",
    "userIdCreated",
    "userIdModified",
    "verjaehrungsfrist",
    "verlustscheinArt",
    "verlustscheinId",
    "vsNummer"
})
public class Verlustschein {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar ausstellDatum;
    protected AusstellendeBehoerde ausstellendeBehoerde;
    protected String bemerkung1;
    protected String bemerkung2;
    protected String betreibungsNr;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateCreated;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateModified;
    protected Verlustschein eresetztVerlustschein;
    protected String forderungsgrund;
    @XmlElement(nillable = true)
    protected List<GlaeubigerVerbindung> glaeubigerVerbindung;
    protected GlaeubigerVertreter glaeubigerVertreter;
    protected HaftungArt haftung;
    protected HerkunftReferenz herkunftReferenz;
    protected Mandant mandant;
    protected PersonInterface schuldner;
    protected StatusType status;
    protected String userIdCreated;
    protected String userIdModified;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar verjaehrungsfrist;
    protected VerlustscheinArt verlustscheinArt;
    protected Long verlustscheinId;
    protected String vsNummer;

    /**
     * Gets the value of the ausstellDatum property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAusstellDatum() {
        return ausstellDatum;
    }

    /**
     * Sets the value of the ausstellDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAusstellDatum(XMLGregorianCalendar value) {
        this.ausstellDatum = value;
    }

    /**
     * Gets the value of the ausstellendeBehoerde property.
     * 
     * @return
     *     possible object is
     *     {@link AusstellendeBehoerde }
     *     
     */
    public AusstellendeBehoerde getAusstellendeBehoerde() {
        return ausstellendeBehoerde;
    }

    /**
     * Sets the value of the ausstellendeBehoerde property.
     * 
     * @param value
     *     allowed object is
     *     {@link AusstellendeBehoerde }
     *     
     */
    public void setAusstellendeBehoerde(AusstellendeBehoerde value) {
        this.ausstellendeBehoerde = value;
    }

    /**
     * Gets the value of the bemerkung1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBemerkung1() {
        return bemerkung1;
    }

    /**
     * Sets the value of the bemerkung1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBemerkung1(String value) {
        this.bemerkung1 = value;
    }

    /**
     * Gets the value of the bemerkung2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBemerkung2() {
        return bemerkung2;
    }

    /**
     * Sets the value of the bemerkung2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBemerkung2(String value) {
        this.bemerkung2 = value;
    }

    /**
     * Gets the value of the betreibungsNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBetreibungsNr() {
        return betreibungsNr;
    }

    /**
     * Sets the value of the betreibungsNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBetreibungsNr(String value) {
        this.betreibungsNr = value;
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
     * Gets the value of the eresetztVerlustschein property.
     * 
     * @return
     *     possible object is
     *     {@link Verlustschein }
     *     
     */
    public Verlustschein getEresetztVerlustschein() {
        return eresetztVerlustschein;
    }

    /**
     * Sets the value of the eresetztVerlustschein property.
     * 
     * @param value
     *     allowed object is
     *     {@link Verlustschein }
     *     
     */
    public void setEresetztVerlustschein(Verlustschein value) {
        this.eresetztVerlustschein = value;
    }

    /**
     * Gets the value of the forderungsgrund property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForderungsgrund() {
        return forderungsgrund;
    }

    /**
     * Sets the value of the forderungsgrund property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForderungsgrund(String value) {
        this.forderungsgrund = value;
    }

    /**
     * Gets the value of the glaeubigerVerbindung property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the glaeubigerVerbindung property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlaeubigerVerbindung().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GlaeubigerVerbindung }
     * 
     * 
     */
    public List<GlaeubigerVerbindung> getGlaeubigerVerbindung() {
        if (glaeubigerVerbindung == null) {
            glaeubigerVerbindung = new ArrayList<GlaeubigerVerbindung>();
        }
        return this.glaeubigerVerbindung;
    }

    /**
     * Gets the value of the glaeubigerVertreter property.
     * 
     * @return
     *     possible object is
     *     {@link GlaeubigerVertreter }
     *     
     */
    public GlaeubigerVertreter getGlaeubigerVertreter() {
        return glaeubigerVertreter;
    }

    /**
     * Sets the value of the glaeubigerVertreter property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlaeubigerVertreter }
     *     
     */
    public void setGlaeubigerVertreter(GlaeubigerVertreter value) {
        this.glaeubigerVertreter = value;
    }

    /**
     * Gets the value of the haftung property.
     * 
     * @return
     *     possible object is
     *     {@link HaftungArt }
     *     
     */
    public HaftungArt getHaftung() {
        return haftung;
    }

    /**
     * Sets the value of the haftung property.
     * 
     * @param value
     *     allowed object is
     *     {@link HaftungArt }
     *     
     */
    public void setHaftung(HaftungArt value) {
        this.haftung = value;
    }

    /**
     * Gets the value of the herkunftReferenz property.
     * 
     * @return
     *     possible object is
     *     {@link HerkunftReferenz }
     *     
     */
    public HerkunftReferenz getHerkunftReferenz() {
        return herkunftReferenz;
    }

    /**
     * Sets the value of the herkunftReferenz property.
     * 
     * @param value
     *     allowed object is
     *     {@link HerkunftReferenz }
     *     
     */
    public void setHerkunftReferenz(HerkunftReferenz value) {
        this.herkunftReferenz = value;
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
     * Gets the value of the schuldner property.
     * 
     * @return
     *     possible object is
     *     {@link PersonInterface }
     *     
     */
    public PersonInterface getSchuldner() {
        return schuldner;
    }

    /**
     * Sets the value of the schuldner property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonInterface }
     *     
     */
    public void setSchuldner(PersonInterface value) {
        this.schuldner = value;
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
     * Gets the value of the verjaehrungsfrist property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getVerjaehrungsfrist() {
        return verjaehrungsfrist;
    }

    /**
     * Sets the value of the verjaehrungsfrist property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setVerjaehrungsfrist(XMLGregorianCalendar value) {
        this.verjaehrungsfrist = value;
    }

    /**
     * Gets the value of the verlustscheinArt property.
     * 
     * @return
     *     possible object is
     *     {@link VerlustscheinArt }
     *     
     */
    public VerlustscheinArt getVerlustscheinArt() {
        return verlustscheinArt;
    }

    /**
     * Sets the value of the verlustscheinArt property.
     * 
     * @param value
     *     allowed object is
     *     {@link VerlustscheinArt }
     *     
     */
    public void setVerlustscheinArt(VerlustscheinArt value) {
        this.verlustscheinArt = value;
    }

    /**
     * Gets the value of the verlustscheinId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getVerlustscheinId() {
        return verlustscheinId;
    }

    /**
     * Sets the value of the verlustscheinId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setVerlustscheinId(Long value) {
        this.verlustscheinId = value;
    }

    /**
     * Gets the value of the vsNummer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVsNummer() {
        return vsNummer;
    }

    /**
     * Sets the value of the vsNummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVsNummer(String value) {
        this.vsNummer = value;
    }

}
