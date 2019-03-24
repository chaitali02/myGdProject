/**
 * 
 */
package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.sql.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

/**
 * @author joy
 *
 */
@Service
public class CloneDataOperator implements IOperator {

	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	@Autowired
	private RuleOperator ruleOperator;
	@Autowired
	private DatasetOperator datasetOperator;
	@Autowired
	private Helper helper;

	static final Logger logger = Logger.getLogger(CloneDataOperator.class);

	/**
	 * 
	 */
	public CloneDataOperator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		String execVersion = baseExec.getVersion();
		Map<String, String> otherParams = execParams.getOtherParams();
		// Set destination
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");

		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(),
				locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());

		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, null, null, null, runMode, false);
		otherParams.put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", tableName);

		// Input datapod/dataset/rule
		ParamListHolder sourceInfo = paramSetServiceImpl.getParamByName(execParams, "sourceLocation");

		MetaIdentifier sourceIdentifier = sourceInfo.getParamValue().getRef();
		Object sourceObj = commonServiceImpl.getOneByUuidAndVersion(sourceIdentifier.getUuid(),
				sourceIdentifier.getVersion(), sourceIdentifier.getType().toString());

//		String sourceStr = null;

		if (otherParams.containsKey("datapodUuid_" + sourceIdentifier.getUuid() + "_tableName")) {
			tableName = otherParams.get("datapodUuid_" + sourceIdentifier.getUuid() + "_tableName");
		} else {
			tableName = commonServiceImpl.getTableNameBySource(sourceObj, runMode);
			otherParams.put("datapodUuid_" + sourceIdentifier.getUuid() + "_tableName", tableName);
		}
		/*
		 * if (sourceIdentifier.getType() == MetaType.datapod) { tableName =
		 * dataStoreServiceImpl.getTableNameByDatapodKey(new
		 * OrderKey(sourceIdentifier.getUuid(), sourceIdentifier.getVersion()),
		 * runMode); // tableName =
		 * datapodServiceImpl.genTableNameByDatapod((Datapod)sourceObj, execVersion,
		 * runMode); otherParams.put("datapodUuid_" + ((Datapod)sourceObj).getUuid() +
		 * "_tableName", tableName); }
		 */

		logger.info("otherParams in cloneDataOperator : " + otherParams);
		return otherParams;
	}

	/**
	 * 
	 * @param exec
	 * @param count
	 * @param tableName
	 * @param locationDatapod
	 * @param execIdentifier
	 * @param runMode
	 * @throws Exception
	 */
	protected void createDataStore(IExecutor exec, long count, String tableName, Datapod locationDatapod,
			MetaIdentifier execIdentifier, RunMode runMode) throws Exception {
		String execVersion = execIdentifier.getVersion();
		String execUuid = execIdentifier.getUuid();
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();

		Object metaExec = commonServiceImpl.getOneByUuidAndVersion(execIdentifier.getUuid(),
				execIdentifier.getVersion(), execIdentifier.getType().toString());
		MetaIdentifierHolder createdBy = (MetaIdentifierHolder) metaExec.getClass().getMethod("getCreatedBy")
				.invoke(metaExec);
		@SuppressWarnings("unchecked")
		List<MetaIdentifierHolder> appInfo = (List<MetaIdentifierHolder>) metaExec.getClass().getMethod("getAppInfo")
				.invoke(metaExec);

		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(getFilePath(locationDatapod, execVersion),
				getFileName(locationDatapod, execVersion),
				new MetaIdentifier(MetaType.datapod, locationDatapod.getUuid(), locationDatapod.getVersion()),
				new MetaIdentifier(MetaType.operatorExec, execUuid, execVersion), appInfo, createdBy,
				SaveMode.Append.toString(), resultRef, count, null, null);

		metaExec.getClass().getMethod("setResult", MetaIdentifierHolder.class).invoke(metaExec, resultRef);
		commonServiceImpl.save(execIdentifier.getType().toString(), metaExec);

	}

	/********************** UNUSED **********************/
//	public String getTableNameBySource(Object sourceData, RunMode runMode) throws Exception {
//		String sourceTableName = null;
//		if (sourceData instanceof Datapod) {
//			Datapod datapod = (Datapod) sourceData;
//			sourceTableName = datapodServiceImpl
//					.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//		} else if (sourceData instanceof DataSet) {
//			DataSet dataSet = (DataSet) sourceData;
//			MetaIdentifierHolder dependsOn = dataSet.getDependsOn();
//			if (dependsOn.getRef().getType().equals(MetaType.datapod)) {
//				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(),
//						dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
//				sourceTableName = datapodServiceImpl
//						.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//			} else if (dependsOn.getRef().getType().equals(MetaType.relation)) {
//				Relation relation = (Relation) sourceData;
//				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
//						relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(),
//						relation.getDependsOn().getRef().getType().toString());
//				sourceTableName = datapodServiceImpl
//						.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//			}
//		} else if (sourceData instanceof Rule) {
//			Rule rule = (Rule) sourceData;
//			MetaIdentifierHolder sourceHolder = rule.getSource();
//			if (sourceHolder.getRef().getType().equals(MetaType.datapod)) {
//				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(),
//						sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
//				sourceTableName = datapodServiceImpl
//						.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//			} else if (sourceHolder.getRef().getType().equals(MetaType.dataset)) {
//				DataSet dataSet = (DataSet) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(),
//						sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
//				MetaIdentifierHolder dependsOn = dataSet.getDependsOn();
//				if (dependsOn.getRef().getType().equals(MetaType.datapod)) {
//					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(),
//							dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
//					sourceTableName = datapodServiceImpl
//							.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//				} else if (dependsOn.getRef().getType().equals(MetaType.relation)) {
//					Relation relation = (Relation) sourceData;
//					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
//							relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(),
//							relation.getDependsOn().getRef().getType().toString());
//					sourceTableName = datapodServiceImpl
//							.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//				}
//			} else if (sourceHolder.getRef().getType().equals(MetaType.relation)) {
//				Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(),
//						sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
//				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
//						relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(),
//						relation.getDependsOn().getRef().getType().toString());
//				sourceTableName = datapodServiceImpl
//						.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//			} else if (sourceHolder.getRef().getType().equals(MetaType.rule)) {
//				Rule rule2 = (Rule) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(),
//						sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
//				sourceTableName = getTableNameBySource(rule2, runMode);
//			}
//		}
//		return sourceTableName;
//	}

	protected String getFilePath(Datapod locationDatapod, String execVersion) {
		return "/" + locationDatapod.getUuid() + "/" + locationDatapod.getVersion() + "/" + execVersion;
	}

	protected String getFileName(Datapod locationDatapod, String execVersion) {
		return String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(),
				execVersion);
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		logger.info(" Inside CloneDataOperator");
//		String execUuid = baseExec.getUuid();
		String execVersion = baseExec.getVersion();
		HashMap<String, String> otherParams = execParams.getOtherParams();
		Map<String, MetaIdentifier> refKeyMap = DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList());

		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());

		ParamListHolder sourceInfo = paramSetServiceImpl.getParamByName(execParams, "sourceLocation");

		ParamListHolder numRecordInfo = paramSetServiceImpl.getParamByName(execParams, "numRecords");

		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");

		Long numRecords = Long.parseLong(numRecordInfo.getParamValue().getValue());

		MetaIdentifier sourceIdentifier = sourceInfo.getParamValue().getRef();
		Object sourceObj = commonServiceImpl.getOneByUuidAndVersion(sourceIdentifier.getUuid(),
				sourceIdentifier.getVersion(), sourceIdentifier.getType().toString());
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(),
				locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		String tableName = otherParams.get("datapodUuid_" + locationDatapod.getUuid() + "_tableName");
		String sourceSql = null;
		if (otherParams.containsKey("datapodUuid_" + sourceIdentifier.getUuid() + "_tableName")) {
			sourceSql = otherParams.get("datapodUuid_" + sourceIdentifier.getUuid() + "_tableName");
		} else if (sourceIdentifier.getType() == MetaType.rule) {
			sourceSql = "(".concat(ruleOperator.generateSql((Rule) sourceObj, DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), otherParams, null,
					execParams, runMode)).concat(")");
		} else if (sourceIdentifier.getType() == MetaType.dataset) {
			sourceSql = "(".concat(datasetOperator.generateSql((DataSet) sourceObj, refKeyMap, otherParams,
					null, execParams, runMode)).concat(")");
		}

		String filePath = "/" + locationDatapod.getUuid() + "/" + locationDatapod.getVersion() + "/" + execVersion;
//		String fileName = String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(), execVersion);

		// Find data count in source

		StringBuilder countSqlBuilder = new StringBuilder(ConstantsUtil.SELECT).append(" COUNT(*) count_data ")
				.append(ConstantsUtil.FROM).append(sourceSql);

		logger.info("Count sql --> " + countSqlBuilder.toString());

		List<Map<String, Object>> countResult = exec.executeAndFetch(countSqlBuilder.toString(),
				commonServiceImpl.getApp().getUuid());
		Long countData = (Long) countResult.get(0).get("count_data");

		int numIterations = (int) (numRecords / countData);
		// logger.info(" numRecords : " + numRecords + " : countData : " + countData + "
		// : numIterations : " + numIterations);
		String generateNumIterRows = "select t.start_r + pe.i as iteration_id FROM (select 1 as start_r,"
				+ numIterations + " as end_r) t lateral view posexplode(split(space(end_r - start_r),'')) pe as i,s";
		String crossJoinSql = "select ss.* FROM " + sourceSql + " ss CROSS JOIN (" + generateNumIterRows
				+ ") ranges ON (1=1)";
		// exec.executeSql(crossJoinSql);
		StringBuilder sb = new StringBuilder(crossJoinSql).append(ConstantsUtil.UNION_ALL).append(genUnionQuery(sourceSql, numRecords, numIterations, countData));

		ResultSetHolder rsHolder = null;
		if(datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())/*
				|| datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
				|| datasource.getType().equalsIgnoreCase(ExecContext.livy_spark.toString())*/) {
			rsHolder = exec.executeAndPersist(sb.toString(), filePath, locationDatapod,
				SaveMode.Append.toString(), commonServiceImpl.getApp().getUuid());
		} else {
			String sql = helper.buildInsertQuery(datasource.getType(), tableName, locationDatapod, sb.toString());
			rsHolder = exec.executeAndPersist(sql, filePath, locationDatapod,
					SaveMode.Append.toString(), commonServiceImpl.getApp().getUuid());
		}

		createDataStore(exec, rsHolder.getCountRows(), tableName, locationDatapod, baseExec.getRef(MetaType.operatorExec), runMode);
		return null;
	}

	public String genUnionQuery(String sourceSql, Long numRecords, int numIterations, Long countData) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		StringBuilder sb = new StringBuilder();
		if(datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())
				|| datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
				|| datasource.getType().equalsIgnoreCase(ExecContext.livy_spark.toString())
				|| datasource.getType().equalsIgnoreCase("livy-spark")) {
			return sb.append("(").append(ConstantsUtil.SELECT).append(" * FROM ").append(sourceSql).append(" LIMIT ")
				.append("(" + numRecords + "- (" + numIterations + " * " + countData + "))").append(")").toString();
		} else {
			Long limit = numRecords - (numIterations * countData);
			return sb.append(ConstantsUtil.SELECT).append(" * FROM ").append(sourceSql).append(" LIMIT ").append(limit).toString();
		}
	}
	@Override
	public BaseExec create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
