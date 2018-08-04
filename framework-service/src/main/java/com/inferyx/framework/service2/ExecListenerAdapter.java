package com.inferyx.framework.service2;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.Stage;

@Service
public class ExecListenerAdapter {
	
	private ConcurrentHashMap<String, CopyOnWriteArrayList<BaseExec>> baseExecListener = new ConcurrentHashMap<>();
	
	private ConcurrentHashMap<String, DagExec> dagExecListener = new ConcurrentHashMap<>();
	
	/**
	 * @return the baseExecListener
	 */
	public ConcurrentHashMap<String, CopyOnWriteArrayList<BaseExec>> getBaseExecListener() {
		return baseExecListener;
	}

	/**
	 * @param baseExecListener the baseExecListener to set
	 */
	public void setBaseExecListener(ConcurrentHashMap<String, CopyOnWriteArrayList<BaseExec>> baseExecListener) {
		this.baseExecListener = baseExecListener;
	}
	/**
	 * @return the dagExecListener
	 */
	public ConcurrentHashMap<String, DagExec> getDagExecListener() {
		return dagExecListener;
	}

	/**
	 * @param dagExecListener the dagExecListener to set
	 */
	public void setDagExecListener(ConcurrentHashMap<String, DagExec> dagExecListener) {
		this.dagExecListener = dagExecListener;
	}

	public ExecListenerAdapter() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param dagExec
	 */
	public void dagStarted(DagExec dagExec) {
		dagExecListener.put(dagExec.getUuid()+"_"+dagExec.getVersion(), dagExec);
	}
	
	public void dagCompleted(DagExec dagExec) {
		dagExecListener.remove(dagExec.getUuid()+"_"+dagExec.getVersion());
	}
	
	public void dagFailed(DagExec dagExec) {
		dagExecListener.remove(dagExec.getUuid()+"_"+dagExec.getVersion());
		// Find baseExecs and remove them
	}
	
	public void dagTerminated(DagExec dagExec) {
		// Find stageExecs, taskExecs, baseExecs and kill them
		for (Stage stage : dagExec.getStages()) {
			//
		}
	}
	
	public void dagKilled() {
		
	}

	public void dagRestarted() {
		
	}
	
	
}
