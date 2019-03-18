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

import com.inferyx.framework.domain.Map;
import com.inferyx.framework.domain.MapExec;

public interface IMapExecDao extends MongoRepository<MapExec, String> {

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public MapExec findAllByUuid(String appUuid, String uuid);

	@Query(value = "{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public MapExec findOneByUuidAndVersion(String appUuid, String uuid, String version);

	@Query(value = "{ 'uuid' : ?0 , 'version' : ?1 }")
	public MapExec findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public MapExec findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{ 'uuid' : ?0 }")
	public MapExec findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'dependsOn.ref.uuid' : ?1}")
	public List<MapExec> findOneByrule(String appUuid, String ruleUUID);

	@Query(value = "{'dependsOn.ref.uuid' : ?0}")
	public List<MapExec> findOneByrule(String ruleUUID);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public MapExec findOneById(String appUuid, String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<MapExec> findAll(String appUuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<MapExec> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<MapExec> findAllVersion(String uuid);
	
	@Query(value = "{}")
	public MapExec findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public MapExec findLatest(String appUuid, Sort sort);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public MapExec findAsOf(String appUuid, String uuid, String version, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public MapExec findOneById(String id);

	@Query(value = "{'uuid':?0}")
	public MapExec findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<MapExec> findAll();
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public MapExec save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public MapExec save(String id);		

}
