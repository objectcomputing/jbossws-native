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
package org.jboss.test.ws.jaxws.samples.dar.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for darRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="darRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="buses" type="{http://org.jboss.ws/samples/dar}bus" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mapId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requests" type="{http://org.jboss.ws/samples/dar}serviceRequest" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "darRequest", propOrder = {
    "buses",
    "mapId",
    "requests"
})
public class DarRequest {

    @XmlElement(nillable = true)
    protected List<Bus> buses;
    protected String mapId;
    @XmlElement(nillable = true)
    protected List<ServiceRequest> requests;

    /**
     * Gets the value of the buses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the buses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBuses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Bus }
     * 
     * 
     */
    public List<Bus> getBuses() {
        if (buses == null) {
            buses = new ArrayList<Bus>();
        }
        return this.buses;
    }

    /**
     * Gets the value of the mapId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMapId() {
        return mapId;
    }

    /**
     * Sets the value of the mapId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMapId(String value) {
        this.mapId = value;
    }

    /**
     * Gets the value of the requests property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requests property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequests().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServiceRequest }
     * 
     * 
     */
    public List<ServiceRequest> getRequests() {
        if (requests == null) {
            requests = new ArrayList<ServiceRequest>();
        }
        return this.requests;
    }

}
