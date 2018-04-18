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
import com.inferyx.framework.dao.IExpressionDao;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.Attribute;
import com.inferyx.framework.domain.BaseEntity;
import com.inferyx.framework.domain.Datapod;
import com.inferyx.framework.domain.DataSet;
import com.inferyx.framework.domain.Expression;
import com.inferyx.framework.domain.Formula;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.User;
import com.inferyx.framework.register.GraphRegister;

@Service
public class ExpressionServiceImpl {

	@Autowired
	GraphRegister<?> registerGraph;
	/*@Autowired
	JavaSparkContext javaSparkContext;*/
	@Autowired
	IExpressionDao iExpressionDao;
	@Autowired
	RelationServiceImpl relationServiceImpl;
	@Autowired
	DatapodServiceImpl datapodServiceImpl;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	@Autowired
	ApplicationServiceImpl applicationServiceImpl;
	@Autowired
	DatasetServiceImpl datasetServiceImpl;
	@Autowired
	FormulaServiceImpl formulaServiceImpl;
	@Autowired 
	RegisterService registerService;
	@Autowired
	CommonServiceImpl<?> commonServiceImpl;
	
	static final Logger logger = Logger.getLogger(ExpressionServiceImpl.class);

	/********************** UNUSED **********************/
	/*public Expression findLatest() {
		return resolveName(iExpressionDao.findLatest(new Sort(Sort.Direction.DESC, "version")));
	}*/

	/********************** UNUSED **********************/
	/*public Expression findOneById(String id) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iExpressionDao.findOneById(appUuid, id);
		} else
			return iExpressionDao.findOne(id);
	}*/

	/********************** UNUSED **********************/
	/*public Expression findOneByUuid(String uid) {
		return iExpressionDao.findOne(uid);
	}*/

	/********************** UNUSED **********************/
	/*public Expression save(Expression expression) throws Exception {
		MetaIdentifierHolder meta = securityServiceImpl.getAppInfo();
		List<MetaIdentifierHolder> metaIdentifierHolderList = new ArrayList<MetaIdentifierHolder>();
		metaIdentifierHolderList.add(meta);
		expression.setAppInfo(metaIdentifierHolderList);
		expression.setBaseEntity();
		Expression expressionSave=iExpressionDao.save(expression);
		registerGraph.updateGraph((Object) expressionSave, MetaType.expression);
		return expressionSave;
	}*/

	/********************** UNUSED **********************/
	/*public List<Expression> findAll() {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iExpressionDao.findAll();
		}
		return iExpressionDao.findAll(appUuid);
	}*/

	/********************** UNUSED **********************/
	/*public Expression resolveName(Expression expression) throws JsonProcessingException {
		
		if (expression == null) {
			return null;
		}
		
		if (expression.getCreatedBy() != null) {
			String createdByRefUuid = expression.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			expression.getCreatedBy().getRef().setName(user.getName());
		}
		if (expression.getAppInfo() != null) {
			for (int i = 0; i < expression.getAppInfo().size(); i++) {
				String appUuid = expression.getAppInfo().get(i).getRef().getUuid();
				Application application = (Application) commonServiceImpl.getLatestByUuid(appUuid, MetaType.application.toString());
				String appName = application.getName();
				expression.getAppInfo().get(i).getRef().setName(appName);
			}
		}
	//	for (int i = 0; i < expression.getExpressionInfo().size(); i++) {
			if (expression.getMatch().getRef().getType() == MetaType.formula) {
				String metUuid = expression.getMatch().getRef().getUuid();
				Formula formula = formulaServiceImpl.findLatestByUuid(metUuid);
				String formulaName = formula.getName();
				expression.getMatch().getRef().setName(formulaName);
			}
			if (expression.getNoMatch().getRef().getType() == MetaType.formula) {
				String metUuid = expression.getNoMatch().getRef().getUuid();
				Formula formula = formulaServiceImpl.findLatestByUuid(metUuid);
				String formulaName = formula.getName();
				expression.getNoMatch().getRef().setName(formulaName);
			}
			for (int i = 0; i < expression.getExpressionInfo().size(); i++) {
			for (int j = 0; j < expression.getExpressionInfo().get(i).getOperand().size(); j++) {
				MetaType operandRefType = expression.getExpressionInfo().get(i).getOperand().get(j).getRef().getType();
				String operandRefUuid = expression.getExpressionInfo().get(i).getOperand().get(j).getRef().getUuid();

				if (operandRefType.toString().equals(MetaType.datapod.toString())) {
					Integer operandAttributeId = expression.getExpressionInfo().get(i).getOperand().get(j)
							.getAttributeId();
					Datapod datapodDO = datapodServiceImpl.findLatestByUuid(operandRefUuid);
					String datapodName = datapodDO.getName();
					expression.getExpressionInfo().get(i).getOperand().get(j).getRef().setName(datapodName);
					List<Attribute> attributeList = datapodDO.getAttributes();
					expression.getExpressionInfo().get(i).getOperand().get(j)
							.setAttributeName(attributeList.get(operandAttributeId).getName());
				}
			}
			}
		//}
		String RefUuid = expression.getDependsOn().getRef().getUuid();
		MetaType type = expression.getDependsOn().getRef().getType();
		if (type.toString().equals(MetaType.relation.toString())) {
			Relation relationDO = relationServiceImpl.findLatestByUuid(RefUuid);
			String relationName = relationDO.getName();
			expression.getDependsOn().getRef().setName(relationName);
		} else if (type.toString().equals(MetaType.datapod.toString())) {
			Datapod dependsOnDatapod = datapodServiceImpl.findLatestByUuid(RefUuid);
			String datapodName = dependsOnDatapod.getName();
			expression.getDependsOn().getRef().setName(datapodName);
		} else if (type.toString().equals(MetaType.dataset.toString())) {
			Dataset dependsOnDataset = datasetServiceImpl.findLatestByUuid(RefUuid);
			String datasetName = dependsOnDataset.getName();
			expression.getDependsOn().getRef().setName(datasetName);
		}

		return expression;
	}
*/
	/*public Expression update(Expression expression) throws IOException {
		expression.exportBaseProperty();
		Expression expressionDet=iExpressionDao.save(expression);
		registerService.createGraph();
		return expressionDet;
	}*/

	/********************** UNUSED **********************/
	/*public boolean isExists(String id) {
		return iExpressionDao.exists(id);
	}*/

	/********************** UNUSED **********************/
	/*public void delete(String Id) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		Expression expression = iExpressionDao.findOneById(appUuid, Id);
		expression.setActive("N");
		iExpressionDao.save(expression);
//		String ID = expression.getId();
//		iExpressionDao.delete(ID);
//		expression.exportBaseProperty();
	}*/

	/********************** UNUSED **********************/
	/*public List<Expression> test(String param1) {
		return iExpressionDao.test(param1);
	}*/

	/********************** UNUSED **********************/
	/*public Expression findAllByUuid(String uuid) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iExpressionDao.findAllByUuid(appUuid, uuid);
	}*/

	/********************** UNUSED **********************/
	/*public Expression findOneByUuidAndVersion(String uuid, String version) {
		String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();
		return iExpressionDao.findOneByUuidAndVersion(appUuid, uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Expression getOneByUuidAndVersion(String uuid, String version) {
		return iExpressionDao.findOneByUuidAndVersion(uuid, version);
	}*/

	/********************** UNUSED **********************/
	/*public Expression findLatestByUuid(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid == null) {
			return iExpressionDao.findLatestByUuid(uuid, new Sort(Sort.Direction.DESC, "version"));
		}
		return iExpressionDao.findLatestByUuid(appUuid, uuid, new Sort(Sort.Direction.DESC, "version"));
	}*/

	/********************** UNUSED **********************/
	/*public List<Expression> findAllLatest() {
		// String appUuid =
		// securityServiceImpl.getAppInfo().getRef().getUuid();;
		Aggregation expressionAggr = newAggregation(group("uuid").max("version").as("version"));
		AggregationResults<Expression> expressionResults = mongoTemplate.aggregate(expressionAggr, "expression",
				Expression.class);
		List<Expression> expressionList = expressionResults.getMappedResults();

		// Fetch the datapod details for each id
		List<Expression> result = new ArrayList<Expression>();
		for (Expression s : expressionList) {
			Expression expressionLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				// String appUuid =
				// securityServiceImpl.getAppInfo().getRef().getUuid();;
				expressionLatest = iExpressionDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			} else {
				expressionLatest = iExpressionDao.findOneByUuidAndVersion(s.getId(), s.getVersion());
			}
			// logger.debug("datapodLatest is " + datapodLatest.getName());
			if(expressionLatest != null)
			{
			result.add(expressionLatest);
			}
		}
		return result;
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public List<Expression> findAllLatestActive() {
		Aggregation expressionAggr = newAggregation(match(Criteria.where("active").is("Y")),
				match(Criteria.where("name").ne(null)), group("uuid").max("version").as("version"));
		AggregationResults<Expression> expressionResults = mongoTemplate.aggregate(expressionAggr, "expression",
				Expression.class);
		List<Expression> expressionList = expressionResults.getMappedResults();

		// Fetch the expression details for each id
		List<Expression> result = new ArrayList<Expression>();
		for (Expression e : expressionList) {
			Expression expressionLatest;
			String appUuid = (securityServiceImpl.getAppInfo() != null
					&& securityServiceImpl.getAppInfo().getRef() != null)
							? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
			if (appUuid != null) {
				expressionLatest = iExpressionDao.findOneByUuidAndVersion(appUuid, e.getId(), e.getVersion());
			} else {
				expressionLatest = iExpressionDao.findOneByUuidAndVersion(e.getId(), e.getVersion());
			}
			if(expressionLatest != null)
			{
			result.add(expressionLatest);
			}
		}
		return result;
	}*/

	public List<Expression> findExpressionByRelation(String relationUUID) throws JsonProcessingException{
		/*String appUuid = securityServiceImpl.getAppInfo().getRef().getUuid();*/
		Aggregation expressionAggr = newAggregation(match(Criteria.where("dependsOn.ref.uuid").is(relationUUID)),
				group("uuid").max("version").as("version"));
		AggregationResults<Expression> expressionResults = mongoTemplate.aggregate(expressionAggr, "expression",
				Expression.class);
		List<Expression> expressionList = expressionResults.getMappedResults();

		// Fetch the expression details for each id
		List<Expression> result = new ArrayList<Expression>();
		for (Expression s : expressionList) {
			//Expression expressionLatest = iExpressionDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			Expression expressionLatest = (Expression) commonServiceImpl.getOneByUuidAndVersion(s.getId(), s.getVersion(), MetaType.expression.toString());
			result.add(expressionLatest);
		}
		return result;
	}

	/********************** UNUSED **********************/
	/*public List<Expression> resolveName(List<Expression> expression) {
		List<Expression> expressionList = new ArrayList<Expression>();
		for (Expression exp : expression) {
			String createdByRefUuid = exp.getCreatedBy().getRef().getUuid();
			User user = userServiceImpl.findLatestByUuid(createdByRefUuid);
			exp.getCreatedBy().getRef().setName(user.getName());
			expressionList.add(exp);
		}
		return expressionList;
	}*/

	/********************** UNUSED **********************/
	/*public List<Expression> findAllByVersion(String uuid) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;
		if (appUuid != null) {
			return iExpressionDao.findAllVersion(appUuid, uuid);
		} else
			return iExpressionDao.findAllVersion(uuid);
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public Expression getAsOf(String uuid, String asOf) {
		String appUuid = (securityServiceImpl.getAppInfo() != null && securityServiceImpl.getAppInfo().getRef() != null)
				? securityServiceImpl.getAppInfo().getRef().getUuid() : null;				
		if (appUuid != null) {
			return iExpressionDao.findAsOf(appUuid, uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
		}
		else
			return iExpressionDao.findAsOf(uuid, asOf,new Sort(Sort.Direction.DESC, "version"));
	}*/

	public List<Expression> findExpressionByType(String uuid) throws JsonProcessingException {
		/*String appUuid = (securityServiceImpl.getAppInfo() != null
				&& securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
		Aggregation expressionAggr = newAggregation(match(Criteria.where("dependsOn.ref.uuid").is(uuid)),
				group("uuid").max("version").as("version"));
		AggregationResults<Expression> expressionResults = mongoTemplate.aggregate(expressionAggr, "expression", Expression.class);
		List<Expression> expressionList = expressionResults.getMappedResults();

		// Fetch Expression details for each id
		List<Expression> result = new ArrayList<Expression>();
		for (Expression s : expressionList) {
			//Expression expressionLatest = iExpressionDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			Expression expressionLatest = (Expression) commonServiceImpl.getOneByUuidAndVersion(s.getId(), s.getVersion(), MetaType.expression.toString());
			result.add(expressionLatest);
		}
		return result;
	}

	/********************** UNUSED **********************/
	/*public MetaIdentifierHolder saveAs(Expression expression) throws Exception {
		MetaIdentifierHolder refMeta = new MetaIdentifierHolder();
		MetaIdentifier ref = new MetaIdentifier();		
		Expression expNew = new Expression();
		expNew.setName(expression.getName()+"_copy");
		expNew.setActive(expression.getActive());		
		expNew.setDesc(expression.getDesc());		
		expNew.setTags(expression.getTags());	
		expNew.setDependsOn(expression.getDependsOn());		
		expNew.setCondition(expression.getCondition());
		expNew.setPublished(expression.getPublished());
		expNew.setNoMatch(expression.getNoMatch());
		expNew.setMatch(expression.getMatch());
		expNew.setExpressionInfo(expression.getExpressionInfo());
		save(expNew);
		ref.setType(MetaType.expression);
		ref.setUuid(expNew.getUuid());
		refMeta.setRef(ref);
		return refMeta;
	}*/

	/********************** UNUSED 
	 * @throws JsonProcessingException **********************/
	/*public List<BaseEntity> findList(List<? extends BaseEntity> expressionList) {
		List<BaseEntity> baseEntityList = new ArrayList<BaseEntity>();
		for(BaseEntity expression : expressionList)
		{
			BaseEntity baseEntity = new BaseEntity();
			String id = expression.getId();
			String uuid = expression.getUuid();
			String version = expression.getVersion();
			String name = expression.getName();
			String desc = expression.getDesc();
			String published=expression.getPublished();
			MetaIdentifierHolder createdBy = expression.getCreatedBy();
			String createdOn = expression.getCreatedOn();
			String[] tags = expression.getTags();
			String active = expression.getActive();
			List<MetaIdentifierHolder> appInfo = expression.getAppInfo();
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

	public List<MetaIdentifierHolder> findExpressionByType2(String uuid) throws JsonProcessingException {
		/*String appUuid = (securityServiceImpl.getAppInfo() != null
				&& securityServiceImpl.getAppInfo().getRef() != null)
						? securityServiceImpl.getAppInfo().getRef().getUuid() : null;*/
		Aggregation expressionAggr = newAggregation(match(Criteria.where("dependsOn.ref.uuid").is(uuid)),
				group("uuid").max("version").as("version"));
		AggregationResults<Expression> expressionResults = mongoTemplate.aggregate(expressionAggr, "expression", Expression.class);
		List<Expression> expressionList = expressionResults.getMappedResults();

		// Fetch Expression details for each id
		List<MetaIdentifierHolder> result = new ArrayList<MetaIdentifierHolder>();
		for (Expression s : expressionList) {
			//Expression expressionLatest = iExpressionDao.findOneByUuidAndVersion(appUuid, s.getId(), s.getVersion());
			Expression expressionLatest = (Expression) commonServiceImpl.getOneByUuidAndVersion(s.getId(), s.getVersion(), MetaType.expression.toString());
			MetaIdentifierHolder expressionMeta = new MetaIdentifierHolder();
			MetaIdentifier expressionInfo = new MetaIdentifier(MetaType.expression,expressionLatest.getUuid(),expressionLatest.getVersion(),expressionLatest.getName());
			expressionMeta.setRef(expressionInfo);
			result.add(expressionMeta);
		}
		return result;
	}
}
