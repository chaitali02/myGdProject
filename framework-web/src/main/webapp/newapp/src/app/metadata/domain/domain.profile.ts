import { AttributeRefHolder } from './domain.attributeRefHolder';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { BaseRule } from './domain.baseRule';
import { FilterInfo } from './domain.filterInfo';


export class Profile extends BaseRule{

    dependsOn : MetaIdentifierHolder;
	attributeInfo : AttributeRefHolder[];
    filterInfo : Array<FilterInfo>;
}