package com.inferyx.framework.domain;

import com.inferyx.framework.enums.RunMode;

public interface Parsable {
	
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
