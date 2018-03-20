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

public class OrderKey extends Key {

	public OrderKey() {
	}
	
	public OrderKey(String uuid, String version) {
		super(uuid, version);
	}

/*	public int compareTo(OrderKey o) {
            int diff = ((int) (getVersion() - o.getVersion()));
            if (diff == 0 )
                    return -1 * getUUID().compareTo(o.getUUID());

            return -1 * diff;
    }
*/
}
