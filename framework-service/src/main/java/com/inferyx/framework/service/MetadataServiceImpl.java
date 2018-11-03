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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.ml.param.DoubleParam;
import org.apache.spark.ml.param.IntParam;
import org.apache.spark.ml.param.LongParam;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.param.ParamPair;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import static org.springframework.data.mongodb.core.query.Criteria.*; 
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inferyx.framework.common.Helper;
import com.inferyx.framework.common.MetadataUtil;
import com.inferyx.framework.dao.IMetaDao;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.BaseEntityStatus;
import com.inferyx.framework.domain.BatchExec;
import com.inferyx.framework.domain.CommentView;
import com.inferyx.framework.domain.Dag;
import com.inferyx.framework.domain.DagExec;
import com.inferyx.framework.domain.DataQualExec;
import com.inferyx.framework.domain.DataQualGroupExec;
import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.Datasource;
import com.inferyx.framework.domain.Distribution;
import com.inferyx.framework.domain.ExecParams;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.FrameworkThreadLocal;
import com.inferyx.framework.domain.Function;
import com.inferyx.framework.domain.GraphExec;
import com.inferyx.framework.domain.IngestExec;
import com.inferyx.framework.domain.IngestGroupExec;
import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.Lov;
import com.inferyx.framework.domain.MapExec;
import com.inferyx.framework.domain.Message;
import com.inferyx.framework.domain.Meta;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Model;
import com.inferyx.framework.domain.Operator;
import com.inferyx.framework.domain.OperatorExec;
import com.inferyx.framework.domain.Param;
import com.inferyx.framework.domain.ParamInfo;
import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamListHolder;
import com.inferyx.framework.domain.ParamSet;
import com.inferyx.framework.domain.ParamSetHolder;
import com.inferyx.framework.domain.PredictExec;
import com.inferyx.framework.domain.ProfileExec;
import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.ReconExec;
import com.inferyx.framework.domain.ReconGroupExec;
import com.inferyx.framework.domain.ReportExec;
import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.executor.ExecContext;
import com.inferyx.framework.domain.RuleExec;
import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.SessionContext;
import com.inferyx.framework.domain.Simulate;
import com.inferyx.framework.domain.SimulateExec;
import com.inferyx.framework.domain.Status;
import com.inferyx.framework.domain.StatusHolder;
import com.inferyx.framework.domain.Train;
import com.inferyx.framework.domain.TrainExec;
import com.inferyx.framework.domain.UploadExec;


@Service
public class MetadataServiceImpl {	
	@Autowired
	IMetaDao iMetadataDao;	
	@Autowired 
	MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	LoadServiceImpl loadServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	MetadataUtil daoRegister;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	@Autowired
	UploadExecServiceImpl uploadExecServiceImpl;
	@Autowired
	ParamSetServiceImpl paramSetServiceImpl;
	@Autowired
	ParamListServiceImpl paramListServiceImpl;
	
	static final Logger logger = Logger.getLogger(MetadataServiceImpl.class);
//	private static final String GET = "get";
//	private static final String SET = "set";

	 public BaseEntity resolveBaseEntity(BaseEntity baseEntity) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException{
		 if (baseEntity == null)
			 return null;

		//Resolve username
		if (baseEntity.getCreatedBy() != null) {		 
			//User user = userServiceImpl.findLatestByUuid(baseEntity.getCreatedBy().getRef().getUuid());
			User user = (User) commonServiceImpl.getLatestByUuid(baseEntity.getCreatedBy().getRef().getUuid(), MetaType.user.toString(),"N");
			//User user = (User) commonServiceImpl.getLatestByUuidWithoutAppUuid(baseEntity.getCreatedBy().getRef().getUuid(), MetaType.user.toString());
			//Object iDao = commonServiceImpl.getClass().getMethod(GET + Helper.getDaoClass(MetaType.user)).invoke(commonServiceImpl);
			//String uuid = baseEntity.getCreatedBy().getRef().getUuid();
			//User user = (User) Helper.getDomainClass(MetaType.user).cast(iDao.getClass().getMethod("findLatestByUuid",String.class,Sort.class ).invoke(iDao, uuid,new Sort(Sort.Direction.DESC, "version")));	
			baseEntity.getCreatedBy().getRef().setName(user.getName());
		}
		
		//Resolve appname
		if (baseEntity.getAppInfo() != null) {
			for (int i = 0; i < baseEntity.getAppInfo().size(); i++) {
				//logger.info(" Baseentity : " + baseEntity.getName() + " : " + baseEntity.getUuid() + " : " + baseEntity.getAppInfo().size());
				String appUuid = baseEntity.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString(),"N");
				//Object iDao = commonServiceImpl.getClass().getMethod(GET + Helper.getDaoClass(MetaType.application)).invoke(commonServiceImpl);
				//Application application = (Application) Helper.getDomainClass(MetaType.application).cast(iDao.getClass().getMethod("findLatestByUuid",String.class,Sort.class ).invoke(iDao, appUuid,new Sort(Sort.Direction.DESC, "version")));	
				baseEntity.getAppInfo().get(i).getRef().setName(application.getName());
			}
		}		
		return baseEntity;
	}	
	
	public Meta findOne(String id) throws JsonProcessingException{
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		//return iMetadataDao.findOneById(appUuid,id);
		return (Meta) commonServiceImpl.getOneById(id, MetaType.meta.toString());
	}

	/********************** UNUSED **********************/
	/*public Meta Save(Meta metadata){
		MetaIdentifierHolder meta=securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList=new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		metadata.setAppInfo(metaIdentifierHolderList);
		metadata.setBaseEntity();
		return iMetadataDao.save(metadata);
	}*/

	/********************** UNUSED **********************/
	/*public List<Meta> findAll(){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iMetadataDao.findAll(); 
		}
		return iMetadataDao.findAll(appUuid);
	}*/

	/********************** UNUSED **********************/
	/*public Meta findOneById(String id){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iMetadataDao.findOneById(appUuid,id);
		}
		return iMetadataDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public List<Meta> test(String param1) {	
		return iMetadataDao.test(param1);
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String Id){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Meta metadata = iMetadataDao.findOneById(appUuid,Id);
		metadata.setActive("N");
		iMetadataDao.save(metadata);
//		String ID=metadata.getId();
//		iMetadataDao.delete(ID);
//		metadata.exportBaseProperty();
	}*/
	/*public Map update(Map map){
		map.exportBaseProperty(objectState.EDIT.toString());
		return iMapDao.save(map);
	}
	
	public boolean isExists(String id){
		return iMapDao.exists(id);
	}
	
	public void  delete(String Id,String version){
		Map map = iMapDao.findOne(Id ,version);
		map.exportBaseProperty(objectState.DELETE.toString());
		iMapDao.save(map);
	}
	
	public List<Map> findAllVersion(String datapodName){
		return iMapDao.findAllVersion(datapodName);
	}*/

	/********************** UNUSED **********************/
	/*public Meta findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iMetadataDao.findAllByUuid(appUuid,uuid);
		
	}*/

	/********************** UNUSED **********************/
	/*public Meta findOneByUuidAndVersion(String uuid,String version){
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iMetadataDao.findOneByUuidAndVersion(appUuid,uuid,version);
	}*/

	/********************** UNUSED **********************/
	/*public Meta findLatestByUuid(String uuid){
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid == null)
		{
			return iMetadataDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));
		}
		return iMetadataDao.findLatestByUuid(appUuid,uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/

	/********************** UNUSED **********************/
	/*public List<Meta> findAllLatest() {
		{	   
		//	String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
			   Aggregation metadataAggr = newAggregation(group("uuid").max("version").as("version"));
			   AggregationResults<Meta> metadataResults = mongoTemplate.aggregate(metadataAggr,"metadata", Meta.class);	   
			   List<Meta> metadataList = metadataResults.getMappedResults();

			   // Fetch the relation details for each id
			   List<Meta> result=new  ArrayList<Meta>();
			   for(Meta s :metadataList)
			   {   
				   Meta metadataLatest;
					String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
					if(appUuid != null)
					{
					//String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();;
						metadataLatest = iMetadataDao.findOneByUuidAndVersion(appUuid,s.getId(), s.getVersion());
					}
					else
					{
						metadataLatest = iMetadataDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
					}
					//logger.debug("datapodLatest is " + datapodLatest.getName());
					if(metadataLatest != null)
					{		
					result.add(metadataLatest);
					}
			   }
			   return result;
			}
	}*/

	/********************** UNUSED **********************/
	/*public List<Meta> findAllLatestActive() 	
	{	   
	   Aggregation metadataAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<Meta> metadataResults = mongoTemplate.aggregate(metadataAggr,"metadata", Meta.class);	   
	   List<Meta> metadataList = metadataResults.getMappedResults();

	   // Fetch the metadata details for each id
	   List<Meta> result=new  ArrayList<Meta>();
	   for(Meta m : metadataList)
	   {   
		   Meta metadataLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
			if(appUuid != null)
			{
				metadataLatest = iMetadataDao.findOneByUuidAndVersion(appUuid,m.getId(), m.getVersion());
			}
			else
			{
				metadataLatest = iMetadataDao.findOneByUuidAndVersion(m.getId(), m.getVersion());
			}
			if(metadataLatest != null)
			{		
			result.add(metadataLatest);
			}
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<Meta> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null )?securityServiceImpl.getAppInfo().getRef().getUuid():null;
		if(appUuid != null)
		{
		return iMetadataDao.findAllVersion(appUuid, uuid);
		}
		else
		return iMetadataDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public Object getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iMetadataDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iMetadataDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	@SuppressWarnings("unchecked")
	public List<BaseEntityStatus> getBaseEntityStatusByCriteria(String role, String appUuid, String type, String name, String userName, String startDate, String endDate, String tags, String active, String status) throws ParseException, JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {
		MetaType metaType = Helper.getMetaType(type);
		//Create query
		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("desc");
		query.fields().include("createdBy");
		query.fields().include("createdOn");
		query.fields().include("tags");
		query.fields().include("active");
		query.fields().include("published");
		query.fields().include("appInfo");
		query.fields().include("statusList");
		
		//Apply filter
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("EEE MMM dd hh:mm:ss yyyy z");
		
		//to find 
		if(!StringUtils.isBlank(role) && role.equalsIgnoreCase("admin")) {
			if(appUuid == null || StringUtils.isBlank(appUuid))
				appUuid = null;
		}else
			if(appUuid == null || StringUtils.isBlank(appUuid))
				appUuid = 	(securityServiceImpl.getAppInfo() != null&& securityServiceImpl.getAppInfo().getRef() != null)? securityServiceImpl.getAppInfo().getRef().getUuid() : null;

		try {
			if (appUuid != null)
			query.addCriteria(Criteria.where("appInfo.ref.uuid").is(appUuid));
			
			if (name != null &&!name.isEmpty())
			query.addCriteria(Criteria.where("name").is(name));
			if (userName != null && !userName.isEmpty()) {
				User user = userServiceImpl.findUserByName(userName);
				query.addCriteria(Criteria.where("createdBy.ref.uuid").is(user.getUuid()));
			}
			if ( (startDate != null && !startDate.isEmpty()) && (endDate != null && !endDate.isEmpty()) )
			query.addCriteria(Criteria.where("_id").ne("1").andOperator(Criteria.where("createdOn").gte(simpleDateFormat.parse(startDate)),
							  										  Criteria.where("createdOn").lte(simpleDateFormat.parse(endDate))));
			else if (startDate != null && !startDate.isEmpty())
			query.addCriteria(Criteria.where("createdOn").gte(startDate));
			else if (endDate != null && !endDate.isEmpty())
			query.addCriteria(Criteria.where("createdOn").lte(endDate));
			if (tags != null && !tags.isEmpty()) {
				ArrayList<?> tagList= new ArrayList<>(Arrays.asList(tags.split(",")));
				query.addCriteria(Criteria.where("tags").all(tagList));
			}
			if (active != null && !active.isEmpty())
			query.addCriteria(Criteria.where("active").is(active));
			if (status != null && !status.isEmpty())
			query.addCriteria(Criteria.where("statusList.stage").in(status));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

		List<Object> metaObjectList = new ArrayList<>();
				
		Class<?> className = Helper.getDomainClass(metaType);		
		metaObjectList = (List<Object>) mongoTemplate.find(query, className);

		List<BaseEntityStatus> baseEntityStatusList = new ArrayList<>();
		
		
		for (Object metaObject: metaObjectList)
		{
			List<Status> execStatus = null;
			//type.toLowerCase() == MetaType.dagexec.toString().toLowerCase()
			if (type.equalsIgnoreCase(MetaType.dagExec.toString())) {
				DagExec execObject = new DagExec();
				execObject = (DagExec) metaObject;
			    execStatus = (List<Status>) execObject.getStatusList();
			}
			else if (type.equalsIgnoreCase(MetaType.ruleExec.toString())) {
				RuleExec execObject =new RuleExec();
				execObject = (RuleExec) metaObject;
				execStatus =  (List<Status>)execObject.getStatusList();
			}
			else if (type.equalsIgnoreCase(MetaType.rulegroupExec.toString())) {
				 RuleGroupExec execObject = new RuleGroupExec();
				execObject = (RuleGroupExec) metaObject;
				execStatus = (List<Status>) execObject.getStatusList();
			}
			else if (type.equalsIgnoreCase(MetaType.profileExec.toString())) {
				 ProfileExec execObject = new ProfileExec();
				execObject = (ProfileExec) metaObject;
				execStatus = (List<Status>) execObject.getStatusList();
			}
			else if (type.equalsIgnoreCase(MetaType.profilegroupExec.toString())) {
				 ProfileGroupExec execObject = new ProfileGroupExec();
				execObject = (ProfileGroupExec) metaObject;
				execStatus = (List<Status>) execObject.getStatusList();
			}
			else if (type.equalsIgnoreCase(MetaType.dqExec.toString())) {
				 DataQualExec execObject = new DataQualExec();
				execObject = (DataQualExec) metaObject;
				execStatus = (List<Status>) execObject.getStatusList();
			}
			else if (type.equalsIgnoreCase(MetaType.dqgroupExec.toString())) {
				 DataQualGroupExec execObject = new DataQualGroupExec();
				execObject = (DataQualGroupExec) metaObject;
				execStatus = (List<Status>) execObject.getStatusList();
			}
			/*else if(type.equalsIgnoreCase(MetaType.vizexec.toString())){
				VizExec execObject = new VizExec();
				execObject = (VizExec) metaObject;
				execStatus = (List<Status>) execObject.getStatus();	
			}*/
			else if(type.equalsIgnoreCase(MetaType.loadExec.toString())){
				LoadExec execObject = new LoadExec();
				execObject = (LoadExec) metaObject;
				execStatus = (List<Status>) execObject.getStatusList();	
			}
			/*else if(type.equalsIgnoreCase(MetaType.modelExec.toString())){
				ModelExec execObject = new ModelExec();
				execObject = (ModelExec) metaObject;
				execStatus = (List<Status>) execObject.getStatusList();	
			}*/
			else if(type.equalsIgnoreCase(MetaType.mapExec.toString())){
				MapExec execObject = new MapExec();
				execObject = (MapExec) metaObject;
				execStatus = (List<Status>) execObject.getStatusList();	
			}else if(type.equalsIgnoreCase(MetaType.session.toString())){
				Session sessionObject = new Session();
				sessionObject = (Session) metaObject;
				execStatus = (List<Status>) sessionObject.getStatusList();	
			}
			else if(type.equalsIgnoreCase(MetaType.trainExec.toString())){
				TrainExec execObject = new TrainExec();
				execObject = (TrainExec) metaObject;
				execStatus = (List<Status>) execObject.getStatusList();	
			}
			else if(type.equalsIgnoreCase(MetaType.predictExec.toString())){
				PredictExec execObject = new PredictExec();
				execObject = (PredictExec) metaObject;
				execStatus = (List<Status>) execObject.getStatusList();	
			}
			else if(type.equalsIgnoreCase(MetaType.simulateExec.toString())){
				SimulateExec execObject = new SimulateExec();
				execObject = (SimulateExec) metaObject;
				execStatus = (List<Status>) execObject.getStatusList();	
			}
			else if(type.equalsIgnoreCase(MetaType.reconExec.toString())){
				ReconExec execObject = new ReconExec();
				execObject = (ReconExec) metaObject;
				execStatus = (List<Status>) execObject.getStatusList();	
			}
			else if(type.equalsIgnoreCase(MetaType.recongroupExec.toString())){
				ReconGroupExec execObject = new ReconGroupExec();
				execObject = (ReconGroupExec) metaObject;
				execStatus = (List<Status>) execObject.getStatusList();	
			} else if(type.equalsIgnoreCase(MetaType.operatorExec.toString())){
				OperatorExec operatorExec = new OperatorExec();
				operatorExec = (OperatorExec) metaObject;
				execStatus = (List<Status>) operatorExec.getStatusList();	
			}
			else if(type.equalsIgnoreCase(MetaType.graphExec.toString())){
				GraphExec graphExec = new GraphExec();
				graphExec = (GraphExec) metaObject;
				execStatus = (List<Status>) graphExec.getStatusList();	
			}
			else if(type.equalsIgnoreCase(MetaType.reportExec.toString())){
				ReportExec reportExec = new ReportExec();
				reportExec = (ReportExec) metaObject;
				execStatus = (List<Status>) reportExec.getStatusList();	
			}
			else if(type.equalsIgnoreCase(MetaType.batchExec.toString())){
				BatchExec batchExec = new BatchExec();
				batchExec = (BatchExec) metaObject;
				execStatus = (List<Status>) batchExec.getStatusList();	
			}
			else if(type.equalsIgnoreCase(MetaType.ingestExec.toString())){
				IngestExec ingestExec = new IngestExec();
				ingestExec = (IngestExec) metaObject;
				execStatus = (List<Status>) ingestExec.getStatusList();	
			}
			else if(type.equalsIgnoreCase(MetaType.ingestgroupExec.toString())){
				IngestGroupExec execObject = new IngestGroupExec();
				execObject = (IngestGroupExec) metaObject;
				execStatus = (List<Status>) execObject.getStatusList();	
			} 
				
			BaseEntityStatus baseEntityStatus = new BaseEntityStatus();			
			BaseEntity baseEntityTmp = (BaseEntity) metaObject;			
			baseEntityTmp = resolveBaseEntity(baseEntityTmp);
			baseEntityStatus.setId(baseEntityTmp.getId());
			baseEntityStatus.setUuid(baseEntityTmp.getUuid());
			baseEntityStatus.setVersion(baseEntityTmp.getVersion());
			baseEntityStatus.setName(baseEntityTmp.getName());
			baseEntityStatus.setDesc(baseEntityTmp.getDesc());
			baseEntityStatus.setCreatedBy(baseEntityTmp.getCreatedBy());
			baseEntityStatus.setCreatedOn(baseEntityTmp.getCreatedOn());
			baseEntityStatus.setTags(baseEntityTmp.getTags());
			baseEntityStatus.setActive(baseEntityTmp.getActive());
			baseEntityStatus.setPublished(baseEntityTmp.getPublished());
			baseEntityStatus.setAppInfo(baseEntityTmp.getAppInfo());
			baseEntityStatus.setStatus(execStatus);
			baseEntityStatus.setType(metaType.toString());
			baseEntityStatusList.add(baseEntityStatus);
		}
		return baseEntityStatusList;	
}
	
	/*
	 * @SuppressWarnings({ "unchecked", "rawtypes" }) public List<BaseEntity>
	 * getBaseEntityByCriteria(String type, String name, String userName, String
	 * startDate, String endDate, String tags, String active, String uuid, String
	 * version, String published) throws ParseException, JsonProcessingException,
	 * IllegalAccessException, IllegalArgumentException, InvocationTargetException,
	 * NoSuchMethodException, SecurityException, NullPointerException {
	 * 
	 * MetaType metaType = Helper.getMetaType(type);
	 * 
	 * //Create query Query query = new Query(); query.fields().include("_id");
	 * query.fields().include("uuid"); query.fields().include("version");
	 * query.fields().include("name"); query.fields().include("desc");
	 * query.fields().include("createdBy"); query.fields().include("createdOn");
	 * query.fields().include("tags"); query.fields().include("active");
	 * query.fields().include("published"); query.fields().include("appInfo");
	 * 
	 * //Apply filter SimpleDateFormat simpleDateFormat = new SimpleDateFormat
	 * ("EEE MMM dd hh:mm:ss yyyy z");
	 * 
	 * //to find String appUuid = (securityServiceImpl.getAppInfo() != null&&
	 * securityServiceImpl.getAppInfo().getRef() != null)?
	 * securityServiceImpl.getAppInfo().getRef().getUuid() : null;
	 * 
	 * try { if (appUuid != null)
	 * query.addCriteria(Criteria.where("appInfo.ref.uuid").is(appUuid)); if (name
	 * != null &&!name.isEmpty())
	 * query.addCriteria(Criteria.where("name").is(name)); if (userName != null &&
	 * !userName.isEmpty()) { User user = userServiceImpl.findUserByName(userName);
	 * if(user != null && user.getUuid().equals(getCurrentUser().getUuid())) {
	 * //logger.info("if part...1.0");
	 * query.addCriteria(Criteria.where("createdBy.ref.uuid").is(user.getUuid())); }
	 * else { //logger.info("else part...1.0"); if(user != null)
	 * query.addCriteria(Criteria.where("_id").ne("1").andOperator(Criteria.where(
	 * "createdBy.ref.uuid").in(user.getUuid()),Criteria.where("published").in("Y"))
	 * ); } }else { //logger.info("else part...1.1"); User currentUser =
	 * getCurrentUser(); if(currentUser != null)
	 * query.addCriteria(Criteria.where("_id").ne("1").orOperator(Criteria.where(
	 * "createdBy.ref.uuid").in(currentUser.getUuid()),Criteria.where("published").
	 * in("Y"))); }
	 * 
	 * 
	 * 
	 * if ( (startDate != null && !startDate.isEmpty()) && (endDate != null &&
	 * !endDate.isEmpty()) )
	 * query.addCriteria(Criteria.where("_id").ne("1").andOperator(Criteria.where(
	 * "createdOn").gte(simpleDateFormat.parse(startDate)),
	 * Criteria.where("createdOn").lte(simpleDateFormat.parse(endDate))));
	 * 
	 * else if (startDate != null && !startDate.isEmpty())
	 * query.addCriteria(Criteria.where("createdOn").gte(startDate)); else if
	 * (endDate != null && !endDate.isEmpty())
	 * 
	 * query.addCriteria(Criteria.where("createdOn").lte(endDate)); if (tags != null
	 * && !tags.isEmpty()) { ArrayList tagList= new
	 * ArrayList(Arrays.asList(tags.split(",")));
	 * query.addCriteria(Criteria.where("tags").all(tagList)); } if (active != null
	 * && !active.isEmpty()) {
	 * query.addCriteria(Criteria.where("active").is(active)); }
	 * 
	 * // else { // query.addCriteria(Criteria.where("active").is("Y")); // }
	 * 
	 * if (StringUtils.isNotBlank(uuid)) {
	 * query.addCriteria(Criteria.where("uuid").is(uuid)); } if
	 * (StringUtils.isNotBlank(version)) {
	 * query.addCriteria(Criteria.where("version").is(version)); } if
	 * (StringUtils.isNotBlank(published)) {
	 * query.addCriteria(Criteria.where("published").is(published)); }
	 * query.with(new Sort(Sort.Direction.DESC,"version")); } catch (ParseException
	 * e1) { // TODO Auto-generated catch block e1.printStackTrace(); return null; }
	 * 
	 * List<Object> metaObjectList = new ArrayList<>();
	 * 
	 * Class<?> className = Helper.getDomainClass(metaType); if (className == null)
	 * { return null; } metaObjectList = (List<Object>) mongoTemplate.find(query,
	 * className);
	 * 
	 * List<BaseEntity> baseEntityList = new ArrayList<>();
	 * 
	 * 
	 * for (Object metaObject: metaObjectList) { BaseEntity baseEntity = new
	 * BaseEntity(); BaseEntity baseEntityTmp = (BaseEntity) metaObject;
	 * baseEntityTmp = resolveBaseEntity(baseEntityTmp);
	 * baseEntity.setId(baseEntityTmp.getId());
	 * baseEntity.setUuid(baseEntityTmp.getUuid());
	 * baseEntity.setVersion(baseEntityTmp.getVersion());
	 * baseEntity.setName(baseEntityTmp.getName());
	 * baseEntity.setDesc(baseEntityTmp.getDesc());
	 * baseEntity.setCreatedBy(baseEntityTmp.getCreatedBy());
	 * baseEntity.setCreatedOn(baseEntityTmp.getCreatedOn());
	 * baseEntity.setTags(baseEntityTmp.getTags());
	 * baseEntity.setActive(baseEntityTmp.getActive());
	 * baseEntity.setPublished(baseEntityTmp.getPublished());
	 * baseEntity.setAppInfo(baseEntityTmp.getAppInfo());
	 * baseEntityList.add(baseEntity); } return baseEntityList; }
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<BaseEntity> getBaseEntityByCriteria(String type, String name, String userName, String startDate,
			String endDate, String tags, String active, String uuid, String version, String published)
			throws ParseException, JsonProcessingException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException {		
		MetaType metaType = Helper.getMetaType(type);
		Criteria criteria = new Criteria();
		List<Criteria> criteriaList = new ArrayList<Criteria>();
		// Apply filter
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy z");
		// to find
		//String appUuid = null ;
		String appUuid =commonServiceImpl.findAppId(type);

//		appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
//				? securityServiceImpl.getAppInfo().getRef().getUuid()
//				: null;
		

		try {
			if (appUuid != null)
				criteriaList.add(where("appInfo.ref.uuid").is(appUuid));
			if (name != null && !name.isEmpty())
				criteriaList.add(where("name").is(name));
			if (userName != null && !userName.isEmpty()) {
				User user = userServiceImpl.findUserByName(userName);
				if (user != null && user.getUuid().equals(getCurrentUser().getUuid())) {
					criteriaList.add(where("createdBy.ref.uuid").is(user.getUuid()));
				} else {
					if (user != null)
						criteriaList.add(where("_id").ne("1").andOperator(
								where("createdBy.ref.uuid").is(user.getUuid()), where("published").is("Y")));
				}
			}
			// else{
			// User currentUser = getCurrentUser();
			// if(currentUser != null)
			// criteriaList.add(where("_id").ne("1").orOperator(where("createdBy.ref.uuid").is(currentUser.getUuid()),where("published").is("Y")));
			// }

			if ((startDate != null && !startDate.isEmpty()) && (endDate != null && !endDate.isEmpty())) {
				criteriaList.add(where("_id").ne("1").and("createdOn").lte(simpleDateFormat.parse(endDate))
						.gte(simpleDateFormat.parse(startDate)));
			}

			else if (startDate != null && !startDate.isEmpty())
				criteriaList.add(where("createdOn").gte(simpleDateFormat.parse(startDate)));
			else if (endDate != null && !endDate.isEmpty())
				criteriaList.add(where("createdOn").lte(simpleDateFormat.parse(endDate)));
			if (tags != null && !tags.isEmpty()) {
				ArrayList tagList = new ArrayList(Arrays.asList(tags.split(",")));
				criteriaList.add(where("tags").all(tagList));
			}
			if (active != null && !active.isEmpty()) {
				criteriaList.add(where("active").is(active));
			}
			if (StringUtils.isNotBlank(uuid)) {
				criteriaList.add(where("uuid").is(uuid));
			}
			if (StringUtils.isNotBlank(version)) {
				criteriaList.add(where("version").is(version));
			}
			if (StringUtils.isNotBlank(published)) {
				criteriaList.add(where("published").is(published));
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

		List<Object> metaObjectList = new ArrayList<>();

		Class<?> className = Helper.getDomainClass(metaType);
		if (className == null) {
			return null;
		}

		Criteria criteria2 = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));

		Aggregation ruleExecAggr;
		if (criteriaList.size() > 0) {
			ruleExecAggr = newAggregation(match(criteria2), group("uuid").max("version").as("version"));
		} else {
			ruleExecAggr = newAggregation(group("uuid").max("version").as("version"));
		}

		AggregationResults ruleExecResults = mongoTemplate.aggregate(ruleExecAggr, type.toLowerCase(), className);
		metaObjectList = ruleExecResults.getMappedResults();

		List<BaseEntity> baseEntityList = new ArrayList<>();

		for (Object metaObject : metaObjectList) {
			BaseEntity baseEntity = new BaseEntity();
			BaseEntity baseEntityTmp_2 = (BaseEntity) metaObject;
			BaseEntity baseEntityTmp = (BaseEntity) commonServiceImpl.getLatestByUuid(baseEntityTmp_2.getId(), type, "N");
			baseEntityTmp = resolveBaseEntity(baseEntityTmp);
			baseEntity.setId(baseEntityTmp.getId());
			baseEntity.setUuid(baseEntityTmp.getUuid());
			baseEntity.setVersion(baseEntityTmp.getVersion());
			baseEntity.setName(baseEntityTmp.getName());
			baseEntity.setDesc(baseEntityTmp.getDesc());
			baseEntity.setCreatedBy(baseEntityTmp.getCreatedBy());
			baseEntity.setCreatedOn(baseEntityTmp.getCreatedOn());
			baseEntity.setTags(baseEntityTmp.getTags());
			baseEntity.setActive(baseEntityTmp.getActive());
			baseEntity.setPublished(baseEntityTmp.getPublished());
			baseEntity.setAppInfo(baseEntityTmp.getAppInfo());
			baseEntityList.add(baseEntity);
		}
		return baseEntityList;
	}

	/********************** UNUSED **********************/
	/*public List<BaseEntity> getBaseEntity(List<Object> metaObjList) {

		List<BaseEntity> baseEntityList = new ArrayList<>();
		
		for (Object metaObject: metaObjList)
		{
			BaseEntity baseEntity = (BaseEntity) metaObject;
			baseEntity.setId(baseEntity.getId());
			baseEntity.setUuid(baseEntity.getUuid());
			baseEntity.setVersion(baseEntity.getVersion());
			baseEntity.setName(baseEntity.getName());
			baseEntity.setDesc(baseEntity.getDesc());
			baseEntity.setCreatedBy(baseEntity.getCreatedBy());
			baseEntity.setCreatedOn(baseEntity.getCreatedOn());
			baseEntity.setTags(baseEntity.getTags());
			baseEntity.setActive(baseEntity.getActive());
			baseEntity.setPublished(baseEntity.getPublished());
			baseEntity.setAppInfo(baseEntity.getAppInfo());
			baseEntityList.add(baseEntity);
		}
		return baseEntityList;
		
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public List<StatusHolder> getGroupExecStatus(String reftype, String uuid, String version) {
		MetaIdentifier ref = new MetaIdentifier(MetaType.valueOf(reftype), uuid, version);
		String type = ref.getType().toString();
		Class groupExecClass = null;
		String execListName = null;
		StatusHolder statusHolder = null;
		List<StatusHolder> statusHolderList = new ArrayList<>();
		// Find class
		groupExecClass = Helper.getDomainClass(MetaType.valueOf(reftype));		
		if (type.equals(MetaType.rulegroupExec.toString())) {
			//groupExecClass = "com.inferyx.framework.metadata.RuleGroupExec";
			execListName = "ruleExecList";
		} else if (type.equals(MetaType.dqgroupExec.toString())) {
			//groupExecClass = "com.inferyx.framework.metadata.DataQualGroupExec";
			execListName = "dataQualExecList";
		} else if (type.equals(MetaType.profilegroupExec.toString())) {
			//groupExecClass = "com.inferyx.framework.metadata.ProfileGroupExec";
			execListName = "profileExecList";
		}
				
		//Create query
		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("active");
		query.fields().include("name");
		query.fields().include("status");
		query.fields().include(execListName);
		
		// Add criteria
		query.addCriteria(Criteria.where("uuid").is(ref.getUuid()).andOperator(Criteria.where("version").is(ref.getVersion()),
				  Criteria.where("active").is("Y")));
		
		List<Object> metaObjectList = new ArrayList<>();
		
		Class<?> dynamicClass = groupExecClass;
		metaObjectList = (List<Object>) mongoTemplate.find(query, dynamicClass);

		if (metaObjectList == null || metaObjectList.isEmpty()) {
			return null;
		}
		
		if (metaObjectList.get(0) instanceof RuleGroupExec) {
			RuleGroupExec ruleGroupExec = (RuleGroupExec) metaObjectList.get(0);
			if (ruleGroupExec.getExecList() == null || ruleGroupExec.getExecList().isEmpty()) {
				return null;
			}
			for (MetaIdentifierHolder refHolder : ruleGroupExec.getExecList()) {
				statusHolder = new StatusHolder();
				RuleExec ruleExec = (RuleExec)daoRegister.getRefObject(refHolder.getRef());
				refHolder.getRef().setName(ruleExec.getName());
				statusHolder.setMetaRef(refHolder);
				statusHolder.setStatusList(ruleExec.getStatusList());
				statusHolderList.add(statusHolder);
			}
			statusHolder = new StatusHolder();
			MetaIdentifierHolder mih = new MetaIdentifierHolder();
			mih.setRef(ref);
			mih.getRef().setName(ruleGroupExec.getName());
			statusHolder.setMetaRef(mih);
			statusHolder.setStatusList(ruleGroupExec.getStatusList());
			statusHolderList.add(statusHolder);
		} else if (metaObjectList.get(0) instanceof DataQualGroupExec) {
			DataQualGroupExec dataQualGroupExec = (DataQualGroupExec) metaObjectList.get(0);
			if (dataQualGroupExec.getExecList() == null || dataQualGroupExec.getExecList().isEmpty()) {
				return null;
			}
			for (MetaIdentifierHolder refHolder : dataQualGroupExec.getExecList()) {
				statusHolder = new StatusHolder();
				DataQualExec dataQualExec = (DataQualExec)daoRegister.getRefObject(refHolder.getRef());
				refHolder.getRef().setName(dataQualExec.getName());
				statusHolder.setMetaRef(refHolder);
				statusHolder.setStatusList(dataQualExec.getStatusList());
				statusHolderList.add(statusHolder);
			}
			statusHolder = new StatusHolder();
			MetaIdentifierHolder mih = new MetaIdentifierHolder();
			mih.setRef(ref);
			mih.getRef().setName(dataQualGroupExec.getName());
			statusHolder.setMetaRef(mih);
			statusHolder.setStatusList(dataQualGroupExec.getStatusList());
			statusHolderList.add(statusHolder);			
		} else if (metaObjectList.get(0) instanceof ProfileGroupExec) {
			ProfileGroupExec profileGroupExec = (ProfileGroupExec) metaObjectList.get(0);
			if (profileGroupExec.getExecList() == null || profileGroupExec.getExecList().isEmpty()) {
				return null;
			}
			for (MetaIdentifierHolder refHolder : profileGroupExec.getExecList()) {
				statusHolder = new StatusHolder();
				ProfileExec profileExec = (ProfileExec)daoRegister.getRefObject(refHolder.getRef());
				refHolder.getRef().setName(profileExec.getName());
				statusHolder.setMetaRef(refHolder);
				statusHolder.setStatusList(profileExec.getStatusList());
				statusHolderList.add(statusHolder);
			}
			statusHolder = new StatusHolder();
			MetaIdentifierHolder mih = new MetaIdentifierHolder();
			mih.setRef(ref);
			mih.getRef().setName(profileGroupExec.getName());
			statusHolder.setMetaRef(mih);
			statusHolder.setStatusList(profileGroupExec.getStatusList());
			statusHolderList.add(statusHolder);			
		} // End-If 
		return statusHolderList;
	}	
	*/
	@SuppressWarnings("unchecked")
	public List<StatusHolder> getGroupExecStatusReflection(String reftype, String uuid, String version) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JsonProcessingException {
		MetaIdentifier ref = new MetaIdentifier(Helper.getMetaType(reftype), uuid, version);
		String type = ref.getType().toString();
		Class<?> groupExecClass = null;
		String execListName = null;
		//StatusHolder statusHolder = null;
		List<StatusHolder> statusHolderList = new ArrayList<>();
		// Find class
		groupExecClass = Helper.getDomainClass(Helper.getMetaType(reftype));		
		if (type.equals(MetaType.rulegroupExec.toString())) {
			//groupExecClass = "com.inferyx.framework.metadata.RuleGroupExec";
			execListName = "execList";
		} else if (type.equals(MetaType.dqgroupExec.toString())) {
			//groupExecClass = "com.inferyx.framework.metadata.DataQualGroupExec";
			execListName = "execList";
		} else if (type.equals(MetaType.profilegroupExec.toString())) {
			//groupExecClass = "com.inferyx.framework.metadata.ProfileGroupExec";
			execListName = "execList";
		} else if (type.equals(MetaType.recongroupExec.toString())) {
			execListName = "execList";
		}
		 else if (type.equals(MetaType.ingestgroupExec.toString())) {
				execListName = "execList";
			}
				
		//Create query
		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("active");
		query.fields().include("name");
		query.fields().include("statusList");
		query.fields().include(execListName);
		
		// Add criteria
		query.addCriteria(Criteria.where("uuid").is(ref.getUuid()).andOperator(Criteria.where("version").is(ref.getVersion()),
				  Criteria.where("active").is("Y")));
		
		List<Object> metaObjectList = new ArrayList<>();
		
		//Class<?> dynamicClass = groupExecClass;
		metaObjectList = (List<Object>) mongoTemplate.find(query, groupExecClass);

		if (metaObjectList == null || metaObjectList.isEmpty()) {
			return null;
		}
		
		if (metaObjectList.get(0) instanceof RuleGroupExec) {
			RuleGroupExec ruleGroupExec = (RuleGroupExec) metaObjectList.get(0);
			if (ruleGroupExec.getExecList() == null || ruleGroupExec.getExecList().isEmpty()) {
				return null;
			}
			/*for (MetaIdentifierHolder refHolder : ruleGroupExec.getRuleExecList()) {
				statusHolder = new StatusHolder();
				RuleExec ruleExec = (RuleExec)daoRegister.getRefObject(refHolder.getRef());
				refHolder.getRef().setName(ruleExec.getName());
				statusHolder.setMetaRef(refHolder);
				statusHolder.setStatus(ruleExec.getStatus());
				statusHolderList.add(statusHolder);
			}
			statusHolder = new StatusHolder();
			MetaIdentifierHolder mih = new MetaIdentifierHolder();
			mih.setRef(ref);
			mih.getRef().setName(ruleGroupExec.getName());
			statusHolder.setMetaRef(mih);
			statusHolder.setStatus(ruleGroupExec.getStatus());
			statusHolderList.add(statusHolder);*/
			
			statusHolderList = getStatusHolderList(ruleGroupExec.getExecList(), Helper.getMetaType(reftype), ref, ruleGroupExec.getName(), ruleGroupExec.getStatusList());			
		
		} else if (metaObjectList.get(0) instanceof DataQualGroupExec) {
			DataQualGroupExec dataQualGroupExec = (DataQualGroupExec) metaObjectList.get(0);
			if (dataQualGroupExec.getExecList() == null || dataQualGroupExec.getExecList().isEmpty()) {
				return null;
			}
			/*for (MetaIdentifierHolder refHolder : dataQualGroupExec.getDataQualExecList()) {
				statusHolder = new StatusHolder();
				DataQualExec dataQualExec = (DataQualExec)daoRegister.getRefObject(refHolder.getRef());
				refHolder.getRef().setName(dataQualExec.getName());
				statusHolder.setMetaRef(refHolder);
				statusHolder.setStatus(dataQualExec.getStatus());
				statusHolderList.add(statusHolder);
			}
			statusHolder = new StatusHolder();
			MetaIdentifierHolder mih = new MetaIdentifierHolder();
			mih.setRef(ref);
			mih.getRef().setName(dataQualGroupExec.getName());
			statusHolder.setMetaRef(mih);
			statusHolder.setStatus(dataQualGroupExec.getStatus());
			statusHolderList.add(statusHolder);	*/	
			
			statusHolderList = getStatusHolderList(dataQualGroupExec.getExecList(), Helper.getMetaType(reftype), ref, dataQualGroupExec.getName(), dataQualGroupExec.getStatusList());
		
		} else if (metaObjectList.get(0) instanceof ProfileGroupExec) {
			ProfileGroupExec profileGroupExec = (ProfileGroupExec) metaObjectList.get(0);
			if (profileGroupExec.getExecList() == null || profileGroupExec.getExecList().isEmpty()) {
				return null;
			}
			/*for (MetaIdentifierHolder refHolder : profileGroupExec.getProfileExecList()) {
				statusHolder = new StatusHolder();
				ProfileExec profileExec = (ProfileExec)daoRegister.getRefObject(refHolder.getRef());
				refHolder.getRef().setName(profileExec.getName());
				statusHolder.setMetaRef(refHolder);
				statusHolder.setStatus(profileExec.getStatus());
				statusHolderList.add(statusHolder);
			}
			statusHolder = new StatusHolder();
			MetaIdentifierHolder mih = new MetaIdentifierHolder();
			mih.setRef(ref);
			mih.getRef().setName(profileGroupExec.getName());
			statusHolder.setMetaRef(mih);
			statusHolder.setStatus(profileGroupExec.getStatus());
			statusHolderList.add(statusHolder);	*/
			statusHolderList = getStatusHolderList(profileGroupExec.getExecList(), Helper.getMetaType(reftype), ref, profileGroupExec.getName(), profileGroupExec.getStatusList());
			
		} else if (metaObjectList.get(0) instanceof ReconGroupExec) {
			ReconGroupExec reconGroupExec = (ReconGroupExec) metaObjectList.get(0);
			if (reconGroupExec.getExecList() == null || reconGroupExec.getExecList().isEmpty()) {
				return null;
			}
			statusHolderList = getStatusHolderList(reconGroupExec.getExecList(), Helper.getMetaType(reftype), ref, reconGroupExec.getName(), reconGroupExec.getStatusList());
		} // End-If 
		else if (metaObjectList.get(0) instanceof IngestGroupExec) {
			IngestGroupExec ingestGroupExec = (IngestGroupExec) metaObjectList.get(0);
			if (ingestGroupExec.getExecList() == null || ingestGroupExec.getExecList().isEmpty()) {
				return null;
			}
			statusHolderList = getStatusHolderList(ingestGroupExec.getExecList(), Helper.getMetaType(reftype), ref, ingestGroupExec.getName(), ingestGroupExec.getStatusList());
		} // End-If 
		return statusHolderList;
	}	
	
	@SuppressWarnings("unchecked")
	public List<StatusHolder> getStatusHolderList(List<MetaIdentifierHolder> holderList, MetaType metaType, MetaIdentifier ref, String groupExecName, List<Status> status) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, JsonProcessingException{
		
		List<StatusHolder> statusHolderList = new ArrayList<>();
		StatusHolder statusHolder = null;
		
		for(MetaIdentifierHolder refHolder : holderList) {
			statusHolder = new StatusHolder();
			Object object = daoRegister.getRefObject(refHolder.getRef());
			refHolder.getRef().setName((String) object.getClass().getMethod("getName").invoke(object));
			statusHolder.setMetaRef(refHolder);
			statusHolder.setStatusList((List<Status>) object.getClass().getMethod("getStatusList").invoke(object));
			statusHolderList.add(statusHolder);
		}
		statusHolder = new StatusHolder();
		MetaIdentifierHolder mih = new MetaIdentifierHolder();
		mih.setRef(ref);
		mih.getRef().setName(groupExecName);
		statusHolder.setMetaRef(mih);
		statusHolder.setStatusList(status);
		statusHolderList.add(statusHolder);			
		return statusHolderList;
	}

	public String getMetaExecList() throws JsonProcessingException {
		//ObjectMapper mapper = new ObjectMapper().writeValueAsString(MetaType.getMetaExecList());
		return new ObjectMapper().writeValueAsString(MetaType.getMetaExecList());
	}	

	/********************** UNUSED **********************/
	/*public List<BaseEntity>  filterPublished(List<BaseEntity> baseEntityList) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		List<BaseEntity> newBaseEntityList = new ArrayList<>();
		User currentUser = getCurrentUser();
		if(baseEntityList != null)
			if(currentUser != null) {
				for(BaseEntity baseEntity : baseEntityList) {
					if(!StringUtils.isBlank(currentUser.getName())) {
						if(currentUser.getName().equalsIgnoreCase(baseEntity.getCreatedBy().getRef().getName()))
							newBaseEntityList.add(baseEntity);
						if(!currentUser.getName().equalsIgnoreCase(baseEntity.getCreatedBy().getRef().getName())) {
							if(baseEntity.getPublished().equalsIgnoreCase("Y"))
								newBaseEntityList.add(baseEntity);
						}
					}else {
						newBaseEntityList.add(baseEntity);
					}				
				}
				return newBaseEntityList;
			}else
				return baseEntityList;
		else
			return baseEntityList;
	}*/	
	public User getCurrentUser() throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		SessionContext sessionContext = null;
		if(requestAttributes != null) {
			HttpServletRequest request = requestAttributes.getRequest();
			if(request != null) {
				HttpSession session = request.getSession(false);
				if(session != null) {
					sessionContext = (SessionContext) session.getAttribute("sessionContext");
					if(sessionContext != null) {
						MetaIdentifierHolder userHolder = sessionContext.getUserInfo();
						User currentUser = (User) commonServiceImpl.getOneByUuidAndVersionWithoutAppUuid(userHolder.getRef().getUuid(), userHolder.getRef().getVersion(), MetaType.user.toString());
						//logger.info("Current user details ->---->>"+"  Username: "+currentUser.getName()+",  First name: "+currentUser.getFirstName()+",  Last name: "+currentUser.getLastName()+",  UUID: "+currentUser.getUuid());
						return currentUser;
					}else
						logger.info("sessionContext is \""+null+"\"");
				}else
					logger.info("HttpSession is \""+null+"\""); 
			}else
				logger.info("HttpServletResponse response is \""+null+"\"");
		}else
			logger.info("ServletRequestAttributes requestAttributes is \""+null+"\"");
		//logger.info(" Session is not available from ServletRequestAttributes. Falling off to FrameworkThreadLocal");
		sessionContext = FrameworkThreadLocal.getSessionContext().get();
		if(sessionContext != null) {
			logger.info(" Finally got sessionContext ");
			MetaIdentifierHolder userHolder = sessionContext.getUserInfo();
			User currentUser = (User) commonServiceImpl.getOneByUuidAndVersionWithoutAppUuid(userHolder.getRef().getUuid(), userHolder.getRef().getVersion(), MetaType.user.toString());
			//logger.info("Current user details ->---->>"+"  Username: "+currentUser.getName()+",  First name: "+currentUser.getFirstName()+",  Last name: "+currentUser.getLastName()+",  UUID: "+currentUser.getUuid());
			return currentUser;
		}
		return null;
	}	
	
	public List<Function> getFunctionByType(String category){
		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("type");
		query.fields().include("createdOn");
		query.fields().include("appInfo");
		query.fields().include("active");
		query.fields().include("desc");
		query.fields().include("published");
		query.fields().include("inputReq");
		query.fields().include("funcType");
		query.fields().include("functionInfo");
		query.fields().include("category");
		

		query.addCriteria(Criteria.where("category").is(category));

		List<Function> function = new ArrayList<>();
		function = (List<Function>) mongoTemplate.find(query, Function.class);
		return function;
		
	}

	public List<ParamListHolder> getParamByParamList(String paramListUuid) throws JsonProcessingException {	
		List<ParamListHolder> holderList = new ArrayList<>();
			
		ParamList paramList = (ParamList) commonServiceImpl.getLatestByUuid(paramListUuid, MetaType.paramlist.toString(),"N");			
		
		for(Param param : paramList.getParams()) {
			ParamListHolder paramListHolder = new ParamListHolder();
			paramListHolder.setParamId(param.getParamId());
			paramListHolder.setParamName(param.getParamName());
			paramListHolder.setParamType(param.getParamType());
//			if (param.getParamType().equalsIgnoreCase(ParamDataType.ONEDARRAY.toString())
//					|| param.getParamType().equalsIgnoreCase(ParamDataType.TWODARRAY.toString())) {
				paramListHolder.setParamValue(param.getParamValue());	
//			} else { 
//				paramListHolder.setParamValue(new MetaIdentifierHolder(new MetaIdentifier(MetaType.simple, null, null), param.getParamValue()));
//			}
			
			paramListHolder.setRef(new MetaIdentifier(MetaType.paramlist, paramList.getUuid(), paramList.getVersion()));
			paramListHolder.getRef().setName(paramList.getName());
			holderList.add(paramListHolder);
		}
		return holderList;
		}	
	
	public List<ParamListHolder> getParamListByFormula(String formulaUuid) throws JsonProcessingException {	

		Formula formula = (Formula) commonServiceImpl.getLatestByUuid(formulaUuid, MetaType.formula.toString(), "N");

		List<ParamListHolder> holderList = new ArrayList<>();
		ParamList paramList = null;
		if (formula.getDependsOn().getRef().getType().equals(MetaType.paramlist)) {

			paramList = (ParamList) commonServiceImpl.getLatestByUuid(formula.getDependsOn().getRef().getUuid(),
					MetaType.paramlist.toString(), "N");
		}
		if(paramList != null)
			for (Param param : paramList.getParams()) {
				ParamListHolder paramListHolder = new ParamListHolder();
				paramListHolder.setParamId(param.getParamId());
				paramListHolder.setParamName(param.getParamName());
				paramListHolder.setParamType(param.getParamType());
				paramListHolder.setRef(new MetaIdentifier(MetaType.paramlist, paramList.getUuid(), paramList.getVersion()));
				paramListHolder.getRef().setName(paramList.getName());
				holderList.add(paramListHolder);
			}
		return holderList;
	}
	
	public List<ParamListHolder> getParamListByDistribution(String distributionUuid) throws JsonProcessingException {	

		Distribution distribution = (Distribution) commonServiceImpl.getLatestByUuid(distributionUuid, MetaType.distribution.toString(), "N");

		List<ParamListHolder> holderList = new ArrayList<>();
		ParamList paramList = null;
		if (distribution.getParamList()!=null && distribution.getParamList().getRef().getType().equals(MetaType.paramlist)) {

			paramList = (ParamList) commonServiceImpl.getLatestByUuid(
					distribution.getParamList().getRef().getUuid(), MetaType.paramlist.toString(), "N");
		}
		if (paramList != null)
			for (Param param : paramList.getParams()) {
				ParamListHolder paramListHolder = new ParamListHolder();
				paramListHolder.setParamId(param.getParamId());
				paramListHolder.setParamName(param.getParamName());
				paramListHolder.setParamType(param.getParamType());
//				if (param.getParamType().equalsIgnoreCase(ParamDataType.ONEDARRAY.toString())
//						|| param.getParamType().equalsIgnoreCase(ParamDataType.TWODARRAY.toString())) 
					paramListHolder.setParamValue(param.getParamValue());		
//				else
//					paramListHolder.setParamValue(new MetaIdentifierHolder(new MetaIdentifier(MetaType.simple, null, null), param.getParamValue()));	
		
				paramListHolder.setRef(
						new MetaIdentifier(MetaType.paramlist, paramList.getUuid(), paramList.getVersion()));
				paramListHolder.getRef().setName(paramList.getName());
				holderList.add(paramListHolder);
			}

		return holderList;
	}
	
	public List<ParamListHolder> getParamListBySimulate(String simulateUuid) throws JsonProcessingException {

		Simulate simulate = (Simulate) commonServiceImpl.getLatestByUuid(simulateUuid, MetaType.simulate.toString(),
				"N");

		List<ParamListHolder> holderList = new ArrayList<>();
		if (simulate.getDistributionTypeInfo() != null
				&& simulate.getDistributionTypeInfo().getRef().getType().equals(MetaType.distribution)) {
			Distribution distribution = (Distribution) commonServiceImpl.getLatestByUuid(
					simulate.getDistributionTypeInfo().getRef().getUuid(), MetaType.distribution.toString(), "N");

			ParamList paramListDistribution = null;
			ParamList paramListSimulate = null;
			if (distribution.getParamList() != null
					&& distribution.getParamList().getRef().getType().equals(MetaType.paramlist)) {

				paramListDistribution = (ParamList) commonServiceImpl.getLatestByUuid(
						distribution.getParamList().getRef().getUuid(), MetaType.paramlist.toString(), "N");
			}
			if (simulate.getParamList() != null
					&& simulate.getParamList().getRef().getType().equals(MetaType.paramlist)) {

				paramListSimulate = (ParamList) commonServiceImpl.getLatestByUuid(
						simulate.getParamList().getRef().getUuid(), MetaType.paramlist.toString(), "N");
			}
			if (paramListDistribution != null)
				for (Param param : paramListDistribution.getParams()) {
					ParamListHolder paramListHolder = new ParamListHolder();
					paramListHolder.setParamId(param.getParamId());
					paramListHolder.setParamName(param.getParamName());
					paramListHolder.setParamType(param.getParamType());
//					if (param.getParamType().equalsIgnoreCase(ParamDataType.ONEDARRAY.toString())
//							|| param.getParamType().equalsIgnoreCase(ParamDataType.TWODARRAY.toString())) 
						paramListHolder.setParamValue(param.getParamValue());	
//					 else 
//						paramListHolder.setParamValue(new MetaIdentifierHolder(
//								new MetaIdentifier(MetaType.simple, null, null), param.getParamValue()));
					
					paramListHolder.setRef(new MetaIdentifier(MetaType.paramlist, paramListDistribution.getUuid(),
							paramListDistribution.getVersion()));
					paramListHolder.getRef().setName(paramListDistribution.getName());
					holderList.add(paramListHolder);
				}
			if (paramListSimulate != null)
				for (Param param : paramListSimulate.getParams()) {
					ParamListHolder paramListHolder = new ParamListHolder();
					paramListHolder.setParamId(param.getParamId());
					paramListHolder.setParamName(param.getParamName());
					paramListHolder.setParamType(param.getParamType());
//					if (param.getParamType().equalsIgnoreCase(ParamDataType.ONEDARRAY.toString())
//							|| param.getParamType().equalsIgnoreCase(ParamDataType.TWODARRAY.toString())) 
						paramListHolder.setParamValue(param.getParamValue());	
//					else 
//						paramListHolder.setParamValue(new MetaIdentifierHolder(
//								new MetaIdentifier(MetaType.simple, null, null), param.getParamValue()));

					paramListHolder.setRef(new MetaIdentifier(MetaType.paramlist, paramListSimulate.getUuid(),
							paramListSimulate.getVersion()));
					paramListHolder.getRef().setName(paramListSimulate.getName());
					holderList.add(paramListHolder);
				}

			return holderList;
		}
		return holderList;
	}

	
	public List<ParamListHolder> getParamListByOperator(String operatorUuid) throws JsonProcessingException {	
		List<ParamListHolder> holderList = new ArrayList<>();
			
		Operator operator = (Operator) commonServiceImpl.getLatestByUuid(operatorUuid, MetaType.operator.toString(),"N");
		ParamList paramList = (ParamList) commonServiceImpl.getLatestByUuid(operator.getParamList().getRef().getUuid(), MetaType.paramlist.toString(), "N");
	
		for(Param param : paramList.getParams()) {
			ParamListHolder paramListHolder = new ParamListHolder();
			paramListHolder.setParamId(param.getParamId());
			paramListHolder.setParamName(param.getParamName());
			paramListHolder.setParamType(param.getParamType());
//			if (param.getParamType().equalsIgnoreCase(ParamDataType.ONEDARRAY.toString())
//					|| param.getParamType().equalsIgnoreCase(ParamDataType.TWODARRAY.toString())) 
				paramListHolder.setParamValue(param.getParamValue());		
//			else 
//				paramListHolder.setParamValue(new MetaIdentifierHolder(new MetaIdentifier(MetaType.simple, null, null), param.getParamValue()));	
			
			paramListHolder.setRef(new MetaIdentifier(MetaType.paramlist, paramList.getUuid(), paramList.getVersion()));
			paramListHolder.getRef().setName(paramList.getName());
			holderList.add(paramListHolder);
		}
		return holderList;
	}
	
	public List<ParamListHolder> getParamListByOperatorType(String operatorTypeUuid) throws JsonProcessingException {	
		List<ParamListHolder> holderList = new ArrayList<>();
			
		Operator operator = (Operator) commonServiceImpl.getLatestByUuid(operatorTypeUuid, MetaType.operator.toString(),"N");			
		ParamList paramList = (ParamList) commonServiceImpl.getLatestByUuid(operator.getParamList().getRef().getUuid(), MetaType.paramlist.toString(), "N");
	
		for(Param param : paramList.getParams()) {
			ParamListHolder paramListHolder = new ParamListHolder();
			paramListHolder.setParamId(param.getParamId());
			paramListHolder.setParamName(param.getParamName());
			paramListHolder.setParamType(param.getParamType());
//			if (param.getParamType().equalsIgnoreCase(ParamDataType.DISTRIBUTION.toString())
//					|| param.getParamType().equalsIgnoreCase(ParamDataType.DATAPOD.toString())) 
				paramListHolder.setParamValue(param.getParamValue());		
//			else 
//				paramListHolder.setParamValue(new MetaIdentifierHolder(new MetaIdentifier(MetaType.simple, null, null), param.getParamValue()));	
			
			paramListHolder.setRef(new MetaIdentifier(MetaType.paramlist, paramList.getUuid(), paramList.getVersion()));
			paramListHolder.getRef().setName(paramList.getName());
			holderList.add(paramListHolder);
		}
		return holderList;
	}
	
//	public List<ParamList> getmaxVersionList(List<ParamList> paramList)
//	{
//		HashMap<String,String> Uuid_version = new HashMap<String,String>();
//		for(ParamList param:paramList) {
//		Uuid_version.put(param.getVersion(),param.getUuid());
//		}
//		
//		
//		return paramList;
//	}
	
	@SuppressWarnings("unchecked")
	public List<BaseEntity> getParamList(String collectionType,String type,String name, String userName, String startDate,
			String endDate, String tags, String active, String uuid, String version, String published) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		
		MetaType metaType = Helper.getMetaType(type);
		Criteria criteria = new Criteria();
		List<Criteria> criteriaList = new ArrayList<Criteria>();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy z");

		String appUuid = commonServiceImpl.findAppId(type);

		try {
			if (appUuid != null)
				criteriaList.add(where("appInfo.ref.uuid").is(appUuid));
			if (name != null && !name.isEmpty())
				criteriaList.add(where("name").is(name));
			if (userName != null && !userName.isEmpty()) {
				User user = userServiceImpl.findUserByName(userName);
				if (user != null && user.getUuid().equals(getCurrentUser().getUuid())) {
					criteriaList.add(where("createdBy.ref.uuid").is(user.getUuid()));
				} else {
					if (user != null)
						criteriaList.add(where("_id").ne("1").andOperator(
								where("createdBy.ref.uuid").is(user.getUuid()), where("published").is("Y")));
				}
			}

			if ((startDate != null && !startDate.isEmpty()) && (endDate != null && !endDate.isEmpty())) {
				criteriaList.add(where("_id").ne("1").and("createdOn").lte(simpleDateFormat.parse(endDate))
						.gte(simpleDateFormat.parse(startDate)));
			}

			else if (startDate != null && !startDate.isEmpty())
				criteriaList.add(where("createdOn").gte(simpleDateFormat.parse(startDate)));
			else if (endDate != null && !endDate.isEmpty())
				criteriaList.add(where("createdOn").lte(simpleDateFormat.parse(endDate)));
			if (tags != null && !tags.isEmpty()) {
				ArrayList<?> tagList = new ArrayList<>(Arrays.asList(tags.split(",")));
				criteriaList.add(where("tags").all(tagList));
			}
			if (active != null && !active.isEmpty()) {
				criteriaList.add(where("active").is(active));
			}
			if (StringUtils.isNotBlank(uuid)) {
				criteriaList.add(where("uuid").is(uuid));
			}
			if (StringUtils.isNotBlank(version)) {
				criteriaList.add(where("version").is(version));
			}
			if (StringUtils.isNotBlank(published)) {
				criteriaList.add(where("published").is(published));
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		List<ParamList> paramList = new ArrayList<>();
		List<String> versionList = new ArrayList<>();
		List<String> uuidList = new ArrayList<>();

		List<ParamList> metaObjectList = new ArrayList<>();

		Class<?> className = Helper.getDomainClass(metaType);
		if (className == null) {
			return null;
		}
	
			criteriaList.add(where("paramListType").is(Helper.getMetaType(collectionType).toString()));
		


		Criteria criteria2 = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
		Aggregation ruleExecAggr = newAggregation(match(criteria2), group("uuid").max("version").as("version"));
		AggregationResults<ParamList> ruleExecResults = (AggregationResults<ParamList>) mongoTemplate.aggregate(ruleExecAggr, MetaType.paramlist.toString(), className);
		metaObjectList = ruleExecResults.getMappedResults();
		// loop metaObjectList to get uuid,version list...
		for (ParamList paramlist : metaObjectList) {
			uuidList.add(paramlist.getId());
			versionList.add(paramlist.getVersion());
		}
		Query query2 = new Query();
		query2.fields().include("uuid");
		query2.fields().include("version");
		query2.fields().include("name");
		query2.fields().include("createdOn");
		query2.fields().include("tags");
		query2.fields().include("createdBy");
		query2.fields().include("appInfo");
		query2.fields().include("active");
		query2.fields().include("desc");
		query2.fields().include("published");
		query2.addCriteria(			
				Criteria.where("uuid").in(uuidList).andOperator(Criteria.where("version").in(versionList)));
				paramList = (List<ParamList>) mongoTemplate.find(query2, ParamList.class);

//		if (collectionType.toString().equalsIgnoreCase(MetaType.rule.toString())) {
//			query2.addCriteria(
//					Criteria.where("uuid").in(uuidList).andOperator(Criteria.where("version").in(versionList)));
//			paramList = (List<ParamList>) mongoTemplate.find(query2, ParamList.class);
//
//		}
//		if (collectionType.toString().equalsIgnoreCase(MetaType.model.toString())) {
//			query2.addCriteria(
//					Criteria.where("uuid").in(uuidList).andOperator(Criteria.where("version").in(versionList)));
//			paramList = (List<ParamList>) mongoTemplate.find(query2, ParamList.class);
//
//		}
		List<BaseEntity> baseEntities = new ArrayList<>(paramList);
		return commonServiceImpl.resolveBaseEntityList(baseEntities);
	}

	@SuppressWarnings({ "unchecked"})
	public List<CommentView> getCommentByType(String uuid, String type)
			throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NullPointerException, ParseException, JSONException {

		List<BaseEntity> result = new ArrayList<BaseEntity>();
		List<BaseEntity> result1 = new ArrayList<BaseEntity>();
		List<String> uuidList = new ArrayList<String>();
		List<String> versionList = new ArrayList<String>();
		List<Criteria> criteriaList = new ArrayList<Criteria>();
		criteriaList.add(where("dependsOn.ref.uuid").is(uuid));
		MetaType metaType = Helper.getMetaType(type);
		Class<?> className = Helper.getDomainClass(metaType);
		Criteria criteria = new Criteria();
		Criteria criteria2 = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
		Aggregation ruleExecAggr = newAggregation(match(criteria2), group("uuid").max("version").as("version"));
		@SuppressWarnings("rawtypes")
		AggregationResults ruleExecResults = mongoTemplate.aggregate(ruleExecAggr, MetaType.comment.toString(), className);
		result = ruleExecResults.getMappedResults();
		for (BaseEntity baseEntity : result) {
			uuidList.add(baseEntity.getId());
			versionList.add(baseEntity.getVersion());
		}
		Query query = new Query();
		query.fields().include("id");
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("type");
		query.fields().include("createdBy");
		query.fields().include("createdOn");
		query.fields().include("appInfo");
		query.fields().include("active");
		query.fields().include("desc");
		query.fields().include("published");
		query.fields().include("uploadExec");

		query.fields().include("dependsOn");
		query.addCriteria(Criteria.where("uuid").in(uuidList));
		query.addCriteria(Criteria.where("version").in(versionList));
		query.addCriteria(Criteria.where("active").is('Y'));
		result1 = (List<BaseEntity>) mongoTemplate.find(query, Helper.getDomainClass(MetaType.comment));

		result = commonServiceImpl.resolveBaseEntityList(result1);

		List<CommentView> CommentViewList = new ArrayList<CommentView>();
		// query2.addCriteria(Criteria.where("dependsOn.ref.uuid").in(baseEntity.getUuid()));

		for (BaseEntity baseEntity : result) {
			CommentView commentView = new CommentView();
			commentView.setId(baseEntity.getId());
			commentView.setUuid(baseEntity.getUuid());
			commentView.setName(baseEntity.getName());
			commentView.setVersion(baseEntity.getVersion());
			commentView.setCreatedBy(baseEntity.getCreatedBy());
			commentView.setCreatedOn(baseEntity.getCreatedOn());
			commentView.setAppInfo(baseEntity.getAppInfo());
			commentView.setActive(baseEntity.getActive());
			commentView.setDesc(baseEntity.getDesc());
			List<MetaIdentifierHolder> uploadExecHolder = new ArrayList<MetaIdentifierHolder>();
			List<UploadExec> result11 = new ArrayList<UploadExec>();
			//
			result11 = uploadExecServiceImpl.findAllByDependOn(baseEntity.getUuid(),"Y");

			// result1 = (List<BaseEntity>) mongoTemplate.find(query2,
			// Helper.getDomainClass(MetaType.uploadExec));
			for (UploadExec uploadExe : result11) {
				MetaIdentifierHolder metaIdentifierHolder = new MetaIdentifierHolder();
				MetaIdentifier metaIdentifier = new MetaIdentifier();
				metaIdentifier.setUuid(uploadExe.getUuid());
				metaIdentifier.setName(uploadExe.getFileName());
				metaIdentifier.setType(MetaType.uploadExec);
				metaIdentifierHolder.setRef(metaIdentifier);
				uploadExecHolder.add(metaIdentifierHolder);
				commentView.setUploadExecInfo(uploadExecHolder);
			}
			CommentViewList.add(commentView);
		}
		return CommentViewList;
	}
	
	public List<Lov> getLovByType(String type) {
		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("type");
		query.fields().include("createdOn");
		query.fields().include("appInfo");
		query.fields().include("active");
		query.fields().include("desc");
		query.fields().include("published");
		query.fields().include("value");
		query.addCriteria(Criteria.where("type").is(type));
		List<Lov> lov = new ArrayList<>();
		lov = (List<Lov>) mongoTemplate.find(query, Lov.class);
		return lov;
	}

	public List<ParamListHolder> getParamListByTrain(String trainUuid, String trainVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainUuid, trainVersion, MetaType.train.toString());
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), train.getDependsOn().getRef().getVersion(), train.getDependsOn().getRef().getType().toString());

		//ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(algorithm.getParamList().getRef().getUuid(), algorithm.getParamList().getRef().getVersion(), algorithm.getParamList().getRef().getType().toString());
		List<ParamListHolder> plHolderList = new ArrayList<>();
		if(model.getType().equalsIgnoreCase(ExecContext.spark.toString())) {
			Algorithm algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(model.getDependsOn().getRef().getUuid(), model.getDependsOn().getRef().getVersion(), model.getDependsOn().getRef().getType().toString());
			MetaIdentifier plMI = null;
			if(train.getUseHyperParams().equalsIgnoreCase("Y")) {
				plMI = algorithm.getParamListWH().getRef();
			} else {
				plMI = algorithm.getParamListWoH().getRef();			
			}
			
			ParamListHolder plHolder = new ParamListHolder();
			plHolder.setRef(plMI);
			plHolderList.add(plHolder);
			ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(plMI.getUuid(), plMI.getVersion(), plMI.getType().toString());
			if(paramList.getTemplateFlg().equalsIgnoreCase("Y")) {
				List<ParamList> childs = commonServiceImpl.getAllLatestParamListByTemplate(null, paramList.getUuid(), paramList.getVersion(), MetaType.model);
				plHolderList.addAll(persistPLTemplateChilds(childs));
			}
		}
		
		return plHolderList;
	}

	public List<ParamListHolder> persistPLTemplateChilds(List<ParamList> childs){
		List<ParamListHolder> plHolderList = new ArrayList<>();
		for(ParamList paramList : childs) {
			ParamListHolder childPLHolder = new ParamListHolder();
			MetaIdentifier childIdentifier = new MetaIdentifier(MetaType.paramlist, paramList.getUuid(), paramList.getVersion(), paramList.getName());
			childPLHolder.setRef(childIdentifier);
			plHolderList.add(childPLHolder);
		}
		return plHolderList;
	}
	
	/********************** UNUSED 
	 * @param algoClass **********************/
//	private List<ParamList> getChilds(String parentPLUuid, String parentPLVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
//		Query query = new Query();
//		query.fields().include("uuid");
//		query.fields().include("version");
//		query.fields().include("actuve");
//		query.fields().include("name");
//		query.fields().include("appInfo");
//		query.fields().include("createdBy");
//		query.fields().include("createdOn");
//		query.fields().include("desc");
//		query.fields().include("tags");
//		query.fields().include("published");
//		query.fields().include("templateFlg");
//		query.fields().include("templateInfo");
//		query.fields().include("params");
//		query.fields().include("paramListType");
//		
//		query.addCriteria(Criteria.where("templateInfo.ref.uuid").is(parentPLUuid));
//		query.addCriteria(Criteria.where("appInfo.ref.uuid").is(commonServiceImpl.getApp().getUuid()));
//		
//		List<ParamList> paramLists = mongoTemplate.find(query, ParamList.class);
//		
//		List<ParamList> latestParamList = new ArrayList<>();
//		Set<String> uuidSet = new HashSet<>();
//		for(ParamList paramList : paramLists) {
//			if(!uuidSet.contains(paramList.getUuid())) {
//				ParamList latestPL = (ParamList) commonServiceImpl.getLatestByUuid(paramList.getUuid(), MetaType.paramlist.toString(), "N");
//				latestParamList.add(latestPL);
//				uuidSet.add(paramList.getUuid());
//			}
//		}
//		return latestParamList;
//	}
	

	public List<ParamMap> getParamMap(ExecParams execParams, String trainUuid, String trainVersion, Object algoClass) throws Exception{
		Train train = (Train) commonServiceImpl.getOneByUuidAndVersion(trainUuid, trainVersion, MetaType.train.toString());		
		Model model = (Model) commonServiceImpl.getOneByUuidAndVersion(train.getDependsOn().getRef().getUuid(), train.getDependsOn().getRef().getVersion(), train.getDependsOn().getRef().getType().toString());
		Algorithm algo = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(model.getDependsOn().getRef().getUuid(), model.getDependsOn().getRef().getVersion(), MetaType.algorithm.toString());
		
		String algoClassName = algo.getTrainClass();
		
		List<ParamMap> paramMapList = new ArrayList<>();
		if(null!= execParams) {
			if(execParams.getParamInfo() != null) {
				for(ParamSetHolder paramSetHolder : execParams.getParamInfo()){
					List<ParamListHolder> paramListHolder = getParamListHolder(paramSetHolder);
					ParamMap paramMap = getParamMapByPLHolder(paramListHolder, algoClass, true, algoClassName);					
					paramMapList.add(paramMap);
				}
			} else if(execParams.getParamListInfo() != null) {
				List<ParamListHolder> paramListHolderList = execParams.getParamListInfo();
				ParamMap paramMap = getParamMapByPLHolder(paramListHolderList, algoClass, false, algoClassName);						
				paramMapList.add(paramMap);
			} 
		} else {
			List<ParamListHolder> paramListHolderList = new ArrayList<>();
			ParamListHolder plHolder = new ParamListHolder();
			plHolder.setRef(algo.getParamListWoH().getRef());
			paramListHolderList.add(plHolder);
			ParamMap paramMap = getParamMapByPLHolder(paramListHolderList, algoClass, false, algoClassName);						
			paramMapList.add(paramMap);
		}

		return paramMapList;
	}

	public ParamMap getParamMapByPLHolder(List<ParamListHolder> paramListHolder, Object algoClass, boolean filterParams, String algoClassName) throws NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, JsonProcessingException {
		ParamMap paramMap = new ParamMap();
		try {
			for(ParamListHolder plh : paramListHolder) {
				ParamList paramList = (ParamList) commonServiceImpl.getLatestByUuid(plh.getRef().getUuid(), MetaType.paramlist.toString());
				if(filterParams) {						
					for(com.inferyx.framework.domain.Param param : paramList.getParams()){
						if(param.getParamId().equals(plh.getParamId())){
							plh.setParamType(param.getParamType());
							plh.setParamName(param.getParamName());
							break;
						}
					}
					paramMap.put(getParamPair(algoClassName,algoClass, plh.getParamName(), plh.getParamType(), plh.getValue()));
				} else {
					for(com.inferyx.framework.domain.Param param : paramList.getParams()){
						paramMap.put(getParamPair(algoClassName,algoClass, param.getParamName(), param.getParamType(), param.getParamValue().getValue()));
					}
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} 		
		return paramMap;
	}
	
	public ParamPair<?> getParamPair(String algoClassName, Object algoClass2, String paramName, String paramType, String paramValue) throws NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException{
								
		Method method = algoClass2.getClass().getMethod(paramName);
		Object obj = method.invoke(algoClass2);
		
		if(paramType.equalsIgnoreCase("integer")){
			Class<?>[] param = new Class[1];
			param[0] = int.class;
			Method method1 = obj.getClass().getMethod("w", param);
			return (ParamPair<?>)method1.invoke((IntParam)obj,Integer.parseInt(paramValue));
		}  else if(paramType.equalsIgnoreCase("long")) {
			Class<?>[] param = new Class[1];
			param[0] = long.class;
			Method method1 = obj.getClass().getMethod("w", param);
			return (ParamPair<?>)method1.invoke((LongParam)obj,Long.parseLong(paramValue));
		} else if(paramType.equalsIgnoreCase("double")) {
			Class<?>[] param = new Class[1];
			param[0] = double.class;
			Method method1 = obj.getClass().getMethod("w", param);
			return (ParamPair<?>)method1.invoke((DoubleParam)obj,Double.parseDouble(paramValue));
		} /*else if(paramType.equalsIgnoreCase("String")) {
			Class<?>[] param = new Class[1];
			param[0] = String.class;
			obj = (Param<String>)obj;
			Method method1 = obj.getClass().getMethod("w", param);
			return (ParamPair<Object>)method1.invoke((Param<String>)obj, paramValue);
		}*/
		return null;
	}
	
	public List<ParamListHolder> getParamListHolder(ParamSetHolder paramSetHolder) throws JsonProcessingException{
		ParamSet paramSet = (ParamSet) commonServiceImpl.getOneByUuidAndVersion(paramSetHolder.getRef().getUuid(), paramSetHolder.getRef().getVersion(), MetaType.paramset.toString());
				
		List<ParamListHolder> paramListHolderList = null;
		if(null != paramSet){
			for(ParamInfo paramInfo : paramSet.getParamInfo()){
				if(paramSetHolder.getParamSetId().equalsIgnoreCase(paramInfo.getParamSetId())){
					paramListHolderList = paramInfo.getParamSetVal();
					break;
				}
			}
		}
		return paramListHolderList;
	}

	public List<ParamListHolder> getParamListByRule(String ruleUuid, String ruleVersion, MetaType paramListType) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Rule rule = (Rule) commonServiceImpl.getOneByUuidAndVersion(ruleUuid, ruleVersion, MetaType.rule.toString());

		List<ParamListHolder> plHolderList = new ArrayList<>();
		if(rule.getParamList() != null) {
			MetaIdentifier plMI = rule.getParamList().getRef();
			ParamListHolder plHolder = new ParamListHolder();
			plHolder.setRef(plMI);
			plHolderList.add(plHolder);
			ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(plMI.getUuid(), plMI.getVersion(), plMI.getType().toString());
			if(paramList.getTemplateFlg().equalsIgnoreCase("Y")) {
				List<ParamList> childs = commonServiceImpl.getAllLatestParamListByTemplate(null, paramList.getUuid(), paramList.getVersion(), paramListType);
				plHolderList.addAll(persistPLTemplateChilds(childs));
			}
		}
		return plHolderList;
	}

	public List<ParamListHolder> getParamListByAlgorithm(String algoUuid, String algoVersion, String isHyperParam) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Algorithm algorithm = (Algorithm) commonServiceImpl.getOneByUuidAndVersion(algoUuid, algoVersion, MetaType.algorithm.toString());
		
		List<ParamListHolder> plHolderList = new ArrayList<>();
		MetaIdentifier plMI = null;
		if(isHyperParam.equalsIgnoreCase("Y")) {
			plMI = algorithm.getParamListWH().getRef();
		} else {
			plMI = algorithm.getParamListWoH().getRef();
		}
		
		ParamListHolder plHolder = new ParamListHolder();
		plHolder.setRef(plMI);
		plHolderList.add(plHolder);
		ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(plMI.getUuid(), plMI.getVersion(), plMI.getType().toString());
		if(paramList.getTemplateFlg().equalsIgnoreCase("Y")) {
			List<ParamList> childs = commonServiceImpl.getAllLatestParamListByTemplate(null, paramList.getUuid(), paramList.getVersion(), MetaType.model);
			plHolderList.addAll(persistPLTemplateChilds(childs));
		}
		
		return plHolderList;
	}	

	/**
	 * 
	 * @param execParams
	 * @param attributeId
	 * @param ref
	 * @return value
	 * @throws JsonProcessingException
	 * @throws ParseException 
	 * @throws NullPointerException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public String getParamValue(ExecParams execParams, Integer attributeId, MetaIdentifier ref) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		logger.info("Ref : " + ref);
		if(execParams != null) {
			if(execParams.getCurrParamSet() != null) {
				return paramSetServiceImpl.getParamValue(execParams, attributeId, ref);
			} else if(execParams.getParamListHolder() != null) {
				return paramListServiceImpl.getParamValue(execParams, attributeId, ref);
			} else if (execParams.getParamListInfo() != null){
				ParamList paramList = (ParamList)daoRegister.getRefObject(ref);
				Application application = commonServiceImpl.getApp(); 
				ParamList appParamList = (ParamList)daoRegister.getRefObject(application.getParamList().getRef());
				String paramName = null;
				com.inferyx.framework.domain.Param param = null;
				for (int i = 0; i < paramList.getParams().size(); i++) {
					param = paramList.getParams().get(i);
					if (param.getParamId().equals(attributeId.toString())) {
						paramName = param.getParamName();
						break;
					}
				}
				
				for(Param param2 : appParamList.getParams()) {
					if((StringUtils.isBlank(paramName) && param2.getParamId().equalsIgnoreCase(attributeId.toString())) 
							|| param2.getParamName().equals(paramName)) {
						logger.info("Param name from app paramlist : " + param2.getParamName());
						return param2.getParamValue().getValue();
					}
				}
				
				for (ParamListHolder paramListHolder : execParams.getParamListInfo()) {
					if (paramListHolder.getParamName().equals(paramName)) {
						return paramListHolder.getParamValue().getValue();
					}
				}
				
				if (param != null && param.getParamValue() != null) {
					return param.getParamValue().getValue();
				}
			} else {
				ParamList paramList = (ParamList)daoRegister.getRefObject(ref);
				for (com.inferyx.framework.domain.Param param : paramList.getParams()) {
					if (param.getParamId().equals(attributeId+"")) {
						return param.getParamValue().getValue();
					}
				}
			}
		} else {
			String paramName = null;
			String refParamValue = null;
			Application application = commonServiceImpl.getApp(); 
			ParamList appParamList = (ParamList)daoRegister.getRefObject(application.getParamList().getRef());
			ParamList paramList = (ParamList)daoRegister.getRefObject(ref);
			for (com.inferyx.framework.domain.Param param : paramList.getParams()) {
				if (param.getParamId().equals(attributeId.toString())) {
					if(appParamList == null) {
						return param.getParamValue().getValue();	// Nothing in execParams. Send from ref
					} else {	// ExecParams has data. Wait and watch
						paramName = param.getParamName();
						refParamValue = param.getParamValue().getValue();
					}
				}
			}
			
			logger.info("Param name : " + paramName);
			logger.info("Param value : " + refParamValue);
			
			for(Param param : appParamList.getParams()) {
				if((StringUtils.isBlank(paramName) && param.getParamId().equalsIgnoreCase(attributeId.toString())) 
						|| param.getParamName().equals(paramName)) {
					logger.info("Param name from app paramlist : " + param.getParamName());
					return param.getParamValue().getValue();
				}
			}
			
			if (StringUtils.isNotBlank(paramName)) {
				return refParamValue;
			}
	}
		return "''";
	}// End method	

	public List<ParamListHolder> getParamListChilds(String plUuid, String plVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		List<ParamListHolder> plHolderList = new ArrayList<>();				
		ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(plUuid, plVersion, MetaType.paramlist.toString());
		List<ParamList> childs = commonServiceImpl.getAllLatestParamListByTemplate(null, paramList.getUuid(), paramList.getVersion(), null);
		plHolderList.addAll(persistPLTemplateChilds(childs));
				
		return plHolderList;
	}
	
	
	public List<Function> getFunctionByCriteria(String category, String inputReq) throws JsonProcessingException {
		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("type");
		query.fields().include("createdOn");
		query.fields().include("appInfo");
		query.fields().include("active");
		query.fields().include("desc");
		query.fields().include("published");
		query.fields().include("inputReq");
		query.fields().include("funcType");
		query.fields().include("functionInfo");
		query.fields().include("category");

		if (inputReq != null && !inputReq.isEmpty()) {
			query.addCriteria(Criteria.where("inputReq").is(inputReq));
		}

		if (category != null && !category.isEmpty()) {
			query.addCriteria(Criteria.where("category").is(category));
		}

		// if (!inputReq.isEmpty() && !category.isEmpty()) {
		// query.addCriteria(
		// Criteria.where("inputReq").is(inputReq).andOperator(Criteria.where("category").is(category)));
		// }
		List<Function> result = new ArrayList<>();
		List<Function> functions = new ArrayList<>();
		functions = (List<Function>) mongoTemplate.find(query, Function.class);
		
		for (Function function : functions) {
			Function latestFunction = (Function) commonServiceImpl.getLatestByUuid(function.getUuid(),
					MetaType.function.toString(), "N");
			result.add(latestFunction);
		}

		return result;

	}
	
	public List<ParamListHolder> getParamByApp(String uuid) throws JsonProcessingException {	
		List<ParamListHolder> holderList = new ArrayList<>();
		ParamList paramList=new ParamList();
		Application application=new Application();
		if (!uuid.isEmpty() && uuid != null) {
			application = (Application) commonServiceImpl.getLatestByUuid(uuid,
					MetaType.application.toString(), "N");
			

		} else {
			MetaIdentifierHolder appdetails=securityServiceImpl.getAppInfo();
			 application = (Application) commonServiceImpl.getLatestByUuid(appdetails.getRef().getUuid(),
					MetaType.application.toString(), "N");

		}
		 paramList = (ParamList) commonServiceImpl
				.getLatestByUuid(application.getParamList().getRef().getUuid(), MetaType.paramlist.toString(), "N");
		for(Param param : paramList.getParams()) {
			ParamListHolder paramListHolder = new ParamListHolder();
			paramListHolder.setParamId(param.getParamId());
			paramListHolder.setParamName(param.getParamName());
			paramListHolder.setParamType(param.getParamType());
			paramListHolder.setParamValue(param.getParamValue());		
			paramListHolder.setRef(new MetaIdentifier(MetaType.paramlist, paramList.getUuid(), paramList.getVersion()));
			paramListHolder.getRef().setName(paramList.getName());
			holderList.add(paramListHolder);
		}
		return holderList;
		}	
	
	
	public List<DataStore> getDatastoreByDatapod(String uuid, String version) throws JsonProcessingException {
		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("type");
		query.fields().include("createdOn");
		query.fields().include("createdBy");
		query.fields().include("appInfo");
		query.fields().include("active");
		query.fields().include("desc");
		query.fields().include("published");
		query.fields().include("location");
		query.fields().include("numRows");
		query.fields().include("runMode");
		query.fields().include("metaId");
		query.fields().include("execId");
		query.fields().include("persistMode");

		if (uuid != null && !uuid.isEmpty()) {
			query.addCriteria(Criteria.where("metaId.ref.uuid").is(uuid));
		}
		List<DataStore> datastoreList = new ArrayList<>();
		datastoreList = (List<DataStore>) mongoTemplate.find(query, DataStore.class);
		
		
		return datastoreList;

	}

	public List<ParamListHolder> getParamListByDag(String dagUuid, String dagVersion, MetaType paramListType) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		Dag dag = (Dag) commonServiceImpl.getOneByUuidAndVersion(dagUuid, dagVersion, MetaType.dag.toString());

		List<ParamListHolder> plHolderList = new ArrayList<>();
		if(dag.getParamList() != null) {
			MetaIdentifier plMI = dag.getParamList().getRef();
			ParamListHolder plHolder = new ParamListHolder();
			plHolder.setRef(plMI);
			plHolderList.add(plHolder);
			ParamList paramList = (ParamList) commonServiceImpl.getOneByUuidAndVersion(plMI.getUuid(), plMI.getVersion(), plMI.getType().toString());
			if(paramList.getTemplateFlg().equalsIgnoreCase("Y")) {
				List<ParamList> childs = commonServiceImpl.getAllLatestParamListByTemplate(null, paramList.getUuid(), paramList.getVersion(), paramListType);
				plHolderList.addAll(persistPLTemplateChilds(childs));
			}
		}
		return plHolderList;
	}

	public List<BaseEntityStatus> getBatchMetaInfoByBatchExec(String batchExecUuid, String batchExecVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		BatchExec batchExec = (BatchExec) commonServiceImpl.getOneByUuidAndVersion(batchExecUuid, batchExecVersion, MetaType.batchExec.toString());
	
		List<BaseEntityStatus> baseEntityStatusList = new ArrayList<>();
		if(batchExec.getExecList() != null) {
			for(MetaIdentifierHolder execList : batchExec.getExecList()) {
				List<Status> execStatus = null;
				MetaIdentifier ref = execList.getRef();			
				Object metaObject =  commonServiceImpl.getOneByUuidAndVersion(ref.getUuid(), ref.getVersion(), ref.getType().toString());
				
				if (ref.getType().toString().equalsIgnoreCase(MetaType.dagExec.toString())) {
					DagExec execObject = new DagExec();
					execObject = (DagExec) metaObject;
				    execStatus = (List<Status>) execObject.getStatusList();
				}
				
				BaseEntityStatus baseEntityStatus = new BaseEntityStatus();			
				BaseEntity baseEntityTmp = (BaseEntity) metaObject;			
				baseEntityTmp = resolveBaseEntity(baseEntityTmp);
				baseEntityStatus.setId(baseEntityTmp.getId());
				baseEntityStatus.setUuid(baseEntityTmp.getUuid());
				baseEntityStatus.setVersion(baseEntityTmp.getVersion());
				baseEntityStatus.setName(baseEntityTmp.getName());
				baseEntityStatus.setDesc(baseEntityTmp.getDesc());
				baseEntityStatus.setCreatedBy(baseEntityTmp.getCreatedBy());
				baseEntityStatus.setCreatedOn(baseEntityTmp.getCreatedOn());
				baseEntityStatus.setTags(baseEntityTmp.getTags());
				baseEntityStatus.setActive(baseEntityTmp.getActive());
				baseEntityStatus.setPublished(baseEntityTmp.getPublished());
				baseEntityStatus.setAppInfo(baseEntityTmp.getAppInfo());
				baseEntityStatus.setStatus(execStatus);
				baseEntityStatus.setType(ref.getType().toString());
				baseEntityStatusList.add(baseEntityStatus);				
			}
		}
		
		return baseEntityStatusList;
	}

	/********************** UNUSED **********************/
	/*public List<String> getFileDetailsByDatasource(String datasourceUuid) throws JsonProcessingException {
		Datasource datasource = (Datasource) commonServiceImpl.getLatestByUuid(datasourceUuid, MetaType.datasource.toString());
		File folder = new File(datasource.getPath());
		File[] listOfFiles = folder.listFiles();
		List<String> fileNameList = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String fileName = listOfFiles[i].getName().substring(0, listOfFiles[i].getName().indexOf("."));
				fileNameList.add(fileName);
			} else if (listOfFiles[i].isDirectory()) {
				logger.info("Directory " + listOfFiles[i].getName());
			}
		}		
		return fileNameList;
	}*/
	
	public List<BaseEntity> getDatapodByDatasource(String datasourceUuid) {		
		MatchOperation filter = match(new Criteria("datasource.ref.uuid").is(datasourceUuid));
		GroupOperation groupOperation = group("uuid").max("version").as("version");
		SortOperation sortOperation = sort(new Sort(Direction.DESC, "version"));
		Aggregation aggregation = newAggregation(filter, groupOperation, sortOperation);
		AggregationResults<Datapod> aggregationResults = mongoTemplate.aggregate(aggregation, MetaType.datapod.toString().toLowerCase(), Datapod.class);
		
		List<BaseEntity> datapodBE = new ArrayList<>();
		for(Datapod datapod : aggregationResults.getMappedResults()) {
			Datapod datapod2 = getDatapod(datapod.getId(), datapod.getVersion());
			if(datapod2 != null) {
				BaseEntity baseEntity = new BaseEntity();
				baseEntity.setUuid(datapod2.getUuid());
				baseEntity.setVersion(datapod2.getVersion());
				baseEntity.setName(datapod2.getName());
				baseEntity.setDesc(datapod2.getDesc());
				baseEntity.setCreatedBy(datapod2.getCreatedBy());
				baseEntity.setCreatedOn(datapod2.getCreatedOn());
				baseEntity.setTags(datapod2.getTags());
				baseEntity.setActive(datapod2.getActive());
				baseEntity.setPublished(datapod2.getPublished());
				baseEntity.setAppInfo(datapod2.getAppInfo());
				datapodBE.add(baseEntity);				
			}	
		}
		
		return datapodBE;
	}
	
	public Datapod getDatapod(String uuid, String version) {
		Query query = new Query();
		query.fields().exclude("_id");
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("active");
		query.fields().include("name");
		query.fields().include("appInfo");
		query.fields().include("createdBy");
		query.fields().include("createdOn");
		query.fields().include("desc");
		query.fields().include("tags");
		query.fields().include("published");
		
		if(uuid != null)
			query.addCriteria(Criteria.where("uuid").is(uuid));
		if(version != null)
			query.addCriteria(Criteria.where("version").is(version));
		
		List<Datapod> datapodList = mongoTemplate.find(query, Datapod.class);
		if(!datapodList.isEmpty()) {
			return datapodList.get(0);
		} else {
			return null;
		}
	}

	public List<Datasource> getDatasourceForFile() {
		@SuppressWarnings("unchecked")
		List<Datasource> datasources = commonServiceImpl.findAllLatest(MetaType.datasource);
		List<Datasource> dsForFile = new ArrayList<>();
		for(Datasource datasource : datasources) {
			if(datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
				/*BaseEntity baseEntity = new BaseEntity();
				baseEntity.setUuid(datasource.getUuid());
				baseEntity.setVersion(datasource.getVersion());
				baseEntity.setName(datasource.getName());
				baseEntity.setDesc(datasource.getDesc());
				baseEntity.setCreatedBy(datasource.getCreatedBy());
				baseEntity.setCreatedOn(datasource.getCreatedOn());
				baseEntity.setTags(datasource.getTags());
				baseEntity.setActive(datasource.getActive());
				baseEntity.setPublished(datasource.getPublished());
				baseEntity.setAppInfo(datasource.getAppInfo());*/
				dsForFile.add(datasource);
			}
		}
		return dsForFile;
	}

	public List<Datasource> getDatasourceForTable() {
		@SuppressWarnings("unchecked")
		List<Datasource> datasources = commonServiceImpl.findAllLatest(MetaType.datasource);
		List<Datasource> dsForFile = new ArrayList<>();
		for(Datasource datasource : datasources) {
			if(!datasource.getType().equalsIgnoreCase(ExecContext.FILE.toString())) {
				/*BaseEntity baseEntity = new BaseEntity();
				baseEntity.setUuid(datasource.getUuid());
				baseEntity.setVersion(datasource.getVersion());
				baseEntity.setName(datasource.getName());
				baseEntity.setDesc(datasource.getDesc());
				baseEntity.setCreatedBy(datasource.getCreatedBy());
				baseEntity.setCreatedOn(datasource.getCreatedOn());
				baseEntity.setTags(datasource.getTags());
				baseEntity.setActive(datasource.getActive());
				baseEntity.setPublished(datasource.getPublished());
				baseEntity.setAppInfo(datasource.getAppInfo());*/
				dsForFile.add(datasource);
			}
		}
		return dsForFile;
	}
	public List<Datasource> getDatasourceForStream() {
		@SuppressWarnings("unchecked")
		List<Datasource> datasources = commonServiceImpl.findAllLatest(MetaType.datasource);
		List<Datasource> dsForFile = new ArrayList<>();
		for(Datasource datasource : datasources) {
			if(datasource.getType().equalsIgnoreCase(ExecContext.STREAM.toString())) {
				/*BaseEntity baseEntity = new BaseEntity();
				baseEntity.setUuid(datasource.getUuid());
				baseEntity.setVersion(datasource.getVersion());
				baseEntity.setName(datasource.getName());
				baseEntity.setDesc(datasource.getDesc());
				baseEntity.setCreatedBy(datasource.getCreatedBy());
				baseEntity.setCreatedOn(datasource.getCreatedOn());
				baseEntity.setTags(datasource.getTags());
				baseEntity.setActive(datasource.getActive());
				baseEntity.setPublished(datasource.getPublished());
				baseEntity.setAppInfo(datasource.getAppInfo());*/
				dsForFile.add(datasource);
			}
		}
		return dsForFile;
	}

	public List<Message> getMessageByUuidAndVersion(String uuid, String version) {

		Query query = new Query();
		query.fields().include("uuid");
		query.fields().include("version");
		query.fields().include("name");
		query.fields().include("desc");
		query.fields().include("createdBy");
		query.fields().include("createdOn");
		query.fields().include("tags");
		query.fields().include("active");
		query.fields().include("published");
		query.fields().include("appInfo");		
		query.fields().include("activityInfo");
		query.fields().include("code");
		query.fields().include("status");
		query.fields().include("message");
		query.fields().include("dependsOn");
		
		if (uuid != null && !uuid.isEmpty()) 
			query.addCriteria(Criteria.where("dependsOn.ref.uuid").is(uuid));
		
		if (version != null && !version.isEmpty()) 
			query.addCriteria(Criteria.where("dependsOn.ref.version").is(version));
		
		List<Message> messages = new ArrayList<>();
		messages = (List<Message>) mongoTemplate.find(query, Message.class);		
		return messages;
	}
}
