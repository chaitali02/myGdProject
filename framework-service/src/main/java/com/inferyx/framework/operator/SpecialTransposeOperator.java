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
import com.inferyx.framework.datascience.Math3Distribution;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.OperatorType;
import com.inferyx.framework.domain.OrderKey;
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
public class SpecialTransposeOperator implements Operator {
	
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	private ExecutorFactory execFactory;
	@Autowired
	private DataStoreServiceImpl dataStoreServiceImpl;
	
	static final Logger logger = Logger.getLogger(SpecialTransposeOperator.class);

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
	public SpecialTransposeOperator() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.Operator#execute(com.inferyx.framework.domain.OperatorType, com.inferyx.framework.domain.ExecParams, java.lang.Object, java.util.Map, java.util.HashMap, java.util.Set, com.inferyx.framework.domain.Mode)
	 */
	@Override
	public void execute(OperatorType operatorType, ExecParams execParams, Object metaExec,
			Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, Mode runMode) throws Exception {
		logger.info("Inside SpecialTransposeOperator.execute");
		StringBuilder sb = new StringBuilder();
		
		OperatorExec operatorExec = (OperatorExec) metaExec;
		
		ParamListHolder transposeDataset = paramSetServiceImpl.getParamByName(execParams, "transpose_dataset");
		ParamListHolder keyParam = paramSetServiceImpl.getParamByName(execParams, "key");
		
		MetaIdentifier datapodIdentifier = transposeDataset.getAttributeInfo().get(0).getRef();
		Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodIdentifier.getUuid(), datapodIdentifier.getVersion(), datapodIdentifier.getType().toString());
//		String keyAttr = keyParam.getValue();
		
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		
		String tableName = dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
		
		List<AttributeRefHolder> attrRefHolders = transposeDataset.getAttributeInfo();
		List<Attribute> attrList = new ArrayList<>();
		Attribute keyAttr = datapod.getAttribute(Integer.parseInt(keyParam.getAttributeInfo().get(0).getAttrId()));
		for (AttributeRefHolder attrRefHolder : attrRefHolders) {
			attrList.add(datapod.getAttribute(Integer.parseInt(attrRefHolder.getAttrId())));
		}
		
		// Get the fieldArray
		boolean isAttrFound = false;
		sb.append(ConstantsUtil.SELECT);
		sb.append(keyAttr.getName());
		sb.append(", instrument, trialVal FROM (");
		sb.append(ConstantsUtil.SELECT);
		sb.append(keyAttr.getName());
		sb.append(", MAP (");
		int count = 0;
		for(Attribute attribute : attrList) {
			isAttrFound = Boolean.TRUE;
			sb.append("'"+attribute.getName() + "', " + attribute.getName());
			sb.append((count < attrList.size()-1)?", ":"");
			count++;
			
		}
		sb.append(") AS tmp_column FROM ");
		sb.append(tableName);
		sb.append("  ) x LATERAL VIEW EXPLODE(tmp_column) exptbl AS instrument, trialVal ");

		attrList = new ArrayList<>();
		attrList.add(datapod.getAttribute(Integer.parseInt(keyParam.getAttributeInfo().get(0).getAttrId())));
		Attribute attr = new Attribute();
		attr.setAttributeId(1);
		attr.setName("instrument");
		attr.setDesc("instrument");
		attr.setDispName("instrument");
		attr.setType("string");
		attr.setPartition("Y");
		attr.setActive("Y");
		attrList.add(attr);
		
		attr = new Attribute();
		attr.setAttributeId(2);
		attr.setName("trialVal");
		attr.setDesc("trialVal");
		attr.setDispName("trialVal");
		attr.setType("double");
		attr.setPartition("N");
		attr.setActive("Y");
		attrList.add(attr);
		
		Datapod dp = new Datapod();
		dp.setBaseEntity();
		dp.setName("distribution_"+datapod.getName());
		dp.setAttributes(attrList);
		dp.setDatasource(new MetaIdentifierHolder(new MetaIdentifier(MetaType.datasource, datasource.getUuid(), datasource.getVersion())));
		commonServiceImpl.save(MetaType.datapod.toString(), dp);
		
		String filePath = "/"+dp.getUuid() + "/" + dp.getVersion() + "/" + operatorExec.getVersion();
		String fileName = String.format("%s_%s_%s", dp.getUuid().replace("-", "_"), dp.getVersion(), operatorExec.getVersion());
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
		
		exec.executeAndPersist(sb.toString(), filePath, dp, SaveMode.Append.toString(), commonServiceImpl.getApp().getUuid());
		
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, fileName, 
				new MetaIdentifier(MetaType.datapod, dp.getUuid(), dp.getVersion()) 
				, new MetaIdentifier(MetaType.operatorExec, operatorExec.getUuid(), operatorExec.getVersion()) ,
				operatorExec.getAppInfo(), operatorExec.getCreatedBy(), SaveMode.Append.toString(), resultRef);
		operatorExec.setResult(resultRef);
		commonServiceImpl.save(MetaType.operatorExec.toString(), metaExec);
	}

}
