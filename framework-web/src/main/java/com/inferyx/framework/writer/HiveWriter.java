package com.inferyx.framework.writer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.service.CommonServiceImpl;

@Component
public class HiveWriter implements IWriter {
	Logger logger=Logger.getLogger(HiveWriter.class);
	CommonServiceImpl<?> commonServiceImpl;
	
	private MetadataUtil daoRegister;
	public MetadataUtil getDaoRegister() {
		return daoRegister;
	}
	public void setDaoRegister(MetadataUtil daoRegister) {
		this.daoRegister = daoRegister;
	}
	@Override
	public void write(Dataset<Row> df, String filePathUrl, Datapod d, String saveMode){	
		try{
			Datasource dataSource = daoRegister.getiDatasourceDao().findLatestByUuid(d.getDatasource().getRef().getUuid(), new Sort(Sort.Direction.DESC, "version")); 
			//Datasource dataSource =  daoRegister.getiDatasourceDao().findOneByUuidAndVersion(d.getDatasource().getRef().getUuid(),d.getDatasource().getRef().getVersion());
			logger.info("Table Name: "+dataSource.getDbname().concat(".").concat(d.getName()));
			List<Attribute> attrList = d.getAttributes();		
			List<String> parList = new ArrayList<String>();
			for(Attribute attr : attrList)
				if(attr.getPartition().equalsIgnoreCase("y")) 
					parList.add(attr.getName());
			if(parList.size() > 0){
				StringBuilder sb = new StringBuilder();
				for(int i=0; i<parList.size(); i++)
					sb.append(parList.get(i)).append(",");
				String[] columnList = sb.substring(0, sb.length()-1).toString().split(",");
				logger.info("Partition column list: "+Arrays.asList(columnList));
				df.sqlContext().sql("set hive.exec.dynamic.partition.mode="+Helper.getPropertyValue("hive.exec.dynamic.partition.mode"));
				//String partitionMode = commonServiceImpl.getSessionParametresPropertyValue("hive.exec.dynamic.partition.mode", "nonstrict");
				//df.sqlContext().sql("set hive.exec.dynamic.partition.mode="+partitionMode);
				df.write().mode(SaveMode.Append).partitionBy(columnList).insertInto(dataSource.getDbname().concat(".").concat(d.getName()));
			}else {
				if(saveMode.equalsIgnoreCase("append"))	
					df.write().mode(SaveMode.Append).saveAsTable(dataSource.getDbname().concat(".").concat(d.getName()));
				else if(saveMode.equalsIgnoreCase("overwrite")) 
					df.write().mode(SaveMode.Overwrite).saveAsTable(dataSource.getDbname().concat(".").concat(d.getName()));
			} 
		}catch(Exception e){
			e.printStackTrace();
		}				
	}
}
