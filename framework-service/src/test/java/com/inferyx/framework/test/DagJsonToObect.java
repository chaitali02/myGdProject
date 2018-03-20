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
import com.inferyx.framework.domain.Map;

public class DagJsonToObect {
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		// set dagexec json file path
		String filePath= "/git/framework/meta/map/company.map";
		ObjectMapper mapper = new ObjectMapper();
		
		Map dagExec = (Map) mapper.readValue(new File(filePath), Map.class);
		System.out.println("Ok");
	}
}
