package com.inferyx.framework.writer;

import java.io.IOException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.inferyx.framework.domain.Datapod;

public interface IWriter {
	
	public void write(Dataset<Row> df, String filePathUrl, Datapod datapod, String saveMode) throws IOException;
}
