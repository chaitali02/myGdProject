import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { AttributeRefHolder } from './domain.attributeRefHolder';

import { BaseEntity } from './domain.baseEntity';
import { FilterInfo } from './domain.filterInfo';

export class DataQuality extends BaseEntity {

	attribute: AttributeRefHolder;
	dependsOn: MetaIdentifierHolder;
	duplicateKeyCheck: String;
	nullCheck: String;
	valueCheck: any[];
	rangeCheck: {};
	dataTypeCheck: String;
	dateFormatCheck: String;
	customFormatCheck: String;
	lengthCheck: {};
	refIntegrityCheck: AttributeRefHolder;
	filterInfo: Array<FilterInfo>;

}