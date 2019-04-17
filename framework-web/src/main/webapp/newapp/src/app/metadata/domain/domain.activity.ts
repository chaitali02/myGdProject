import { BaseEntity } from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';

export class Activity extends BaseEntity{

	status : String ;
	userInfo : MetaIdentifierHolder;
	metaInfo : MetaIdentifierHolder ;
	sessionInfo : MetaIdentifierHolder;
	requestUrl : String;
}



