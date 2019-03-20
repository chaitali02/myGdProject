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

import com.inferyx.framework.domain.Datapod;

public interface IDatapodDao extends MongoRepository<Datapod, String> {

	/*
	 * @Query(
	 * value="{ 'appInfo':{$elemMatch: { 'ref.uuid': {$eq:?0}}} ,'uuid' : ?1 , 'version' : ?2 }"
	 * ) public Datapod findOneByUuidAndVersion(String appUuid, String uuid,
	 * String version);
	 */

	@Query(value = "{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public Datapod findOneByUuidAndVersion(String appUuid, String uuid, String version);

	@Query(value = "{'uuid' : ?0 , 'version' : ?1 }")
	public Datapod findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'name' : ?1 }")
	public Datapod findOneByFileName(String appUuid, String fileName);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Datapod findOneById(String appUuid, String id);

	@Query(value = "{ '_id' : ?0 }")
	public Datapod findOneById(String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'id' : ?1, 'attributes.name' : ?2 ,'attributes.type' : ?3 ,'attributes.desc' : ?4}")
	public List<Datapod> findOneForDelete(String appUuid, String id, String name, String type, String desc);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 }")
	public Datapod findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{'uuid' : ?0 }")
	public Datapod findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<Datapod> test(String param1);

	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]}")
	public List<Datapod> findAll(String appUuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid':?1}")
	public Datapod findAllByUuid(String appUuid, String uuid);

	@Query(value = "{'uuid':?0}")
	public Datapod findAllByUuid(String uuid);

	
	/*
	 * @Query(
	 * value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'targetDatapod.name' : ?1}"
	 * ) public List<Datapod> findAllVersion(String appUuid, String
	 * datapodName);
	 */

	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<Datapod> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0 }")
	public List<Datapod> findAllVersion(String uuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Datapod findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Datapod findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public Datapod findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Datapod findLatest(String appUuid, Sort sort);
	
	@Query(value="{}")
	public List<Datapod> findAll();		
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Datapod save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Datapod save(String id);	
	
	@Query(value = "{$and: [{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }]} ,{$or : [  {'name':{$regex : ?1,$options : \"i\"}} , {'desc':{$regex : ?1,$options : \"i\"}}, {'attributes':{$elemMatch:{'desc':{$regex : ?1,$options : \"i\"}}}},{'attributes':{$elemMatch:{'name':{$regex : ?1,$options : \"i\"}}}}] }]}")
	public List<Datapod> findAll(String appUuid,String searchStr);	

//	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
//	public Datapod count(String appUuid);

}
