package com.inferyx.framework.register;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.Registry;
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

	public List<Registry> register(String uuid, String version, List<Registry> registryList, Mode runMode) throws Exception {
		//Datasource ds = datasourceServiceImpl.findOneByUuidAndVersion(uuid, version);
		Datasource ds = (Datasource) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.datasource.toString());
		String filepath = hdfsInfo.getHdfsURL()+ds.getPath();
		for(int i=0; i<registryList.size(); i++) {
			MetaIdentifierHolder dagExec = datapodServiceImpl.createAndLoad(filepath+registryList.get(i).getName()+".csv", runMode);
			if(dagExec != null) {
				registryList.get(i).setStatus("Registered");
				Datapod dp = datapodServiceImpl.findOneByName(registryList.get(i).getName());
				registryList.get(i).setRegisteredOn(dp.getCreatedOn());
				registryList.get(i).setRegisteredBy(dp.getCreatedBy().getRef().getName());

			}
		}
 		return registryList;
	}

	
}
