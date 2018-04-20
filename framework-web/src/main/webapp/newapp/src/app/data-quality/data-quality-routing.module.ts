
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DataQualityComponent } from './data-quality.component';
import{DataQualityResultComponent} from './data-qualityresult.component'
import{DataQualityDetailComponent} from './data-qualitydetail.component'
import{DataQualityGroupDetailComponent} from './data-qualitygroupdetail.components'
const routes: Routes = [{ 
	  path: '',
    component: DataQualityComponent,
    children: [
      { path: ':type/:id/:version/:type/:mode', component: DataQualityResultComponent},
      { path: 'dq/:id/:version/:mode', component: DataQualityDetailComponent},    
      { path: 'dq', component: DataQualityDetailComponent},   
      { path: 'dqgroup/:id/:version/:mode', component: DataQualityGroupDetailComponent},    
      { path: 'dqgroup', component: DataQualityGroupDetailComponent},     
            
  ]
   }
];

  export const routing1: ModuleWithProviders = RouterModule.forChild(routes);