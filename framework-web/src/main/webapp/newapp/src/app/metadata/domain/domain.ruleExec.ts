import { MetaIdentifier } from './domain.metaIdentifier';
import { BaseRuleExec } from './domain.baseRuleExec';
import { ExecParams } from './domain.ExecParams';

export class RuleExec extends BaseRuleExec{

    private execParams : ExecParams;	
	private paramSet : MetaIdentifier;
}