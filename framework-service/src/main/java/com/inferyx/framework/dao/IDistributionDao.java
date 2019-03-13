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

import com.inferyx.framework.domain.Distribution;

/**
 * @author Ganesh
 *
 */
public interface IDistributionDao extends MongoRepository<Distribution, String> {
	@Query(value = "{ 'uuid' : ?0 , 'version' : ?1 }")
	public Distribution findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 , 'version' : ?2 }")
	public Distribution findOneByUuidAndVersion(String appUuid, String uuid, String version);

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<Distribution> test(String param1);

	@Query(value = "{'uuid':?0}")
	public Distribution findAllByUuid(String uuid);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public Distribution findAllByUuid(String appUuid, String uuid);

	@Query(value = "{}")
	public Distribution findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Distribution findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ 'uuid' : ?0 }")
	public Distribution findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 }")
	public Distribution findLatestByUuid(String appUuid, String datapodUUID, Sort sort);

	@Query(value = "{ 'userInfo.ref.uuid' : ?0 }")
	public List<Distribution> findActivityByUser(String uuid, Sort sort);

	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<Distribution> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<Distribution> findAllVersion(String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Distribution> findAll(String appUuid);	
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Distribution findOneById(String appUuid, String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Distribution findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Distribution findAsOf(String uuid, String version, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);	

	@Query(value = "{ '_id' : ?0 }")
	public Distribution findOneById(String id);
	
	@Query(value="{}")
	public List<Distribution> findAll();	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Distribution save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Distribution save(String id);

	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

}
