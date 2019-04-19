

import {BaseEntity} from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { SourceAttr } from './domain.sourceAttr';
import { FormulaType } from './domain.formulaType';

export class Formula extends BaseEntity{

    dependsOn :MetaIdentifierHolder;
	formulaInfo :Array<SourceAttr> ;
	formulaType :FormulaType;
}