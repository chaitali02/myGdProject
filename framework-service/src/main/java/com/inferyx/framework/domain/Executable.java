package com.inferyx.framework.domain;

import com.inferyx.framework.enums.RunMode;

/**
 * 
 * @author joy
 *
 */
public interface Executable {
	
	/**
	 * 
	 * @param baseExec
	 * @param execParams
	 * @param runMode
	 * @throws Exception 
	 */
	public void execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception;

}
