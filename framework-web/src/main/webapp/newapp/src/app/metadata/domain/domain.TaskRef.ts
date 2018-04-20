import { MetaIdentifier } from './domain.metaIdentifier';

export class TaskRef{
    private dagRef : MetaIdentifier;
	private taskId : String;
	private stageId : String;
	private taskName : String;
	private dependsOn : String[];
}