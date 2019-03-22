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

import com.inferyx.framework.domain.ReconGroupExec;



public interface IReconGroupExecDao extends MongoRepository<ReconGroupExec, String> {
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'uuid':?1}")
	public List<ReconGroupExec> findAllByUuid(String appUuid,String uuid);
	
	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public ReconGroupExec findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public ReconGroupExec findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'uuid' : ?1 }")
	public ReconGroupExec findLatestByUuid(String appUuid,String uuid, Sort sort);
	
	@Query(value="{ 'uuid' : ?0 }")
	public ReconGroupExec findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public List<ReconGroupExec> findAll(String appUuid);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  , '_id' : ?1 }")
	public ReconGroupExec findOneById(String appUuid, String id);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<ReconGroupExec> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<ReconGroupExec> findAllVersion(String uuid);
	
	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'dependsOn.ref.uuid' : ?1,'dependsOn.ref.version' : ?2}")
	public List<ReconGroupExec> findReconGroupExecByReconGroup(String appUuid, String uuid, String version);
	
	@Query(value = "{'dependsOn.ref.uuid' : ?0,'dependsOn.ref.version' : ?1}")
	public List<ReconGroupExec> findReconGroupExecByRuleGroup(String uuid, String version);
	
	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public ReconGroupExec findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public ReconGroupExec findLatest(Sort sort);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public ReconGroupExec findLatest(String appUuid, Sort sort);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1, 'version':{$lte:?2 }}")
	public ReconGroupExec findAsOf(String appUuid, String uuid, String version, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public ReconGroupExec findOneById(String id);

	@Query(value = "{'uuid':?0}")
	public ReconGroupExec findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<ReconGroupExec> findAll();	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public ReconGroupExec save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public ReconGroupExec save(String id);		

}
