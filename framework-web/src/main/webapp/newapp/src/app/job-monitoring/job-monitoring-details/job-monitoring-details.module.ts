
import { routing } from './job-monitoring-details-routing.module'
import { TagInputModule } from 'ngx-chips';
import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { AgGridModule } from 'ag-grid-angular/main';
import { FormsModule } from '@angular/forms';
import { CommonModule , DatePipe} from '@angular/common';
import { NgModule } from '@angular/core';
import { ProjectSharedModule } from '../../shared/module/shared.module';
import { MessagesModule, MessageModule } from 'primeng/primeng';
import { SelectModule } from 'ng-select';
import { CheckboxModule} from 'primeng/primeng';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown/angular2-multiselect-dropdown';

import { CommonService } from '../../metadata/services/common.service';

import { MultiSelectModule } from 'primeng/components/multiselect/multiselect';
import { DropdownModule } from 'primeng/components/dropdown/dropdown';
import { GrowlModule } from 'primeng/components/growl/growl';

import {jobMonitoringDetailsComponent} from './jobMonitoringDetails.component'
import {LoadExecComponent} from './loadExec/loadExec.component';
import {MapExecComponent} from './mapExec/mapExec.component';
import {VizExecComponent} from './vizExec/vizExec.component';
import { ModelExecComponent } from './modelExec/modelExec.component';
import { ProfileExecComponent } from './profileExec/profileExec.component';
import { ProfileGroupExecComponent } from './profileGroupExec/profileGroupExec.component';
import { DqExecComponent } from './dqExec/dqExec.component';
import { DqGroupExecComponent } from './dqGroupExec/dqGroupExec.component';
import { RuleExecComponent } from './ruleExec/ruleExec.component';
import { RuleGroupExecComponent } from './ruleGroupExec/ruleGroupExec.component';
import { PipelineExecComponent } from './pipelineExec/pipelineExec.component';

@NgModule({
    imports:[
        CommonModule,
        FormsModule,
        routing,
        ProjectSharedModule,
        AgGridModule,
        UiSwitchModule,
        TagInputModule,
        MessagesModule,
        MessageModule,
        SelectModule,
        AngularMultiSelectModule,
        MultiSelectModule,
        DropdownModule,
        GrowlModule,
        CheckboxModule,
    ],
    declarations :[
        jobMonitoringDetailsComponent,
        LoadExecComponent,
        MapExecComponent,
        VizExecComponent,
        ModelExecComponent,
        ProfileExecComponent,
        ProfileGroupExecComponent,
        DqExecComponent,
        DqGroupExecComponent,
        RuleExecComponent,
        RuleGroupExecComponent,
        PipelineExecComponent
      
    ],
    providers:[
        CommonService
    ]
})
 
export class jobMonitoringDetailsModule{}