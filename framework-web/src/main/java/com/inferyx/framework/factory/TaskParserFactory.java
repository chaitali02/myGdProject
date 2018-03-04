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
package com.inferyx.framework.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.parser.MapTaskParser;
import com.inferyx.framework.parser.TaskParser;

@Component
public class TaskParserFactory {
	
	@Autowired private MapTaskParser mapTaskParser;
	@Autowired private TaskParser taskParser;
	
	public TaskParser getTaskParser(MetaType taskType) {
		switch (taskType) {
			case map :
				return mapTaskParser;
			case mapiter : 
				return taskParser;
			default : 
				return taskParser;
		}
	}

}
