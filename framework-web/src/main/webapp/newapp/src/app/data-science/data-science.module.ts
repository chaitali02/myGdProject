
import { routing1 } from './data-science-routing.module'
import { TagInputModule } from 'ngx-chips';
import { UiSwitchModule } from 'ngx-toggle-switch/src';
import { AgGridModule } from 'ag-grid-angular/main';
import { FormsModule } from '@angular/forms';
import { CommonModule , DatePipe} from '@angular/common';
import { NgModule } from '@angular/core';
import { ProjectSharedModule } from '../shared/module/shared.module';
import { MessagesModule, MessageModule } from 'primeng/primeng';
import { CheckboxModule, DataTableModule} from 'primeng/primeng';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown/angular2-multiselect-dropdown';

import { MetaDataDataPodService } from '../data-preparation/datapod/datapod.service';

import { CommonService } from '../metadata/services/common.service';

import { ModelComponent } from './model/model.component';
import { ParamlistComponent } from './paramlist/paramlist.component';
import { MultiSelectModule } from 'primeng/components/multiselect/multiselect';
import { DropdownModule } from 'primeng/components/dropdown/dropdown';
import { GrowlModule } from 'primeng/components/growl/growl';
import { ParamsetComponent } from './paramset/paramset.component';

import { AlgorithmComponent } from './algorithm/algorithm.component';
import { DataScienceComponent } from './data-science.component';

import { ModelService } from './../metadata/services/model.service';
import { ResultsComponent } from './results/results.component';
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

@NgModule({
    imports:[
        CommonModule,
        FormsModule,
        routing1,
        ProjectSharedModule,
        AgGridModule,
        UiSwitchModule,
        TagInputModule,
        MessagesModule,
        MessageModule,
        AngularMultiSelectModule,
        MultiSelectModule,
        DropdownModule,
        GrowlModule,
        CheckboxModule,
        DataTableModule
        

    ],
    declarations :[
        DataScienceComponent,
        AlgorithmComponent,
        ModelComponent,
        ParamlistComponent,
        ParamsetComponent,
        ResultsComponent,
        TrainingComponent,
        DistributionComponent,
        PredictionComponent,
        SimulationComponent,
        OperatorComponent,
        
    ],
    providers:[
        CommonService,
        ModelService,
        TrainingService,
        DistributionService,
<<<<<<< HEAD
        DatasetService
=======
        PredictionService, 
        SimulationService,
        OperatorService,
        OperatorComponent
>>>>>>> c9d0cec15b87e0844f3227e2b7f138c952585ff0
    ]
})
 
export class DataScienceModule{}