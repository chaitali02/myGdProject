import { BaseRule } from './domain.baseRule';
import { FilterInfo } from './domain.filterInfo';
import { AttributeRefHolder } from './domain.attributeRefHolder';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';

export class Recon extends BaseRule{
    sourceAttr: AttributeRefHolder;
	sourceFunc: MetaIdentifierHolder;
	targetAttr: AttributeRefHolder;
	targetFunc: MetaIdentifierHolder;
	sourceFilter: FilterInfo[];
	targetFilter: FilterInfo[];
	sourceDistinct: String = "N";
	targetDistinct: String = "N";
	sourceGroup: AttributeRefHolder[];
	targetGroup: AttributeRefHolder[];
}