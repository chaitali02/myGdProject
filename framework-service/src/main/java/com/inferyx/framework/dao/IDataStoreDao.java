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

import com.inferyx.framework.domain.DataStore;
import com.inferyx.framework.domain.Datasource;

public interface IDataStoreDao extends MongoRepository<DataStore, String> 
{
	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public DataStore findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public DataStore findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<DataStore> test(String param1);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid':?1}")
	public DataStore findAllByUuid(String appUuid,String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 }")
	public DataStore findLatestByUuid(String appUuid,String uuid, Sort sort);
	
	@Query(value="{ 'uuid' : ?0 }")
	public DataStore findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{'metaId.ref.uuid' : ?0 }")
	public DataStore findLatestByMeta(String uuid, Sort sort);
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1}")
	public List<DataStore> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<DataStore> findAllVersion(String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'metaId.ref.uuid' : ?1 , 'metaId.ref.version' : ?2 }")
	public DataStore findDataStoreByMeta(String appUuid,String uuid, String version);
	
	@Query(value="{'metaId.ref.uuid' : ?0 , 'metaId.ref.version' : ?1 }")
	public DataStore findDataStoreByMeta(String uuid, String version);
	
	@Query(value="{'metaId.ref.uuid' : ?0 }")
	public List<DataStore> findAllDataStoreByMeta(String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<DataStore> findAll(String appUuid);	
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public DataStore findOneById(String appUuid, String id);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'execId.ref.uuid' : ?1, 'execId.ref.version' : ?2}")
	public DataStore findDataStoreByExecUuidVersion(String appUuid, String uuid, String version);
	
	@Query(value="{'execId.ref.uuid' : ?0, 'execId.ref.version' : ?1}")
	public DataStore findDataStoreByExecUuidVersion(String uuid, String version);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public DataStore findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public DataStore findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public DataStore findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public DataStore findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public DataStore findOneById(String id);
	
	@Query(value="{'uuid':?0}")
	public DataStore findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<DataStore> findAll();	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public DataStore save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public DataStore save(String id);		

}
