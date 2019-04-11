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

import com.inferyx.framework.domain.Privilege;
import com.inferyx.framework.domain.Profile;

public interface IProfileDao extends MongoRepository<Profile, String>{

	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public Profile findOneByUuidAndVersion(String appUuid, String profileUUID, String version);
	
	@Query(value="{'uuid' : ?0 , 'version' : ?1 }")
	public Profile findOneByUuidAndVersion(String datapodUUID, String version);
	
	@Query(value="{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<Profile> test(String param1);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public List<Profile> findAll(String appUuid);	
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid':?1}")
	public Profile findAllByUuid(String appUuid, String uuid);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] , 'uuid' : ?1 }")
	public Profile findLatestByUuid(String appUuid, String datapodUUID, Sort sort);
	
	@Query(value="{ 'uuid' : ?0 }")
	public Profile findLatestByUuid(String profileUuid, Sort sort);
	
	@Query(value="{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<Profile> findAllVersion(String appUuid,String uuid);
	
	@Query(value = "{'uuid' : ?0}")
	public List<Profile> findAllVersion(String uuid);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  , '_id' : ?1 }")
	public Profile findOneById(String appUuid, String id);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1, 'version':{$lte:?2 }}")
	public Profile findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Profile findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public Profile findLatest(Sort sort);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public Profile findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public Profile findOneById(String id);

	@Query(value = "{'uuid':?0}")
	public Profile findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<Profile> findAll();
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Profile save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Profile save(String id);
	
}
