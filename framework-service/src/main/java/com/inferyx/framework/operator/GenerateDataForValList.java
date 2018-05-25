/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.datascience.Math3Distribution;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.GenVal;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.ParamListHolder;
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
public class GenerateDataForValList extends GenerateDataOperator {
	
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
	
	static final Logger logger = Logger.getLogger(GenerateDataForValList.class);

	/**
	 * 
	 */
	public GenerateDataForValList() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, String> populateParams(com.inferyx.framework.domain.Operator operator,
			ExecParams execParams, MetaIdentifier execIdentifier, Map<String, MetaIdentifier> refKeyMap,
			HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, List<String> datapodList,
			RunMode runMode) throws Exception {
		String execUuid = execIdentifier.getUuid();
		String execVersion = execIdentifier.getVersion();
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
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, runMode);
		otherParams.put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", tableName);
		otherParams.put("datapodUuid_" + "valList" + "_tableName", valTableName);
			
		return otherParams;
	}

	@Override
	public String parse(com.inferyx.framework.domain.Operator operator, ExecParams execParams,
			MetaIdentifier execIdentifier, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, List<String> datapodList, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public String execute(com.inferyx.framework.domain.Operator operator, ExecParams execParams,
			MetaIdentifier execIdentifier, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
		String execUuid = execIdentifier.getUuid();
		String execVersion = execIdentifier.getVersion();
		int numRepetitions = 0;
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		
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
		ResultSetHolder resultSetHolder = exec.executeAndRegister(rangeSql, tableName, datasource.getType());
		
		// save result
		save(exec, resultSetHolder, tableName, locationDatapod, execIdentifier, runMode);
		
		return tableName;
	}

	/*@Override
	public String execute(com.inferyx.framework.datascience.Operator operator, ExecParams execParams,
			MetaIdentifier execIdentifier, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
		String execUuid = execIdentifier.getUuid();
		String execVersion = execIdentifier.getVersion();
		int numRepetitions = 0;
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		
		ParamListHolder numIterationsInfo = paramSetServiceImpl.getParamByName(execParams, "numIterations");
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		int numIterations = Integer.parseInt(numIterationsInfo.getParamValue().getValue());
		
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());

		// Get the attribute 
		String attributeName = "id";
		String attrTableName = otherParams.get("datapodUuid_" + "valList" + "_tableName");
		String countSql = "select count("+attributeName+") as " + attributeName + " from " + attrTableName;
		// Get number of customers
		List<Map<String, Object>> dataList = exec.executeAndFetch(countSql, commonServiceImpl.getApp().getUuid());
		// retrieve attribute
		if (dataList == null || dataList.isEmpty() || dataList.get(0) == null || dataList.get(0).get(attributeName) == null) {
			numRepetitions = 0;
		} else {
			numRepetitions = Integer.parseInt(""+dataList.get(0).get(attributeName));
		}
		
		// Get resolved numIterations
		numRepetitions = getResolvedIterations(numIterations, numRepetitions);
		
		Object distributionObject = getDistributionObject(execParams, numRepetitions, execVersion, otherParams);
		String tableName = otherParams.get("datapodUuid_" + locationDatapod.getUuid() + "_tableName");
		
		// Generate Data 
		ResultSetHolder resultSetHolder = exec.generateData(distributionObject, locationDatapod.getAttributes(), numRepetitions, execVersion, tableName);
		
		// Generate Bucket Id
		String bucketsSql = getBucketsSql(numIterations, resultSetHolder);
		resultSetHolder = exec.executeAndRegister(bucketsSql, tableName, datasource.getType());
		
		// Generate sequence from instrument sql
		String seqSql = getInstrumentSql(attrTableName);
		resultSetHolder = exec.executeAndRegister(bucketsSql, attrTableName, datasource.getType());
		
		// Generate Data
		String genDataSql = genDataSql(attrTableName, tableName);
		resultSetHolder = exec.executeAndRegister(genDataSql, tableName, datasource.getType());
		
		// save result
		save(exec, resultSetHolder, tableName, locationDatapod, execIdentifier, runMode);
		
		return tableName;
	}
*/	
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
