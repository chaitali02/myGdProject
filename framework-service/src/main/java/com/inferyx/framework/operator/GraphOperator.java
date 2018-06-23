/**
 * 
 */
package com.inferyx.framework.operator;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.GraphEdge;
import com.inferyx.framework.domain.GraphNode;
import com.inferyx.framework.domain.Graphpod;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;

/**
 * @author joy
 *
 */
@Service
public class GraphOperator implements IOperator {
	
	@Autowired
	CommonServiceImpl commonServiceImpl;
	@Autowired
	AttributeMapOperator attributeMapOperator;
	@Autowired
	MetadataUtil daoRegister;

	/**
	 * 
	 */
	public GraphOperator() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.domain.Parsable#parse(com.inferyx.framework.domain.BaseExec, com.inferyx.framework.domain.ExecParams, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// Fetch graphPod - START
		Graphpod graphPod = (Graphpod) commonServiceImpl.getOneByUuidAndVersion(baseExec.getDependsOn().getRef().getUuid(), baseExec.getDependsOn().getRef().getVersion(), MetaType.graphpod.toString());
		// Fetch graphPod - END
		// Get node 
		List<GraphNode> nodeList = graphPod.getNodeInfo();
		List<GraphEdge> edgeList = graphPod.getEdgeInfo();
		String nodeSql = createNodeSql(nodeList, execParams);
		String edgeSql = createEdgeSql(edgeList, execParams);
		baseExec.setExec(nodeSql.concat("|||").concat(edgeSql));
		return baseExec;
	}

	/**
	 * 
	 * @param nodeList
	 * @return
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws JsonProcessingException 
	 */
	private String createNodeSql(List<GraphNode> nodeList, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if (nodeList == null || nodeList.isEmpty()) {
			return null;
		}
		String nodeSql = null;
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (GraphNode graphNode : nodeList) {
			if (count > 0) {
				sb.append(" UNION ALL ");
			}
			sb.append(ConstantsUtil.SELECT);
			sb.append(" ");
			// Fetch id attribute 
			AttributeRefHolder nodeIdRefHolder = graphNode.getNodeId();
			sb.append(attributeMapOperator.sourceAttrSql(daoRegister, nodeIdRefHolder, nodeIdRefHolder, 
												DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
												execParams.getOtherParams(), execParams));
			sb.append(" AS id, ");
			AttributeRefHolder nodeNameRefHolder =  graphNode.getNodeName();
			sb.append(attributeMapOperator.sourceAttrSql(daoRegister, nodeNameRefHolder, nodeNameRefHolder, 
					DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
					execParams.getOtherParams(), execParams));
			sb.append(" AS ");
			sb.append(attributeMapOperator.sourceAttrAlias(daoRegister, nodeNameRefHolder, nodeNameRefHolder, 
					DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
					execParams.getOtherParams()));
			sb.append(", ");
			for (AttributeRefHolder propHolder : graphNode.getNodeProperties()) {
				sb.append(attributeMapOperator.sourceAttrSql(daoRegister, propHolder, propHolder, 
						DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
						execParams.getOtherParams(), execParams));
				sb.append(" AS ");
				sb.append(attributeMapOperator.sourceAttrAlias(daoRegister, propHolder, propHolder, 
						DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
						execParams.getOtherParams()));
				sb.append(", ");
			}
			sb.append(ConstantsUtil.FROM);
			BaseExec sourceExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(nodeIdRefHolder.getRef().getUuid(), nodeIdRefHolder.getRef().getVersion(), nodeIdRefHolder.getRef().getType().toString());
			sb.append(" ").append(sourceExec.getName()).append(" ");
			count++;
		}
		nodeSql = sb.toString().replaceAll(",  FROM", " FROM");
		return nodeSql;
	}
	
	/**
	 * 
	 * @param edgeList
	 * @param execParams
	 * @return
	 * @throws JsonProcessingException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NullPointerException
	 * @throws ParseException
	 */
	private String createEdgeSql(List<GraphEdge> edgeList, ExecParams execParams) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		if (edgeList == null || edgeList.isEmpty()) {
			return null;
		}
		String edgeSql = null;
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (GraphEdge graphEdge : edgeList) {
			if (count > 0) {
				sb.append(" UNION ALL ");
			}
			sb.append(ConstantsUtil.SELECT);
			sb.append(" ");
			// Fetch id attribute 
			AttributeRefHolder sourceNodeIdRefHolder = graphEdge.getSourceNodeId();
			sb.append(attributeMapOperator.sourceAttrSql(daoRegister, sourceNodeIdRefHolder, sourceNodeIdRefHolder, 
												DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
												execParams.getOtherParams(), execParams));
			sb.append(" AS src, ");
			AttributeRefHolder targetNodeIdRefHolder =  graphEdge.getTargetNodeId();
			sb.append(attributeMapOperator.sourceAttrSql(daoRegister, targetNodeIdRefHolder, targetNodeIdRefHolder, 
					DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
					execParams.getOtherParams(), execParams));
			sb.append(" AS dst, ");
			for (AttributeRefHolder propHolder : graphEdge.getEdgeProperties()) {
				sb.append(attributeMapOperator.sourceAttrSql(daoRegister, propHolder, propHolder, 
						DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
						execParams.getOtherParams(), execParams));
				sb.append(" AS ");
				sb.append(attributeMapOperator.sourceAttrAlias(daoRegister, propHolder, propHolder, 
						DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
						execParams.getOtherParams()));
				sb.append(", ");
			}
			sb.append(ConstantsUtil.FROM);
			BaseExec sourceExec = (BaseExec) commonServiceImpl.getOneByUuidAndVersion(sourceNodeIdRefHolder.getRef().getUuid(), sourceNodeIdRefHolder.getRef().getVersion(), sourceNodeIdRefHolder.getRef().getType().toString());
			sb.append(" ").append(sourceExec.getName()).append(" ");
			count++;
		}
		edgeSql = sb.toString().replaceAll(",  FROM", " FROM");
		return edgeSql;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.domain.Executable#execute(com.inferyx.framework.domain.BaseExec, com.inferyx.framework.domain.ExecParams, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.inferyx.framework.operator.Operator#create(com.inferyx.framework.domain.BaseExec, com.inferyx.framework.domain.ExecParams, com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public Map<String, String> create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
