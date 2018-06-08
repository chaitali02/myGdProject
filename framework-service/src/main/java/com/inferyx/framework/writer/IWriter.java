package com.inferyx.framework.writer;

import java.io.IOException;

import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ResultSetHolder;

public interface IWriter {
	
	public void write(ResultSetHolder rsHolder, String filePathUrl, Datapod datapod, String saveMode) throws IOException;
}
