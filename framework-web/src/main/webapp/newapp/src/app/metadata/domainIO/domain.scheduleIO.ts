import { BaseEntity } from './../domain/domain.baseEntity';
import { Schedule } from "../domain/domain.schedule";
import { MetaIdentifierHolder } from '../domain/domain.metaIdentifierHolder';

export class ScheduleIO extends BaseEntity{
    
	startDate: any;
    endDate: any;
    nextRunTime: Date;
	frequencyType: String;          //Once/Daily/Weekly/Bi-Weekly/Monthly/Quarterly/Yearly
	frequencyDetail: String[];      //if weekly then days/if monthly then date/if quarterly then quarter
    dependsOn: MetaIdentifierHolder;
	scheduleChg: String;
    attrtype : String;
    // attribute : Schedule;
}