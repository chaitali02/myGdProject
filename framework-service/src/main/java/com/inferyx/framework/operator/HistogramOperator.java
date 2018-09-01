package com.inferyx.framework.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.SparkException;
import org.apache.spark.sql.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

/**
 * @author Ganesh
 *
 */
@Component
public class HistogramOperator implements IOperator {
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private DatasetOperator datasetOperator;
	@Autowired
	Engine engine;
	@Autowired
	private SparkExecutor<?> sparkExecutor;
	
	static final Logger logger = Logger.getLogger(HistogramOperator.class);
	
	/* 
	 * @Ganesh
	 * 
	 * @param baseExec
	 * @param execParams
	 * @param runMode
	 * @return
	 * @throws Exception
	 */
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @Ganesh
	 * 
	 * @param baseExec
	 * @param execParams
	 * @param runMode
	 * @return
	 * @throws Exception
	 */
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		ParamListHolder numBucketsInfo = paramSetServiceImpl.getParamByName(execParams, "numBuckets");
		ParamListHolder sourceInfo = paramSetServiceImpl.getParamByName(execParams, "sourceAttr");
		ParamListHolder keyInfo = paramSetServiceImpl.getParamByName(execParams, "key");
		HashMap<String, String> otherParams = execParams.getOtherParams();

		String appUuid = commonServiceImpl.getApp().getUuid();
		
		int numBuckets = Integer.parseInt(numBucketsInfo.getParamValue().getValue());
		String key = keyInfo.getParamValue().getValue();
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		
		String locationTableName = otherParams.get("datapodUuid_" + locationDatapod.getUuid() + "_tableName");
		if(locationTableName == null || locationTableName.isEmpty()) {
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			/*if ((!engine.getExecEngine().equalsIgnoreCase("livy-spark")
					|| !engine.getExecEngine().equalsIgnoreCase(ExecContext.livy_spark.toString()))
					&& !datasource.getType().equalsIgnoreCase(ExecContext.spark.toString()) 
					&& !datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {*/
			if (!datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
				locationTableName = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(locationDatapod.getUuid(), locationDatapod.getVersion()), runMode);
			}  else {
				locationTableName = String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(), baseExec.getVersion());
			}			
			execParams.getOtherParams().put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", locationTableName);
		}
		
		
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());		
		
		String sql = generateSql(sourceInfo.getAttributeInfo(), execParams, otherParams, runMode);
		ResultSetHolder rsHolder = exec.histogram(locationDatapod, locationTableName, sql, key, numBuckets, appUuid);
		save(exec, rsHolder, locationTableName, locationDatapod,  baseExec.getRef(MetaType.operatorExec), runMode);
		return null;
	}	
	
	/* 
	 * @Ganesh
	 * 
	 * @param baseExec
	 * @param execParams
	 * @param runMode
	 * @return
	 * @throws Exception
	 */
	@Override
	public BaseExec create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @Ganesh
	 * 
	 * @param baseExec
	 * @param execParams
	 * @param runMode
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String generateSql(List<AttributeRefHolder> sourceAttrs, ExecParams execParams, HashMap<String, String> otherParams, RunMode runMode) throws Exception {
		String sql = null;
		MetaIdentifier sourceMI = sourceAttrs.get(0).getRef();
		
		if(sourceMI.getType().equals(MetaType.datapod)) {
			StringBuilder sqlBuilder = new StringBuilder();
			
			Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceMI.getUuid(), sourceMI.getVersion(), sourceMI.getType().toString());
			
			String tableName = otherParams.get("datapodUuid_" + datapod.getUuid() + "_tableName");
			if(tableName == null || tableName.isEmpty()) {
				tableName = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			}
			sqlBuilder.append("SELECT ");
			
			for(int i=0; i<sourceAttrs.size(); i++) {
				String attrName = datapod.getAttributeName(Integer.parseInt(sourceAttrs.get(i).getAttrId()));				
				sqlBuilder.append("CAST(").append(attrName).append(" AS DOUBLE) AS ").append(attrName);
				if(i<(sourceAttrs.size()-1))
					sqlBuilder.append(",");				
			}
			
			sqlBuilder.append(" FROM ");
			sqlBuilder.append(tableName);
			sqlBuilder.append(" ").append(datapod.getName());
			sql = sqlBuilder.toString();
		}  else if(sourceMI.getType().equals(MetaType.dataset)) {
			DataSet dataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(sourceMI.getUuid(), sourceMI.getVersion(), sourceMI.getType().toString());
			
			List<AttributeSource> attributeInfo = new ArrayList<>();
			for(AttributeRefHolder sourceAttr : sourceAttrs) {
				for(AttributeSource attrSource : dataset.getAttributeInfo()) {
						if(attrSource.getAttrSourceId().equalsIgnoreCase(sourceAttr.getAttrId())) {
							attributeInfo.add(attrSource);
						}
				}
			}
			dataset.setAttributeInfo(attributeInfo);
			sql = datasetOperator.generateSql(dataset, DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), otherParams, new HashSet<>(), execParams, runMode);
		}
		return sql;
	}
	

	/**
	 * 
	 * @param exec
	 * @param resultSetHolder
	 * @param tableName
	 * @param locationDatapod
	 * @param execIdentifier
	 * @param runMode
	 * @throws Exception
	 */
	protected void save (IExecutor exec, 
						ResultSetHolder resultSetHolder,  
						String tableName, 
						Datapod locationDatapod, 
						MetaIdentifier execIdentifier, 
						RunMode runMode) throws Exception {
		String execVersion = execIdentifier.getVersion();
		String execUuid = execIdentifier.getUuid();
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
//		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		Datasource datasource = commonServiceImpl.getDatasourceByDatapod(locationDatapod);
		if(datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())/*
				|| datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
				|| datasource.getType().equalsIgnoreCase(ExecContext.livy_spark.toString())
				|| datasource.getType().equalsIgnoreCase("livy-spark")*/) {
			resultSetHolder = exec.registerAndPersist(resultSetHolder, tableName, getFilePath(locationDatapod, execVersion), locationDatapod, SaveMode.Append.toString(), commonServiceImpl.getApp().getUuid());
		} else {
			resultSetHolder = sparkExecutor.persistDataframe(resultSetHolder, datasource, locationDatapod);
		}		
		logger.info("execIdentifier : " + execIdentifier.getUuid() +":"+ execIdentifier.getVersion() +":"+ execIdentifier.getType());
		Object metaExec = commonServiceImpl.getOneByUuidAndVersion(execIdentifier.getUuid(), execIdentifier.getVersion(), execIdentifier.getType().toString());
		MetaIdentifierHolder createdBy = (MetaIdentifierHolder) metaExec.getClass().getMethod("getCreatedBy").invoke(metaExec);
		@SuppressWarnings("unchecked")
		List<MetaIdentifierHolder> appInfo = (List<MetaIdentifierHolder>) metaExec.getClass().getMethod("getAppInfo").invoke(metaExec);
		
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(getFilePath(locationDatapod, execVersion), getFileName(locationDatapod, execVersion), 
				new MetaIdentifier(MetaType.datapod, locationDatapod.getUuid(), locationDatapod.getVersion()) 
				, new MetaIdentifier(MetaType.operatorExec, execUuid, execVersion) ,
				appInfo, createdBy, SaveMode.Append.toString(), resultRef, resultSetHolder.getCountRows(), null, null);
		
		metaExec.getClass().getMethod("setResult", MetaIdentifierHolder.class).invoke(metaExec, resultRef);
		logger.info("After setResult : " + ((OperatorExec)metaExec).getResult().getRef().getUuid());
		commonServiceImpl.save(execIdentifier.getType().toString(), metaExec);
		
	}
	
	protected String getFilePath (Datapod locationDatapod, String execVersion) {
		return "/"+locationDatapod.getUuid() + "/" + locationDatapod.getVersion() + "/" + execVersion;
	}
	
	protected String getFileName (Datapod locationDatapod, String execVersion) {
		return String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(), execVersion);
	}
}