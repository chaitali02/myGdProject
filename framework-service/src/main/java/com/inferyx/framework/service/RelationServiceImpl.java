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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaSparkContext;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IRelationDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class RelationServiceImpl {
	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IRelationDao iRelationDao;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	RegisterService registerService;
    @Autowired
    CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(RelationServiceImpl.class);

	/********************** UNUSED **********************/
	/*public Relation findLatest() {
		return resolveName(iRelationDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/
	/********************** UNUSED **********************/
	/*public Relation findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iRelationDao.findOneById(appUuid, id);
		} else
			return iRelationDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public Relation findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
				if (appUuid != null) {
		return iRelationDao.findOneByUuidAndVersion(appUuid, uuid, version);
				}
				else
				{
					return iRelationDao.findOneByUuidAndVersion(uuid, version);
				}
	}*/

	/********************** UNUSED **********************/
	/*public Relation getOneByUuidAndVersion(String uuid, String version) {

		return iRelationDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Relation findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iRelationDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iRelationDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public Relation save(Relation relation) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		relation.setAppInfo(metaIdentifierHolderList);
		relation.setBaseEntity();
		Relation relationDet=iRelationDao.save(relation);
		registerGraph.updateGraph((Object) relationDet, MetaType.relation);
		return relationDet;
	}*/

	/********************** UNUSED **********************/
	/*public Relation resolveName(Relation relation) {
		if (relation.getCreatedBy() != null) {
			String createdByRefUuid = relation.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			relation.getCreatedBy().getRef().setName(user.getName());
		}
		String dependsOnRefUuid = relation.getDependsOn().getRef().getUuid();
		Datapod datapodDO = datapodServiceImpl.findLatestByUuid(dependsOnRefUuid);

		String datapodName = datapodDO.getName();
		relation.getDependsOn().getRef().setName(datapodName);
		if (relation.getAppInfo() != null) {
			for (int i = 0; i < relation.getAppInfo().size(); i++) {
				String appUuid = relation.getAppInfo().get(i).getRef().getUuid();
				Application application = applicationServiceImpl.findLatestByUuid(appUuid);
				String appName = application.getName();
				relation.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		for (int i = 0; i < relation.getRelationInfo().size(); i++) {

			//for (int l = 0; l < relation.getRelationInfo().get(i).getJoin().size(); l++) {
				String joinRefUuid = relation.getRelationInfo().get(i).getJoin().getRef().getUuid();
				Datapod joindatapodDO = datapodServiceImpl.findLatestByUuid(joinRefUuid);
				String joinDatapodName = joindatapodDO.getName();
				relation.getRelationInfo().get(i).getJoin().getRef().setName(joinDatapodName);
		//	}
			for (int j = 0; j < relation.getRelationInfo().get(i).getJoinKey().size(); j++) {
				for (int k = 0; k < relation.getRelationInfo().get(i).getJoinKey().get(j).getOperand().size(); k++) {

					String operandRefUuid = relation.getRelationInfo().get(i).getJoinKey().get(j).getOperand().get(k)
							.getRef().getUuid();
					Integer operandAttributeId = relation.getRelationInfo().get(i).getJoinKey().get(j).getOperand()
							.get(k).getAttributeId();
					Datapod operandDatapodDO = datapodServiceImpl.findLatestByUuid(operandRefUuid);
					String operandDatapodName = operandDatapodDO.getName();
					relation.getRelationInfo().get(i).getJoinKey().get(j).getOperand().get(k).getRef()
							.setName(operandDatapodName);
					List<Attribute> attributeList = operandDatapodDO.getAttributes();
					relation.getRelationInfo().get(i).getJoinKey().get(j).getOperand().get(k)
							.setAttributeName(attributeList.get(operandAttributeId).getName());
				}
			}
		}
		return relation;
	}
*/
	/********************** UNUSED **********************/
	/*public List<Relation> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iRelationDao.findAll();
		}
		return iRelationDao.findAll(appUuid);
	}*/

	/*public Relation update(Relation relation) throws IOException {
		relation.exportBaseProperty();
		Relation relationDet=iRelationDao.save(relation);
		registerService.createGraph();
		return relationDet;
	}*/

	/********************** UNUSED **********************/
	/*public boolean isExists(String id) {
		return iRelationDao.exists(id);
	}*/

	/********************** UNUSED **********************/
	/*public void delete(String id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Relation relation = iRelationDao.findOneById(appUuid, id);
		relation.setActive("N");
		iRelationDao.save(relation);
//		String ID = relation.getId();
//		iRelationDao.delete(ID);
//		relation.exportBaseProperty();
//		logger.info(relation);
//		// iRelationDao.save(relation);
	}*/

	public List<Relation> findRelationByDatapod(String datapodUUID) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		List<Relation> relationList = iRelationDao.findRelationByDatapod(appUuid, datapodUUID);
		return relationList;
	}

	/********************** UNUSED **********************/
	/*public List<Relation> test(String param1) {
		return iRelationDao.test(param1);
	}*/

	/********************** UNUSED **********************/
	/*public Relation findAllByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iRelationDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iRelationDao.findAllByUuid(appUuid, uuid);
	}*/

	/********************** UNUSED **********************/
	/*public List<Relation> findAllLatest() {
		{
			// String appUuid =
			// securityServiceImpl.getAppInfo().getRef().getUuid();
			Aggregation relationAggr = newAggregation(group("uuid").max("version").as("version"));
			AggregationResults<Relation> relationResults = mongoTemplate.aggregate(relationAggr, "relation",
					Relation.class);
			List<Relation> relationList = relationResults.getMappedResults();

			// Fetch the relation details for each id
			List<Relation> result = new ArrayList<Relation>();
			for (Relation s : relationList) {
				Relation relationLatest;
				String appUuid = (securityServiceImpl.getAppInfo() != null
						&& securityServiceImpl.getAppInfo().getRef() != null)
								? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
				if (appUuid != null) {
					// String appUuid =
					// securityServiceImpl.getAppInfo().getRef().getUuid();;
					relationLatest = iRelationDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
				} else {
					relationLatest = iRelationDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
				}
				// logger.debug("datapodLatest is " + datapodLatest.getName());
				if(relationLatest != null)
				{
				result.add(relationLatest);
				}
			}
			return result;
		}

	}*/

	/********************** UNUSED **********************/
	/*public List<Relation> findAllLatestActive() {
		Aggregation relationAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Relation> relationResults = mongoTemplate.aggregate(relationAggr, "relation",
				Relation.class);
		List<Relation> relationList = relationResults.getMappedResults();

		// Fetch the relation details for each id
		List<Relation> result = new ArrayList<Relation>();
		for (Relation r : relationList) {
			Relation relationLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				relationLatest = iRelationDao.findOneByUuidAndVersion(appUuid, r.getId(), r.getVersion());
			} else {
				relationLatest = iRelationDao.findOneByUuidAndVersion(r.getId(), r.getVersion());
			}

			if(relationLatest != null)
			{
			result.add(relationLatest);
			}
		}
		return result;
	}*/

	public List<Datapod> findDatapodByRelation(String relationUuid,String version) throws JSONException, JsonProcessingException {
		List<String> datapodUuid = new ArrayList<String>();
		List<Datapod> finalDatapodList = new ArrayList<Datapod>();
		/*String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
		Relation relationDetails = null;
		if(version.equals(null) || version.isEmpty() || version.equals(""))	{
		/*if (appUuid != null) {			
			relationDetails = iRelationDao.findLatestByUuid(appUuid, relationUuid,
					new Sort(Sort.Direction.DESC, "version"));
		} else {
			relationDetails = iRelationDao.findLatestByUuid(relationUuid, new Sort(Sort.Direction.DESC, "version"));
		}*/
			relationDetails = (Relation) commonServiceImpl.getLatestByUuid(relationUuid, MetaType.relation.toString());
		}else{
			/*if (appUuid != null) {			
				relationDetails = iRelationDao.findOneByUuidAndVersion(appUuid, relationUuid, version);
			} else {
				relationDetails = iRelationDao.findOneByUuidAndVersion(relationUuid, version);
			}*/
			relationDetails = (Relation) commonServiceImpl.getOneByUuidAndVersion(relationUuid, version, MetaType.relation.toString());
		}
		String sourceUUId = relationDetails.getDependsOn().getRef().getUuid();
		datapodUuid.add(sourceUUId);
		if (relationDetails.getRelationInfo().size() > 0) {
			for (int i = 0; i < relationDetails.getRelationInfo().size(); i++) {
				String joinInfoUUId = relationDetails.getRelationInfo().get(i).getJoin().getRef().getUuid();
				datapodUuid.add(joinInfoUUId);
			}
		}
		logger.info("datapoduuid : " + datapodUuid);
		for (int j = 0; j < datapodUuid.size(); j++) {
			//Datapod datapod = datapodServiceImpl.findLatestByUuid(datapodUuid.get(j));
			Datapod datapod = (Datapod) commonServiceImpl.getLatestByUuid(datapodUuid.get(j), MetaType.datapod.toString());
			finalDatapodList.add(datapod);
		}
		return finalDatapodList;
	}

	/********************** UNUSED **********************/
	/*public List<Relation> resolveName(List<Relation> relation) {
		List<Relation> relationList = new ArrayList<Relation>();
		for (Relation rel : relation) {
			String createdByRefUuid = rel.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			rel.getCreatedBy().getRef().setName(user.getName());
			relationList.add(rel);
		}
		return relationList;
	}*/

	/********************** UNUSED **********************/
	/*public List<Relation> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iRelationDao.findAllVersion(appUuid, uuid);
		} else
			return iRelationDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED **********************/
	/*public Relation getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iRelationDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iRelationDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Relation relation) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Relation relNew = new Relation();
		relNew.setName(relation.getName()+"_copy");
		relNew.setActive(relation.getActive());
		relNew.setDependsOn(relation.getDependsOn());
		relNew.setDesc(relation.getDesc());
		relNew.setRelationInfo(relation.getRelationInfo());
		relNew.setTags(relation.getTags());
		save(relNew);
		ref.setType(MetaType.relation);
		ref.setUuid(relNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED **********************/	
	/*public List<BaseEntity> findList(List<? extends BaseEntity> relationList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity relation : relationList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = relation.getId();
			String uuid = relation.getUuid();
			String version = relation.getVersion();
			String name = relation.getName();
			String desc = relation.getDesc();
			String published=relation.getPublished();
			MetaIdentifierHolder createdBy = relation.getCreatedBy();
			String createdOn = relation.getCreatedOn();
			String[] tags = relation.getTags();
			String active = relation.getActive();
			List<MetaIdentifierHolder> appInfo = relation.getAppInfo();
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