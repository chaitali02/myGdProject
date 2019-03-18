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
import com.inferyx.framework.domain.Application;

public interface IApplicationDao extends MongoRepository<Application, String> {
	
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public Application findOneByUuidAndVersion(String uuid, String version);	
	
	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public Application findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{'uuid':?0}")
	public Application findAllByUuid(String uuid);
	
	@Query(value="{ 'uuid' : ?0 }")
	public Application findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 }")
	public Application findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{'uuid' : ?0}")
	public List<Application> findAllVersion(String uuid);
	
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<Application> findAllVersion(String appUuid, String uuid);
	
	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Application findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public Application findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Application findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Application findAsOf(String appUuid, String uuid, String version, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Application findOneById(String appUuid, String id);


	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Application> findAll(String appUuid);	
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public Application findAllByUuid(String appUuid, String uuid);
	
	@Query(value = "{ '_id' : ?0 }")
	public Application findOneById(String id);
	
	@Query(value="{}")
	public List<Application> findAll();			
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Application save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Application save(String id);

}
