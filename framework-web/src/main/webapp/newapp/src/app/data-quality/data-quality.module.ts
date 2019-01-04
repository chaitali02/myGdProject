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

import { DataQualityComponent } from './data-quality.component';
import { DataQualityResultComponent} from './data-qualityresult.component'
import { DataQualityDetailComponent} from './data-qualitydetail.component'
import { DataQualityGroupDetailComponent} from './data-qualitygroupdetail.components'
import { CommonService}from '../metadata/services/common.service';
import { DataQualityService}from '../metadata/services/dataQuality.services';

import {JointjsService} from '../shared/components/jointjs/jointjsservice'
import { routing1} from './data-quality-routing.module'
import { DialogModule } from 'primeng/components/dialog/dialog';


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
        DialogModule
       
    ],
    declarations: [
        DataQualityComponent,
        DataQualityResultComponent,
        DataQualityDetailComponent,
        DataQualityGroupDetailComponent   
    ],
    providers: [
        CommonService,
        DataQualityService,
        JointjsService,
    ],

})

export class DataQualityModule { }