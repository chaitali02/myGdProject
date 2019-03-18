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

import com.inferyx.framework.domain.RuleGroup;
import com.inferyx.framework.domain.RuleGroupExec;


public interface IRuleGroupExecDao extends MongoRepository<RuleGroupExec, String> {
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid':?1}")
	public List<RuleGroupExec> findAllByUuid(String appUuid,String uuid);
	
	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public RuleGroupExec findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public RuleGroupExec findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 }")
	public RuleGroupExec findLatestByUuid(String appUuid,String uuid, Sort sort);
	
	@Query(value="{ 'uuid' : ?0 }")
	public RuleGroupExec findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<RuleGroupExec> findAll(String appUuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public RuleGroupExec findOneById(String appUuid, String id);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<RuleGroupExec> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<RuleGroupExec> findAllVersion(String uuid);
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'dependsOn.ref.uuid' : ?1,'dependsOn.ref.version' : ?2}")
	public List<RuleGroupExec> findRuleGroupExecByRuleGroup(String appUuid, String uuid, String version);
	
	@Query(value = "{'dependsOn.ref.uuid' : ?0,'dependsOn.ref.version' : ?1}")
	public List<RuleGroupExec> findRuleGroupExecByRuleGroup(String uuid, String version);
	
	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public RuleGroupExec findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public RuleGroupExec findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public RuleGroupExec findLatest(String appUuid, Sort sort);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public RuleGroupExec findAsOf(String appUuid, String uuid, String version, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public RuleGroupExec findOneById(String id);

	@Query(value = "{'uuid':?0}")
	public RuleGroupExec findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<RuleGroupExec> findAll();	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public RuleGroupExec save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public RuleGroupExec save(String id);		

}
