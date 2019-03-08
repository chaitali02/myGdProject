import { AttributeHolder } from '../domain/domain.attributeHolder';

// used for attribute table mapping - businessRule

export class AttributeInfoIO{
    name: String; 
    id : any
    
    sourceAttributeType: {}; 
    isSourceAtributeSimple: {};
    isSourceAtributeDatapod: {}; 
    isSourceAtributeFormula: {};
    isSourceAtributeExpression: {}; 
    isSourceAtributeFunction: {};
    isSourceAtributeParamList: {}; 

    sourcesimple: String; 
    sourceattribute: AttributeHolder; 
    sourceexpression:{};
    sourceformula:{}; 
    sourcefunction: {}; 
    sourceparamlist:{}
}