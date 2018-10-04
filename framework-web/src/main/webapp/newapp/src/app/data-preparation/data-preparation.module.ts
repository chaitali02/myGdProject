import { DatasetComponent } from './dataset/dataset.component';
import { NgModule } from '@angular/core';
import { CommonModule , DatePipe} from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AgGridModule } from 'ag-grid-angular/main';
import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { TagInputModule } from 'ngx-chips';
import { MessagesModule} from 'primeng/primeng';
import { MessageModule} from 'primeng/primeng';
import {NgSelectModule, NgOption} from '@ng-select/ng-select';
import {DropdownModule,SelectItem} from 'primeng/primeng';
import {DataTableModule,SharedModule, GrowlModule} from 'primeng/primeng';
import { ProjectSharedModule } from '../shared/module/shared.module';

import { DataPreparationComponent} from './data-preparation.component';
import { DatapodComponent} from '../data-preparation/datapod/datapod.component';
import { FormulaComponent} from '../data-preparation/formula/formula.component';
import { FilterComponent} from '../data-preparation/filter/filter.component';
import { LoadComponent} from '../data-preparation/load/load.component';

import { MetaDataDataPodService } from '../data-preparation/datapod/datapod.service';
import { ExpressionComponent } from './expression/expression.component'
import{ CommonService } from '../metadata/services/common.service'
import { DatapodService } from '../metadata/services/datapod.service';
import { MapServices } from '../metadata/services/map.service';
import { DatasetService } from '../metadata/services/dataset.service';
import {RelationService} from './relation/relation.service';

import { routing1 } from './data-preparation-routing.module';
import { FunctionComponent } from './function/function.component';
import { MapComponent } from './map/map.component';
import { RelationComponent } from './relation/relation.component';
import { TooltipModule } from 'primeng/components/tooltip/tooltip';


@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        routing1,
        ProjectSharedModule,
        AgGridModule,
        UiSwitchModule,
        TagInputModule,
        MessagesModule,
        MessageModule,
        NgSelectModule,
        DropdownModule,
        DataTableModule,
        SharedModule,
        GrowlModule,
        TooltipModule        
    ],
    declarations: [
        DataPreparationComponent,
        DatapodComponent,
        FormulaComponent,
        DatasetComponent,
        ExpressionComponent,
        FilterComponent,
        FunctionComponent,
        MapComponent,
        RelationComponent,
        LoadComponent,
        
    ],
    providers: [
        MetaDataDataPodService,
        CommonService,
        MapServices,
        DatasetService,
        RelationService,
        DatapodService
    ]

})
export class DataPreparationModule { }