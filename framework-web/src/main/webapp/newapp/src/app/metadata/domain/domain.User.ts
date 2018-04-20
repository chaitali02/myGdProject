import { BaseEntity } from './domain.baseEntity';

export class User extends BaseEntity{

    private firstName : String;
	private middleName : String;
	private lastName : String;
	private emailId : String;
	private password : String;
}