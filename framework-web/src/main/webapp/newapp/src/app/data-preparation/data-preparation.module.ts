import { DatasetComponent } from './dataset/dataset.component';
import { NgModule } from '@angular/core';
import { CommonModule , DatePipe} from '@angular/common';
import { FormsModule } from '@angular/forms';
// import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { TagInputModule } from 'ngx-chips';
import { MessagesModule, ChipsModule} from 'primeng/primeng';
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
import { StorageServiceModule } from 'angular-webstorage-service'
import { DialogModule } from 'primeng/components/dialog/dialog';
import {DragDropModule} from 'primeng/components/dragdrop/dragdrop';
import { ResultComponent } from './result/result.component';
import { ResultModule } from '../shared/module/result.module';
import { UiSwitchModule } from 'ngx-ui-switch';
import { FormulaService } from '../metadata/services/formula.service';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        routing1,
        ProjectSharedModule,
        MessagesModule,
        MessageModule,TagInputModule,
        NgSelectModule,
        DropdownModule,
        DataTableModule,
        SharedModule,
        GrowlModule,
        TooltipModule,
        StorageServiceModule,
        DialogModule,
        DragDropModule,
        ResultModule,
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
        ResultComponent,
        
    ],
    providers: [
        MetaDataDataPodService,
        CommonService,
        MapServices,
        DatasetService,
        RelationService,
        DatapodService,
        FormulaService
    ]

})
export class DataPreparationModule { }