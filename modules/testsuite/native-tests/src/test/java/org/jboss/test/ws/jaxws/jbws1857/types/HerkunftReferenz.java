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
 * <p>Java class for herkunftReferenz complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="herkunftReferenz">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ausloesendeAnwendung" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ausloesendeStelleAutomatisch" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ausloesendeStelleManuell" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ausloesenderSachbearbManuell" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ausloesenderSachbearbeiterautomatisch" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="glaeubigervertreter" type="{http://example.com}glaeubigerVertreter" minOccurs="0"/>
 *         &lt;element name="herkunftReferenzId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="mandant" type="{http://example.com}mandant" minOccurs="0"/>
 *         &lt;element name="referenzAutomatisch" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="referenzManuell" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "herkunftReferenz", propOrder = {
    "ausloesendeAnwendung",
    "ausloesendeStelleAutomatisch",
    "ausloesendeStelleManuell",
    "ausloesenderSachbearbManuell",
    "ausloesenderSachbearbeiterautomatisch",
    "glaeubigervertreter",
    "herkunftReferenzId",
    "mandant",
    "referenzAutomatisch",
    "referenzManuell"
})
public class HerkunftReferenz {

    protected String ausloesendeAnwendung;
    protected String ausloesendeStelleAutomatisch;
    protected String ausloesendeStelleManuell;
    protected String ausloesenderSachbearbManuell;
    protected String ausloesenderSachbearbeiterautomatisch;
    protected GlaeubigerVertreter glaeubigervertreter;
    protected Long herkunftReferenzId;
    protected Mandant mandant;
    protected String referenzAutomatisch;
    protected String referenzManuell;

    /**
     * Gets the value of the ausloesendeAnwendung property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAusloesendeAnwendung() {
        return ausloesendeAnwendung;
    }

    /**
     * Sets the value of the ausloesendeAnwendung property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAusloesendeAnwendung(String value) {
        this.ausloesendeAnwendung = value;
    }

    /**
     * Gets the value of the ausloesendeStelleAutomatisch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAusloesendeStelleAutomatisch() {
        return ausloesendeStelleAutomatisch;
    }

    /**
     * Sets the value of the ausloesendeStelleAutomatisch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAusloesendeStelleAutomatisch(String value) {
        this.ausloesendeStelleAutomatisch = value;
    }

    /**
     * Gets the value of the ausloesendeStelleManuell property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAusloesendeStelleManuell() {
        return ausloesendeStelleManuell;
    }

    /**
     * Sets the value of the ausloesendeStelleManuell property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAusloesendeStelleManuell(String value) {
        this.ausloesendeStelleManuell = value;
    }

    /**
     * Gets the value of the ausloesenderSachbearbManuell property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAusloesenderSachbearbManuell() {
        return ausloesenderSachbearbManuell;
    }

    /**
     * Sets the value of the ausloesenderSachbearbManuell property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAusloesenderSachbearbManuell(String value) {
        this.ausloesenderSachbearbManuell = value;
    }

    /**
     * Gets the value of the ausloesenderSachbearbeiterautomatisch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAusloesenderSachbearbeiterautomatisch() {
        return ausloesenderSachbearbeiterautomatisch;
    }

    /**
     * Sets the value of the ausloesenderSachbearbeiterautomatisch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAusloesenderSachbearbeiterautomatisch(String value) {
        this.ausloesenderSachbearbeiterautomatisch = value;
    }

    /**
     * Gets the value of the glaeubigervertreter property.
     * 
     * @return
     *     possible object is
     *     {@link GlaeubigerVertreter }
     *     
     */
    public GlaeubigerVertreter getGlaeubigervertreter() {
        return glaeubigervertreter;
    }

    /**
     * Sets the value of the glaeubigervertreter property.
     * 
     * @param value
     *     allowed object is
     *     {@link GlaeubigerVertreter }
     *     
     */
    public void setGlaeubigervertreter(GlaeubigerVertreter value) {
        this.glaeubigervertreter = value;
    }

    /**
     * Gets the value of the herkunftReferenzId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getHerkunftReferenzId() {
        return herkunftReferenzId;
    }

    /**
     * Sets the value of the herkunftReferenzId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setHerkunftReferenzId(Long value) {
        this.herkunftReferenzId = value;
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
     * Gets the value of the referenzAutomatisch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenzAutomatisch() {
        return referenzAutomatisch;
    }

    /**
     * Sets the value of the referenzAutomatisch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenzAutomatisch(String value) {
        this.referenzAutomatisch = value;
    }

    /**
     * Gets the value of the referenzManuell property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenzManuell() {
        return referenzManuell;
    }

    /**
     * Sets the value of the referenzManuell property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenzManuell(String value) {
        this.referenzManuell = value;
    }

}
