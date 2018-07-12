/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataType;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.GraphEdge;
import com.inferyx.framework.domain.GraphNode;
import com.inferyx.framework.domain.Graphpod;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Property;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;

/**
 * @author joy
 *
 */
@Service
public class GraphOperator implements IOperator {
	
	@Autowired
	CommonServiceImpl commonServiceImpl;
	@Autowired
	DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	AttributeMapOperator attributeMapOperator;
	@Autowired
	MetadataUtil daoRegister;
	
	private static final Logger logger = Logger.getLogger(GraphOperator.class);

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
		String nodeSql = createNodeSql(nodeList, execParams, baseExec, runMode);
		String edgeSql = createEdgeSql(edgeList, execParams, baseExec, runMode);
		baseExec.setExec(nodeSql.concat("|||").concat(edgeSql));
		logger.info(" Sqls : " + baseExec.getExec());
		commonServiceImpl.save(MetaType.graphExec.toString(), baseExec);
		return baseExec;
	}

	/**
	 * 
	 * @param nodeList
	 * @return
	 * @throws Exception 
	 */
	private String createNodeSql(List<GraphNode> nodeList, ExecParams execParams, BaseExec baseExec, RunMode runMode) throws Exception {
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
					DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
					execParams));
			sb.append(" AS id, ");

			sb.append(count + " AS nodeIndex, ");

			AttributeRefHolder nodeNameRefHolder = graphNode.getNodeName();
			sb.append(attributeMapOperator.sourceAttrSql(daoRegister, nodeNameRefHolder, nodeNameRefHolder,
					DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
					execParams));
			sb.append(" AS nodeName, '");

			/*
			 * sb.append(attributeMapOperator.sourceAttrAlias(daoRegister,
			 * nodeNameRefHolder, nodeNameRefHolder,
			 * DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()),
			 * execParams.getOtherParams())); sb.append(", ");
			 */

			sb.append(graphNode.getNodeType());
			sb.append("' AS nodeType, '");

			sb.append(graphNode.getNodeSize());
			sb.append("' AS nodeSize, '");

			sb.append(graphNode.getNodeIcon());
			sb.append("' AS nodeIcon, ");

			Boolean status = true;

			sb.append("concat('{', ");
			for (AttributeRefHolder propHolder : graphNode.getNodeProperties()) {

				String type = propHolder.getAttrType();
				if (type.toUpperCase().equalsIgnoreCase(DataType.STRING.toString())) {
					sb.append("'''\"");
					sb.append(attributeMapOperator.sourceAttrAlias(daoRegister, propHolder, propHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()),
							execParams.getOtherParams()));
					sb.append("\"'':\"', ");

					sb.append(attributeMapOperator.sourceAttrSql(daoRegister, propHolder, propHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
							execParams));
					// sb.append(" AS ");

					sb.append(", ");
					sb.append("'\",' ");
					status = true;
				} else {
					sb.append("'''\"");
					sb.append(attributeMapOperator.sourceAttrAlias(daoRegister, propHolder, propHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()),
							execParams.getOtherParams()));
					sb.append("\"'':', ");

					sb.append(attributeMapOperator.sourceAttrSql(daoRegister, propHolder, propHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
							execParams));
					// sb.append(" AS ");

					sb.append(", ");
					sb.append("',' ");
					status = false;
				}
			}
			sb.delete(sb.length() - 5, sb.length());
			if (status == true) {
				sb.append("'\"}')");
			} else {
				sb.append("'}')");
			}

			sb.append(" AS nodeProperties ,");
			// added propertyId
			AttributeRefHolder propertyIdRefHolder = graphNode.getNodeBackgroundInfo().getPropertyId();
			sb.append(attributeMapOperator.sourceAttrSql(daoRegister, propertyIdRefHolder, propertyIdRefHolder,
					DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
					execParams));
			sb.append(" AS nHPropertyId,' ");
			// added type
			sb.append(graphNode.getHighlightInfo().getType());
			sb.append("' AS type, ");
			// added propertyInfo

			AttributeRefHolder propertyIdRefHolder1 = graphNode.getHighlightInfo().getPropertyId();
			sb.append(attributeMapOperator.sourceAttrSql(daoRegister, propertyIdRefHolder1, propertyIdRefHolder1,
					DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
					execParams));
			sb.append(" AS nBPropertyId");

			sb.append(ConstantsUtil.FROM);

			Object source = commonServiceImpl.getOneByUuidAndVersion(graphNode.getNodeSource().getRef().getUuid(),
					graphNode.getNodeSource().getRef().getVersion(),
					graphNode.getNodeSource().getRef().getType().toString());
			sb.append(" ").append(commonServiceImpl.getSource(source, baseExec, execParams, runMode)).append(" ")
					.append(((BaseEntity) source).getName()).append(" ");

			count++;
		}
		nodeSql = sb.toString().replaceAll(",  FROM", " FROM");
		return nodeSql;
	}
	
	/**
	 * 
	 * @param datapod
	 * @param indvTask
	 * @param datapodList
	 * @param dagExec
	 * @param otherParams
	 * @return
	 * @throws Exception
	 */
	protected String getTableName(Datapod datapod, 
			HashMap<String, String> otherParams, BaseExec baseExec, RunMode runMode) throws Exception {
		if (otherParams != null && otherParams.containsKey("datapodUuid_" + datapod.getUuid() + "_tableName")) {
			return otherParams.get("datapodUuid_" + datapod.getUuid() + "_tableName");
		} else {
			try {
				return dataStoreServiceImpl.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
			} catch(Exception e) {
				return String.format("%s_%s_%s", datapod.getUuid().replaceAll("-", "_"), datapod.getVersion(), baseExec.getVersion());
			}
		}
	}
	
	/**
	 * 
	 * @param edgeList
	 * @param execParams
	 * @return
	 * @throws Exception 
	 */
	private String createEdgeSql(List<GraphEdge> edgeList, ExecParams execParams, BaseExec baseExec, RunMode runMode) throws Exception {
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
			

			sb.append(count+" AS edgeIndex, ");
			
			
			AttributeRefHolder sourceNodeIdRefHolder = graphEdge.getSourceNodeId();
			sb.append(attributeMapOperator.sourceAttrSql(daoRegister, sourceNodeIdRefHolder, sourceNodeIdRefHolder, 
												DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
												execParams.getOtherParams(), execParams));
			sb.append(" AS src, ");
			AttributeRefHolder targetNodeIdRefHolder =  graphEdge.getTargetNodeId();
			sb.append(attributeMapOperator.sourceAttrSql(daoRegister, targetNodeIdRefHolder, targetNodeIdRefHolder, 
					DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
					execParams.getOtherParams(), execParams));
			sb.append(" AS dst, '");
			sb.append(graphEdge.getEdgeName());
			sb.append("' AS edgeName, '");
			sb.append(graphEdge.getEdgeType());
			sb.append("' AS edgeType, ");
			Boolean status = true;
			sb.append("concat('{', ");
			for (AttributeRefHolder propHolder : graphEdge.getEdgeProperties()) {
				String type=propHolder.getAttrType();
				if(type.toUpperCase().equalsIgnoreCase(DataType.STRING.toString())) {
				sb.append("'''\"");
				sb.append(attributeMapOperator.sourceAttrAlias(daoRegister, propHolder, propHolder, 
						DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
						execParams.getOtherParams()));
				sb.append("\"'':\"', ");
				
				sb.append(attributeMapOperator.sourceAttrSql(daoRegister, propHolder, propHolder, 
						DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
						execParams.getOtherParams(), execParams));
//				sb.append(" AS ");
				
				sb.append(", ");
				sb.append("'\",' ");
				status=true;
				}else 
				{
					sb.append("'''\"");
					sb.append(attributeMapOperator.sourceAttrAlias(daoRegister, propHolder, propHolder, 
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
							execParams.getOtherParams()));
					sb.append("\"'':', ");
					
					sb.append(attributeMapOperator.sourceAttrSql(daoRegister, propHolder, propHolder, 
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), 
							execParams.getOtherParams(), execParams));
//					sb.append(" AS ");
					
					sb.append(", ");
					sb.append("',' ");
					status=false;
				}
			}
			sb.delete(sb.length() - 5, sb.length());
			if(status==true) {
				sb.append("'\"}')");   
			}else {
				sb.append("'}')");   
			}
			sb.append(" AS edgeProperties, ");
			
			AttributeRefHolder propertyIdRefHolder = graphEdge.getHighlightInfo().getPropertyId();
			sb.append(attributeMapOperator.sourceAttrSql(daoRegister, propertyIdRefHolder, propertyIdRefHolder,
					DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
					execParams));
			sb.append(" AS eHpropertyId, ");
			
			sb.append("concat('{', ");
			for (Property property : graphEdge.getHighlightInfo().getPropertyInfo()) {
				sb.append("'''");
				sb.append(property.getPropertyName());
				sb.append("'':', ");
				sb.append("'");
				sb.append(property.getPropertyValue());
				sb.append("'");
				sb.append(", ");
				sb.append("',' ");
			}
			sb.delete(sb.length() - 5, sb.length());
			sb.append("'}')");
			sb.append(" AS propertyInfo ");

			
			
			sb.append(ConstantsUtil.FROM);
			Object source = commonServiceImpl.getOneByUuidAndVersion(graphEdge.getEdgeSource().getRef().getUuid(), graphEdge.getEdgeSource().getRef().getVersion(), graphEdge.getEdgeSource().getRef().getType().toString());
			sb.append(" ").append(commonServiceImpl.getSource(source, baseExec, execParams, runMode)).append(" ").append(((BaseEntity) source).getName()).append(" ");
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

	@Override
	public BaseExec create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}