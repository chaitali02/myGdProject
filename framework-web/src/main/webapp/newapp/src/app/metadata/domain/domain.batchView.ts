import { BaseEntity } from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';
import { Schedule } from './domain.schedule';

export class BatchView extends BaseEntity{
    pipelineInfo : MetaIdentifierHolder[];
	inParallel: String;
	batchChg : String;
	scheduleInfo : Schedule[];
}