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
package com.inferyx.framework.test;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.domain.MetaIdentifier;

class Key implements Comparable<Key>{
	public String uuid;
	public Long version;
	@Override
	public int compareTo(Key o) {
		int diff = ((int) (version - o.version));
		if (diff == 0 )
			return -1 * uuid.compareTo(o.uuid);
			
		return -1 * diff;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Key other = (Key) obj;
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

public class TreeMapTest {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		Key k1= new Key();
		k1.uuid = "abcd";
		k1.version = new Long(3);

		Key k2 = new Key();
		k2.uuid = "abce";
		k2.version = new Long(5);

		Key k3 = new Key();
		k3.uuid = "abcf";
		k3.version = new Long(4);

		Key k4 = new Key();
		k4.uuid = "abcg";
		k4.version = new Long(4);

		
		Map<Key, Object> hash = new TreeMap<>();
	
		hash.put(k1, new String("Ok1"));
		hash.put(k2, new String("Ok2"));
		hash.put(k3, new String("Ok3"));
		hash.put(k4, new String("Ok4"));
		
		for(Key key : hash.keySet()) {
			System.out.println(key.uuid);
		}
		
		String json = "{}";
		
		
		MetaIdentifier id = (new ObjectMapper().readValue(json.getBytes(), MetaIdentifier.class));
		
		
		
	}
}
