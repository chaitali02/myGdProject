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

public class DashboardSectionMeta {
	private MetaIdentifier ref;
	private String x_pos;
	private String y_pos;
	
	public MetaIdentifier getRef() {
		return ref;
	}
	public void setRef(MetaIdentifier ref) {
		this.ref = ref;
	}
	public String getX_pos() {
		return x_pos;
	}
	public void setX_pos(String x_pos) {
		this.x_pos = x_pos;
	}
	public String getY_pos() {
		return y_pos;
	}
	public void setY_pos(String y_pos) {
		this.y_pos = y_pos;
	}
	
}
