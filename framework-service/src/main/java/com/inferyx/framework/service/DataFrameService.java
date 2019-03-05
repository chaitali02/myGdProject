/*******************************************************************************
 * Copyright (C) Inferyx Inc, 2018 All rights reserved. 
 *
 * This unpublished material is proprietary to Inferyx Inc.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@inferyx.com>
 *******************************************************************************/
package com.inferyx.framework.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inferyx.framework.factory.DataSourceFactory;

@Service
public class DataFrameService {
	@Autowired
	DataSourceFactory dataSourceFactory;
	/*@Autowired
	private HiveContext hiveContext;*/

	private static final Logger logger = Logger.getLogger(DataFrameService.class);

	/********************** UNUSED **********************/
	/*
	 * public Dataset<Row> getDataFrameByDataset(String uuid, String version, Mode
	 * runMode) throws Exception { // TODO Auto-generated method stub DataSet
	 * dataset = new DataSet(); if (version != null) { //dataset =
	 * datasetServiceImpl.findOneByUuidAndVersion(uuid,version); dataset = (DataSet)
	 * commonServiceImpl.getOneByUuidAndVersion(uuid,version,
	 * MetaType.dataset.toString()); } else { //dataset =
	 * datasetServiceImpl.findLatestByUuid(uuid); dataset = (DataSet)
	 * commonServiceImpl.getLatestByUuid(uuid, MetaType.dataset.toString()); }
	 * 
	 * //List<Map<String, Object>> data = new ArrayList<>(); String sql =
	 * datasetOperator.generateSql(dataset, null, null,new HashSet<>(), null,
	 * runMode); Datasource datasource = commonServiceImpl.getDatasourceByApp();
	 * IExecutor exec = execFactory.getExecutor(datasource.getType());
	 * ResultSetHolder rsHolder = null; rsHolder = exec.executeSql(sql);
	 * Dataset<Row> df = rsHolder.getDataFrame(); return df; }
	 */

	/********************** UNUSED **********************/
	/*
	 * public Dataset getDataFrameByDatapod(String datapodUUID , String
	 * datapodVersion) throws Exception { Datapod datapod = new Datapod(); if
	 * (datapodVersion != null) { //datapod =
	 * datapodServiceImpl.findOneByUuidAndVersion(datapodUUID,datapodVersion);
	 * datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodUUID,
	 * datapodVersion, MetaType.datapod.toString()); } else { //datapod =
	 * datapodServiceImpl.findLatestByUuid(datapodUUID); datapod = (Datapod)
	 * commonServiceImpl.getLatestByUuid(datapodUUID, MetaType.datapod.toString());
	 * } DataStore datastore =
	 * dataStoreServiceImpl.findLatestByMeta(datapod.getUuid(),
	 * datapod.getVersion()); if (datastore == null) {
	 * logger.error("Datastore is not available for this datapod"); throw new
	 * Exception(); } IReader iReader = dataSourceFactory.getDatapodReader(datapod,
	 * commonActivity); String datasourceUUID =
	 * datapod.getDatasource().getRef().getUuid(); String datasourceVersion =
	 * datapod.getDatasource().getRef().getVersion(); Datasource datasource =
	 * (Datasource) commonActivity.getRefObject(new
	 * MetaIdentifier(MetaType.datasource, datasourceUUID, datasourceVersion));
	 * Datasource datasource_2 = commonServiceImpl.getDatasourceByApp(); IConnector
	 * conn = connFactory.getConnector(datasource_2.getType().toLowerCase());
	 * ConnectionHolder cPAUSEer = conn.getConnection(); Object obj =
	 * cPAUSEer.getStmtObject(); if(obj instanceof HiveStatement || obj instanceof
	 * HiveContext){ DataFrameHolder dataFrameHolder = iReader.read(datapod,
	 * datastore, hdfsInfo,obj, datasource); Dataset<Row> df =
	 * dataFrameHolder.getDataframe(); return df; } return null; }
	 */
	/********************** UNUSED **********************/
	/*
	 * public Dataset<Row> getDataFrameByDataStore(String datastoreUUID, String
	 * datastoreVersion, Datapod datapod) throws Exception { //DataStore datastore =
	 * findOneByUuidAndVersion(datastoreUUID, datastoreVersion); DataStore datastore
	 * = (DataStore) commonServiceImpl.getOneByUuidAndVersion(datastoreUUID,
	 * datastoreVersion, MetaType.datastore.toString()); if (datastore == null) {
	 * logger.error("Datastore is not available for this datapod"); throw new
	 * Exception(); } IReader iReader = dataSourceFactory.getDatapodReader(datapod,
	 * commonActivity); String datasourceUUID =
	 * datapod.getDatasource().getRef().getUuid(); String datasourceVersion =
	 * datapod.getDatasource().getRef().getVersion(); Datasource datasource =
	 * (Datasource) commonActivity.getRefObject(new
	 * MetaIdentifier(MetaType.datasource, datasourceUUID, datasourceVersion));
	 * Datasource datasource_2 = commonServiceImpl.getDatasourceByApp(); IConnector
	 * conn = connFactory.getConnector(datasource_2.getType().toLowerCase());
	 * ConnectionHolder cPAUSEer = conn.getConnection();
	 * 
	 * Object obj = cPAUSEer.getStmtObject(); if(obj instanceof HiveContext) {
	 * DataFrameHolder dataFrameHolder = iReader.read(datapod, datastore,
	 * hdfsInfo,obj, datasource); Dataset<Row> df = dataFrameHolder.getDataframe();
	 * return df; } return null; }
	 */

	/********************** UNUSED **********************/
	/*
	 * public List<String> getAtributeValues(ResultSetHolder rsHolder, String
	 * attributeName){ List<String> result = new ArrayList<>(); Dataset<Row> df =
	 * rsHolder.getDataFrame(); Row[] rows = (Row[]) df.head(100);
	 * result.add(attributeName); for (Row row : rows) { for (int i = 0; i <
	 * row.size(); i++) { String columnValue = String.valueOf(row.get(i));
	 * result.add(columnValue); } } return result; }
	 */

	/********************** UNUSED **********************/
	/*
	 * public HSSFWorkbook
	 * getDownloadableContentsForDatapodResults(HttpServletResponse response, int
	 * rowLimit) throws SQLException { Dataset<Row> dfSorted = null; ResultSet
	 * rsSorted = null; ResultSetHolder rsHolder = null; HSSFWorkbook workbook = new
	 * HSSFWorkbook(); HSSFSheet sheet =
	 * workbook.createSheet(MetaType.datapod.toString()); ArrayList<String> al =
	 * null; ArrayList<ArrayList<String>> arlist = new
	 * ArrayList<ArrayList<String>>(); ServletRequestAttributes requestAttributes =
	 * (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	 * if(requestAttributes != null) { HttpServletRequest request =
	 * requestAttributes.getRequest(); if(request != null) { HttpSession session =
	 * request.getSession(); if(session != null) { rsHolder = (ResultSetHolder)
	 * session.getAttribute("rsHolder"); }else
	 * logger.info("HttpSession is \""+null+"\""); }else
	 * logger.info("HttpServletResponse is \""+null+"\""); }else
	 * logger.info("ServletRequestAttributes requestAttributes is \""+null+"\"");
	 * 
	 * if(rsHolder.getType().equals(ResultType.resultset)) { rsSorted =
	 * rsHolder.getResultSet(); ResultSetMetaData rsmd = rsSorted.getMetaData(); int
	 * numOfCols = rsmd.getColumnCount(); String[] columns = new String[numOfCols];
	 * for(int i=1; i<=numOfCols; i++) columns[i] = rsmd.getColumnName(i); try { al
	 * = new ArrayList<>(); for (String column : columns) { if(column.contains("."))
	 * al.add(column.substring(column.indexOf(".")+1)); else al.add(column); }
	 * arlist.add(al); rsSorted.first(); while(rsSorted.next()) { al = new
	 * ArrayList<>(); for (int i=0; i<numOfCols; i++) {
	 * al.add(String.valueOf(rsSorted.getObject(i))); for (int k = 0; k <
	 * arlist.size(); k++) { ArrayList<String> ardata = (ArrayList<String>)
	 * arlist.get(k); HSSFRow hssfRow = sheet.createRow((short) k); for (int p = 0;
	 * p < ardata.size(); p++) { HSSFCell cell = hssfRow.createCell((short) p);
	 * cell.setCellValue(ardata.get(p).toString()); } } } arlist.add(al); }
	 * //response.addHeader("Content-Disposition",
	 * "attachment; filename=/home/gridedge1/Datapod.xlsx"); ServletOutputStream sos
	 * = response.getOutputStream(); //workbook.write(sos);
	 * //sos.write(workbook.getBytes()); sos.close(); }catch (IOException e1) {
	 * e1.printStackTrace(); logger.info("exception caught while download file"); }
	 * }else { // // * DataStore dds = findDataStoreByMeta(uuid, version); String
	 * dtn = // * getTableName(ds.getUuid(), dds.getVersion()); // *
	 * logger.info("Datastore - Table name:"+dtn); // // DataFrame df =
	 * sqlContext.sql("select * from "+tn); dfSorted = rsHolder.getDataFrame();
	 * Row[] drows = (Row[]) dfSorted.head(rowLimit); //
	 * logger.info("Rows"+df.count()); String[] dcolumns = dfSorted.columns(); try {
	 * al = new ArrayList<>(); for (String column : dcolumns) { al.add(column); }
	 * arlist.add(al); for (Row row : drows) { al = new ArrayList<>(); for (int i =
	 * 0; i < dcolumns.length; i++) { al.add(String.valueOf(row.get(i)));
	 * 
	 * for (int k = 0; k < arlist.size(); k++) { ArrayList<String> ardata =
	 * (ArrayList<String>) arlist.get(k); HSSFRow hssfRow = sheet.createRow((short)
	 * k);
	 * 
	 * for (int p = 0; p < ardata.size(); p++) { HSSFCell cell =
	 * hssfRow.createCell((short) p); cell.setCellValue(ardata.get(p).toString()); }
	 * } } arlist.add(al); } // response.addHeader("Content-Disposition",
	 * "attachment; filename=Datapod.xlsx"); //ServletOutputStream sos =
	 * response.getOutputStream(); // workbook.write(sos);
	 * //sos.write(workbook.getBytes()); // sos.close(); }catch (Exception e1) {
	 * e1.printStackTrace(); logger.info("exception caught while download file"); }
	 * } return workbook; }
	 */

	/********************** UNUSED **********************/
	/*public List<Attribute> getAttributeList(String csvFileName, String parquetDir, boolean flag,
			boolean writeToParquet) {
		Dataset<Row> df = hiveContext.read().format("com.databricks.spark.csv").option("inferSchema", "true")
				.option("header", "true").load(csvFileName);
		df.printSchema();
		StructType st = df.schema();
		Seq<StructField> seqFields = st.thisCollection();
		Iterator<StructField> iter = st.iterator();
		List<Attribute> attributes = new ArrayList<Attribute>();
		int i = 0;
		while (iter.hasNext()) {
			StructField sf = iter.next();
			Attribute attr1 = new Attribute();
			attr1.setAttributeId(i++);
			attr1.setType(sf.dataType().typeName());
			attr1.setName(sf.name());
			attr1.setDesc(sf.name());
			attr1.setDispName(sf.name());
			attr1.setActive("Y");
			attributes.add(attr1);

		}
		if (flag) {
			Attribute attr2 = new Attribute();
			attr2.setAttributeId(i++);
			attr2.setType("Integer");
			attr2.setName("version");
			attr2.setDesc("version");
			attr2.setDispName("version");
			attr2.setActive("Y");
			attributes.add(attr2);
		}
		if (writeToParquet) {
			df.write().parquet(parquetDir);

		}
		logger.info("Length of seq:" + seqFields.length());
		return attributes;
	}*/

	/********************** UNUSED **********************/
	/*public DataFrameHolder getaDataFrameHolder(String filePath, Object conObject) {
		String tableName = "";
		Dataset<Row> df = null;
		DataFrameHolder dfm = new DataFrameHolder();
		hiveContext = (HiveContext) conObject;
		DataFrameReader reader = hiveContext.read();
		df = reader.load(filePath);
		tableName = Helper.genTableName(filePath);
		dfm.setDataframe(df);
		dfm.setTableName(tableName);
		return dfm;
	}*/

	/********************** UNUSED **********************/
	/*public void registerDatapod(IConnector conn, String tableName, Datapod dp, Datasource ds,
			DataStore dataStoreDetails, IReader iReader, ExecContext execContext) throws IOException {
		ConnectionHolder cPAUSEer = conn.getConnection();
		Object obj = cPAUSEer.getStmtObject();
		if (obj instanceof HiveContext && !execContext.equals(ExecContext.livy_spark)) {
			DataFrameHolder dataFrameHolder = iReader.read(dp, dataStoreDetails, hdfsInfo, obj, ds);
			Dataset<Row> df = dataFrameHolder.getDataframe();
			tableName = dataFrameHolder.getTableName();
			String[] tablenameList = ((HiveContext) obj).tableNames();
			boolean tableFound = false;
			if (tablenameList != null && tablenameList.length > 0) {
				for (String tname : tablenameList) {
					if (tname.equals(tableName)) {
						tableFound = true;
						break;
					}
				}
			}
			if (!tableFound) {
				df.persist(StorageLevel.MEMORY_AND_DISK());
				// df.cache();
				HiveContext hiveContext = (HiveContext) cPAUSEer.getStmtObject();
				hiveContext.registerDataFrameAsTable(df, tableName);
				// hiveContext.registerDataFrameAsTable(df, tableName);
				logger.info("datapodRegister: Registering datapod " + tableName);
				// hiveContext.registerDataFrameAsTable(df, tableName);
				df.printSchema();
			}
		}
	}*/

	/********************** UNUSED **********************/
	/*
	 * public Dataset<Row> getDataFrame(String filePath) { Dataset<Row> dfTmp =
	 * hiveContext.read().format("com.databricks.spark.csv").option("inferSchema",
	 * "true") .option("header", "true").load(filePath); return dfTmp; }
	 */

	/********************** UNUSED **********************/
	/*
	 * public void registerDataFrameAsTable(Dataset<Row> df, String string) {
	 * hiveContext.registerDataFrameAsTable(df, string); }
	 */
}
