
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DataProfileComponent } from './data-profile.component';
import{DataProfileresultComponent} from './data-profileresult.component'
import{DataProfileDetailComponent} from './data-profiledetail.component'
import{DataProfileGroupDetailComponent} from './data-profilegroupdetail.components'
//import { ProfileExecComponent } from '../job-monitoring/job-monitoring-details/profileExec/profileExec.component';
const routes: Routes = [{ 
	  path: '',
    component: DataProfileComponent,
    children: [{ path: 'profileexec/:id/:version/:type/:mode', component: DataProfileresultComponent},
    { path: 'profilegroupexec/:id/:version/:type/:mode', component: DataProfileresultComponent},
    { path: 'profile/:id/:version/:mode', component: DataProfileDetailComponent},
    { path: 'profile', component: DataProfileDetailComponent},
    { path: 'profilegroup/:id/:version/:mode', component: DataProfileGroupDetailComponent},    
    { path: 'profilegroup', component: DataProfileGroupDetailComponent},  
   // { path: 'profileExec/:id/:version/:mode', component: ProfileExecComponent, pathMatch: 'full'},
    
  ]
   }
];

  export const routing1: ModuleWithProviders = RouterModule.forChild(routes);