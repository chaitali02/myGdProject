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
//import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.spark.SparkConf;
import org.springframework.stereotype.Component;

import com.inferyx.framework.executor.ExecContext;

@Component
public class SparkInfo {
	private Properties prop;	   
    
    public Properties getProp() {
		return prop;
	}

	public void setProp(Properties prop) {
		this.prop = prop;
	}

	public SparkConf getSparkConfiguration() throws IOException {
        SparkConf sparkConf = new SparkConf();        
		Map<String, String> sparkProps = new HashMap<String,String>();		
		try {
			String key;
			String value;
			String sparkkey = null;
			String sparkvalue = null;
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				key = (String) e.nextElement();
				 value = prop.getProperty(key);							
				if(key.contains(ExecContext.spark.toString())) {					
					sparkProps.put(key, value);				
				}
			}
			for(int i=0; i<sparkProps.size(); i++)
			{				
				for(Map.Entry<String, String> ent : sparkProps.entrySet()) {
					sparkkey = ent.getKey().toString();
					sparkvalue = ent.getValue().toString();		
					sparkConf.set(sparkkey, sparkvalue);
				}
			}
		} finally {
			}
        return sparkConf;
    }
}
