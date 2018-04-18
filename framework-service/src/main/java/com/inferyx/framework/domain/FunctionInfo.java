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
import java.util.List;

public  class FunctionInfo {
		private String name;
		private String type;
		private List<ParamInfoHolder> paramInfoHolder;

		public List<ParamInfoHolder> getParamInfoHolder() {
			return paramInfoHolder;
		}

		public void setParamInfoHolder(List<ParamInfoHolder> paramInfoHolder) {
			this.paramInfoHolder = paramInfoHolder;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setType(String type) {
			this.type = type;
		}

		

	}