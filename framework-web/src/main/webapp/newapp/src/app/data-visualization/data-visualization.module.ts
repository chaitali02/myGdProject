import { NgModule } from '@angular/core';
import { CommonModule ,DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AgGridModule } from 'ag-grid-angular/main';
import { MultiSelectModule } from 'primeng/primeng';
import { DropdownModule,SelectItem } from 'primeng/primeng';
import { DataTableModule,SharedModule } from 'primeng/primeng';
import { GrowlModule } from 'primeng/primeng';
import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { TagInputModule } from 'ngx-chips';
import { DndListModule } from 'ngx-drag-and-drop-lists';
import {DndModule} from 'ng2-dnd';

import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown/angular2-multiselect-dropdown';

import { ProjectSharedModule } from '../shared/module/shared.module';

import { DataVisualizationComponent } from './data-visualization.component';
import { DashboardComponent } from './dashboard/dashboard.component'
import { DashboardDetailComponent } from './dashboard/dashboarddetail.component';

import { VizpodDetailComponent } from './vizpod/vizpoddetail.component'

import { CommonService }from '../metadata/services/common.service';
import { DashboardService } from '../metadata/services/dashboard.service';
import { routing1  } from './data-visualization-routing.module';
import { VizpodService } from  '../metadata/services/vizpod.service';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        MultiSelectModule,
        DropdownModule,
        DataTableModule,
        SharedModule,
        GrowlModule,
        AgGridModule.withComponents(''),
        UiSwitchModule,
        TagInputModule,
        DndListModule,
        AngularMultiSelectModule,
        DndModule.forRoot(),
        routing1,
        ProjectSharedModule,
        
    ],
    declarations: [
        DataVisualizationComponent,
        DashboardComponent,
        DashboardDetailComponent,
        VizpodDetailComponent
      
     ],
    providers: [
        CommonService,
        DashboardService,
        VizpodService
    ],
  //  entryComponents: [DropdownComponent],
})

export class DataVisualizationModule { }