import { RelationComponent } from './relation/relation.component';
import { MapComponent } from './map/map.component';
import { FunctionComponent } from './function/function.component';
import { ExpressionComponent } from './expression/expression.component';
import { DatasetComponent } from './dataset/dataset.component';
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ProjectSharedModule } from '../shared/module/shared.module';

import { DataPreparationComponent} from './data-preparation.component';
import { DatapodComponent} from '../data-preparation/datapod/datapod.component';
import {FormulaComponent} from '../data-preparation/formula/formula.component';
import {FilterComponent} from '../data-preparation/filter/filter.component';
import {LoadComponent} from '../data-preparation/load/load.component';
import { ResultComponent } from './result/result.component';

const routes: Routes = [
    {
        path: '', component: DataPreparationComponent,
        children: [
        //{path: '', component: DatapodComponent},
        {path: 'datapod/:id/:version/:mode', component: DatapodComponent,pathMatch:'full' },
        {path: 'datapod', component: DatapodComponent},        
        {path: 'datapod/datapod/:id/:version/:mode', redirectTo : 'datapod/:id/:version/:mode'},
        {path: 'datapod/datapod', redirectTo : 'datapod'},
        {path: 'formula/:id/:version/:mode', component: FormulaComponent, pathMatch: 'full'},
        {path: 'formula/formula/:id/:version/:mode', redirectTo : 'formula/:id/:version/:mode' },
        {path: 'formula', component: FormulaComponent, pathMatch: 'full'},
        {path: 'dataset/:id/:version/:mode', component: DatasetComponent, pathMatch: 'full'},
        {path: 'dataset/dataset/:id/:version/:mode', component:DatasetComponent, pathMatch: 'full'},
        {path: 'dataset', component: DatasetComponent, pathMatch: 'full'}, 
        
        {path: 'expression/:id/:version/:mode', component: ExpressionComponent, pathMatch: 'full'},
        {path: 'expression/expression/:id/:version/:mode', component:ExpressionComponent, pathMatch: 'full'},
        {path: 'expression', component: ExpressionComponent, pathMatch: 'full'}, 
        
        {path: 'filter/:id/:version/:mode', component: FilterComponent, pathMatch: 'full'},
        {path: 'filter/filter/:id/:version/:mode', component: FilterComponent, pathMatch: 'full'},
        {path: 'filter', component: FilterComponent, pathMatch: 'full'},
        {path: 'function', component: FunctionComponent, pathMatch: 'full'},
        {path: 'function/:id/:version/:mode', component: FunctionComponent, pathMatch: 'full'},
        {path: 'function/function/:id/:version/:mode', component:FunctionComponent, pathMatch: 'full'},
        {path: 'map/:id/:version/:mode', component: MapComponent, pathMatch: 'full'},
        {path: 'map/map/:id/:version/:mode', component:MapComponent, pathMatch: 'full'}, 
        {path: 'map', component: MapComponent, pathMatch: 'full'},  
        {path: 'relation/:id/:version/:mode', component: RelationComponent, pathMatch: 'full'},
        {path: 'relation/relation/:id/:version/:mode', component:RelationComponent, pathMatch: 'full'},
        {path: 'relation', component: RelationComponent, pathMatch: 'full'},   
        {path: 'load/:id/:version/:mode', component: LoadComponent, pathMatch: 'full'},
        {path: 'load/filter/:id/:version/:mode', component: LoadComponent, pathMatch: 'full'},
        {path: 'load', component: LoadComponent, pathMatch: 'full'},

        {path: 'mapexec/:id/:version/:mode', component: ResultComponent, pathMatch: 'full'},
       // {path: 'mapexec', component: ResultComponent, pathMatch: 'full'},

        {path: 'expressionDataset/:depends/:dependsOnLabel/:dependsOnUuid', component: ExpressionComponent, pathMatch: 'full'},
        {path: 'formulaDataset/:depends/:dependsOnLabel/:dependsOnUuid', component: FormulaComponent, pathMatch: 'full'}, 
        ]
    }
]
export const routing1: ModuleWithProviders = RouterModule.forChild(routes);