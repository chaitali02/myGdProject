import {MetaIdentifierHolder} from './domain.metaIdentifierHolder';
import {AttributeRefHolder} from './domain.attributeRefHolder';

import {BaseEntity} from './domain.baseEntity';

export class DataQuality extends BaseEntity{

    private target : MetaIdentifierHolder ;	
	private attribute : AttributeRefHolder ;	
	private dependsOn : MetaIdentifierHolder ;	
	private duplicateKeyCheck : String;
	private nullCheck :String;
	private valueCheck :String;
	private rangeCheck : {};
	private dataTypeCheck : String;
	private dateFormatCheck : String;
	private customFormatCheck : String;
	private lengthCheck : {};
	private refIntegrityCheck : AttributeRefHolder;	
	//private String stdDevCheck;
	private filterInfo : AttributeRefHolder;
	private userInfo : MetaIdentifierHolder[];
	
}