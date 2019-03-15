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

import com.inferyx.framework.domain.ProfileGroupExec;
import com.inferyx.framework.domain.Relation;


public interface IRelationDao extends MongoRepository<Relation, String>{

	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'dependsOn.ref.uuid':?1}")
    public List<Relation> findRelationByDatapod(String appUuid,String datapodUUID);

    @Query(value="{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
    public List<Relation> test(String param1);
    
    @Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 , 'version' : ?2 }")
    public Relation findOneByUuidAndVersion(String appUuid,String uuid, String version);
    
    @Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
    public Relation findOneByUuidAndVersion(String uuid, String version);

    @Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
    public Relation findLatestByUuid(String appUuid,String uuid, Sort sort);
    
    @Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
    public Relation findAsOf(String appUuid,String uuid, String version,Sort sort);
    
    @Query(value="{ 'uuid' : ?0, 'version':{$lte:?1 }}")
    public Relation findAsOf(String uuid,  String version, Sort sort);
    
    @Query(value="{'uuid' : ?0 }")
    public Relation findLatestByUuid(String uuid, Sort sort);

    @Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
    public Relation findAllByUuid(String appUuid,String uuid);

    @Query(value="{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
    public List<Relation> findAllVersion(String appUuid,String uuid);
    
    @Query(value="{'uuid' : ?0}")
    public List<Relation> findAllVersion(String uuid);
    
    @Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Relation findOneById(String appUuid, String id);
    
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Relation> findAll(String appUuid);	
	
	@Query(value="{}")
	public List<Relation> findAll();	
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{}")
	public Relation findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Relation findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public Relation findOneById(String id);

	@Query(value = "{'uuid':?0}")
	public Relation findAllByUuid(String uuid);
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Relation save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Relation save(String id);	
}
