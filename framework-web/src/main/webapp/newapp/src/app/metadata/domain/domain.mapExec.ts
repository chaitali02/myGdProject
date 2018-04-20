import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { BaseEntity } from './domain.baseEntity';
import { ExecParams } from './domain.ExecParams';
import { MetaIdentifier } from './domain.metaIdentifier';
import { Status } from './domain.status';

export class MapExec extends BaseEntity{

    private statusList : Status;
    dependsOn : MetaIdentifierHolder;		
    private execParams : ExecParams ;	
    private refKeyList : MetaIdentifier;
    private exec : String;
    private result : MetaIdentifierHolder;
}