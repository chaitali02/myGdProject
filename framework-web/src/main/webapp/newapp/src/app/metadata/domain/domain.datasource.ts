import { BaseEntity } from './domain.baseEntity';

export class Datasource extends BaseEntity{

   // private type : String;
	private access : String;
	private driver : String;
	private host : String;
	private dbname : String;
	private port : String;
	private username : String;
	private password : String;
	private path : String;
	private subType : String;
}