/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.spark.sql.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.enums.RunMode;
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
public class CloneDataOperator implements Operator {
	
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
	
	static final Logger logger = Logger.getLogger(CloneDataOperator.class);


	/**
	 * 
	 */
	public CloneDataOperator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, String> populateParams(com.inferyx.framework.domain.Operator operator, ExecParams execParams,
			MetaIdentifier execIdentifier, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, List<String> datapodList, RunMode runMode) throws Exception {
		String execVersion = execIdentifier.getVersion();
		// Set destination
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, runMode);
		otherParams.put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", tableName);
		
		// Input datapod/dataset/rule
		ParamListHolder sourceInfo = paramSetServiceImpl.getParamByName(execParams, "sourceLocation");
		
		MetaIdentifier sourceIdentifier = sourceInfo.getParamValue().getRef();
		Object sourceObj = commonServiceImpl.getOneByUuidAndVersion(sourceIdentifier.getUuid(), sourceIdentifier.getVersion(), sourceIdentifier.getType().toString());
		
		String sourceStr = null;
		
		
		if (otherParams.containsKey("datapodUuid_" + sourceIdentifier.getUuid() + "_tableName")) {
			tableName = otherParams.get("datapodUuid_" + sourceIdentifier.getUuid() + "_tableName");
		} else {
			tableName = getTableNameBySource(sourceObj, runMode);
			otherParams.put("datapodUuid_" + sourceIdentifier.getUuid() + "_tableName", tableName);
		}
		/*if (sourceIdentifier.getType() == MetaType.datapod) {
			tableName = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(sourceIdentifier.getUuid(), sourceIdentifier.getVersion()), runMode);
//			tableName = datapodServiceImpl.genTableNameByDatapod((Datapod)sourceObj, execVersion, runMode);
			otherParams.put("datapodUuid_" + ((Datapod)sourceObj).getUuid() + "_tableName", tableName);
		}*/
		
		logger.info("otherParams in cloneDataOperator : "+ otherParams);
		return otherParams;
	}

	@Override
	public String parse(com.inferyx.framework.domain.Operator operator, ExecParams execParams,
			MetaIdentifier execIdentifier, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, List<String> datapodList, RunMode runMode) throws Exception {
		return null;
	}

	@Override
	public String execute(com.inferyx.framework.domain.Operator operator, ExecParams execParams,
			MetaIdentifier execIdentifier, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
		logger.info(" Inside CloneDataOperator");
		String execUuid = execIdentifier.getUuid();
		String execVersion = execIdentifier.getVersion();
		
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		
		ParamListHolder sourceInfo = paramSetServiceImpl.getParamByName(execParams, "sourceLocation");
		
		ParamListHolder numRecordInfo = paramSetServiceImpl.getParamByName(execParams, "numRecords");
		
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		Long numRecords = Long.parseLong(numRecordInfo.getParamValue().getValue()); 
		
		MetaIdentifier sourceIdentifier = sourceInfo.getParamValue().getRef();
		Object sourceObj = commonServiceImpl.getOneByUuidAndVersion(sourceIdentifier.getUuid(), sourceIdentifier.getVersion(), sourceIdentifier.getType().toString());
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		String tableName = otherParams.get("datapodUuid_" + locationDatapod.getUuid() + "_tableName");
		String sourceSql = null;
		if (otherParams.containsKey("datapodUuid_" + sourceIdentifier.getUuid() + "_tableName")) {
			sourceSql = otherParams.get("datapodUuid_" + sourceIdentifier.getUuid() + "_tableName");
		} else if (sourceIdentifier.getType() == MetaType.rule) {
			sourceSql = "(".concat(ruleOperator.generateSql((Rule)sourceObj, refKeyMap, otherParams, usedRefKeySet, execParams, runMode)).concat(")");
		} else if (sourceIdentifier.getType() == MetaType.dataset) {
			sourceSql = "(".concat(datasetOperator.generateSql((DataSet)sourceObj, refKeyMap, otherParams, usedRefKeySet, execParams, runMode)).concat(")");
		}
		
		String filePath = "/"+locationDatapod.getUuid() + "/" + locationDatapod.getVersion() + "/" + execVersion;
		String fileName = String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(), execVersion);
		
		// Find data count in source 
		
		StringBuilder countSqlBuilder = new StringBuilder(ConstantsUtil.SELECT)
				.append(" COUNT(*) count_data ")
				.append(ConstantsUtil.FROM)
				.append(sourceSql);
		
		logger.info("Count sql --> " + countSqlBuilder.toString());
		
		List<Map<String, Object>> countResult = exec.executeAndFetch(countSqlBuilder.toString(), commonServiceImpl.getApp().getUuid());
		Long countData = (Long) countResult.get(0).get("count_data");
		
		int numIterations = (int) (numRecords / countData);
//		logger.info(" numRecords : " + numRecords + " : countData : " + countData + " : numIterations : " + numIterations);
		String generateNumIterRows = "select t.start_r + pe.i as iteration_id FROM (select 1 as start_r," + numIterations 
				+ " as end_r) t lateral view posexplode(split(space(end_r - start_r),'')) pe as i,s";
		String crossJoinSql = "select ss.* FROM " + sourceSql + " ss CROSS JOIN (" + generateNumIterRows + ") ranges ON (1=1)";
//		exec.executeSql(crossJoinSql);
		StringBuilder sb = new StringBuilder(crossJoinSql)
				.append(ConstantsUtil.UNION_ALL)
				.append("(")
				.append(ConstantsUtil.SELECT)
				.append(" * FROM ")
				.append(sourceSql)
				.append(" LIMIT ")
				.append("("+numRecords +"- ("+numIterations+" * "+countData+"))")
				.append(")");
		
		exec.executeAndPersist(sb.toString(), filePath, locationDatapod, SaveMode.Append.toString(), commonServiceImpl.getApp().getUuid());
		
		
		return null;
	}

	public String getTableNameBySource(Object sourceData, RunMode runMode) throws Exception {
		String sourceTableName = null;
		if(sourceData instanceof Datapod) {
			Datapod datapod = (Datapod) sourceData;
			sourceTableName = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
		} else if(sourceData instanceof DataSet) {
			DataSet dataSet = (DataSet) sourceData;
			MetaIdentifierHolder dependsOn = dataSet.getDependsOn();
			if(dependsOn.getRef().getType().equals(MetaType.datapod)) {
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(), dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
				sourceTableName = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			} else if(dependsOn.getRef().getType().equals(MetaType.relation)) {
				Relation relation = (Relation) sourceData;
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(), relation.getDependsOn().getRef().getType().toString());
				sourceTableName = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			}
		} else if(sourceData instanceof Rule) {
			Rule rule = (Rule) sourceData;
			MetaIdentifierHolder sourceHolder = rule.getSource();
			if(sourceHolder.getRef().getType().equals(MetaType.datapod)) {
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(), sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
				sourceTableName = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			} else if(sourceHolder.getRef().getType().equals(MetaType.dataset)) {
				DataSet dataSet = (DataSet) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(), sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
				MetaIdentifierHolder dependsOn = dataSet.getDependsOn();
				if(dependsOn.getRef().getType().equals(MetaType.datapod)) {
					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(), dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
					sourceTableName = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
				} else if(dependsOn.getRef().getType().equals(MetaType.relation)) {
					Relation relation = (Relation) sourceData;
					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(), relation.getDependsOn().getRef().getType().toString());
					sourceTableName = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
				}
			} else if(sourceHolder.getRef().getType().equals(MetaType.relation)) {
				Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(), sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(), relation.getDependsOn().getRef().getType().toString());
				sourceTableName = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			} else if(sourceHolder.getRef().getType().equals(MetaType.rule)) {
				Rule rule2 = (Rule) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(), sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
				sourceTableName = getTableNameBySource(rule2, runMode);
			}
		}
		return sourceTableName;
	}

	
}
