package com.inferyx.framework.writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.springframework.data.domain.Sort;

import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;

public class ImpalaWriter implements IWriter {
	Logger logger=Logger.getLogger(ImpalaWriter.class);
	private MetadataUtil daoRegister;
	public MetadataUtil getDaoRegister() {
		return daoRegister;
	}
	public void setDaoRegister(MetadataUtil daoRegister) {
		this.daoRegister = daoRegister;
	}
	@Override
	public void write(Dataset<Row> dataFrame, String filePathUrl, Datapod datapod, String saveMode) throws IOException {
		try{
			Datasource dataSource = daoRegister.getiDatasourceDao().findLatestByUuid(datapod.getDatasource().getRef().getUuid(), new Sort(Sort.Direction.DESC, "version")); 
			//Datasource dataSource =  daoRegister.getiDatasourceDao().findOneByUuidAndVersion(d.getDatasource().getRef().getUuid(),d.getDatasource().getRef().getVersion());
			logger.info("Table Name: "+dataSource.getDbname().concat(".").concat(datapod.getName()));
			List<Attribute> attrList = datapod.getAttributes();		
			List<String> parList = new ArrayList<String>();
			for(Attribute attr : attrList)
				if(attr.getPartition().equalsIgnoreCase("y")) 
					parList.add(attr.getName());	
			if(parList.size() > 0){
				StringBuilder sb = new StringBuilder();
				for(int i=0; i<parList.size(); i++)	{
					sb.append(parList.get(i)).append(",");
				}
				String[] columnList = sb.substring(0, sb.length()-1).toString().split(",");
				logger.info("Partition column list >----->> "+Arrays.asList(columnList));
				//dataFrame.sqlContext().sql("set hive.exec.dynamic.partition.mode="+Helper.getProperty("hive.exec.dynamic.partition.mode"));
				dataFrame.write().mode(SaveMode.Append).partitionBy(columnList).insertInto(dataSource.getDbname().concat(".").concat(datapod.getName()));
			}else {
				if(saveMode.equalsIgnoreCase("append"))	{
					dataFrame.write().mode(SaveMode.Append).saveAsTable(dataSource.getDbname().concat(".").concat(datapod.getName()));
				}else if(saveMode.equalsIgnoreCase("overwrite")) {
					dataFrame.write().mode(SaveMode.Overwrite).saveAsTable(dataSource.getDbname().concat(".").concat(datapod.getName()));
				}
			}
		}/*catch(FileNotFoundException e){
			e.printStackTrace();
		}*/catch(Exception e){
			e.printStackTrace();
		}			 
	}
}
