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

import com.inferyx.framework.domain.DataQual;
import com.inferyx.framework.domain.DataQualExec;


public interface IDataQualExecDao extends MongoRepository<DataQualExec, String>{
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public DataQualExec findAllByUuid(String appUuid,String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 , 'version' : ?2 }")
	public DataQualExec findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public DataQualExec findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public DataQualExec findLatestByUuid(String appUuid,String uuid, Sort sort);
	
	@Query(value="{ 'uuid' : ?0 }")
	public DataQualExec findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'dependsOn.ref.uuid' : ?1}")
	public List<DataQualExec> findOneByDataQual(String appUuid,String DataQualUUID);
	
	@Query(value="{'dependsOn.ref.uuid' : ?0}")
	public List<DataQualExec> findOneByDataQual(String DataQualUUID);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public DataQualExec findOneById(String appUuid, String id);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<DataQualExec> findAll(String appUuid);	
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<DataQualExec> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<DataQualExec> findAllVersion(String uuid);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public DataQualExec findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public DataQualExec findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public DataQualExec findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public DataQualExec findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public DataQualExec findOneById(String id);
	
	@Query(value="{'uuid':?0}")
	public DataQualExec findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<DataQualExec> findAll();	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public DataQualExec save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public DataQualExec save(String id);			

	
}
