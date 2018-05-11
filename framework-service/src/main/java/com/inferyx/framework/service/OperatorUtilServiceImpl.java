package com.inferyx.framework.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.datascience.Operator;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.OperatorType;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamInfo;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ParamSet;

@Service
public class OperatorUtilServiceImpl {

	@Autowired
	CommonServiceImpl<?> commonServiceImpl;

	public OperatorUtilServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	public List<Param> getParamList(Operator operator, ExecParams execParams) throws JsonProcessingException {
		Param param = null;
		List<Param> newParamList = new ArrayList<>();
		ParamSet paramSet = null;//execParams.getParamSetHolder().getRef();
		
		List<ParamInfo> paramInfoList = paramSet.getParamInfo();
		OperatorType operatorType = (OperatorType) commonServiceImpl.getOneByUuidAndVersion(
				operator.getOperatorType().getRef().getUuid(), operator.getOperatorType().getRef().getVersion(),
				operator.getOperatorType().getRef().getType().toString());
		// Get paramList
		ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(
				operatorType.getParamList().getRef().getUuid(), operatorType.getParamList().getRef().getVersion(),
				operatorType.getParamList().getRef().getType().toString());
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
	}
	
	public ExecParams getExecParams (com.inferyx.framework.domain.Operator operator) {
		if (operator == null 
				|| operator.getOperatorParams() == null 
				|| !operator.getOperatorParams().containsKey(ConstantsUtil.EXEC_PARAMS)
				|| operator.getOperatorParams().get(ConstantsUtil.EXEC_PARAMS) == null) {
			return null;
		}
		return (ExecParams) operator.getOperatorParams().get(ConstantsUtil.EXEC_PARAMS);
	}
	
}
