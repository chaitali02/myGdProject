import { BaseEntity } from './domain.baseEntity';
import { Status } from './domain.status';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { MetaIdentifier } from './domain.metaIdentifier';

export class BaseRuleExec extends BaseEntity{

    private statusList : Status;
	private dependsOn : MetaIdentifierHolder;
	private exec : String ;
	private result : MetaIdentifierHolder;
	private refKeyLis : MetaIdentifier;

}