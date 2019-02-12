import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
// import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { TagInputModule } from 'ngx-chips';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown/angular2-multiselect-dropdown';
import { CheckboxModule, CalendarModule, ChipsModule } from 'primeng/primeng';

import { ResultModule } from '../shared/module/result.module';
import { ProjectSharedModule } from '../shared/module/shared.module';
import { UiSwitchModule } from 'ngx-ui-switch';

import { DataQualityComponent } from './data-quality.component';
import { DataQualityResultComponent } from './data-qualityresult.component'
import { DataQualityDetailComponent } from './data-qualitydetail.component'
import { DataQualityGroupDetailComponent } from './data-qualitygroupdetail.components'
import { CommonService } from '../metadata/services/common.service';
import { DataQualityService } from '../metadata/services/dataQuality.services';

import { JointjsService } from '../shared/components/jointjs/jointjsservice'
import { routing1 } from './data-quality-routing.module'
import { DialogModule } from 'primeng/components/dialog/dialog';
import { CompareResultComponent } from './compareresult/compareresult.component';
import { MetadataService } from '../metadata/services/metadata.service';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        // UiSwitchModule,
        TagInputModule,
        AngularMultiSelectModule,
        CheckboxModule,
        routing1,
        ResultModule,
        ProjectSharedModule,
        DialogModule,
        CalendarModule,
        ChipsModule,
        UiSwitchModule.forRoot({
            size: 'medium',
            color: '#15C5D5',
            switchColor: '#FFFFF',
            defaultBoColor : '#ccc',
            checkedLabel: 'Yes',
            uncheckedLabel: 'No'
          })
    ],
    declarations: [
        DataQualityComponent,
        DataQualityResultComponent,
        DataQualityDetailComponent,
        DataQualityGroupDetailComponent,
        CompareResultComponent
    ],
    providers: [
        CommonService,
        DataQualityService,
        JointjsService,
        MetadataService
    ],

})

export class DataQualityModule { }