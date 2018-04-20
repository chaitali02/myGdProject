import { Status } from './domain.status';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { ExecParams } from './domain.ExecParams';

export class LoadExec{
    private statusList : Status;
	private dependsOn : MetaIdentifierHolder;	
	private execParams : ExecParams ;	
	private exec : String;
	private result : MetaIdentifierHolder ;
}