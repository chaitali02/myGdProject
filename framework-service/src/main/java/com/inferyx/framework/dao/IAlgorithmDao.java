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

import com.inferyx.framework.domain.Activity;
import com.inferyx.framework.domain.Algorithm;

public interface IAlgorithmDao  extends MongoRepository<Algorithm, String>{
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public Algorithm findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public Algorithm findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{'uuid':?0}")
	public Algorithm findAllByUuid(String uuid);
	
	@Query(value="{ 'uuid' : ?0 }")
	public Algorithm findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Algorithm findOneById(String appUuid, String id);

	@Query(value = "{ '_id' : ?0 }")
	public Algorithm findOneById(String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 }")
	public Algorithm findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid':?1}")
	public Algorithm findAllByUuid(String appUuid, String uuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<Algorithm> findAllVersion(String appUuid, String uuid);
	
	@Query(value = "{'uuid' : ?0}")
	public List<Algorithm> findAllVersion(String uuid);
	
	@Query(value = "{}")
	public Algorithm findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Algorithm findLatest(String appUuid, Sort sort);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Algorithm findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Algorithm> findAll(String appUuid);
	
	@Query(value="{}")
	public List<Algorithm> findAll();		
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Algorithm save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Algorithm save(String id);

}
