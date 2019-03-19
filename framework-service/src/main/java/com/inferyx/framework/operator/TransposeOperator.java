/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.sql.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.ConstantsUtil;
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
import com.inferyx.framework.service.DatasetServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

/**
 * @author joy
 *
 */
@Service
public class TransposeOperator implements IOperator {
	
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
	private DatasetServiceImpl datasetServiceImpl;
	@Autowired
	private Helper helper;
	
	static final Logger logger = Logger.getLogger(TransposeOperator.class);

	/**
	 * 
	 * +-----+------+------+
	 * |  id |  yes |  no  |
	 * +-----+------+------+
	 * | 001 |  21  |  11  |
	 * | 002 |  9   |  89  |
	 * +-----+------+------+
	 * 
	 * ----------------- SQL ---------------------------
	 * SELECT id
  	 * , bool
  	 * , val
	 * FROM (
     * SELECT id
     * , MAP('yes', yes, 'no', no) AS tmp_column
     * FROM database.table ) x
	 * LATERAL VIEW EXPLODE(tmp_column) exptbl AS bool, val
	 * 
	 * ------------- TO ----------------------------------
	 * 
	 * +-----+------+------+
	 * |  id | bool | val  |
	 * +-----+------+------+
	 * | 001 |  yes |  21  |
	 * | 001 |  no  |  11  |
	 * | 002 |  yes |  9   |
	 * | 002 |  no  |  89  |
	 * +-----+------+------+
	 * 
	 */
	public TransposeOperator() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		ParamListHolder sourceDatapodInfo = paramSetServiceImpl.getParamByName(execParams, "sourceDatapod");
		HashMap<String, String> otherParams = execParams.getOtherParams();
		if (otherParams == null) {
			otherParams = new HashMap<String, String>();
			execParams.setOtherParams(otherParams);
		}
		String execVersion = baseExec.getVersion();
		String sourceTableName = null;
//		String destTableName = null;
		
		if (otherParams == null) {
			otherParams = new HashMap<>();
		}
		
		Datapod sourceData = null;
		DataSet sourceDataset = null;
		MetaIdentifier sourceDataIdentifier = sourceDatapodInfo.getAttributeInfo().get(0).getRef();
		if (sourceDataIdentifier.getType() == MetaType.datapod) {
			sourceData = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceDataIdentifier.getUuid(), sourceDataIdentifier.getVersion(), sourceDataIdentifier.getType().toString());
		} else if (sourceDataIdentifier.getType() == MetaType.dataset) {
			sourceDataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(sourceDataIdentifier.getUuid(), sourceDataIdentifier.getVersion(), sourceDataIdentifier.getType().toString());
		}

		if (otherParams.containsKey("datapodUuid_" + sourceDataIdentifier.getUuid() + "_tableName")) {
			sourceTableName = otherParams.get("datapodUuid_" + sourceDataIdentifier.getUuid() + "_tableName");
		} else {
			if (sourceDataIdentifier.getType() == MetaType.datapod) {
				sourceTableName = commonServiceImpl.getTableNameBySource(sourceData, runMode);
			} else if (sourceDataIdentifier.getType() == MetaType.dataset) {
				sourceTableName = sourceDataset.getName();
			}
			otherParams.put("datapodUuid_" + sourceDataIdentifier.getUuid() + "_tableName", sourceTableName);
		}
		
		// Set destination
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		
//		String newVersion = Helper.getVersion();
//		locationDatapod.setVersion(newVersion);
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, runMode);
		otherParams.put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", tableName);
		logger.info("otherParams in transposeOperator : "+ otherParams);
		
		return otherParams;
		
	}
	
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.Operator#execute(com.inferyx.framework.domain.OperatorType, com.inferyx.framework.domain.ExecParams, java.lang.Object, java.util.Map, java.util.HashMap, java.util.Set, com.inferyx.framework.domain.Mode)
	 */
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		logger.info("Executing TransposeOperator");
		StringBuilder sb = new StringBuilder();
		
		String execUuid = baseExec.getUuid();
		String execVersion = baseExec.getVersion();
		HashMap<String, String> otherParams = execParams.getOtherParams();
		
		//OperatorExec operatorExec = (OperatorExec) execIdentifier;
		
		ParamListHolder sourceDatapodInfo = paramSetServiceImpl.getParamByName(execParams, "sourceDatapod");
		ParamListHolder keyInfo = paramSetServiceImpl.getParamByName(execParams, "key");
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		//MetaIdentifier sourceDatapodIdentifier = sourceDatapodInfo.getAttributeInfo().get(0).getRef();
		//Datapod sourceDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceDatapodIdentifier.getUuid(), sourceDatapodIdentifier.getVersion(), sourceDatapodIdentifier.getType().toString());
		
//		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
		Datasource locationDpDatasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(locationDatapod.getDatasource().getRef().getUuid(), 
																					locationDatapod.getDatasource().getRef().getVersion(), 
																					locationDatapod.getDatasource().getRef().getType().toString());
		IExecutor exec = execFactory.getExecutor(appDatasource.getType());
		
		//String sourceTableName = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(sourceDatapod.getUuid(), sourceDatapod.getVersion()), runMode);
		
		String sourceTableName = null;
		
		/*List<AttributeRefHolder> attrRefHolders = sourceDatapodInfo.getAttributeInfo();
		List<Attribute> attrList = new ArrayList<>();
		List<Attribute> keyAttrList = new ArrayList<>();		
		List<AttributeRefHolder> keyAttrs = keyInfo.getAttributeInfo();
//		//Attribute keyAttr = sourceDatapod.getAttribute(Integer.parseInt(keyInfo.getAttributeInfo().get(0).getAttrId()));
		for (AttributeRefHolder attrRefHolder : attrRefHolders) {
			attrList.add(sourceDatapod.getAttribute(Integer.parseInt(attrRefHolder.getAttrId())));
		}
		for (AttributeRefHolder attrRefHolder : keyAttrs) {
			keyAttrList.add(sourceDatapod.getAttribute(Integer.parseInt(attrRefHolder.getAttrId())));
		}*/
		
		MetaIdentifier sourceDataIdentifier = sourceDatapodInfo.getAttributeInfo().get(0).getRef();
		Datapod sourceData = null;
		DataSet sourceDataset = null;
		List<String> attrList = null;
		String sourceTableSql = null;
		sourceTableName = otherParams.get("datapodUuid_" + sourceDataIdentifier.getUuid() + "_tableName");
		if (sourceDataIdentifier.getType() == MetaType.datapod) {
			sourceData = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceDataIdentifier.getUuid(), sourceDataIdentifier.getVersion(), sourceDataIdentifier.getType().toString());
			attrList = getColumnNameList(sourceData, sourceDatapodInfo);
			sourceTableSql = sourceTableName;
		} else if (sourceDataIdentifier.getType() == MetaType.dataset) {
			sourceDataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(sourceDataIdentifier.getUuid(), sourceDataIdentifier.getVersion(), sourceDataIdentifier.getType().toString());
			attrList = getColumnNameList(sourceDataset, sourceDatapodInfo);
			sourceTableSql = "(" + datasetServiceImpl.generateSql(sourceDataset, DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), otherParams, new HashSet<>(), execParams, runMode) + ") "
					+ sourceDataset.getName();
		}
//		sourceTableName = getTableNameBySource(sourceData, runMode);
		
		
		MetaIdentifier keyIdentifier = keyInfo.getAttributeInfo().get(0).getRef();
		Object key = commonServiceImpl.getOneByUuidAndVersion(keyIdentifier.getUuid(), keyIdentifier.getVersion(), keyIdentifier.getType().toString());
		List<String> keyAttrList = getColumnNameList(key, keyInfo);
		
		
		//String version = operatorExec.getVersion();
		
		// Get the fieldArray
//		boolean isAttrFound = false;
		sb.append(ConstantsUtil.SELECT);
		
		/*for (Attribute attribute : keyAttrList) {
			sb.append(attribute.getName()).append(", ");
		}*/
		int count = 0;
		for (String columnName : keyAttrList) {
			sb.append(columnName).append(" ");
			sb.append(locationDatapod.getAttributeName(count++)).append(", ");
		}
		
		sb.append("tranpose_column ");
		sb.append(locationDatapod.getAttributeName(count++));
		sb.append(", transpose_value ");
		sb.append(locationDatapod.getAttributeName(count++));
		sb.append(", " + execVersion + " ");
		sb.append(locationDatapod.getAttributeName(count++));
		sb.append(" FROM (");
		sb.append(ConstantsUtil.SELECT);
		
		/*for (Attribute attribute : keyAttrList) {
			sb.append(attribute.getName()).append(", ");
		}*/
		for (String columnName : keyAttrList) {
			sb.append(columnName).append(", ");
		}
		
		sb.append(" MAP (");
		count = 0;
		/*for(Attribute attribute : attrList) {
			isAttrFound = Boolean.TRUE;
			sb.append("'"+attribute.getName() + "', " + attribute.getName());
			sb.append((count < attrList.size()-1)?", ":"");
			count++;
			
		}*/
		for(String columnName : attrList) {
//			isAttrFound = Boolean.TRUE;
			sb.append("'"+columnName + "', " + columnName);
			sb.append((count < attrList.size()-1)?", ":"");
			count++;
			
		}
		
		sb.append(") AS tmp_column FROM ");
		sb.append(sourceTableSql);
		sb.append("  ) x LATERAL VIEW EXPLODE(tmp_column) exptbl AS tranpose_column, transpose_value ");
		String sql = sb.toString();
		logger.info("my sql: "+sql);
		
		String filePath = "/"+locationDatapod.getUuid() + "/" + locationDatapod.getVersion() + "/" + execVersion;
		String fileName = String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(), execVersion);
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
		
		/*String newVersion = Helper.getVersion();
		locationDatapod.setVersion(newVersion);
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, runMode);*/
		String tableName = otherParams.get("datapodUuid_" + locationDatapod.getUuid() + "_tableName");
		
		logger.info("Transpose sql --> " + sql);
		ResultSetHolder resultSetHolder = null;
		if(locationDpDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())/*
				|| datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
				|| datasource.getType().equalsIgnoreCase(ExecContext.livy_spark.toString())*/) {
			resultSetHolder = exec.executeRegisterAndPersist(sql, tableName, filePath, locationDatapod, SaveMode.Append.toString(), true, commonServiceImpl.getApp().getUuid());
		} else {
			String query = helper.buildInsertQuery(appDatasource.getType(), tableName, locationDatapod, sql);
			resultSetHolder = exec.executeSql(query);
		}
		
		Object metaExec = commonServiceImpl.getOneByUuidAndVersion(baseExec.getUuid(), baseExec.getVersion(), MetaType.operatorExec.toString());
		MetaIdentifierHolder createdBy = (MetaIdentifierHolder) metaExec.getClass().getMethod("getCreatedBy").invoke(metaExec);
		@SuppressWarnings("unchecked")
		List<MetaIdentifierHolder> appInfo = (List<MetaIdentifierHolder>) metaExec.getClass().getMethod("getAppInfo").invoke(metaExec);
		
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, fileName, 
				new MetaIdentifier(MetaType.datapod, locationDatapod.getUuid(), locationDatapod.getVersion()) 
				, new MetaIdentifier(MetaType.operatorExec, execUuid, execVersion) ,
				appInfo, createdBy, SaveMode.Append.toString(), resultRef, resultSetHolder.getCountRows(), null, null);
		
		metaExec.getClass().getMethod("setResult", MetaIdentifierHolder.class).invoke(metaExec, resultRef);
		commonServiceImpl.save(MetaType.operatorExec.toString(), metaExec);
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

	/********************** UNUSED **********************/
//	public String getTableNameBySource(Object sourceData, RunMode runMode) throws Exception {
//		String sourceTableName = null;
//		if(sourceData instanceof Datapod) {
//			Datapod datapod = (Datapod) sourceData;
//			sourceTableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//		} else if(sourceData instanceof DataSet) {
//			DataSet dataSet = (DataSet) sourceData;
//			MetaIdentifierHolder dependsOn = dataSet.getDependsOn();
//			if(dependsOn.getRef().getType().equals(MetaType.datapod)) {
//				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(), dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
//				sourceTableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//			} else if(dependsOn.getRef().getType().equals(MetaType.relation)) {
//				Relation relation = (Relation) sourceData;
//				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(), relation.getDependsOn().getRef().getType().toString());
//				sourceTableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//			}
//		} else if(sourceData instanceof Rule) {
//			Rule rule = (Rule) sourceData;
//			MetaIdentifierHolder sourceHolder = rule.getSource();
//			if(sourceHolder.getRef().getType().equals(MetaType.datapod)) {
//				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(), sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
//				sourceTableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//			} else if(sourceHolder.getRef().getType().equals(MetaType.dataset)) {
//				DataSet dataSet = (DataSet) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(), sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
//				MetaIdentifierHolder dependsOn = dataSet.getDependsOn();
//				if(dependsOn.getRef().getType().equals(MetaType.datapod)) {
//					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(), dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
//					sourceTableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//				} else if(dependsOn.getRef().getType().equals(MetaType.relation)) {
//					Relation relation = (Relation) sourceData;
//					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(), relation.getDependsOn().getRef().getType().toString());
//					sourceTableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//				}
//			} else if(sourceHolder.getRef().getType().equals(MetaType.relation)) {
//				Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(), sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
//				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(), relation.getDependsOn().getRef().getType().toString());
//				sourceTableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//			} else if(sourceHolder.getRef().getType().equals(MetaType.rule)) {
//				Rule rule2 = (Rule) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(), sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
//				sourceTableName = getTableNameBySource(rule2, runMode);
//			}
//		}
//		return sourceTableName;
//	}

	@Override
	public BaseExec create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
