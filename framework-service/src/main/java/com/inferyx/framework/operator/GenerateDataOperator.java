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

import com.inferyx.framework.datascience.Math3Distribution;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Feature;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.OperatorType;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
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
	
	static final Logger logger = Logger.getLogger(GenerateDataOperator.class);

	/**
	 * 
	 */
	public GenerateDataOperator() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.Operator#execute(com.inferyx.framework.domain.OperatorType, com.inferyx.framework.domain.ExecParams, java.util.Map, java.util.HashMap, java.util.Set, com.inferyx.framework.domain.Mode)
	 */
	@Override
	public void execute(OperatorType operatorType, ExecParams execParams, Object metaExec,
			Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
	
		OperatorExec operatorExec = (OperatorExec) metaExec;
		
		ParamListHolder distributionInfo = paramSetServiceImpl.getParamByName(execParams, "distribution");
		ParamListHolder numIterationsInfo = paramSetServiceImpl.getParamByName(execParams, "numIterations");
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		
		int numIterations = Integer.parseInt(numIterationsInfo.getParamValue().getValue());
		MetaIdentifier datapodIdentifier = locationInfo.getAttributeInfo().get(0).getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodIdentifier.getUuid(), datapodIdentifier.getVersion(), datapodIdentifier.getType().toString());
		Distribution distribution = (Distribution) commonServiceImpl.getOneByUuidAndVersion(distributionInfo.getParamValue().getRef().getUuid(), distributionInfo.getParamValue().getRef().getVersion(), distributionInfo.getParamValue().getRef().getType().toString()); 
		
		List<ParamListHolder> distParamHolderList = new ArrayList<>();
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		ExecParams distExecParam = new ExecParams();

		List<ParamListHolder> paramListInfo = execParams.getParamListInfo();
		for(ParamListHolder holder : paramListInfo) {
			if(holder.getRef().getUuid().equalsIgnoreCase(distribution.getParamList().getRef().getUuid())) {
				distParamHolderList.add(holder);
			}
		}
		distExecParam.setParamListInfo(distParamHolderList);
		Object distributionObject = mlDistribution.getDistribution(distribution, distExecParam);
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		int attributeId = 0;
		for(Attribute attribute : locationDatapod.getAttributes()) {
//			for(AttributeRefHolder attributeRefHolder : locationInfo.getAttributeInfo())
//				if(attribute.getAttributeId().equals(Integer.parseInt(attributeRefHolder.getAttrId()))) {
					attribute.setAttributeId(attributeId);
					attributeId++;
					attributes.add(attribute);
//				}			
		}		
		
		String tableName = String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(), operatorExec.getVersion());
		ResultSetHolder resultSetHolder = exec.generateData(distributionObject, attributes, numIterations, operatorExec.getVersion());
		
		String filePath = "/"+locationDatapod.getUuid() + "/" + locationDatapod.getVersion() + "/" + operatorExec.getVersion();
		String fileName = String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(), operatorExec.getVersion());
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
		
		//dp.seta
		exec.registerAndPersist(resultSetHolder, tableName, filePath, locationDatapod, SaveMode.Append.toString(), commonServiceImpl.getApp().getUuid());
		
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, fileName, 
				new MetaIdentifier(MetaType.datapod, locationDatapod.getUuid(), locationDatapod.getVersion()) 
				, new MetaIdentifier(MetaType.operatorExec, operatorExec.getUuid(), operatorExec.getVersion()) ,
				operatorExec.getAppInfo(), operatorExec.getCreatedBy(), SaveMode.Append.toString(), resultRef, resultSetHolder.getCountRows(), null);

		operatorExec.setResult(resultRef);
		commonServiceImpl.save(MetaType.operatorExec.toString(), metaExec);
	}

}
