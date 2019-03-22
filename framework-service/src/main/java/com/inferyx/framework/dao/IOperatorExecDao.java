/**
 * 
 */
package com.inferyx.framework.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.inferyx.framework.domain.OperatorExec;

/**
 * @author Ganesh
 *
 */
public interface IOperatorExecDao extends MongoRepository<OperatorExec, String> {

	@Query(value = "{ 'uuid' : ?0 , 'version' : ?1 }")
	public OperatorExec findOneByUuidAndVersion(String uuid, String version);

	@Query(value = "{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public OperatorExec findOneByUuidAndVersion(String appUuid, String uuid, String version);

	@Query(value = "{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<OperatorExec> test(String param1);

	@Query(value = "{'uuid':?0}")
	public OperatorExec findAllByUuid(String uuid);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid':?1}")
	public OperatorExec findAllByUuid(String appUuid, String uuid);

	@Query(value = "{}")
	public OperatorExec findLatest(Sort sort);
	
	@Query(value="{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public OperatorExec findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ 'uuid' : ?0 }")
	public OperatorExec findLatestByUuid(String uuid, Sort sort);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] , 'uuid' : ?1 }")
	public OperatorExec findLatestByUuid(String appUuid, String datapodUUID, Sort sort);

	@Query(value = "{ 'userInfo.ref.uuid' : ?0 }")
	public List<OperatorExec> findActivityByUser(String uuid, Sort sort);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1}")
	public List<OperatorExec> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<OperatorExec> findAllVersion(String uuid);
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] }")
	public List<OperatorExec> findAll(String appUuid);	
	
	@Query(value="{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  , '_id' : ?1 }")
	public OperatorExec findOneById(String appUuid, String id);

	@Query(value = "{  $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ] ,'uuid' : ?1, 'version':{$lte:?2 }}")
	public OperatorExec findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public OperatorExec findAsOf(String uuid, String version, Sort sort);

	@Query(value = "{ $or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ]  ,'_id' : ?1}")
	public void delete(String appUuid, String id);	

	@Query(value = "{ '_id' : ?0 }")
	public OperatorExec findOneById(String id);
	
	@Query(value="{}")
	public List<OperatorExec> findAll();	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public OperatorExec save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public OperatorExec save(String id);

	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
}
