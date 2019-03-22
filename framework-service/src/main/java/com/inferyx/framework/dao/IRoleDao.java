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

import com.inferyx.framework.domain.Relation;
import com.inferyx.framework.domain.Role;

public interface IRoleDao extends MongoRepository<Role, String>{
	
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public Role findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public Role findOneByUuidAndVersion(String appUuid, String uuid, String version);
		
	@Query(value="{'uuid':?0}")
	public Role findAllByUuid(String uuid);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid':?1}")
	public Role findAllByUuid(String appUuid, String uuid);
	
	@Query(value="{ 'uuid' : ?0 }")
	public Role findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1 }")
	public Role findLatestByUuid(String appUuid, String uuid, Sort sort);
		
	@Query(value = "{'uuid' : ?0}")
	public List<Role> findAllVersion(String uuid);
	
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<Role> findAllVersion(String appUuid, String uuid);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public List<Role> findAll(String appUuid);	
	
	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Role findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1, 'version':{$lte:?2 }}")
	public Role findAsOf(String appUuid, String uuid, String version, Sort sort);
	
	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  , '_id' : ?1 }")
	public Role findOneById(String appUuid, String id);
	
	@Query(value = "{}")
	public Role findLatest(Sort sort);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public Role findLatest(String appUuid, Sort sort);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{ '_id' : ?0 }")
	public Role findOneById(String id);
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Role save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Role save(String id);	

	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

	
}