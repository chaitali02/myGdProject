package com.inferyx.framework.reader;

import java.io.IOException;

import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.domain.DataFrameHolder;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;

public interface IReader {
	DataFrameHolder read(Datapod dp, DataStore datastore, HDFSInfo hdfsInfo, Object conObject, Datasource ds) throws IOException;
}
