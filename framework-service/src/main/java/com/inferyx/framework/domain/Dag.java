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

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;



@Document(collection="dag")
public class Dag extends BaseEntity{

    private Double xPos;
    private Double yPos;
    private String templateFlg = "N";
    private MetaIdentifierHolder templateInfo;
    private List<Stage> stages = new ArrayList<Stage>();
    private List<Status> statusList = new ArrayList<>();
    private MetaIdentifierHolder paramList;
    private SenderInfo senderInfo;
    
    public Dag() {}
    
    public Dag(Dag dag) {
    	setBaseEntity();
    	xPos = dag.getxPos();
    	yPos = dag.getyPos();
    	templateFlg = dag.getTemplateFlg();
    	templateInfo = dag.getTemplateInfo();
    	stages = dag.getStages();
    	statusList.addAll(dag.getStatusList());
    	paramList = dag.getParamList();
    	senderInfo = dag.getSenderInfo();
    }
    
	/**
	 * @Ganesh
	 *
	 * @return the senderInfo
	 */
	public SenderInfo getSenderInfo() {
		return senderInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @param senderInfo the senderInfo to set
	 */
	public void setSenderInfo(SenderInfo senderInfo) {
		this.senderInfo = senderInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @return the templateFlg
	 */
	public String getTemplateFlg() {
		return templateFlg;
	}

	/**
	 * @Ganesh
	 *
	 * @param templateFlg the templateFlg to set
	 */
	public void setTemplateFlg(String templateFlg) {
		this.templateFlg = templateFlg;
	}

	/**
	 * @Ganesh
	 *
	 * @return the templateInfo
	 */
	public MetaIdentifierHolder getTemplateInfo() {
		return templateInfo;
	}

	/**
	 * @Ganesh
	 *
	 * @param templateInfo the templateInfo to set
	 */
	public void setTemplateInfo(MetaIdentifierHolder templateInfo) {
		this.templateInfo = templateInfo;
	}

	public Double getxPos() {
		return xPos;
	}

	public void setxPos(Double xPos) {
		this.xPos = xPos;
	}

	public Double getyPos() {
		return yPos;
	}

	public void setyPos(Double yPos) {
		this.yPos = yPos;
	}

	/**
	 * 
	 * @return The stages
	 */
	public List<Stage> getStages() {
		return stages;
	}

	/**
	 * 
	 * @param stages
	 *            The stages
	 */
	public void setStages(List<Stage> stages) {
		this.stages = stages;
	}

	public List<Status> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}
    
	public MetaIdentifierHolder getParamList() {
		return paramList;
	}
	public void setParamList(MetaIdentifierHolder paramList) {
		this.paramList = paramList;
	}
	
}