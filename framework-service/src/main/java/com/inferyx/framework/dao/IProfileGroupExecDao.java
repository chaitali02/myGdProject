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

import com.inferyx.framework.domain.ProfileGroup;
import com.inferyx.framework.domain.ProfileGroupExec;


public interface IProfileGroupExecDao extends MongoRepository<ProfileGroupExec, String>{
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid':?1}")
	public List<ProfileGroupExec> findAllByUuid(String appUuid,String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 , 'version' : ?2 }")
	public ProfileGroupExec findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public ProfileGroupExec findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 }")
	public ProfileGroupExec findLatestByUuid(String appUuid,String uuid, Sort sort);
	
	@Query(value="{ 'uuid' : ?0 }")
	public ProfileGroupExec findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<ProfileGroupExec> findAll(String appUuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public ProfileGroupExec findOneById(String appUuid, String id);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<ProfileGroupExec> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<ProfileGroupExec> findAllVersion(String uuid);
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'dependsOn.ref.uuid' : ?1,'dependsOn.ref.version' : ?2}")
	public List<ProfileGroupExec> findProfileGroupExecByDataQualGroup(String appUuid, String uuid, String version);
	
	@Query(value = "{'dependsOn.ref.uuid' : ?0,'dependsOn.ref.version' : ?1}")
	public List<ProfileGroupExec> findProfileGroupExecByDataQualGroup(String uuid, String version);
   
	@Query(value = "{'dependsOn.ref.uuid' : ?0,'dependsOn.ref.version' : ?1}")
	public List<ProfileGroupExec> finddqGroupExecBydqGroup(String uuid, String version);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public ProfileGroupExec findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public ProfileGroupExec findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{'dependsOn.ref.uuid' : ?0,'dependsOn.ref.version' : ?1}")
	public List<ProfileGroupExec> findProfileGroupExecByProfileGroup(String uuid, String version);
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'dependsOn.ref.uuid' : ?1,'dependsOn.ref.version' : ?2}")
	public List<ProfileGroupExec> findProfileGroupExecByProfileGroup(String appUuid, String uuid, String version);

	@Query(value = "{}")
	public ProfileGroupExec findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public ProfileGroupExec findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public ProfileGroupExec findOneById(String id);

	@Query(value = "{'uuid':?0}")
	public ProfileGroupExec findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<ProfileGroupExec> findAll();
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public ProfileGroupExec save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public ProfileGroupExec save(String id);	

	
}
