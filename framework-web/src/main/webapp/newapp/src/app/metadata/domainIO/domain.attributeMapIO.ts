import { AttributeRefHolder } from '../domain/domain.attributeRefHolder';
import { AttributeIO } from './domain.attributeIO';

export class AttributeMapIO {
    attrMapId: any;
    sourceType: String;
    sourceAttribute: any;
    targetAttribute: any;
    IsTargetAttributeSimple: String;
    // 	sourceAttr: AttributeRefHolder;
    // 	targetAttr: AttributeRefHolder;
    selected?: any
}