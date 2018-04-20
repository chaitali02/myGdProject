import { BaseEntity } from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';

export class Role extends BaseEntity{

	private privilegeInfo : MetaIdentifierHolder[];
}