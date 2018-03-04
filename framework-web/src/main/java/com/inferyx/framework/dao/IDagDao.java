/*******************************************************************************
 * Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
 *
 * This unpublished material is proprietary to GridEdge Consulting LLC.
 * The methods and techniques described herein are considered  trade 
 * secrets and/or confidential. Reproduction or distribution, in whole or 
 * in part, is forbidden.
 *
 * Written by Yogesh Palrecha <ypalrecha@gridedge.com>
 *******************************************************************************/
package com.inferyx.framework.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.inferyx.framework.domain.Activity;
import com.inferyx.framework.domain.Dag;

public interface IDagDao extends MongoRepository<Dag, String> {

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 , 'version' : ?2 }")
	public Dag findOneByUuidAndVersion(String appUuid, String uuid, String version);

	@Query(value = "{ 'uuid' : ?0 , 'version' : ?1 }")
	public Dag findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<Dag> test(String param1);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Dag> findAll(String appUuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public List<Dag> findAllByUuid(String appUuid, String uuid);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public Dag findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{ 'uuid' : ?0 }")
	public Dag findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1}")
	public List<Dag> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<Dag> findAllVersion(String uuid);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Dag findOneById(String appUuid, String id);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Dag findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Dag findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public Dag findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Dag findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public Dag findOneById(String id);
	
	@Query(value="{'uuid':?0}")
	public Dag findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<Dag> findAll();		
		
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Dag save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Dag save(String id);
}
