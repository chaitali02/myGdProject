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
package com.inferyx.framework.operator;

import java.util.HashMap;
import java.util.List;
import java.util.Set;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.FilterInfo;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Recon;
import com.inferyx.framework.domain.ReconExec;
import com.inferyx.framework.enums.FunctionCategory;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.MessageStatus;

/**
 * @author Ganesh
 *
 */
@Component
public class ReconOperator {
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	protected FunctionOperator functionOperator;
	@Autowired
	private DatasetOperator datasetOperator;
	@Autowired
	FilterOperator2 filterOperator2;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;

	static final Logger LOGGER = Logger.getLogger(ReconOperator.class);

	/**
	 * @Ganesh
	 *
	 * @return the runMode
	 */
	public RunMode getRunMode() {
		return runMode;
	}

	/**
	 * @Ganesh
	 *
	 * @param runMode
	 *            the runMode to set
	 */
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}
	

	Datapod datapod;
	RunMode runMode;

	private String CASE_WHEN = " CASE WHEN ";
	private String THEN = " THEN 'PASS' ELSE 'FAIL' END AS ";
	private String SELECT = " SELECT ";
	private String FROM = " FROM ";
	private String BRACKET_OPEN = "( ";
	private String BRACKET_CLOSE = " ) ";
//	private String GROUP_BY = " GROUP BY ";
	private String COMMA = ", ";
	private String BLANK = " ";
	private String SINGLE_QUOTE = "\'";
	private String WHERE_1_1 = " WHERE (1=1)";
	private String CROSS_JOIN = " CROSS JOIN ";
	
	private String SOURCE_UUID_ALIAS = "";
	private String SOURCE_VERSION_ALIAS = "";
	private String SOURCE_NAME_ALIAS = "";
	
	private String TARGET_UUID_ALIAS = "";
	private String TARGET_VERSION_ALIAS = "";
	private String TARGET_NAME_ALIAS = "";
	
	private String SOURCE_UUID = null;
	private String SOURCE_VERSION = null;
	private String SOURCE_NAME = null;
	private String SOURCE_ATTR_NAME = null;
	
	private String TARGET_UUID = null;
	private String TARGET_VERSION = null;
	private String TARGET_NAME = null;
	private String TARGET_ATTR_NAME = null;

	public String generateSql(Recon recon, ReconExec reconExec, List<String> datapodList, DagExec dagExec,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
		String sql = "";
		try {			
			String sourceDistinct = recon.getSourceDistinct();
			String targetDistinct = recon.getTargetDistinct();
			
//			Object sourceObj = daoRegister.getRefObject(recon.getSourceAttr().getRef());
//			Object targetObj = daoRegister.getRefObject(recon.getTargetAttr().getRef());
			Object sourceObj = commonServiceImpl.getOneByUuidAndVersion(recon.getSourceAttr().getRef().getUuid(), recon.getSourceAttr().getRef().getVersion(), recon.getSourceAttr().getRef().getType().toString(), "N");
			Object targetObj = commonServiceImpl.getOneByUuidAndVersion(recon.getTargetAttr().getRef().getUuid(), recon.getTargetAttr().getRef().getVersion(), recon.getTargetAttr().getRef().getType().toString(), "N");
			
			resolveSource(sourceObj, recon);
			resolveTarget(targetObj, recon);
			
//			Function sourceFun = (Function) daoRegister.getRefObject(recon.getSourceFunc().getRef());
//			Function targetFun = (Function) daoRegister.getRefObject(recon.getTargetFunc().getRef());
			Function sourceFun = (Function) commonServiceImpl.getOneByUuidAndVersion(recon.getSourceFunc().getRef().getUuid(), recon.getSourceFunc().getRef().getVersion(), recon.getSourceFunc().getRef().getType().toString(), "N");
			Function targetFun = (Function) commonServiceImpl.getOneByUuidAndVersion(recon.getTargetFunc().getRef().getUuid(), recon.getTargetFunc().getRef().getVersion(), recon.getTargetFunc().getRef().getType().toString(), "N");
			
			
			String sourceVal = generateVal(sourceFun, SOURCE_ATTR_NAME, sourceDistinct, recon);
			String targetVal = generateVal(targetFun, TARGET_ATTR_NAME, targetDistinct, recon);
			
			sql = SELECT 
			      + SOURCE_UUID_ALIAS.toLowerCase() + " AS " + SOURCE_UUID_ALIAS + COMMA 
			      + SOURCE_VERSION_ALIAS.toLowerCase() + " AS " + SOURCE_VERSION_ALIAS + COMMA
			      + SOURCE_NAME_ALIAS.toLowerCase() + " AS " + SOURCE_NAME_ALIAS + COMMA 
			      + "sourceattributeid" + " AS sourceAttributeId" + COMMA
			      + "sourceattributename" + " AS sourceAttributeName" + COMMA
			      + "sourcevalue" + " AS sourceValue" + COMMA
			      
			      + TARGET_UUID_ALIAS.toLowerCase() + " AS " + TARGET_UUID_ALIAS + COMMA 
			      + TARGET_VERSION_ALIAS.toLowerCase() + " AS " + TARGET_VERSION_ALIAS + COMMA
			      + TARGET_NAME_ALIAS.toLowerCase() + " AS " + TARGET_NAME_ALIAS + COMMA 
			      + "targetattributeid" + " AS targetAttributeId" + COMMA
			      + "targetattributename" + " AS targetAttributeName" + COMMA		      
			      + "targetvalue" + " AS targetValue" + COMMA
			      
			      + caseWrapper(generateCheck(), " status") + COMMA
			      + SINGLE_QUOTE + reconExec.getVersion() + SINGLE_QUOTE + " AS version"
			      
			      + FROM
//			      + BRACKET_OPEN
			      
			      + BRACKET_OPEN
			      + SELECT
			      + SINGLE_QUOTE + SOURCE_UUID  + SINGLE_QUOTE + " AS " + SOURCE_UUID_ALIAS + COMMA 
			      + SINGLE_QUOTE + SOURCE_VERSION  + SINGLE_QUOTE + " AS " + SOURCE_VERSION_ALIAS + COMMA
			      + SINGLE_QUOTE + SOURCE_NAME + SINGLE_QUOTE + " AS " + SOURCE_NAME_ALIAS + COMMA 
			      + SINGLE_QUOTE + recon.getSourceAttr().getAttrId() + SINGLE_QUOTE + " AS sourceAttributeId" + COMMA
			      + SINGLE_QUOTE + SOURCE_ATTR_NAME + SINGLE_QUOTE + " AS sourceAttributeName" + COMMA
			      + sourceVal + " AS sourceValue"
			      + FROM
			      + generateFrom(sourceObj, datapodList, dagExec, reconExec.getExecParams(), refKeyMap, otherParams, usedRefKeySet, runMode)//getTableName(sourceDp, datapodList, dagExec, otherParams, runMode) 
			      + BLANK
			      + " source "
			      + WHERE_1_1 
			      + generateFilter("source", recon,sourceObj, recon.getSourceFilter(), refKeyMap, otherParams, usedRefKeySet, reconExec.getExecParams(), runMode)
//			      + GROUP_BY
//			      + SOURCE_UUID_ALIAS + COMMA
//			      + SOURCE_VERSION_ALIAS + COMMA
//			      + SOURCE_NAME_ALIAS + COMMA
//			      + "sourceAttributeId" + COMMA
//			      + "sourceAttributeName"		      
			      + BRACKET_CLOSE
			      + "source2"

			      + CROSS_JOIN 
			      
			      + BRACKET_OPEN
			      + SELECT
			      + SINGLE_QUOTE + TARGET_UUID  + SINGLE_QUOTE + " AS " + TARGET_UUID_ALIAS + COMMA 
			      + SINGLE_QUOTE + TARGET_VERSION  + SINGLE_QUOTE + " AS " + TARGET_VERSION_ALIAS + COMMA
			      + SINGLE_QUOTE + TARGET_NAME + SINGLE_QUOTE + " AS " + TARGET_NAME_ALIAS + COMMA 
			      + SINGLE_QUOTE + recon.getTargetAttr().getAttrId() + SINGLE_QUOTE + " AS targetAttributeId" + COMMA
			      + SINGLE_QUOTE + TARGET_ATTR_NAME + SINGLE_QUOTE + " AS targetAttributeName" + COMMA		      
			      + targetVal + " AS targetValue" 
			      + FROM
			      + generateFrom(targetObj, datapodList, dagExec, null, refKeyMap, otherParams, usedRefKeySet, runMode)//getTableName(targetDp, datapodList, dagExec, otherParams, runMode)
			      + BLANK
			      + " target "
			      + WHERE_1_1 
			      + generateFilter("target",recon, targetObj, recon.getTargetFilter(), refKeyMap, otherParams, usedRefKeySet, reconExec.getExecParams(), null)
//			      + GROUP_BY
//			      + TARGET_UUID_ALIAS + COMMA
//			      + TARGET_VERSION_ALIAS + COMMA
//			      + TARGET_NAME_ALIAS + COMMA
//			      + "targetAttributeId" + COMMA
//			      + "targetAttributeName"
			      + BRACKET_CLOSE
			      + " target2";
			      
//			      + BRACKET_CLOSE;
		} catch (NullPointerException e) {
			e.printStackTrace();
			LOGGER.error(e);
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}
			
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.reconExec, reconExec.getUuid(), reconExec.getVersion()));
			commonServiceImpl.sendResponse("404", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not generate query.", dependsOn);
			throw new NullPointerException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e);
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.reconExec, reconExec.getUuid(), reconExec.getVersion()));
			commonServiceImpl.sendResponse("404", MessageStatus.FAIL.toString(), (message != null) ? message : "Can not generate query.", dependsOn);
			throw new RuntimeException((message != null) ? message : "Can not generate query.");
		}
		
		LOGGER.info("Recon sql: "+sql);
		return sql;
	}
	
	public void resolveSource(Object sourceObj, Recon recon) {
		if(sourceObj instanceof DataSet) {
			DataSet sourceDs = (DataSet) sourceObj;
			SOURCE_UUID = sourceDs.getUuid();
			SOURCE_VERSION = sourceDs.getVersion();
			SOURCE_NAME = sourceDs.getName();
			SOURCE_UUID_ALIAS = "sourceUuid";
			SOURCE_VERSION_ALIAS = "sourceVersion";
			SOURCE_NAME_ALIAS = "sourceName";
			for(AttributeSource attributeSource : sourceDs.getAttributeInfo()) {
				if(attributeSource.getAttrSourceId().equalsIgnoreCase(recon.getSourceAttr().getAttrId())) {
					SOURCE_ATTR_NAME = attributeSource.getAttrSourceName();
					break;
				} else {
					SOURCE_ATTR_NAME = "";
				}
			}
		} else if(sourceObj instanceof Datapod) {
			Datapod sourceDp = (Datapod) sourceObj;
			SOURCE_UUID = sourceDp.getUuid();
			SOURCE_VERSION = sourceDp.getVersion();
			SOURCE_NAME = sourceDp.getName();
			SOURCE_UUID_ALIAS = "sourceUuid";
			SOURCE_VERSION_ALIAS = "sourceVersion";
			SOURCE_NAME_ALIAS = "sourceName";
			for(Attribute attribute : sourceDp.getAttributes()) {
				if(attribute.getAttributeId().equals(Integer.parseInt(recon.getSourceAttr().getAttrId()))) {
					SOURCE_ATTR_NAME = attribute.getName();
					break;
				} else {
					SOURCE_ATTR_NAME = "";
				}
			}
		}	
	}
	
	public void resolveTarget(Object targetObj, Recon recon) {
		if(targetObj instanceof DataSet) {
			DataSet targetDs = (DataSet) targetObj;
			TARGET_UUID = targetDs.getUuid();
			TARGET_VERSION = targetDs.getVersion();
			TARGET_NAME = targetDs.getName();
			TARGET_UUID_ALIAS = "targetUuid";
			TARGET_VERSION_ALIAS = "targetVersion";
			TARGET_NAME_ALIAS = "targetName";
			for(AttributeSource attributeSource : targetDs.getAttributeInfo()) {
				if(attributeSource.getAttrSourceId().equalsIgnoreCase(recon.getTargetAttr().getAttrId())) {
					TARGET_ATTR_NAME = attributeSource.getAttrSourceName();
					break;
				} else {
					TARGET_ATTR_NAME = "";
				}
			}
		} else if(targetObj instanceof Datapod) {
			Datapod targetDp = (Datapod) targetObj;
			TARGET_UUID = targetDp.getUuid();
			TARGET_VERSION = targetDp.getVersion();
			TARGET_NAME = targetDp.getName();
			TARGET_UUID_ALIAS = "targetUuid";
			TARGET_VERSION_ALIAS = "targetVersion";
			TARGET_NAME_ALIAS = "targetName";
			for(Attribute attribute : targetDp.getAttributes()) {
				if(attribute.getAttributeId().equals(Integer.parseInt(recon.getTargetAttr().getAttrId()))) {
					TARGET_ATTR_NAME = attribute.getName();
					break;
				} else {
					TARGET_ATTR_NAME = "";
				}
			}
		}	
	}
	
	public String generateFrom(Object obj, List<String> datapodList, DagExec dagExec, ExecParams execParams, java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, 
			Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
		if(obj instanceof DataSet) {
			return "( " + datasetOperator.generateSql((DataSet)obj, refKeyMap, otherParams, usedRefKeySet, execParams, runMode) +" )";
		} else if(obj instanceof Datapod) {
//			return getTableName((Datapod)obj, datapodList, dagExec, otherParams, runMode);
			return datapodServiceImpl.genTableNameByDatapod((Datapod)obj, dagExec != null ? dagExec.getVersion(): null, datapodList, otherParams, dagExec, runMode, true);
		}
		return null;
	}


	public String generateFilter(String tableName, Recon recon, Object object, List<FilterInfo> filterAttrRefHolder,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, ExecParams execParams, RunMode runMode)
			throws Exception {
		String objectName = null;
		if(object instanceof Datapod) {				
			Datapod datapod = (Datapod) object;
			objectName = datapod.getName();
		}
		else if(object instanceof DataSet ) {
			DataSet dataset = (DataSet) object;
			objectName = dataset.getName();
		}
		if (filterAttrRefHolder != null && !filterAttrRefHolder.isEmpty()) {
			MetaIdentifierHolder filterSource = new MetaIdentifierHolder(new MetaIdentifier(MetaType.recon, recon.getUuid(), recon.getVersion()));
			Datasource mapSourceDS =  commonServiceImpl.getDatasourceByObject(recon);
			String filter = filterOperator2.generateSql(filterAttrRefHolder, refKeyMap, filterSource, otherParams, usedRefKeySet, execParams, false, false, runMode, mapSourceDS);

			//String filter = filterOperator.generateSql(filterAttrRefHolder, refKeyMap, otherParams, usedRefKeySet, execParams, false, false, runMode);
			if(filter.contains(objectName))
				filter = filter.replace(objectName+".", tableName+".");
			return filter;
		}
		return BLANK;
	}
	
	public String generateCheck() {
		return "sourceValue" + "=" + "targetValue";
	}
	
	public String generateVal(Function function, String attrName, String distinctFlg, Recon recon) throws Exception {
		StringBuilder val = new StringBuilder();
		Datasource datasource = commonServiceImpl.getDatasourceByObject(recon); 
		if(function.getCategory().equalsIgnoreCase(FunctionCategory.AGGREGATE.toString())) {
			val
			.append(functionOperator.generateSql(function, null, null, datasource))
			.append(BRACKET_OPEN)
			.append(distinctFlg != null && distinctFlg.equalsIgnoreCase("Y") ? " DISTINCT " : "")
			.append(attrName)
			.append(BRACKET_CLOSE);
		}else
			throw new Exception("Wrong function type.");
		return val.toString();
	}

	private String caseWrapper(String check, String colName) {
		StringBuilder caseBuilder = new StringBuilder(CASE_WHEN).append(check).append(THEN).append(colName)
				.append(BLANK);
		return caseBuilder.toString();
	}	
}