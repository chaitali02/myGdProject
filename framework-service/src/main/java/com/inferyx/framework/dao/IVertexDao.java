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

import com.inferyx.framework.domain.User;
import com.inferyx.framework.domain.Vertex;

/**
 * @author joy
 *
 */
public interface IVertexDao extends MongoRepository<Vertex, String> {
	
	@Query(value = "{ 'uuid' : ?0 , 'version' : ?1 }")
	public Vertex findOneByUuidAndVersion(String uuid, String version);

	/*@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 , 'version' : ?2 }")
	public Vertex findOneByUuidAndVersion(String appUuid, String uuid, String version);*/

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<Vertex> test(String param1);

	@Query(value = "{'uuid':?0}")
	public Vertex findAllByUuid(String uuid);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid':?1}")
	public Vertex findAllByUuid(String appUuid, String uuid);

	@Query(value = "{ 'uuid' : ?0 }")
	public Vertex findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] , 'uuid' : ?1 }")
	public Vertex findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{ 'userInfo.ref.uuid' : ?0 }")
	public List<Vertex> findVertexByUser(String uuid, Sort sort);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1}")
	public List<Vertex> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<Vertex> findAllVersion(String uuid);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public List<Vertex> findAll(String appUuid);	
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  , '_id' : ?1 }")
	public Vertex findOneById(String appUuid, String id);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1, 'version':{$lte:?2 }}")
	public Vertex findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Vertex findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public Vertex findOneById(String id);
	
	
	@Query(value = "{ 'uuid' : ?0 }")
	public Vertex findOneByUuid(String id);
	
	@Query(value="{}")
	public List<Vertex> findAll();	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Vertex save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Vertex save(String id);		

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public void delete(String appUuid);
	
	
	@Query(value = "{'_id' : ?0}")
	public void delete1(String id);
	
	@Query(value = "{}")
	public Vertex findLatest(Sort sort);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public Vertex findLatest(String appUuid, Sort sort);
	
	@Query("{ uuid: { $in: ?0 } }")
    List<Vertex> findAllByUuidContaining(List<String> uuids);
	
	@Query("{ uuid: { $in: ?0 } ,nodeType: { $in: ?1 }}")
    List<Vertex> findAllByUuidAndnodeTypeContaining(List<String> uuids,List<String> nodeType);
}
