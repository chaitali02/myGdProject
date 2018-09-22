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
package com.inferyx.framework.connector;

import java.io.IOException;

import com.inferyx.framework.domain.Datasource;

public interface IConnector {

	ConnectionHolder getConnection() throws IOException;
	ConnectionHolder getConnection(Object input, Object input2) throws IOException;
	ConnectionHolder getConnectionByDatasource(Datasource datasource) throws IOException;
}
