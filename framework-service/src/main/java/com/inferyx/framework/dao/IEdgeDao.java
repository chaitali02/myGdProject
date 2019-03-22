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
import com.inferyx.framework.domain.GraphMetaIdentifier;
import com.inferyx.framework.domain.GraphMetaIdentifierHolder;
import com.inferyx.framework.domain.Vertex;

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

	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'uuid' : ?1 }")
	public Edge findLatestByUuid(String appUuid,String datapodUUID, Sort sort);
	
	@Query(value = "{ 'userInfo.ref.uuid' : ?0 }")
	public List<Edge> findEdgeByUser(String uuid, Sort sort);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1}")
	public List<Edge> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<Edge> findAllVersion(String uuid);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public List<Edge> findAll(String appUuid);	
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  , '_id' : ?1 }")
	public Edge findOneById(String appUuid, String id);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1, 'version':{$lte:?2 }}")
	public Edge findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Edge findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public Edge findOneById(String id);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'uuid':?1}")
	public Edge findAllByUuid(String appUuid,String uuid);
	
	@Query(value="{}")
	public List<Edge> findAll();
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Edge save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Edge save(String id);		

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public void deleteAll(String appUuid);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{}")
	public Edge findLatest(Sort sort);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public Edge findLatest(String appUuid, Sort sort);	
	
	@Query(value="{'src' : ?0}")
	public List<Edge> findAllBySrc(String uuid);
	
	@Query(value="{'src' : ?0,'dst' : ?1}")
	public Edge findOneBySrcAndDst(String src,String dst);
	
	@Query(value="{'src' : ?0,'dst' : ?1,'relationType' : ?2}")
	public Edge findOneBySrcAndDstAndRelationType(String src,String dst,String relationtype);
	
	@Query(value="{'src' :{ $in: ?0 },'dst' : { $in: ?1 },'relationType' : { $in: ?2 }}")
	public List<Edge> findBySrcAndDstAndRelationType(List<String> src,List<String> dst,List<String> nodeType);
	
	
	@Query(value="{'dst' : ?0,'src' : ?1}")
	public Edge findOneByDstAndSrc(String dst,String src);
	
	@Query(value="{'dst' : ?0,'src' : ?1,'relationType' : ?2}")
	public Edge findOneByDstAndSrcAndRelationType(String dst,String src,String relationtype);
	
	@Query(value="{'dst' : ?0}")
	public List<Edge> findAllByDst(String uuid);
	
	@Query(value="{'srcInfo.ref.uuid' : ?0},{'dstInfo.ref.uuid' : ?0}")
	public List<Edge> findEdgeByRef(String src,String dst);
	
}
