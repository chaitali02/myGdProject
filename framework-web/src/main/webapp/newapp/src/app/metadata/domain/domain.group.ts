import { BaseEntity } from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';


export class Group extends BaseEntity{

    private roleInfo : MetaIdentifierHolder;
	roleId : MetaIdentifierHolder ;
	appId : MetaIdentifierHolder;


}