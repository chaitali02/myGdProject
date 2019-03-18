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

import com.inferyx.framework.domain.TrainExec;

public interface ITrainExecDao extends MongoRepository<TrainExec, String> {
	@Query(value = "{ 'uuid' : ?0 , 'version' : ?1 }")
	public TrainExec findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public TrainExec findOneByUuidAndVersion(String appUuid, String uuid, String version);

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<TrainExec> test(String param1);

	@Query(value = "{'uuid':?0}")
	public TrainExec findAllByUuid(String uuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public TrainExec findAllByUuid(String appUuid, String uuid);

	@Query(value = "{}")
	public TrainExec findLatest(Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public TrainExec findLatest(String appUuid, Sort sort);

	@Query(value = "{ 'uuid' : ?0 }")
	public TrainExec findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 }")
	public TrainExec findLatestByUuid(String appUuid, String datapodUUID, Sort sort);

	@Query(value = "{ 'userInfo.ref.uuid' : ?0 }")
	public List<TrainExec> findActivityByUser(String uuid, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1}")
	public List<TrainExec> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<TrainExec> findAllVersion(String uuid);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<TrainExec> findAll(String appUuid);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public TrainExec findOneById(String appUuid, String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public TrainExec findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public TrainExec findAsOf(String uuid, String version, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);

	@Query(value = "{ '_id' : ?0 }")
	public TrainExec findOneById(String id);

	@Query(value = "{}")
	public List<TrainExec> findAll();

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public TrainExec save(String appUuid, String id);

	@Query(value = "{'_id' : ?0}")
	public TrainExec save(String id);

	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
}
