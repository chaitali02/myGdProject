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
package com.inferyx.framework.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.inferyx.framework.domain.Rule;
import com.inferyx.framework.domain.RuleExec;


public interface IRuleExecDao extends MongoRepository<RuleExec, String>{
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public RuleExec findAllByUuid(String appUuid,String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 , 'version' : ?2 }")
	public RuleExec findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public RuleExec findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public RuleExec findLatestByUuid(String appUuid,String uuid, Sort sort);
	
	@Query(value="{ 'uuid' : ?0 }")
	public RuleExec findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'dependsOn.ref.uuid' : ?1}")
	public List<RuleExec> findOneByrule(String appUuid,String ruleUUID);
	
	@Query(value="{'dependsOn.ref.uuid' : ?0}")
	public List<RuleExec> findOneByrule(String ruleUUID);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public RuleExec findOneById(String appUuid, String id);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<RuleExec> findAll(String appUuid);	
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<RuleExec> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<RuleExec> findAllVersion(String uuid);
	
	@Query(value = "{}")
	public RuleExec findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public RuleExec findLatest(String appUuid, Sort sort);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public RuleExec findAsOf(String appUuid, String uuid, String version, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public RuleExec findOneById(String id);

	@Query(value = "{'uuid':?0}")
	public RuleExec findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<RuleExec> findAll();	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public RuleExec save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public RuleExec save(String id);		
	
}
