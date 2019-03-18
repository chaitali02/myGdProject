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

import com.inferyx.framework.domain.ParamList;
import com.inferyx.framework.domain.ParamSet;

public interface IParamSetDao  extends MongoRepository<ParamSet, String>{
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public ParamSet findOneByUuidAndVersion(String uuid, String version);	
	
	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public ParamSet findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{'uuid':?0}")
	public ParamSet findAllByUuid(String uuid);
	
	@Query(value="{ 'uuid' : ?0 }")
	public ParamSet findLatestByUuid(String uuid, Sort sort);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public ParamSet findOneById(String appUuid, String id);

	@Query(value = "{ '_id' : ?0 }")
	public ParamSet findOneById(String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 }")
	public ParamSet findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid':?1}")
	public ParamSet findAllByUuid(String appUuid, String uuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<ParamSet> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'dependsOn.ref.uuid': ?1}")
	public List<ParamSet> findLatestByDependsOn(String appUuid, String dependsOnuuid, Sort sort);

	@Query(value = "{'dependsOn.ref.uuid': ?0}")
	public List<ParamSet> findLatestByDependsOn(String dependsOnuuid, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'dependsOn.ref.uuid': ?1,'dependsOn.ref.version': ?2}")
	public List<ParamSet> findOneByDependsOn(String appUuid, String dependsOnuuid, String dependsOnVersion);

	@Query(value = "{'dependsOn.ref.uuid': ?0,'dependsOn.ref.version': ?1}")
	public List<ParamSet> findOneByDependsOn(String dependsOnuuid, String dependsOnVersion);
	
	@Query(value = "{'uuid' : ?0}")
	public List<ParamSet> findAllVersion(String uuid);
	
	@Query(value = "{}")
	public ParamSet findLatest(Sort sort);	
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public ParamSet findLatest(String appUuid, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<ParamSet> findAll(String appUuid);	

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public ParamSet findAsOf(String appUuid, String uuid, String version, Sort sort);
	
	@Query(value="{}")
	public List<ParamSet> findAll();
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public ParamSet save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public ParamSet save(String id);		
}
