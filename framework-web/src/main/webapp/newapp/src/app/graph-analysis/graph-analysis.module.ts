import { NgModule } from '@angular/core';
import { CommonModule ,DatePipe} from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ShContextMenuModule } from 'ng2-right-click-menu';
import { TagInputModule } from 'ngx-chips';
import {MultiSelectModule} from 'primeng/primeng';
import {DropdownModule,SelectItem} from 'primeng/primeng';
import {DataTableModule,SharedModule} from 'primeng/primeng';
import { MessagesModule} from 'primeng/primeng';
import { MessageModule} from 'primeng/primeng';
import {GrowlModule} from 'primeng/primeng';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';

import { ProjectSharedModule } from '../shared/module/shared.module';
import { ResultModule } from '../shared/module/result.module';

import { GraphAnalysisComponent } from './graph-analysis.component';
// import { DataPiplineResultComponent } from './data-pipelineresult.component';
import{ JointjsComponent} from './jointjs.component';


import{CommonService}from '../metadata/services/common.service';
import { DataPipelineService } from '../metadata/services/dataPipeline.service';
import{JointjsService} from './jointjsservice'
import{SharedDataService} from './shareddata.service'
import { D3Service, D3, Selection } from 'd3-ng2-service';

import { graphanalysisrouting} from './graph-analysis-routing'
// import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { DialogModule } from 'primeng/components/dialog/dialog';
@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        graphanalysisrouting,
        ProjectSharedModule,
        ShContextMenuModule,
        TagInputModule,
        DropdownModule,
        SharedModule,
        MessagesModule,
        // UiSwitchModule,
        MessageModule,
        GrowlModule,
        DataTableModule,
        MultiSelectModule,
        ResultModule,
        DialogModule,
        AngularMultiSelectModule
       
    ],
    declarations: [
        GraphAnalysisComponent,
        //DataPiplineResultComponent,
        JointjsComponent
     ],
    providers: [
        CommonService,
        DataPipelineService,
        JointjsService,
        D3Service,
        SharedDataService,
        JointjsComponent
        
    ],
  entryComponents: [JointjsComponent],
})

export class graphAnalysisModule { }