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

import com.inferyx.framework.domain.ParamSet;
import com.inferyx.framework.domain.Privilege;

public interface IPrivilegeDao extends MongoRepository<Privilege, String>{

	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public Privilege findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public Privilege findOneByUuidAndVersion(String uuid, String version);
		
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public Privilege findAllByUuid(String appUuid,String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public Privilege findLatestByUuid(String appUuid,String uuid, Sort sort);
	
	@Query(value="{ 'uuid' : ?0 }")
	public Privilege findLatestByUuid(String uuid, Sort sort);
		
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<Privilege> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<Privilege> findAllVersion(String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Privilege> findAll(String appUuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Privilege findOneById(String appUuid, String id);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Privilege findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Privilege findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public Privilege findLatest(Sort sort);	
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Privilege findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public Privilege findOneById(String id);

	@Query(value = "{'uuid':?0}")
	public Privilege findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<Privilege> findAll();
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Privilege save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Privilege save(String id);	
	
}
