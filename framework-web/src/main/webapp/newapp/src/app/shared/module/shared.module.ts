import { NgModule, Input } from '@angular/core';
import { ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { D3Service, D3, Selection } from 'd3-ng2-service';
import { C3Component } from '../components';
import { ClockComponent, BreadcrumbComponent, KnowledgeGraphComponent } from '../components';
import { D3Component } from '../../D3/d3.component';
import { ModelComponent } from '../popUpModel/model.component';
import { ParamlistComponent } from '../components/paramlist/paramlist.component';
import { FormsModule } from '@angular/forms';
// import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { TagInputModule } from 'ngx-chips';
import { DropdownModule } from 'primeng/components/dropdown/dropdown';
import { ParamlistService } from '../../metadata/services/paramlist.service';
import { CalendarModule } from 'primeng/components/calendar/calendar';
//import { DataPiplineResultComponent } from '../components/data-pipelineresult/data-pipelineresult.component';
//import { JointjsComponent } from './../../data-pipeline/jointjs.component';
//import { ShContextMenuModule } from 'ng2-right-click-menu';
import { MultiSelectModule, DataTableModule, ChipsModule } from 'primeng/primeng';
import { UiSwitchModule } from 'ngx-ui-switch';
//import { JointjsGroupComponent } from '../components/jointjsgroup/jointjsgroup.component'
//import { TableRenderComponent } from '../components/resulttable/resulttable.component';


@NgModule({
    imports: [
        CommonModule,
        RouterModule,
        FormsModule,
        //UiSwitchModule,
        TagInputModule,
        DropdownModule,
        // ShContextMenuModule,
        MultiSelectModule,
        CalendarModule,
        ChipsModule,
        UiSwitchModule.forRoot({
            size: 'small',
            color: '#15C5D5',
            switchColor: '#FFFFF',
            defaultBoColor: '#ccc',
            // checkedLabel: 'Yes',
            //uncheckedLabel: 'No'
        })
        //DataTableModule
    ],
    exports: [CommonModule,
        ClockComponent,
        BreadcrumbComponent,
        D3Component, C3Component,
        ModelComponent,
        ParamlistComponent,
        KnowledgeGraphComponent,
        //DataPiplineResultComponent,
        MultiSelectModule
    ],
    declarations: [
        ClockComponent,
        BreadcrumbComponent,
        D3Component,
        C3Component,
        ModelComponent,
        ParamlistComponent,
        KnowledgeGraphComponent
        //DataPiplineResultComponent,
        //JointjsComponent,
        //JointjsGroupComponent,
        //TableRenderComponent
    ],
    providers: [ParamlistService,
        //JointjsComponent
    ],
    entryComponents: [
        //JointjsComponent
    ]
})
export class ProjectSharedModule {
    static forRoot(): ModuleWithProviders {
        return {
            ngModule: ProjectSharedModule,
            providers: [D3Service]
        };
    }
}