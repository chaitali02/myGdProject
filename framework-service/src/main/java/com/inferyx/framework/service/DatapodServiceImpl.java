/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

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
import com.inferyx.framework.common.HDFSInfo;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.common.WorkbookUtil;
import com.inferyx.framework.dao.IDataStoreDao;
import com.inferyx.framework.dao.IDatapodDao;
import com.inferyx.framework.dao.IDatasourceDao;
import com.inferyx.framework.dao.IDownloadDao;
import com.inferyx.framework.dao.IUploadDao;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeRefHolder;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DatapodStatsHolder;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.DownloadExec;
import com.inferyx.framework.domain.Load;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Mode;
import com.inferyx.framework.domain.OrderKey;
import com.inferyx.framework.domain.UploadExec;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.executor.IExecutor;
import com.inferyx.framework.factory.DataSourceFactory;
import com.inferyx.framework.factory.ExecutorFactory;
import com.inferyx.framework.register.GraphRegister;

@Service
public class DatapodServiceImpl {

	static final Logger logger = Logger.getLogger(DatapodServiceImpl.class);

	@Autowired
	GraphRegister<?> registerGraph;
	@Autowired
	IDatapodDao idatapodDao;
	@Autowired
	IDataStoreDao idatastoreDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private static Datapod datapod;
	@Autowired
	private LoadServiceImpl loadServiceImpl;
	/*@Autowired
	private DagServiceImpl dagServiceImpl;*/
	@Autowired
	private DataStoreServiceImpl datastoreServiceImpl;
	@Autowired
	private DatasourceServiceImpl datasourceServiceImpl;
	/*@Autowired
	private HiveContext hiveContext;*/
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
	MetadataUtil commonActivity;
	@Autowired
	DataSourceFactory dataSourceFactory;
	@Autowired
	HDFSInfo hdfsInfo;
	protected DataStoreServiceImpl dataStoreServiceImpl;
	@Autowired
	DataFrameService dataFrameService;
	@Autowired
	IUploadDao iDownloadDao;
	@Autowired
	private MessageServiceImpl messageServiceImpl;
//	@Autowired
//	NewGraph newGraph;
	
	/*@Autowired
	private JavaSparkContext javaSparkContext;*/
	
	public DataStoreServiceImpl getDataStoreServiceImpl() {
		return dataStoreServiceImpl;
	}

	public void setDataStoreServiceImpl(DataStoreServiceImpl dataStoreServiceImpl) {
		this.dataStoreServiceImpl = dataStoreServiceImpl;
	}
	
	public DatapodServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	public long count() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
		return idatapodDao.count();
		}
	   Query query = new Query();
	   query.addCriteria(Criteria.where("appInfo.ref.uuid").is(appUuid));    
	   return mongoTemplate.count(query, Datapod.class);
	}

	/********************** UNUSED **********************/
	/*public Datapod findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return idatapodDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return idatapodDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public List<Datapod> findOneForDelete(String id, String name, String type, String desc) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return idatapodDao.findOneForDelete(appUuid, id, name, type, desc);
	}*/

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
	/*public Datapod findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			if (StringUtils.isBlank(version)) {
				return idatapodDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
			}
			return idatapodDao.findOneByUuidAndVersion(appUuid, uuid, version);
		} else {
			if (StringUtils.isBlank(version)) {
				return idatapodDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
			}
			return idatapodDao.findOneByUuidAndVersion(uuid, version);
		}
	}*/

	/********************** UNUSED **********************/
	/*public Datapod getOneByUuidAndVersion(String uuid, String version) {
		return idatapodDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public List<Datapod> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return idatapodDao.findAll();
		}
		return idatapodDao.findAll(appUuid);
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
	/*public Datapod resolveName(Datapod datapod) {

		if (datapod == null)
			return null;
					
		if (datapod.getCreatedBy() != null) {
			String createdByRefUuid = datapod.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			datapod.getCreatedBy().getRef().setName(user.getName());
		}
		if (datapod.getAppInfo() != null) {
			for (int i = 0; i < datapod.getAppInfo().size(); i++) {
				if( datapod.getAppInfo().get(i)!=null){
				String appUuid = datapod.getAppInfo().get(i).getRef().getUuid();
				Application application = applicationServiceImpl.findLatestByUuid(appUuid);
				String appName = application.getName();
				datapod.getAppInfo().get(i).getRef().setName(appName);
				}
			}
		}
		return datapod;
	}*/

	/********************** UNUSED **********************/
	/*public List<Datapod> findAllLatest() {
		logger.debug("start of findAllLatest()");
		Aggregation datapodAggr = newAggregation(group("uuid").max("version").as("version"));
		AggregationResults<Datapod> datapodResults = mongoTemplate.aggregate(datapodAggr, "datapod", Datapod.class);
		List<Datapod> datapodList = datapodResults.getMappedResults();
		// Fetch the datapod details for each id
		List<Datapod> result = new ArrayList<Datapod>();
		for (Datapod s : datapodList) {
			Datapod datapodLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				// String appUuid =
				// securityServiceImpl.getAppInfo().getRef().getUuid();;
				datapodLatest = idatapodDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			} else {
				datapodLatest = idatapodDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
			}
			// logger.debug("datapodLatest is " + datapodLatest.getName());
			if(datapodLatest != null){
			result.add(datapodLatest);
			}
		}
		logger.debug("End of findAllLatest()");
		return result;
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

	/********************** UNUSED **********************/
	/*public Datapod findAllByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;	
				if(appUuid!=null)
					return idatapodDao.findAllByUuid(appUuid, uuid);
				else
					return idatapodDao.findAllByUuid(uuid);
	}*/

	public Datapod findOneByName(String fileName) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		//String appUuid = "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cb";
		return idatapodDao.findOneByFileName(appUuid, fileName);
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

	// This method returns the attributeName of the ref object
	public String getAttributeName(String uuid, int attrId) throws JsonProcessingException {
		String alias = null;
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
		/*if (appUuid != null) {
			datapod = idatapodDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
		} else {
			datapod = idatapodDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}*/
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

	public MetaIdentifierHolder createAndLoad(String csvFileName, Mode runMode) throws Exception {		
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		//String appUuid = "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cb";
		// Check if datapod exists
		String fileName = Helper.getFileName(csvFileName);
		Datapod dp = null;
		dp = findOneByName(fileName);
		// Create datapod and relation if it does not exist
		if (dp == null) {
			/*DataFrame df = hiveContext.read().format("com.databricks.spark.csv").option("inferSchema", "true")
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
			Attribute attr2 = new Attribute();
			attr2.setAttributeId(i++);
			attr2.setType("Integer");
			attr2.setName("version");
			attr2.setDesc("version");
			attr2.setDispName("version");
			attr2.setActive("Y");
			attributes.add(attr2);*/
			String parquetDir = null;

			IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
			List<Attribute> attributes = exec.fetchAttributeList(csvFileName, parquetDir, true, false, appUuid);
			/*List<Attribute> attributes = dataFrameService.getAttributeList(csvFileName, parquetDir, true, false);*/
			logger.info("Attributes:" + attributes);

			// Create datapod
			List<Datasource> datasourceList = iDatasourceDao.findDatasourceByType(appUuid, ExecContext.FILE.toString());
			dp = new Datapod();
			for(Datasource datasource:datasourceList) {
				MetaIdentifier datasourceRef = new MetaIdentifier(MetaType.datasource, datasource.getUuid(),
						datasource.getVersion());
				MetaIdentifierHolder mHolder = new MetaIdentifierHolder();
				mHolder.setRef(datasourceRef);
				dp.setDatasource(mHolder);
				dp.setCache("Y");
				dp.setName(fileName);
				dp.setAttributes(attributes);
				
				try {
					dp = save(dp);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			/*
			 * // Create relation Relation r = new Relation();
			 * MetaIdentifierHolder mih = new MetaIdentifierHolder();
			 * mih.setRef(new MetaIdentifier());
			 * mih.getRef().setUuid(dp.getUuid());
			 * mih.getRef().setType(MetaType.datapod);
			 * mih.getRef().setVersion(dp.getVersion()); r.setDependsOn(mih); r
			 * = relationServiceImpl.Save(r);
			 */
		}

		/* Code for UploadExec*/	
		//String uploadPath = hdfsInfo.getHdfsURL()+hdfsInfo.getSchemaPath() + "/upload/" + csvFileName;
		String uploadPath = csvFileName;
//		UploadExec uploadExec=new UploadExec();			       
//		uploadExec.setBaseEntity();
//		uploadExec.setLocation(uploadPath);
//		uploadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.datapod,dp.getUuid(),dp.getVersion())));
//		uploadExec.setFileName(fileName);
//		commonServiceImpl.save(MetaType.uploadExec.toString(), uploadExec);
		
		// Load datapod using load object
		//String fileName = Helper.getFileName(csvFileName);		
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
		loadServiceImpl.executeSql(loadExec, null, fileName, new OrderKey(dp.getUuid(), dp.getVersion()), null/*, null*/, Mode.BATCH);
		
		return new MetaIdentifierHolder(loadExec.getRef(MetaType.loadExec));
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
	@SuppressWarnings("resource")
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
	/*public List<Datapod> resolveName(List<Datapod> datapod) {
		List<Datapod> datapodList = new ArrayList<Datapod>();
		for (Datapod dpod : datapod) {
			String createdByRefUuid = dpod.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			dpod.getCreatedBy().getRef().setName(user.getName());
			datapodList.add(dpod);
		}
		return datapodList;
	}*/

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

	/********************** UNUSED **********************/
	/*public List<Datapod> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return idatapodDao.findAllVersion(appUuid, uuid);
		} else
			return idatapodDao.findAllVersion(uuid);

	}*/

	/********************** UNUSED **********************/
	/*public Datapod getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return idatapodDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return idatapodDao.findAsOf(uuid, asOf, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> datapodList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity datapod : datapodList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = datapod.getId();
			String uuid = datapod.getUuid();
			String version = datapod.getVersion();
			String name = datapod.getName();
			String desc = datapod.getDesc();
			String published=datapod.getPublished();
			MetaIdentifierHolder createdBy = datapod.getCreatedBy();
			String createdOn = datapod.getCreatedOn();
			String[] tags = datapod.getTags();
			String active = datapod.getActive();
			List<MetaIdentifierHolder> appInfo = datapod.getAppInfo();
			baseEntity.setId(id);
			baseEntity.setUuid(uuid);
			baseEntity.setVersion(version);
			baseEntity.setName(name);
			baseEntity.setDesc(desc);
			baseEntity.setCreatedBy(createdBy);
			baseEntity.setCreatedOn(createdOn);
			baseEntity.setPublished(published);
			baseEntity.setTags(tags);
			baseEntity.setActive(active);
			baseEntity.setAppInfo(appInfo);
			baseEntityList.add(baseEntity);
		}
		return baseEntityList;
	}
*/
	public List<Datapod> SearchDatapodByName(String name, String datasourceUuid) throws JsonProcessingException {		
		 Aggregation filterAggr = 
					newAggregation(	
					match(Criteria.where("datasource.ref.uuid").is(datasourceUuid).andOperator(Criteria.where("name").is(name))),
					group("uuid").max("version").as("version"));

				 AggregationResults<Datapod> groupResults 
					= mongoTemplate.aggregate(filterAggr, "datapod",Datapod.class);
				List<Datapod> datapodList = groupResults.getMappedResults();
				List<Datapod> result = new ArrayList<Datapod>();
				for (Datapod d : datapodList) {
					//Datapod datapodLatest = idatapodDao.findOneByUuidAndVersion(d.getId(), d.getVersion());
					Datapod datapodLatest = (Datapod) commonServiceImpl.getOneByUuidAndVersion(d.getId(), d.getVersion(), MetaType.datapod.toString());
					if(datapodLatest != null)
						result.add(datapodLatest);
				}				
		return result;
}

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public MetaIdentifierHolder saveAs(Datapod datapod) throws Exception{
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Datapod dpNew = new Datapod();
		dpNew.setName(datapod.getName()+"_copy");
		dpNew.setActive(datapod.getActive());		
		dpNew.setDesc(datapod.getDesc());		
		dpNew.setTags(datapod.getTags());
		dpNew.setAttributes(datapod.getAttributes());
		dpNew.setCache(datapod.getCache());
		dpNew.setDatasource(datapod.getDatasource());
		save(dpNew);
		ref.setType(MetaType.datapod);
		ref.setUuid(dpNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

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
	
	@SuppressWarnings("unlikely-arg-type")
	public void upload(MultipartFile csvFile, String datapodUuid) {		
		String csvFileName = csvFile.getOriginalFilename();
		try {
			//patern matching for csv filename
			Pattern p = Pattern.compile("[ !@#$%&*()+=|<>?{}\\[\\]~-]");
			Matcher match = p.matcher(csvFileName);
			/*while(match.find()){
			String s = match.group();
			csvFileName = csvFileName.replaceAll("\\"+s, "");
			}*/
			boolean z = match.find();
			if (z == true || csvFileName.contains(" ")) 
				throw new Exception("CSV file name contains white space or special character");
			else
				 System.out.println("There is no special char.");

			String uploadPath = null;
			/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
					? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
			Datasource datasource = null;		

			//csvFileName = csvFileName.substring(StringUtils.lastIndexOf(csvFileName, "/") + 1, csvFileName.length());
			uploadPath = hdfsInfo.getSchemaPath() + "/upload/" + csvFileName;
			// Copy file to server location
			File file = new File(uploadPath);
			csvFile.transferTo(file);
		 
			uploadPath = hdfsInfo.getHdfsURL()+hdfsInfo.getSchemaPath() + "/upload/" + csvFileName;
			Datapod datapod = (Datapod) commonServiceImpl.getLatestByUuid(datapodUuid, MetaType.datapod.toString());
			
			//Check datapod and csv attributes name
			Boolean flag = true;
			String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
			String parquetDir = null;
			IExecutor exec = execFactory.getExecutor(ExecContext.spark.toString());
			
			List<Attribute> attributes = exec.fetchAttributeList(uploadPath, parquetDir, true, false, appUuid);
			attributes.remove(attributes.size()-1);
			ListIterator<Attribute> attributeIterator = attributes.listIterator();
			
			List<Attribute> dpAttrs = datapod.getAttributes();
			int count = 0;
			for(int i=0; i<dpAttrs.size();i++) {
				System.out.println("index : "+i);
				if(dpAttrs.get(i).getName().contentEquals("version")){					
					dpAttrs.remove(dpAttrs.get(i));
					count++;
				}	
				if(count>1) {
					throw new Exception("2 Version column in datapod");
				}
			}
			
			if(attributes.size()==dpAttrs.size())
			{
				for(Attribute a : dpAttrs) {					
					if(attributeIterator.hasNext()) {
						Attribute attribute  = attributeIterator.next();				
						
						String b = attribute.getName();
						String c = a.getName();
						
						if(!b.equals(c)) {
							logger.info("CSV Column not matched : "+attribute.getName());
							throw new Exception("CSV Column not matched:<b>"+attribute.getName()+"</b> Position:<b>"+(attributeIterator.nextIndex()+"</b>")	);
						}
					}	
				}
			}
			else {
				throw new Exception("CSV and Datapod column not matched");
			}
				
			/* Code for UploadExec*/		
			UploadExec uploadExec=new UploadExec();			       
			uploadExec.setBaseEntity();
			uploadExec.setLocation(uploadPath);
			uploadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.datapod,datapod.getUuid(),datapod.getVersion())));
			uploadExec.setFileName(csvFileName);
			uploadExec.setName(Helper.getFileName(csvFileName));
			commonServiceImpl.save(MetaType.uploadExec.toString(), uploadExec);
			   
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
			
			loadServiceImpl.executeSql(loadExec, null, fileName, new OrderKey(datapod.getUuid(), datapod.getVersion()), null/*, null*/, Mode.BATCH);
		
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException | NullPointerException
				| ParseException | IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			setResponseMsg(e.getMessage());			
		}	
	}

	public HttpServletResponse download(String uuid, String version, String format, int offset,
			int limit, HttpServletResponse response, int rowLimit, String sortBy, String order, String requestId,
			Mode runMode) throws Exception {
		datastoreServiceImpl.setRunMode(runMode);
		DataStore ds = datastoreServiceImpl.findDataStoreByMeta(uuid, version);
		if (ds == null) {
			logger.error("Datastore is not available for this datapod");
			throw new Exception();
		}
		List<Map<String, Object>> results = datastoreServiceImpl.getDatapodResults(ds.getUuid(), ds.getVersion(), null,
				0, limit, response, rowLimit, null, null, null, runMode);
		response = commonServiceImpl.download(uuid, version, format, offset, limit, response, rowLimit, sortBy, order, requestId, runMode, results,MetaType.downloadExec,new MetaIdentifierHolder(new MetaIdentifier(MetaType.datapod,uuid,version)));
		
		/*
		String downloadPath = Helper.getPropertyValue("framework.file.download.path");
       DownloadExec downloadExec=new DownloadExec();
       
       downloadExec.setBaseEntity();
       downloadExec.setLocation(downloadPath + "/" + downloadExec.getUuid() + "_" + downloadExec.getVersion() + ".xlsx");
       downloadExec.setDependsOn(new MetaIdentifierHolder(new MetaIdentifier(MetaType.datapod,uuid,version)));
		try {
			FileOutputStream fileOut = null;
			response.setContentType("application/xml charset=utf-16");
			response.setHeader("Content-type", "application/xml");
			HSSFWorkbook workbook = WorkbookUtil.getWorkbook(results);

			downloadPath = Helper.getPropertyValue("framework.file.download.path");
			response.addHeader("Content-Disposition", "attachment; filename=" + uuid + ".xlsx");
			ServletOutputStream os = response.getOutputStream();
			workbook.write(os);

			fileOut = new FileOutputStream(downloadPath + "/" + downloadExec.getUuid() + "_" + downloadExec.getVersion() + ".xlsx");
			workbook.write(fileOut);
			os.write(workbook.getBytes());
			commonServiceImpl.save(MetaType.datapod.toString(), downloadExec);
			os.close();
			fileOut.close();

		} catch (IOException e1) {
			e1.printStackTrace();
			logger.info("exception caught while download file");
			response.setStatus(300);
        	throw new FileNotFoundException();
		}*/
		return response;

	}
	
	void setResponseMsg(String msg){
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
			} else
				logger.info("HttpServletResponse response is \"" + null + "\"");
		} else
			logger.info("ServletRequestAttributes requestAttributes is \"" + null + "\"");
	}

}
