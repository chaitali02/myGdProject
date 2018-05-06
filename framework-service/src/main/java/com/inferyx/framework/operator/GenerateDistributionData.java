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
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.OperatorType;
import com.inferyx.framework.domain.ParamListHolder;
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
public class GenerateDistributionData implements Operator {
	
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
	
	static final Logger logger = Logger.getLogger(JoinTablesOperator.class);

	/**
	 * 
	 */
	public GenerateDistributionData() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.Operator#execute(com.inferyx.framework.domain.OperatorType, com.inferyx.framework.domain.ExecParams, java.util.Map, java.util.HashMap, java.util.Set, com.inferyx.framework.domain.Mode)
	 */
	@Override
	public void execute(OperatorType operatorType, ExecParams execParams, Object metaExec,
			Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams, Set<MetaIdentifier> usedRefKeySet, Mode runMode) throws Exception {
	
		OperatorExec operatorExec = (OperatorExec) metaExec;
		
		ParamListHolder distributionInfo = paramSetServiceImpl.getParamByName(execParams, "DISTRIBUTION_INFO");
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "LOCATION_INFO");
		ParamListHolder numIterationsInfo = paramSetServiceImpl.getParamByName(execParams, "NUMITERATIONS_INFO");
		
		int numIterations = Integer.parseInt(numIterationsInfo.getParamValue().getValue());
		MetaIdentifier datapodIdentifier = locationInfo.getAttributeInfo().get(0).getRef();
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodIdentifier.getUuid(), datapodIdentifier.getVersion(), datapodIdentifier.getType().toString());
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
		Object object = mlDistribution.getDistribution(distribution, distExecParam);
		
		List<Feature> features = new ArrayList<>();
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		int attributeId = 0;
		Attribute attribute2 = new Attribute();
		attribute2.setActive("Y");
		attribute2.setAttributeId(attributeId);
		attribute2.setDesc("id");
		attribute2.setDispName("id");
		attribute2.setName("id");
		attribute2.setPartition("N");
		attribute2.setType("integer");
		attributes.add(attribute2);
		attributeId++;
		for(Attribute attribute : datapod.getAttributes()) {
			for(AttributeRefHolder attributeRefHolder : locationInfo.getAttributeInfo())
				if(attribute.getAttributeId().equals(Integer.parseInt(attributeRefHolder.getAttrId()))) {
					Feature feature = new Feature();
					feature.setName(attribute.getName());
					features.add(feature);
					attribute.setAttributeId(attributeId);
					attributeId++;
					attributes.add(attribute);
				}			
		}
		

		Datapod dp = new Datapod();
		dp.setBaseEntity();
		dp.setName("distribution_"+datapod.getName());
		dp.setAttributes(attributes);
		dp.setDatasource(new MetaIdentifierHolder(new MetaIdentifier(MetaType.datasource, datasource.getUuid(), datasource.getVersion())));
		commonServiceImpl.save(MetaType.datapod.toString(), dp);
		
		String tabName = exec.generateFeatureData(object, features, numIterations, ("tempDistributionTable"));
		String sql = "SELECT * FROM " + tabName;
		
		String filePath = "/"+dp.getUuid() + "/" + dp.getVersion() + "/" + operatorExec.getVersion();
		String fileName = String.format("%s_%s_%s", dp.getUuid().replace("-", "_"), dp.getVersion(), operatorExec.getVersion());
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
		
		//dp.seta
		exec.executeAndPersist(sql, filePath, datapod, SaveMode.Append.toString(), commonServiceImpl.getApp().getUuid());
		
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, fileName, 
				new MetaIdentifier(MetaType.datapod, dp.getUuid(), dp.getVersion()) 
				, new MetaIdentifier(MetaType.operatorExec, operatorExec.getUuid(), operatorExec.getVersion()) ,
				operatorExec.getAppInfo(), operatorExec.getCreatedBy(), SaveMode.Append.toString(), resultRef);
		operatorExec.setResult(resultRef);
		commonServiceImpl.save(MetaType.operatorExec.toString(), metaExec);
	}

}
