import { NgModule } from '@angular/core';
import { CommonModule ,DatePipe} from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AgGridModule } from 'ag-grid-angular/main';
import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { TagInputModule } from 'ngx-chips';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown/angular2-multiselect-dropdown';
import { CheckboxModule} from 'primeng/primeng';

import { ResultModule } from '../shared/module/result.module';
import { ProjectSharedModule } from '../shared/module/shared.module';

import { DataReconComponent } from './data-recon.component';
import{DataReconDetailComponent} from './data-recondetail.component'
import{DataReconGroupDetailComponent} from './data-reconGroupdetail.component'
import{DataReconresultComponent} from './data-reconresult.component'
// import { DataQualityDetailComponent} from './data-qualitydetail.component'
// import { DataQualityGroupDetailComponent} from './data-qualitygroupdetail.components'
import { CommonService}from '../metadata/services/common.service';
import { DataReconService}from '../metadata/services/dataRecon.services';

import {JointjsService} from '../shared/components/jointjs/jointjsservice'
import { routing1} from './data-recon-routing.module'


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
        ProjectSharedModule
       
    ],
    declarations: [
        DataReconComponent,
        DataReconDetailComponent,
        DataReconGroupDetailComponent,
        DataReconresultComponent
        // DataQualityResultComponent,
        // DataQualityDetailComponent,
        // DataQualityGroupDetailComponent   
    ],
    providers: [
        CommonService,
        DataReconService,
        //DataQualityService,
        JointjsService,
    ],

})

export class DataReconModule {debugger; }
