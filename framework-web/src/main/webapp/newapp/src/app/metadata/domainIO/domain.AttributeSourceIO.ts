import { AttributeRefHolder } from '../domain/domain.attributeRefHolder';

export class AttributeSourceIO {
    attrSourceId: String;
    attrDisplaySeq: String;
    attrSourceName: String;
    sourceAttr: AttributeRefHolder;

    name: String;
    sourceAttributeType: any;
    isSourceAtributeSimple: Boolean;
    isSourceAtributeDatapod: Boolean;
    isSourceAtributeFormula: Boolean;
    isSourceAtributeExpression: Boolean;
    isSourceAtributeFunction: Boolean;
    isSourceAtributeParamList: Boolean;
    sourcesimple: any;
    sourceattribute: any;
    sourceexpression: any;
    sourceformula: any;
    sourcefunction: any;
    id: any;
}