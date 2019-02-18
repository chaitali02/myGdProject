
import { routing } from './job-monitoring-details-routing.module'
import { TagInputModule } from 'ngx-chips';
// import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { ProjectSharedModule } from '../../shared/module/shared.module';
import { MessagesModule, MessageModule } from 'primeng/primeng';
import { CheckboxModule } from 'primeng/primeng';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';

import { CommonService } from '../../metadata/services/common.service';

import { MultiSelectModule } from 'primeng/components/multiselect/multiselect';
import { DropdownModule } from 'primeng/components/dropdown/dropdown';
import { GrowlModule } from 'primeng/components/growl/growl';

import { jobMonitoringDetailsComponent } from './jobMonitoringDetails.component'
import { LoadExecComponent } from './loadExec/loadExec.component';
import { MapExecComponent } from './mapExec/mapExec.component';
import { VizExecComponent } from './vizExec/vizExec.component';
import { TrainExecComponent } from './trainExec/trainExec.component';
import { PredictExecComponent } from './predictExec/predictExec.component';
import { SimulateExecComponent } from './simulateExec/simulateExec.component'

import { ProfileExecComponent } from './profileExec/profileExec.component';
import { ProfileGroupExecComponent } from './profileGroupExec/profileGroupExec.component';
import { DqExecComponent } from './dqExec/dqExec.component';
import { DqGroupExecComponent } from './dqGroupExec/dqGroupExec.component';
import { RuleExecComponent } from './ruleExec/ruleExec.component';
import { RuleGroupExecComponent } from './ruleGroupExec/ruleGroupExec.component';
import { PipelineExecComponent } from './pipelineExec/pipelineExec.component';
import { DownloadExecComponent } from './downloadExec/downloadExec.component';
import { UploadExecComponent } from './uploadExec/uploadExec.componet'
import { BatchExecComponent } from './batchExec/batchExec.component';
import { IngestExecComponent } from './ingestExec/ingestExec.component';
import { ReportExecComponent } from './reportExec/reportExec.component';
import { ReconExecComponent } from './reconExec/reconExec.component';
import { DialogModule } from 'primeng/components/dialog/dialog';
import { ReconGroupExecComponent } from './reconGroupExec/reconGroupExec.component';
import { IngestGroupExecComponent } from './ingestGroupExec/ingestGroupExec.component';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        routing,
        ProjectSharedModule,
        // UiSwitchModule,
        TagInputModule,
        MessagesModule,
        MessageModule,
        AngularMultiSelectModule,
        MultiSelectModule,
        DropdownModule,
        GrowlModule,
        CheckboxModule,
        DialogModule
       
    ],
    declarations: [
        jobMonitoringDetailsComponent,
        LoadExecComponent,
        MapExecComponent,
        VizExecComponent,
        TrainExecComponent,
        ProfileExecComponent,
        ProfileGroupExecComponent,
        DqExecComponent,
        DqGroupExecComponent,
        RuleExecComponent,
        RuleGroupExecComponent,
        PipelineExecComponent,
        PredictExecComponent,
        SimulateExecComponent,
        DownloadExecComponent,
        UploadExecComponent,
        BatchExecComponent,
        IngestExecComponent,
        IngestGroupExecComponent,
        ReportExecComponent,
        ReconExecComponent,
        ReconGroupExecComponent
    ],
    providers: [
        CommonService
    ]
})

export class jobMonitoringDetailsModule { }