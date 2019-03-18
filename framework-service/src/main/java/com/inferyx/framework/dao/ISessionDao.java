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

import com.inferyx.framework.domain.RuleGroupExec;
import com.inferyx.framework.domain.Session;


public interface ISessionDao extends MongoRepository<Session, String> 
{
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public Session findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public Session findOneByUuidAndVersion(String appUuid, String uuid, String version);
	
	@Query(value="{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<Session> test(String param1);
	
	@Query(value="{'uuid':?0}")
	public Session findAllByUuid(String uuid);
	
	@Query(value="{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public Session findAllByUuid(String appUuid, String uuid);
	
	@Query(value="{ 'uuid' : ?0 }")
	public Session findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public Session findLatestByUuid(String appUuid,String uuid, Sort sort);
	
	@Query(value="{'userInfo.ref.uuid' : ?0}")
	public Session findSessionByUser(String uuid,Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'userInfo.ref.uuid' : ?0}")
	public Session findSessionByUser(String appUuid, String uuid,Sort sort);
	
	@Query(value="{'status.0.stage' : ?0}")
	public Session findSessionByStatus(String status);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'status.0.stage' : ?1}")
	public Session findSessionByStatus(String appUuid, String status);
	
	/*//introducing for datastore implementation
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 , 'version' : ?2 }")
	public User findDataStoreByMeta(String appUuid,String uuid, String version);*/
	
	@Query(value="{'sessionId' : ?0}")
	public Session findSessionBySessionId(String SessionId);
	
	@Query(value = "{'uuid' : ?0}")
	public List<Session> findAllVersion(String uuid);
	
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<Session> findAllVersion(String appUuid, String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Session> findAll(String appUuid);	
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Session findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Session findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Session findOneById(String appUuid, String id);
	
	@Query(value = "{}")
	public Session findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Session findLatest(String appUuid, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{ '_id' : ?0 }")
	public Session findOneById(String id);
	
	@Query(value="{}")
	public List<Session> findAll();		
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Session save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Session save(String id);

	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
}
