
import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DataReconComponent } from './data-recon.component';
// import{DataQualityResultComponent} from './data-qualityresult.component'
 import{DataReconDetailComponent} from './data-recondetail.component'
// import{DataQualityGroupDetailComponent} from './data-qualitygroupdetail.components'
const routes: Routes = [{ 
	  path: '',
    component: DataReconComponent,
    children: [
      //{ path: ':type/:id/:version/:type/:mode', component: DataQualityResultComponent},
      { path: 'createreconerule/:id/:version/:mode', component: DataReconDetailComponent},    
      { path: 'createreconerule', component: DataReconDetailComponent},   
      //{ path: 'dqgroup/:id/:version/:mode', component: DataQualityGroupDetailComponent},    
      //{ path: 'dqgroup', component: DataQualityGroupDetailComponent},     
            
  ]
   }
];

  export const routing1: ModuleWithProviders = RouterModule.forChild(routes);