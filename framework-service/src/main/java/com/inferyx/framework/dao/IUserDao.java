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

import com.inferyx.framework.domain.Session;
import com.inferyx.framework.domain.User;


public interface IUserDao extends MongoRepository<User, String> 
{
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public User findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 , 'version' : ?2 }")
	public User findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<User> test(String param1);
	
	@Query(value="{'uuid':?0}")
	public User findAllByUuid(String uuid);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public User findAllByUuid(String appUuid, String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 }")
	public User findLatestByUuid(String appUuid, String datapodUUID, Sort sort);

	@Query(value="{ 'uuid' : ?0 }")
	public User findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{ 'name' : ?0 }")
	public User findLatestByUsername(String userName, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'name' : ?1 }")
	public User findLatestByUsername(String appUuid, String userName, Sort sort);
	
	@Query(value="{'uuid' : ?0}")
	public List<User> findAllVersion(String uuid);
	
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<User> findAllVersion(String appUuid, String uuid);

	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<User> findAll(String appUuid);	
	
	@Query(value="{ 'name' : ?0 }")
	public User findUserByName(String userName);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'name' : ?1 }")
	public User findUserByName(String appUuid, String userName);
	
	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public User findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public User findAsOf(String appUuid,String uuid, String version, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public User findOneById(String appUuid, String id);
	
	@Query(value = "{}")
	public User findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public User findLatest(String appUuid, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	

	@Query(value = "{ '_id' : ?0 }")
	public User findOneById(String id);
	
	@Query(value="{}")
	public List<User> findAll();	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public User save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public User save(String id);		

	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

}