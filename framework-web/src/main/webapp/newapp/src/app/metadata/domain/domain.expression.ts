import { FilterInfo } from './domain.filterInfo';
import { AttributeRefHolder } from './domain.attributeRefHolder';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { BaseEntity } from './domain.baseEntity';


export class Expression extends BaseEntity{

	private dependsOn : MetaIdentifierHolder ;
	private condition : String;
	private match : AttributeRefHolder;
	private noMatch : AttributeRefHolder;	
	private expressionInfo : FilterInfo[];	
	
}