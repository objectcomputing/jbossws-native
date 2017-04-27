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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for kantonType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="kantonType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="JU"/>
 *     &lt;enumeration value="GE"/>
 *     &lt;enumeration value="NE"/>
 *     &lt;enumeration value="VS"/>
 *     &lt;enumeration value="VD"/>
 *     &lt;enumeration value="TI"/>
 *     &lt;enumeration value="TG"/>
 *     &lt;enumeration value="AG"/>
 *     &lt;enumeration value="GR"/>
 *     &lt;enumeration value="SG"/>
 *     &lt;enumeration value="AI"/>
 *     &lt;enumeration value="AR"/>
 *     &lt;enumeration value="SH"/>
 *     &lt;enumeration value="BL"/>
 *     &lt;enumeration value="BS"/>
 *     &lt;enumeration value="SO"/>
 *     &lt;enumeration value="FR"/>
 *     &lt;enumeration value="ZG"/>
 *     &lt;enumeration value="GL"/>
 *     &lt;enumeration value="NW"/>
 *     &lt;enumeration value="OW"/>
 *     &lt;enumeration value="SZ"/>
 *     &lt;enumeration value="UR"/>
 *     &lt;enumeration value="LU"/>
 *     &lt;enumeration value="BE"/>
 *     &lt;enumeration value="ZH"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "kantonType")
@XmlEnum
public enum KantonType {

    JU,
    GE,
    NE,
    VS,
    VD,
    TI,
    TG,
    AG,
    GR,
    SG,
    AI,
    AR,
    SH,
    BL,
    BS,
    SO,
    FR,
    ZG,
    GL,
    NW,
    OW,
    SZ,
    UR,
    LU,
    BE,
    ZH;

    public String value() {
        return name();
    }

    public static KantonType fromValue(String v) {
        return valueOf(v);
    }

}
