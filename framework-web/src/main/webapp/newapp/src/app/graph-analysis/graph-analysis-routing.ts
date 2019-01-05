
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { GraphAnalysisComponent } from './graph-analysis.component';


const routes: Routes = [
  {path: 'graphpod/graphAnalysis/graphpod/:id/:version/:mode', redirectTo : 'graphpod/:id/:version/:mode'},
  {path: 'graphpod/:id/:version/:mode',component: GraphAnalysisComponent},
  {path: 'graphpod',component: GraphAnalysisComponent},
  {path: '',component: GraphAnalysisComponent},
  
];

  export const graphanalysisrouting: ModuleWithProviders = RouterModule.forChild(routes);