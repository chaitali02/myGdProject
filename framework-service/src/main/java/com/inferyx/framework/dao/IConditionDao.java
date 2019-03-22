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

import com.inferyx.framework.domain.Activity;
import com.inferyx.framework.domain.Condition;

public interface IConditionDao  extends MongoRepository<Condition, String> {
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?0 , 'version' : ?1 }")
	public Condition findOneByUuidAndVersion(String appUuid, String datapodUUID, String version);
	
	@Query(value="{'uuid' : ?0 , 'version' : ?1 }")
	public Condition findOneByUuidAndVersion(String datapodUUID, String version);
	
	@Query(value="{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<Condition> test(String param1);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public List<Condition> findAll(String appUuid);	
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid':?1}")
	public Condition findAllByUuid(String appUuid, String uuid);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] , 'uuid' : ?1 }")
	public Condition findLatestByUuid(String appUuid, String datapodUUID, Sort sort);
	
	@Query(value="{ 'uuid' : ?0 }")
	public Condition findLatestByUuid(String datapodUUID, Sort sort);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1}")
	public List<Condition> findAllVersion(String appUuid,String uuid);
	
	@Query(value = "{'uuid' : ?0}")
	public List<Condition> findAllVersion(String uuid);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] , '_id' : ?1 }")
	public Condition findOneById(String appUuid, String id);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1, 'version':{$lte:?2 }}")
	public Condition findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Condition findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public Condition findOneById(String id);
	
	@Query(value="{'uuid':?0}")
	public Condition findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<Condition> findAll();
		
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Condition save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Condition save(String id);
	
	@Query(value = "{}")
	public Condition findLatest(Sort sort);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public Condition findLatest(String appUuid, Sort sort);
}
