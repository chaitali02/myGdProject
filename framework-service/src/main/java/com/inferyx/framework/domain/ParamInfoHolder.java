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
public  class ParamInfoHolder {
			private String paramId;
			private String paramName;
			private String paramType;
			private String paramReq;
			private String paramDefVal;

			public String getParamId() {
				return paramId;
			}

			public void setParamId(String paramId) {
				this.paramId = paramId;
			}

			public String getParamName() {
				return paramName;
			}

			public void setParamName(String paramName) {
				this.paramName = paramName;
			}

			public String getParamType() {
				return paramType;
			}

			public void setParamType(String paramType) {
				this.paramType = paramType;
			}

			public String getParamReq() {
				return paramReq;
			}

			public void setParamReq(String paramReq) {
				this.paramReq = paramReq;
			}

			public String getParamDefVal() {
				return paramDefVal;
			}

			public void setParamDefVal(String paramDefVal) {
				this.paramDefVal = paramDefVal;
			}
		}