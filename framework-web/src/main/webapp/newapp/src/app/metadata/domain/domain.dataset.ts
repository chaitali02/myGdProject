

import {BaseEntity} from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { FilterInfo } from './domain.filterInfo';
import { AttributeSource } from './domain.attributeSource';

export class Dataset extends BaseEntity{

    dependsOn :MetaIdentifierHolder;
	filterInfo : Array<FilterInfo>;	
	attributeInfo: Array<AttributeSource>;
	groupBy :MetaIdentifierHolder;
	limit : number;
}