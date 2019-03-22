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

import com.inferyx.framework.domain.Report;

public interface IReportDao  extends MongoRepository<Report, String>{
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public Report findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public Report findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{'uuid':?0}")
	public Report findAllByUuid(String uuid);
	
	@Query(value="{ 'uuid' : ?0 }")
	public Report findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  , '_id' : ?1 }")
	public Report findOneById(String appUuid, String id);

	@Query(value = "{ '_id' : ?0 }")
	public Report findOneById(String id);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'uuid' : ?1 }")
	public Report findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'uuid':?1}")
	public Report findAllByUuid(String appUuid, String uuid);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
    @Query(value ="{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<Report> findAllVersion(String appUuid, String uuid);
	
	@Query(value = "{'uuid' : ?0}")
	public List<Report> findAllVersion(String uuid);
	
	@Query(value = "{}")
	public Report findLatest(Sort sort);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public Report findLatest(String appUuid, Sort sort);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1, 'version':{$lte:?2 }}")
	public Report findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public List<Report> findAll(String appUuid);
	
	@Query(value="{}")
	public List<Report> findAll();		
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Report save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Report save(String id);

}
