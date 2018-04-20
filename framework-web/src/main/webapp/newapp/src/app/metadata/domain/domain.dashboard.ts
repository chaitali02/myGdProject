import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { BaseEntity } from './domain.baseEntity';
import { AttributeRefHolder } from './domain.attributeRefHolder';
import { Section } from './domain.section';

export class Dashboard extends BaseEntity{

	private sectionInfo : Section[];
	private dependsOn : MetaIdentifierHolder;
	private filterInfo : AttributeRefHolder[];
}