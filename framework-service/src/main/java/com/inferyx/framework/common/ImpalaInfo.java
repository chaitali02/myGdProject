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


import org.springframework.stereotype.Component;

@Component
public class ImpalaInfo {

/****************************Unused******************************/
	/*	public ImpalaInfo getImpalaConfiguration() throws IOException {
		Resource resource = new ClassPathResource("/framework.properties");
		Properties prop = PropertiesLoaderUtils.loadProperties(resource);
		// impala confi object
		Map<String, String> impalaProps = new HashMap<String, String>();
		try {
			String key;
			String value;
			String impalaKey = null;
			String impalaValue = null;
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				key = (String) e.nextElement();
				value = prop.getProperty(key);
				if (key.contains(ExecContext.IMPALA.toString())) {

					impalaProps.put(key, value);
				}
			}

			for (int i = 0; i < impalaProps.size(); i++) {

				for (Map.Entry<String, String> entry : impalaProps.entrySet()) {

					impalaKey = entry.getKey().toString();
					impalaValue = entry.getValue().toString();
					// impalaConfig
				}
			}
		} finally {

		}

		return null;

	}*/

}
