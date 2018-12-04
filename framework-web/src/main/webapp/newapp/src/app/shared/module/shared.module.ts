import { NgModule,Input } from '@angular/core';
import { ModuleWithProviders } from '@angular/core';
import { CommonModule }       from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { D3Service, D3, Selection } from 'd3-ng2-service';
import { C3Component } from '../components';
import { ClockComponent,BreadcrumbComponent } from '../components';
import { D3Component } from '../../D3/d3.component';
import { ModelComponent} from '../popUpModel/model.component';
import { ParamlistComponent } from '../components/paramlist/paramlist.component';
import { FormsModule } from '@angular/forms';
import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { TagInputModule } from 'ngx-chips';
import { DropdownModule } from 'primeng/components/dropdown/dropdown';
import { ParamlistService } from '../../metadata/services/paramlist.service';
//import { DataPiplineResultComponent } from '../components/data-pipelineresult/data-pipelineresult.component';
//import { JointjsComponent } from './../../data-pipeline/jointjs.component';
//import { ShContextMenuModule } from 'ng2-right-click-menu';
import {MultiSelectModule, DataTableModule} from 'primeng/primeng';
//import { JointjsGroupComponent } from '../components/jointjsgroup/jointjsgroup.component'
//import { TableRenderComponent } from '../components/resulttable/resulttable.component';


@NgModule({
    imports: [CommonModule,RouterModule,FormsModule, UiSwitchModule,TagInputModule,
        DropdownModule,
       // ShContextMenuModule,
        MultiSelectModule
        //DataTableModule
    ],
    exports: [CommonModule,ClockComponent,BreadcrumbComponent,D3Component,C3Component,ModelComponent,ParamlistComponent,
        //DataPiplineResultComponent,
        MultiSelectModule
    ],
    declarations: [
        ClockComponent,BreadcrumbComponent,D3Component,C3Component,ModelComponent,ParamlistComponent,
        //DataPiplineResultComponent,
        //JointjsComponent,
        //JointjsGroupComponent,
        //TableRenderComponent
    ],
    providers:[ParamlistService,
        //JointjsComponent
    ],
    entryComponents: [
        //JointjsComponent
    ]
})
export class ProjectSharedModule {
    static forRoot() : ModuleWithProviders {
        return {
            ngModule: ProjectSharedModule,
            providers: [D3Service]
        };
    }
 }