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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Recon;
import com.inferyx.framework.domain.ReconExec;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.enums.FunctionCategory;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.MessageServiceImpl;
import com.inferyx.framework.service.MessageStatus;

/**
 * @author Ganesh
 *
 */
@Component
public class ReconOperator {
	@Autowired
	private MetadataUtil daoRegister;
	@Autowired
	private DataStoreServiceImpl datastoreServiceImpl;
	@Autowired
	private CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	protected FunctionOperator functionOperator;
	@Autowired
	FilterOperator filterOperator;
	@Autowired
	private MessageServiceImpl messageServiceImpl;

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
	private String GROUP_BY = " GROUP BY ";
	private String COMMA = ", ";
	private String BLANK = " ";
	private String SINGLE_QUOTE = "\'";
	private String WHERE_1_1 = " WHERE (1=1)";
	private String CROSS_JOIN = " CROSS JOIN ";

	public String generateSql(Recon recon, ReconExec reconExec, List<String> datapodList, DagExec dagExec,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
		String sql = "";
		try {
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			String datasourceName = datasource.getType();
			if (runMode.equals(RunMode.ONLINE)) {
				if (datasourceName.equalsIgnoreCase(ExecContext.MYSQL.toString())) {
					datasourceName = ExecContext.MYSQL.toString();
				} else if (datasourceName.equalsIgnoreCase(ExecContext.ORACLE.toString())) {
					datasourceName = ExecContext.ORACLE.toString();
				} else {
					datasourceName = ExecContext.spark.toString();
				}
			}
			
			/*Object sourceObj = daoRegister.getRefObject(recon.getSourceAttr().getRef());
			Object targetObj = daoRegister.getRefObject(recon.getTargetAttr().getRef());
			
			Datapod sourceDp = getDatapod(sourceObj, recon, "source");
			Datapod targetDp = getDatapod(targetObj, recon, "target");*/
			
			Datapod sourceDp = (Datapod) daoRegister.getRefObject(recon.getSourceAttr().getRef());
			Datapod targetDp = (Datapod) daoRegister.getRefObject(recon.getTargetAttr().getRef());
			
			String sourceAttrName = sourceDp.getAttributeName(Integer.parseInt(recon.getSourceAttr().getAttrId()));
			String targetAttrName = targetDp.getAttributeName(Integer.parseInt(recon.getTargetAttr().getAttrId()));
			
			Function sourceFun = (Function) daoRegister.getRefObject(recon.getSourceFunc().getRef());
			Function targetFun = (Function) daoRegister.getRefObject(recon.getTargetFunc().getRef());
			
			String sourceVal = generateVal(sourceFun, sourceAttrName);
			String targetVal = generateVal(targetFun, targetAttrName);
			
			sql = SELECT 
			      + "sourcedatapodid" + " AS sourceDatapodId" + COMMA 
			      + "sourcedatapodversion" + " AS sourceDatapodVersion" + COMMA
			      + "sourcedatapodname" + " AS sourceDatapodName" + COMMA 
			      + "sourceattrid" + " AS sourceAttrId" + COMMA
			      + "sourceattrname" + " AS sourceAttrName" + COMMA
			      + "sourceval" + " AS sourceVal" + COMMA
			      
			      + "targetdatapodid" + " AS targetDatapodId" + COMMA 
			      + "targetdatapodversion" + " AS targetDatapodVersion" + COMMA
			      + "targetdatapodname" + " AS targetDatapodName" + COMMA 
			      + "targetattrid" + " AS targetAttrId" + COMMA
			      + "targetattrname" + " AS targetAttrName" + COMMA		      
			      + "targetval" + " AS targetVal" + COMMA
			      
			      + caseWrapper(generateCheck(sourceVal, targetVal), " status") + COMMA
			      + SINGLE_QUOTE + reconExec.getVersion() + SINGLE_QUOTE + " AS version"
			      
			      + FROM
			      + BRACKET_OPEN
			      
			      + BRACKET_OPEN
			      + SELECT
			      + SINGLE_QUOTE + sourceDp.getUuid()  + SINGLE_QUOTE + " AS sourceDatapodId" + COMMA 
			      + SINGLE_QUOTE + sourceDp.getVersion()  + SINGLE_QUOTE + " AS sourceDatapodVersion" + COMMA
			      + SINGLE_QUOTE + sourceDp.getName() + SINGLE_QUOTE + " AS sourceDatapodName" + COMMA 
			      + SINGLE_QUOTE + recon.getSourceAttr().getAttrId() + SINGLE_QUOTE + " AS sourceAttrId" + COMMA
			      + SINGLE_QUOTE + sourceAttrName + SINGLE_QUOTE + " AS sourceAttrName" + COMMA
			      + sourceVal + " AS sourceVal"
			      + FROM
			      + getTableName(sourceDp, datapodList, dagExec, runMode) 
			      + BLANK
			      + " source "
			      + WHERE_1_1 
			      + generateFilter("source", sourceDp, recon.getSourceFilter(), refKeyMap, otherParams, usedRefKeySet, reconExec.getExecParams())
			      + GROUP_BY
			      + "sourceDatapodId" + COMMA
			      + "sourceDatapodVersion" + COMMA
			      + "sourceDatapodName" + COMMA
			      + "sourceAttrId" + COMMA
			      + "sourceAttrName"		      
			      + BRACKET_CLOSE

			      + CROSS_JOIN 
			      
			      + BRACKET_OPEN
			      + SELECT
			      + SINGLE_QUOTE + targetDp.getUuid()  + SINGLE_QUOTE + " AS targetDatapodId" + COMMA 
			      + SINGLE_QUOTE + sourceDp.getVersion()  + SINGLE_QUOTE + " AS targetDatapodVersion" + COMMA
			      + SINGLE_QUOTE + targetDp.getName() + SINGLE_QUOTE + " AS targetDatapodName" + COMMA 
			      + SINGLE_QUOTE + recon.getTargetAttr().getAttrId() + SINGLE_QUOTE + " AS targetAttrId" + COMMA
			      + SINGLE_QUOTE + targetAttrName + SINGLE_QUOTE + " AS targetAttrName" + COMMA		      
			      + targetVal + " AS targetVal" 
			      + FROM
			      + getTableName(targetDp, datapodList, dagExec, runMode)
			      + BLANK
			      + " target "
			      + WHERE_1_1 
			      + generateFilter("target", targetDp, recon.getTargetFilter(), refKeyMap, otherParams, usedRefKeySet, reconExec.getExecParams())
			      + GROUP_BY
			      + "targetDatapodId" + COMMA
			      + "targetDatapodVersion" + COMMA
			      + "targetDatapodName" + COMMA
			      + "targetAttrId" + COMMA
			      + "targetAttrName"
			      + BRACKET_CLOSE
			      
			      + BRACKET_CLOSE
			      + " reconTab ";
		} catch (NullPointerException e) {
			e.printStackTrace();			e.getMessage();
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			if (requestAttributes != null) {
				HttpServletResponse response = requestAttributes.getResponse();
				if (response != null) {
					response.setContentType("application/json");
					Message message = new Message("404", MessageStatus.FAIL.toString(), e.getMessage());
					Message savedMessage = messageServiceImpl.save(message);
					ObjectMapper mapper = new ObjectMapper();
					String messageJson = mapper.writeValueAsString(savedMessage);
					response.setContentType("application/json");
					response.setStatus(404);
					response.getOutputStream().write(messageJson.getBytes());
					response.getOutputStream().close();
				} else
					LOGGER.info("HttpServletResponse response is \"" + null + "\"");
			} else
				LOGGER.info("ServletRequestAttributes requestAttributes is \"" + null + "\"");
			throw new NullPointerException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			if (requestAttributes != null) {
				HttpServletResponse response = requestAttributes.getResponse();
				if (response != null) {
					response.setContentType("application/json");
					Message message = new Message("404", MessageStatus.FAIL.toString(), e.getMessage());
					Message savedMessage = messageServiceImpl.save(message);
					ObjectMapper mapper = new ObjectMapper();
					String messageJson = mapper.writeValueAsString(savedMessage);
					response.setContentType("application/json");
					response.setStatus(404);
					response.getOutputStream().write(messageJson.getBytes());
					response.getOutputStream().close();
				} else
					LOGGER.info("HttpServletResponse response is \"" + null + "\"");
			} else
				LOGGER.info("ServletRequestAttributes requestAttributes is \"" + null + "\"");
			throw new Exception(e.getMessage());
		}
		
		LOGGER.info("Recon sql: "+sql);
		return sql;
	}
	
	/********************** UNUSED **********************/
	/*public Datapod getDatapod(Object object, Recon recon, String attrType) throws JsonProcessingException {
		Datapod datapod = null;
		if(object instanceof Relation) {
			Relation relation = (Relation) object;
			datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(), relation.getDependsOn().getRef().getType().toString());
		} else if(object instanceof DataSet) {
			DataSet dataset = (DataSet) object;
			List<AttributeSource> attributeInfo = dataset.getAttributeInfo();
			
			for(AttributeSource attributeSource : attributeInfo) {
				if(attrType.equalsIgnoreCase("source") && attributeSource.getAttrSourceId().equalsIgnoreCase(recon.getSourceAttr().getAttrId())) {
					datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(attributeSource.getSourceAttr().getRef().getUuid(), attributeSource.getSourceAttr().getRef().getVersion(), attributeSource.getSourceAttr().getRef().getType().toString());
					break;
				} else if(attrType.equalsIgnoreCase("target") && attributeSource.getAttrSourceId().equalsIgnoreCase(recon.getTargetAttr().getAttrId())) {
					datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(attributeSource.getSourceAttr().getRef().getUuid(), attributeSource.getSourceAttr().getRef().getVersion(), attributeSource.getSourceAttr().getRef().getType().toString());
					break;
				}				
			}
		} else if(object instanceof Rule) {
			Rule rule = (Rule) object;
		} else if(object instanceof Datapod) {
			datapod = (Datapod) object;
		}
		return datapod;
	}*/

	public String generateFilter(String tableName, Datapod datapod, List<AttributeRefHolder> filterAttrRefHolder,
			java.util.Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, ExecParams execParams)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if (filterAttrRefHolder != null && !filterAttrRefHolder.isEmpty()) {
			String result = filterOperator.generateSql(filterAttrRefHolder, refKeyMap, otherParams, usedRefKeySet, execParams);
			if(result.contains(datapod.getName()))
				result = result.replace(datapod.getName()+".", tableName+".");
			return result;
		}
		return BLANK;
	}
	
	public String generateCheck(String sourceVal, String targetVal) {
		return "sourceVal" + "==" + "targetVal";
	}
	
	public String generateVal(Function function, String attrName) throws Exception {
		StringBuilder val = new StringBuilder();
			if(function.getCategory().equalsIgnoreCase(FunctionCategory.AGGREGATE.toString())) {
				val
				.append(functionOperator.generateSql(function, null, null))
				.append(BRACKET_OPEN)
				.append(attrName)
				.append(BRACKET_CLOSE);
			}else
				throw new Exception("Wrong function type.");
		return val.toString();
	}
	
	public String getTableName(Datapod datapod, List<String> datapodList, DagExec dagExec, RunMode runMode)
			throws Exception {
		if (datapodList != null && datapodList.contains(datapod.getUuid())) {
			return String.format("%s_%s_%s", datapod.getUuid().replaceAll("-", "_"), datapod.getVersion(),
					dagExec.getVersion());
		}
		datastoreServiceImpl.setRunMode(runMode);
		return datastoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()),
				runMode);
	}
	
	private String caseWrapper(String check, String colName) {
		StringBuilder caseBuilder = new StringBuilder(CASE_WHEN).append(check).append(THEN).append(colName)
				.append(BLANK);
		return caseBuilder.toString();
	}	
}
