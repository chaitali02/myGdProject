import { MetaIdentifier } from './domain.metaIdentifier';
import { ExecParams } from './domain.ExecParams';

export class PipelineExec{

    dependsOn : MetaIdentifier;
	
	private execParams : ExecParams;
}