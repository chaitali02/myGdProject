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

/**
 * @author joy
 *
 */
public class BaseRuleExec extends BaseExec {
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BaseRuleExec [statusList=" + statusList + ", dependsOn=" + dependsOn + ", exec=" + exec
				+ ", summaryExec=" + summaryExec + ", result=" + result + ", refKeyList=" + refKeyList + ", execParams="
				+ execParams + ", securityServiceImpl=" + securityServiceImpl + "]";
	}

}
