/**
 * 
 */
package com.inferyx.framework.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.inferyx.framework.domain.DQRecExec;

/**
 * @author gridedge01
 *
 */
public interface IDQRecExecDao extends MongoRepository<DQRecExec, String> {
	@Query(value = "{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public DQRecExec findOneByUuidAndVersion(String appUuid, String uuid, String version);

	@Query(value = "{'uuid' : ?0 , 'version' : ?1 }")
	public DQRecExec findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'name' : ?1 }")
	public DQRecExec findOneByFileName(String appUuid, String fileName);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  , '_id' : ?1 }")
	public DQRecExec findOneById(String appUuid, String id);

	@Query(value = "{ '_id' : ?0 }")
	public DQRecExec findOneById(String id);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'id' : ?1, 'attributes.name' : ?2 ,'attributes.type' : ?3 ,'attributes.desc' : ?4}")
	public List<DQRecExec> findOneForDelete(String appUuid, String id, String name, String type, String desc);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'uuid' : ?1 }")
	public DQRecExec findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{'uuid' : ?0 }")
	public DQRecExec findLatestByUuid(String uuid, Sort sort);

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<DQRecExec> test(String param1);

	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]}")
	public List<DQRecExec> findAll(String appUuid);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'uuid':?1}")
	public DQRecExec findAllByUuid(String appUuid, String uuid);

	@Query(value = "{'uuid':?0}")
	public DQRecExec findAllByUuid(String uuid);

	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<DQRecExec> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0 }")
	public List<DQRecExec> findAllVersion(String uuid);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public DQRecExec findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public DQRecExec findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public DQRecExec findLatest(Sort sort);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public DQRecExec findLatest(String appUuid, Sort sort);
	
	@Query(value="{}")
	public List<DQRecExec> findAll();		
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public DQRecExec save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public DQRecExec save(String id);	
	
	@Query(value = "{$and: [{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }]} ,{$or : [  {'name':{$regex : ?1,$options : \"i\"}} , {'desc':{$regex : ?1,$options : \"i\"}}, {'attributes':{$elemMatch:{'desc':{$regex : ?1,$options : \"i\"}}}},{'attributes':{$elemMatch:{'name':{$regex : ?1,$options : \"i\"}}}}] }]}")
	public List<DQRecExec> findAll(String appUuid,String searchStr);	
}
