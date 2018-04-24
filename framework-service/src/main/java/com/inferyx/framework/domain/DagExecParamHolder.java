/**
 * 
 */
package com.inferyx.framework.domain;

import java.util.Map;

/**
 * @author joy
 *
 */
public class DagExecParamHolder {
	
	private ExecParams dagExecParams;
	private Map<String, ExecParams> taskExecParams;

	/**
	 * 
	 */
	public DagExecParamHolder() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the dagExecParams
	 */
	public ExecParams getDagExecParams() {
		return dagExecParams;
	}

	/**
	 * @param dagExecParams the dagExecParams to set
	 */
	public void setDagExecParams(ExecParams dagExecParams) {
		this.dagExecParams = dagExecParams;
	}

	/**
	 * @return the taskExecParams
	 */
	public Map<String, ExecParams> getTaskExecParams() {
		return taskExecParams;
	}

	/**
	 * @param taskExecParams the taskExecParams to set
	 */
	public void setTaskExecParams(Map<String, ExecParams> taskExecParams) {
		this.taskExecParams = taskExecParams;
	}
	
}
