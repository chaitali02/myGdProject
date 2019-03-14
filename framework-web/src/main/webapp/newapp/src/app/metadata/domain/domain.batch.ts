import { BaseEntity } from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';

export class Batch extends BaseEntity{
    pipelineInfo : MetaIdentifierHolder[];
	inParallel: String;
}