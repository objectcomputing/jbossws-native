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
package org.jboss.test.ws.jaxrpc.jbws168;

public class UserType {

	// The alphabetical order of these properties matters
	// It is different from what is defined in schema
	private String propB;

	private String propC;

	private String propA;

	public UserType() {
	}

	public UserType(String propa, String propb, String propc) {
		propA = propa;
		propB = propb;
		propC = propc;
	}

	public String getPropB() {
		return propB;
	}

	public void setPropB(String propB) {
		this.propB = propB;
	}

	public String getPropC() {
		return propC;
	}

	public void setPropC(String propC) {
		this.propC = propC;
	}

	public String getPropA() {
		return propA;
	}

	public void setPropA(String propA) {
		this.propA = propA;
	}

	public boolean equals(Object arg0) {
		if (arg0 == this) return true;
		if (!(arg0 instanceof UserType)) return false;
		UserType ut = (UserType) arg0;
		return ut.toString().equals(toString());
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public String toString() {
		return "[propA=" + propA + ",propB=" + propB + ",propC=" + propC + "]";
	}
}
