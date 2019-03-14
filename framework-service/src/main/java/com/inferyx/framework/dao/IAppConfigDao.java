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
import com.inferyx.framework.domain.AppConfig;

public interface IAppConfigDao  extends MongoRepository<AppConfig, String>{
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public AppConfig findOneByUuidAndVersion(String uuid, String version);	
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 , 'version' : ?2 }")
	public AppConfig findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{'uuid':?0}")
	public AppConfig findAllByUuid(String uuid);
	
	@Query(value="{ 'uuid' : ?0 }")
	public AppConfig findLatestByUuid(String uuid, Sort sort);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public AppConfig findOneById(String appUuid, String id);

	@Query(value = "{ '_id' : ?0 }")
	public AppConfig findOneById(String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 }")
	public AppConfig findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid':?1}")
	public AppConfig findAllByUuid(String appUuid, String uuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1}")
	public List<AppConfig> findAllVersion(String appUuid, String uuid);
	
	@Query(value = "{'uuid' : ?0}")
	public List<AppConfig> findAllVersion(String uuid);
	
	@Query(value = "{}")
	public AppConfig findLatest(Sort sort);	
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public AppConfig findLatest(String appUuid, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<AppConfig> findAll(String appUuid);	

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public AppConfig findAsOf(String appUuid, String uuid, String version, Sort sort);
	
	@Query(value="{}")
	public List<AppConfig> findAll();
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public AppConfig save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public AppConfig save(String id);		
}
