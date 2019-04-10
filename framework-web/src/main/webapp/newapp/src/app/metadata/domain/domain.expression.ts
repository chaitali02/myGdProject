import { FilterInfo } from './domain.filterInfo';
import { AttributeRefHolder } from './domain.attributeRefHolder';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { BaseEntity } from './domain.baseEntity';


export class Expression extends BaseEntity{

	 dependsOn : MetaIdentifierHolder ;
	 condition : String;
	 match : AttributeRefHolder;
	 noMatch : AttributeRefHolder;	
	 expressionInfo : FilterInfo[];	

}