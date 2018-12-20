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
package com.inferyx.framework.domain;

/**
 * @author Ganesh
 *
 */
public class TrainExecView extends BaseExec {
	DeployExec deployExec;
	TrainResultView trainResultView;
	
	/**
	 * @Ganesh 
	 *
	 * @return the deployExec
	 */
	public DeployExec getDeployExec() {
		return deployExec;
	}
	/**
	 * @Ganesh 
	 *
	 * @param deployExec the deployExec to set
	 */
	public void setDeployExec(DeployExec deployExec) {
		this.deployExec = deployExec;
	}
	/**
	 * @Ganesh 
	 *
	 * @return the trainResultView
	 */
	public TrainResultView getTrainResultView() {
		return trainResultView;
	}
	/**
	 * @Ganesh 
	 *
	 * @param trainResultView the trainResultView to set
	 */
	public void setTrainResultView(TrainResultView trainResultView) {
		this.trainResultView = trainResultView;
	}	
}
