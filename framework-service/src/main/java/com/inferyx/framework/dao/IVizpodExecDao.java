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

import com.inferyx.framework.domain.VizExec;
import com.inferyx.framework.domain.Vizpod;

public interface IVizpodExecDao extends MongoRepository<VizExec, String> {

	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'vizpod.name' : ?1, 'vizpod.version' : ?2 }")
	public List<VizExec> findLatestVizpodExec(String appUuid,String vizpodUUID,String vizpodVersion);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'dependsOn.name' : ?1}")
	public List<VizExec> findVizpodExecVersion(String appUuid,String datapodName);

	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'stages.tasks.map.target.ref.uuid' : ?1}")
	public List<VizExec> findOneByDatapod(String appUuid,String datapodUUID);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'dependsOn.uuid' : ?1}")
	public List<VizExec> findOneByvizpod(String appUuid,String vizpodUUID);
	
	@Query(value="{$and: [ {'uuid' : ?0 },{'version' : ?1}]},{$project: [{'status' : 1}]}")
	public List<VizExec> test(String param1, String param2);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public VizExec findAllByUuid(String appUuid,String uuid);
	
	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public VizExec findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public VizExec findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public VizExec findLatestByUuid(String appUuid,String datapodUUID, Sort sort);
	
	@Query(value="{ 'uuid' : ?0 }")
	public VizExec findLatestByUuid(String uuid, Sort sort);
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1}")
	public List<VizExec> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<VizExec> findAllVersion(String uuid);

	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public VizExec findOneById(String appUuid, String id);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<VizExec> findAll(String appUuid);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public VizExec findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public VizExec findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public VizExec findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public VizExec findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public VizExec findOneById(String id);

	@Query(value = "{'uuid':?0}")
	public VizExec findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<VizExec> findAll();	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public VizExec save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public VizExec save(String id);		


}
