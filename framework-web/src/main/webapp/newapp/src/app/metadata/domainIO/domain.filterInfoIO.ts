import { AttributeIO } from './domain.attributeIO';

export class FilterInfoIO {

	lhsType: String;
	logicalOperator: String;
	operator: String;
	rhsType: String
	// rhsAttribute: { uuid: String, label: String, attributeId: String };
	// lhsAttribute: { uuid: String, label: String, attributeId: String };
	rhsAttribute: AttributeIO;
	lhsAttribute: AttributeIO;
	rhsAttribute1: String;
	rhsAttribute2: String;
	selected?: any;

}