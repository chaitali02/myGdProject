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

import { ProjectSharedModule } from '../shared/module/shared.module';
import { ResultModule } from '../shared/module/result.module';

import { DataPiplineComponent } from './data-pipeline.component';
import { DataPiplineResultComponent } from './data-pipelineresult.component';
import{ JointjsComponent} from './jointjs.component';


import{CommonService}from '../metadata/services/common.service';
import { DataPipelineService } from '../metadata/services/dataPipeline.service';
import{JointjsService} from './jointjsservice'
import{SharedDataService} from './shareddata.service'
import { D3Service, D3, Selection } from 'd3-ng2-service';

import { datapiplinerouting} from './data-pipeline-routing'
import { UiSwitchModule } from 'ngx-toggle-switch/src';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        datapiplinerouting,
        ProjectSharedModule,
        ShContextMenuModule,
        TagInputModule,
        DropdownModule,
        SharedModule,
        MessagesModule,
        UiSwitchModule,
        MessageModule,
        GrowlModule,
        DataTableModule,
        MultiSelectModule,
        ResultModule
       
    ],
    declarations: [
        DataPiplineComponent,
        DataPiplineResultComponent,
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

export class DataPiplineModule { }