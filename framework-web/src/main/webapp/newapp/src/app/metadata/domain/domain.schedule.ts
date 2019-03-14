import { BaseEntity } from './domain.baseEntity';
import { MetaIdentifierHolder } from './domain.metaIdentifierHolder';

export class Schedule extends BaseEntity{
	startDate: Date;
	endDate: Date;
	nextRunTime: Date;
	frequencyType: String;          //Once/Daily/Weekly/Bi-Weekly/Monthly/Quarterly/Yearly
	frequencyDetail: String[];      //if weekly then days/if monthly then date/if quarterly then quarter
    dependsOn: MetaIdentifierHolder;
	scheduleChg: String;
}