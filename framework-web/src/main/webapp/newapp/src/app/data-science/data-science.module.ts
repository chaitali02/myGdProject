
import { routing1 } from './data-science-routing.module'
import { TagInputModule } from 'ngx-chips';
// import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { FormsModule } from '@angular/forms';
import { CommonModule , DatePipe} from '@angular/common';
import { NgModule } from '@angular/core';
import { ProjectSharedModule } from '../shared/module/shared.module';
import { MessagesModule, MessageModule } from 'primeng/primeng';
import { CheckboxModule, DataTableModule} from 'primeng/primeng';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown/angular2-multiselect-dropdown';

import { MetaDataDataPodService } from '../data-preparation/datapod/datapod.service';

import { CommonService } from '../metadata/services/common.service';
import { MenuModule,MenuItem} from 'primeng/primeng';
import { ModelComponent } from './model/model.component';

import { MultiSelectModule } from 'primeng/components/multiselect/multiselect';
import { DropdownModule } from 'primeng/components/dropdown/dropdown';
import { GrowlModule } from 'primeng/components/growl/growl';
import { ParamsetComponent } from './paramset/paramset.component';

import { AlgorithmComponent } from './algorithm/algorithm.component';
import { DataScienceComponent } from './data-science.component';

import { ModelService } from './../metadata/services/model.service';
import { ResultsComponent } from './results/resultsCommon.component';
import {TrainingComponent } from './training/training.component';
import { TrainingService } from '../metadata/services/training.service';
import { DistributionComponent } from './distribution/distribution.component';
import { DistributionService } from '../metadata/services/distribution.service';
import { DatasetService } from '../metadata/services/dataset.service';
import { OperatorComponent } from './operator/operator.component';
import { OperatorService } from '../metadata/services/operator.service';
import { PredictionComponent } from './prediction/prediction.component';
import { PredictionService } from '../metadata/services/prediction.service';
import {SimulationComponent } from './simulation/simulation.component';
import {SimulationService } from '../metadata/services/simulation.service';
//import {PopupModule} from 'ng2-opd-popup';
import { DialogModule } from 'primeng/components/dialog/dialog'
import { CommonListService } from '../common-list/common-list.service';
import { CalendarModule } from 'primeng/components/calendar/calendar';
import {ResultDetailsComponent} from './results/resultDetails.component';
import { DataScienceResultService } from '../metadata/services/dataScienceResult.service';
import { AlgorithmService } from '../metadata/services/algorithm.service';
import { ParamlistService } from '../metadata/services/paramlist.service';

//import {PmmlDirective} from './results/pmml.directive';
@NgModule({
    imports:[
        CommonModule,
        FormsModule,
        routing1,
        ProjectSharedModule,
        // UiSwitchModule,
        TagInputModule,
        MessagesModule,
        MessageModule,
        AngularMultiSelectModule,
        MultiSelectModule,
        DropdownModule,
        GrowlModule,
        CheckboxModule,
        DataTableModule,
        DialogModule,
        CalendarModule,
        MenuModule,

    ],
    declarations :[
        DataScienceComponent,
        AlgorithmComponent,
        ModelComponent,
        ParamsetComponent,
        ResultsComponent,
        TrainingComponent,
        DistributionComponent,
        PredictionComponent,
        SimulationComponent,
        OperatorComponent,
        ResultDetailsComponent,
        //PmmlDirective
    ],
    providers:[
        CommonService,
        ModelService,
        TrainingService,
        DistributionService,
        DatasetService,
        PredictionService, 
        SimulationService,
        OperatorService,
        OperatorComponent,
        CommonListService,
        DataScienceResultService,
        AlgorithmService

    ]
})
 
export class DataScienceModule{}