package com.inferyx.framework.writer;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.inferyx.framework.domain.Datapod;

public interface IWriter {
	
	public void write(Dataset<Row> df, String filePathUrl, Datapod d, String saveMode);
}
