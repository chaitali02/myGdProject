/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

/**
 * @author joy
 *
 */
public class CreateGraphOperator implements Operator {
	
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
	@Autowired
	private DatasetOperator datasetOperator;

	static final Logger logger = Logger.getLogger(CreateGraphOperator.class);

	/**
	 * 
	 */
	public CreateGraphOperator() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.domain.Parsable#parse(com.inferyx.framework.domain.BaseExec, com.inferyx.framework.domain.ExecParams, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.domain.Executable#execute(com.inferyx.framework.domain.BaseExec, com.inferyx.framework.domain.ExecParams, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		/******************* Fetch the node - START **********************/
		ParamListHolder nodeInfo = paramSetServiceImpl.getParamByName(execParams, "nodeLocation");

		MetaIdentifier nodeIdentifier = nodeInfo.getParamValue().getRef();
		Object nodeObj = commonServiceImpl.getOneByUuidAndVersion(nodeIdentifier.getUuid(),
				nodeIdentifier.getVersion(), nodeIdentifier.getType().toString());
		/******************* Fetch the node - END **********************/
		/******************* Fetch the edge - START **********************/
		ParamListHolder edgeInfo = paramSetServiceImpl.getParamByName(execParams, "edgeLocation");

		MetaIdentifier edgeIdentifier = edgeInfo.getParamValue().getRef();
		Object edgeObj = commonServiceImpl.getOneByUuidAndVersion(edgeIdentifier.getUuid(),
				edgeIdentifier.getVersion(), edgeIdentifier.getType().toString());
		/******************* Fetch the edge - END **********************/
		/******************* Fetch node column name - START ************************/
		ParamListHolder nodeColInfo = paramSetServiceImpl.getParamByName(execParams, "nodeColName");
		String nodeColName = nodeColInfo.getValue();
		/******************* Fetch node column name - END ************************/
		/******************* Fetch edge source column name - START ************************/
		ParamListHolder edgeSrcColInfo = paramSetServiceImpl.getParamByName(execParams, "edgeSrcColName");
		String edgeSrcColName = edgeSrcColInfo.getValue();
		/******************* Fetch edge source column name - END ************************/
		
		/******************* Fetch edge dest column name - START ************************/
		ParamListHolder edgeDstColInfo = paramSetServiceImpl.getParamByName(execParams, "edgeDstColName");
		String edgeDstColName = edgeDstColInfo.getValue();
		/******************* Fetch edge dest column name - END ************************/
		/******************* Create graph Frame - START **********************/
		// Rename node column name to 'id'
		// Rename source edge column name to 'src'
		// Rename destination edge column name to 'dst'
		// Create GraphFrame
		/******************* Create graph Frame - END **********************/
		/******************* Fetch the save location - START **********************/
		// Need to design save location and mode
		/******************* Fetch the save location - END **********************/
		/******************* Save graph Frame - START **********************/
		
		/******************* Save graph Frame - END **********************/
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.Operator#create(com.inferyx.framework.domain.BaseExec, com.inferyx.framework.domain.ExecParams, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public Map<String, String> create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		String execVersion = baseExec.getVersion();
		Map<String, String> otherParams = execParams.getOtherParams();
		String tableName = null;
		/*************** Input Node - START ****************/
		// Input datapod/dataset/rule
		ParamListHolder nodeInfo = paramSetServiceImpl.getParamByName(execParams, "nodeLocation");

		MetaIdentifier nodeIdentifier = nodeInfo.getParamValue().getRef();
		Object nodeObj = commonServiceImpl.getOneByUuidAndVersion(nodeIdentifier.getUuid(),
				nodeIdentifier.getVersion(), nodeIdentifier.getType().toString());

		if (otherParams.containsKey("datapodUuid_" + nodeIdentifier.getUuid() + "_tableName")) {
			tableName = otherParams.get("datapodUuid_" + nodeIdentifier.getUuid() + "_tableName");
		} else {
//			tableName = getTableNameBySource(nodeObj, runMode);
			otherParams.put("datapodUuid_" + nodeIdentifier.getUuid() + "_tableName", tableName);
		}
		/*************** Input Node - END ****************/
		
		/*************** Input Edge - START ****************/
		// Input datapod/dataset/rule
		ParamListHolder edgeInfo = paramSetServiceImpl.getParamByName(execParams, "nodeLocation");

		MetaIdentifier edgeIdentifier = edgeInfo.getParamValue().getRef();
		Object edgeObj = commonServiceImpl.getOneByUuidAndVersion(edgeIdentifier.getUuid(),
				edgeIdentifier.getVersion(), edgeIdentifier.getType().toString());

		if (otherParams.containsKey("datapodUuid_" + edgeIdentifier.getUuid() + "_tableName")) {
			tableName = otherParams.get("datapodUuid_" + edgeIdentifier.getUuid() + "_tableName");
		} else {
	//		tableName = getTableNameBySource(edgeObj, runMode);
			otherParams.put("datapodUuid_" + edgeIdentifier.getUuid() + "_tableName", tableName);
		}
		/*************** Input Edge - END ****************/
		
		
		/*************** Define the save location - START ****************/
		/*************** Define the save location - END ****************/
		return null;
	}

}
