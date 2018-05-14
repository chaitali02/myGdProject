/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.OperatorType;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.OperatorUtilServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

/**
 * @author joy
 *
 */
@Service
public class JoinTablesOperator implements com.inferyx.framework.operator.Operator {
	
	@Autowired
	CommonServiceImpl commonServiceImpl;
	@Autowired
	OperatorUtilServiceImpl operatorUtilServiceImpl;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	RelationOperator relationOperator;
	
	static final Logger logger = Logger.getLogger(JoinTablesOperator.class);
	
	/**
	 * 
	 */
	public JoinTablesOperator() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String execute(OperatorType operatorType, 
						ExecParams execParams, 
						MetaIdentifier execIdentifier, 
						java.util.Map<String, MetaIdentifier> refKeyMap, 
						HashMap<String, String> otherParams, 
						Set<MetaIdentifier> usedRefKeySet, RunMode runMode) throws Exception {
//		OperatorType operatorType = (OperatorType) commonServiceImpl.getOneByUuidAndVersion(operator.getOperatorType().getRef().getUuid(), operator.getOperatorType().getRef().getVersion(), operator.getOperatorType().getRef().getType().toString());
		ParamListHolder relationIdentifier = paramSetServiceImpl.getParamByName(execParams, "RELATION");
		Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(relationIdentifier.getParamValue().getRef().getUuid(), relationIdentifier.getParamValue().getRef().getVersion(), relationIdentifier.getParamValue().getRef().getType().toString());
//		String joinTableName = paramSetServiceImpl.getParamByName(execParams, "JOIN_TABLE_NAME").getValue();
		String sql = relationOperator.generateSql(relation, refKeyMap, otherParams, execParams, usedRefKeySet, runMode);
		logger.info("Inside JoinTablesOperator relation sql : " + sql);
		return null;
	}

	@Override
	public Map<String, String> populateParams(OperatorType operatorType, ExecParams execParams,
			MetaIdentifier execIdentifier, Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, List<String> datapodList, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parse(OperatorType operatorType, ExecParams execParams, MetaIdentifier execIdentifier,
			Map<String, MetaIdentifier> refKeyMap, HashMap<String, String> otherParams,
			Set<MetaIdentifier> usedRefKeySet, List<String> datapodList, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
