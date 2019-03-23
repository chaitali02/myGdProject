
import { SharedService } from './../shared/shared.service';
import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
// import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { TagInputModule } from 'ngx-chips';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { CheckboxModule, CalendarModule, OverlayPanelModule, DialogModule, MultiSelectModule, ChipsModule } from 'primeng/primeng';
import { ResultModule } from '../shared/module/result.module';
import { ProjectSharedModule } from '../shared/module/shared.module';

import { BatchSchedulerComponent } from './batch-scheduler.component';
//import{DataProfileresultComponent} from './data-profileresult.component'
//import{DataProfileDetailComponent} from './data-profiledetail.component'
//import{DataProfileGroupDetailComponent} from './data-profilegroupdetail.components'
import { CommonService } from '../metadata/services/common.service';
// import {jointjsGroupService} from '../shared/components/jointjsgroup/joinjsgroup.service'
import { batchschedulerrouting } from './batch-scheduler-routing';
import { BatchSchedulerResultComponent } from './batch-schedulerresult.component';
//import { ProfileExecComponent } from '../job-monitoring/job-monitoring-details/profileExec/profileExec.component';
import { BatchSchedulerdetailComponent } from './batch-schedulerdetail.component';
import { MenuModule, MenuItem } from 'primeng/primeng';
import { TableModule } from 'primeng/table';
import { MetadataService } from '../metadata/services/metadata.service';
import { SharedDataService } from '../data-pipeline/shareddata.service';
import { DataPipelineService } from '../metadata/services/dataPipeline.service';
import { BatchService } from '../metadata/services/batch.service';
import { JointjsService } from '../shared/components/jointjs/jointjsservice';
import { UiSwitchModule } from 'ngx-ui-switch';
//import { DataPiplineResultComponent } from './../data-pipeline/data-pipelineresult.component';
//import { JointjsComponent } from '../data-pipeline/jointjs.component';
//import { ShContextMenuModule } from 'ng2-right-click-menu';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        // UiSwitchModule,
        TagInputModule,
        AngularMultiSelectModule,
        CheckboxModule,
        ResultModule,
        ProjectSharedModule,
        batchschedulerrouting,
        CalendarModule,
        OverlayPanelModule,
        DialogModule,
        MenuModule,
        MultiSelectModule,
        ChipsModule,
        UiSwitchModule.forRoot({
            size: 'small',
            color: '#15C5D5',
            switchColor: '#FFFFF',
            defaultBoColor: '#ccc',
            // checkedLabel: 'Yes',
            //uncheckedLabel: 'No'
        }),
        TableModule
        //ShContextMenuModule


    ],
    exports: [
        MultiSelectModule
    ],
    declarations: [
        BatchSchedulerComponent,
        BatchSchedulerResultComponent,
        BatchSchedulerdetailComponent
        //DataPiplineResultComponent,
        //JointjsComponent
        //DataProfileDetailComponent,
        //DataProfileresultComponent,
        //DataProfileGroupDetailComponent,

    ],
    providers: [
        CommonService,
        JointjsService,
        MetadataService,
        SharedDataService,
        DataPipelineService,
        BatchService
        //JointjsComponent
        //  jointjsGroupService
    ],
    entryComponents: [
        //JointjsComponent
    ],
})

export class BatchSchedulerModule { }