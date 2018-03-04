package com.inferyx.framework.writer;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.Datapod;

@Component
public class ParquetWriter implements IWriter {

	@Override
	public void write(Dataset<Row> df, String filePathUrl, Datapod d, String saveMode) {
		if(saveMode.equalsIgnoreCase("append"))	{
			df.write().mode(SaveMode.Append).parquet(filePathUrl);
		}else if(saveMode.equalsIgnoreCase("overwrite")) {
			df.write().mode(SaveMode.Overwrite).parquet(filePathUrl);
		}
	}
}
