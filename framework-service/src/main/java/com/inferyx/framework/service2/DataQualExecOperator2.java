/**
 * 
 */
package com.inferyx.framework.service2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.factory2.ExecFactory2;
import com.inferyx.framework.operator.DQOperator;
import com.inferyx.framework.operator.IOperator;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataQualServiceImpl;
import com.inferyx.framework.service.MessageStatus;

/**
 * @author joy
 *
 */
@Service
public class DataQualExecOperator2 implements IOperator {
	
	@Autowired
	ExecFactory2 execFactory2;
	@Autowired
	CommonServiceImpl commonServiceImpl;
	@Autowired
	DQOperator dqOperator;
	@Autowired
	DataQualServiceImpl dataQualServiceImpl;

	static final Logger logger = Logger.getLogger(DataQualExecOperator2.class);
	/**
	 * 
	 */
	public DataQualExecOperator2() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		logger.info("Inside DataQualExecOperator2.parse");
		DataQual dataQual = null;
		Set<MetaIdentifier> usedRefKeySet = new HashSet<>();
		DataQualExec dataQualExec = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(baseExec.getUuid(), baseExec.getVersion(), MetaType.dqExec.toString());
		dataQual = (DataQual) commonServiceImpl.getOneByUuidAndVersion(dataQualExec.getDependsOn().getRef().getUuid(), dataQualExec.getDependsOn().getRef().getVersion(), MetaType.dq.toString());
		try{
		dataQualExec.setExec(dqOperator.generateSql(dataQual, null, dataQualExec, null, usedRefKeySet, execParams.getOtherParams(), runMode, null));
		dataQualExec.setRefKeyList(new ArrayList<>(usedRefKeySet));
		
		synchronized (dataQualExec.getUuid()) {
			DataQualExec dataQualExec1 = (DataQualExec) commonServiceImpl.getOneByUuidAndVersion(baseExec.getUuid(), baseExec.getVersion(), MetaType.dqExec.toString());
			dataQualExec1.setExec(dataQualExec.getExec());
			dataQualExec1.setRefKeyList(dataQualExec.getRefKeyList());
			commonServiceImpl.save(MetaType.dqExec.toString(), dataQualExec1);
			dataQualExec1 = null;
		}
		}catch(Exception e){
			commonServiceImpl.setMetaStatus(dataQualExec, MetaType.dqExec, Status.Stage.FAILED);
			e.printStackTrace();
			String message = null;
			message = e.getMessage();
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.dqExec, dataQualExec.getUuid(), dataQualExec.getVersion()));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "FAILED data quality parsing.", dependsOn);
			throw new Exception((message != null) ? message : "FAILED data quality parsing.");
		}
		return dataQualExec;
	}

	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		return dataQualServiceImpl.execute(baseExec, execParams, runMode);
	}

	@Override
	public BaseExec create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// Fetch Map
		BaseEntity baseEntity = (BaseEntity) commonServiceImpl.getOneByUuidAndVersion(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), baseExec.getDependsOn().getRef().getType().toString());
		if (StringUtils.isBlank(baseExec.getUuid())) {
			baseExec = execFactory2.getExec(MetaType.dqExec, baseExec.getDependsOn().getRef());
		}
		baseExec.setAppInfo(baseEntity.getAppInfo());
		baseExec.setExecParams(execParams);
		baseExec.setName(baseEntity.getName());
		// Decide later whether populate ref key is at all required
		return baseExec;
	}

	@Override
	public Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
