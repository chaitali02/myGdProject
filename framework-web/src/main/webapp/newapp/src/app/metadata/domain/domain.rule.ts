import { BaseRule } from './domain.baseRule';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { FilterInfo } from './domain.filterInfo';
import { AttributeSource } from './domain.attributeSource';

export class Rule extends BaseRule{
    
    expressionInfo: Array<MetaIdentifierHolder>;
	filterInfo: Array<FilterInfo> ;	
	debugMode: boolean;
	source: MetaIdentifierHolder ;	// May be a relation, datapod or dataset
	attributeInfo: Array<AttributeSource>; 
	persistFlag: String ;
	datasource: MetaIdentifierHolder ;
	paramList: MetaIdentifierHolder ;

}   