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

import org.apache.hadoop.hive.conf.HiveConf;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import com.inferyx.framework.executor.ExecContext;

@Component

public class HiveInfo {
	
	
/*private Properties prop;	   
    
    public Properties getProp() {
		return prop;
	}

	public void setProp(Properties prop) {
		this.prop = prop;
	}*/
	
	
/************************************Unused******************************/
	/*public HiveConf getHiveConfiguration() throws IOException {
		Resource resource = new ClassPathResource("/framework.properties");
		Properties prop = PropertiesLoaderUtils.loadProperties(resource);
        HiveConf hiveConf = new HiveConf();        
		Map<String, String> hiveProps = new HashMap<String,String>();		
		try {
			String key;
			String value;
			String hiveKey = null;
			String hiveValue = null;
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				key = (String) e.nextElement();
				 value = prop.getProperty(key);							
				if(key.contains(ExecContext.HIVE.toString()))
				{					
					hiveProps.put(key, value);							
					
					}	
					
				}
			
			for(int i=0; i<hiveProps.size(); i++)
			{				
				for(Map.Entry<String, String> ent : hiveProps.entrySet())
				{
					hiveKey = ent.getKey().toString();
					hiveValue = ent.getValue().toString();		
					hiveConf.set(hiveKey, hiveValue);
				}			
				
			}		

		} finally {
			
			}
		
        return hiveConf;
    }*/
}
