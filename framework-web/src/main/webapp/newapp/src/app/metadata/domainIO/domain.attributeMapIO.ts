import { AttributeRefHolder } from '../domain/domain.attributeRefHolder';
import { AttributeIO } from './domain.attributeIO';
import { MetaIdentifier } from '../domain/domain.metaIdentifier';
import { SourceAttr } from '../domain/domain.sourceAttr';

export class AttributeMapIO {
    attrMapId: any;
    sourceType: String;
    sourceAttribute: any;
    targetAttribute: any;
    IsTargetAttributeSimple: String;
    // 	sourceAttr: AttributeRefHolder;
    // 	targetAttr: AttributeRefHolder;
    selected?: any

    // for business rule
    attrSourceId: any;
    attrSourceName: any;
    sourceAttr: any;
    attrDisplaySeq: any;
}