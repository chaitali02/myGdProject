/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.spark.sql.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OperatorType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.ResultSetHolder;
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
public class TransposeOperator implements Operator {
	
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

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.Operator#execute(com.inferyx.framework.domain.OperatorType, com.inferyx.framework.domain.ExecParams, java.lang.Object, java.util.Map, java.util.HashMap, java.util.Set, com.inferyx.framework.domain.Mode)
	 */
	@Override
	public String execute(OperatorType operatorType, ExecParams execParams, MetaIdentifier execIdentifier,
			Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
		logger.info("Executing TransposeOperator");
		StringBuilder sb = new StringBuilder();
		
		String execUuid = execIdentifier.getUuid();
		String execVersion = execIdentifier.getVersion();
		
		//OperatorExec operatorExec = (OperatorExec) execIdentifier;
		
		ParamListHolder sourceDatapodInfo = paramSetServiceImpl.getParamByName(execParams, "sourceDatapod");
		ParamListHolder keyInfo = paramSetServiceImpl.getParamByName(execParams, "key");
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		//MetaIdentifier sourceDatapodIdentifier = sourceDatapodInfo.getAttributeInfo().get(0).getRef();
		//Datapod sourceDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceDatapodIdentifier.getUuid(), sourceDatapodIdentifier.getVersion(), sourceDatapodIdentifier.getType().toString());
		
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		
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
		Object sourceData = commonServiceImpl.getOneByUuidAndVersion(sourceDataIdentifier.getUuid(), sourceDataIdentifier.getVersion(), sourceDataIdentifier.getType().toString());
		List<String> attrList = getColumnNameList(sourceData, sourceDatapodInfo);
		sourceTableName = getTableNameBySource(sourceData, runMode);
		
		MetaIdentifier keyIdentifier = keyInfo.getAttributeInfo().get(0).getRef();
		Object key = commonServiceImpl.getOneByUuidAndVersion(keyIdentifier.getUuid(), keyIdentifier.getVersion(), keyIdentifier.getType().toString());
		List<String> keyAttrList = getColumnNameList(key, keyInfo);
		
		
		//String version = operatorExec.getVersion();
		
		// Get the fieldArray
		boolean isAttrFound = false;
		sb.append(ConstantsUtil.SELECT);
		
		/*for (Attribute attribute : keyAttrList) {
			sb.append(attribute.getName()).append(", ");
		}*/
		for (String columnName : keyAttrList) {
			sb.append(columnName).append(", ");
		}
		
		sb.append("tranpose_column, transpose_value, " + execVersion + " version FROM (");
		sb.append(ConstantsUtil.SELECT);
		
		/*for (Attribute attribute : keyAttrList) {
			sb.append(attribute.getName()).append(", ");
		}*/
		for (String columnName : keyAttrList) {
			sb.append(columnName).append(", ");
		}
		
		sb.append(" MAP (");
		int count = 0;
		/*for(Attribute attribute : attrList) {
			isAttrFound = Boolean.TRUE;
			sb.append("'"+attribute.getName() + "', " + attribute.getName());
			sb.append((count < attrList.size()-1)?", ":"");
			count++;
			
		}*/
		for(String columnName : attrList) {
			isAttrFound = Boolean.TRUE;
			sb.append("'"+columnName + "', " + columnName);
			sb.append((count < attrList.size()-1)?", ":"");
			count++;
			
		}
		
		sb.append(") AS tmp_column FROM ");
		sb.append(sourceTableName);
		sb.append("  ) x LATERAL VIEW EXPLODE(tmp_column) exptbl AS tranpose_column, transpose_value ");
		String sql = sb.toString();
		logger.info("my sql: "+sql);
		
		String filePath = "/"+locationDatapod.getUuid() + "/" + locationDatapod.getVersion() + "/" + execVersion;
		String fileName = String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(), execVersion);
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
		
		String newVersion = Helper.getVersion();
		locationDatapod.setVersion(newVersion);
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, runMode);
		
		ResultSetHolder resultSetHolder = exec.executeRegisterAndPersist(sql, tableName, filePath, locationDatapod, SaveMode.Append.toString(), commonServiceImpl.getApp().getUuid());
		
		Object metaExec = commonServiceImpl.getOneByUuidAndVersion(execIdentifier.getUuid(), execIdentifier.getVersion(), execIdentifier.getType().toString());
		MetaIdentifierHolder createdBy = (MetaIdentifierHolder) metaExec.getClass().getMethod("getCreatedBy").invoke(metaExec);
		@SuppressWarnings("unchecked")
		List<MetaIdentifierHolder> appInfo = (List<MetaIdentifierHolder>) metaExec.getClass().getMethod("getAppInfo").invoke(metaExec);
		
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, fileName, 
				new MetaIdentifier(MetaType.datapod, locationDatapod.getUuid(), locationDatapod.getVersion()) 
				, new MetaIdentifier(execIdentifier.getType(), execUuid, execVersion) ,
				appInfo, createdBy, SaveMode.Append.toString(), resultRef, resultSetHolder.getCountRows(), null);
		
		metaExec.getClass().getMethod("setResult", MetaIdentifierHolder.class).invoke(metaExec, resultRef);
		commonServiceImpl.save(execIdentifier.getType().toString(), metaExec);
		return tableName;
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
