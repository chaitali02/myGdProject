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

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.datascience.Math3Distribution;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
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
public class GenerateDataOperator implements Operator {
	
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	private Math3Distribution mlDistribution;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	private DatapodServiceImpl datapodServiceImpl;
	
	static final Logger logger = Logger.getLogger(GenerateDataOperator.class);

	/**
	 * 
	 */
	public GenerateDataOperator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, String> populateParams(com.inferyx.framework.datascience.Operator operator, ExecParams execParams,
			MetaIdentifier execIdentifier, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, List<String> datapodList, RunMode runMode) throws Exception {
		String execVersion = execIdentifier.getVersion();
		// Set destination
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		
		String newVersion = Helper.getVersion();
		locationDatapod.setVersion(newVersion);
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, runMode);
		otherParams.put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", tableName);
			
		return otherParams;
	}

	@Override
	public String parse(com.inferyx.framework.datascience.Operator operator, ExecParams execParams, MetaIdentifier execIdentifier,
			Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, List<String> datapodList, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.Operator#execute(com.inferyx.framework.domain.OperatorType, com.inferyx.framework.domain.ExecParams, java.util.Map, java.util.HashMap, java.util.Set, com.inferyx.framework.domain.Mode)
	 */
	@Override
	public String execute(com.inferyx.framework.datascience.Operator operator, ExecParams execParams, MetaIdentifier execIdentifier,
			Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
	
		String execUuid = execIdentifier.getUuid();
		String execVersion = execIdentifier.getVersion();
		
		ParamListHolder distributionInfo = paramSetServiceImpl.getParamByName(execParams, "distribution");
		ParamListHolder numIterationsInfo = paramSetServiceImpl.getParamByName(execParams, "numIterations");
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		int numIterations = Integer.parseInt(numIterationsInfo.getParamValue().getValue());
		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(), locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());
		Distribution distribution = (Distribution) commonServiceImpl.getOneByUuidAndVersion(distributionInfo.getParamValue().getRef().getUuid(), distributionInfo.getParamValue().getRef().getVersion(), distributionInfo.getParamValue().getRef().getType().toString()); 
		
		List<ParamListHolder> distParamHolderList = new ArrayList<>();
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		ExecParams distExecParam = new ExecParams();

		List<ParamListHolder> paramListInfo = execParams.getParamListInfo();
		for(ParamListHolder holder : paramListInfo) {
			if(holder.getRef().getUuid().equalsIgnoreCase(distribution.getParamList().getRef().getUuid()) 
					/*&& (!holder.getParamName().equalsIgnoreCase("distribution") 
							&& !holder.getParamName().equalsIgnoreCase("saveLocation" )
							&& !holder.getParamName().equalsIgnoreCase("numIterations" ))*/) {
				distParamHolderList.add(holder);
			}
		}
		distExecParam.setParamListInfo(distParamHolderList);
		Object distributionObject = mlDistribution.getDistribution(distribution, distExecParam);
		
		/*List<Attribute> attributes = new ArrayList<Attribute>();
		int attributeId = 0;
		for(Attribute attribute : locationDatapod.getAttributes()) {
//			for(AttributeRefHolder attributeRefHolder : locationInfo.getAttributeInfo())
//				if(attribute.getAttributeId().equals(Integer.parseInt(attributeRefHolder.getAttrId()))) {
					attribute.setAttributeId(attributeId);
					attributeId++;
					attributes.add(attribute);
//				}			
		}*/		
		
		/*String newVersion = Helper.getVersion();
		locationDatapod.setVersion(newVersion);
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, runMode);*/
		String tableName = otherParams.get("datapodUuid_" + locationDatapod.getUuid() + "_tableName");
		//String tableName = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(locationDatapod.getUuid(), locationDatapod.getVersion()), runMode);
		ResultSetHolder resultSetHolder = exec.generateData(distributionObject, locationDatapod.getAttributes(), numIterations, execVersion);
		
		String filePath = "/"+locationDatapod.getUuid() + "/" + locationDatapod.getVersion() + "/" + execVersion;
		String fileName = String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(), execVersion);
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
		
		exec.registerAndPersist(resultSetHolder, tableName, filePath, locationDatapod, SaveMode.Append.toString(), commonServiceImpl.getApp().getUuid());
		
		Object metaExec = commonServiceImpl.getOneByUuidAndVersion(execIdentifier.getUuid(), execIdentifier.getVersion(), execIdentifier.getType().toString());
		MetaIdentifierHolder createdBy = (MetaIdentifierHolder) metaExec.getClass().getMethod("getCreatedBy").invoke(metaExec);
		@SuppressWarnings("unchecked")
		List<MetaIdentifierHolder> appInfo = (List<MetaIdentifierHolder>) metaExec.getClass().getMethod("getAppInfo").invoke(metaExec);
		
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, fileName, 
				new MetaIdentifier(MetaType.datapod, locationDatapod.getUuid(), locationDatapod.getVersion()) 
				, new MetaIdentifier(MetaType.operatorExec, execUuid, execVersion) ,
				appInfo, createdBy, SaveMode.Append.toString(), resultRef, resultSetHolder.getCountRows(), null);
		
		metaExec.getClass().getMethod("setResult", MetaIdentifierHolder.class).invoke(metaExec, resultRef);
		commonServiceImpl.save(execIdentifier.getType().toString(), metaExec);
		return tableName;
	}

}
