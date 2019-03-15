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

import com.inferyx.framework.domain.Export;
import com.inferyx.framework.domain.Expression;


public interface IExpressionDao extends MongoRepository<Expression, String>{

	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public Expression findLatestByUuid(String appUuid, String uuid, Sort sort);
	
	@Query(value="{ 'uuid' : ?0 }")
	public Expression findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 , 'version' : ?2 }")
	public Expression findOneByUuidAndVersion(String appUuid, String uuid, String version);
	
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public Expression findOneByUuidAndVersion( String uuid, String version);
	
	@Query(value="{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<Expression> test(String param1);

	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public Expression findAllByUuid(String appUuid,String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Expression> findAll(String appUuid);	
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Expression findOneById(String appUuid, String id);
	
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<Expression> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<Expression> findAllVersion(String uuid);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Expression findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Expression findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public Expression findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Expression findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public Expression findOneById(String id);

	@Query(value = "{'uuid':?0}")
	public Expression findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<Expression> findAll();
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Expression save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Expression save(String id);		
	
}
