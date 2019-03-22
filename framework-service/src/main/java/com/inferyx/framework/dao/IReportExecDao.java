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
import com.inferyx.framework.domain.ReportExec;

public interface IReportExecDao  extends MongoRepository<ReportExec, String>{
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public ReportExec findOneByUuidAndVersion(String uuid, String version);
	
	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public ReportExec findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{'uuid':?0}")
	public ReportExec findAllByUuid(String uuid);
	
	@Query(value="{ 'uuid' : ?0 }")
	public ReportExec findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  , '_id' : ?1 }")
	public ReportExec findOneById(String appUuid, String id);

	@Query(value = "{ '_id' : ?0 }")
	public ReportExec findOneById(String id);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'uuid' : ?1 }")
	public ReportExec findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'uuid':?1}")
	public ReportExec findAllByUuid(String appUuid, String uuid);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
    @Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<ReportExec> findAllVersion(String appUuid, String uuid);
	
	@Query(value = "{'uuid' : ?0}")
	public List<ReportExec> findAllVersion(String uuid);
	
	@Query(value = "{}")
	public ReportExec findLatest(Sort sort);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public ReportExec findLatest(String appUuid, Sort sort);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1, 'version':{$lte:?2 }}")
	public ReportExec findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public List<ReportExec> findAll(String appUuid);
	
	@Query(value="{}")
	public List<ReportExec> findAll();		
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public ReportExec save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public ReportExec save(String id);

}
