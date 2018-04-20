import { Attribute } from './domain.attribute';
import { OrderKey } from './domain.orderKey';
import { Value } from './domain.value';
import { OperatorNew } from './domain.operatorNew';
import { LogicalOperand } from './domain.logicalOperand';
import { MetaIdentifier } from './domain.metaIdentifier';

export class Operand{

    private ref : MetaIdentifier;
	
	private attributeId : Attribute;
	
	private condition : OrderKey;

	private value : Value; 
	
	private operand : OperatorNew;
	
	private logicaloperand : LogicalOperand;
	
	private operator : String ;
}