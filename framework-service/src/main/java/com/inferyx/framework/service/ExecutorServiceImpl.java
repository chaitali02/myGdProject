package com.inferyx.framework.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;

@Service
public class ExecutorServiceImpl {
	
	@Autowired
	private Engine engine;
	@Autowired
	private Helper helper;
	@Autowired
	private CommonServiceImpl commonServiceImpl;

	public ExecutorServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Send datasource by app here
	 * @param runMode
	 * @param datasource
	 * @return
	 */
	public ExecContext getExecContext(RunMode runMode, Datasource datasource) {

//		ExecContext execContext = null;
//		if (runMode.equals(RunMode.ONLINE)) {
//			execContext = (engine.getExecEngine().equalsIgnoreCase("livy-spark")
//						|| engine.getExecEngine().equalsIgnoreCase("spark"))
//							? helper.getExecutorContext(engine.getExecEngine())
//							: helper.getExecutorContext(ExecContext.spark.toString());
//		} else {
//			execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
//		}
		return helper.getExecutorContext(datasource.getType().toLowerCase());
	}

}
