import { Status } from './domain.status';
import { BaseEntity } from './domain.baseEntity';
import {MetaIdentifierHolder} from './domain.metaIdentifierHolder';

export class BaseRuleGroupExec extends BaseEntity{

    private execList : MetaIdentifierHolder;
	private dependsOn : MetaIdentifierHolder;	
	private statusList : Status[];

}