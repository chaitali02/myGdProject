
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DataPiplineComponent } from './data-pipeline.component';
import { DataPiplineResultComponent } from './data-pipelineresult.component';


const routes: Routes = [
   
  {path: 'dag/:id/:version/:mode',component: DataPiplineComponent},
  {path: 'dag',component: DataPiplineComponent},
  {path: '',component: DataPiplineComponent},
  {path: 'dagexec/:id/:version/:type/:mode',component:DataPiplineResultComponent},
  
  
];

  export const datapiplinerouting: ModuleWithProviders = RouterModule.forChild(routes);