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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.dao.IFormulaDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.AttributeSource;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.FormulaType;
import com.inferyx.framework.domain.FormulaTypeHolder;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class FormulaServiceImpl {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IFormulaDao iFormulaDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;	
	@Autowired
	DatasetServiceImpl datasetServiceImpl;
	@Autowired
	ExpressionServiceImpl expressionServiceImpl;
	@Autowired
	RegisterService registerService;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;

	static final Logger logger = Logger.getLogger(FormulaServiceImpl.class);
	
	/********************** UNUSED **********************/
	/*public Formula findLatest() {
		return resolveName(iFormulaDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public Formula findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iFormulaDao.findOneById(appUuid, id);
		} else
			return iFormulaDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public Formula save(Formula formula) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		formula.setAppInfo(metaIdentifierHolderList);
		formula.setBaseEntity();
		Formula formulaDet=iFormulaDao.save(formula);
		registerGraph.updateGraph((Object) formulaDet, MetaType.formula);
		return formulaDet;
	}*/

	/********************** UNUSED **********************/
	/*public List<Formula> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iFormulaDao.findAll();
		}
		return iFormulaDao.findAll(appUuid);
	}*/

	/********************** UNUSED **********************/
	/*public boolean isExists(String id) {
		return iFormulaDao.exists(id);
	}*/

	/********************** UNUSED **********************/
	/*public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Formula formula = iFormulaDao.findOneById(appUuid, Id);
		formula.setActive("N");
		iFormulaDao.save(formula);
//		String ID = formula.getId();
//		iFormulaDao.delete(ID);
//		formula.exportBaseProperty();
	}*/

	/********************** UNUSED **********************/
	/*public Formula resolveName(Formula formula) throws JsonProcessingException {
		if (formula.getCreatedBy() != null) {
			String createdByRefUuid = formula.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			formula.getCreatedBy().getRef().setName(user.getName());
		}
		if (formula.getAppInfo() != null) {
			for (int i = 0; i < formula.getAppInfo().size(); i++) {
				String appUuid = formula.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				formula.getAppInfo().get(i).getRef().setName(appName);
			}
		}
		String dependsOnRefUuid = formula.getDependsOn().getRef().getUuid();
		MetaType type = formula.getDependsOn().getRef().getType();
		if (type.toString().equals(MetaType.relation.toString())) {
			Relation relationDO = relationServiceImpl.findLatestByUuid(dependsOnRefUuid);
			String relationName = relationDO.getName();
			formula.getDependsOn().getRef().setName(relationName);
		} else if (type.toString().equals(MetaType.datapod.toString())) {
			Datapod dependsOnDatapod = datapodServiceImpl.findLatestByUuid(dependsOnRefUuid);
			String datapodName = dependsOnDatapod.getName();
			formula.getDependsOn().getRef().setName(datapodName);
		}
		else if (type.toString().equals(MetaType.dataset.toString())) {
			Dataset dependsOnDataset = datasetServiceImpl.findLatestByUuid(dependsOnRefUuid);
			String datasetName = dependsOnDataset.getName();
			formula.getDependsOn().getRef().setName(datasetName);
		}

		for (int i = 0; i < formula.getFormulaInfo().size(); i++) {
			MetaType formulaInfoRefType = formula.getFormulaInfo().get(i).getRef().getType();
			if (formulaInfoRefType.toString().equals(MetaType.datapod.toString())) {
				String formulaInfoRefUuid = formula.getFormulaInfo().get(i).getRef().getUuid();
				Integer formulaInfoAttributeId = formula.getFormulaInfo().get(i).getAttributeId();
				Datapod datapodDO = datapodServiceImpl.findLatestByUuid(formulaInfoRefUuid);
				String datapodName = datapodDO.getName();
				formula.getFormulaInfo().get(i).getRef().setName(datapodName);
				List<Attribute> attributeList = datapodDO.getAttributes();
				formula.getFormulaInfo().get(i).setAttributeName(attributeList.get(formulaInfoAttributeId).getName());
			}
			else if (formulaInfoRefType.toString().equals(MetaType.dataset.toString())) {
				String formulaInfoRefUuid = formula.getFormulaInfo().get(i).getRef().getUuid();
				Integer formulaInfoAttributeId = formula.getFormulaInfo().get(i).getAttributeId();
				Dataset datasetDO = datasetServiceImpl.findLatestByUuid(formulaInfoRefUuid);
				String datasetName = datasetDO.getName();
				formula.getFormulaInfo().get(i).getRef().setName(datasetName);
				List<AttributeSource> attributeList = datasetDO.getAttributeInfo();
				formula.getFormulaInfo().get(i).setAttributeName(attributeList.get(formulaInfoAttributeId).getAttrSourceName());
			}
			else if (formulaInfoRefType.toString().equals(MetaType.formula.toString())) {
				String formulaInfoRefUuid = formula.getFormulaInfo().get(i).getRef().getUuid();
				Formula formulaDO = findLatestByUuid(formulaInfoRefUuid);
				String formulaName = formulaDO.getName();
				formula.getFormulaInfo().get(i).getRef().setName(formulaName);
				}
			else if (formulaInfoRefType.toString().equals(MetaType.expression.toString())) {
				String formulaInfoRefUuid = formula.getFormulaInfo().get(i).getRef().getUuid();
				//Integer formulaInfoAttributeId = formula.getFormulaInfo().get(i).getAttributeId();
				Expression expressionDO = expressionServiceImpl.findLatestByUuid(formulaInfoRefUuid);
				String expressionName = expressionDO.getName();
				formula.getFormulaInfo().get(i).getRef().setName(expressionName);
				//List<AttributeSource> attributeList = datasetDO.getAttributeInfo();
				//formula.getFormulaInfo().get(i).setAttributeName(attributeList.get(formulaInfoAttributeId).getAttrSourceName());
			}
		}

		return formula;
	}
*/
	/********************** UNUSED **********************/
	/*public List<Formula> test(String param1) {
		return iFormulaDao.test(param1);
	}*/

	/********************** UNUSED **********************/
	/*public Formula findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iFormulaDao.findAllByUuid(appUuid, uuid);
	}*/

	/********************** UNUSED **********************/
	/*public Formula findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iFormulaDao.findOneByUuidAndVersion(appUuid, uuid, version);
		} 
		return iFormulaDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Formula getOneByUuidAndVersion(String uuid, String version) {

		return iFormulaDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Formula findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iFormulaDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iFormulaDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public List<Formula> findAllLatest() {
		{
			// String appUuid =
			// securityServiceImpl.getAppInfo().getRef().getUuid();;
			Aggregation formulaAggr = newAggregation(group("uuid").max("version").as("version"));
			AggregationResults<Formula> formulaResults = mongoTemplate.aggregate(formulaAggr, "formula", Formula.class);
			List<Formula> formulaList = formulaResults.getMappedResults();

			// Fetch the relation details for each id
			List<Formula> result = new ArrayList<Formula>();
			for (Formula s : formulaList) {
				Formula formulaLatest;
				String appUuid = (securityServiceImpl.getAppInfo() != null
						&& securityServiceImpl.getAppInfo().getRef() != null)
								? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
				if (appUuid != null) {
					// String appUuid =
					// securityServiceImpl.getAppInfo().getRef().getUuid();;
					formulaLatest = iFormulaDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
				} else {
					formulaLatest = iFormulaDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
				}
				// logger.debug("datapodLatest is " + datapodLatest.getName());
				if(formulaLatest != null)
				{
				result.add(formulaLatest);
				}
			}
			return result;
		}
	}*/

	/********************** UNUSED **********************/
	/*public List<Formula> findAllLatestActive() {
		Aggregation formulaAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Formula> formulaResults = mongoTemplate.aggregate(formulaAggr, "formula", Formula.class);
		List<Formula> formulaList = formulaResults.getMappedResults();

		// Fetch the formula details for each id
		List<Formula> result = new ArrayList<Formula>();
		for (Formula f : formulaList) {
			Formula formulaLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				formulaLatest = iFormulaDao.findOneByUuidAndVersion(appUuid, f.getId(), f.getVersion());
			} else {
				formulaLatest = iFormulaDao.findOneByUuidAndVersion(f.getId(), f.getVersion());
			}
			if(formulaLatest != null)
			{
			result.add(formulaLatest);
			}
		}
		return result;
	}*/

	/********************** UNUSED **********************/
	// Find formula by relation
	/*public List<Formula> findFormulaByRelation(String relationUUID) {
		String appUuid = (securityServiceImpl.getAppInfo() != null
				&& securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		Aggregation formulaAggr = newAggregation(match(Criteria.where("dependsOn.ref.uuid").is(relationUUID)),
				group("uuid").max("version").as("version"));
		AggregationResults<Formula> formulaResults = mongoTemplate.aggregate(formulaAggr, "formula", Formula.class);
		List<Formula> formulaList = formulaResults.getMappedResults();

		// Fetch relation details for each id
		List<Formula> result = new ArrayList<Formula>();
		for (Formula s : formulaList) {
			Formula formulaLatest = iFormulaDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			result.add(formulaLatest);
		}
		return result;
	}*/

	/********************** UNUSED **********************/
	/*public List<Formula> resolveName(List<Formula> formula) {
		List<Formula> formulaList = new ArrayList<Formula>();
		for (Formula form : formula) {
			String createdByRefUuid = form.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			form.getCreatedBy().getRef().setName(user.getName());
			formulaList.add(form);
		}
		return formulaList;
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public List<Formula> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iFormulaDao.findAllVersion(appUuid, uuid);
		} else
			return iFormulaDao.findAllVersion(uuid);
	}*/

	public List<Formula> findFormulaByType(String uuid) throws JsonProcessingException {
		/*
		 * String appUuid = (securityServiceImpl.getAppInfo() != null &&
		 * securityServiceImpl.getAppInfo().getRef() != null) ?
		 * securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		 */
		Aggregation formulaAggr = newAggregation(match(Criteria.where("dependsOn.ref.uuid").is(uuid)),
				group("uuid").max("version").as("version"));
		AggregationResults<Formula> formulaResults = mongoTemplate.aggregate(formulaAggr, "formula", Formula.class);
		List<Formula> formulaList = formulaResults.getMappedResults();

		// Fetch formula details for each id
		List<Formula> result = new ArrayList<Formula>();
		for (Formula s : formulaList) {
			// Formula formulaLatest = iFormulaDao.findOneByUuidAndVersion(appUuid,
			// s.getId(), s.getVersion());
			Formula formulaLatest = (Formula) commonServiceImpl.getOneByUuidAndVersion(s.getId(), s.getVersion(),
					MetaType.formula.toString());
			result.add(formulaLatest);
		}
		return result;
	}

	/********************** UNUSED **********************/
	/*public Formula getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iFormulaDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iFormulaDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Formula formula) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Formula formulaNew = new Formula();
		formulaNew.setName(formula.getName()+"_copy");
		formulaNew.setActive(formula.getActive());		
		formulaNew.setDesc(formula.getDesc());		
		formulaNew.setTags(formula.getTags());	
		formulaNew.setDependsOn(formula.getDependsOn());	
		formulaNew.setFormulaInfo(formula.getFormulaInfo());
		formulaNew.setFormulaType(formula.getFormulaType());		
		save(formulaNew);
		ref.setType(MetaType.formula);
		ref.setUuid(formulaNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> formulaList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity formula : formulaList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = formula.getId();
			String uuid = formula.getUuid();
			String version = formula.getVersion();
			String name = formula.getName();
			String desc = formula.getDesc();
			String published=formula.getPublished();
			MetaIdentifierHolder createdBy = formula.getCreatedBy();
			String createdOn = formula.getCreatedOn();
			String[] tags = formula.getTags();
			String active = formula.getActive();
			List<MetaIdentifierHolder> appInfo = formula.getAppInfo();
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

	public List<FormulaTypeHolder> findFormulaByType2(String uuid, String[] formulaType) throws JsonProcessingException {
		/*String appUuid = (securityServiceImpl.getAppInfo() != null
				&& securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
		List<Formula> formulaList = new ArrayList<>();
		
		List<Criteria> criteriaList = new ArrayList<>();
		
		
		try {
			if(uuid != null)
				criteriaList.add(Criteria.where("dependsOn.ref.uuid").is(uuid));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		if (formulaType.length == 0) {
			Criteria criteria = new Criteria(); 
			Criteria criteria2 = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
			Aggregation formulaAggr = newAggregation(match(criteria2),
					group("uuid").max("version").as("version"));
			AggregationResults<Formula> formulaResults = mongoTemplate.aggregate(formulaAggr, "formula", Formula.class);
			formulaList.addAll(formulaResults.getMappedResults());
		} else {
			for (String type : formulaType) {	
				Criteria criteria = new Criteria(); 
				criteriaList.add(Criteria.where("formulaType").is(type));	
				Criteria criteria2 = criteria.orOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
				Aggregation formulatype = newAggregation(
						match(criteria2),
						group("uuid").max("version").as("version"));
				AggregationResults<Formula> formulaResults = mongoTemplate.aggregate(formulatype, "formula",
						Formula.class);
				formulaList.addAll(formulaResults.getMappedResults());
			}
		}

		// Fetch formula details for each id
		List<FormulaTypeHolder> result = new ArrayList<FormulaTypeHolder>();
		for (Formula s : formulaList) {
			//Formula formulaLatest = iFormulaDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			Formula formulaLatest = (Formula) commonServiceImpl.getOneByUuidAndVersion(s.getId(), s.getVersion(), MetaType.formula.toString());
			FormulaTypeHolder formulaTypeHolder = new FormulaTypeHolder();
			MetaIdentifier formulaInfo = new MetaIdentifier(MetaType.formula,formulaLatest.getUuid(),formulaLatest.getVersion(),formulaLatest.getName());
			formulaTypeHolder.setFormulaType(formulaLatest.getFormulaType());			
			formulaTypeHolder.setRef(formulaInfo);
			result.add(formulaTypeHolder);
		}
		return result;
	}

}