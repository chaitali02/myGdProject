package com.inferyx.framework.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.connector.RConnector;
import com.inferyx.framework.datascience.Operator;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.executor.RExecutor;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.ModelExecServiceImpl;
import com.inferyx.framework.service.ModelServiceImpl;
import com.inferyx.framework.service.OperatorServiceImpl;


@RestController
@RequestMapping(value = "/operator")
public class OperatorController {
	
	
	@Autowired
	private OperatorServiceImpl operatorServiceImpl;
	@Autowired
	RConnector rConnector;
	@Autowired
	RExecutor rExecutor;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	@RequestMapping(value = "/execute", method = RequestMethod.POST)
	public boolean operator(@RequestParam("uuid") String operatorUuid, 
			@RequestParam("version") String operatorVersion,
			@RequestBody(required = false) ExecParams execParams,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "mode", required = false, defaultValue = "ONLINE") String mode) throws Exception {
		try {
			Operator operator = (Operator) commonServiceImpl.getOneByUuidAndVersion(operatorUuid, operatorVersion,
					MetaType.operator.toString());
			OperatorExec operatorExec = null;
			operatorExec = operatorServiceImpl.create(operator, execParams, null, operatorExec);
			return operatorServiceImpl.operator(operator, execParams, operatorExec);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@RequestMapping(value = "/getResults", method = RequestMethod.GET)
	List<Map<String, Object>> getOperatorResults(@RequestParam("uuid") String operatorExecUuid,
			@RequestParam("version") String operatorExecVersion,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "rowLimit", required = false, defaultValue = "1000") int rowLimit) throws Exception {
		rowLimit = Integer.parseInt(Helper.getPropertyValue("framework.result.row.limit"));
		return operatorServiceImpl.getOperatorResults(operatorExecUuid, operatorExecVersion, rowLimit);
	}

}
