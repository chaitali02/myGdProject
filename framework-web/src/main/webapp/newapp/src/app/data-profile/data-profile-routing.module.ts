
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DataProfileComponent } from './data-profile.component';
import{DataProfileresultComponent} from './data-profileresult.component'
import{DataProfileDetailComponent} from './data-profiledetail.component'
import{DataProfileGroupDetailComponent} from './data-profilegroupdetail.components'
//import { ProfileExecComponent } from '../job-monitoring/job-monitoring-details/profileExec/profileExec.component';
import{CompareProfileResultComponent} from './compare-profile/compare-profileresult.component'

const routes: Routes = [{ 
	  path: '',
    component: DataProfileComponent,
    children: [{ path: 'profileexec/:id/:version/:type/:mode', component: DataProfileresultComponent},
    { path: 'profilegroupexec/:id/:version/:type/:mode', component: DataProfileresultComponent},
    { path: 'profile/:id/:version/:mode', component: DataProfileDetailComponent},
    { path: 'profile', component: DataProfileDetailComponent},
    { path: 'profilegroup/:id/:version/:mode', component: DataProfileGroupDetailComponent},    
    { path: 'profilegroup', component: DataProfileGroupDetailComponent},  
    { path: 'profilegroupexec/:id/:version/:type/:mode/dataProfiling/profile/:id/:version/:mode', redirectTo: 'profile/:id/:version/:mode'},
    { path: 'JobMonitoring/profileExec/:id/:version/:mode', redirectTo: 'profileExec/:id/:mode'},
    { path: 'profileExec/:id/:mode', loadChildren: 'app/job-monitoring/job-monitoring-details/job-monitoring-details.module#jobMonitoringDetailsModule' },
    { path: 'JobMonitoring', loadChildren: 'app/job-monitoring/job-monitoring-details/job-monitoring-details.module#jobMonitoringDetailsModule'},
    // { path: 'profileExec/:id/:version/:mode', component: ProfileExecComponent, pathMatch: 'full'},
    
    { path: 'profilegroup', component: DataProfileGroupDetailComponent},        
    { path: 'compareprofile/:type', component: CompareProfileResultComponent}
  ]
   }
];

  export const routing1: ModuleWithProviders = RouterModule.forChild(routes);