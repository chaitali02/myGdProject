import { LogicalOperand } from './domain.logicalOperand';
import { FilterInfo } from './domain.filterInfo';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';

export class RelationInfo{

    private join : MetaIdentifierHolder;
    private joinType : String;
    private joinKey : FilterInfo;
    private logicaloperand : LogicalOperand;


}