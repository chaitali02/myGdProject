import { UiSwitchModule } from 'ngx-ui-switch';
import { NgModule } from '@angular/core';
import { CommonModule ,DatePipe} from '@angular/common';
import { FormsModule } from '@angular/forms';
// import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { TagInputModule } from 'ngx-chips';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { CheckboxModule, ChipsModule} from 'primeng/primeng';

import { ResultModule } from '../shared/module/result.module';
import { ProjectSharedModule } from '../shared/module/shared.module';

import { BusinessRulesComponent } from './business-rule.component';
import { BusinessRulesDetailComponent} from './business-rulesdetail.component'
import { BusinessRulesGroupDetailComponent} from './business-rulesgroupdetail.components'
import { BusinessRulesResultComponent} from './business-rulesresult.component'

import { RuleService } from '../metadata/services/rule.service';
import { CommonService}from '../metadata/services/common.service';
import { JointjsService} from '../shared/components/jointjs/jointjsservice';
import { routing1} from './business-rules-routing.module';
import { DialogModule } from 'primeng/components/dialog/dialog';
import {DragDropModule} from 'primeng/components/dragdrop/dragdrop';

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
        DragDropModule,
        ChipsModule,
        UiSwitchModule.forRoot({
            size: 'small',
            color: '#15C5D5',
            switchColor: '#FFFFF',
            defaultBoColor : '#ccc',
           // checkedLabel: 'Yes',
            //uncheckedLabel: 'No'
          })    
    ],
    declarations: [
        BusinessRulesComponent,
        BusinessRulesDetailComponent,
        BusinessRulesGroupDetailComponent,
        BusinessRulesResultComponent
    ],
    providers: [
        RuleService,
        CommonService,
        JointjsService
    ],

})

export class BusinessRulesModule { }