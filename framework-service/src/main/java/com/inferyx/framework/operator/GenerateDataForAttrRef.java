/**
 * 
 */
package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.DatasetServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

/**
 * @author joy
 */
@Service
public class GenerateDataForAttrRef extends GenerateDataOperator {
	
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	@Autowired
	private DatasetServiceImpl datasetServiceImpl;
	@Autowired
	private Helper helper;
	
	static final Logger logger = Logger.getLogger(GenerateDataForAttrRef.class);

	/**
	 * 
	 */
	public GenerateDataForAttrRef() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		String execVersion = baseExec.getVersion();
		HashMap<String, String> otherParams = execParams.getOtherParams();
		if (otherParams == null) {
			otherParams = new HashMap<String, String>();
			execParams.setOtherParams(otherParams);
		}
		// Set attribute source 
		ParamListHolder attrInfo = paramSetServiceImpl.getParamByName(execParams, "attrRef");
		// Set destination
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		
		MetaIdentifier attrDpIdentifier = attrInfo.getAttributeInfo().get(0).getRef();
		Datapod attrDatapod = null; 
//		DataSet attrDataset = null;
		if (attrDpIdentifier.getType() == MetaType.datapod) {
			attrDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(attrDpIdentifier.getUuid(), attrDpIdentifier.getVersion(), attrDpIdentifier.getType().toString());
			String attrDpTableName = datapodServiceImpl.getTableNameByDatapodKey(new OrderKey(attrDatapod.getUuid(), attrDatapod.getVersion()), runMode);
			otherParams.put("datapodUuid_" + attrDatapod.getUuid() + "_tableName", attrDpTableName);
		} 
		
//		String newVersion = Helper.getVersion();
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, null, null, null, runMode, false);
		otherParams.put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", tableName);
		logger.info(" Filled up otherParams : " + otherParams);	
		return otherParams;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return null;
	}
	
	
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		HashMap<String, String> otherParams = execParams.getOtherParams();
//		String execUuid = baseExec.getUuid();
		String execVersion = baseExec.getVersion();
//		int numRepetitions = 0;
//		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(appDatasource.getType());
		
		ParamListHolder numIterationsInfo = paramSetServiceImpl.getParamByName(execParams, "numIterations");
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		ParamListHolder attrInfo = paramSetServiceImpl.getParamByName(execParams, "attrRef");
		// Set min range
		ParamListHolder minRandInfo = paramSetServiceImpl.getParamByName(execParams, "min");
		// Set max range
		ParamListHolder maxRandInfo = paramSetServiceImpl.getParamByName(execParams, "max");
		
		
		int numIterations = Integer.parseInt(numIterationsInfo.getParamValue().getValue());
		
		int minRand = Integer.parseInt(minRandInfo.getParamValue().getValue());
		int maxRand = Integer.parseInt(maxRandInfo.getParamValue().getValue());
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		String tableName = otherParams.get("datapodUuid_" + locationDatapod.getUuid() + "_tableName");
		Datasource locationDpDatasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(locationDatapod.getDatasource().getRef().getUuid(), 
																								locationDatapod.getDatasource().getRef().getVersion(), 
																								locationDatapod.getDatasource().getRef().getType().toString());
		
		MetaIdentifier attrIdentifier = attrInfo.getAttributeInfo().get(0).getRef();
		Object attrDp = commonServiceImpl.getOneByUuidAndVersion(attrIdentifier.getUuid(), attrIdentifier.getVersion(), attrIdentifier.getType().toString());
		List<String> attrList = getColumnNameList(attrDp, attrInfo);
		// Get the attribute 
		String attributeName = attrList.get(0);
		logger.info("OtherParams : " + otherParams);
		logger.info("attrIdentifier.getUuid : " + attrIdentifier.getUuid());

		String attrTableName = otherParams.get("datapodUuid_" + attrIdentifier.getUuid() + "_tableName");
		String attrTableNameSql = attrTableName;
		// handle dataSet - Start
		DataSet attrDataset = null;
		if (StringUtils.isBlank(attrTableName) && attrIdentifier.getType() == MetaType.dataset) {
			attrDataset = (DataSet)attrDp;
			attrTableNameSql = "(" + datasetServiceImpl.generateSql(attrDataset, DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), otherParams, new HashSet<>(), execParams, runMode, new HashMap<String, String>()) + ") "
						+ attrDataset.getName();
			attrTableName = attrDataset.getName();
		}
		// handle dataSet - End
		if(attrTableName.contains("framework.")) {
			attrTableName = attrTableName.replaceAll("framework.", "");
		}
		List<Attribute> attributeList = locationDatapod.getAttributes();
//		String randFunction = datasource.getType().equalsIgnoreCase(ExecContext.HIVE.toString()) ? "rand()" : "randn()";
//		String rangeSql = "select ranges.iteration_id as "+attributeList.get(0).getDispName()+","+attrTableName+"."+attributeName+" as "+attributeList.get(1).getDispName()
//						+", ("+randFunction+" * ("+maxRand+" - "+minRand+") + "+minRand+") as "+attributeList.get(2).getDispName()+", "+execVersion+" as "+attributeList.get(3).getDispName()
//						+" FROM "
//						+attrTableName+ " CROSS JOIN (select t.start_r + pe.i as iteration_id FROM (select 1 as start_r,"+numIterations+" as end_r) t lateral view "
//						+ " posexplode(split(space(end_r - start_r),'')) pe as i,s) ranges ON (1=1)";
		String rangeSql = generateRangeSql(attributeList, attrTableName, attrTableNameSql, attributeName, execVersion, numIterations, maxRand, minRand);
		ResultSetHolder resultSetHolder = null;
		if(locationDpDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())/*
				|| datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
				|| datasource.getType().equalsIgnoreCase(ExecContext.livy_spark.toString())*/) {
			resultSetHolder = exec.executeAndRegister(rangeSql, tableName, commonServiceImpl.getApp().getUuid());
		} else {
			String sql = null;
			if(exec instanceof SparkExecutor<?> && !locationDpDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
				sql = rangeSql;
			} else {
				sql = helper.buildInsertQuery(appDatasource.getType(), tableName, locationDatapod, rangeSql);
			}
			resultSetHolder = exec.executeSql(sql);
		}
		
		// save result
		save(exec, resultSetHolder, tableName, locationDatapod, baseExec.getRef(MetaType.operatorExec), runMode);
		return null;
	}

public List<String> getColumnNameList(Object source, ParamListHolder holder ){
		
		List<String> columns = new ArrayList<>();
		List<AttributeRefHolder> attributeInfo = holder.getAttributeInfo();
		if(source instanceof Datapod) {
			Datapod datapod = (Datapod) source;
			
			for(Attribute attribute : datapod.getAttributes()) {
				for(AttributeRefHolder attributeRefHolder : attributeInfo)
					if(attribute.getAttributeId().equals(Integer.parseInt(""+attributeRefHolder.getAttrId()))) {
						columns.add(attribute.getName());
					}
			}
		} else if(source instanceof DataSet) {
			DataSet dataSet = (DataSet) source;
			
			for(AttributeSource attributeSource : dataSet.getAttributeInfo()) {
				for(AttributeRefHolder attributeRefHolder : attributeInfo)
					if(attributeSource.getAttrSourceId().equalsIgnoreCase(""+attributeRefHolder.getAttrId())) {
						columns.add(attributeSource.getAttrSourceName());
					}
			}
		} else if(source instanceof Rule) {
			Rule rule = (Rule) source;

			for(AttributeSource attributeSource : rule.getAttributeInfo()) {
				for(AttributeRefHolder attributeRefHolder : attributeInfo)
					if(attributeSource.getAttrSourceId().equalsIgnoreCase(""+attributeRefHolder.getAttrId())) {
						columns.add(attributeSource.getAttrSourceName());
					}
			}
		}
		return columns;
	}

	protected String getBucketsSql(int numIterations, ResultSetHolder resultSetHolder) {
		String sql = "select floor(ROW_NUMBER() over (order by 1)/"+numIterations+")+1 as bucketId, * from "+resultSetHolder.getTableName();
		return sql;
	}
	
	protected String getInstrumentSql (String tableName) {
		String sql = "select ROW_NUMBER() over (order by 1) - 1 as seqId, * from "+tableName;
		return sql;
	}
	
	protected String genDataSql (String instrumentTableName, String distroTableName, String attributeName, List<Attribute> distroAttrs) {
		StringBuilder sb = new StringBuilder("select ");
		sb.append(instrumentTableName).append(".");
		sb.append(attributeName).append(", ");
		for (Attribute attr : distroAttrs) {
			sb.append(distroTableName).append(".");
			sb.append(attr.getDispName()).append(", ");
		}
		String sql = sb.substring(0, sb.length() - 2).toString();
		sql = sql +" from " + instrumentTableName + " join " + distroTableName + " on ( " + instrumentTableName + ".seqId = " + distroTableName + ". bucketId)";
		logger.info(" genDataSql : " + sql);
		return sql;
	}

	public String generateRangeSql(List<Attribute> attributeList, String attrTableName, String attrTableNameSql,String attributeName, String execVersion, int numIterations, int maxRand, int minRand) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		if(datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
			return "select ranges.iteration_id as "+attributeList.get(0).getDispName()+","+attrTableName+"."+attributeName+" as "+attributeList.get(1).getDispName()
					+", ("+"randn(null)"+" * ("+maxRand+" - "+minRand+") + "+minRand+") as "+attributeList.get(2).getDispName()+", "+execVersion+" as "+attributeList.get(3).getDispName()
					+" FROM "
					+attrTableNameSql+ " CROSS JOIN (select t.start_r + pe.i as iteration_id FROM (select 1 as start_r,"+numIterations+" as end_r) t lateral view "
					+ " posexplode(split(space(end_r - start_r),'')) pe as i,s) ranges ON (1=1)";
		} else if(datasource.getType().equalsIgnoreCase(ExecContext.HIVE.toString())) {
			return "select ranges.iteration_id as "+attributeList.get(0).getDispName()+","+attrTableName+"."+attributeName+" as "+attributeList.get(1).getDispName()
					+", ("+"randn(null)"+" * ("+maxRand+" - "+minRand+") + "+minRand+") as "+attributeList.get(2).getDispName()+", "+execVersion+" as "+attributeList.get(3).getDispName()
					+" FROM "
					+attrTableNameSql+ " CROSS JOIN (select t.start_r + pe.i as iteration_id FROM (select 1 as start_r,"+numIterations+" as end_r) t lateral view "
					+ " posexplode(split(space(end_r - start_r),'')) pe as i,s) ranges ON (1=1)";
		} else if(datasource.getType().equalsIgnoreCase(ExecContext.IMPALA.toString())) {
			return "select ranges.iteration_id as "+attributeList.get(0).getDispName()+","+attrTableName+"."+attributeName+" as "+attributeList.get(1).getDispName()
					+", ("+"randn(null)"+" * ("+maxRand+" - "+minRand+") + "+minRand+") as "+attributeList.get(2).getDispName()+", "+execVersion+" as "+attributeList.get(3).getDispName()
					+" FROM "
					+attrTableNameSql+ " CROSS JOIN (select t.start_r + pe.i as iteration_id FROM (select 1 as start_r,"+numIterations+" as end_r) t lateral view "
					+ " posexplode(split(space(end_r - start_r),'')) pe as i,s) ranges ON (1=1)";
		} else {
			return "select ranges.iteration_id as "+attributeList.get(0).getDispName()+","+attrTableName+"."+attributeName+" as "+attributeList.get(1).getDispName()
					+", ("+"randn(null)"+" * ("+maxRand+" - "+minRand+") + "+minRand+") as "+attributeList.get(2).getDispName()+", "+execVersion+" as "+attributeList.get(3).getDispName()
					+" FROM "
					+attrTableNameSql+ " CROSS JOIN (select t.start_r + pe.i as iteration_id FROM (select 1 as start_r,"+numIterations+" as end_r) t lateral view "
					+ " posexplode(split(space(end_r - start_r),'')) pe as i,s) ranges ON (1=1)";
		}
	}
}
