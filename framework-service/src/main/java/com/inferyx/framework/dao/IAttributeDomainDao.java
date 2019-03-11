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

import com.inferyx.framework.domain.AttributeDomain;


/**
 * @author Ganesh
 *
 */
public interface IAttributeDomainDao extends MongoRepository<AttributeDomain, String> {
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 , 'version' : ?2 }")
	public AttributeDomain findOneByUuidAndVersion(String appUuid, String uuid, String version);

	@Query(value = "{'uuid' : ?0 , 'version' : ?1 }")
	public AttributeDomain findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'name' : ?1 }")
	public AttributeDomain findOneByFileName(String appUuid, String fileName);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public AttributeDomain findOneById(String appUuid, String id);

	@Query(value = "{ '_id' : ?0 }")
	public AttributeDomain findOneById(String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'id' : ?1, 'attributes.name' : ?2 ,'attributes.type' : ?3 ,'attributes.desc' : ?4}")
	public List<AttributeDomain> findOneForDelete(String appUuid, String id, String name, String type, String desc);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 }")
	public AttributeDomain findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{'uuid' : ?0 }")
	public AttributeDomain findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<AttributeDomain> test(String param1);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<AttributeDomain> findAll(String appUuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid':?1}")
	public AttributeDomain findAllByUuid(String appUuid, String uuid);

	@Query(value = "{'uuid':?0}")
	public AttributeDomain findAllByUuid(String uuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public List<AttributeDomain> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0 }")
	public List<AttributeDomain> findAllVersion(String uuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public AttributeDomain findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public AttributeDomain findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public AttributeDomain findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public AttributeDomain findLatest(String appUuid, Sort sort);
	
	@Query(value="{}")
	public List<AttributeDomain> findAll();		
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public AttributeDomain save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public AttributeDomain save(String id);	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,$or : [  {'name':{$regex : ?1,$options : \"i\"}} , {'desc':{$regex : ?1,$options : \"i\"}}, {'attributes':{$elemMatch:{'desc':{$regex : ?1,$options : \"i\"}}}},{'attributes':{$elemMatch:{'name':{$regex : ?1,$options : \"i\"}}}}] }")
	public List<AttributeDomain> findAll(String appUuid,String searchStr);
}
