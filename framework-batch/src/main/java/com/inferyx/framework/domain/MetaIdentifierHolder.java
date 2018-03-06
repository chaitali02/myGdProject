/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.domain;

import java.io.Serializable;

public class MetaIdentifierHolder {
	private MetaIdentifier ref;
	private String value;
	//private String attributeId;	// Optional
	//private String attrName; //Optional

	/*public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}*/

	public MetaIdentifierHolder() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public MetaIdentifierHolder(MetaIdentifier ref, String value) {
		super();
		this.ref = ref;
		this.value = value;
	}
	
	public MetaIdentifierHolder(MetaIdentifier ref) {
		super();
		this.ref = ref;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public MetaIdentifier getRef() {
		return ref;
	}
	public void setRef(MetaIdentifier ref) {
		this.ref = ref;
	}
	/*public String getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}*/

	@Override
	public String toString() {
		return "MetaIdentifierHolder [ref=" + ref + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaIdentifierHolder other = (MetaIdentifierHolder) obj;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
