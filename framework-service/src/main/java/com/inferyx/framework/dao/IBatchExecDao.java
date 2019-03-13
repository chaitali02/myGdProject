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

import com.inferyx.framework.domain.BatchExec;

public interface IBatchExecDao extends MongoRepository<BatchExec, String> {

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 , 'version' : ?2 }")
	public BatchExec findOneByUuidAndVersion(String appUuid, String uuid, String version);

	@Query(value = "{ 'uuid' : ?0 , 'version' : ?1 }")
	public BatchExec findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<BatchExec> test(String param1);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<BatchExec> findAll(String appUuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public List<BatchExec> findAllByUuid(String appUuid, String uuid);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public BatchExec findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{ 'uuid' : ?0 }")
	public BatchExec findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<BatchExec> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<BatchExec> findAllVersion(String uuid);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public BatchExec findOneById(String appUuid, String id);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public BatchExec findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public BatchExec findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public BatchExec findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public BatchExec findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public BatchExec findOneById(String id);
	
	@Query(value="{'uuid':?0}")
	public BatchExec findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<BatchExec> findAll();		
		
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public BatchExec save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public BatchExec save(String id);
}
