/**
 * 
 */
package com.inferyx.framework.operator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.sql.SaveMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.common.ConstantsUtil;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseExec;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ResultSetHolder;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.executor.SparkExecutor;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.service.CommonServiceImpl;
import com.inferyx.framework.service.DataStoreServiceImpl;
import com.inferyx.framework.service.DatapodServiceImpl;
import com.inferyx.framework.service.ParamSetServiceImpl;

/**
 * @author joy
 *
 */
@Service
public class MatrixMultOperator implements IOperator {

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
	private SparkExecutor<?> sparkExecutor;
	
	static final Logger logger = Logger.getLogger(MatrixMultOperator.class);

	/**
	 * 
	 */
	public MatrixMultOperator() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.inferyx.framework.operator.IParsable#parse(com.inferyx.framework.domain.
	 * BaseExec, com.inferyx.framework.domain.ExecParams,
	 * com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public BaseExec parse(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.inferyx.framework.operator.IExecutable#execute(com.inferyx.framework.
	 * domain.BaseExec, com.inferyx.framework.domain.ExecParams,
	 * com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public String execute(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		String execVersion = baseExec.getVersion();
		// Get exec
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		Map<String, String> otherParams = execParams.getOtherParams();
		ParamListHolder lhsKeyAttrInfo = paramSetServiceImpl.getParamByName(execParams, "lhsKeyAttr");
		ParamListHolder rhsKeyAttrInfo = paramSetServiceImpl.getParamByName(execParams, "rhsKeyAttr");
		ParamListHolder lhsAttrInfo = paramSetServiceImpl.getParamByName(execParams, "lhsAttrs");
		ParamListHolder rhsAttrInfo = paramSetServiceImpl.getParamByName(execParams, "rhsAttrs");
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");
		ParamListHolder operationType = paramSetServiceImpl.getParamByName(execParams, "operatorType");
		MetaIdentifier lhsAttrDpIdentifier = lhsAttrInfo.getAttributeInfo().get(0).getRef();
		MetaIdentifier rhsAttrDpIdentifier = rhsAttrInfo.getAttributeInfo().get(0).getRef();
		
		String lhsTableName = otherParams.get("datapodUuid_" + lhsAttrDpIdentifier.getUuid() + "_tableName");
		String rhsTableName = otherParams.get("datapodUuid_" + rhsAttrDpIdentifier.getUuid() + "_tableName");
		String operation = operationType.getParamValue().getValue();
/*		String lhsTableName = otherParams
				.get("datapodUuid_" + lhsDataInfo.getParamValue().getRef().getUuid() + "_tableName");
		String rhsTableName = otherParams
				.get("datapodUuid_" + rhsDataInfo.getParamValue().getRef().getUuid() + "_tableName");*/
		String saveTableName = otherParams.get("datapodUuid_" + locationInfo.getParamValue().getRef().getUuid() + "_tableName");
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locationInfo.getParamValue().getRef().getUuid(), 
																			locationInfo.getParamValue().getRef().getVersion(), 
																			locationInfo.getParamValue().getRef().getType().toString());
		Datasource targetDatasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(locationDatapod.getDatasource().getRef().getUuid(), 
																							locationDatapod.getDatasource().getRef().getVersion(), 
																							locationDatapod.getDatasource().getRef().getType().toString());
		List<AttributeRefHolder> lhsKeyAttrList = lhsKeyAttrInfo.getAttributeInfo();
		List<AttributeRefHolder> rhsKeyAttrList = rhsKeyAttrInfo.getAttributeInfo();
		List<AttributeRefHolder> lhsAttrList = lhsAttrInfo.getAttributeInfo();
		List<AttributeRefHolder> rhsAttrList = rhsAttrInfo.getAttributeInfo();

		String lhsSql = generateSql(lhsTableName, lhsKeyAttrList, lhsAttrList);
		String rhsSql = generateSql(rhsTableName, rhsKeyAttrList, rhsAttrList);
		
//		Dataset<Row> lhsDf = exec.executeSql(generateSql(lhsTableName, lhsKeyAttrList, lhsAttrList)).getDataFrame();
//		Dataset<Row> rhsDf = exec.executeSql(generateSql(rhsTableName, rhsKeyAttrList, rhsAttrList)).getDataFrame();
//		JavaRDD<MatrixEntry> lhsMatrixEntry = lhsDf.toJavaRDD().map(data -> {
//			return new MatrixEntry(new Long(data.get(0) + ""), new Long(data.get(1) + ""), data.getDouble(2));
//		});
//		JavaRDD<MatrixEntry> rhsMatrixEntry = rhsDf.toJavaRDD().map(data -> {
//			return new MatrixEntry(new Long(data.get(0) + ""), new Long(data.get(1) + ""), data.getDouble(2));
//		});
//		CoordinateMatrix lhsCoMat = new CoordinateMatrix(lhsMatrixEntry.rdd());
//		CoordinateMatrix rhsCoMat = new CoordinateMatrix(rhsMatrixEntry.rdd());
//		BlockMatrix resultMatrix = null;
//		
//		switch (operation) {
//		case ADD:
//			resultMatrix = lhsCoMat.toBlockMatrix().add(rhsCoMat.toBlockMatrix());
//			break;
//		case SUB:
//			resultMatrix = lhsCoMat.toBlockMatrix().subtract(rhsCoMat.toBlockMatrix());
//			break;
//		case MUL:
//			resultMatrix = lhsCoMat.toBlockMatrix().multiply(rhsCoMat.toBlockMatrix());
//			break;
//		default:
//			resultMatrix = lhsCoMat.toBlockMatrix().multiply(rhsCoMat.toBlockMatrix());
//			break;
//		}
//		JavaRDD<Row> rowRdd = printResult(resultMatrix, baseExec, otherParams, runMode, exec);
//		// Convert Rdd to Dataframe and register Dataframe
//		// Obtain list of attr names from locationDatapod
//		String[] columns = new String[locationDatapod.getAttributes().size()];
//		int count = 0;
//		for (Attribute attr : locationDatapod.getAttributes()) {
//			columns[count] = attr.getName();
//			count++;
//		}
////		List<Object> tableColumns = Arrays.asList(columns);
//		StructType schema = createSchema(locationDatapod.getAttributes());
//		sparkExecutor.createAndRegisterDataset(rowRdd, schema, saveTableName+"_df");
		
		ResultSetHolder rsHolder = exec.mattrix(locationDatapod, operation, lhsTableName, rhsTableName, lhsSql, rhsSql, saveTableName, baseExec, otherParams, runMode);
		
		// Save result
		String filePath = "/"+locationDatapod.getUuid() + "/" + locationDatapod.getVersion() + "/" + execVersion;
		String fileName = String.format("%s_%s_%s", locationDatapod.getUuid().replace("-", "_"), locationDatapod.getVersion(), execVersion);
		MetaIdentifierHolder resultRef = new MetaIdentifierHolder();
		
		String sql = "SELECT * FROM " + saveTableName+"_df";
		ResultSetHolder rsHolder2 = null;
		if(targetDatasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())/*
				|| datasource.getType().equalsIgnoreCase(ExecContext.spark.toString())
				|| datasource.getType().equalsIgnoreCase(ExecContext.livy_spark.toString())
				|| datasource.getType().equalsIgnoreCase("livy-spark")*/) {
			rsHolder2 = exec.executeRegisterAndPersist(sql, saveTableName, filePath, locationDatapod, SaveMode.Append.toString(), true, commonServiceImpl.getApp().getUuid());
		} else {
			rsHolder2 = sparkExecutor.persistDataframe(rsHolder, datasource, locationDatapod, null);
		}
		
		Object metaExec = commonServiceImpl.getOneByUuidAndVersion(baseExec.getUuid(), baseExec.getVersion(), MetaType.operatorExec.toString());
		MetaIdentifierHolder createdBy = (MetaIdentifierHolder) metaExec.getClass().getMethod("getCreatedBy").invoke(metaExec);
		@SuppressWarnings("unchecked")
		List<MetaIdentifierHolder> appInfo = (List<MetaIdentifierHolder>) metaExec.getClass().getMethod("getAppInfo").invoke(metaExec);
		
		dataStoreServiceImpl.setRunMode(runMode);
		dataStoreServiceImpl.create(filePath, fileName, 
				new MetaIdentifier(MetaType.datapod, locationDatapod.getUuid(), locationDatapod.getVersion()) 
				, new MetaIdentifier(MetaType.operatorExec, baseExec.getUuid(), execVersion) ,
				appInfo, createdBy, SaveMode.Append.toString(), resultRef, rsHolder2.getCountRows(), null, null);
		
		metaExec.getClass().getMethod("setResult", MetaIdentifierHolder.class).invoke(metaExec, resultRef);
		commonServiceImpl.save(MetaType.operatorExec.toString(), metaExec);
		
		return null;
	}
		
/*	*//**
	 * 
	 * @param resultMatrix
	 * @param baseExec
	 * @param otherParams
	 * @param runMode
	 * @param exec
	 * @return
	 *//*
	private JavaRDD<Row> printResult(BlockMatrix resultMatrix, BaseExec baseExec, Map<String, String> otherParams, RunMode runMode, IExecutor exec) {
		resultMatrix.toCoordinateMatrix().toIndexedRowMatrix().rows().toJavaRDD().collect().forEach(t -> logger.info(t.vector()));
		JavaRDD<Row> rowRdd = matrixToRddConverter.convertToRows(resultMatrix);
		return rowRdd;
	}
	
	*//**
	 * 
	 * @param tableColumns
	 * @return
	 *//*
	public StructType createSchema(List<Attribute> attributes){

        List<StructField> fields  = new ArrayList<StructField>();
        for(Attribute attr  : attributes){         

                fields.add(DataTypes.createStructField(attr.getName(),(DataType)sparkExecutor.getDataType(attr.getType()), true));            

        }
        return DataTypes.createStructType(fields);
    }*/

	/**
	 * 
	 * @param tableName
	 * @param attrList
	 * @return
	 */
	private String generateSql(String tableName, List<AttributeRefHolder> keyAttrList, List<AttributeRefHolder> attrList) {
		String keyAttrs = "";
		for (int count = 0; count < keyAttrList.size(); count++) {
			keyAttrs = keyAttrs.concat(keyAttrList.get(count).getAttrName());
			if (count < (keyAttrList.size() - 1)) {
				keyAttrs = keyAttrs.concat(ConstantsUtil.COMMA);
			}
		}
		StringBuilder sb = new StringBuilder(
				" SELECT floor((row_number() OVER (PARTITION BY 1 ORDER BY ");
				sb.append(keyAttrs).append(ConstantsUtil.COMMA).append("seq))/3-0.000000001) AS rn, seq, val, ")
				  		.append(keyAttrs).append(" FROM ")
						.append(tableName).append(" CROSS JOIN ( SELECT ")/*.append(keyAttrs)*/
						.append(" ARRAY(");
		for (int count = 0; count < attrList.size(); count++) {
			sb.append("cast(matatab").append(ConstantsUtil.DOT).append(attrList.get(count).getAttrName())
					.append(" AS double)");
			if (count < (attrList.size() - 1)) {
				sb.append(ConstantsUtil.COMMA);
			}
		}
		sb.append(") AS arr_r from ")
		.append(tableName)
		.append(" matatab ORDER BY 1) exp LATERAL VIEW POSEXPLODE(exp.arr_r) rec AS seq, val ORDER BY ")
		.append(keyAttrs).append(", seq");
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.inferyx.framework.operator.IOperator#create(com.inferyx.framework.domain.
	 * BaseExec, com.inferyx.framework.domain.ExecParams,
	 * com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public BaseExec create(BaseExec baseExec, ExecParams execParams, RunMode runMode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.inferyx.framework.operator.IOperator#customCreate(com.inferyx.framework.
	 * domain.BaseExec, com.inferyx.framework.domain.ExecParams,
	 * com.inferyx.framework.enums.RunMode)
	 */
	@Override
	public Map<String, String> customCreate(BaseExec baseExec, ExecParams execParams, RunMode runMode)
			throws Exception {
		HashMap<String, String> otherParams = execParams.getOtherParams();
		if (otherParams == null) {
			otherParams = new HashMap<String, String>();
			execParams.setOtherParams(otherParams);
		}
		String execVersion = baseExec.getVersion();
		// Set destination
//		ParamListHolder lhsDataInfo = paramSetServiceImpl.getParamByName(execParams, "lhsData");
//		ParamListHolder rhsDataInfo = paramSetServiceImpl.getParamByName(execParams, "rhsData");
		ParamListHolder lhsAttrInfo = paramSetServiceImpl.getParamByName(execParams, "lhsAttrs");
		ParamListHolder rhsAttrInfo = paramSetServiceImpl.getParamByName(execParams, "rhsAttrs");
		ParamListHolder locationInfo = paramSetServiceImpl.getParamByName(execParams, "saveLocation");

		MetaIdentifier locDpIdentifier = locationInfo.getParamValue().getRef();
		Datapod locationDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(locDpIdentifier.getUuid(),
				locDpIdentifier.getVersion(), locDpIdentifier.getType().toString());

		// Get the attribute's corresponding datapod
		MetaIdentifier lhsAttrDpIdentifier = lhsAttrInfo.getAttributeInfo().get(0).getRef();
		Datapod lhsDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(lhsAttrDpIdentifier.getUuid(), lhsAttrDpIdentifier.getVersion(), lhsAttrDpIdentifier.getType().toString());
		MetaIdentifier rhsAttrDpIdentifier = rhsAttrInfo.getAttributeInfo().get(0).getRef();
		Datapod rhsDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(rhsAttrDpIdentifier.getUuid(), rhsAttrDpIdentifier.getVersion(), rhsAttrDpIdentifier.getType().toString());
		/*Datapod lhsDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
				lhsDataInfo.getParamValue().getRef().getUuid(), lhsDataInfo.getParamValue().getRef().getVersion(),
				lhsDataInfo.getParamValue().getRef().getType().toString());*/

		/*Datapod rhsDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
				rhsDataInfo.getParamValue().getRef().getUuid(), rhsDataInfo.getParamValue().getRef().getVersion(),
				rhsDataInfo.getParamValue().getRef().getType().toString());*/

		String lhsTableName = null;
		if (otherParams.containsKey("datapodUuid_" + lhsDatapod.getUuid() + "_tableName")) {
			lhsTableName = otherParams.get("datapodUuid_" + lhsDatapod.getUuid() + "_tableName");
		} else {
			lhsTableName = commonServiceImpl.getTableNameBySource(lhsDatapod, runMode);
			otherParams.put("datapodUuid_" + lhsDatapod.getUuid() + "_tableName", lhsTableName);
		}

		String rhsTableName = null;
		if (otherParams.containsKey("datapodUuid_" + rhsDatapod.getUuid() + "_tableName")) {
			rhsTableName = otherParams.get("datapodUuid_" + rhsDatapod.getUuid() + "_tableName");
		} else {
			rhsTableName = commonServiceImpl.getTableNameBySource(rhsDatapod, runMode);
			otherParams.put("datapodUuid_" + rhsDatapod.getUuid() + "_tableName", rhsTableName);
		}
		String tableName = datapodServiceImpl.genTableNameByDatapod(locationDatapod, execVersion, null, null, null, runMode, false);
		logger.info(" lhsTableName : " + lhsTableName);
		logger.info(" rhsTableName : " + rhsTableName);
		logger.info(" saveTableName : " + tableName);
		otherParams.put("datapodUuid_" + lhsDatapod.getUuid() + "_tableName", lhsTableName);
		otherParams.put("datapodUuid_" + rhsDatapod.getUuid() + "_tableName", rhsTableName);
		otherParams.put("datapodUuid_" + locationDatapod.getUuid() + "_tableName", tableName);
		logger.info("otherParams in matrixMultDataOperator : " + otherParams);
		return otherParams;
	}

	/********************** UNUSED **********************/
//	/**
//	 * 
//	 * @param sourceData
//	 * @param runMode
//	 * @return
//	 * @throws Exception
//	 */
//	public String getTableNameBySource(Object sourceData, RunMode runMode) throws Exception {
//		String sourceTableName = null;
//		if (sourceData instanceof Datapod) {
//			Datapod datapod = (Datapod) sourceData;
//			sourceTableName = datapodServiceImpl
//					.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//		} else if (sourceData instanceof DataSet) {
//			DataSet dataSet = (DataSet) sourceData;
//			MetaIdentifierHolder dependsOn = dataSet.getDependsOn();
//			if (dependsOn.getRef().getType().equals(MetaType.datapod)) {
//				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(),
//						dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
//				sourceTableName = datapodServiceImpl
//						.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//			} else if (dependsOn.getRef().getType().equals(MetaType.relation)) {
//				Relation relation = (Relation) sourceData;
//				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
//						relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(),
//						relation.getDependsOn().getRef().getType().toString());
//				sourceTableName = datapodServiceImpl
//						.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//			}
//		} else if (sourceData instanceof Rule) {
//			Rule rule = (Rule) sourceData;
//			MetaIdentifierHolder sourceHolder = rule.getSource();
//			if (sourceHolder.getRef().getType().equals(MetaType.datapod)) {
//				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(),
//						sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
//				sourceTableName = datapodServiceImpl
//						.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//			} else if (sourceHolder.getRef().getType().equals(MetaType.dataset)) {
//				DataSet dataSet = (DataSet) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(),
//						sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
//				MetaIdentifierHolder dependsOn = dataSet.getDependsOn();
//				if (dependsOn.getRef().getType().equals(MetaType.datapod)) {
//					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dependsOn.getRef().getUuid(),
//							dependsOn.getRef().getVersion(), dependsOn.getRef().getType().toString());
//					sourceTableName = datapodServiceImpl
//							.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//				} else if (dependsOn.getRef().getType().equals(MetaType.relation)) {
//					Relation relation = (Relation) sourceData;
//					Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
//							relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(),
//							relation.getDependsOn().getRef().getType().toString());
//					sourceTableName = datapodServiceImpl
//							.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//				}
//			} else if (sourceHolder.getRef().getType().equals(MetaType.relation)) {
//				Relation relation = (Relation) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(),
//						sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
//				Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(
//						relation.getDependsOn().getRef().getUuid(), relation.getDependsOn().getRef().getVersion(),
//						relation.getDependsOn().getRef().getType().toString());
//				sourceTableName = datapodServiceImpl
//						.getTableNameByDatapod(new OrderKey(datapod.getUuid(), datapod.getVersion()), runMode);
//			} else if (sourceHolder.getRef().getType().equals(MetaType.rule)) {
//				Rule rule2 = (Rule) commonServiceImpl.getOneByUuidAndVersion(sourceHolder.getRef().getUuid(),
//						sourceHolder.getRef().getVersion(), sourceHolder.getRef().getType().toString());
//				sourceTableName = getTableNameBySource(rule2, runMode);
//			}
//		}
//		return sourceTableName;
//	}

}
