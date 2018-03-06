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

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.domain.DagExec;

public class DagExecJsonToObject {
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		if (args.length == 0 || args[0] == null )
			return;
		
		// set dagexec json file path
		String filePath= args[0];
		ObjectMapper mapper = new ObjectMapper();
		
		DagExec dagExec = (DagExec) mapper.readValue(new File(filePath), DagExec.class);
		System.out.println("Ok");
	}
}
