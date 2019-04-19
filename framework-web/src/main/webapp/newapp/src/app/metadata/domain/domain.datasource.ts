import { BaseEntity } from './domain.baseEntity';

export class Datasource extends BaseEntity{

   type : String;
	access : String;
	driver : String;
	host : String;
	dbname : String;
	port : String;
	username : String;
	password : String;
	path : String;
	subType : String;
	sessionParameters: String;
	sid: String;
	
}