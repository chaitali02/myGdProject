/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.inferyx.framework.domain.UploadExec;

public interface IUploadDao extends MongoRepository<UploadExec, String> {
	@Query(value = "{ 'uuid' : ?0 , 'version' : ?1 }")
	public UploadExec findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 , 'version' : ?2 }")
	public UploadExec findOneByUuidAndVersion(String appUuid, String uuid, String version);

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<UploadExec> test(String param1);

	@Query(value = "{'uuid':?0}")
	public UploadExec findAllByUuid(String uuid);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public UploadExec findAllByUuid(String appUuid, String uuid);

	@Query(value = "{}")
	public UploadExec findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public UploadExec findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ 'uuid' : ?0 }")
	public UploadExec findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 }")
	public UploadExec findLatestByUuid(String appUuid, String datapodUUID, Sort sort);

	@Query(value = "{ 'userInfo.ref.uuid' : ?0 }")
	public List<UploadExec> findActivityByUser(String uuid, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1}")
	public List<UploadExec> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<UploadExec> findAllVersion(String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<UploadExec> findAll(String appUuid);	
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public UploadExec findOneById(String appUuid, String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public UploadExec findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public UploadExec findAsOf(String uuid, String version, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	

	@Query(value = "{ '_id' : ?0 }")
	public UploadExec findOneById(String id);
	
	@Query(value="{}")
	public List<UploadExec> findAll();	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public UploadExec save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public UploadExec save(String id);

	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

}
