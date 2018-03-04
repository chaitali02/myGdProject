package com.inferyx.framework.factory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.Engine;
import com.inferyx.framework.executor.HiveExecutor;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.ImpalaExecutor;
import com.inferyx.framework.executor.LivyExecutor;
import com.inferyx.framework.executor.MySqlExecutor;
import com.inferyx.framework.executor.OracleExecutor;
import com.inferyx.framework.executor.PythonExecutor;
import com.inferyx.framework.executor.RExecutor;
import com.inferyx.framework.executor.SparkExecutor;

@Component
public class ExecutorFactory {
	
	@Autowired
	SparkExecutor sparkExec;
	@Autowired
	HiveExecutor hiveExec;
	@Autowired
	ImpalaExecutor impalaExec;
	@Autowired
	OracleExecutor oracleExecutor;
	@Autowired
	MySqlExecutor mySqlExecutor;
	@Autowired
	LivyExecutor livyExecutor;
	@Autowired
	Engine engine;
	@Autowired
	RExecutor rExecutor;
	@Autowired
	PythonExecutor pythonExecutor;
	
	public IExecutor getExecutor(String context) {
		String executionEngine = engine.getExecEngine();
		if(executionEngine != null && !StringUtils.isBlank(executionEngine) && executionEngine.equalsIgnoreCase("livy-spark"))
			executionEngine = "livy_spark";
		switch(context.toLowerCase())
		{
		case "spark":	return sparkExec;
		case "livy_spark":	return livyExecutor;
		case "hive": return hiveExec;
		case "impala": return impalaExec;
		case "oracle": return oracleExecutor;
		case "mysql": return mySqlExecutor;
		case "file":	return (executionEngine != null && executionEngine == "livy_spark") ? livyExecutor : sparkExec;
		case "r" : return rExecutor;
		case "python" : return pythonExecutor;
		default:				
		}
		return null;
	}
}
