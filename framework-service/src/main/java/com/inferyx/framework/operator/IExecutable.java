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
	 * @return 
	 * @throws Exception 
	 */
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception;

}
