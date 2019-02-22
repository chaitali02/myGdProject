import { BaseEntity } from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';

export class BaseRuleGroup extends BaseEntity{

	ruleInfo : MetaIdentifierHolder[];
	inParallel : String;
}