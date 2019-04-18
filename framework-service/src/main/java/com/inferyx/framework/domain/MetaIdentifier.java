/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.domain;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class MetaIdentifier {

	private MetaType type;
	private String uuid;
	private String version;
	private String name;
	private String displayName;

	public MetaIdentifier() {
		super();// TODO Auto-generated constructor stub
	}
	
	public MetaIdentifier(MetaType type, String uuid, String version) {
		super();
		this.type = type;
		this.uuid = uuid;
		this.version = version;
	}
	
	public MetaIdentifier(MetaType type, String uuid, String version, String name) {
		super();
		this.type = type;
		this.uuid = uuid;
		this.version = version;
		this.name = name;
	}
	
	public MetaType getType() {
		return type;
	}
	public void setType(MetaType type) {
		this.type = type;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/* Obsolete method and can be removed
	 * 
	 * @JsonIgnore
	@Transient
	public Object getCollectionObject(Loader loader) {
		OrderKey key = new OrderKey(getUuid(), getVersion());
		Set<OrderKey> keySets = null;
		
		if(type == MetaType.datapod) {
			keySets = loader.getCachDataPod().keySet();
			OrderKey oKey = loader.getLatestMetaKey(keySets, key);
			setVersion(oKey.getVersion());
			return loader.getCachDataPod().get(oKey);
		}
		if(type == MetaType.filter) {
			keySets = loader.getCachFilter().keySet();
			OrderKey oKey = loader.getLatestMetaKey(keySets, key);
			setVersion(oKey.getVersion());
			return loader.getCachFilter().get(oKey);

		}
		if(type == MetaType.relation) {
			keySets = loader.getCachRelation().keySet();
			OrderKey oKey = loader.getLatestMetaKey(keySets, key);
			setVersion(oKey.getVersion());
			return loader.getCachRelation().get(oKey);

		}
		if(type == MetaType.formula) {
			keySets = loader.getCachFormula().keySet();
			OrderKey oKey = loader.getLatestMetaKey(keySets, key);
			setVersion(oKey.getVersion());
			return loader.getCachFormula().get(oKey);

		}
		if(type == MetaType.condition) {
			keySets = loader.getCacheCondition().keySet();
			OrderKey oKey = loader.getLatestMetaKey(keySets, key);
			setVersion(oKey.getVersion());
			return loader.getCacheCondition().get(oKey);

		}
		if(type == MetaType.expression) {
			keySets = loader.getCachExpression().keySet();
			OrderKey oKey = loader.getLatestMetaKey(keySets, key);
			setVersion(oKey.getVersion());
			return loader.getCachExpression().get(oKey);

		}
		if(type == MetaType.dagexec) {
			keySets = loader.getCachDagExec().keySet();
			OrderKey oKey = loader.getLatestMetaKey(keySets, key);
			setVersion(oKey.getVersion());
			return loader.getCachDagExec().get(oKey);

		}
		if(type == MetaType.map) {
			keySets = loader.getCachMap().keySet();
			OrderKey oKey = loader.getLatestMetaKey(keySets, key);
			setVersion(oKey.getVersion());
			return loader.getCachMap().get(oKey);
		}
		if(type == MetaType.dag) {
			keySets = loader.getCachDAG().keySet();
			OrderKey oKey = loader.getLatestMetaKey(keySets, key);
			setVersion(oKey.getVersion());
			return loader.getCachDAG().get(oKey);
		}

		System.out.println("Meta not found");
		return null;
	}*/

	@JsonIgnore
	@Transient
	public OrderKey getKey() {
		return new OrderKey(getUuid(), getVersion());
	}
	public String toString() {
		ObjectMapper obj = new ObjectMapper();
		try {
			return obj.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			
		}
		
		return super.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		MetaIdentifier other = (MetaIdentifier) obj;
		if (type != other.type)
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}


	
	
	
}
