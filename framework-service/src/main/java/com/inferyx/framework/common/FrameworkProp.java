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

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

@Component
public class FrameworkProp {

	public Map<String, String> getProperties() throws IOException {
		Resource resource = new ClassPathResource("/framework.properties");
		Properties prop = PropertiesLoaderUtils.loadProperties(resource);          
		Map<String, String> frameworkProps = new HashMap<String,String>();		
		try {
			String key;
			String value;			
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				key = (String) e.nextElement();
				 value = prop.getProperty(key);							
				if(key.contains("framework"))
				{
					frameworkProps.put(key, value);
				}					
				}
		} finally {
			
			}
		
        return frameworkProps;
    }
}
