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
import org.springframework.data.mongodb.repository.Query;


import com.inferyx.framework.domain.Datapod;

public interface IDatapodLoaderDao 
{
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] , 'uuid' : ?1 , 'version' : ?2 }")
	public Datapod findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{ 'uuid' : ?0 }")
	public Datapod findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'uuid' : ?1 }")
	public Datapod findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public Datapod findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1, 'version':{$lte:?2 }}")
	public Datapod findAsOf(String appUuid, String uuid, String version, Sort sort);	
	
	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1}")
	public List<Datapod> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<Datapod> findAllVersion(String uuid);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  , '_id' : ?1 }")
	public Datapod findOneById(String appUuid, String id);

	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public List<Datapod> findAll(String appUuid);		
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid':?1}")
	public Datapod findAllByUuid(String appUuid, String uuid);
	
	@Query(value = "{ '_id' : ?0 }")
	public Datapod findOneById(String id);
	
	@Query(value="{'uuid':?0}")
	public Datapod findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<Datapod> findAll();		
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Datapod save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Datapod save(String id);		

	@Query(value = "{}")
	public Datapod findLatest(Sort sort);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public Datapod findLatest(String appUuid, Sort sort);
}
