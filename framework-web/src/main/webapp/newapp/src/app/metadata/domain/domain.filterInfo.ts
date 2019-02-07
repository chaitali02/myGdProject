import { SourceAttr } from './domain.sourceAttr';

export class FilterInfo {

	display_seq: String;
	logicalOperator: String;
	operator: String;
	operand: Array<SourceAttr>;

}