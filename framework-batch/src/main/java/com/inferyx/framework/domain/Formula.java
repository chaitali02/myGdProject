/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection="formula")
public class Formula extends BaseEntity{
	
	//@Autowired private MetadataUtil commonActivity;
	//private String name;
	//private String desc;
	private MetaIdentifierHolder dependsOn;
	private List<SourceAttr> formulaInfo = new ArrayList<>();
	private FormulaType formulaType;

	/*public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}*/
	
	public List<SourceAttr> getFormulaInfo() {
		return formulaInfo;
	}
	
	public void setFormulaInfo(List<SourceAttr> formula) {
		
		this.formulaInfo = formula;
	}
	
	public MetaIdentifierHolder getDependsOn() {
		return dependsOn;
	}
	
	public void setDependsOn(MetaIdentifierHolder dependsOn) {
		this.dependsOn = dependsOn;
	}

	public FormulaType getFormulaType() {
		return formulaType;
	}

	public void setFormulaType(FormulaType formulaType) {
		this.formulaType = formulaType;
	}
	
	/*public String sql(MetaIdentifierUtil commonActivity) {
		StringBuilder builder = new StringBuilder();
		for(SourceAttr sourceAttr : getFormulaInfo()) {
			if(sourceAttr.getRef().getType() == MetaType.simple) {
				builder.append(sourceAttr.getValue());
			}
			if(sourceAttr.getRef().getType() == MetaType.datapod) {
				// Bhanu - JIRA FW-3 - Remove cache
				// Datapod datapod = (Datapod) sourceAttr.getRef().getCollectionObject(loader);
				Datapod datapod = (Datapod) commonActivity.getRefObject(sourceAttr.getRef());
				// End JIRA FW-3 Changes
				builder.append(datapod.sql(sourceAttr.getAttributeId()));
			}
		}
		
		System.out.println(String.format("Generalize formula %s", builder.toString()));
		return builder.toString();
	}*/
	

}