import { BaseEntity } from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';

export class Activity extends BaseEntity{

	private status : String ;
	private userInfo : MetaIdentifierHolder;
	private metaInfo : MetaIdentifierHolder ;
	private sessionInfo : MetaIdentifierHolder;
	private requestUrl : String;
}



