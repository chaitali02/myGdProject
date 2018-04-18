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

import com.inferyx.framework.domain.Dimension;
import com.inferyx.framework.domain.Edge;

public interface IEdgeDao extends MongoRepository<Edge, String> {

	@Query(value = "{ 'uuid' : ?0 , 'version' : ?1 }")
	public Edge findOneByUuidAndVersion(String uuid, String version);

	/*@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 , 'version' : ?2 }")
	public Edge findOneByUuidAndVersion(String appUuid, String uuid, String version);*/

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<Edge> test(String param1);

	@Query(value = "{'uuid':?0}")
	public Edge findAllByUuid(String uuid);
	
	/*@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public Edge findAllByUuid(String appUuid, String uuid);*/

	@Query(value = "{ 'uuid' : ?0 }")
	public Edge findLatestByUuid(String uuid, Sort sort);
	
	/*@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 }")
	public Edge findLatestByUuid(String appUuid, String datapodUUID, Sort sort);*/

	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 }")
	public Edge findLatestByUuid(String appUuid,String datapodUUID, Sort sort);
	
	@Query(value = "{ 'userInfo.ref.uuid' : ?0 }")
	public List<Edge> findEdgeByUser(String uuid, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1}")
	public List<Edge> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<Edge> findAllVersion(String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Edge> findAll(String appUuid);	
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Edge findOneById(String appUuid, String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Edge findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Edge findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public Edge findOneById(String id);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid':?1}")
	public Edge findAllByUuid(String appUuid,String uuid);
	
	@Query(value="{}")
	public List<Edge> findAll();
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Edge save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Edge save(String id);		

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{}")
	public Edge findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Edge findLatest(String appUuid, Sort sort);	
	
	@Query(value="{'src' : ?0}")
	public List<Edge> findAllBySrc(String uuid);
	
	@Query(value="{'dst' : ?0}")
	public List<Edge> findAllByDst(String uuid);
}
