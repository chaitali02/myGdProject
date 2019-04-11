import { BaseRule } from '../domain/domain.baseRule';
import { FilterInfoIO } from './domain.filterInfoIO';
import { Rule } from '../domain/domain.rule';

export class RuleIO{

	baseRule: Rule;
	filterInfo: Array<FilterInfoIO>;
	// filterTableArray: Array<FilterInfoIO>;
	attributeMap: any;
	attributeTableArray: any;
	// paramlistArray1: any;

	isSimpleExits:Boolean;
	isFormulaExits: Boolean;
	isAttributeExits: Boolean;
	isFunctionExits:Boolean;
	isParamlistExits:Boolean;
	isDatasetExits:Boolean;
	isRelationExists: Boolean;


}