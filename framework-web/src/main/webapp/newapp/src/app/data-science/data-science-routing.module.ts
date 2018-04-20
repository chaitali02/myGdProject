import { ParamlistComponent } from './paramlist/paramlist.component';
import { AlgorithmComponent } from './algorithm/algorithm.component';


import { DataScienceComponent } from './data-science.component';
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ProjectSharedModule } from '../shared/module/shared.module';
import { ModelComponent } from './model/model.component';
import { ParamsetComponent } from './paramset/paramset.component';
import { ResultsComponent } from './results/results.component';


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
           
         ]
    }
]
export const routing1: ModuleWithProviders = RouterModule.forChild(routes);