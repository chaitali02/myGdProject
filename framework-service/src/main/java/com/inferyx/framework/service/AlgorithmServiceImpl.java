package com.inferyx.framework.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.inferyx.framework.dao.IAlgorithmDao;
import com.inferyx.framework.domain.Algorithm;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class AlgorithmServiceImpl {
	
	@Autowired
	GraphRegister<?> registerGraph;/*
	@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IAlgorithmDao iAlgorithmDao;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	
	static final Logger logger = Logger.getLogger(AlgorithmServiceImpl.class);

	/********************** UNUSED **********************/
	/*public Algorithm findLatest() {
		return resolveName(iAlgorithmDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public Algorithm findAllByUuid(String uuid) {
		return iAlgorithmDao.findAllByUuid(uuid);	
	}*/

	/********************** UNUSED **********************/
	/*public Algorithm findLatestByUuid(String uuid){
		return iAlgorithmDao.findLatestByUuid(uuid,new Sort(Sort.Direction.DESC, "version"));	
	}*/

	/********************** UNUSED **********************/
	/*public Algorithm findOneByUuidAndVersion(String uuid,String version){
		return iAlgorithmDao.findOneByUuidAndVersion(uuid,version);
	}*/

	/********************** UNUSED **********************/
	/*public Algorithm findOneById(String id){
		return iAlgorithmDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public List<Algorithm> findAll(){
		return iAlgorithmDao.findAll();
	}*/

	/********************** UNUSED **********************/
	/*public void  delete(String Id){
		Algorithm algorithm = iAlgorithmDao.findOne(Id);
		algorithm.setActive("N");
		iAlgorithmDao.save(algorithm);
		//String ID=application.getId();
		//iApplicationDao.delete(ID);
		//application.exportBaseProperty();
	}*/

	/********************** UNUSED **********************/
	/*public Algorithm save(Algorithm algorithm) throws Exception{
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		algorithm.setAppInfo(metaIdentifierHolderList);
		algorithm.setBaseEntity();
		Algorithm app=iAlgorithmDao.save(algorithm);
		registerGraph.updateGraph((Object) app, MetaType.algorithm);
		return app;
	}*/

	/********************** UNUSED **********************/
	/*public List<Algorithm> resolveName(List<Algorithm> algorithms) {
		List<Algorithm> algorithmList = new ArrayList<Algorithm>(); 
		for(Algorithm algorithm : algorithms)
		{
			String createdByRefUuid = algorithm.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			algorithm.getCreatedBy().getRef().setName(user.getName());
			algorithmList.add(algorithm);
		}
		return algorithmList;
	}*/

	/********************** UNUSED **********************/
	/*public Algorithm resolveName(Algorithm algorithm) {
		if (algorithm.getCreatedBy() != null) {
			String createdByRefUuid = algorithm.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			algorithm.getCreatedBy().getRef().setName(user.getName());
		}
		if (algorithm.getAppInfo() != null) {
			for (int i = 0; i < algorithm.getAppInfo().size(); i++) {
				String appUuid = algorithm.getAppInfo().get(i).getRef().getUuid();
				Application application = applicationServiceImpl.findLatestByUuid(appUuid);
				String appName = application.getName();
				algorithm.getAppInfo().get(i).getRef().setName(appName);
			}
		}		
		return algorithm;
	}*/	

	/********************** UNUSED **********************/
	/*public List<Algorithm> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iAlgorithmDao.findAllVersion(appUuid, uuid);
		} else
			return iAlgorithmDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public List<Algorithm> findAllLatest() {	
			   Aggregation AlgorithmAggr = newAggregation(group("uuid").max("version").as("version"));
			   AggregationResults<Algorithm> AlgorithmResults = mongoTemplate.aggregate(AlgorithmAggr, "algorithm", Algorithm.class);	   
			   List<Algorithm> AlgorithmList = AlgorithmResults.getMappedResults();

			   // Fetch the relation details for each id
			   List<Algorithm> result=new  ArrayList<Algorithm>();
			   for(Algorithm a :AlgorithmList)
			   {   
				   Algorithm AlgorithmLatest = iAlgorithmDao.findOneByUuidAndVersion(a.getId(),a.getVersion());
				   result.add(AlgorithmLatest);
			   }
			   return result;			
	}*/

	/********************** UNUSED **********************/
	/*public List<Algorithm> findAllLatestActive() 	
	{ 
		Aggregation appAggr = newAggregation(match(Criteria.where("active").is("Y")),match(Criteria.where("name").ne(null)),group("uuid").max("version").as("version"));
	   AggregationResults<Algorithm> appResults = mongoTemplate.aggregate(appAggr, "algorithm", Algorithm.class);	   
	   List<Algorithm> appList = appResults.getMappedResults();

	   // Fetch the application details for each id
	   List<Algorithm> result=new  ArrayList<Algorithm>();
	   for(Algorithm a : appList)
	   {   		     
		   Algorithm appLatest = iAlgorithmDao.findOneByUuidAndVersion(a.getId(), a.getVersion());  		   
		   result.add(appLatest);
	   }
	   return result;
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Algorithm algorithm) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Algorithm appNew = new Algorithm();
		appNew.setName(algorithm.getName()+"_copy");
		appNew.setActive(algorithm.getActive());		
		appNew.setDesc(algorithm.getDesc());		
		appNew.setTags(algorithm.getTags());	
		save(appNew);
		ref.setType(MetaType.algorithm);
		ref.setUuid(appNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/
/*	public List<BaseEntity> findList(List<? extends BaseEntity> algorithmList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity algorithm : algorithmList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = algorithm.getId();
			String uuid = algorithm.getUuid();
			String version = algorithm.getVersion();
			String name = algorithm.getName();
			String desc = algorithm.getDesc();
			String published=algorithm.getPublished();
			MetaIdentifierHolder createdBy = algorithm.getCreatedBy();
			String createdOn = algorithm.getCreatedOn();
			String[] tags = algorithm.getTags();
			String active = algorithm.getActive();
			List<MetaIdentifierHolder> appInfo = algorithm.getAppInfo();
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
	}*/
}
