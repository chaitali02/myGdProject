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
package com.inferyx.framework.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.inferyx.framework.domain.Status;

public class ExecUtil {
	
	/***************************Unused***************************/
	/*public static List<Status> createStatus (List<Status> statusList, Status.Stage status) {
		if (statusList == null) {
			statusList = new ArrayList<>();
		}
		
		if (statusList.contains(Status.Stage.FAILED)) {
			return statusList;
		}
		statusList.add(new Status(status, new Date()));
		return statusList;
	}
*/
}
