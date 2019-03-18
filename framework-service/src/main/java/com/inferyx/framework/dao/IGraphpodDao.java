
package com.inferyx.framework.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.inferyx.framework.domain.Graphpod;

public interface IGraphpodDao extends MongoRepository<Graphpod, String> {
	@Query(value="{$or: [ { publicFlag: \"Y\"},{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} }],'uuid' : ?1 , 'version' : ?2 }")
	public Graphpod findOneByUuidAndVersion(String appUuid,String datapodUUID, String version);
	
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public Graphpod findOneByUuidAndVersion(String datapodUUID, String version);
	
	@Query(value="{$group :{ _id : '$uuid', maxVersion : {$max : '$version'} }}")
	public List<Graphpod> test(String param1);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid':?1}")
	public Graphpod findAllByUuid(String appUuid,String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1 }")
	public Graphpod findLatestByUuid(String appUuid,String datapodUUID, Sort sort);
	
	@Query(value="{ 'uuid' : ?0 }")
	public Graphpod findLatestByUuid(String datapodUUID, Sort sort);
	
	@Query(value = "{$or: [ { publicFlag: \"Y\"}, { 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} } ],'uuid' : ?1}")
	public List<Graphpod> findAllVersion(String appUuid, String uuid);

	@Query(value = "{'uuid' : ?0}")
	public List<Graphpod> findAllVersion(String uuid);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Graphpod> findAll(String appUuid);	
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Graphpod findOneById(String appUuid, String id);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Graphpod findAsOf(String appUuid, String uuid, String version, Sort sort);

	@Query(value = "{ 'uuid' : ?0, 'version':{$lte:?1 }}")
	public Graphpod findAsOf(String uuid, String version, Sort sort);
	
	@Query(value = "{}")
	public Graphpod findLatest(Sort sort);
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Graphpod findLatest(String appUuid, Sort sort);
	
	@Query(value = "{ '_id' : ?0 }")
	public Graphpod findOneById(String id);

	@Query(value = "{'uuid':?0}")
	public Graphpod findAllByUuid(String uuid);
	
	@Query(value="{}")
	public List<Graphpod> findAll();	
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Graphpod save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Graphpod save(String id);		
	
}
