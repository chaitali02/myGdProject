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
import com.inferyx.framework.domain.Dashboard;

public interface IDashboardDao extends MongoRepository<Dashboard, String> {
	
	@Query(value="{}")
	public List<Dashboard> findAll();

	@Query(value="{ 'uuid' : ?0 }")
	public List<Dashboard> findAllByUuid(String uuid);

	@Query(value="{'uuid' : ?0 , 'version' : ?1 }")
	public Dashboard findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public Dashboard findOneByUuidAndVersion(String appUuid,String uuid, String version);

	@Query(value="{ 'uuid' : ?0 }")
	public Dashboard findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public Dashboard findLatestByUuid(String appUuid,String uuid, Sort sort);
	
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<Dashboard> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<Dashboard> findAllVersion(String uuid);
	
	@Query(value="{ 'name' : ?0 }")
	public Dashboard findLatestByName(String name, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'name' : ?1 }")
	public Dashboard findLatestByName(String appUuid, String name, Sort sort);
	
	@Query(value = "{}")
	public Dashboard findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Dashboard findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Dashboard findAsOf(String appUuid, String uuid, String version, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Dashboard findOneById(String appUuid, String id);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Dashboard> findAll(String appUuid);		
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public Dashboard findAllByUuid(String appUuid, String uuid);
	
	@Query(value = "{ '_id' : ?0 }")
	public Dashboard findOneById(String id);	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Dashboard save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Dashboard save(String id);

}
