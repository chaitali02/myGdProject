/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.sql.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.common.ConstantsUtil;
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
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Relation;
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
 * 1 ["a", "b", "c"]
 * 2 ["d", "e", "f"]
 * 
 * select id, feature_ex from test_arr lateral view explode (feature) ex_t_1 as faeture_ex;
 * 
 * 1	a
 * 1	b
 * 1	c
 * 2	d
 * 2	e
 * 2	f
 * 
 * @author joy
 *
 */
public class ArrTransposeOperator implements IOperator {

	
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
	private Helper helper;
	
	static final Logger logger = Logger.getLogger(ArrTransposeOperator.class);
	/**
	 * 
	 */
	public ArrTransposeOperator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		logger.info("Parsing ArrTransposeOperator");
		StringBuilder sb = new StringBuilder();
		
		HashMap<String, String> otherParams = execParams.getOtherParams();
		
		//OperatorExec operatorExec = (OperatorExec) execIdentifier;
		
		ParamListHolder sourceDatapodInfo = paramSetServiceImpl.getParamByName(execParams, "sourceDatapod");
		ParamListHolder arrInfo = paramSetServiceImpl.getParamByName(execParams, "arrs");
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		
//		Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
		
		String sourceTableName = null;
		
		MetaIdentifier sourceDataIdentifier = sourceDatapodInfo.getAttributeInfo().get(0).getRef();
		Datapod sourceData = null;
		DataSet sourceDataset = null;
		List<String> attrList = null;
		sourceTableName = otherParams.get("datapodUuid_" + sourceDataIdentifier.getUuid() + "_tableName");
		if (sourceDataIdentifier.getType() == MetaType.datapod) {
			sourceData = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceDataIdentifier.getUuid(), sourceDataIdentifier.getVersion(), sourceDataIdentifier.getType().toString());
			attrList = getColumnNameList(sourceData, sourceDatapodInfo);
		} else if (sourceDataIdentifier.getType() == MetaType.dataset) {
			sourceDataset = (DataSet) commonServiceImpl.getOneByUuidAndVersion(sourceDataIdentifier.getUuid(), sourceDataIdentifier.getVersion(), sourceDataIdentifier.getType().toString());
			attrList = getColumnNameList(sourceDataset, sourceDatapodInfo);
		}
		
		MetaIdentifier arrIdentifier = arrInfo.getAttributeInfo().get(0).getRef();
		Object key = commonServiceImpl.getOneByUuidAndVersion(arrIdentifier.getUuid(), arrIdentifier.getVersion(), arrIdentifier.getType().toString());
		List<String> arrAttrList = getColumnNameList(key, arrInfo);
		
		// Get the fieldArray
		sb.append(ConstantsUtil.SELECT);
		
		int count = 0;
		for (String columnName : attrList) {
			if (!arrAttrList.contains(columnName)) {
				sb.append(columnName + "_ex").append(" ");
			} else {
				sb.append(columnName).append(" ");
			}
			sb.append(locationDatapod.getAttributeName(count++)).append(", ");
		}
		sb.append(" FROM ")
		  .append(sourceTableName);
		
		for (String colName : arrAttrList) {
			count++;
			sb.append(" LATERAL VIEW EXPLODE(")
			  .append(colName)
			  .append(")")
			  .append("ex_t_"+count + " as ") 
			  .append(colName + "_ex ");
		}
		baseExec.setExec(sb.toString());
		return baseExec;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		String execUuid = baseExec.getUuid();
		String execVersion = baseExec.getVersion();
		HashMap<String, String> otherParams = execParams.getOtherParams();
		
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		String sql = baseExec.getExec();
		logger.info("my sql: "+sql);
		
		String filePath = "/"+locationDatapod.getUuid() + "/" + locationDatapod.getVersion() + "/" + execVersion;
		String fileName = String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(), execVersion);
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
		
		String tableName = otherParams.get("datapodUuid_" + locationDatapod.getUuid() + "_tableName");
		
		logger.info("Transpose sql --> " + sql);
		ResultSetHolder resultSetHolder = null;
		Datasource appDatasource = commonServiceImpl.getDatasourceByApp();
		Datasource locationDpDatasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(locationDatapod.getDatasource().getRef().getUuid(), 
																					locationDatapod.getDatasource().getRef().getVersion(), 
																					locationDatapod.getDatasource().getRef().getType().toString());
		IExecutor exec = execFactory.getExecutor(appDatasource.getType());
		if(locationDpDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
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

	@Override
	public BaseExec create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unused")
	@Override
	public Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode)
			throws Exception {
		ParamListHolder sourceDatapodInfo = paramSetServiceImpl.getParamByName(execParams, "sourceDatapod");
		HashMap<String, String> otherParams = execParams.getOtherParams();
		if (otherParams == null) {
			otherParams = new HashMap<String, String>();
			execParams.setOtherParams(otherParams);
		}
		String execVersion = baseExec.getVersion();
		String sourceTableName = null;
		
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
				sourceTableName = getTableNameBySource(sourceData, runMode);
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
	
	public List<String> getColumnNameList(Object source, ParamListHolder holder){
		
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

	public String getTableNameBySource(Object sourceData, RunMode runMode) throws Exception {
		String sourceTableName = null;
		if(sourceData instanceof Datapod) {
			Datapod datapod = (Datapod) sourceData;
			sourceTableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
		} else if(sourceData instanceof DataSet) {
			DataSet dataSet = (DataSet) sourceData;
			MetaIdentifierHolder dependsOn = dataSet.getDependsOn();
			if(dependsOn.getRef().getType().equals(MetaType.datapod)) {
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(), dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
				sourceTableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			} else if(dependsOn.getRef().getType().equals(MetaType.relation)) {
				Relation relation = (Relation) sourceData;
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(), relation.getDependsOn().getRef().getType().toString());
				sourceTableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			}
		} else if(sourceData instanceof Rule) {
			Rule rule = (Rule) sourceData;
			MetaIdentifierHolder sourceHolder = rule.getSource();
			if(sourceHolder.getRef().getType().equals(MetaType.datapod)) {
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(), sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
				sourceTableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			} else if(sourceHolder.getRef().getType().equals(MetaType.dataset)) {
				DataSet dataSet = (DataSet) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(), sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
				MetaIdentifierHolder dependsOn = dataSet.getDependsOn();
				if(dependsOn.getRef().getType().equals(MetaType.datapod)) {
					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(), dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
					sourceTableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
				} else if(dependsOn.getRef().getType().equals(MetaType.relation)) {
					Relation relation = (Relation) sourceData;
					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(), relation.getDependsOn().getRef().getType().toString());
					sourceTableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
				}
			} else if(sourceHolder.getRef().getType().equals(MetaType.relation)) {
				Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(), sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(), relation.getDependsOn().getRef().getType().toString());
				sourceTableName = datapodServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			} else if(sourceHolder.getRef().getType().equals(MetaType.rule)) {
				Rule rule2 = (Rule) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(), sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
				sourceTableName = getTableNameBySource(rule2, runMode);
			}
		}
		return sourceTableName;
	}

}
