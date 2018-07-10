import { ParamlistComponent } from './paramlist/paramlist.component';
import { AlgorithmComponent } from './algorithm/algorithm.component';
import { DataScienceComponent } from './data-science.component';
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ProjectSharedModule } from '../shared/module/shared.module';
import { ModelComponent } from './model/model.component';
import { ParamsetComponent } from './paramset/paramset.component';
import { ResultsComponent } from './results/resultsCommon.component';
import {TrainingComponent } from './training/training.component';
import {DistributionComponent } from './distribution/distribution.component';
import {PredictionComponent } from './prediction/prediction.component';
import {SimulationComponent} from './simulation/simulation.component'
import {OperatorComponent} from './operator/operator.component';
import {ResultDetailsComponent} from './results/resultDetails.component';

const routes: Routes = [
    {
         path: '', component: DataScienceComponent,
         children: [
            //{path: '', component: DatapodComponent},
            {path: 'algorithm/:id/:version/:mode', component: AlgorithmComponent,pathMatch:'full' },
            {path: 'algorithm/algorithm/:id/:version/:mode', component:AlgorithmComponent, pathMatch: 'full'},
            {path: 'algorithm', component: AlgorithmComponent, pathMatch: 'full'},
            {path: 'model/:id/:version/:mode', component: ModelComponent,pathMatch:'full' },
            {path: 'model/model/:id/:version/:mode', component:ModelComponent, pathMatch: 'full'},
            {path: 'model', component: ModelComponent, pathMatch: 'full'},
            {path: 'paramlist/:id/:version/:mode', component: ParamlistComponent,pathMatch:'full' },
            {path: 'paramlist/paramlist/:id/:version/:mode', component:ParamlistComponent, pathMatch: 'full'},
            {path: 'paramlist', component: ParamlistComponent, pathMatch: 'full'},
            {path: 'paramset/:id/:version/:mode', component: ParamsetComponent,pathMatch:'full' },
            {path: 'paramset/paramset/:id/:version/:mode', component:ParamsetComponent, pathMatch: 'full'},
            {path: 'paramset', component: ParamsetComponent, pathMatch: 'full'},
            {path: ':type/:id/:version/:type/:mode', component: ResultsComponent,pathMatch:'full' },
            {path: 'train/:id/:version/:mode', component: TrainingComponent,pathMatch:'full' },
            {path: 'train/train/:id/:version/:mode', component:TrainingComponent, pathMatch: 'full'},
            {path: 'train', component: TrainingComponent, pathMatch: 'full'},
            {path: 'distribution/:id/:version/:mode', component: DistributionComponent,pathMatch:'full' },
            {path: 'distribution/distribution/:id/:version/:mode', component:DistributionComponent, pathMatch: 'full'},
            {path: 'distribution', component:DistributionComponent, pathMatch: 'full'},
            {path: 'operator/:id/:version/:mode', component: OperatorComponent,pathMatch:'full' },
            {path: 'operator/operator/:id/:version/:mode', component:OperatorComponent, pathMatch: 'full'},
            {path: 'operator', component:OperatorComponent, pathMatch: 'full'},
            {path: 'prediction/:id/:version/:mode', component: PredictionComponent,pathMatch:'full' },
            {path: 'prediction/prediction/:id/:version/:mode', component:PredictionComponent, pathMatch: 'full'},
            {path: 'prediction', component:PredictionComponent, pathMatch: 'full'},
            {path: 'simulation/:id/:version/:mode', component: SimulationComponent,pathMatch:'full' },
            {path: 'simulation/simulation/:id/:version/:mode', component:SimulationComponent, pathMatch: 'full'},
            {path: 'simulation', component:SimulationComponent, pathMatch: 'full'},

            {path: 'results', component: ResultsComponent, pathMatch: 'full'},
            {path: 'resultDetails/:id/:version/:type', component: ResultDetailsComponent, pathMatch: 'full'},
         ]
    }
]
export const routing1: ModuleWithProviders = RouterModule.forChild(routes);