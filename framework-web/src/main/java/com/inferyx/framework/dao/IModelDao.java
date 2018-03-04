package com.inferyx.framework.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.inferyx.framework.domain.Meta;
import com.inferyx.framework.domain.Model;


public interface IModelDao extends MongoRepository<Model, String>{
	@Query(value="{ 'uuid' : ?0 , 'version' : ?1 }")
	public Model findOneByUuidAndVersion(String uuid, String version);	
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}, 'uuid' : ?1 , 'version' : ?2 }")
	public Model findOneByUuidAndVersion(String appUuid,String uuid, String version);
	
	@Query(value="{'uuid':?0}")
	public Model findAllByUuid(String uuid);
	
	@Query(value="{ 'uuid' : ?0 }")
	public Model findLatestByUuid(String uuid, Sort sort);
		
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} , '_id' : ?1 }")
	public Model findOneById(String appUuid, String id);

	@Query(value = "{ '_id' : ?0 }")
	public Model findOneById(String id);
	
	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid' : ?1 }")
	public Model findLatestByUuid(String appUuid, String uuid, Sort sort);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'uuid':?1}")
	public Model findAllByUuid(String appUuid, String uuid);

	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public void delete(String appUuid, String id);

	@Query(value = "{'_id' : ?0}")
	public void delete(String id);
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1}")
	
	public List<Model> findAllVersion(String appUuid, String uuid);
	
	@Query(value = "{'uuid' : ?0}")
	public List<Model> findAllVersion(String uuid);
	
	@Query(value = "{}")
	public Model findLatest(Sort sort);	
	
	@Query(value="{'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public Model findLatest(String appUuid, Sort sort);
	
	@Query(value="{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}}}")
	public List<Model> findAll(String appUuid);	

	@Query(value = "{ 'appInfo':{$elemMatch: { 'ref.uuid': ?0}},'uuid' : ?1, 'version':{$lte:?2 }}")
	public Model findAsOf(String appUuid, String uuid, String version, Sort sort);
	
	@Query(value="{}")
	public List<Model> findAll();
	
	@Query(value = "{'appInfo':{$elemMatch: { 'ref.uuid': ?0}} ,'_id' : ?1}")
	public Model save(String appUuid, String id);
	
	@Query(value = "{'_id' : ?0}")
	public Model save(String id);		
}
