import { MetaIdentifier } from './domain.metaIdentifier';

export class MetaIdentifierHolder extends MetaIdentifier
{
    ref : MetaIdentifier ;
    value : String;
    
    attrId?: String;
    attrName?: String;
    attrType?: String;
    attrUnitType?: any;
}