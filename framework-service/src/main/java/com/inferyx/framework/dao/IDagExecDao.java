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
import com.inferyx.framework.domain.DagExec;

public interface IDagExecDao extends MongoRepository<DagExec, String>{
	
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'dag.name' : ?1, 'dag.version' : ?2 }")
	public List<DagExec> findLatestDagExec(String appUuid,String dagUUID,String dagVersion);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'dependsOn.name' : ?1}")
	public List<DagExec> findDagExecVersion(String appUuid, String datapodName);

	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'stages.tasks.map.target.ref.uuid' : ?1}")
	public List<DagExec> findOneByDatapod(String appUuid,String datapodUUID);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public List<DagExec> findAll(String appUuid);	
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'dependsOn.uuid' : ?1}")
	public List<DagExec> findAllByDag(String appUuid,String dagUUID);
	
	@Query(value="{$and: [ {'uuid' : ?0 },{'version' : ?1}]},{$project: [{'status' : 1}]}")
	public List<DagExec> test(String param1, String param2);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid':?1}")
	public DagExec findAllByUuid(String appUuid,String uuid);
	
	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public DagExec findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{'uuid' : ?0 , 'version' : ?1 }")
	public DagExec findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1 }")
	public DagExec findLatestByUuid(String appUuid,String datapodUUID, Sort sort);
	
	@Query(value="{ 'uuid' : ?0 }")
	public DagExec findLatestByUuid(String datapodUUID, Sort sort);
	
	@Query(value="{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<DagExec> findAllVersion(String appUuid,String uuid);
	
	@Query(value = "{'uuid' : ?0}")
	public List<DagExec> findAllVersion(String uuid);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  , '_id' : ?1 }")
	public DagExec findOneById(String appUuid, String id);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1, 'version':{$lte:?2 }}")
	public DagExec findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public DagExec findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public DagExec findLatest(Sort sort);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public DagExec findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public DagExec findOneById(String id);
	
	@Query(value="{'uuid':?0}")
	public DagExec findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<DagExec> findAll();			
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public DagExec save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public DagExec save(String id);
}
