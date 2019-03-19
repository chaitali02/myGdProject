/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.GenVal;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

/**
 * @author joy
 *
 */
@Service
public class GenerateDataForValList extends GenerateDataOperator {
	
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	@Autowired
	private Helper helper;
	
	static final Logger logger = Logger.getLogger(GenerateDataForValList.class);

	/**
	 * 
	 */
	public GenerateDataForValList() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		String execUuid = baseExec.getUuid();
		String execVersion = baseExec.getVersion();
		Map<String, String> otherParams = execParams.getOtherParams();
		ParamListHolder valInfo = paramSetServiceImpl.getParamByName(execParams, "valList");
		// Set destination
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		
		String valStr = valInfo.getParamValue().getValue();
		String []valStrArr = valStr.split(",");
		List<String> valList = Arrays.asList(valStrArr);
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		String valTableName = "valList_"+execUuid+"_"+execVersion;
		exec.createAndRegister(valList, GenVal.class, valTableName, commonServiceImpl.getApp().getUuid());
		
//		String newVersion = Helper.getVersion();
//		locationDatapod.setVersion(newVersion);
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, null, null, null, runMode, false);
		otherParams.put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", tableName);
		otherParams.put("datapodUuid_" + "valList" + "_tableName", valTableName);
			
		return otherParams;
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		String execUuid = baseExec.getUuid();
		String execVersion = baseExec.getVersion();
		Map<String, String> otherParams = execParams.getOtherParams();
//		int numRepetitions = 0;
		Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(appDatasource.getType());
		
		ParamListHolder numIterationsInfo = paramSetServiceImpl.getParamByName(execParams, "numIterations");
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
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

		// Get the attribute 
		String attributeName = "id";
		String attrTableName = otherParams.get("datapodUuid_" + "valList" + "_tableName");
		
		List<Attribute> attributeList = locationDatapod.getAttributes();
		
		// Generate Data
		String rangeSql = "select ranges.iteration_id as "+attributeList.get(0).getDispName()+","+attrTableName+"."+attributeName+" as "+attributeList.get(1).getDispName()
				+", (randn() * ("+maxRand+" - "+minRand+") + "+minRand+") as "+attributeList.get(2).getDispName()+", "+execVersion+" as "+attributeList.get(3).getDispName()
				+" FROM "
				+attrTableName+ " CROSS JOIN (select t.start_r + pe.i as iteration_id FROM (select 1 as start_r,"+numIterations+" as end_r) t lateral view "
				+ " posexplode(split(space(end_r - start_r),'')) pe as i,s) ranges ON (1=1)";
		ResultSetHolder resultSetHolder = null;
		if(locationDpDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())/*
				|| datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
				|| datasource.getType().equalsIgnoreCase(ExecContext.livy_spark.toString())*/) {
			resultSetHolder = exec.executeAndRegister(rangeSql, tableName, appDatasource.getType());
		}  else {
			String sql = helper.buildInsertQuery(appDatasource.getType(), tableName, locationDatapod, rangeSql);
			resultSetHolder = exec.executeAndPersist(sql, null, locationDatapod, null, null);
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
	
	protected String genDataSql (String instrumentTableName, String distroTableName) {
		String sql = "select * from " + instrumentTableName + " join " + distroTableName + " on ( " + instrumentTableName + ".seqId = " + distroTableName + ". bucketId)";
		return sql;
	}

}
