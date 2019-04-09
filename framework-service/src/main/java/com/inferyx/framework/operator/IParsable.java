package com.inferyx.framework.operator;

import java.util.Map;

import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.enums.RunMode;

public interface IParsable {
	
	/**
	 * 
	 * @param baseExec
	 * @param execParams
	 * @param runMode
	 * @return
	 * @throws Exception 
	 */
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception;

}
