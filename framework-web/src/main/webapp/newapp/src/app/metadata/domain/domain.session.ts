import { Status } from './domain.status';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { BaseEntity } from './domain.baseEntity';

export class Session extends BaseEntity{

	private userInfo : MetaIdentifierHolder;
	private statusList : Status[];
	private roleInfo : MetaIdentifierHolder;
	private sessionId : String;
	private ipAddress : String;
}