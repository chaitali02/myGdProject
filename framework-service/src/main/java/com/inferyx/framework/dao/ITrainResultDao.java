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

import com.inferyx.framework.domain.TrainResult;

/**
 * @author Ganesh
 *
 */
public interface ITrainResultDao extends MongoRepository<TrainResult, String> {
	@Query(value ="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public TrainResult findOneByUuidAndVersion(String appUuid, String uuid, String version);

	@Query(value = "{'uuid' : ?0 , 'version' : ?1 }")
	public TrainResult findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'name' : ?1 }")
	public TrainResult findOneByFileName(String appUuid, String fileName);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public TrainResult findOneById(String appUuid, String id);

	@Query(value = "{ '_id' : ?0 }")
	public TrainResult findOneById(String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'id' : ?1, 'attributes.name' : ?2 ,'attributes.type' : ?3 ,'attributes.desc' : ?4}")
	public List<TrainResult> findOneForDelete(String appUuid, String id, String name, String type, String desc);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 }")
	public TrainResult findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{'uuid' : ?0 }")
	public TrainResult findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<TrainResult> test(String param1);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<TrainResult> findAll(String appUuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid':?1}")
	public TrainResult findAllByUuid(String appUuid, String uuid);

	@Query(value = "{'uuid':?0}")
	public TrainResult findAllByUuid(String uuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public List<TrainResult> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0 }")
	public List<TrainResult> findAllVersion(String uuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public TrainResult findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public TrainResult findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public TrainResult findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public TrainResult findLatest(String appUuid, Sort sort);
	
	@Query(value="{}")
	public List<TrainResult> findAll();		
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public TrainResult save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public TrainResult save(String id);	
}
