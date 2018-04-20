import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { BaseEntity } from './domain.baseEntity';

export class Thread extends BaseEntity{

    name : String;
	execInfo : MetaIdentifierHolder;
}