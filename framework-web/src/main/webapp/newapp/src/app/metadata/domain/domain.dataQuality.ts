import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { AttributeRefHolder } from './domain.attributeRefHolder';

import { BaseEntity } from './domain.baseEntity';
import { FilterInfo } from './domain.filterInfo';

export class DataQuality extends BaseEntity {

	attribute: AttributeRefHolder;
	private dependsOn: MetaIdentifierHolder;
	private duplicateKeyCheck: String;
	private nullCheck: String;
	private valueCheck: String;
	private rangeCheck: {};
	private lengthCheck: {};
	private refIntegrityCheck: AttributeRefHolder;

	filterInfo: Array<FilterInfo>;

	// private target : MetaIdentifierHolder ;		
	// private dataTypeCheck : String;
	// private dateFormatCheck : String;
	// private customFormatCheck : String;
	// //private String stdDevCheck;
	// private userInfo : MetaIdentifierHolder[];

}