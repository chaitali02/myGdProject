import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { BatchSchedulerComponent } from './batch-scheduler.component';
import { BatchSchedulerResultComponent } from './batch-schedulerresult.component';
//import { DataPiplineResultComponent } from './../data-pipeline/data-pipelineresult.component';

const routes: Routes = [   
  {path: 'batch/:id/:version/:mode', component: BatchSchedulerComponent},
  {path: 'batch', component: BatchSchedulerComponent},
  {path:  '', component: BatchSchedulerComponent},
  {path: 'batchexec/:id/:version/:type/:mode', component : BatchSchedulerResultComponent},
  //{path: 'dagexec/:id/:version/:type/:mode', component : DataPiplineResultComponent}, 
  
  //app/batchScheduler/batch/fa0cb37f-dabf-4194-a7ac-0359f6b0ad15/1542963103/false'
  //'app/list/batch/batchScheduler/batch/fa0cb37f-dabf-4194-a7ac-0359f6b0ad15/1542963103/true'
 //edit=> app/list/batch/00a08bf4-2871-4b65-b5a0-031373dd0e56/1543055175/false
 //dataPipeline/jobexecutorlistdagexec/fc8277a3-1480-4401-be8f-740ef5428e73/1543818000/true
];

export const batchschedulerrouting: ModuleWithProviders = RouterModule.forChild(routes);