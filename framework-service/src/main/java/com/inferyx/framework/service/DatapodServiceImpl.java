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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.Engine;
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.dao.IDataStoreDao;
import com.inferyx.framework.dao.IDatapodDao;
import com.inferyx.framework.dao.IDatasourceDao;
import com.inferyx.framework.dao.IUploadDao;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.CompareMetaData;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DatapodStatsHolder;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.Key;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.Profile;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.UploadExec;
import com.inferyx.framework.enums.Compare;
import com.inferyx.framework.enums.Layout;
import com.inferyx.framework.enums.RunMode;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.operator.DatasetOperator;
import com.inferyx.framework.operator.FormulaOperator;
import com.inferyx.framework.operator.HistogramOperator;
import com.inferyx.framework.operator.RelationOperator;
import com.inferyx.framework.operator.RuleOperator;
import com.inferyx.framework.register.DatapodRegister;
import com.inferyx.framework.register.GraphRegister;

@Service
public class DatapodServiceImpl {

	static final Logger logger = Logger.getLogger(DatapodServiceImpl.class);

	@Autowired
	DatapodRegister datapodRegister;	
	@Autowired
	GraphRegister<?> registerGraph;
	@Autowired
	IDatapodDao idatapodDao;
	@Autowired
	Helper helper;
	@Autowired
	IDataStoreDao idatastoreDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private static Datapod datapod;
	@Autowired
	private LoadServiceImpl loadServiceImpl;
	@Autowired
	private DataStoreServiceImpl datastoreServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	IDatasourceDao iDatasourceDao;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired 
	RegisterService registerService;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	ExecutorFactory execFactory;
	@Autowired
	DataSourceFactory dataSourceFactory;
	@Autowired
	HDFSInfo hdfsInfo;
	@Autowired
	protected DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	DataFrameService dataFrameService;
	@Autowired
	IUploadDao iDownloadDao;
	@Autowired
	private MessageServiceImpl messageServiceImpl;
	@Autowired
	Engine engine;
	@Autowired
	private HistogramOperator histogramOperator;
	@Autowired
	private IDatapodDao iDatapodDao;
	@Autowired
	private FormulaOperator formulaOperator;
	@Autowired
	private DatasetOperator datasetOperator;
	@Autowired
	private RuleOperator ruleOperator;
	@Autowired
	private RelationOperator relationOperator;
	@Autowired
	private DownloadServiceImpl downloadServiceImpl;
	
	public DatapodServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	public long count() throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
		return idatapodDao.count();
		}
	   Query query = new Query();
	    query.addCriteria(Criteria.where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()));
		query.addCriteria(Criteria.where("active").is("Y"));    
	   return mongoTemplate.count(query, Datapod.class);
	}

	
	
	public Datapod save(Datapod datapod) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		datapod.setAppInfo(metaIdentifierHolderList);
		datapod.setBaseEntity();
		Datapod dp=idatapodDao.save(datapod);
		registerGraph.updateGraph((Object) dp, MetaType.datapod);
		return dp;
	}	


	/********************** UNUSED **********************/
	/*public Datapod getOneByUuidAndVersion(String uuid, String version) {
		return idatapodDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Datapod update(Datapod datapod) {
		datapod.setBaseEntity();		
		return idatapodDao.save(datapod);
	}*/

	/********************** UNUSED **********************/
	/*public boolean isExists(String id) {
		return idatapodDao.exists(id);
	}*/


	/********************** UNUSED **********************/
	/*public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Datapod datapod = idatapodDao.findOneById(appUuid, Id);
		datapod.setActive("N");
		idatapodDao.save(datapod);
//		String ID = datapod.getId();
//		idatapodDao.delete(appUuid, ID);
//		datapod.exportBaseProperty();
	}*/

	/********************** UNUSED **********************/
	/*public List<Datapod> test(String param1) {
		return idatapodDao.test(param1);
	}*/

	/********************** UNUSED **********************/
	/*public Datapod findLatest() {
		return resolveName(idatapodDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED 
	 * @throws ParseException 
	 * @throws JsonProcessingException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException **********************/
	/*public Datapod findAllByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;	
				if(appUuid!=null)
					return idatapodDao.findAllByUuid(appUuid, uuid);
				else
					return idatapodDao.findAllByUuid(uuid);
	}*/

	public Datapod findOneByName(String fileName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JsonProcessingException, ParseException {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		//String appUuid = "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cb";
		Datapod dp=idatapodDao.findOneByFileName(appUuid, fileName);
		if (dp != null)
			dp = (Datapod) commonServiceImpl.resolveName(dp.getUuid(), MetaType.datapod.toString());

		return dp;
	}

	/********************** UNUSED **********************/
	/*public Datapod findOneById(String id) {
		System.out.print(id);
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;

				if(appUuid != null){
					logger.info("get" + idatapodDao.findOneById(appUuid, id));
					return idatapodDao.findOneById(appUuid, id);
				}else
					return idatapodDao.findOneById(id);
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public String getAttributeName(Datapod datapod, int attrId) {
		return datapod.getAttribute(attrId).getName();
	}*/
// Comment by Vaibhav due to dosent have any refran..
/*	public String getAttributeName(Datapod datapod, String attributeId) {
		List<Attribute> attributes = datapod.getAttributes();
		for (Attribute attribute : attributes) {
			if (attribute.getAttributeId() != null 
				&& attribute.getAttributeId().equals(attributeId)) {
				return attribute.getName();
			}
		}
		return null;
	}*/
	
	// This method returns the attributeName of the ref object
	public String getAttributeName(String uuid, int attrId) throws JsonProcessingException {
		String alias = null;
		datapod = (Datapod) commonServiceImpl.getLatestByUuid(uuid, MetaType.datapod.toString());
		alias = datapod.getAttribute(attrId).getName();
		return alias;
	}

	public StructType populateSchema(String dpuuid, String version) throws JsonProcessingException {
		Datapod datapod;
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		if (null != version) {
			//datapod = idatapodDao.findLatestByUuid(appUuid, dpuuid, new Sort(Sort.Direction.DESC, "version"));
			datapod = (Datapod) commonServiceImpl.getLatestByUuid(dpuuid, MetaType.datapod.toString());
		} else {
			//datapod = idatapodDao.findOneByUuidAndVersion(appUuid, dpuuid, version);
			datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(dpuuid, version, MetaType.datapod.toString());
		}

		List<Attribute> attrList = datapod.getAttributes();
		List<StructField> fields = new ArrayList<StructField>();
		for (Attribute attr : attrList) {
			DataType dt = DataTypes.StringType;
			if (attr.getType().equalsIgnoreCase("string")) {
				dt = DataTypes.StringType;
			} else if (attr.getType().equalsIgnoreCase("double")) {
				dt = DataTypes.DoubleType;
			} else if (attr.getType().equalsIgnoreCase("integer")) {
				dt = DataTypes.IntegerType;
			} else if (attr.getType().equalsIgnoreCase("float")) {
				dt = DataTypes.FloatType;
			} else if (attr.getType().equalsIgnoreCase("boolean")) {
				dt = DataTypes.BooleanType;
			} else if (attr.getType().equalsIgnoreCase("date")) {
				dt = DataTypes.DateType;
			} else if (attr.getType().equalsIgnoreCase("long")) {
				dt = DataTypes.LongType;
			}
			fields.add(DataTypes.createStructField(attr.getName(), dt, true));
		}
		return DataTypes.createStructType(fields);

	}

	public MetaIdentifierHolder createAndLoad(String csvFileName, RunMode runMode) throws Exception {		
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		// Check datapod whether it exists
		String fileName = Helper.getFileName(csvFileName).toLowerCase();
		
		// Creating datapod	
		Datapod dp = new Datapod();
		
		// Create datapod and relation if it does not exist
		IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
		List<Attribute> attributes = exec.fetchAttributeList(csvFileName, null, true, false, appUuid);
		logger.info("Attributes:" + attributes);

		Datapod tempDatapod = findOneByName(fileName);
		if(tempDatapod !=null) {
			dp.setUuid(tempDatapod.getUuid());
		}
		Datasource datasource = commonServiceImpl.getDatasourceByApp();
		MetaIdentifier datasourceRef = new MetaIdentifier(MetaType.datasource, datasource.getUuid(),
				datasource.getVersion());
		MetaIdentifierHolder mHolder = new MetaIdentifierHolder();
		mHolder.setRef(datasourceRef);
		dp.setDatasource(mHolder);
		dp.setCache("Y");
		dp.setName(fileName.toLowerCase());
		dp.setAttributes(attributes);
		
		try {
			dp = save(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* Code for UploadExec*/	
		String uploadPath = csvFileName;		
		Load load = new Load();
		AttributeRefHolder loadSourceMIHolder = new AttributeRefHolder();
		MetaIdentifier loadSourceMIdentifier = new MetaIdentifier();
		
		loadSourceMIdentifier.setType(MetaType.datasource);
		loadSourceMIdentifier.setUuid(dp.getDatasource().getRef().getUuid());
		loadSourceMIHolder.setRef(loadSourceMIdentifier);
		loadSourceMIHolder.setValue(uploadPath);
		load.setSource(loadSourceMIHolder);
		load.setActive("Y");
		load.setAppend("Y");
		load.setHeader("Y");
		load.setName(fileName);

		AttributeRefHolder loadTargetMih = new AttributeRefHolder();
		MetaIdentifier loadTargetMi = new MetaIdentifier();
		loadTargetMi.setType(MetaType.datapod);
		loadTargetMi.setUuid(dp.getUuid());
		loadTargetMi.setVersion(dp.getVersion());
		loadTargetMih.setRef(loadTargetMi);

		load.setTarget(loadTargetMih);
		load = loadServiceImpl.save(load);
		
		//Create Load exec and datastore
		LoadExec loadExec = null;
		loadExec = loadServiceImpl.create(load.getUuid(), load.getVersion(), null, null, loadExec);
		try {
			loadServiceImpl.executeSql(loadExec, null, fileName, new OrderKey(dp.getUuid(), dp.getVersion()), RunMode.BATCH, null);
			return new MetaIdentifierHolder(loadExec.getRef(MetaType.loadExec));
		} catch (Exception e) {
			iDatapodDao.delete(dp);
			throw new RuntimeException(e);
		}
	}

	// Generate excel file from datapod
	/*
	 * public String genDataDict(String path, String multiTab) throws
	 * IOException, FileNotFoundException, InvalidFormatException { // Create
	 * multiple tabs in a single document for datapods if
	 * (multiTab.equalsIgnoreCase("y")) { try {
	 * 
	 * Integer id = null; String name = null, type = null, desc = null;
	 * 
	 * List<Datapod> datapods = findAllLatest(); HSSFWorkbook workbook = new
	 * HSSFWorkbook();
	 * 
	 * for (Datapod datapod : datapods) {
	 * 
	 * ArrayList<String> al = null; ArrayList<ArrayList<String>> arlist = new
	 * ArrayList<ArrayList<String>>();
	 * 
	 * HSSFSheet sheet = workbook.createSheet(datapod.getName());
	 * 
	 * List<Attribute> attributes = datapod.getAttributes(); al = new
	 * ArrayList<>(); al.add("Id"); al.add("Name"); al.add("Type");
	 * al.add("Desc"); arlist.add(al); for (Attribute attr : attributes) {
	 * 
	 * al = new ArrayList<String>();
	 * 
	 * id = attr.getAttributeId(); name = attr.getName(); type = attr.getType();
	 * desc = attr.getDesc();
	 * 
	 * al.add(id.toString()); al.add(name); al.add(type); al.add(desc);
	 * arlist.add(al);
	 * 
	 * for (int k = 0; k < arlist.size(); k++) {
	 * 
	 * ArrayList<String> ardata = (ArrayList<String>) arlist.get(k);
	 * 
	 * HSSFRow row = sheet.createRow((short) k);
	 * 
	 * for (int p = 0; p < ardata.size(); p++) {
	 * 
	 * HSSFCell cell = row.createCell((short) p);
	 * cell.setCellValue(ardata.get(p).toString()); } }
	 * 
	 * }
	 * 
	 * FileOutputStream fileOut = new FileOutputStream(path + "/Datapods.xlsx");
	 * workbook.write(fileOut); fileOut.flush(); fileOut.close();
	 * 
	 * }
	 * 
	 * } catch (Exception ex) { logger.info(ex);
	 * 
	 * } }
	 * 
	 * // Create single document for all datapods else if
	 * (multiTab.equalsIgnoreCase("n")) { File file = null; PrintWriter pw =
	 * null; StringBuilder sb = new StringBuilder(); Integer id = null; // TODO
	 * Auto-generated method stub String name = null, type = null, desc = null;
	 * 
	 * List<Datapod> datapod = findAllLatest(); file = new File(path +
	 * "/BeforeAspect.xlsx"); try { sb.append("Datapod,AttributeId,Name,Type,Desc");
	 * sb.append("\n"); for (Datapod data : datapod) { pw = new
	 * PrintWriter(file); List<Attribute> attrb = data.getAttributes();
	 * 
	 * for (Attribute attr : attrb) { id = attr.getAttributeId(); name =
	 * attr.getName(); type = attr.getType(); desc = attr.getDesc();
	 * 
	 * sb.append(data.getName()); sb.append(','); sb.append(id); sb.append(',');
	 * sb.append(name); sb.append(','); sb.append(type); sb.append(',');
	 * sb.append(desc); sb.append('\n');
	 * 
	 * } pw.write(sb.toString()); } } catch (Exception ex) {
	 * ex.printStackTrace(); } finally { pw.close(); }
	 * 
	 * } return "Your excel file has been generated in folder:" + path; }
	 */

	// Generate excel file from datapod
	@SuppressWarnings({ "resource", "unchecked" })
	public void genDataDict(String multitab, HttpServletResponse response) {
		response.setContentType("application/xml charset=utf-16");
		try {
			// Create multiple tabs in a single document for datapods
			if (multitab.equalsIgnoreCase("y")) {
				Integer id = null;
				String name = null, type = null, desc = null;
				//List<Datapod> datapods = findAllLatest();
				List<Datapod> datapods = commonServiceImpl.findAllLatest(MetaType.datapod);
				HSSFWorkbook workbook = new HSSFWorkbook();

				for (Datapod datapod : datapods) {
					ArrayList<String> al = null;
					ArrayList<ArrayList<String>> arlist = new ArrayList<ArrayList<String>>();
					HSSFSheet sheet = workbook.createSheet(datapod.getName());
					List<Attribute> attributes = datapod.getAttributes();
					al = new ArrayList<>();
					al.add("Id");
					al.add("Name");
					al.add("Type");
					al.add("Desc");
					arlist.add(al);

					for (Attribute attr : attributes) {
						al = new ArrayList<String>();
						id = attr.getAttributeId();
						name = attr.getName();
						type = attr.getType();
						desc = attr.getDesc();
						al.add(id.toString());
						al.add(name);
						al.add(type);
						al.add(desc);
						arlist.add(al);

						for (int k = 0; k < arlist.size(); k++) {
							ArrayList<String> ardata = (ArrayList<String>) arlist.get(k);
							HSSFRow row = sheet.createRow((short) k);

							for (int p = 0; p < ardata.size(); p++) {
								HSSFCell cell = row.createCell((short) p);
								cell.setCellValue(ardata.get(p).toString());
							}
						}
					}
				}
				response.addHeader("Content-Disposition", "attachment; filename=Datapods.xlsx");
				ServletOutputStream sos = response.getOutputStream();
				workbook.write(sos);
				sos.write(workbook.getBytes());
				sos.close();

			} else if (multitab.equalsIgnoreCase("n")) {
				StringBuilder sb = new StringBuilder();
				Integer id = null;
				String name = null, type = null, desc = null;
				//List<Datapod> datapod = findAllLatest();
				List<Datapod> datapod = commonServiceImpl.findAllLatest(MetaType.datapod);
				sb.append("Datapod,AttributeId,Name,Type,Desc");
				sb.append("\n");

				for (Datapod data : datapod) {
					List<Attribute> attrb = data.getAttributes();

					for (Attribute attr : attrb) {
						id = attr.getAttributeId();
						name = attr.getName();
						type = attr.getType();
						desc = attr.getDesc();
						sb.append(data.getName());
						sb.append(',');
						sb.append(id);
						sb.append(',');
						sb.append(name);
						sb.append(',');
						sb.append(type);
						sb.append(',');
						sb.append(desc);
						sb.append('\n');
					}
				}
				logger.info("sb.toString()-->" + sb.toString());
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] stringByte = sb.toString().getBytes();
				bos.write(stringByte);
				response.addHeader("Content-Disposition", "attachment; filename=BeforeAspect.xlsx");
				logger.info("bos: " + bos.size());
				response.getOutputStream().write(bos.toByteArray());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			logger.info("exception caught while download file");
		}
	}

	/********************** UNUSED **********************/
	/*public List<Datapod> findAllLatestActive()

	{
		Aggregation datapodAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Datapod> datapodResults = mongoTemplate.aggregate(datapodAggr, "datapod", Datapod.class);
		List<Datapod> datapodList = datapodResults.getMappedResults();

		// Fetch the datapod details for each id
		List<Datapod> result = new ArrayList<Datapod>();
		for (Datapod d : datapodList) {
			Datapod datapodLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				datapodLatest = idatapodDao.findOneByUuidAndVersion(appUuid, d.getId(), d.getVersion());
			} else {
				datapodLatest = idatapodDao.findOneByUuidAndVersion(d.getId(), d.getVersion());
			}
			if(datapodLatest != null)
			{
			result.add(datapodLatest);
			}
		}
		return result;
	}
*/


	public List<Datapod> searchDatapodByName(String name, String datasourceUuid) throws JsonProcessingException {	
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;		
		 Aggregation filterAggr = 
					newAggregation(	
					match(Criteria.where("appInfo.ref.uuid").is(appUuid).andOperator(Criteria.where("name").is(name))),
					group("uuid").max("version").as("version"));

				 AggregationResults<Datapod> groupResults 
					= mongoTemplate.aggregate(filterAggr, MetaType.datapod.toString(), Datapod.class);
				List<Datapod> datapodList = groupResults.getMappedResults();
				List<Datapod> result = new ArrayList<Datapod>();
				for (Datapod datapod : datapodList) {
					//Datapod datapodLatest = idatapodDao.findOneByUuidAndVersion(d.getId(), d.getVersion());
					Datapod datapodLatest = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapod.getId(), datapod.getVersion(), MetaType.datapod.toString());
					if(datapodLatest != null)
						result.add(datapodLatest);
				}				
		return result;
}

	@SuppressWarnings("unchecked")
	public List<DatapodStatsHolder> getDatapodStats() throws JsonProcessingException {		
		List<DatapodStatsHolder> result = new ArrayList<DatapodStatsHolder>();
		//List<Datapod> datapodList = findAllLatest();
		List<Datapod> datapodList = commonServiceImpl.findAllLatest(MetaType.datapod);
		//List<Datasource> dsList = datasourceServiceImpl.findAllLatest();
		List<Datasource> dsList = commonServiceImpl.findAllLatest(MetaType.datasource);
		Map<String,String> dsMap = new HashMap<String,String>();
		for (Datasource dsrc : dsList) {
			dsMap.put(dsrc.getUuid(), dsrc.getName());
		}
		
		for (Datapod dp : datapodList) {

			DatapodStatsHolder dsh = new DatapodStatsHolder();
			MetaIdentifier mi = new MetaIdentifier();
			mi.setType(MetaType.datapod);
			mi.setUuid(dp.getUuid());
			mi.setVersion(dp.getVersion());
			mi.setName(dp.getName());
			dsh.setRef(mi);
			dsh.setDataSource(dsMap.get(dp.getDatasource().getRef().getUuid()));

			DataStore ds = datastoreServiceImpl.findLatestByMeta(dp.getUuid(),dp.getVersion());
			if (ds != null) {
				dsh.setNumRows(ds.getNumRows());
				dsh.setLastUpdatedOn(ds.getCreatedOn());
			}
			result.add(dsh);			
			
		}
			return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<DatapodStatsHolder> getDatapodStats(String searchStr) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {

		List<DatapodStatsHolder> result = new ArrayList<DatapodStatsHolder>();
		List<Datapod> datapodList = commonServiceImpl.findAllLatest(MetaType.datapod, searchStr);
		List<Datasource> dsList = commonServiceImpl.findAllLatest(MetaType.datasource);
		
		/*Application application = commonServiceImpl.getApp();
		Criteria criteria = new Criteria();
		Query query = new Query();
		String appUuid=application.getUuid();
		
		if(appUuid != null && !appUuid.isEmpty()) {
			query.addCriteria(Criteria.where("_id").ne("1").orOperator(where("appInfo.ref.uuid").is(appUuid),where("publicFlag").is("Y")));
			
		}*/
		
		Map<String, String> dsMap = new HashMap<String, String>();
		for (Datasource dsrc : dsList) {
			dsMap.put(dsrc.getUuid(), dsrc.getName());
		}

		for (Datapod dp : datapodList) {

			DatapodStatsHolder dsh = new DatapodStatsHolder();
			MetaIdentifier mi = new MetaIdentifier();
			mi.setType(MetaType.datapod);
			mi.setUuid(dp.getUuid());
			mi.setVersion(dp.getVersion());
			mi.setName(dp.getName());
			dsh.setRef(mi);
			dsh.setDataSource(dsMap.get(dp.getDatasource().getRef().getUuid()));

			DataStore ds = datastoreServiceImpl.findLatestByMeta(dp.getUuid(), dp.getVersion());
			
			if (ds != null) {
				dsh.setNumRows(datastoreServiceImpl.getDataStoreTotalCountByDatapod(dp.getUuid(),MetaType.datapod.toString()));
				dsh.setLastUpdatedOn(ds.getCreatedOn());
			}
			result.add(dsh);

		}
		return result;
	}
	

	@SuppressWarnings("unchecked")
	public List<DatapodStatsHolder> getDatapodStats2()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NullPointerException, ParseException, IOException {
		List<DatapodStatsHolder> result = new ArrayList<DatapodStatsHolder>();
		List<Datapod> datapodList = commonServiceImpl.findAllLatest(MetaType.datapod);
		List<Datasource> dsList = commonServiceImpl.findAllLatest(MetaType.datasource);
		Map<String,String> dsMap = new HashMap<String,String>();
		for (Datasource dsrc : dsList) {
			dsMap.put(dsrc.getUuid(), dsrc.getName());
		}
		for (Datapod dp : datapodList) {

			DatapodStatsHolder dsh = new DatapodStatsHolder();
			MetaIdentifier mi = new MetaIdentifier();
			mi.setType(MetaType.datapod);
			mi.setUuid(dp.getUuid());
			mi.setVersion(dp.getVersion());
			mi.setName(dp.getName());
			dsh.setRef(mi);
			dsh.setDataSource(dsMap.get(dp.getDatasource().getRef().getUuid()));
			
			Query query = new Query();
			try {
				query.fields().include("uuid");
				query.fields().include("version");
				query.fields().include("name");
				query.fields().include("type");
				query.fields().include("dependsOn");
				query.fields().include("createdOn");
				query.fields().include("appInfo"); 
				
				query.addCriteria(Criteria.where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()));
				query.addCriteria(Criteria.where("active").is("Y")); 
				query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(dp.getUuid()));
				//query.addCriteria(Criteria.where("dependsOn.ref.version").is(dp.getVersion()));
				query.with(new Sort(Sort.Direction.DESC, "version"));
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			List<Profile> profileObjectList = new ArrayList<>();
			profileObjectList = (List<Profile>) mongoTemplate.find(query, Profile.class);

			Query query2 = new Query();
			if(profileObjectList.size() > 0) {
				try {
					query2.fields().include("uuid");
					query2.fields().include("version");
					query2.fields().include("name");
					query2.fields().include("type");
					query2.fields().include("exec");
					query2.fields().include("dependsOn");
					query2.fields().include("createdOn");
					query2.fields().include("result");
					query2.fields().include("statusList");
					query2.fields().include("appInfo");
					
					query2.addCriteria(Criteria.where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()));
					query2.addCriteria(Criteria.where("active").is("Y")); 
					query2.addCriteria(Criteria.where("statusList.stage").in(Status.Stage.COMPLETED.toString()));
					query2.addCriteria(Criteria.where("dependsOn.ref.uuid").is(profileObjectList.get(0).getUuid()));
					//query2.addCriteria(Criteria.where("dependsOn.ref.version").is(profileObjectList.get(0).getVersion()));
					query2.with(new Sort(Sort.Direction.DESC, "version"));
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				List<ProfileExec> profileExecObjList = new ArrayList<>();
				profileExecObjList = (List<ProfileExec>) mongoTemplate.find(query2, ProfileExec.class);
				
				if(profileExecObjList.size() > 0) {
					ProfileExec profileExec = profileExecObjList.get(0);
					String profileQuery = profileExec.getExec();
					
					String appUuid = commonServiceImpl.getApp().getUuid();
					
					Datasource datasource = commonServiceImpl.getDatasourceByApp();
					IExecutor exec = execFactory.getExecutor(datasource.getType());
					try {
						List<Map<String, Object>> data = exec.executeAndFetch(profileQuery, appUuid);
						Long numRows = (Long) data.get(0).get("numRows");
						dsh.setNumRows(numRows);
						dsh.setLastUpdatedOn(profileExec.getCreatedOn());
					} catch (Exception e) {
						/*DataStore ds = datastoreServiceImpl.findLatestByMeta(dp.getUuid(),dp.getVersion());
						if (ds != null) {
							dsh.setNumRows(ds.getNumRows());
							dsh.setLastUpdatedOn(ds.getCreatedOn());
						}*/
						dsh.setNumRows(0L);
						dsh.setLastUpdatedOn("");
					}
					
				} else {
					/*DataStore ds = datastoreServiceImpl.findLatestByMeta(dp.getUuid(),dp.getVersion());
					if (ds != null) {
					dsh.setNumRows(ds.getNumRows());
					dsh.setLastUpdatedOn(ds.getCreatedOn());
				}*/
				dsh.setNumRows(0L);
				dsh.setLastUpdatedOn("");
				}
				
			} else {
				/*DataStore ds = datastoreServiceImpl.findLatestByMeta(dp.getUuid(),dp.getVersion());
				if (ds != null) {
				dsh.setNumRows(ds.getNumRows());
				dsh.setLastUpdatedOn(ds.getCreatedOn());
			}*/
			dsh.setNumRows(0L);
			dsh.setLastUpdatedOn("");
			}
			
			/*DataStore ds = datastoreServiceImpl.findLatestByMeta(dp.getUuid(),dp.getVersion());
			if (ds != null) {
				dsh.setNumRows(ds.getNumRows());
				dsh.setLastUpdatedOn(ds.getCreatedOn());
			}*/
			result.add(dsh);			
			
		}
			return result;
	}
	
	public void upload(MultipartFile csvFile, String datapodUuid, String desc) throws JsonProcessingException, JSONException, ParseException {		
		String csvFileName = csvFile.getOriginalFilename();
		UploadExec uploadExec=new UploadExec();	
		Status status = new Status(Status.Stage.PENDING, new Date());
		List<Status> statusList = new ArrayList<>();
		statusList.add(status);
		uploadExec.setStatusList(statusList);
		try {
			//patern matching for csv filename
			Pattern pattern = Pattern.compile("[ !@#$%&*()+=|<>?{}\\[\\]~-]");
			Matcher match = pattern.matcher(csvFileName);
			boolean isMatched = match.find();			
			if (isMatched || csvFileName.contains(" ")) {
				throw new Exception("CSV file name contains white space or special character");
			}
			
			String directory = Helper.getPropertyValue("framework.file.upload.path");
			String uploadPath = directory.endsWith("/") ? (directory + csvFileName) : (directory+"/"+csvFileName);
		
		
			uploadExec.setBaseEntity();
			// Copy file to server location
			File file = new File(uploadPath);
			csvFile.transferTo(file);
		 
			uploadPath = hdfsInfo.getHdfsURL() + uploadPath;
			Datapod datapod = (Datapod) commonServiceImpl.getLatestByUuid(datapodUuid, MetaType.datapod.toString());
			uploadExec.setLocation(uploadPath);
			uploadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.datapod,datapod.getUuid(),datapod.getVersion())));
			uploadExec.setFileName(csvFileName);
			uploadExec.setName(Helper.getFileName(csvFileName));
			
		
			//Check datapod and csv attributes name
//			Boolean flag = true;
			String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
			String parquetDir = null;
			IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
			
			List<Attribute> attributes = exec.fetchAttributeList(uploadPath, parquetDir, true, false, appUuid);
			//attributes.remove(attributes.size()-1);
			ListIterator<Attribute> attributeIterator = attributes.listIterator();
			
			List<Attribute> dpAttrs = datapod.getAttributes();
			
	
			int count = 0;
			for(int i=0; i<dpAttrs.size();i++) {
				if(dpAttrs.get(i).getName().contentEquals("version")) {	
					if(!attributes.get(attributes.size()-1).getName().contentEquals("version")) {
						dpAttrs.remove(dpAttrs.get(i));
						count++;
					}
				}	
				if(count>1) {
					throw new Exception("2 Version column in datapod");
				}
			}
			
			if(attributes.size()==dpAttrs.size()) {
				for(Attribute dpAttr : dpAttrs) {					
					if(attributeIterator.hasNext()) {
						Attribute attribute  = attributeIterator.next();				
						
						if (Character.isDigit(attribute.getName().charAt(0))) {
							status = new Status(Status.Stage.FAILED, new Date());
							statusList.add(status);
							uploadExec.setStatusList(statusList);
							commonServiceImpl.save(MetaType.uploadExec.toString(), uploadExec);
							throw new Exception("CSV file column name contains <b>Numeric value</b>.");
						}
						if (!attribute.getName().equalsIgnoreCase(dpAttr.getName())) {
							status = new Status(Status.Stage.FAILED, new Date());
							statusList.add(status);
							uploadExec.setStatusList(statusList);
							commonServiceImpl.save(MetaType.uploadExec.toString(), uploadExec);
							logger.info("CSV Column not matched : " + attribute.getName());
							throw new Exception("CSV Column not matched:<b>" + attribute.getName()
									+ "</b> Position:<b>" + (attributeIterator.nextIndex() + "</b>"));
						}
					}	
				}
			} else {
				status = new Status(Status.Stage.FAILED, new Date());
				statusList.add(status);
				uploadExec.setStatusList(statusList);
				commonServiceImpl.save(MetaType.uploadExec.toString(), uploadExec);
				throw new Exception("CSV and Datapod column not matched");
			}
				
			
			   
			// Load datapod using load object
			String fileName = Helper.getFileName(csvFileName);
			Load load = new Load();
			AttributeRefHolder loadSourceMIHolder = new AttributeRefHolder();
			MetaIdentifier loadSourceMIdentifier = new MetaIdentifier();
			
			loadSourceMIdentifier.setType(MetaType.datasource);
			loadSourceMIdentifier.setUuid(datapod.getDatasource().getRef().getUuid());
			loadSourceMIHolder.setRef(loadSourceMIdentifier);
			loadSourceMIHolder.setValue(uploadPath);
			load.setSource(loadSourceMIHolder);
			load.setActive("Y");
			load.setAppend("Y");
			load.setHeader("Y");
			load.setName(fileName);

			AttributeRefHolder loadTargetMih = new AttributeRefHolder();
			MetaIdentifier loadTargetMi = new MetaIdentifier();
			loadTargetMi.setType(MetaType.datapod);
			loadTargetMi.setUuid(datapodUuid);
			loadTargetMi.setVersion(datapod.getVersion());
			loadTargetMih.setRef(loadTargetMi);

			load.setTarget(loadTargetMih);
			load = loadServiceImpl.save(load);
			
			//Create Load exec and datastore
			LoadExec loadExec = null;
			loadExec = loadServiceImpl.create(load.getUuid(), load.getVersion(), null, null, loadExec);
			status = new Status(Status.Stage.RUNNING, new Date());
			statusList.add(status);
			uploadExec.setStatusList(statusList);
			loadServiceImpl.executeSql(loadExec, null, fileName, new OrderKey(datapod.getUuid(), datapod.getVersion()),
					RunMode.BATCH, desc);
			status = new Status(Status.Stage.COMPLETED, new Date());
			statusList.add(status);
			uploadExec.setStatusList(statusList);
			commonServiceImpl.save(MetaType.uploadExec.toString(), uploadExec);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException | NullPointerException
				| ParseException | IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			status = new Status(Status.Stage.FAILED, new Date());
			statusList.add(status);
			uploadExec.setStatusList(statusList);
			commonServiceImpl.save(MetaType.uploadExec.toString(), uploadExec);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			status = new Status(Status.Stage.FAILED, new Date());
			statusList.add(status);
			uploadExec.setStatusList(statusList);
			commonServiceImpl.save(MetaType.uploadExec.toString(), uploadExec);
			setResponseMsg(e.getMessage());			
		}	
	}

	public HttpServletResponse download(String datastoreUuid, String datastoreVersion, String format, int offset,
			int limit, HttpServletResponse response, int rowLimit, String sortBy, String order, String requestId,
			RunMode runMode, Layout layout) throws Exception {
		datastoreServiceImpl.setRunMode(runMode);
		DataStore dataStore = datastoreServiceImpl.findDataStoreByMeta(datastoreUuid, datastoreVersion);
		if (dataStore == null) {
			logger.error("Datastore is not available for this datapod");
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Datastore is not available for this datapod", null);
			throw new RuntimeException("Datastore is not available for this datapod");
		}
		
		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.download.maxrows"));
		if(rowLimit > maxRows) {
			logger.error("Requested rows exceeded the limit of "+maxRows);
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Requested rows exceeded the limit of "+maxRows, null);
			throw new RuntimeException("Requested rows exceeded the limit of "+maxRows);
		}
		
		datastoreServiceImpl.setRunMode(runMode);
		List<Map<String, Object>> results = datastoreServiceImpl.getDatapodResults(dataStore.getUuid(), dataStore.getVersion(), null,
				0, rowLimit, response, rowLimit, null, null, null, runMode);
		response = downloadServiceImpl.download(format, response, runMode, results, dataStore.getExecId(), layout,
				null, false, "framework.file.download.path", null, dataStore.getMetaId());
		return response;

	}
	
	public void setResponseMsg(String msg){
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		if (requestAttributes != null) {
			HttpServletResponse response = requestAttributes.getResponse();
			if (response != null) {
				response.setContentType("application/json");
				Message message = new Message("406", MessageStatus.FAIL.toString(), msg);
				Message savedMessage;
					try {
						savedMessage = messageServiceImpl.save(message);
						ObjectMapper mapper = new ObjectMapper();
						String messageJson = mapper.writeValueAsString(savedMessage);
						response.setContentType("application/json");
						response.setStatus(406);
						response.getOutputStream().write(messageJson.getBytes());
						response.getOutputStream().close();
						
					} catch (IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException | NullPointerException
							| JSONException | ParseException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
			} else {
				logger.info("HttpServletResponse response is \"" + null + "\"");
			}
		} else {
			logger.info("ServletRequestAttributes requestAttributes is \"" + null + "\"");
		}
	}

//	public String genTableNameByDatapod(Datapod datapod, String execversion, List<String> datapodList, HashMap<String, String> otherParams, DagExec dagExec, RunMode runMode, boolean getFromgetTableNameByDatapodKey) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
//		Datasource datapodDs = commonServiceImpl.getDatasourceByDatapod(datapod);		
//		if(runMode.equals(RunMode.BATCH)) {
//			if (!datapodDs.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
//				return datapodDs.getDbname() + "." + datapod.getName();
//			} else {
//				return String.format("%s_%s_%s", datapod.getUuid().replace("-", "_"), datapod.getVersion(), execversion);
//			}
//		} else {
//			return String.format("%s_%s_%s", datapod.getUuid().replace("-", "_"), datapod.getVersion(), execversion);
//		}		
//	}

	public String genTableNameByDatapod(Datapod datapod, String execversion, List<String> datapodList,
			HashMap<String, String> otherParams, DagExec dagExec, RunMode runMode, boolean getFromgetTableNameByDatapodKey)
			throws Exception {
		logger.info(" OtherParams : datapod : " + otherParams + " : " + datapod.getUuid());
		if (runMode.equals(RunMode.ONLINE) && datapodList != null && datapodList.contains(datapod.getUuid())) {
			return String.format("%s_%s_%s", datapod.getUuid().replaceAll("-", "_"), datapod.getVersion(),
					dagExec.getVersion());
		} else if (otherParams != null && otherParams.containsKey("datapodUuid_" + datapod.getUuid() + "_tableName")) {
			return otherParams.get("datapodUuid_" + datapod.getUuid() + "_tableName");
		}
		
		if(getFromgetTableNameByDatapodKey) {
			try {
				return getTableNameByDatapodKey(new OrderKey(datapod.getUuid(), datapod.getVersion()),
						runMode);
			} catch (Exception e) {
				logger.info(e);
				throw e;
				// TODO: handle exception
			} 
		}
		
		Datasource datapodDs = commonServiceImpl.getDatasourceByDatapod(datapod);
		if (runMode.equals(RunMode.BATCH)) {
			if (!datapodDs.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
				return datapodDs.getDbname() + "." + datapod.getName();
			} else {
				return String.format("%s_%s_%s", datapod.getUuid().replace("-", "_"), datapod.getVersion(),
						execversion);
			}
		} else {
			return String.format("%s_%s_%s", datapod.getUuid().replace("-", "_"), datapod.getVersion(), execversion);
		}
	}
	
	public List<CompareMetaData> compareMetadata(String datapodUuid, String datapodVersion, RunMode runMode) throws Exception {
		Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodUuid, datapodVersion, MetaType.datapod.toString());
		MetaIdentifier dsMI = targetDatapod.getDatasource().getRef();
		Datasource targetDS = (Datasource) commonServiceImpl.getOneByUuidAndVersion(dsMI.getUuid(), dsMI.getVersion(), dsMI.getType().toString());
		IExecutor exec = execFactory.getExecutor(targetDS.getType());
		
		String sourceTableName = null;
		try {
			sourceTableName = getTableNameByDatapodKey(new OrderKey(datapodUuid, datapodVersion), runMode);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return exec.compareMetadata(targetDatapod, targetDS, sourceTableName);
	}

	public String getTableNameByDatapodKey(Key sourceAttr, RunMode runMode) throws Exception {

		Datapod dp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(sourceAttr.getUUID(), sourceAttr.getVersion(), MetaType.datapod.toString(), "N");
		return getTableNameByDatapod(dp, runMode);

	}
	
	public String getTableNameByDatapodKey(String uuid, String version, RunMode runMode) throws Exception {

		Datapod dp = (Datapod) commonServiceImpl.getOneByUuidAndVersion(uuid, version, MetaType.datapod.toString(), "N");
		return getTableNameByDatapod(dp, runMode);

	}

	public String getTableNameByDatapod(Datapod dp, RunMode runMode) throws Exception {

//		Datasource ds = (Datasource) commonServiceImpl.getLatestByUuid(dp.getDatasource().getRef().getUuid(), MetaType.datasource.toString(), "N");
//		if (!ds.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
//			String tableName = ds.getDbname() + "." + dp.getName();
//			return tableName;
//		} 

		DataStore datastore = dataStoreServiceImpl.findLatestByMeta(dp.getUuid(), dp.getVersion());
		if (datastore == null) {
			logger.error("No datastore found for datapod "+dp.getName()+".");
			throw new Exception("No datastore found for datapod "+dp.getName()+".");
		}

		return dataStoreServiceImpl.getTableNameByDatastore(datastore, runMode);
//		
//		String hdfsLocation = String.format("%s%s", hdfsInfo.getHdfsURL(), hdfsInfo.getSchemaPath());
//		String filePath = datastore.getLocation();
//		filePath = String.format("%s%s", hdfsLocation, filePath);
//		String tableName = Helper.genTableName(filePath);
//
//		if (datastore.getPersistMode() == null || !datastore.getPersistMode().equals("MEMORY_ONLY")) {
//			datapodRegister.registerDatapod(datastore, dp, runMode);
//		}	
		
	}

	public Datapod synchronizeMetadata(String datapodUuid, String datapodVersion, RunMode runMode) throws IOException, JSONException, ParseException {
		Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodUuid, datapodVersion, MetaType.datapod.toString());
		MetaIdentifier dsMI = targetDatapod.getDatasource().getRef();
		Datasource targetDS = (Datasource) commonServiceImpl.getOneByUuidAndVersion(dsMI.getUuid(), dsMI.getVersion(), dsMI.getType().toString());
		IExecutor exec = execFactory.getExecutor(targetDS.getType());
		
		String sourceTableName = null;
		try {
			sourceTableName = getTableNameByDatapodKey(new OrderKey(datapodUuid, datapodVersion), runMode);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		List<CompareMetaData> comparisonResult = exec.compareMetadata(targetDatapod, targetDS, sourceTableName);
		List<Attribute> attributes = new ArrayList<>();
		int i = 0;
		for(CompareMetaData compareMetaData : comparisonResult) {
			String propertyName = compareMetaData.getSourceAttribute();
			if(propertyName != null && !propertyName.isEmpty()) {
				boolean containsProperty = isPropertyInAttributeList(propertyName, targetDatapod.getAttributes());
//				String attrType = compareMetaData.getSourceType().toLowerCase();
//				if(attrType.contains("type")) {
//					attrType = attrType.replaceAll("type", "");
//				} 
//				
				Integer length = compareMetaData.getSourceLength().isEmpty() ? null : Integer.parseInt(compareMetaData.getSourceLength());
				if(containsProperty) {
					Attribute attribute = getAttributeByName(propertyName, targetDatapod.getAttributes());
					attribute.setAttributeId(i);
					attribute.setLength(length);
					attribute.setType(compareMetaData.getSourceType());
					attributes.add(attribute);
				} else {
					Attribute attribute = new Attribute();
					attribute.setName(propertyName);
					attribute.setDesc(propertyName);
					attribute.setDispName(propertyName);
					attribute.setLength(length);
					attribute.setType(compareMetaData.getSourceType());
					attribute.setPartition("N");
					attribute.setAttributeId(i);
					attribute.setActive("Y");
					
					attributes.add(attribute);
				}
				i++;
			}
		}
		
		if(!attributes.isEmpty()) {
			targetDatapod.setAttributes(attributes);
			targetDatapod.setId(null);
			targetDatapod.setVersion(null);
			BaseEntity baseEntity = (BaseEntity) commonServiceImpl.save(MetaType.datapod.toString(), targetDatapod);
			return (Datapod) commonServiceImpl.getOneByUuidAndVersion(baseEntity.getUuid(), baseEntity.getVersion(), MetaType.datapod.toString());
		} else {
			return targetDatapod;
		}
	}
	
	public Attribute getAttributeByName(String attributeName, List<Attribute> attributes) {
		for(Attribute attribute : attributes) {
			if(attributeName.equalsIgnoreCase(attribute.getName())) {
				return attribute;
			}
		}
		return null;
	}
	
	public boolean isPropertyInAttributeList(String propertyName, List<Attribute> attributes) {
		boolean containsProperty = false;
		for(Attribute attribute : attributes) {
			if(propertyName.equalsIgnoreCase(attribute.getName())) {
				containsProperty = true;
				break;
			}
		}
		return containsProperty;
	}
	
	public String getMetaStatsByDatapodName(String datapodUuid, String datapodVersion, RunMode runMode) throws Exception {
		Datapod targetDatapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodUuid, datapodVersion,
				MetaType.datapod.toString());
		MetaIdentifier dsMI = targetDatapod.getDatasource().getRef();
		Datasource datasource = (Datasource) commonServiceImpl.getOneByUuidAndVersion(dsMI.getUuid(), dsMI.getVersion(),
				dsMI.getType().toString());
		IExecutor exec = execFactory.getExecutor(datasource.getType());
		String sourceTableName = null;
		try {
			datastoreServiceImpl.setRunMode(runMode);
			sourceTableName = getTableNameByDatapodKey(new OrderKey(datapodUuid, datapodVersion),
					runMode);
		} catch (Exception e) {
			// TODO: handle exception
		}
		List<CompareMetaData> result = exec.compareMetadata(targetDatapod, datasource, sourceTableName);
		Map<String, Integer> map = new HashMap<String, Integer>();
		String status = null;
		Integer modifyCount = 1, deletCount = 1, newCount = 1, noChangeCount = 1;
		for (CompareMetaData meta : result) 
		{
			if (meta.getStatus().equalsIgnoreCase(Compare.MODIFIED.toString())) {
				map.put(meta.getStatus(), modifyCount);
				modifyCount++;
			}
			if (meta.getStatus().equalsIgnoreCase(Compare.DELETED.toString())) {
				map.put(meta.getStatus(), deletCount);
				deletCount++;
			}
			if (meta.getStatus().equalsIgnoreCase(Compare.NEW.toString())) {
				map.put(meta.getStatus(), newCount);
				newCount++;
			}
			if (meta.getStatus().equalsIgnoreCase(Compare.NOCHANGE.toString())) {
				map.put(meta.getStatus(), noChangeCount);
				noChangeCount++;
			}
		}

		if (map.keySet().contains(Compare.NEW.toString()) || map.keySet().contains(Compare.DELETED.toString())
				|| map.keySet().contains(Compare.MODIFIED.toString())) {
			status = Compare.MODIFIED.toString();
		} else {
			status = Compare.NOCHANGE.toString();
		}

		return status;
	}

	public List<Map<String, Object>> getAttrHistogram(String datapodUuid, String datapodVersion, String attributeId, int numBuckets, RunMode runMode) throws Exception {
		List<AttributeRefHolder> attrRefHolderList = new ArrayList<>();
		AttributeRefHolder attrRefHolder = new AttributeRefHolder();
		attrRefHolder.setAttrId(attributeId);
		attrRefHolder.setRef(new MetaIdentifier(MetaType.datapod, datapodUuid, datapodVersion));
		attrRefHolderList.add(attrRefHolder);
		String limitValue = Helper.getPropertyValue("framework.histogram.sample.size");
		int limit = Integer.parseInt(limitValue);
		String resultLimitValue = Helper.getPropertyValue("framework.histogram.result.size");
		int resultLimit = Integer.parseInt(resultLimitValue);
		return histogramOperator.getAttrHistogram(attrRefHolderList, numBuckets, limit, resultLimit, runMode);
	}

	public List<Map<String, Object>> getFormulaValues(String formulaUuid, String formulaVersion, RunMode runMode) throws Exception {
		Formula formula = (Formula) commonServiceImpl.getOneByUuidAndVersion(formulaUuid, formulaVersion, MetaType.formula.toString());
		Datasource formulaDs = commonServiceImpl.getDatasourceByObject(formula);
		String formulaQuery = formulaOperator.generateSql(formula, null, null, null, formulaDs);
		
		MetaIdentifier dependsOn = formula.getDependsOn().getRef();
		Object dependsOnObj = commonServiceImpl.getOneByUuidAndVersion(dependsOn.getUuid(), dependsOn.getVersion(), dependsOn.getType().toString(), "N");
		String dependsOnSql = generateSqlByObject(dependsOnObj, runMode);
		String dependsOnAliaseName = "";
		if(dependsOnObj instanceof Datapod) {
			dependsOnAliaseName = ((Datapod)dependsOnObj).getName();
		} 
		
		StringBuilder builder = new StringBuilder("SELECT").append(" ");
		builder.append("DISTINCT").append(" ").append(formulaQuery).append(" AS value ");
		builder.append("FROM").append(" ").append("(");
		builder.append(dependsOnSql).append(" ").append(dependsOnAliaseName);
		builder.append(")").append(" ").append(dependsOn.getName());
		
		logger.info("SQL gnerated for formula values: "+builder.toString());
		
		String appUuid = commonServiceImpl.getApp().getUuid();
		Datasource appDs = commonServiceImpl.getDatasourceByApp();
		IExecutor exec = execFactory.getExecutor(appDs.getType());
		return exec.executeAndFetchByDatasource(builder.toString(), formulaDs, appUuid);
	}
	
	public String generateSqlByObject(Object object, RunMode runMode) throws Exception {
		if(object instanceof Datapod) {
			return generateSqlByDatapod((Datapod)object, runMode);
		} else if(object instanceof DataSet) {
			return datasetOperator.generateSql((DataSet)object, null, null, new HashSet<>(), null, runMode);
		} else if(object instanceof Rule) {
			return ruleOperator.generateSql((Rule) object, null, null, new HashSet<>(), null, runMode);
		} else if(object instanceof Relation) {
//			MetaIdentifier dependsOn = ((Relation)object).getDependsOn().getRef();
//			Object relDependsOn = commonServiceImpl.getOneByUuidAndVersion(dependsOn.getUuid(), dependsOn.getVersion(), dependsOn.getType().toString(), "N");
//			String dependsOnSql = generateSqlByObject(relDependsOn, runMode);
			String relSql = relationOperator.generateSql((Relation)object, null, null, null, new HashSet<>(), runMode);
			if(relSql != null && !relSql.isEmpty()) {
				return "SELECT * FROM "+relSql;
			} 
		}
		
		return null;
	}
	
	public String generateSqlByDatapod(Datapod datapod, RunMode runMode) throws Exception {
		String tableName = getTableNameByDatapodKey(new Key(datapod.getUuid(), datapod.getVersion()), runMode);
		StringBuilder builder = new StringBuilder("SELECT ");
		
		int i = 0;
		for(Attribute attribute : datapod.getAttributes()) {
			builder.append(attribute.getName()).append(" ");
			builder.append(" AS ").append(attribute.getName());
			if(i<(datapod.getAttributes().size()-1)) {
				builder.append(", ");
			}
			i++;
		}
		
		builder.append(" FROM ");
		builder.append(tableName);
		
		return builder.toString();
	}
	public String generateSqlByDatapod(Datapod datapod, RunMode runMode, List<AttributeRefHolder> listAttributes)
			throws Exception {
		String tableName = getTableNameByDatapodKey(new Key(datapod.getUuid(), datapod.getVersion()),
				runMode);
		StringBuilder builder = new StringBuilder("SELECT ");

		int i = 0;
		for (Attribute attribute : datapod.getAttributes()) {
			for (AttributeRefHolder attributeRefHolder : listAttributes) {

				if (attributeRefHolder.getAttrId().equalsIgnoreCase(attribute.getAttributeId().toString())) {
					builder.append(attribute.getName()).append(" ");
					builder.append(" AS ").append(attribute.getName());
					if (i < (datapod.getAttributes().size() - 1)) {
						builder.append(", ");
					}
					i++;
				}

			}
		}//need to remove ,
		builder.setLength(builder.length() - 2);
		builder.append(" FROM ");
		builder.append(tableName);

		return builder.toString();

	}

	public List<Map<String, Object>> getDatapodSample(String uuid, String version, int rows, RunMode runMode) throws Exception {

//		setRunMode(runMode);

		DataStore ds = datastoreServiceImpl.findDataStoreByMeta(uuid, version);
	
		if (ds == null) {
			logger.error("Datastore is not available for this datapod");			
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.datastore, uuid, version));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Datastore is not available for this datapod.", dependsOn);
			throw new RuntimeException("Datastore is not available for this datapod.");
			
		}
		int maxRows = Integer.parseInt(Helper.getPropertyValue("framework.sample.maxrows"));
		if(rows > maxRows) {
			logger.error("Number of rows "+rows+" exceeded. Max row allow "+maxRows);
			MetaIdentifierHolder dependsOn = new MetaIdentifierHolder();
			dependsOn.setRef(new MetaIdentifier(MetaType.datastore, uuid, version));
			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), "Number of rows "+rows+" exceeded. Max row allow "+maxRows, dependsOn);
			throw new RuntimeException("Number of rows "+rows+" exceeded. Max row allow "+maxRows);
		}
		
		String order = null;
		String sortBy = null;
		int offset = 0;
		int limit = rows;
		
		List<Map<String, Object>> data = new ArrayList<>();
		try {
			String tn = getTableNameByDatapodKey(uuid, version, runMode);
			logger.info("Table name:" + tn);
			
			List<String> orderList = new ArrayList<>();
			List<String> sortList = new ArrayList<>();
			if(StringUtils.isNotBlank(order)) {	
			 orderList = Arrays.asList(order.split("\\s*,\\s*"));
			}
			if(StringUtils.isNotBlank(sortBy)) {
			 sortList = Arrays.asList(sortBy.split("\\s*,\\s*"));
			}
			
			StringBuilder orderBy = new StringBuilder();
			
			ExecContext execContext = null;
			Datasource datasource = commonServiceImpl.getDatasourceByApp();
			if (runMode == null || runMode.equals(RunMode.ONLINE)) {
				execContext = (engine.getExecEngine().equalsIgnoreCase("livy-spark") || engine.getExecEngine().equalsIgnoreCase("livy_spark"))
						? helper.getExecutorContext(engine.getExecEngine()) : helper.getExecutorContext(ExecContext.spark.toString());
			} else {
				execContext = helper.getExecutorContext(datasource.getType().toLowerCase());
			}
			IExecutor exec = execFactory.getExecutor(execContext.toString());

			limit = offset + limit;
			offset = offset + 1;
			String appUuid = null;
			appUuid = commonServiceImpl.getApp().getUuid();

			MetaIdentifier datapodMI = ds.getMetaId().getRef();
			Datapod datapod = (Datapod) commonServiceImpl.getOneByUuidAndVersion(datapodMI.getUuid(), datapodMI.getVersion(), datapodMI.getType().toString());
			Datasource datapodDS = commonServiceImpl.getDatasourceByDatapod(datapod);
			if (StringUtils.isNotBlank(sortBy) || StringUtils.isNotBlank(order) ) {
				for (int i = 0; i < sortList.size(); i++) {
					orderBy.append(sortList.get(i)).append(" ").append(orderList.get(i));
				}
			} else {
				if(datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
						|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
					if(datapodDS.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
						data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " WHERE rownum< " + limit, datapodDS, appUuid);
					} else {
						data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " LIMIT " + limit, datapodDS, appUuid);
					}					
				} else {
					if(datasource.getType().toUpperCase().contains(ExecContext.ORACLE.toString())) {
						if(engine.getExecEngine().equalsIgnoreCase("livy-spark")
								|| datasource.getType().toUpperCase().contains(ExecContext.spark.toString())
								|| datasource.getType().toUpperCase().contains(ExecContext.FILE.toString())) {
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " LIMIT " + limit, datapodDS, appUuid);
						} else {
							data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " WHERE rownum< " + limit, datapodDS, appUuid);
						}
					} else {
						data = exec.executeAndFetchByDatasource("SELECT * FROM " + tn + " LIMIT " + limit, datapodDS, appUuid);
					}
				}
			}
			return data;
		}catch (IOException | NullPointerException e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.", null);
			throw new RuntimeException((message != null) ? message : "Table not found.");
		}catch (Exception e) {
			e.printStackTrace();
			String message = null;
			try {
				message = e.getMessage();
			}catch (Exception e2) {
				// TODO: handle exception
			}

			commonServiceImpl.sendResponse("412", MessageStatus.FAIL.toString(), (message != null) ? message : "Table not found.", null);
			throw new RuntimeException((message != null) ? message : "Table not found.");
		}
	}
	
}
