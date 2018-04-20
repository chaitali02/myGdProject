import { ExecParams } from './domain.ExecParams';
import { MetaIdentifier } from './domain.metaIdentifier';
import { Status } from './domain.status';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder'; 

export class ModelExec{

	private statusList : Status;
	dependsOn : MetaIdentifierHolder;	
	private execParams : ExecParams;
	refKeyList : MetaIdentifier;
	private exec : String;
	private result : MetaIdentifierHolder ; // Datastore info
}