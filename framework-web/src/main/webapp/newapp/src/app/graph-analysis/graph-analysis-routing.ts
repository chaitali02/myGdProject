
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { GraphAnalysisComponent } from './graph-analysis.component';


const routes: Routes = [
   
  {path: 'graphpod/:id/:version/:mode',component: GraphAnalysisComponent},
  {path: 'graphpod',component: GraphAnalysisComponent},
  {path: '',component: GraphAnalysisComponent},
  
];

  export const graphanalysisrouting: ModuleWithProviders = RouterModule.forChild(routes);