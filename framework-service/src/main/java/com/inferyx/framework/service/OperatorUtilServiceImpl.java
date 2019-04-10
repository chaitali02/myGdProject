package com.inferyx.framework.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.Operator;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamInfo;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ParamSet;

@Service
public class OperatorUtilServiceImpl {


	public OperatorUtilServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	/****************************Unused********************************/
	/*public List<Param> getParamList(Operator operator, ExecParams execParams) throws JsonProcessingException {
		Param param = null;
		List<Param> newParamList = new ArrayList<>();
		ParamSet paramSet = null;//execParams.getParamSetHolder().getRef();
		
		List<ParamInfo> paramInfoList = paramSet.getParamInfo();
		// Get paramList
		ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(
				operator.getParamList().getRef().getUuid(), operator.getParamList().getRef().getVersion(),
				operator.getParamList().getRef().getType().toString());
		for (ParamInfo paramInfo : paramInfoList) {
			for (ParamListHolder paramListHolder : paramInfo.getParamSetVal()) {
				param = paramList.getParams().get(Integer.parseInt(paramListHolder.getParamId()));
				MetaIdentifierHolder miHolder = new MetaIdentifierHolder();
				miHolder.setValue(paramListHolder.getValue());
				param.setParamValue(miHolder);
				newParamList.add(param);
			}
		}
		return newParamList;
	}*/
	
	/****************************Unused*************************/
	/*public ExecParams getExecParams (com.inferyx.framework.domain.TaskOperator operator) {
		if (operator == null 
				|| operator.getOperatorParams() == null 
				|| !operator.getOperatorParams().containsKey(ConstantsUtil.EXEC_PARAMS)
				|| operator.getOperatorParams().get(ConstantsUtil.EXEC_PARAMS) == null) {
			return null;
		}
		return (ExecParams) operator.getOperatorParams().get(ConstantsUtil.EXEC_PARAMS);
	}*/
	
}
