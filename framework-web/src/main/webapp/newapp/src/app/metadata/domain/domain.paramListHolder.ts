import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { AttributeRefHolder } from './domain.attributeRefHolder';

export class ParamListHolder extends MetaIdentifierHolder {

	paramId: String;
	paramName: String;
	paramType: String;
	paramDispName: String;
	paramDesc: String;
	paramValue: MetaIdentifierHolder;
	attributeInfo: Array<AttributeRefHolder>;
}