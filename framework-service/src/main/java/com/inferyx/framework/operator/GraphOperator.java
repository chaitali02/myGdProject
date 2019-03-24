/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.common.DagExecUtil;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.DataType;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.GraphEdge;
import com.inferyx.framework.domain.GraphNode;
import com.inferyx.framework.domain.Graphpod;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Property;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.service.CommonServiceImpl;

/**
 * @author joy
 *
 */
@Service
public class GraphOperator implements IOperator {
	
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	AttributeMapOperator attributeMapOperator;
	
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
		//commonServiceImpl.setMetaStatus(baseExec, MetaType.graphExec.toString(),null);
		commonServiceImpl.setMetaStatus(baseExec, MetaType.graphExec,
				Status.Stage.STARTING);
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
		commonServiceImpl.setMetaStatus(baseExec, MetaType.graphExec,
				Status.Stage.READY);
		return baseExec;
	}

	/**
	 * 
	 * @param nodeList
	 * @return
	 * @throws Exception 
	 */
	private String createNodeSql(List<GraphNode> nodeList, ExecParams execParams, BaseExec baseExec, RunMode runMode)
			throws Exception {
		if (nodeList == null || nodeList.isEmpty()) {
			return null;
		}
		String nodeSql = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		int count = 0;
		String propertyIdKey = null;
		for (GraphNode graphNode : nodeList) {
			if (count > 0) {
				sb.append(" UNION ALL ");
			}
			sb.append(ConstantsUtil.SELECT);
			sb.append(" ");
			// Fetch id attribute
			AttributeRefHolder nodeIdRefHolder = graphNode.getNodeId();
			sb.append(attributeMapOperator.sourceAttrSql(nodeIdRefHolder, nodeIdRefHolder,
					DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
					execParams));
			sb.append(" AS id, ");

			// Fetch id nodeBackgroundInfo
			if (graphNode.getNodeBackgroundInfo() != null) {
				AttributeRefHolder nbpropID = graphNode.getNodeBackgroundInfo().getPropertyId();
				sb.append(attributeMapOperator.sourceAttrSql(nbpropID, nbpropID,
						DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
						execParams));
				sb.append(" AS nBPropertyId, ");

			} else {
				sb.append("0 AS nBPropertyId, ");

			}

			sb.append(count + " AS nodeIndex, ");

			AttributeRefHolder nodeNameRefHolder = graphNode.getNodeName();
			sb.append(attributeMapOperator.sourceAttrSql(nodeNameRefHolder, nodeNameRefHolder,
					DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
					execParams));
			sb.append(" AS nodeName, ");

			if (graphNode.getNodeSize() != null) {
				AttributeRefHolder nodeSizeRefHolder = graphNode.getNodeSize();
				sb.append(attributeMapOperator.sourceAttrSql(nodeSizeRefHolder, nodeSizeRefHolder,
						DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
						execParams));
			} else {
				sb.append(0);
			}
			sb.append(" AS nodeSize, '");
			/*
			 * sb.append(attributeMapOperator.sourceAttrAlias(daoRegister,
			 * nodeNameRefHolder, nodeNameRefHolder,
			 * DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()),
			 * execParams.getOtherParams())); sb.append(", ");
			 */

			sb.append(graphNode.getNodeType());
			sb.append("' AS nodeType, '");

			sb.append(graphNode.getNodeIcon());
			sb.append("' AS nodeIcon, ");

			/*
			 * @SuppressWarnings("unchecked") List<AttributeRefHolder> setAttrRefHolder =
			 * graphNode.getNodeProperties();
			 * 
			 * AttributeRefHolder attrRefHolder=
			 * graphNode.getNodeBackgroundInfo().getPropertyId();
			 * 
			 * //setAttrRefHolder.add(attrRefHolder); for (AttributeRefHolder attrRefHolder1
			 * : setAttrRefHolder)
			 * setAttrRefHolder.add(attrRefHolder.getAttrName().contains(attrRefHolder1.
			 * getAttrName()) ? : null); System.out.println(setAttrRefHolder);
			 */

			// added propertyId into sb2
			if (graphNode.getNodeBackgroundInfo() != null) {
				AttributeRefHolder propertyIdRefHolder = graphNode.getNodeBackgroundInfo().getPropertyId();
				sb2.append("'''\"");
				sb2.append(attributeMapOperator.sourceAttrAlias(propertyIdRefHolder, propertyIdRefHolder,
						DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams()));
				sb2.append("\"'':\"', ");

				sb2.append(attributeMapOperator.sourceAttrSql(propertyIdRefHolder, propertyIdRefHolder,
						DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
						execParams));

				sb2.append(", ");

				propertyIdKey = attributeMapOperator.sourceAttrAlias(propertyIdRefHolder, propertyIdRefHolder,
						DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams());
			}
			Boolean status = true;
			Boolean flag = true;

			sb.append("concat('{', ");
			for (AttributeRefHolder propHolder : graphNode.getNodeProperties()) {

				String type = propHolder.getAttrType();
				if (type.toUpperCase().equalsIgnoreCase(DataType.STRING.toString())) {
					sb.append("'''\"");
					sb.append(attributeMapOperator.sourceAttrAlias(propHolder, propHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()),
							execParams.getOtherParams()));
					sb.append("\"'':\"', ");
					sb.append("NVL( ");
					sb.append(attributeMapOperator.sourceAttrSql(propHolder, propHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
							execParams));
					// sb.append(" AS ");
					sb.append(", \'\') ");
					sb.append(", ");
					sb.append("'\",' ");
					status = true;
				} else {
					sb.append("'''\"");
					sb.append(attributeMapOperator.sourceAttrAlias(propHolder, propHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()),
							execParams.getOtherParams()));
					sb.append("\"'':', ");
					sb.append("NVL( ");
					sb.append(attributeMapOperator.sourceAttrSql(propHolder, propHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
							execParams));
					// sb.append(" AS ");
					sb.append(", 0) ");
					sb.append(", ");
					sb.append("',' ");
					status = false;
				}
				if (propertyIdKey != null
						&& propertyIdKey.equalsIgnoreCase(attributeMapOperator.sourceAttrAlias(propHolder, propHolder,
								DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()),
								execParams.getOtherParams()))) {
					flag = false;
				}
			}
			if (flag == true && graphNode.getNodeBackgroundInfo() != null) {
				// sb.delete(sb.length() - 5, sb.length());
				sb.append(sb2);
			} else {
				sb.delete(sb.length() - 5, sb.length());
			}

			if (status == true) {
				sb.append("'\"}')");
			} else {
				sb.append("'}')");
			}

			sb.append(" AS nodeProperties ,'");

			if (graphNode.getHighlightInfo() != null) {
				// added type
				sb.append(graphNode.getHighlightInfo().getType());
				sb.append("' AS type, ");
				// added propertyInfo

				AttributeRefHolder propertyIdRefHolder1 = graphNode.getHighlightInfo().getPropertyId();
				sb.append(attributeMapOperator.sourceAttrSql(propertyIdRefHolder1, propertyIdRefHolder1,
						DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
						execParams));
				sb.append(" AS nHPropertyId");

			} else {
				sb.deleteCharAt(sb.lastIndexOf("'"));
				sb.append("' ' AS type, ");
				sb.append("0 AS nHPropertyId");

			}
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

	/********************** UNUSED **********************/
//	/**
//	 * 
//	 * @param datapod
//	 * @param indvTask
//	 * @param datapodList
//	 * @param dagExec
//	 * @param otherParams
//	 * @return
//	 * @throws Exception
//	 */
//	protected String getTableName(Datapod datapod, 
//			HashMap<String, String> otherParams, BaseExec baseExec, RunMode runMode) throws Exception {
//		if (otherParams != null && otherParams.containsKey("datapodUuid_" + datapod.getUuid() + "_tableName")) {
//			return otherParams.get("datapodUuid_" + datapod.getUuid() + "_tableName");
//		} else {
//			try {
//				return datapodServiceImpl.getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//			} catch(Exception e) {
//				return String.format("%s_%s_%s", datapod.getUuid().replaceAll("-", "_"), datapod.getVersion(), baseExec.getVersion());
//			}
//		}
//	}
	
	/**
	 * 
	 * @param edgeList
	 * @param execParams
	 * @return
	 * @throws Exception 
	 */
	private String createEdgeSql(List<GraphEdge> edgeList, ExecParams execParams, BaseExec baseExec, RunMode runMode)
			throws Exception {
		if (edgeList == null || edgeList.isEmpty()) {
			return null;
		}
		String edgeSql = null;
		String propertyIdKey = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb3 = new StringBuilder();

		int count = 0;
		for (GraphEdge graphEdge : edgeList) {
			if (count > 0) {
				sb.append(" UNION ALL ");
			}
			sb.append(ConstantsUtil.SELECT);
			sb.append(" ");
			// Fetch id attribute

			sb.append(count + " AS edgeIndex, ");

			AttributeRefHolder sourceNodeIdRefHolder = graphEdge.getSourceNodeId();
			sb.append(attributeMapOperator.sourceAttrSql(sourceNodeIdRefHolder, sourceNodeIdRefHolder,
					DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
					execParams));
			sb.append(" AS src, ");
			AttributeRefHolder targetNodeIdRefHolder = graphEdge.getTargetNodeId();
			sb.append(attributeMapOperator.sourceAttrSql(targetNodeIdRefHolder, targetNodeIdRefHolder,
					DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
					execParams));
			sb.append(" AS dst, '");
			sb.append(graphEdge.getEdgeName());
			sb.append("' AS edgeName, '");
			sb.append(graphEdge.getEdgeType());
			sb.append("' AS edgeType,'");

			BaseEntity baseEntity = (BaseEntity) commonServiceImpl.getOneByUuidAndVersion(
					graphEdge.getEdgeSource().getRef().getUuid(), graphEdge.getEdgeSource().getRef().getVersion(),
					graphEdge.getEdgeSource().getRef().getType().toString());
			String edgeSource = (baseEntity == null) ? "" : baseEntity.getName();

			sb.append(edgeSource);
			sb.append("' AS edgeSource, ");

			// added propertyId into sb2
			if (graphEdge.getHighlightInfo() != null) {
				AttributeRefHolder hIpropIdRefHolder = graphEdge.getHighlightInfo().getPropertyId();
				if (hIpropIdRefHolder.getAttrType().toUpperCase().equalsIgnoreCase(DataType.STRING.toString())) {
					sb3.append("\"");
					sb3.append(attributeMapOperator.sourceAttrAlias(hIpropIdRefHolder, hIpropIdRefHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()),
							execParams.getOtherParams()));
					sb3.append("\"':\"', ");

					sb3.append("NVL( ");
					sb3.append(attributeMapOperator.sourceAttrSql(hIpropIdRefHolder, hIpropIdRefHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
							execParams));
					sb3.append(", \'\') ");
					sb3.append(", ");

					propertyIdKey = attributeMapOperator.sourceAttrAlias(hIpropIdRefHolder, hIpropIdRefHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()),
							execParams.getOtherParams());

				} else {
					sb3.append("\"");
					sb3.append(attributeMapOperator.sourceAttrAlias(hIpropIdRefHolder, hIpropIdRefHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()),
							execParams.getOtherParams()));
					sb3.append("\"':\"', ");

					sb3.append("NVL( ");
					sb3.append(attributeMapOperator.sourceAttrSql(hIpropIdRefHolder, hIpropIdRefHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
							execParams));
					sb3.append(", 0) ");
					sb3.append(", ");

					propertyIdKey = attributeMapOperator.sourceAttrAlias(hIpropIdRefHolder, hIpropIdRefHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()),
							execParams.getOtherParams());

				}
			}
			Boolean flag = true;
			Boolean status = true;
			sb.append("concat('{', ");
			// graphEdge.getEdgeProperties().set(graphEdge.getEdgeProperties().size()-1,
			// graphEdge.getHighlightInfo().getPropertyId());
			for (AttributeRefHolder propHolder : graphEdge.getEdgeProperties()) {
				String type = propHolder.getAttrType();
				if (type.toUpperCase().equalsIgnoreCase(DataType.STRING.toString())) {
					sb.append("'''\"");
					sb.append(attributeMapOperator.sourceAttrAlias(propHolder, propHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()),
							execParams.getOtherParams()));
					sb.append("\"'':\"', ");
					sb.append("NVL( ");
					sb.append(attributeMapOperator.sourceAttrSql(propHolder, propHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
							execParams));
					// sb.append(" AS ");
					sb.append(", \'\') ");
					sb.append(", ");
					sb.append("'\",' ");
					status = true;
				} else {
					sb.append("'''\"");
					sb.append(attributeMapOperator.sourceAttrAlias(propHolder, propHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()),
							execParams.getOtherParams()));
					sb.append("\"'':', ");
					sb.append("NVL( ");
					sb.append(attributeMapOperator.sourceAttrSql(propHolder, propHolder,
							DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
							execParams));
					// sb.append(" AS ");
					sb.append(", 0) ");
					sb.append(", ");
					sb.append("',' ");
					status = false;
				}

				if (propertyIdKey != null
						&& propertyIdKey.equalsIgnoreCase(attributeMapOperator.sourceAttrAlias(propHolder, propHolder,
								DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()),
								execParams.getOtherParams()))) {
					flag = false;
				}
			}
			if (graphEdge.getHighlightInfo() != null && flag == true) {
				// sb.delete(sb.length() - 5, sb.length());
				sb.append(sb3);
			} else {
				sb.delete(sb.length() - 5, sb.length());
			}

			if (status == true) {
				sb.append("'\"}')");
			} else {
				sb.append("'}')");
			}
			sb.append(" AS edgeProperties, ");
			// remove

			if (graphEdge.getHighlightInfo() != null) {
				AttributeRefHolder propertyIdRefHolder = graphEdge.getHighlightInfo().getPropertyId();
				sb.append(attributeMapOperator.sourceAttrSql(propertyIdRefHolder, propertyIdRefHolder,
						DagExecUtil.convertRefKeyListToMap(execParams.getRefKeyList()), execParams.getOtherParams(),
						execParams));
				sb.append(" AS eHpropertyId, ");

				// graphEdge.getEdgeSource().getRef().get

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

			} else {
				sb.append("0 AS eHpropertyId, ");
				sb.append(" '' AS propertyInfo   ");
				sb.delete(sb.length() - 2, sb.length());

			}

			sb.append(ConstantsUtil.FROM);
			Object source = commonServiceImpl.getOneByUuidAndVersion(graphEdge.getEdgeSource().getRef().getUuid(),
					graphEdge.getEdgeSource().getRef().getVersion(),
					graphEdge.getEdgeSource().getRef().getType().toString());
			sb.append(" ").append(commonServiceImpl.getSource(source, baseExec, execParams, runMode)).append(" ")
					.append(((BaseEntity) source).getName()).append(" ");
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