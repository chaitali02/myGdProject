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

import com.inferyx.framework.domain.Import;
import com.inferyx.framework.domain.Load;


public interface ILoadDao extends MongoRepository<Load, String> {
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 , 'version' : ?2 }")
	public Load findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{'uuid' : ?0 , 'version' : ?1 }")
	public Load findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public Load findLatestByUuid(String appUuid,String uuid, Sort sort);
	
	@Query(value="{'uuid' : ?0 }")
	public Load findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Load> findAll(String appUuid);		
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Load findOneById(String appUuid, String id);
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1}")
	public List<Load> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<Load> findAllVersion(String uuid);
	
	@Query(value = "{}")
	public Load findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Load findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Load findAsOf(String appUuid, String uuid, String version, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public Load findOneById(String id);
	
	@Query(value="{}")
	public List<Load> findAll();

	@Query(value = "{'uuid':?0}")
	public Load findAllByUuid(String uuid);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public Load findAllByUuid(String appUuid, String uuid);
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Load save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Load save(String id);	
}
