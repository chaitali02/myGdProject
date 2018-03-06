package com.inferyx.framework.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.mongodb.repository.Query;

import com.inferyx.framework.domain.LoadExec;
import com.inferyx.framework.domain.Log;

public interface ILogDao extends MongoRepository<Log, String> {
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 , 'version' : ?2 }")
	public Log findOneByUuidAndVersion(String appUuid, String uuid, String version);

	@Query(value = "{'uuid' : ?0 , 'version' : ?1 }")
	public Log findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'name' : ?1 }")
	public Log findOneByFileName(String appUuid, String fileName);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Log findOneById(String appUuid, String id);

	@Query(value = "{ '_id' : ?0 }")
	public Log findOneById(String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'id' : ?1, 'attributes.name' : ?2 ,'attributes.type' : ?3 ,'attributes.desc' : ?4}")
	public List<Log> findOneForDelete(String appUuid, String id, String name, String type, String desc);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 }")
	public Log findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{'uuid' : ?0 }")
	public Log findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<Log> test(String param1);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Log> findAll(String appUuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid':?1}")
	public Log findAllByUuid(String appUuid, String uuid);

	@Query(value = "{'uuid':?0}")
	public Log findAllByUuid(String uuid);
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public List<Log> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0 }")
	public List<Log> findAllVersion(String uuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Log findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Log findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public Log findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Log findLatest(String appUuid, Sort sort);
	
	@Query(value="{}")
	public List<Log> findAll();		
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Log save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Log save(String id);	



}
