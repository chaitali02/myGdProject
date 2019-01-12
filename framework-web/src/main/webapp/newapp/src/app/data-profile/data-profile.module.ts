import { CompareResultComponent } from './../compareresult/compareresult.component';
import { NgModule } from '@angular/core';
import { CommonModule ,DatePipe} from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AgGridModule } from 'ag-grid-angular/main';
import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { TagInputModule } from 'ngx-chips';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown/angular2-multiselect-dropdown';
import {CheckboxModule, TabViewModule} from 'primeng/primeng';
import { ResultModule } from '../shared/module/result.module';
import { ProjectSharedModule } from '../shared/module/shared.module';

import { DataProfileComponent } from './data-profile.component';
import{DataProfileresultComponent} from './data-profileresult.component'
import{DataProfileDetailComponent} from './data-profiledetail.component'
import{DataProfileGroupDetailComponent} from './data-profilegroupdetail.components'
import{CompareProfileResultComponent} from './compare-profile/compare-profileresult.component'
import{CommonService}from '../metadata/services/common.service';
import{ProfileService}from '../metadata/services/profile.service';
import {JointjsService} from '../shared/components/jointjs/jointjsservice'//
// import {jointjsGroupService} from '../shared/components/jointjsgroup/joinjsgroup.service'
import { routing1} from './data-profile-routing.module'
import { CalendarModule } from 'primeng/components/calendar/calendar';
//import { ProfileExecComponent } from '../job-monitoring/job-monitoring-details/profileExec/profileExec.component';
import { DialogModule } from 'primeng/components/dialog/dialog';
import { AttributeTabComponent } from './compare-profile/attributeTab/attributetab.component';
import { ResultsTabComponent } from './compare-profile/resultsTab/resultstab.component';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        UiSwitchModule,
        TagInputModule,
        AngularMultiSelectModule,
        CheckboxModule,
        routing1,
        ResultModule,
        ProjectSharedModule,
        DialogModule,
        TabViewModule,
        CalendarModule
    ],
    declarations: [
        DataProfileComponent,
        DataProfileDetailComponent,
        DataProfileresultComponent,
        DataProfileGroupDetailComponent,
        CompareProfileResultComponent,
        AttributeTabComponent,
        ResultsTabComponent
    ],
    providers: [
        CommonService,
        JointjsService,
        ProfileService
      //  jointjsGroupService
    ],

})

export class DataProfileModule { }