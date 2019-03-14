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
package com.inferyx.framework.register;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.FileType;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Registry;
import com.inferyx.framework.enums.Compare;
import com.inferyx.framework.enums.RegistryType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.DatasourceServiceImpl;

@Component
public class CSVRegister extends DataSourceRegister {
	
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	DatasourceServiceImpl datasourceServiceImpl;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;
    @Autowired
	HDFSInfo hdfsInfo;
    
	/*public DataFrame load(String filePath, HiveContext hiveContext){
		return hiveContext.read().format("com.databricks.spark.csv").option("inferSchema", "true")
				.option("header", "true").load(filePath);
	}*/

	public List<Registry> register(String uuid, String version, List<Registry> registryList, RunMode runMode) throws Exception {
		Datasource ds = (Datasource) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.datasource.toString());
		String filepath = hdfsInfo.getHdfsURL()+ds.getPath();
		for(int i=0; i<registryList.size(); i++) {
			MetaIdentifierHolder loadExec = null;
			try {
				loadExec = datapodServiceImpl.createAndLoad(filepath+registryList.get(i).getName()+"."+FileType.CSV.toString().toLowerCase(), runMode);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			if(loadExec != null) {
				registryList.get(i).setStatus(RegistryType.REGISTERED.toString());
			} else {
				registryList.get(i).setStatus(RegistryType.NOT_REGISTERED.toString());
			}
			registryList.get(i).setCompareStatus(Compare.NEW.toString());
			Datapod dp = datapodServiceImpl.findOneByName(registryList.get(i).getName().toLowerCase());
			registryList.get(i).setRegisteredOn(dp.getCreatedOn());
			registryList.get(i).setRegisteredBy(dp.getCreatedBy().getRef().getName());
		}
 		return registryList;
	}

	
}
