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

public interface IActivityDao extends MongoRepository<Activity, String> {
	@Query(value = "{ 'uuid' : ?0 , 'version' : ?1 }")
	public Activity findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 , 'version' : ?2 }")
	public Activity findOneByUuidAndVersion(String appUuid, String uuid, String version);

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<Activity> test(String param1);

	@Query(value = "{'uuid':?0}")
	public Activity findAllByUuid(String uuid);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public Activity findAllByUuid(String appUuid, String uuid);

	@Query(value = "{}")
	public Activity findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Activity findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ 'uuid' : ?0 }")
	public Activity findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 }")
	public Activity findLatestByUuid(String appUuid, String datapodUUID, Sort sort);

	@Query(value = "{ 'userInfo.ref.uuid' : ?0 }")
	public List<Activity> findActivityByUser(String uuid, Sort sort);

	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<Activity> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<Activity> findAllVersion(String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Activity> findAll(String appUuid);	
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Activity findOneById(String appUuid, String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Activity findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Activity findAsOf(String uuid, String version, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	

	@Query(value = "{ '_id' : ?0 }")
	public Activity findOneById(String id);
	
	@Query(value="{}")
	public List<Activity> findAll();	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Activity save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Activity save(String id);

	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

}
