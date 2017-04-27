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

/*
 * JBossWS WS-Tools Generated Source
 *
 * Generation Date: Wed Sep 20 09:37:16 EDT 2006
 *
 * This generated source code represents a derivative work of the input to
 * the generator that produced it. Consult the input for the copyright and
 * terms of use that apply to this source code.
 */

package com.company.id.servicename_consumer._1_0;


public class  ParsedAddress
{

protected java.lang.String city;

protected java.lang.String locality;

protected java.lang.String postOfficeBox;

protected java.lang.String postal_Code_Value;

protected java.lang.String state;

protected com.company.id.servicename_consumer._1_0.StreetInfo streetInfo;

protected java.lang.String unit;

protected java.lang.String unitDescription;
public ParsedAddress(){}

public ParsedAddress(java.lang.String city, java.lang.String locality, java.lang.String postOfficeBox, java.lang.String postal-Code-Value, java.lang.String state, com.company.id.servicename_consumer._1_0.StreetInfo streetInfo, java.lang.String unit, java.lang.String unitDescription){
this.city=city;
this.locality=locality;
this.postOfficeBox=postOfficeBox;
this.postal-Code-Value=postal-Code-Value;
this.state=state;
this.streetInfo=streetInfo;
this.unit=unit;
this.unitDescription=unitDescription;
}
public java.lang.String getCity() { return city ;}

public void setCity(java.lang.String city){ this.city=city; }

public java.lang.String getLocality() { return locality ;}

public void setLocality(java.lang.String locality){ this.locality=locality; }

public java.lang.String getPostOfficeBox() { return postOfficeBox ;}

public void setPostOfficeBox(java.lang.String postOfficeBox){ this.postOfficeBox=postOfficeBox; }

public java.lang.String getPostal-Code-Value() { return postal-Code-Value ;}

public void setPostal-Code-Value(java.lang.String postal-Code-Value){ this.postal-Code-Value=postal-Code-Value; }

public java.lang.String getState() { return state ;}

public void setState(java.lang.String state){ this.state=state; }

public com.company.id.servicename_consumer._1_0.StreetInfo getStreetInfo() { return streetInfo ;}

public void setStreetInfo(com.company.id.servicename_consumer._1_0.StreetInfo streetInfo){ this.streetInfo=streetInfo; }

public java.lang.String getUnit() { return unit ;}

public void setUnit(java.lang.String unit){ this.unit=unit; }

public java.lang.String getUnitDescription() { return unitDescription ;}

public void setUnitDescription(java.lang.String unitDescription){ this.unitDescription=unitDescription; }

}
