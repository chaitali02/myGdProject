import { BaseEntity } from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';

export class DataQualityGroup extends BaseEntity {

	ruleInfo: MetaIdentifierHolder[];
	inParallel: String;

}