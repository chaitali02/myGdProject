package com.inferyx.framework.operator;

import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.enums.RunMode;

/**
 * 
 * @author joy
 *
 */
public interface IExecutable {
	
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
