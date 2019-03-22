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

import com.inferyx.framework.domain.Ingest;

/**
 * @author Ganesh
 *
 */
public interface IIngestDao extends MongoRepository<Ingest, String> {
	@Query(value = "{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public Ingest findOneByUuidAndVersion(String appUuid, String uuid, String version);

	@Query(value = "{'uuid' : ?0 , 'version' : ?1 }")
	public Ingest findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'name' : ?1 }")
	public Ingest findOneByFileName(String appUuid, String fileName);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  , '_id' : ?1 }")
	public Ingest findOneById(String appUuid, String id);

	@Query(value = "{ '_id' : ?0 }")
	public Ingest findOneById(String id);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'id' : ?1, 'attributes.name' : ?2 ,'attributes.type' : ?3 ,'attributes.desc' : ?4}")
	public List<Ingest> findOneForDelete(String appUuid, String id, String name, String type, String desc);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'uuid' : ?1 }")
	public Ingest findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{'uuid' : ?0 }")
	public Ingest findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<Ingest> test(String param1);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public List<Ingest> findAll(String appUuid);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'uuid':?1}")
	public Ingest findAllByUuid(String appUuid, String uuid);

	@Query(value = "{'uuid':?0}")
	public Ingest findAllByUuid(String uuid);

	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<Ingest> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0 }")
	public List<Ingest> findAllVersion(String uuid);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1, 'version':{$lte:?2 }}")
	public Ingest findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Ingest findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public Ingest findLatest(Sort sort);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public Ingest findLatest(String appUuid, Sort sort);
	
	@Query(value="{}")
	public List<Ingest> findAll();		
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Ingest save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Ingest save(String id);
}
